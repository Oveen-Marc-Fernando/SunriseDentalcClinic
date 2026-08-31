package controller;

import model.DentistModel;
import javax.swing.JOptionPane;

/**
 * Controller for the Dentist Management registration wizard (OS_DM_1 – OS_DM_5).
 *
 * Responsibilities:
 *  - Owns the single shared {@link DentistModel} that travels across all 5 steps.
 *  - Collects data from each wizard step View.
 *  - Validates each step before allowing navigation forward.
 *  - Orchestrates opening the next / previous View.
 *  - Saves (persists) the completed dentist record on final submission.
 *
 * Each wizard View receives this controller in its constructor and calls:
 *   controller.goNextFromStep1(...)  – instead of directly creating the next view.
 *
 * @author oveen
 */
public class DentistManagementController {

    // ── Shared dentist directory ────────────────────────────────────────────
    // Backed by the real "dentists" table (db/schema.sql) via DentistDAO —
    // every dentist registered through this wizard (Step 5's Working Days /
    // Start Time / End Time / Room No) lands there, keyed by full name once
    // read back. This is what Appointment Management's Step 2 reads to build
    // its "available dentists" and "available dates & times" dropdowns,
    // instead of that screen inventing its own schedule data. Pulling fresh
    // from the DAO on every call (rather than caching) means any screen that
    // saves a dentist is immediately visible to every other screen — same
    // "shared source of truth" guarantee the old static Map gave, just
    // backed by MySQL instead of memory now.
    private static final dao.DentistDAO DENTIST_DAO = new dao.DentistDAO();

    // Login creation — a dentist added directly through this wizard needs a
    // "users" row too (see [[dentist-my-profile-doctor-id-link]] for why that
    // link matters), same as one who self-registers via Register.java. Kept
    // separate from DentistModel (which mirrors the "dentists" table only)
    // and only actually written to the DB once the whole wizard is submitted
    // (submitFromStep5), same "don't touch the DB until Step 5" convention
    // as the rest of this controller.
    private static final dao.UserDAO USER_DAO = new dao.UserDAO();

    /** Read-only view of every registered dentist's availability, keyed by full name. */
    public static java.util.Map<String, DentistModel> getDirectory() {
        return java.util.Collections.unmodifiableMap(DENTIST_DAO.findAll());
    }

    /** Live count of every dentist on record — same "count() straight from the DAO" convention as the other controllers. */
    public static int count() {
        return DENTIST_DAO.findAll().size();
    }

    /** A single dentist's full record by Dentist ID, or null if there's no match — DentistProfileController's linked-login lookup. */
    public static DentistModel findById(String dentistId) {
        if (dentistId == null || dentistId.trim().isEmpty()) {
            return null;
        }
        for (DentistModel d : DENTIST_DAO.findAll().values()) {
            if (dentistId.trim().equalsIgnoreCase(d.getDentistId())) {
                return d;
            }
        }
        return null;
    }

    /**
     * Registers/updates a dentist's record (insert if the Dentist ID is new,
     * update in place if it already exists). Used by
     * {@link DentistProfileController} so a dentist editing their own "My
     * Profile" wizard shows up (or gets updated) the same way a record saved
     * by Office Staff's Dentist Management wizard would — Appointment
     * Management's Step 2 reads from this same table.
     */
    public static boolean registerDentist(DentistModel model) {
        if (model == null || model.getFullName() == null || model.getFullName().trim().isEmpty()) {
            return false;
        }
        return DENTIST_DAO.upsert(model);
    }

    /**
     * Deletes a dentist by Dentist ID — OS_DM_Grid's Delete button. Returns
     * false if the delete was rejected (most commonly because the dentist
     * still has appointments on record; {@code appointments} has a
     * {@code RESTRICT} foreign key on dentist name), so the caller can leave
     * the row in place and explain why instead of removing it from the grid
     * only for it to reappear next time the grid is opened.
     */
    public static boolean deleteDentist(String dentistId) {
        return DENTIST_DAO.delete(dentistId);
    }

    /** Next auto-generated Dentist ID, e.g. "D104" — OS_DM_1's read-only Dentist ID field. */
    public static String nextDentistId() {
        return DENTIST_DAO.nextDentistId();
    }

    /** Outcome of {@link #createLoginForDentist}, distinguishing why it didn't succeed. */
    public enum LoginOutcome { SUCCESS, USERNAME_TAKEN, NOT_FOUND, FAILED }

    /**
     * Submits a login request for a dentist who was added straight through
     * this wizard and so never went through Register.java — e.g. backfilling
     * D101/D102 via OS_DM_Grid's "Create Login" action. Office Staff only
     * picks the username; the request lands PENDING in Administration's own
     * approval queue (AD_APR_UserLogins) — same review self-registered
     * dentists go through. Only once Administration approves it does the
     * system generate a temporary password and email it (see
     * {@link controller.UserApprovalController#approve}); nothing is emailed
     * from here.
     */
    public static LoginOutcome createLoginForDentist(String dentistId, String username) {
        if (username == null || username.trim().isEmpty()) {
            return LoginOutcome.FAILED;
        }
        DentistModel dentist = findById(dentistId);
        if (dentist == null) {
            return LoginOutcome.NOT_FOUND;
        }
        if (USER_DAO.getStatus(username.trim()) != null) {
            return LoginOutcome.USERNAME_TAKEN;
        }
        boolean ok = USER_DAO.insertPendingDentistLogin(username.trim(), dentist.getFullName(),
                dentist.getEmail(), dentist.getNic(), dentist.getMobileNo(), dentistId);
        return ok ? LoginOutcome.SUCCESS : LoginOutcome.FAILED;
    }

    // ── Shared model ─────────────────────────────────────────────────────────
    private final DentistModel dentistModel;

    // Step 1's chosen username — held here (not on DentistModel) until Step
    // 5's final submit actually writes it to "users", with a freshly
    // generated temporary password (see createPendingLogin()).
    private String pendingUsername;

    // ── Constructor ───────────────────────────────────────────────────────────
    public DentistManagementController() {
        this.dentistModel = new DentistModel();
    }

    /** Allow injecting an existing (pre-populated) model, e.g. for edit mode. */
    public DentistManagementController(DentistModel existingModel) {
        this.dentistModel = existingModel;
    }

    // ── Model accessor ────────────────────────────────────────────────────────
    public DentistModel getDentistModel() {
        return dentistModel;
    }

    /** Step 1's chosen username, so navigating Back then Next again doesn't lose it. */
    public String getPendingUsername() {
        return pendingUsername;
    }

    // =========================================================================
    // STEP 1 – Dentist Information
    // =========================================================================

    /**
     * Called by OS_DM_1 when the user clicks "Next".
     * Saves step-1 data into the model, validates, then opens OS_DM_2.
     *
     * @return true if navigation succeeded, false if validation failed
     */
    public boolean goNextFromStep1(
            String title,
            String gender,
            String fullName,
            String dentistId,
            String dob,
            String nic,
            String username,
            javax.swing.JFrame currentView) {

        // Validate required fields
        if (fullName == null || fullName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Full Name is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (dentistId == null || dentistId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Dentist ID is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // The login this dentist will use to reach the Dentist Dashboard —
        // Office Staff only picks the username; a temporary password gets
        // generated and emailed once the whole wizard is submitted (see
        // createPendingLogin()), same idea as an Administrator issuing a new
        // Office Staff login rather than that person self-registering.
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Username is required so this dentist can log in.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // Skip the taken-username check if we're just re-entering this same
        // step (Back then Next again) with a username already accepted —
        // otherwise a dentist would see their own pending username rejected.
        if (!username.trim().equalsIgnoreCase(pendingUsername) && USER_DAO.getStatus(username.trim()) != null) {
            JOptionPane.showMessageDialog(currentView,
                    "That username is already taken.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Save to model
        dentistModel.setTitle(title);
        dentistModel.setGender(gender);
        dentistModel.setFullName(fullName.trim());
        dentistModel.setDentistId(dentistId.trim());
        dentistModel.setDob(dob);
        dentistModel.setNic(nic);
        pendingUsername = username.trim();

        // Navigate forward
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_DM_2(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    // =========================================================================
    // STEP 2 – Professional Information
    // =========================================================================

    public boolean goNextFromStep2(
            String slmcNo,
            String qualification,
            String university,
            String graduationYear,
            String specialization,
            String experience,
            String licenseStatus,
            javax.swing.JFrame currentView) {

        if (slmcNo == null || slmcNo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "SLMC Registration No is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        dentistModel.setSlmcNo(slmcNo.trim());
        dentistModel.setQualification(qualification);
        dentistModel.setUniversity(university);
        dentistModel.setGraduationYear(graduationYear);
        dentistModel.setSpecialization(specialization);
        dentistModel.setExperience(experience);
        dentistModel.setLicenseStatus(licenseStatus);

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_DM_3(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    public void goBackFromStep2(javax.swing.JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_DM_1(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 3 – Contact Information
    // =========================================================================

    public boolean goNextFromStep3(
            String mobileNo,
            String email,
            String address,
            String emergencyNo,
            javax.swing.JFrame currentView) {

        if (mobileNo == null || mobileNo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Mobile No is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Email is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        dentistModel.setMobileNo(mobileNo.trim());
        dentistModel.setEmail(email.trim());
        dentistModel.setAddress(address);
        dentistModel.setEmergencyNo(emergencyNo);

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_DM_4(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    public void goBackFromStep3(javax.swing.JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_DM_2(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 4 – Employment Information
    // =========================================================================

    public boolean goNextFromStep4(
            String joinedDate,
            String employmentType,
            String consultationFee,
            String employmentStatus,
            javax.swing.JFrame currentView) {

        if (consultationFee == null || consultationFee.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Consultation Fee is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        dentistModel.setJoinedDate(joinedDate);
        dentistModel.setEmploymentType(employmentType);
        dentistModel.setConsultationFee(consultationFee.trim());
        dentistModel.setEmploymentStatus(employmentStatus);

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_DM_5(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    public void goBackFromStep4(javax.swing.JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_DM_3(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 5 – Availability (Final Submit)
    // =========================================================================

    public boolean submitFromStep5(
            String workingDays,
            String startTime,
            String endTime,
            String breakTime,
            String roomNo,
            javax.swing.JFrame currentView) {

        if (workingDays == null || workingDays.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Working Days is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        dentistModel.setWorkingDays(workingDays.trim());
        dentistModel.setStartTime(startTime);
        dentistModel.setEndTime(endTime);
        dentistModel.setBreakTime(breakTime);
        dentistModel.setRoomNo(roomNo);

        // Persist / save the dentist record
        if (!saveDentist()) {
            view.IconFactory.showErrorDialog(currentView,
                    "Couldn't save this dentist — check the console for details and try again.", null);
            return false;
        }

        boolean loginCreated = createPendingLogin();
        String message = "Dentist \"" + dentistModel.getFullName() + "\" registered successfully!"
                + (loginCreated
                        ? "\n\nLogin request submitted (username \"" + pendingUsername + "\") — pending "
                                + "Administrator approval. A temporary password will be generated and emailed to "
                                + dentistModel.getEmail() + " once it's approved."
                        : "\n\n(Login wasn't created — check the console for details; use \"Create Login\" from this grid to add one.)");
        view.IconFactory.showSuccessDialog(currentView, message, () -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                new view.OS_DM_Grid().setVisible(true);
            });
            currentView.dispose();
        });
        return true;
    }

    public void goBackFromStep5(javax.swing.JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_DM_4(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // Persistence (save to DB / storage)
    // =========================================================================

    /** Saves the completed {@link DentistModel} to the {@code dentists} table. */
    private boolean saveDentist() {
        System.out.println("[CONTROLLER] Saving new dentist record: " + dentistModel.getFullName()
                + " (" + dentistModel.getDentistId() + ")");
        return DENTIST_DAO.upsert(dentistModel);
    }

    /**
     * Writes Step 1's chosen username into "users" as a PENDING dentist login
     * request, linked to the just-saved Dentist ID, once the dentist record
     * itself is safely on disk. Re-checks the username is still free first:
     * it was already validated back on Step 1, but the wizard can sit open
     * for a while before Step 5, so someone else could have taken it in the
     * meantime. Nothing is emailed yet — this request now waits in
     * Administration's approval queue (AD_APR_UserLogins); only once it's
     * approved does the system generate and email a temporary password (see
     * {@link controller.UserApprovalController#approve}).
     */
    private boolean createPendingLogin() {
        if (pendingUsername == null || pendingUsername.isEmpty()) {
            return false;
        }
        if (USER_DAO.getStatus(pendingUsername) != null) {
            System.err.println("[CONTROLLER] Username \"" + pendingUsername
                    + "\" was taken between Step 1 and Step 5 — dentist saved without a login.");
            return false;
        }
        return USER_DAO.insertPendingDentistLogin(pendingUsername, dentistModel.getFullName(),
                dentistModel.getEmail(), dentistModel.getNic(), dentistModel.getMobileNo(), dentistModel.getDentistId());
    }
}
