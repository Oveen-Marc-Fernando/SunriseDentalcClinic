package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import model.AppointmentModel;

/**
 * Data access for the {@code appointments} table.
 *
 * Backs both OS_AM_Grid (every appointment, office-wide) and D_APP_Grid
 * (just the logged-in dentist's own) from the same table — they used to
 * carry two entirely separate hardcoded sample sets with different ID
 * schemes ("108"/"109"/"110" vs. "AP-201"/"AP-202"/"AP-203"); this unifies
 * them on the app-wide "APO101, APO102, ..." scheme already seeded in
 * db/schema.sql. Also backs
 * OS_AM_2's double-booking check, replacing its in-memory BOOKED_SLOTS set.
 *
 * Views never call this DAO directly — they go through
 * {@link controller.AppointmentManagementController}, which is also where
 * the appointment-scheduling business logic (working-day / slot
 * calculation) lives.
 *
 * @author oveen
 */
public class AppointmentDAO {

    private static final String BASE_SELECT_SQL =
            "SELECT a.appointment_id, a.patient_name, a.dentist_name, a.treatment_type, "
            + "a.appointment_date, a.appointment_time, a.status, a.address, a.contact_no, "
            + "p.city, p.mobile_no, d.room_no "
            + "FROM appointments a "
            + "LEFT JOIN patients p ON p.full_name = a.patient_name "
            + "LEFT JOIN dentists d ON d.full_name = a.dentist_name";

    /** Every appointment on record — OS_AM_Grid's "office-wide" view. */
    public List<AppointmentModel> findAll() {
        List<AppointmentModel> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE_SELECT_SQL + " ORDER BY a.appointment_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] findAll failed: " + e.getMessage());
        }
        return rows;
    }

    /** Just this dentist's own appointments — D_APP_Grid's "My Appointments" view. */
    public List<AppointmentModel> findByDentist(String dentistName) {
        List<AppointmentModel> rows = new ArrayList<>();
        String sql = BASE_SELECT_SQL + " WHERE a.dentist_name = ? ORDER BY a.appointment_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] findByDentist failed: " + e.getMessage());
        }
        return rows;
    }

    /** Single appointment by ID, or null if not found — used for Billing's Step 1 auto-fill. */
    public AppointmentModel findById(String appointmentId) {
        String sql = BASE_SELECT_SQL + " WHERE a.appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] findById failed: " + e.getMessage());
            return null;
        }
    }

    public int countAll() {
        return countWhere(null, null);
    }

    public int countForDentist(String dentistName) {
        return countWhere("dentist_name = ?", dentistName);
    }

    private int countWhere(String whereClause, String param) {
        String sql = "SELECT COUNT(*) FROM appointments" + (whereClause != null ? " WHERE " + whereClause : "");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] count failed: " + e.getMessage());
            return 0;
        }
    }

    /**
     * True if this dentist has any non-Rejected appointment on the given
     * date (any time) — used by D_RS_Leave to decide whether a leave
     * request for that date is even allowed.
     */
    public boolean hasAppointmentOnDate(String dentistName, LocalDate date) {
        String sql = "SELECT 1 FROM appointments WHERE dentist_name = ? AND appointment_date = ? "
                + "AND status <> 'Rejected' LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistName);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] hasAppointmentOnDate failed: " + e.getMessage());
            return false;
        }
    }

    /** True if this dentist already has a non-Rejected appointment at this exact date+time. */
    public boolean isBooked(String dentistName, LocalDate date, LocalTime time) {
        String sql = "SELECT 1 FROM appointments WHERE dentist_name = ? AND appointment_date = ? "
                + "AND appointment_time = ? AND status <> 'Rejected' LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistName);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setTime(3, java.sql.Time.valueOf(time));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] isBooked failed: " + e.getMessage());
            return false;
        }
    }

    /** Next auto-generated Appointment ID, e.g. "APO104" — one past the highest numeric suffix on record. */
    public String nextAppointmentId() {
        String sql = "SELECT appointment_id FROM appointments WHERE appointment_id LIKE 'APO%'";
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
            System.err.println("[AppointmentDAO] nextAppointmentId failed: " + e.getMessage());
        }
        return "APO" + (max + 1);
    }

    /** Inserts a new appointment. Called from OS_AM_2's "Save" button (via the controller). */
    /** Returns {@code true} only if the row was actually written — callers must not assume success otherwise. */
    public boolean insert(String appointmentId, String patientName, String dentistName, String treatmentType,
            LocalDate date, LocalTime time, String status, String address, String contactNo) {
        String sql = "INSERT INTO appointments (appointment_id, patient_name, dentist_name, treatment_type, "
                + "appointment_date, appointment_time, status, address, contact_no) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentId);
            ps.setString(2, patientName);
            ps.setString(3, dentistName);
            ps.setString(4, treatmentType);
            ps.setDate(5, java.sql.Date.valueOf(date));
            ps.setTime(6, java.sql.Time.valueOf(time));
            ps.setString(7, status);
            ps.setString(8, SqlUtil.blankToNull(address));
            ps.setString(9, SqlUtil.blankToNull(contactNo));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] insert failed for " + appointmentId + ": " + e.getMessage());
            return false;
        }
    }

    /** Deletes an appointment by Appointment ID. Returns true if a row was actually removed. */
    public boolean delete(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return false;
        }
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] delete failed for " + appointmentId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates every column this table actually owns — OS_AM_Grid's Edit
     * popup. ({@code city}/{@code mobile_no} in {@link model.AppointmentModel}
     * are read-only, joined in from the matching patient row, not real
     * columns here — see {@link #BASE_SELECT_SQL} — so they're deliberately
     * not settable through this method.)
     */
    public boolean update(String appointmentId, String patientName, String dentistName, String treatmentType,
            LocalDate date, LocalTime time, String status) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE appointments SET patient_name = ?, dentist_name = ?, treatment_type = ?, "
                + "appointment_date = ?, appointment_time = ?, status = ? WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientName);
            ps.setString(2, dentistName);
            ps.setString(3, treatmentType);
            ps.setDate(4, date != null ? java.sql.Date.valueOf(date) : null);
            ps.setTime(5, time != null ? java.sql.Time.valueOf(time) : null);
            ps.setString(6, status);
            ps.setString(7, appointmentId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] update failed for " + appointmentId + ": " + e.getMessage());
            return false;
        }
    }

    /** Updates just the status column — D_APP_Grid's clickable status pill (Completed/Pending/Rejected). */
    public void updateStatus(String appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, appointmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] updateStatus failed for " + appointmentId + ": " + e.getMessage());
        }
    }

    private static AppointmentModel mapRow(ResultSet rs) throws SQLException {
        AppointmentModel m = new AppointmentModel();
        m.setAppointmentId(rs.getString("appointment_id"));
        m.setPatientName(rs.getString("patient_name"));
        m.setDentistName(rs.getString("dentist_name"));
        m.setTreatmentType(rs.getString("treatment_type"));
        m.setDate(SqlUtil.formatDate(rs.getDate("appointment_date")));
        m.setTime(SqlUtil.formatTime(rs.getTime("appointment_time")));
        m.setStatus(rs.getString("status"));
        m.setAddress(SqlUtil.nullToBlank(rs.getString("address")));
        m.setContactNo(SqlUtil.nullToBlank(rs.getString("contact_no")));
        m.setCity(SqlUtil.nullToBlank(rs.getString("city")));
        m.setMobileNo(SqlUtil.nullToBlank(rs.getString("mobile_no")));
        m.setRoomNo(SqlUtil.nullToBlank(rs.getString("room_no")));
        return m;
    }
}
