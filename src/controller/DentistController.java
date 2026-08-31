package controller;

import model.User;

/**
 * Controller for the Dentist_Dashboard view.
 *
 * @author oveen
 */
public class DentistController {

    private final User currentUser;
    // Tracks a mid-session rename so getUsername()/subsequent saves target
    // the new row — currentUser itself stays untouched since User is
    // immutable and shared with AppController's session state.
    private String usernameOverride;

    public DentistController(User currentUser) {
        this.currentUser = currentUser;
    }

    // ── Dashboard data ────────────────────────────────────────────────────────

    /** "Hii Dr &lt;name&gt;, Welcome!!" — the real logged-in dentist's name, not a generic label. */
    public String getWelcomeMessage() {
        String name = getDisplayName();
        // Some accounts already have "Dr"/"Dr." baked into their full name
        // (older seed data); only prepend it when it's not already there,
        // so it never doubles up into "Hii Dr Dr. Smith".
        String withTitle = name.regionMatches(true, 0, "dr", 0, 2) ? name : "Dr " + name;
        return "Hii " + withTitle + ", Welcome!!";
    }

    public String getDisplayName() {
        return (currentUser != null && currentUser.getFullName() != null)
                ? currentUser.getFullName()
                : "Dentist";
    }

    public String getUsername() {
        if (usernameOverride != null) {
            return usernameOverride;
        }
        return currentUser != null && currentUser.getUsername() != null ? currentUser.getUsername() : "";
    }

    // Same convention as OfficeStaffController's badges: read the live
    // record count straight from each tile's own grid/controller, instead
    // of a static stub number — the badge always matches what you'd see
    // after clicking into that grid.
    // My Appointments is dentist-scoped, so it goes straight through
    // AppointmentManagementController rather than D_APP_Grid's own static
    // count (which is instance/session based now).
    public int getMyAppointments()  { return AppointmentManagementController.countForDentist(getDisplayName()); }
    public int getMyPatients()      { return view.D_PA_Grid.getRecordCount(getDisplayName()); }
    // Request Supplies is dentist-scoped, same as everything else on this
    // dashboard — each dentist only sees/counts their own request history
    // (supply_requests.dentist_name), not the whole clinic's.
    public int getRequestSupplies() { return SupplyRequestController.getRecordCountForDentist(getDisplayName()); }
    public int getRequestLeaves()   { return LeaveRequestController.getRecordCountForDentist(getDisplayName()); }

    /**
     * True only for a login Office Staff created with a system-generated
     * temporary password (Dentist Management) that hasn't been changed yet —
     * Dentist_Dashboard uses this to force the Edit Profile popup open on
     * this dentist's very first visit. Always false for a self-registered
     * account, which picked its own password at signup.
     */
    public boolean mustChangePassword() {
        return new dao.UserDAO().getMustChangePassword(getUsername());
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void openMyAppointments() {
        System.out.println("[CONTROLLER] Action triggered: My Appointments");
        javax.swing.SwingUtilities.invokeLater(() -> new view.D_APP_Grid(currentUser).setVisible(true));
    }
    public void openMyPatients() {
        System.out.println("[CONTROLLER] Action triggered: My Patients");
        javax.swing.SwingUtilities.invokeLater(() -> new view.D_PA_Grid(currentUser).setVisible(true));
    }
    public void openMySchedule() {
        System.out.println("[CONTROLLER] Action triggered: My Schedule");
        javax.swing.SwingUtilities.invokeLater(() -> new view.D_SCH_Calendar(currentUser).setVisible(true));
    }
    public void openMyProfile() {
        System.out.println("[CONTROLLER] Action triggered: My Profile");
        javax.swing.SwingUtilities.invokeLater(() -> new view.D_Profile_1(currentUser).setVisible(true));
    }

    public void openRequestSupplies() {
        System.out.println("[CONTROLLER] Action triggered: Request Supplies");
        javax.swing.SwingUtilities.invokeLater(() -> new view.D_RS_Grid(currentUser).setVisible(true));
    }

    public void openRequestLeaves() {
        System.out.println("[CONTROLLER] Action triggered: Request Leaves");
        javax.swing.SwingUtilities.invokeLater(() -> new view.D_RS_Leave(currentUser).setVisible(true));
    }

    public void editProfile() { System.out.println("[CONTROLLER] Action triggered: Edit Profile for " + getDisplayName()); }

    /**
     * Called after the Edit Profile popup's Save button. Either field may be
     * a no-op ({@code newUsername} unchanged from the current one, or
     * {@code newPassword} left blank) — see {@link ProfileSaveResult} for how
     * partial success/failure is reported back to the view.
     */
    public ProfileSaveResult saveProfileChanges(String newUsername, String newPassword) {
        dao.UserDAO userDao = new dao.UserDAO();
        String effectiveUsername = getUsername();

        ProfileSaveResult.Field usernameOutcome = ProfileSaveResult.Field.UNCHANGED;
        String trimmedUsername = newUsername != null ? newUsername.trim() : "";
        if (!trimmedUsername.isEmpty() && !trimmedUsername.equalsIgnoreCase(effectiveUsername)) {
            switch (userDao.renameUsername(effectiveUsername, trimmedUsername)) {
                case SUCCESS:
                    usernameOverride = trimmedUsername;
                    effectiveUsername = trimmedUsername;
                    controller.AppController.renameCurrentUser(trimmedUsername);
                    usernameOutcome = ProfileSaveResult.Field.SUCCESS;
                    break;
                case TAKEN:
                    usernameOutcome = ProfileSaveResult.Field.USERNAME_TAKEN;
                    break;
                default:
                    usernameOutcome = ProfileSaveResult.Field.FAILED;
                    break;
            }
        }

        ProfileSaveResult.Field passwordOutcome = ProfileSaveResult.Field.UNCHANGED;
        if (newPassword != null && !newPassword.isEmpty()) {
            boolean changed = userDao.updatePassword(effectiveUsername, newPassword);
            passwordOutcome = changed ? ProfileSaveResult.Field.SUCCESS : ProfileSaveResult.Field.FAILED;
            if (changed) {
                // They've now set their own password — the forced-change
                // popup (see mustChangePassword()) never needs to reappear.
                userDao.clearMustChangePassword(effectiveUsername);
            }
        }

        System.out.println("[CONTROLLER] Edit Profile saved for " + getDisplayName()
                + " — username:" + usernameOutcome + " password:" + passwordOutcome);
        return new ProfileSaveResult(usernameOutcome, passwordOutcome);
    }
    public void logout()      { System.out.println("[CONTROLLER] Action triggered: Logout for " + getDisplayName()); }
}
