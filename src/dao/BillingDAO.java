package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.BillingModel;

/**
 * Data access for the {@code billings} table — backs
 * {@link controller.BillingManagementController} (OS_BM_1-4) and read by
 * OS_BM_Grid.
 *
 * @author oveen
 */
public class BillingDAO {

    private static final String SELECT_ALL_SQL =
            "SELECT billing_id, appointment_id, patient_id, dentist_name, patient_name, appointment_date, "
            + "appointment_charges, clinical_total, medicine_total, total_bill_amount, email_sent_count "
            + "FROM billings ORDER BY billing_id";

    /** Every bill on record — OS_BM_Grid's table. */
    public List<BillingModel> findAll() {
        List<BillingModel> bills = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bills.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BillingDAO] findAll failed: " + e.getMessage());
        }
        return bills;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM billings";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("[BillingDAO] count failed: " + e.getMessage());
            return 0;
        }
    }

    /** Inserts a new bill. Called from OS_BM_4's final submit. Returns true only if a row was actually written. */
    public boolean insert(BillingModel m) {
        String sql = "INSERT INTO billings (billing_id, appointment_id, patient_id, dentist_name, patient_name, "
                + "appointment_date, appointment_charges, clinical_total, medicine_total, total_bill_amount) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getBillingId());
            ps.setString(2, SqlUtil.blankToNull(m.getAppointmentId()));
            ps.setString(3, SqlUtil.blankToNull(m.getPatientId()));
            ps.setString(4, m.getDentistName());
            ps.setString(5, m.getPatientName());
            ps.setDate(6, SqlUtil.parseDate(m.getAppointmentDate()));
            ps.setBigDecimal(7, java.math.BigDecimal.valueOf(m.getAppointmentCharges()));
            ps.setBigDecimal(8, java.math.BigDecimal.valueOf(m.getClinicalTotal()));
            ps.setBigDecimal(9, java.math.BigDecimal.valueOf(m.getMedicineTotal()));
            ps.setBigDecimal(10, java.math.BigDecimal.valueOf(m.getTotalBillAmount()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BillingDAO] insert failed for " + m.getBillingId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates only the identifying/reference fields — OS_BM_Grid's Edit
     * popup. Deliberately doesn't touch the money columns (appointment
     * charges, clinical/medicine totals, total bill amount): those are
     * computed from Billing's own multi-step wizard (services/medicines
     * picked, quantities deducted from stock) and free-editing them here
     * would desync the bill from what was actually charged, without
     * re-running any of that logic.
     */
    public boolean updateReferenceFields(String billingId, String dentistName, String patientName, java.sql.Date appointmentDate) {
        if (billingId == null || billingId.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE billings SET dentist_name = ?, patient_name = ?, appointment_date = ? WHERE billing_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistName);
            ps.setString(2, patientName);
            ps.setDate(3, appointmentDate);
            ps.setString(4, billingId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BillingDAO] updateReferenceFields failed for " + billingId + ": " + e.getMessage());
            return false;
        }
    }

    /** Next auto-generated Billing ID, e.g. "B104" — one past the highest numeric suffix on record. */
    public String nextBillingId() {
        String sql = "SELECT billing_id FROM billings WHERE billing_id LIKE 'B%'";
        int max = 100;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    int n = Integer.parseInt(rs.getString(1).substring(1));
                    max = Math.max(max, n);
                } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                    // non-numeric suffix — skip it, doesn't affect the running max
                }
            }
        } catch (SQLException e) {
            System.err.println("[BillingDAO] nextBillingId failed: " + e.getMessage());
        }
        return "B" + (max + 1);
    }

    /** Deletes a bill by Billing ID. Returns true if a row was actually removed. */
    public boolean delete(String billingId) {
        if (billingId == null || billingId.trim().isEmpty()) {
            return false;
        }
        String sql = "DELETE FROM billings WHERE billing_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, billingId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BillingDAO] delete failed for " + billingId + ": " + e.getMessage());
            return false;
        }
    }

    private static BillingModel mapRow(ResultSet rs) throws SQLException {
        BillingModel m = new BillingModel();
        m.setBillingId(rs.getString("billing_id"));
        m.setAppointmentId(SqlUtil.nullToBlank(rs.getString("appointment_id")));
        m.setPatientId(SqlUtil.nullToBlank(rs.getString("patient_id")));
        m.setDentistName(rs.getString("dentist_name"));
        m.setPatientName(rs.getString("patient_name"));
        m.setAppointmentDate(SqlUtil.formatDate(rs.getDate("appointment_date")));
        java.math.BigDecimal appt = rs.getBigDecimal("appointment_charges");
        m.setAppointmentCharges(appt == null ? 0 : appt.doubleValue());
        java.math.BigDecimal clinical = rs.getBigDecimal("clinical_total");
        m.setClinicalTotal(clinical == null ? 0 : clinical.doubleValue());
        java.math.BigDecimal medicine = rs.getBigDecimal("medicine_total");
        m.setMedicineTotal(medicine == null ? 0 : medicine.doubleValue());
        java.math.BigDecimal total = rs.getBigDecimal("total_bill_amount");
        m.setTotalBillAmount(total == null ? 0 : total.doubleValue());
        m.setEmailSentCount(rs.getInt("email_sent_count"));
        return m;
    }

    /** Bumps a bill's Sent Email Count by one — called after a genuinely successful send (BillPreviewDialog's Email button). */
    public boolean incrementEmailSentCount(String billingId) {
        if (billingId == null || billingId.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE billings SET email_sent_count = email_sent_count + 1 WHERE billing_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, billingId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BillingDAO] incrementEmailSentCount failed for " + billingId + ": " + e.getMessage());
            return false;
        }
    }
}
