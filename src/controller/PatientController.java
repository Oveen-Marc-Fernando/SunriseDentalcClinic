package controller;

import model.PatientDashboardModel;
import model.PatientModel;
import model.User;

/**
 * Controller for the Patient_Dashboard view.
 * Mediates between the view and {@link PatientDashboardModel}.
 *
 * @author oveen
 */
public class PatientController {

    private static final dao.PatientDAO PATIENT_DAO = new dao.PatientDAO();
    private static final dao.UserDAO USER_DAO = new dao.UserDAO();

    private final User                 currentUser;
    private final PatientDashboardModel model;
    // Tracks a mid-session rename so getUsername()/subsequent saves target
    // the new row — currentUser itself stays untouched since User is
    // immutable and shared with AppController's session state.
    private String usernameOverride;

    public PatientController(User currentUser) {
        this.currentUser = currentUser;
        this.model       = new PatientDashboardModel();
    }

    // ── Dashboard data ────────────────────────────────────────────────────────

    /** "Hii &lt;name&gt;, Welcome!!" — the real logged-in patient's name, not a generic label. */
    public String getWelcomeMessage() {
        return "Hii " + getDisplayName() + ", Welcome!!";
    }

    public String getDisplayName() {
        return (currentUser != null && currentUser.getFullName() != null)
                ? currentUser.getFullName()
                : "Patient";
    }

    public String getUsername() {
        if (usernameOverride != null) {
            return usernameOverride;
        }
        return currentUser != null && currentUser.getUsername() != null ? currentUser.getUsername() : "";
    }

    public int    getUpcomingAppointments() { return model.getUpcomingAppointments(); }
    public int    getPastAppointments()     { return model.getPastAppointments(); }
    public double getOutstandingBill()      { return model.getOutstandingBill(); }
    public int    getOpenHelpDesk()         { return model.getOpenHelpDesk(); }
    public int    getAvailableReports()     { return model.getAvailableReports(); }

    // ── "My Appointments"/"My Billings" tile badges + Medical Notes ─────────
    // This app has no real login-to-patient-record foreign key, so — same
    // best-effort exact-name-match convention used by Billing's own email
    // lookup elsewhere — these count/read records whose patient_name /
    // full_name matches this logged-in user's display name.

    /** Live count of this patient's own appointments — "My Appointments" tile badge. */
    public int getMyAppointmentCount() {
        if (currentUser == null) return 0;
        String name = getDisplayName();
        int count = 0;
        for (model.AppointmentModel a : AppointmentManagementController.getAll()) {
            if (name.equalsIgnoreCase(a.getPatientName())) count++;
        }
        return count;
    }

    /** Live count of this patient's own bills — "My Billings" tile badge. */
    public int getMyBillingCount() {
        if (currentUser == null) return 0;
        String name = getDisplayName();
        int count = 0;
        for (model.BillingModel b : BillingManagementController.getAll()) {
            if (name.equalsIgnoreCase(b.getPatientName())) count++;
        }
        return count;
    }

    /**
     * "Good"/"Fair"/"Poor" from this patient's own Dental Information (My
     * Profile step 4) — the Medical Notes panel's live status line. Returns
     * null if there's no matching patient record yet, or it was never set.
     */
    public String getOralHygiene() {
        if (currentUser == null) return null;
        String linkedId = USER_DAO.getPatientId(currentUser.getUsername());
        PatientModel m = (linkedId != null && !linkedId.trim().isEmpty()) ? PATIENT_DAO.findById(linkedId) : null;
        if (m == null) {
            m = PATIENT_DAO.findByFullName(currentUser.getFullName()); // legacy fallback, same as PatientProfileController
        }
        if (m == null) return null;
        return m.getOralHygiene();
    }

    /**
     * A one-line reminder for this patient's soonest upcoming appointment
     * (today or later, not Rejected) — the dashboard's highlights panel.
     * Same best-effort name-match convention as {@link #getMyAppointmentCount()}.
     * Never null — falls back to an invitation to book one.
     */
    public String getAppointmentReminder() {
        if (currentUser == null) {
            return "No upcoming appointments on file.";
        }
        String name = getDisplayName();
        model.AppointmentModel soonest = null;
        java.time.LocalDate today = java.time.LocalDate.now();
        for (model.AppointmentModel a : AppointmentManagementController.getAll()) {
            if (!name.equalsIgnoreCase(a.getPatientName())) continue;
            if ("Rejected".equalsIgnoreCase(a.getStatus())) continue;
            java.time.LocalDate date;
            try {
                date = java.time.LocalDate.parse(a.getDate());
            } catch (Exception ex) {
                continue; // no usable date on this row — skip it
            }
            if (date.isBefore(today)) continue;
            if (soonest == null || date.isBefore(java.time.LocalDate.parse(soonest.getDate()))) {
                soonest = a;
            }
        }
        if (soonest == null) {
            return "You have no upcoming appointments — book one anytime from My Appointments!";
        }
        return "Your next appointment is " + soonest.getTreatmentType() + " with " + titledDentistName(soonest.getDentistName())
                + " on " + soonest.getDate() + " at " + soonest.getTime() + ".";
    }

    /** "Supem Fernando" -> "Dr Supem Fernando" — appointments store just the bare dentist name, title comes from their own record. */
    private String titledDentistName(String dentistName) {
        if (dentistName == null) return "";
        model.DentistModel dentist = DentistManagementController.getDirectory().get(dentistName);
        String title = (dentist != null && dentist.getTitle() != null && !dentist.getTitle().trim().isEmpty())
                ? dentist.getTitle().trim() : "Dr";
        return title + " " + dentistName;
    }

    /** A fresh My Profile wizard controller, seeded from this same logged-in user. */
    public PatientProfileController newProfileController() {
        return new PatientProfileController(currentUser);
    }

    /**
     * True only for a login Office Staff created with a system-generated
     * temporary password (Patient Management) that hasn't been changed yet —
     * Patient_Dashboard uses this to force the Edit Profile popup open on
     * this patient's very first visit. Always false for a self-registered
     * account, which picked its own password at signup.
     */
    public boolean mustChangePassword() {
        return USER_DAO.getMustChangePassword(getUsername());
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void openProfile()      { System.out.println("[CONTROLLER] Action triggered: Profile"); }
    public void openAppointments() { System.out.println("[CONTROLLER] Action triggered: Appointments"); }
    public void openBillings()     { System.out.println("[CONTROLLER] Action triggered: Billings"); }
    public void openReports()      { System.out.println("[CONTROLLER] Action triggered: Reports"); }
    public void openHelpDesk()     { System.out.println("[CONTROLLER] Action triggered: Help Desk"); }

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
