package controller;

import model.User;

/**
 * Controller for the Administration_Dashboard view.
 *
 * The dashboard's five bottom tiles (Dentists / Patients / Appointments /
 * Inventory / Billings) are read-only live analytics, not navigation — each
 * count is read straight from the same real DAO-backed source every other
 * dashboard's own badges use, so the number here always matches what you'd
 * see in the corresponding grid. Operations / Approvals / Reports are the
 * dashboard's actual navigation buttons.
 *
 * @author oveen
 */
public class AdministrationController {

    private final User currentUser;
    // Tracks a mid-session rename so getUsername()/subsequent saves target
    // the new row — currentUser itself stays untouched since User is
    // immutable and shared with AppController's session state.
    private String usernameOverride;

    public AdministrationController(User currentUser) {
        this.currentUser = currentUser;
    }

    // ── Dashboard data ────────────────────────────────────────────────────────

    public String getWelcomeMessage() {
        return "Hii Administration, Welcome!!";
    }

    public String getDisplayName() {
        return (currentUser != null && currentUser.getFullName() != null)
                ? currentUser.getFullName()
                : "Administration";
    }

    public String getUsername() {
        if (usernameOverride != null) {
            return usernameOverride;
        }
        return currentUser != null && currentUser.getUsername() != null ? currentUser.getUsername() : "";
    }

    // ── Live analytics — real counts, not stubbed numbers ───────────────────
    public int getTotalDentists()     { return DentistManagementController.count(); }
    public int getTotalPatients()     { return PatientManagementController.count(); }
    public int getTotalAppointments() { return AppointmentManagementController.countAll(); }
    public int getTotalInventory()    { return InventoryManagementController.count(); }
    public int getTotalBillings()     { return BillingManagementController.count(); }

    // ── Trend chart (dashboard's "28 Day Trend" widget) ─────────────────────
    // Two real series over the same trailing window: daily billed income
    // (from "billings".appointment_date/total_bill_amount) and daily
    // appointments booked ("appointments".appointment_date) as the stand-in
    // for "services" — both read through the same controllers every other
    // screen already uses, so the chart never shows a number that couldn't
    // also be found in Billings/Appointments.
    private static final int TREND_DAYS = 28;
    private static final java.time.format.DateTimeFormatter TREND_DATE_ISO =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final java.time.format.DateTimeFormatter TREND_DAY_LABEL =
            java.time.format.DateTimeFormatter.ofPattern("d MMM");

    public java.util.List<String> getTrendDayLabels() {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.List<String> labels = new java.util.ArrayList<>();
        for (int i = TREND_DAYS - 1; i >= 0; i--) {
            labels.add(today.minusDays(i).format(TREND_DAY_LABEL));
        }
        return labels;
    }

    /** Total billed amount per day over the trailing {@value #TREND_DAYS} days. */
    public double[] getTrendIncome() {
        java.time.LocalDate start = java.time.LocalDate.now().minusDays(TREND_DAYS - 1);
        double[] values = new double[TREND_DAYS];
        for (model.BillingModel b : BillingManagementController.getAll()) {
            int idx = dayIndex(b.getAppointmentDate(), start);
            if (idx >= 0) values[idx] += b.getTotalBillAmount();
        }
        return values;
    }

    /** Appointments booked per day over the trailing {@value #TREND_DAYS} days — this dashboard's "services" series. */
    public double[] getTrendAppointments() {
        java.time.LocalDate start = java.time.LocalDate.now().minusDays(TREND_DAYS - 1);
        double[] values = new double[TREND_DAYS];
        for (model.AppointmentModel a : AppointmentManagementController.getAll()) {
            int idx = dayIndex(a.getDate(), start);
            if (idx >= 0) values[idx] += 1;
        }
        return values;
    }

    /** "yyyy-MM-dd" -&gt; offset from {@code start} in days, or -1 if blank/unparseable/outside the window. */
    private static int dayIndex(String dateStr, java.time.LocalDate start) {
        if (dateStr == null || dateStr.trim().isEmpty()) return -1;
        try {
            java.time.LocalDate d = java.time.LocalDate.parse(dateStr.trim(), TREND_DATE_ISO);
            long offset = java.time.temporal.ChronoUnit.DAYS.between(start, d);
            return (offset >= 0 && offset < TREND_DAYS) ? (int) offset : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void openOperations() { System.out.println("[CONTROLLER] Action triggered: Operations"); }
    public void openApprovals()  { System.out.println("[CONTROLLER] Action triggered: Approvals"); }
    public void openReports()    { System.out.println("[CONTROLLER] Action triggered: Reports"); }

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
