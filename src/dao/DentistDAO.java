package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import model.DentistModel;

/**
 * Data access for the {@code dentists} table — backs
 * {@link controller.DentistManagementController}'s shared directory, which
 * every screen that needs a dentist's own schedule reads from (D_Profile,
 * D_RS_Leave, Appointment Management's Step 2, ...).
 *
 * @author oveen
 */
public class DentistDAO {

    private static final String SELECT_ALL_SQL =
            "SELECT dentist_id, title, gender, full_name, dob, nic, slmc_no, qualification, "
            + "university, graduation_year, specialization, experience, license_status, mobile_no, email, "
            + "address, emergency_no, joined_date, employment_type, consultation_fee, employment_status, "
            + "working_days, start_time, end_time, break_time, room_no FROM dentists ORDER BY dentist_id";

    /** Every dentist on record, keyed by full name — same shape DentistManagementController.getDirectory() always returned. */
    public Map<String, DentistModel> findAll() {
        Map<String, DentistModel> directory = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DentistModel m = mapRow(rs);
                directory.put(m.getFullName(), m);
            }
        } catch (SQLException e) {
            System.err.println("[DentistDAO] findAll failed: " + e.getMessage());
        }
        return directory;
    }

    /**
     * Inserts a new dentist, or updates the existing row if {@code dentist_id}
     * already exists (the wizard's "Dentist ID" field is the natural key,
     * same as the table's primary key in db/schema.sql).
     */
    public boolean upsert(DentistModel m) {
        if (m == null || m.getDentistId() == null || m.getDentistId().trim().isEmpty()) {
            System.err.println("[DentistDAO] upsert skipped: missing dentist_id");
            return false;
        }
        String sql =
                "INSERT INTO dentists (dentist_id, title, gender, full_name, dob, nic, slmc_no, "
                + "qualification, university, graduation_year, specialization, experience, license_status, "
                + "mobile_no, email, address, emergency_no, joined_date, employment_type, consultation_fee, "
                + "employment_status, working_days, start_time, end_time, break_time, room_no) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE title=VALUES(title), gender=VALUES(gender), full_name=VALUES(full_name), "
                + "dob=VALUES(dob), nic=VALUES(nic), slmc_no=VALUES(slmc_no), "
                + "qualification=VALUES(qualification), university=VALUES(university), "
                + "graduation_year=VALUES(graduation_year), specialization=VALUES(specialization), "
                + "experience=VALUES(experience), license_status=VALUES(license_status), mobile_no=VALUES(mobile_no), "
                + "email=VALUES(email), address=VALUES(address), emergency_no=VALUES(emergency_no), "
                + "joined_date=VALUES(joined_date), employment_type=VALUES(employment_type), "
                + "consultation_fee=VALUES(consultation_fee), employment_status=VALUES(employment_status), "
                + "working_days=VALUES(working_days), start_time=VALUES(start_time), end_time=VALUES(end_time), "
                + "break_time=VALUES(break_time), room_no=VALUES(room_no)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, m.getDentistId().trim());
            ps.setString(i++, m.getTitle());
            ps.setString(i++, m.getGender());
            ps.setString(i++, m.getFullName());
            ps.setDate(i++, SqlUtil.parseDate(m.getDob()));
            ps.setString(i++, m.getNic());
            ps.setString(i++, m.getSlmcNo());
            ps.setString(i++, m.getQualification());
            ps.setString(i++, m.getUniversity());
            // graduation_year is a YEAR column — setString() on it round-trips badly (see
            // SqlUtil.parseYear's javadoc), so go through setInt/setNull instead.
            Integer gradYear = SqlUtil.parseYear(m.getGraduationYear());
            if (gradYear != null) {
                ps.setInt(i++, gradYear);
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }
            ps.setString(i++, m.getSpecialization());
            ps.setString(i++, m.getExperience());
            ps.setString(i++, SqlUtil.blankToNull(m.getLicenseStatus()));
            ps.setString(i++, m.getMobileNo());
            ps.setString(i++, m.getEmail());
            ps.setString(i++, m.getAddress());
            ps.setString(i++, m.getEmergencyNo());
            ps.setDate(i++, SqlUtil.parseDate(m.getJoinedDate()));
            ps.setString(i++, m.getEmploymentType());
            ps.setBigDecimal(i++, parseFee(m.getConsultationFee()));
            ps.setString(i++, m.getEmploymentStatus());
            ps.setString(i++, m.getWorkingDays());
            ps.setTime(i++, SqlUtil.parseTime(m.getStartTime()));
            ps.setTime(i++, SqlUtil.parseTime(m.getEndTime()));
            ps.setString(i++, m.getBreakTime());
            ps.setString(i++, m.getRoomNo());
            ps.executeUpdate();
            // Not checking the affected-row count here: ON DUPLICATE KEY UPDATE
            // legitimately reports 0 rows when the row already matched every
            // value being written (a genuine no-op save, not a failure) — the
            // only real failure signal for this statement is an exception.
            return true;
        } catch (SQLException e) {
            System.err.println("[DentistDAO] upsert failed for " + m.getDentistId() + ": " + e.getMessage());
            return false;
        }
    }

    /** Next auto-generated Dentist ID, e.g. "D104" — one past the highest numeric suffix on record. */
    public String nextDentistId() {
        String sql = "SELECT dentist_id FROM dentists WHERE dentist_id LIKE 'D%'";
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
            System.err.println("[DentistDAO] nextDentistId failed: " + e.getMessage());
        }
        return "D" + (max + 1);
    }

    /**
     * Deletes a dentist by Dentist ID. Returns false (and leaves the row in
     * place) if it fails — most commonly because {@code appointments} has a
     * {@code RESTRICT} foreign key on dentist full_name, so a dentist with
     * existing appointments can't be deleted until those are reassigned or
     * removed first.
     */
    public boolean delete(String dentistId) {
        if (dentistId == null || dentistId.trim().isEmpty()) {
            return false;
        }
        String sql = "DELETE FROM dentists WHERE dentist_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DentistDAO] delete failed for " + dentistId + ": " + e.getMessage());
            return false;
        }
    }

    private static java.math.BigDecimal parseFee(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return new java.math.BigDecimal(text.trim().replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static DentistModel mapRow(ResultSet rs) throws SQLException {
        DentistModel m = new DentistModel();
        m.setDentistId(rs.getString("dentist_id"));
        m.setTitle(rs.getString("title"));
        m.setGender(rs.getString("gender"));
        m.setFullName(rs.getString("full_name"));
        m.setDob(SqlUtil.formatDate(rs.getDate("dob")));
        m.setNic(rs.getString("nic"));
        m.setSlmcNo(rs.getString("slmc_no"));
        m.setQualification(rs.getString("qualification"));
        m.setUniversity(rs.getString("university"));
        // getInt() (not getString()) sidesteps yearIsDateType returning "yyyy-01-01" for a YEAR column.
        int gradYear = rs.getInt("graduation_year");
        m.setGraduationYear(rs.wasNull() ? "" : String.valueOf(gradYear));
        m.setSpecialization(rs.getString("specialization"));
        m.setExperience(rs.getString("experience"));
        m.setLicenseStatus(rs.getString("license_status"));
        m.setMobileNo(rs.getString("mobile_no"));
        m.setEmail(rs.getString("email"));
        m.setAddress(rs.getString("address"));
        m.setEmergencyNo(rs.getString("emergency_no"));
        m.setJoinedDate(SqlUtil.formatDate(rs.getDate("joined_date")));
        m.setEmploymentType(rs.getString("employment_type"));
        java.math.BigDecimal fee = rs.getBigDecimal("consultation_fee");
        m.setConsultationFee(fee == null ? "" : fee.toPlainString());
        m.setEmploymentStatus(rs.getString("employment_status"));
        m.setWorkingDays(rs.getString("working_days"));
        m.setStartTime(SqlUtil.formatTime(rs.getTime("start_time")));
        m.setEndTime(SqlUtil.formatTime(rs.getTime("end_time")));
        m.setBreakTime(rs.getString("break_time"));
        m.setRoomNo(rs.getString("room_no"));
        return m;
    }
}
