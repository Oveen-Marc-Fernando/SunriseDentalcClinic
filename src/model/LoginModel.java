package model;

/**
 * Data-access layer for authentication and login validation.
 *
 * Backed by dao.UserDAO against the real {@code users} table (see
 * db/schema.sql) — LogInController's public API is unchanged, so nothing
 * calling this class needed to change when it stopped being an in-memory
 * store.
 *
 * Demo Credentials Reference (seeded by db/schema.sql)
 * ──────────────────────────
 *  Username        Password   Role
 *  ─────────────── ──────── ──────────────
 *  officestaff     staff123   OFFICE_STAFF
 *  dentist         dent456    DENTIST
 *  admin           admin789   ADMINISTRATION
 *  patient         pat000     PATIENT
 *  adminlegacy     1234       ADMINISTRATION
 *
 * @author oveen
 */
public class LoginModel {

    private final dao.UserDAO userDAO = new dao.UserDAO();

    /**
     * Validates credentials against the {@code users} table and returns a
     * populated {@link User} on success.
     *
     * @param username plain-text username
     * @param password plain-text password
     * @return a populated {@link User} on success, or {@code null} on failure
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        return userDAO.authenticate(username.trim(), password);
    }

    /**
     * Legacy method kept for backward-compatibility.
     */
    public boolean validateLogin(User user) {
        return user != null && authenticate(user.getUsername(), user.getPassword()) != null;
    }
}
