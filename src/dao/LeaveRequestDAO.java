package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.LeaveRequestModel;

/**
 * Data access for the {@code leave_requests} table — backs
 * {@link controller.LeaveRequestController} (D_RS_Leave).
 *
 * @author oveen
 */
public class LeaveRequestDAO {

    private static final String SELECT_ALL_SQL =
            "SELECT leave_request_id, dentist_name, leave_date, status FROM leave_requests ORDER BY leave_date";

    /** Every leave request on record. */
    public List<LeaveRequestModel> findAll() {
        List<LeaveRequestModel> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[LeaveRequestDAO] findAll failed: " + e.getMessage());
        }
        return requests;
    }

    /** Number of leave requests on record. */
    public int count() {
        String sql = "SELECT COUNT(*) FROM leave_requests";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("[LeaveRequestDAO] count failed: " + e.getMessage());
            return 0;
        }
    }

    /** Number of leave requests on record for one dentist — Dentist Dashboard's "Request Leaves" badge. */
    public int countForDentist(String dentistName) {
        if (dentistName == null || dentistName.trim().isEmpty()) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM leave_requests WHERE dentist_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("[LeaveRequestDAO] countForDentist failed: " + e.getMessage());
            return 0;
        }
    }

    /**
     * True if this dentist already has a Pending leave request sitting in
     * AD_APR_DentistLeave's queue — D_RS_Leave blocks a second request until
     * an Administrator approves/rejects the first one.
     */
    public boolean hasPendingRequest(String dentistName) {
        if (dentistName == null || dentistName.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM leave_requests WHERE dentist_name = ? AND status = 'Pending'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[LeaveRequestDAO] hasPendingRequest failed: " + e.getMessage());
            return false;
        }
    }

    /** Most recent Approved leave date on record for the given dentist, or "" if none. */
    public String lastApprovedDate(String dentistName) {
        String sql = "SELECT leave_date FROM leave_requests WHERE dentist_name = ? AND status = 'Approved' "
                + "ORDER BY leave_date DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? SqlUtil.formatDate(rs.getDate(1)) : "";
            }
        } catch (SQLException e) {
            System.err.println("[LeaveRequestDAO] lastApprovedDate failed: " + e.getMessage());
            return "";
        }
    }

    /** Inserts a new leave request (always starts Pending). Called from D_RS_Leave's "Request" button. */
    public void insert(LeaveRequestModel m) {
        String sql = "INSERT INTO leave_requests (dentist_name, leave_date, status) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getDentistName());
            ps.setDate(2, SqlUtil.parseDate(m.getLeaveDate()));
            ps.setString(3, m.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[LeaveRequestDAO] insert failed for " + m.getDentistName() + ": " + e.getMessage());
        }
    }

    private static LeaveRequestModel mapRow(ResultSet rs) throws SQLException {
        LeaveRequestModel m = new LeaveRequestModel();
        m.setLeaveRequestId(rs.getInt("leave_request_id"));
        m.setDentistName(rs.getString("dentist_name"));
        m.setLeaveDate(SqlUtil.formatDate(rs.getDate("leave_date")));
        m.setStatus(rs.getString("status"));
        return m;
    }

    /** Approves/rejects a leave request by ID — AD_APR_DentistLeave's Approve/Reject buttons. */
    public boolean updateStatus(int leaveRequestId, String status) {
        String sql = "UPDATE leave_requests SET status = ? WHERE leave_request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, leaveRequestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LeaveRequestDAO] updateStatus failed for leaveRequestId=" + leaveRequestId + ": " + e.getMessage());
            return false;
        }
    }
}
