package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.PatientModel;

/**
 * Data access for the {@code patients} table — backs
 * {@link controller.PatientManagementController} (OS_PM_1-4), and read by
 * both OS_PM_Grid (Office Staff's full list) and D_PA_Grid (a dentist's own
 * "My Patients", same rows projected onto a more clinical set of columns).
 *
 * @author oveen
 */
public class PatientDAO {

    private static final String SELECT_ALL_SQL =
            "SELECT patient_id, title, gender, full_name, dob, age, nic, address_line1, address_line2, "
            + "city, mobile_no, landline_no, email, blood_group, allergies, medical_conditions, "
            + "current_medications, previous_surgeries, general_medical_notes, last_dental_visit, "
            + "dental_history, dental_problems, oral_hygiene, dental_medical_notes "
            + "FROM patients ORDER BY patient_id";

    /** Every patient on record. */
    public List<PatientModel> findAll() {
        List<PatientModel> patients = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PatientDAO] findAll failed: " + e.getMessage());
        }
        return patients;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("[PatientDAO] count failed: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Looks up a patient's Patient ID by full name — same best-effort match
     * as {@link #findEmailByFullName}, used so Billing Step 1 can link a
     * bill to a real Patient ID instead of just carrying a free-text name.
     * Returns "" if there's no match.
     */
    public String findIdByFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String sql = "SELECT patient_id FROM patients WHERE full_name = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? SqlUtil.nullToBlank(rs.getString(1)) : "";
            }
        } catch (SQLException e) {
            System.err.println("[PatientDAO] findIdByFullName failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * Looks up a patient's email by Patient ID — reliable, used in
     * preference to {@link #findEmailByFullName} whenever a billing row
     * carries a real patient_id, since patient_name on older billing rows
     * can include an honorific ("Mr. Thejaan") that won't exact-match this
     * table's plain full_name ("Thejaan"). Returns "" if there's no match
     * or no email on file.
     */
    public String findEmailById(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return "";
        }
        String sql = "SELECT email FROM patients WHERE patient_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? SqlUtil.nullToBlank(rs.getString(1)) : "";
            }
        } catch (SQLException e) {
            System.err.println("[PatientDAO] findEmailById failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * Looks up a patient's email by full name — Billing only carries the
     * patient's name (free text off the appointment), not a real foreign
     * key to this table, so this is a best-effort match. Returns "" if
     * there's no match or no email on file.
     */
    public String findEmailByFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String sql = "SELECT email FROM patients WHERE full_name = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? SqlUtil.nullToBlank(rs.getString(1)) : "";
            }
        } catch (SQLException e) {
            System.err.println("[PatientDAO] findEmailByFullName failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * Looks up a patient's full record by Patient ID — reliable, used in
     * preference to {@link #findByFullName} whenever a login is already
     * linked to a real patient_id (see UserDAO.getPatientId/linkPatientId).
     * Returns null if there's no match.
     */
    public PatientModel findById(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return null;
        }
        String sql =
                "SELECT patient_id, title, gender, full_name, dob, age, nic, address_line1, address_line2, "
                + "city, mobile_no, landline_no, email, blood_group, allergies, medical_conditions, "
                + "current_medications, previous_surgeries, general_medical_notes, last_dental_visit, "
                + "dental_history, dental_problems, oral_hygiene, dental_medical_notes "
                + "FROM patients WHERE patient_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("[PatientDAO] findById failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Looks up a patient's full record by full name — best-effort, same
     * convention as {@link #findEmailByFullName}/{@link #findIdByFullName};
     * this app has no real login-to-patient-record foreign key, so Patient
     * self-service (PA_PR_1..4) resolves "which patient record is this
     * logged-in user" this way. Returns null if there's no match.
     */
    public PatientModel findByFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return null;
        }
        String sql =
                "SELECT patient_id, title, gender, full_name, dob, age, nic, address_line1, address_line2, "
                + "city, mobile_no, landline_no, email, blood_group, allergies, medical_conditions, "
                + "current_medications, previous_surgeries, general_medical_notes, last_dental_visit, "
                + "dental_history, dental_problems, oral_hygiene, dental_medical_notes "
                + "FROM patients WHERE full_name = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("[PatientDAO] findByFullName failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Inserts a new patient, or updates the existing row if the Patient ID
     * already exists. Called from OS_PM_4's final submit (and Patient
     * self-service's My Profile wizard). Returns true only if a row was
     * actually written.
     */
    public boolean upsert(PatientModel m) {
        if (m == null || m.getPatientId() == null || m.getPatientId().trim().isEmpty()) {
            System.err.println("[PatientDAO] upsert skipped: missing patient_id");
            return false;
        }
        String sql =
                "INSERT INTO patients (patient_id, title, gender, full_name, dob, age, nic, "
                + "address_line1, address_line2, city, mobile_no, landline_no, email, blood_group, allergies, "
                + "medical_conditions, current_medications, previous_surgeries, general_medical_notes, "
                + "last_dental_visit, dental_history, dental_problems, oral_hygiene, "
                + "dental_medical_notes) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE title=VALUES(title), gender=VALUES(gender), full_name=VALUES(full_name), "
                + "dob=VALUES(dob), age=VALUES(age), nic=VALUES(nic), "
                + "address_line1=VALUES(address_line1), address_line2=VALUES(address_line2), city=VALUES(city), "
                + "mobile_no=VALUES(mobile_no), landline_no=VALUES(landline_no), email=VALUES(email), "
                + "blood_group=VALUES(blood_group), allergies=VALUES(allergies), "
                + "medical_conditions=VALUES(medical_conditions), current_medications=VALUES(current_medications), "
                + "previous_surgeries=VALUES(previous_surgeries), general_medical_notes=VALUES(general_medical_notes), "
                + "last_dental_visit=VALUES(last_dental_visit), dental_history=VALUES(dental_history), "
                + "dental_problems=VALUES(dental_problems), oral_hygiene=VALUES(oral_hygiene), "
                + "dental_medical_notes=VALUES(dental_medical_notes)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, m.getPatientId().trim());
            ps.setString(i++, m.getTitle());
            ps.setString(i++, m.getGender());
            ps.setString(i++, m.getFullName());
            ps.setDate(i++, SqlUtil.parseDate(m.getDob()));
            ps.setString(i++, m.getAge());
            ps.setString(i++, m.getNic());
            ps.setString(i++, m.getAddressLine1());
            ps.setString(i++, m.getAddressLine2());
            ps.setString(i++, m.getCity());
            ps.setString(i++, m.getMobileNo());
            ps.setString(i++, m.getLandlineNo());
            ps.setString(i++, m.getEmail());
            ps.setString(i++, m.getBloodGroup());
            ps.setString(i++, m.getAllergies());
            ps.setString(i++, m.getMedicalConditions());
            ps.setString(i++, m.getCurrentMedications());
            ps.setString(i++, m.getPreviousSurgeries());
            ps.setString(i++, m.getGeneralMedicalNotes());
            ps.setDate(i++, SqlUtil.parseDate(m.getLastDentalVisit()));
            ps.setString(i++, m.getDentalHistory());
            ps.setString(i++, m.getDentalProblems());
            ps.setString(i++, m.getOralHygiene());
            ps.setString(i++, m.getDentalMedicalNotes());
            // Note: MySQL's ON DUPLICATE KEY UPDATE reports 0 rows affected
            // when the update matched a row but changed nothing (values
            // identical to what's already stored) — that's still a real
            // success, not a failure, so success here means "no exception",
            // not "executeUpdate() > 0".
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[PatientDAO] upsert failed for " + m.getPatientId() + ": " + e.getMessage());
            return false;
        }
    }

    /** Next auto-generated Patient ID, e.g. "P104" — one past the highest numeric suffix on record. */
    public String nextPatientId() {
        String sql = "SELECT patient_id FROM patients WHERE patient_id LIKE 'P%'";
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
            System.err.println("[PatientDAO] nextPatientId failed: " + e.getMessage());
        }
        return "P" + (max + 1);
    }

    /** Deletes a patient by Patient ID. Returns true if a row was actually removed. */
    public boolean delete(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return false;
        }
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PatientDAO] delete failed for " + patientId + ": " + e.getMessage());
            return false;
        }
    }

    private static PatientModel mapRow(ResultSet rs) throws SQLException {
        PatientModel m = new PatientModel();
        m.setPatientId(rs.getString("patient_id"));
        m.setTitle(rs.getString("title"));
        m.setGender(rs.getString("gender"));
        m.setFullName(rs.getString("full_name"));
        m.setDob(SqlUtil.formatDate(rs.getDate("dob")));
        m.setAge(rs.getString("age"));
        m.setNic(rs.getString("nic"));
        m.setAddressLine1(rs.getString("address_line1"));
        m.setAddressLine2(rs.getString("address_line2"));
        m.setCity(rs.getString("city"));
        m.setMobileNo(rs.getString("mobile_no"));
        m.setLandlineNo(rs.getString("landline_no"));
        m.setEmail(rs.getString("email"));
        m.setBloodGroup(rs.getString("blood_group"));
        m.setAllergies(rs.getString("allergies"));
        m.setMedicalConditions(rs.getString("medical_conditions"));
        m.setCurrentMedications(rs.getString("current_medications"));
        m.setPreviousSurgeries(rs.getString("previous_surgeries"));
        m.setGeneralMedicalNotes(rs.getString("general_medical_notes"));
        m.setLastDentalVisit(SqlUtil.formatDate(rs.getDate("last_dental_visit")));
        m.setDentalHistory(rs.getString("dental_history"));
        m.setDentalProblems(rs.getString("dental_problems"));
        m.setOralHygiene(rs.getString("oral_hygiene"));
        m.setDentalMedicalNotes(rs.getString("dental_medical_notes"));
        return m;
    }
}
