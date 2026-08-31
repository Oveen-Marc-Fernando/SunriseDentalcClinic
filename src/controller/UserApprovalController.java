package controller;

import java.util.List;
import model.UserApprovalModel;

/**
 * Controller for AD_APR_UserLogins — Administration's screen for approving
 * or rejecting pending accounts. Two different kinds land here: a
 * self-registered account (see {@link RegisterController}) that picked its
 * own password at signup, and an Office-Staff-created dentist login request
 * (see {@link dao.UserDAO#insertPendingDentistLogin}) that has no usable
 * password yet — {@link #approve} tells them apart and reacts accordingly.
 *
 * @author oveen
 */
public class UserApprovalController {

    private static final dao.UserDAO USER_DAO = new dao.UserDAO();

    public static List<UserApprovalModel> getAll() {
        return USER_DAO.findAllForApproval();
    }

    /**
     * Approves a pending account. Branches on whether this row is an
     * Office-Staff-created dentist login request (see
     * {@link dao.UserDAO#insertPendingDentistLogin} — identifiable by
     * already having a linked dentist_id, unlike an ordinary self-signup):
     * those get a freshly generated temporary password and a "here's your
     * password" email instead of the plain "you're approved" notice a
     * self-registered account gets, since a self-signup already picked
     * their own password back at Register.java and has nothing to be
     * emailed.
     */
    public static boolean approve(String username) {
        String dentistId = USER_DAO.getDentistId(username);
        if (dentistId != null && !dentistId.trim().isEmpty()) {
            return approveDentistLoginRequest(username);
        }
        boolean ok = USER_DAO.approve(username);
        if (ok) {
            sendApprovedEmail(username);
        }
        return ok;
    }

    public static boolean reject(String username) {
        return USER_DAO.reject(username);
    }

    private static boolean approveDentistLoginRequest(String username) {
        String tempPassword = generateTempPassword();
        boolean ok = USER_DAO.approveDentistLoginWithTempPassword(username, tempPassword);
        if (ok) {
            sendTemporaryPasswordEmail(username, tempPassword);
        }
        return ok;
    }

    /** A random 10-character temporary password (letters + digits) — never shown on screen, only ever emailed. */
    private static String generateTempPassword() {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789"; // no 0/O/1/l/I — easy to misread
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Fire-and-forget, same convention as {@link #sendApprovedEmail} — the
     * approval + new password already succeeded in the database, so a
     * slow/unreachable SMTP server must never block or fail it.
     */
    private static void sendTemporaryPasswordEmail(String username, String tempPassword) {
        String email = USER_DAO.getEmail(username);
        if (email == null || email.trim().isEmpty() || !mail.MailSender.isConfigured()) {
            return;
        }
        String trimmedEmail = email.trim();
        String fullName = USER_DAO.getFullName(username);
        new Thread(() -> {
            try {
                mail.MailSender.sendTemporaryPasswordEmail(trimmedEmail, fullName, username, tempPassword);
            } catch (Exception e) {
                System.err.println("[UserApprovalController] temporary password email failed: " + e.getMessage());
            }
        }, "temp-password-email").start();
    }

    /**
     * Fire-and-forget — the approval itself already succeeded in the
     * database, so a slow/unreachable SMTP server (or email simply not
     * being on file / not configured yet) must never fail or block it.
     */
    private static void sendApprovedEmail(String username) {
        String email = USER_DAO.getEmail(username);
        if (email == null || email.trim().isEmpty() || !mail.MailSender.isConfigured()) {
            return;
        }
        String trimmedEmail = email.trim();
        String fullName = USER_DAO.getFullName(username);
        new Thread(() -> {
            try {
                mail.MailSender.sendAccountApprovedEmail(trimmedEmail, fullName, username);
            } catch (Exception e) {
                System.err.println("[UserApprovalController] approval email failed: " + e.getMessage());
            }
        }, "approval-email").start();
    }
}
