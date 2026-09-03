package controller;

import model.User;

/**
 * Controller for the OfficeStaff_Dashboard view.
 *
 * @author oveen
 */
public class OfficeStaffController {

    private final User currentUser;
    // Tracks a mid-session rename so getUsername()/subsequent saves target
    // the new row — currentUser itself stays untouched since User is
    // immutable and shared with AppController's session state.
    private String usernameOverride;

    public OfficeStaffController(User currentUser) {
        this.currentUser = currentUser;
    }

    // ── Dashboard data ────────────────────────────────────────────────────────

    public String getWelcomeMessage() {
        return "Hii Receptionist, Welcome!!";
    }

    public String getDisplayName() {
        return (currentUser != null && currentUser.getFullName() != null)
                ? currentUser.getFullName()
                : "Office Staff";
    }

    public String getUsername() {
        if (usernameOverride != null) {
            return usernameOverride;
        }
        return currentUser != null && currentUser.getUsername() != null ? currentUser.getUsername() : "";
    }

    // These read straight from each grid's own sample-data record count
    // (see e.g. OS_DM_Grid.getRecordCount()) rather than the dashboard
    // model's static placeholder numbers, so a tile's badge always matches
    // the row count you'd actually see after clicking into that grid.
    public int getTotalDentists()       { return view.OS_DM_Grid.getRecordCount(); }
    public int getTotalPatients()       { return view.OS_PM_Grid.getRecordCount(); }
    public int getPendingAppointments() { return view.OS_AM_Grid.getRecordCount(); }
    public int getPendingBillings()     { return view.OS_BM_Grid.getRecordCount(); }
    public int getPendingApprovals()    { return view.OS_APM_Grid.getRecordCount(); }
    public int getOpenHelpDesk()        { return view.OS_IM_Grid.getRecordCount(); }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void openDentistManagement() {
        System.out.println("[CONTROLLER] Action triggered: Dentist Management");
        javax.swing.SwingUtilities.invokeLater(() -> new view.OS_DM_Grid().setVisible(true));
    }
    public void openPatientManagement() {
        System.out.println("[CONTROLLER] Action triggered: Patient Management");
        javax.swing.SwingUtilities.invokeLater(() -> new view.OS_PM_Grid().setVisible(true));
    }
    public void openAppointmentManagement() {
        System.out.println("[CONTROLLER] Action triggered: Appointment Management");
        javax.swing.SwingUtilities.invokeLater(() -> new view.OS_AM_Grid().setVisible(true));
    }
    public void openBillings() {
        System.out.println("[CONTROLLER] Action triggered: Billings");
        javax.swing.SwingUtilities.invokeLater(() -> new view.OS_BM_Grid().setVisible(true));
    }
    public void openHelpDesk() {
        System.out.println("[CONTROLLER] Action triggered: Inventory Management");
        javax.swing.SwingUtilities.invokeLater(() -> new view.OS_IM_Grid().setVisible(true));
    }
    public void openApprovals() {
        System.out.println("[CONTROLLER] Action triggered: Approvals");
        javax.swing.SwingUtilities.invokeLater(() -> new view.OS_APM_Grid().setVisible(true));
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
            passwordOutcome = userDao.updatePassword(effectiveUsername, newPassword)
                    ? ProfileSaveResult.Field.SUCCESS : ProfileSaveResult.Field.FAILED;
        }

        System.out.println("[CONTROLLER] Edit Profile saved for " + getDisplayName()
                + " — username:" + usernameOutcome + " password:" + passwordOutcome);
        return new ProfileSaveResult(usernameOutcome, passwordOutcome);
    }

    public void logout() { System.out.println("[CONTROLLER] Action triggered: Logout for " + getDisplayName()); }
}
