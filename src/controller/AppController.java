package controller;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.User;
import view.Administration_Dashboard;
import view.Dentist_Dashboard;
import view.LoginForm;
import view.OfficeStaff_Dashboard;
import view.Patient_Dashboard;
import view.Public_Dashboard;
import view.Register;

/**
 * Central Application Controller — orchestrates the entire MVC lifecycle.
 *
 * Responsibilities:
 *  1. Bootstrap: sets LAF, then displays the Public_Dashboard.
 *  2. Navigation: provides static methods so any View/Controller can request
 *     navigation without creating Views directly in non-controller code.
 *  3. Session: tracks the currently authenticated user across all modules.
 *
 * This is the ONLY class that decides which View to instantiate — no View
 * should directly create another View for top-level navigation.
 *
 * @author oveen
 */
public final class AppController {

    // ── Session state ────────────────────────────────────────────────────────
    private static User currentUser;

    private AppController() { /* utility class — no instances */ }

    // =========================================================================
    // 1. Application Bootstrap
    // =========================================================================

    /**
     * Entry point called from main().  Sets up LAF and shows the public home.
     */
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            applyLookAndFeel();
            showPublicDashboard();
        });
    }

    /**
     * Applies a clean cross-platform look and feel.
     */
    private static void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            // Fallback to system LAF if Nimbus unavailable
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("[AppController] Could not set LAF: " + ex.getMessage());
        }
    }

    // =========================================================================
    // 2. Navigation (called by other controllers / views)
    // =========================================================================

    /** Shows the unauthenticated Public Dashboard (home page). */
    public static void showPublicDashboard() {
        currentUser = null;
        SwingUtilities.invokeLater(() -> {
            new Public_Dashboard().setVisible(true);
        });
    }

    /** Opens the Login dialog, parented against the given frame. */
    public static void showLogin(javax.swing.JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm(parent);
        });
    }

    /** Opens the Register dialog, parented against the given frame. */
    public static void showRegister(javax.swing.JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            new Register(parent);
        });
    }

    /**
     * Called after successful authentication.
     * Routes the user to the correct role-based dashboard.
     *
     * @param user the authenticated User object
     */
    public static void openDashboardForUser(User user) {
        if (user == null) {
            showPublicDashboard();
            return;
        }
        currentUser = user;

        SwingUtilities.invokeLater(() -> {
            switch (user.getRole()) {
                case ADMINISTRATION:
                    new Administration_Dashboard(user).setVisible(true);
                    break;
                case OFFICE_STAFF:
                    new OfficeStaff_Dashboard(user).setVisible(true);
                    break;
                case DENTIST:
                    new Dentist_Dashboard(user).setVisible(true);
                    break;
                case PATIENT:
                    new Patient_Dashboard(user).setVisible(true);
                    break;
                default:
                    showPublicDashboard();
                    break;
            }
        });
    }

    /**
     * Logs the current user out and returns to the Public Dashboard.
     *
     * @param currentDashboard the dashboard JFrame to dispose
     */
    public static void logout(javax.swing.JFrame currentDashboard) {
        currentUser = null;
        if (currentDashboard != null) {
            currentDashboard.dispose();
        }
        showPublicDashboard();
    }

    // =========================================================================
    // 3. Session
    // =========================================================================

    /** Returns the currently authenticated user, or null if logged out. */
    public static User getCurrentUser() {
        return currentUser;
    }

    /** Returns true if a user is currently logged in. */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Updates the session's username after a successful Edit Profile rename
     * (User is immutable, so this rebuilds it rather than mutating it in
     * place). No-op if nobody's logged in — callers only invoke this right
     * after a rename that already succeeded against the DB.
     */
    public static void renameCurrentUser(String newUsername) {
        if (currentUser == null || newUsername == null || newUsername.trim().isEmpty()) {
            return;
        }
        currentUser = new User(newUsername.trim(), currentUser.getPassword(),
                currentUser.getFullName(), currentUser.getRole());
    }
}
