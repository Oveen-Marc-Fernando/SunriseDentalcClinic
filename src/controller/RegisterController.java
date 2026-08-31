package controller;

import model.User;

/**
 * Controller for the Register screen — creates a new self-signup account
 * that starts life PENDING (see {@code dao.UserDAO#insertPending}) and can't
 * log in until an Administrator approves it via AD_APR_UserLogins.
 *
 * @author oveen
 */
public class RegisterController {

    private static final dao.UserDAO USER_DAO = new dao.UserDAO();

    /** Outcome of {@link #register}, distinguishing why it didn't succeed. */
    public enum RegisterOutcome { SUCCESS, USERNAME_TAKEN, FAILED }

    public static RegisterOutcome register(String username, String password, String fullName, String email,
            String nic, String contactNumber, String roleLabel) {
        User.Role role = mapRole(roleLabel);
        // Patient accounts don't need Administrator review — they can log
        // in the instant they register. Dentist (and anything else) still
        // goes through the PENDING/approve flow (AD_APR_UserLogins).
        boolean autoApprove = role == User.Role.PATIENT;
        boolean ok = autoApprove
                ? USER_DAO.insertApproved(username, password, fullName, email, nic, contactNumber, role)
                : USER_DAO.insertPending(username, password, fullName, email, nic, contactNumber, role);
        if (!ok) {
            // insert(...) only fails on a duplicate PK or a real DB error; a
            // quick existence check afterward is the simplest way to tell
            // them apart without teaching the DAO to parse MySQL error codes.
            return USER_DAO.getStatus(username) != null ? RegisterOutcome.USERNAME_TAKEN : RegisterOutcome.FAILED;
        }

        sendConfirmationEmail(email, fullName, roleLabel, autoApprove);
        return RegisterOutcome.SUCCESS;
    }

    /**
     * Fire-and-forget — registration itself already succeeded in the
     * database, so a slow/unreachable SMTP server (or email simply not
     * being configured yet) must never fail or block the registration flow.
     */
    private static void sendConfirmationEmail(String email, String fullName, String roleLabel, boolean autoApprove) {
        if (email == null || email.trim().isEmpty() || !mail.MailSender.isConfigured()) {
            return;
        }
        String trimmedEmail = email.trim();
        new Thread(() -> {
            try {
                if (autoApprove) {
                    mail.MailSender.sendRegistrationSuccessEmail(trimmedEmail, fullName);
                } else {
                    mail.MailSender.sendRegistrationPendingEmail(trimmedEmail, fullName, roleLabel);
                }
            } catch (Exception e) {
                System.err.println("[RegisterController] confirmation email failed: " + e.getMessage());
            }
        }, "registration-email").start();
    }

    private static User.Role mapRole(String roleLabel) {
        if (roleLabel == null) {
            return User.Role.UNKNOWN;
        }
        switch (roleLabel.trim()) {
            case "Office Staff": return User.Role.OFFICE_STAFF;
            case "Dentist":      return User.Role.DENTIST;
            case "Patient":      return User.Role.PATIENT;
            default:             return User.Role.UNKNOWN;
        }
    }
}
