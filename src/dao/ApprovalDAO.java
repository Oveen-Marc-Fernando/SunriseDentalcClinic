package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.ApprovalModel;

/**
 * Data access for the {@code approvals} table. Backs OS_APM_Grid (read) and
 * OS_APM_Add (write) — before this, OS_APM_Grid read a hardcoded sample
 * array and OS_APM_Add's "Send" button didn't persist anything at all.
 *
 * @author oveen
 */
public class ApprovalDAO {

    public List<ApprovalModel> findAll() {
        List<ApprovalModel> rows = new ArrayList<>();
        String sql = "SELECT approval_id, description, remarks, approval_date, amount, status, submitted_by "
                + "FROM approvals ORDER BY approval_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ApprovalDAO] findAll failed: " + e.getMessage());
        }
        return rows;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM approvals";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("[ApprovalDAO] count failed: " + e.getMessage());
            return 0;
        }
    }

    /** Next auto-generated Approval ID, e.g. "APR104" — one past the highest numeric suffix on record. */
    public String nextApprovalId() {
        String sql = "SELECT approval_id FROM approvals WHERE approval_id LIKE 'APR%'";
        int max = 100;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    int n = Integer.parseInt(rs.getString(1).substring(3));
                    max = Math.max(max, n);
                } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                    // non-numeric suffix — skip it, doesn't affect the running max
                }
            }
        } catch (SQLException e) {
            System.err.println("[ApprovalDAO] nextApprovalId failed: " + e.getMessage());
        }
        return "APR" + (max + 1);
    }

    /** Inserts a new approval request, always starting life as "Pending" — OS_APM_Add's "Send" button. */
    public void insert(ApprovalModel m) {
        String sql = "INSERT INTO approvals (approval_id, description, remarks, approval_date, amount, status, submitted_by) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getApprovalId());
            ps.setString(2, m.getDescription());
            ps.setString(3, SqlUtil.blankToNull(m.getRemarks()));
            ps.setDate(4, SqlUtil.parseDate(m.getApprovalDate()));
            ps.setBigDecimal(5, parseAmount(m.getAmount()));
            ps.setString(6, m.getStatus() != null ? m.getStatus() : "Pending");
            ps.setString(7, SqlUtil.blankToNull(m.getSubmittedBy()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ApprovalDAO] insert failed for " + m.getApprovalId() + ": " + e.getMessage());
        }
    }

    private static java.math.BigDecimal parseAmount(String text) {
        if (text == null) return java.math.BigDecimal.ZERO;
        try {
            return new java.math.BigDecimal(text.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return java.math.BigDecimal.ZERO;
        }
    }

    /** Approves/declines an approval request — AD_APR_OfficeStaff's Approve/Reject buttons. */
    public boolean updateStatus(String approvalId, String status) {
        if (approvalId == null || approvalId.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE approvals SET status = ?, approval_date = COALESCE(approval_date, CURDATE()) WHERE approval_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, approvalId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ApprovalDAO] updateStatus failed for " + approvalId + ": " + e.getMessage());
            return false;
        }
    }

    private static ApprovalModel mapRow(ResultSet rs) throws SQLException {
        ApprovalModel m = new ApprovalModel();
        m.setApprovalId(rs.getString("approval_id"));
        m.setDescription(rs.getString("description"));
        m.setRemarks(SqlUtil.nullToBlank(rs.getString("remarks")));
        m.setApprovalDate(SqlUtil.formatDate(rs.getDate("approval_date")));
        m.setAmount(rs.getBigDecimal("amount") != null ? rs.getBigDecimal("amount").toPlainString() : "0");
        m.setStatus(rs.getString("status"));
        m.setSubmittedBy(rs.getString("submitted_by"));
        return m;
    }
}
