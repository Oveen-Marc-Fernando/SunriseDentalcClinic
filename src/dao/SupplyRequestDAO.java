package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.SupplyRequestModel;

/**
 * Data access for the {@code supply_requests} table — backs
 * {@link controller.SupplyRequestController} (D_RS_Add / D_RS_History).
 *
 * @author oveen
 */
public class SupplyRequestDAO {

    private static final String SELECT_ALL_SQL =
            "SELECT tracking_id, product_id, product_type, product_name, description, quantity, "
            + "expiry_date, manufacture_date, status, dentist_name FROM supply_requests ORDER BY tracking_id";

    /** Every supply request on record — Administration's unscoped approval screen reads this. */
    public List<SupplyRequestModel> findAll() {
        List<SupplyRequestModel> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[SupplyRequestDAO] findAll failed: " + e.getMessage());
        }
        return requests;
    }

    /**
     * Only the requests this dentist submitted — D_RS_Grid/D_RS_History's
     * own "my requests" view and the Dentist Dashboard badge. Legacy rows
     * with no recorded dentist_name never match any dentist here.
     */
    public List<SupplyRequestModel> findByDentist(String dentistName) {
        List<SupplyRequestModel> requests = new ArrayList<>();
        if (dentistName == null || dentistName.trim().isEmpty()) {
            return requests;
        }
        String sql = "SELECT tracking_id, product_id, product_type, product_name, description, quantity, "
                + "expiry_date, manufacture_date, status, dentist_name FROM supply_requests "
                + "WHERE dentist_name = ? ORDER BY tracking_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[SupplyRequestDAO] findByDentist failed: " + e.getMessage());
        }
        return requests;
    }

    /** Number of supply requests on record. */
    public int count() {
        String sql = "SELECT COUNT(*) FROM supply_requests";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("[SupplyRequestDAO] count failed: " + e.getMessage());
            return 0;
        }
    }

    /** Number of supply requests this dentist submitted — Dentist Dashboard's own badge. */
    public int countForDentist(String dentistName) {
        if (dentistName == null || dentistName.trim().isEmpty()) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM supply_requests WHERE dentist_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("[SupplyRequestDAO] countForDentist failed: " + e.getMessage());
            return 0;
        }
    }

    /** Inserts a new supply request — always starts life Pending. Called from D_RS_Add's "Request" button. */
    public void insert(SupplyRequestModel m) {
        String sql = "INSERT INTO supply_requests (tracking_id, product_id, product_type, product_name, "
                + "description, quantity, expiry_date, manufacture_date, status, dentist_name) "
                + "VALUES (?,?,?,?,?,?,?,?,'Pending',?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTrackingId());
            ps.setString(2, SqlUtil.blankToNull(m.getProductId()));
            ps.setString(3, m.getProductType());
            ps.setString(4, m.getProductName());
            ps.setString(5, m.getDescription());
            ps.setInt(6, parseQuantity(m.getQuantity()));
            ps.setDate(7, SqlUtil.parseDate(m.getExpiryDate()));
            ps.setDate(8, SqlUtil.parseDate(m.getManufactureDate()));
            ps.setString(9, SqlUtil.blankToNull(m.getDentistName()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[SupplyRequestDAO] insert failed for " + m.getTrackingId() + ": " + e.getMessage());
        }
    }

    /** Approves or rejects a Pending request — AD_APR_SupplyRequest's Approve/Reject buttons. */
    public boolean updateStatus(String trackingId, String status) {
        if (trackingId == null || trackingId.trim().isEmpty() || status == null || status.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE supply_requests SET status = ? WHERE tracking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.trim());
            ps.setString(2, trackingId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SupplyRequestDAO] updateStatus failed for " + trackingId + ": " + e.getMessage());
            return false;
        }
    }

    private static int parseQuantity(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private static SupplyRequestModel mapRow(ResultSet rs) throws SQLException {
        SupplyRequestModel m = new SupplyRequestModel();
        m.setTrackingId(rs.getString("tracking_id"));
        m.setProductId(rs.getString("product_id"));
        m.setProductType(rs.getString("product_type"));
        m.setProductName(rs.getString("product_name"));
        m.setDescription(rs.getString("description"));
        m.setQuantity(String.valueOf(rs.getInt("quantity")));
        m.setExpiryDate(SqlUtil.formatDate(rs.getDate("expiry_date")));
        m.setManufactureDate(SqlUtil.formatDate(rs.getDate("manufacture_date")));
        m.setStatus(rs.getString("status"));
        m.setDentistName(rs.getString("dentist_name"));
        return m;
    }
}
