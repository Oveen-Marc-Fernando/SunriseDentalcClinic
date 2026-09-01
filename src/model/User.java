package model;

import java.util.Objects;

/**
 * User model representing an authenticated system user.
 * Supports role-based access control (RBAC) for dashboard routing.
 *
 * @author oveen
 */
public final class User {

    /**
     * Enumeration of all supported user roles in the system.
     */
    public enum Role {
        OFFICE_STAFF,
        DENTIST,
        ADMINISTRATION,
        PATIENT,
        UNKNOWN
    }

    private final String username;
    private final String password;
    private final String fullName;
    private final Role   role;

    // ── Constructors ──────────────────────────────────────────────────────────

    /** Legacy constructor kept for backward-compatibility. */
    public User(String username, String password) {
        this(username, password, username, Role.UNKNOWN);
    }

    /** Full constructor used after authentication. */
    public User(String username, String password, String fullName, Role role) {
        this.username = Objects.requireNonNull(username, "username cannot be null").trim();
        this.password = Objects.requireNonNull(password, "password cannot be null");
        this.fullName = fullName != null ? fullName.trim() : username;
        this.role     = role != null ? role : Role.UNKNOWN;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public Role   getRole()     { return role; }

    // ── Convenience helpers ───────────────────────────────────────────────────

    public boolean isOfficeStaff()    { return role == Role.OFFICE_STAFF;   }
    public boolean isDentist()        { return role == Role.DENTIST;        }
    public boolean isAdministration() { return role == Role.ADMINISTRATION; }
    public boolean isPatient()        { return role == Role.PATIENT;        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username.toLowerCase(), user.username.toLowerCase()) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username.toLowerCase(), role);
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', fullName='" + fullName + "', role=" + role + '}';
    }
}