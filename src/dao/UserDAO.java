package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

/**
 * Data access for the {@code users} table — backs {@link model.LoginModel}'s
 * authenticate() call.
 *
 * Passwords are compared as plain text here only because that's what
 * LoginModel always did (a stub/demo auth store, matching db/schema.sql's
 * seed data) — hash them (e.g. BCrypt) on the way in and compare hashes here
 * before any real deployment.
 *
 * @author oveen
 */
public class UserDAO {

    /**
     * Looks up {@code username}, and returns the matching {@link User} only
     * if the stored password also matches AND the account is APPROVED.
     * Returns {@code null} on any mismatch, unknown username, a still-PENDING
     * or REJECTED account, or database error — callers can't tell those apart
     * (same as the in-memory LoginModel this replaces); use {@link #getStatus}
     * separately if the caller wants a more specific reason to show.
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        String sql = "SELECT username, password, full_name, role, status FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                if (!rs.getString("password").equals(password)) {
                    return null;
                }
                if (!"APPROVED".equals(rs.getString("status"))) {
                    return null;
                }
                User.Role role;
                try {
                    role = User.Role.valueOf(rs.getString("role"));
                } catch (IllegalArgumentException e) {
                    role = User.Role.UNKNOWN;
                }
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("full_name"), role);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] authenticate failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Raw account status ("PENDING"/"APPROVED"/"REJECTED"), or {@code null}
     * if the username doesn't exist. Lets a failed login distinguish "wrong
     * password" from "account not approved yet" without changing
     * {@link #authenticate}'s own can't-tell-them-apart contract.
     */
    public String getStatus(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT status FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("status") : null;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getStatus failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Registers a new self-signup account (Register.java) — always starts
     * life PENDING, so it can't log in until an Administrator approves it
     * (see {@link #approve}/{@link #reject}, AD_APR_UserLogins). Returns
     * {@code false} on a duplicate username or any DB failure.
     */
    public boolean insertPending(String username, String password, String fullName, String email,
            String nic, String contactNumber, User.Role role) {
        return insert(username, password, fullName, email, nic, contactNumber, role, false);
    }

    /**
     * Same as {@link #insertPending}, but the account starts life already
     * APPROVED (with today's date stamped) instead of waiting on an
     * Administrator — for roles that don't need review before logging in
     * (Patient self-registration; see RegisterController).
     */
    public boolean insertApproved(String username, String password, String fullName, String email,
            String nic, String contactNumber, User.Role role) {
        return insert(username, password, fullName, email, nic, contactNumber, role, true);
    }

    /**
     * Same shape as {@link #insertApproved}, but for an account Office Staff
     * creates on someone else's behalf with a system-generated temporary
     * password (Dentist Management) — flags {@code must_change_password} so
     * the Edit Profile popup forces itself open on that person's first
     * dashboard visit until they pick their own password.
     */
    public boolean insertApprovedWithTempPassword(String username, String tempPassword, String fullName,
            String email, String nic, String contactNumber, User.Role role) {
        if (username == null || username.trim().isEmpty() || tempPassword == null || tempPassword.isEmpty()
                || role == null) {
            return false;
        }
        String sql = "INSERT INTO users (username, password, full_name, email, nic, contact_number, role, "
                + "status, approved_date, must_change_password) VALUES (?,?,?,?,?,?,?,'APPROVED',CURDATE(),TRUE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, tempPassword);
            ps.setString(3, fullName != null ? fullName.trim() : username.trim());
            ps.setString(4, SqlUtil.blankToNull(email));
            ps.setString(5, SqlUtil.blankToNull(nic));
            ps.setString(6, SqlUtil.blankToNull(contactNumber));
            ps.setString(7, role.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] insertApprovedWithTempPassword failed for " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Registers a dentist login Office Staff is creating on someone else's
     * behalf (Dentist Management), but — unlike {@link #insertApprovedWithTempPassword} —
     * starts life PENDING instead of APPROVED: this request now has to go
     * through Administration's own approval queue (AD_APR_UserLogins) same
     * as a self-registered dentist, before a real temporary password is
     * generated and emailed (see {@link #approveDentistLoginWithTempPassword}).
     * {@code dentistId} is linked immediately — that's what lets
     * {@link controller.UserApprovalController#approve} tell this kind of
     * pending row apart from an ordinary self-signup one. The password
     * column is NOT NULL, so a random placeholder goes in for now; it's
     * unusable (login is blocked while PENDING) and gets overwritten the
     * moment this request is actually approved.
     */
    public boolean insertPendingDentistLogin(String username, String fullName, String email,
            String nic, String contactNumber, String dentistId) {
        if (username == null || username.trim().isEmpty() || dentistId == null || dentistId.trim().isEmpty()) {
            return false;
        }
        String placeholderPassword = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO users (username, password, full_name, email, nic, contact_number, role, "
                + "status, dentist_id, must_change_password) VALUES (?,?,?,?,?,?,?,'PENDING',?,TRUE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, placeholderPassword);
            ps.setString(3, fullName != null ? fullName.trim() : username.trim());
            ps.setString(4, SqlUtil.blankToNull(email));
            ps.setString(5, SqlUtil.blankToNull(nic));
            ps.setString(6, SqlUtil.blankToNull(contactNumber));
            ps.setString(7, User.Role.DENTIST.name());
            ps.setString(8, dentistId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] insertPendingDentistLogin failed for " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Approves a pending Office-Staff-created dentist login request — sets
     * the real system-generated password, flips status to APPROVED, and
     * stamps today's date. {@code must_change_password} was already TRUE
     * from {@link #insertPendingDentistLogin}, so it's just reasserted here
     * for clarity. Called from {@link controller.UserApprovalController#approve}
     * once it's identified this pending row as this kind (linked dentist_id),
     * right before the temporary password gets emailed.
     */
    public boolean approveDentistLoginWithTempPassword(String username, String tempPassword) {
        if (username == null || username.trim().isEmpty() || tempPassword == null || tempPassword.isEmpty()) {
            return false;
        }
        String sql = "UPDATE users SET password = ?, status = 'APPROVED', approved_date = CURDATE(), "
                + "must_change_password = TRUE WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tempPassword);
            ps.setString(2, username.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] approveDentistLoginWithTempPassword failed for " + username + ": " + e.getMessage());
            return false;
        }
    }

    /** True if this login must change its password before doing anything else — see {@link #insertApprovedWithTempPassword}. */
    public boolean getMustChangePassword(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT must_change_password FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getMustChangePassword failed: " + e.getMessage());
            return false;
        }
    }

    /** Clears the forced-password-change flag — called once they actually pick their own password. */
    public boolean clearMustChangePassword(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE users SET must_change_password = FALSE WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] clearMustChangePassword failed: " + e.getMessage());
            return false;
        }
    }

    private boolean insert(String username, String password, String fullName, String email,
            String nic, String contactNumber, User.Role role, boolean autoApprove) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()
                || role == null) {
            return false;
        }
        String sql = autoApprove
                ? "INSERT INTO users (username, password, full_name, email, nic, contact_number, role, status, approved_date) "
                        + "VALUES (?,?,?,?,?,?,?,'APPROVED',CURDATE())"
                : "INSERT INTO users (username, password, full_name, email, nic, contact_number, role, status) "
                        + "VALUES (?,?,?,?,?,?,?,'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password);
            ps.setString(3, fullName != null ? fullName.trim() : username.trim());
            ps.setString(4, SqlUtil.blankToNull(email));
            ps.setString(5, SqlUtil.blankToNull(nic));
            ps.setString(6, SqlUtil.blankToNull(contactNumber));
            ps.setString(7, role.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] insert failed for " + username + ": " + e.getMessage());
            return false;
        }
    }

    /** This login's own full name, or null if the username doesn't exist. */
    public String getFullName(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT full_name FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getFullName failed: " + e.getMessage());
            return null;
        }
    }

    /** This login's own registered email (set at signup), or null if there isn't one on file. */
    public String getEmail(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT email FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getEmail failed: " + e.getMessage());
            return null;
        }
    }

    /** This login's own registered NIC (set at signup), or null if there isn't one on file. */
    public String getNic(String username) {
        return getSingleColumn("nic", username);
    }

    /** This login's own registered contact number (set at signup), or null if there isn't one on file. */
    public String getContactNumber(String username) {
        return getSingleColumn("contact_number", username);
    }

    private String getSingleColumn(String column, String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT " + column + " FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getSingleColumn(" + column + ") failed: " + e.getMessage());
            return null;
        }
    }

    /** Every account on record, in username order — backs AD_APR_UserLogins. */
    public java.util.List<model.UserApprovalModel> findAllForApproval() {
        java.util.List<model.UserApprovalModel> rows = new java.util.ArrayList<>();
        String sql = "SELECT username, full_name, role, status, approved_date FROM users ORDER BY username";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int n = 1;
            while (rs.next()) {
                model.UserApprovalModel m = new model.UserApprovalModel();
                m.setLoginId("L" + (100 + n++));
                m.setUsername(rs.getString("username"));
                m.setFullName(rs.getString("full_name"));
                m.setLoginType(rs.getString("role"));
                m.setStatus(rs.getString("status"));
                m.setApprovedDate(SqlUtil.formatDate(rs.getDate("approved_date")));
                rows.add(m);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findAllForApproval failed: " + e.getMessage());
        }
        return rows;
    }

    /** Approves a pending account (sets status=APPROVED and stamps today's date) — AD_APR_UserLogins' Approve button. */
    public boolean approve(String username) {
        return setStatus(username, "APPROVED", true);
    }

    /** Rejects a pending account — AD_APR_UserLogins' Reject button. */
    public boolean reject(String username) {
        return setStatus(username, "REJECTED", false);
    }

    private boolean setStatus(String username, String status, boolean stampToday) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String sql = stampToday
                ? "UPDATE users SET status = ?, approved_date = CURDATE() WHERE username = ?"
                : "UPDATE users SET status = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, username.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] setStatus(" + status + ") failed for " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * The patient_id this login is permanently linked to (set the first time
     * that login's My Profile wizard saves anything), or null if it's never
     * been linked yet — either a non-Patient account, or a Patient account
     * that predates this link and still needs a one-time name-match fallback.
     */
    public String getPatientId(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT patient_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getPatientId failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Permanently links this login to a real "patients" row — from then on,
     * {@link #getPatientId} finds it directly by ID, so editing that
     * patient's Full Name in the self-service wizard can never again break
     * "which patient record is mine" (the bug this link exists to fix).
     */
    public boolean linkPatientId(String username, String patientId) {
        if (username == null || username.trim().isEmpty() || patientId == null || patientId.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE users SET patient_id = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId.trim());
            ps.setString(2, username.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] linkPatientId failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * The dentist_id this login is permanently linked to (set the first time
     * that login's My Profile wizard saves anything), or null if it's never
     * been linked yet — same convention as {@link #getPatientId}, for a
     * DENTIST-role login and its own row in "dentists".
     */
    public String getDentistId(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT dentist_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getDentistId failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Permanently links this login to a real "dentists" row — same
     * convention as {@link #linkPatientId}, so editing that dentist's own
     * Full Name in the self-service wizard can never again break "which
     * dentist record is mine".
     */
    public boolean linkDentistId(String username, String dentistId) {
        if (username == null || username.trim().isEmpty() || dentistId == null || dentistId.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE users SET dentist_id = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistId.trim());
            ps.setString(2, username.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] linkDentistId failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reverse lookup of {@link #getDentistId} — the username (if any) already
     * linked to this Dentist ID, or null if that dentist has no login yet.
     * Used by OS_DM_Grid to decide whether its "Create Login" action applies
     * to a given row (dentists added directly by Office Staff, like D101/
     * D102, start out with a "dentists" record but no matching "users" row).
     * Excludes REJECTED rows on purpose — a rejected login request must not
     * permanently block that dentist from ever getting a working login;
     * without this, {@link #insertPendingDentistLogin}'s rejected row would
     * make every future lookup here think one already "exists."
     */
    public String getUsernameForDentistId(String dentistId) {
        if (dentistId == null || dentistId.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT username FROM users WHERE dentist_id = ? AND status <> 'REJECTED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentistId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getUsernameForDentistId failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Reverse lookup of {@link #getPatientId} — the username (if any)
     * already linked to this Patient ID, or null if that patient has no
     * login yet. Used by OS_PM_Grid to decide whether its "Create Login"
     * action applies to a given row, same convention as
     * {@link #getUsernameForDentistId}.
     */
    public String getUsernameForPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT username FROM users WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getUsernameForPatientId failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Overwrites {@code username}'s stored password with {@code newPassword}.
     * Returns {@code true} only if a row was actually changed (i.e. the
     * username exists) — callers should treat {@code false} as a failure,
     * not silently assume the change took effect.
     */
    public boolean updatePassword(String username, String newPassword) {
        if (username == null || username.trim().isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] updatePassword failed: " + e.getMessage());
            return false;
        }
    }

    /** Outcome of {@link #renameUsername}, distinguishing why a rename didn't happen. */
    public enum RenameOutcome { SUCCESS, TAKEN, NOT_FOUND, FAILED }

    /**
     * Renames {@code oldUsername} to {@code newUsername}. {@code username}
     * is the table's primary key, so a collision with an existing row comes
     * back as {@link RenameOutcome#TAKEN} rather than a generic failure —
     * callers can show that as a specific "already taken" message instead of
     * a vague DB error.
     */
    public RenameOutcome renameUsername(String oldUsername, String newUsername) {
        if (oldUsername == null || oldUsername.trim().isEmpty()
                || newUsername == null || newUsername.trim().isEmpty()) {
            return RenameOutcome.FAILED;
        }
        String trimmedOld = oldUsername.trim();
        String trimmedNew = newUsername.trim();
        if (trimmedNew.equalsIgnoreCase(trimmedOld)) {
            return RenameOutcome.SUCCESS; // no actual change requested
        }
        String sql = "UPDATE users SET username = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trimmedNew);
            ps.setString(2, trimmedOld);
            return ps.executeUpdate() > 0 ? RenameOutcome.SUCCESS : RenameOutcome.NOT_FOUND;
        } catch (SQLException e) {
            // MySQL duplicate-key on the PRIMARY KEY column (e.g. "1062 Duplicate entry").
            if ("23000".equals(e.getSQLState())) {
                return RenameOutcome.TAKEN;
            }
            System.err.println("[UserDAO] renameUsername failed: " + e.getMessage());
            return RenameOutcome.FAILED;
        }
    }

    /**
     * Keeps a login's own display name in sync with its linked profile
     * record's Full Name. Without this, editing Full Name in My Profile
     * (DentistProfileController / PatientProfileController) only updates
     * "dentists"/"patients" — which cascades fine into "appointments" and
     * "leave_requests" (their dentist_name/patient_name columns have
     * ON UPDATE CASCADE FKs to those tables) — but "users.full_name" has no
     * such FK and silently drifts out of sync. Every "my own data" screen
     * that filters by {@code currentUser.getFullName()} (dashboard badges,
     * D_APP_Grid, D_PA_Grid, D_RS_Leave, ...) then stops matching anything,
     * since the cascaded tables have already moved on to the new name.
     */
    public boolean updateFullName(String username, String fullName) {
        if (username == null || username.trim().isEmpty() || fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE users SET full_name = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            ps.setString(2, username.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] updateFullName failed: " + e.getMessage());
            return false;
        }
    }
}
