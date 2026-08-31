package controller;

import model.DentistModel;
import model.User;

/**
 * Controller for the Dentist "My Profile" wizard (D_Profile_1 – D_Profile_5),
 * opened from the Dentist Dashboard's "My Profile" tile.
 *
 * Mirrors {@link DentistManagementController} step-for-step (same 5-step
 * shape, same {@link DentistModel}), but is scoped to the signed-in dentist
 * editing their own record rather than Office Staff registering someone
 * else's:
 *  - Pre-fills from the dentist's existing entry in
 *    {@link DentistManagementController#getDirectory()} when one exists
 *    (matched by full name), so the dentist sees their current info instead
 *    of a blank form.
 *  - Step 1's "Back" returns to the Dentist Dashboard instead of a grid.
 *  - Final "Submit" saves back into the shared directory via
 *    {@link DentistManagementController#registerDentist(DentistModel)} so
 *    Appointment Management keeps seeing up-to-date availability.
 *
 * @author oveen
 */
public class DentistProfileController {

    private static final dao.UserDAO USER_DAO = new dao.UserDAO();

    private final User        currentUser;
    private final DentistModel dentistModel;

    public DentistProfileController(User currentUser) {
        this.currentUser  = currentUser;
        this.dentistModel = resolveExistingModel(currentUser);
    }

    /** Allow injecting an existing (in-progress) model as the wizard steps forward/back. */
    public DentistProfileController(User currentUser, DentistModel existingModel) {
        this.currentUser  = currentUser;
        this.dentistModel = existingModel;
    }

    private static DentistModel resolveExistingModel(User user) {
        String username = user != null ? user.getUsername() : null;

        // Prefer the durable link (set by a previous save's persist() below)
        // — immune to the dentist later editing their own Full Name here,
        // unlike a name-match lookup would be (that used to silently orphan
        // this login from its own "dentists" row the moment the name drifted).
        if (username != null) {
            String linkedId = USER_DAO.getDentistId(username);
            if (linkedId != null && !linkedId.trim().isEmpty()) {
                DentistModel linked = DentistManagementController.findById(linkedId);
                if (linked != null) {
                    return linked;
                }
            }
        }
        // One-time fallback for accounts that predate this link (or whose
        // link is somehow stale) — best-effort by name, same as before.
        if (user != null && user.getFullName() != null) {
            DentistModel existing = DentistManagementController.getDirectory().get(user.getFullName().trim());
            if (existing != null) {
                return existing;
            }
        }
        DentistModel fresh = new DentistModel();
        if (user != null) {
            fresh.setFullName(user.getFullName());
        }
        // Auto-generated like Patient ID (PatientProfileController's own
        // nextPatientId() fallback) — Dentist ID is locked/read-only on
        // D_Profile_1, so this is the value that actually gets saved, not
        // just a suggestion.
        fresh.setDentistId(DentistManagementController.nextDentistId());
        // Pre-fill from what was already collected at signup (Register.java)
        // — same convention as PatientProfileController — one less thing to
        // retype across My Profile's Contact Information step.
        if (username != null) {
            setIfPresent(USER_DAO.getEmail(username), fresh::setEmail);
            setIfPresent(USER_DAO.getNic(username), fresh::setNic);
            setIfPresent(USER_DAO.getContactNumber(username), fresh::setMobileNo);
        }
        return fresh;
    }

    private static void setIfPresent(String value, java.util.function.Consumer<String> setter) {
        if (value != null && !value.trim().isEmpty()) {
            setter.accept(value.trim());
        }
    }

    public DentistModel getDentistModel() { return dentistModel; }
    public User          getCurrentUser() { return currentUser; }

    // =========================================================================
    // STEP 1 – My Information
    // =========================================================================

    /** Validates and copies Step 1's fields into the shared model. Shared by "Next" and "Update". */
    private boolean applyStep1(
            String title, String gender, String fullName, String dentistId,
            String dob, String nic, javax.swing.JFrame currentView) {

        if (fullName == null || fullName.trim().isEmpty()) {
            view.IconFactory.showErrorDialog(currentView, "Full Name is required.", null);
            return false;
        }
        if (dentistId == null || dentistId.trim().isEmpty()) {
            view.IconFactory.showErrorDialog(currentView, "Dentist ID is required.", null);
            return false;
        }

        dentistModel.setTitle(title);
        dentistModel.setGender(gender);
        dentistModel.setFullName(fullName.trim());
        dentistModel.setDentistId(dentistId.trim());
        dentistModel.setDob(dob);
        dentistModel.setNic(nic);
        return true;
    }

    public boolean goNextFromStep1(
            String title,
            String gender,
            String fullName,
            String dentistId,
            String dob,
            String nic,
            javax.swing.JFrame currentView) {

        if (!applyStep1(title, gender, fullName, dentistId, dob, nic, currentView)) {
            return false;
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.D_Profile_2(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    /**
     * Step 1's own "Update" button — validates and persists just this page
     * immediately, without walking the rest of the wizard. Stays on the
     * current view (no navigation, no dispose) so the dentist can keep
     * editing other steps afterward, or just close.
     */
    public boolean updateFromStep1(
            String title,
            String gender,
            String fullName,
            String dentistId,
            String dob,
            String nic,
            javax.swing.JFrame currentView) {

        if (!applyStep1(title, gender, fullName, dentistId, dob, nic, currentView)) {
            return false;
        }
        if (!saveProfile()) {
            view.IconFactory.showErrorDialog(currentView,
                    "Couldn't save this page — check the console for details and try again.", null);
            return false;
        }
        view.IconFactory.showSuccessDialog(currentView, "Profile updated successfully!", null);
        return true;
    }

    /** Step 1's "Back" leaves the wizard entirely and returns to the Dentist Dashboard. */
    public void goBackToDashboard(javax.swing.JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.Dentist_Dashboard(currentUser).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 2 – My Professional Information
    // =========================================================================

    /** Validates and copies Step 2's fields into the shared model. Shared by "Next" and "Update". */
    private boolean applyStep2(
            String slmcNo, String qualification, String university, String graduationYear,
            String specialization, String experience, String licenseStatus, javax.swing.JFrame currentView) {

        if (slmcNo == null || slmcNo.trim().isEmpty()) {
            view.IconFactory.showErrorDialog(currentView, "SLMC Registration No is required.", null);
            return false;
        }

        dentistModel.setSlmcNo(slmcNo.trim());
        dentistModel.setQualification(qualification);
        dentistModel.setUniversity(university);
        dentistModel.setGraduationYear(graduationYear);
        dentistModel.setSpecialization(specialization);
        dentistModel.setExperience(experience);
        dentistModel.setLicenseStatus(licenseStatus);
        return true;
    }

    public boolean goNextFromStep2(
            String slmcNo,
            String qualification,
            String university,
            String graduationYear,
            String specialization,
            String experience,
            String licenseStatus,
            javax.swing.JFrame currentView) {

        if (!applyStep2(slmcNo, qualification, university, graduationYear, specialization, experience, licenseStatus, currentView)) {
            return false;
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.D_Profile_3(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    /** Step 2's own "Update" button — validates and persists just this page immediately. */
    public boolean updateFromStep2(
            String slmcNo,
            String qualification,
            String university,
            String graduationYear,
            String specialization,
            String experience,
            String licenseStatus,
            javax.swing.JFrame currentView) {

        if (!applyStep2(slmcNo, qualification, university, graduationYear, specialization, experience, licenseStatus, currentView)) {
            return false;
        }
        if (!saveProfile()) {
            view.IconFactory.showErrorDialog(currentView,
                    "Couldn't save this page — check the console for details and try again.", null);
            return false;
        }
        view.IconFactory.showSuccessDialog(currentView, "Profile updated successfully!", null);
        return true;
    }

    public void goBackFromStep2(javax.swing.JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.D_Profile_1(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 3 – My Contact Information
    // =========================================================================

    /** Validates and copies Step 3's fields into the shared model. Shared by "Next" and "Update". */
    private boolean applyStep3(
            String mobileNo, String email, String address, String emergencyNo, javax.swing.JFrame currentView) {

        if (mobileNo == null || mobileNo.trim().isEmpty()) {
            view.IconFactory.showErrorDialog(currentView, "Mobile No is required.", null);
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            view.IconFactory.showErrorDialog(currentView, "Email is required.", null);
            return false;
        }

        dentistModel.setMobileNo(mobileNo.trim());
        dentistModel.setEmail(email.trim());
        dentistModel.setAddress(address);
        dentistModel.setEmergencyNo(emergencyNo);
        return true;
    }

    public boolean goNextFromStep3(
            String mobileNo,
            String email,
            String address,
            String emergencyNo,
            javax.swing.JFrame currentView) {

        if (!applyStep3(mobileNo, email, address, emergencyNo, currentView)) {
            return false;
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.D_Profile_4(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    /** Step 3's own "Update" button — validates and persists just this page immediately. */
    public boolean updateFromStep3(
            String mobileNo,
            String email,
            String address,
            String emergencyNo,
            javax.swing.JFrame currentView) {

        if (!applyStep3(mobileNo, email, address, emergencyNo, currentView)) {
            return false;
        }
        if (!saveProfile()) {
            view.IconFactory.showErrorDialog(currentView,
                    "Couldn't save this page — check the console for details and try again.", null);
            return false;
        }
        view.IconFactory.showSuccessDialog(currentView, "Profile updated successfully!", null);
        return true;
    }

    public void goBackFromStep3(javax.swing.JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.D_Profile_2(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 4 – My Employment Information
    // =========================================================================

    /** Validates and copies Step 4's fields into the shared model. Shared by "Next" and "Update". */
    private boolean applyStep4(
            String joinedDate, String employmentType, String consultationFee,
            String employmentStatus, javax.swing.JFrame currentView) {

        if (consultationFee == null || consultationFee.trim().isEmpty()) {
            view.IconFactory.showErrorDialog(currentView, "Consultation Fee is required.", null);
            return false;
        }

        dentistModel.setJoinedDate(joinedDate);
        dentistModel.setEmploymentType(employmentType);
        dentistModel.setConsultationFee(consultationFee.trim());
        dentistModel.setEmploymentStatus(employmentStatus);
        return true;
    }

    public boolean goNextFromStep4(
            String joinedDate,
            String employmentType,
            String consultationFee,
            String employmentStatus,
            javax.swing.JFrame currentView) {

        if (!applyStep4(joinedDate, employmentType, consultationFee, employmentStatus, currentView)) {
            return false;
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.D_Profile_5(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    /** Step 4's own "Update" button — validates and persists just this page immediately. */
    public boolean updateFromStep4(
            String joinedDate,
            String employmentType,
            String consultationFee,
            String employmentStatus,
            javax.swing.JFrame currentView) {

        if (!applyStep4(joinedDate, employmentType, consultationFee, employmentStatus, currentView)) {
            return false;
        }
        if (!saveProfile()) {
            view.IconFactory.showErrorDialog(currentView,
                    "Couldn't save this page — check the console for details and try again.", null);
            return false;
        }
        view.IconFactory.showSuccessDialog(currentView, "Profile updated successfully!", null);
        return true;
    }

    public void goBackFromStep4(javax.swing.JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.D_Profile_3(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 5 – My Availability (Final Submit)
    // =========================================================================

    public boolean submitFromStep5(
            String workingDays,
            String startTime,
            String endTime,
            String breakTime,
            String roomNo,
            javax.swing.JFrame currentView) {

        if (workingDays == null || workingDays.trim().isEmpty()) {
            view.IconFactory.showErrorDialog(currentView, "Working Days is required.", null);
            return false;
        }

        dentistModel.setWorkingDays(workingDays.trim());
        dentistModel.setStartTime(startTime);
        dentistModel.setEndTime(endTime);
        dentistModel.setBreakTime(breakTime);
        dentistModel.setRoomNo(roomNo);

        if (!saveProfile()) {
            view.IconFactory.showErrorDialog(currentView,
                    "Couldn't save this profile — check the console for details and try again.", null);
            return false;
        }

        view.IconFactory.showSuccessDialog(currentView, "Profile saved successfully!", () -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                new view.Dentist_Dashboard(currentUser).setVisible(true);
            });
            currentView.dispose();
        });
        return true;
    }

    public void goBackFromStep5(javax.swing.JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.D_Profile_4(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // Persistence
    // =========================================================================

    /**
     * Saves the dentist's own profile — persists via
     * {@link DentistManagementController#registerDentist(DentistModel)},
     * which upserts into the real "dentists" table (see {@code dao.DentistDAO}),
     * so Appointment Management immediately sees the updated availability.
     * Called both by Step 5's final Submit and by each earlier step's own
     * "Update" button (see updateFromStep1..4), so any single page can be
     * saved without walking the rest of the wizard.
     */
    private boolean saveProfile() {
        System.out.println("[CONTROLLER] Saving dentist profile:");
        System.out.println("  Full Name      : " + dentistModel.getFullName());
        System.out.println("  Dentist ID      : " + dentistModel.getDentistId());
        System.out.println("  SLMC No        : " + dentistModel.getSlmcNo());
        System.out.println("  Specialization : " + dentistModel.getSpecialization());
        System.out.println("  Mobile         : " + dentistModel.getMobileNo());
        System.out.println("  Email          : " + dentistModel.getEmail());
        System.out.println("  Employment     : " + dentistModel.getEmploymentType());
        System.out.println("  Fee            : " + dentistModel.getConsultationFee());
        System.out.println("  Working Days   : " + dentistModel.getWorkingDays());
        System.out.println("  Room No        : " + dentistModel.getRoomNo());

        boolean ok = DentistManagementController.registerDentist(dentistModel);
        if (ok && currentUser != null && currentUser.getUsername() != null) {
            // (Re)confirm the durable link every save — covers first-ever
            // save (where resolveExistingModel had nothing to link to yet)
            // and keeps it correct even if Dentist ID were ever to change.
            USER_DAO.linkDentistId(currentUser.getUsername(), dentistModel.getDentistId());
            // Keep the login's own display name in sync too — "dentists"
            // just cascaded to whatever Full Name was typed on Step 1
            // (appointments/leave_requests follow automatically via their
            // own ON UPDATE CASCADE FKs), but "users.full_name" has no such
            // FK and would otherwise be left behind, silently breaking every
            // "my own data" screen that filters by getFullName() — dashboard
            // badges, My Appointments, My Patients, Request Leaves.
            if (dentistModel.getFullName() != null && !dentistModel.getFullName().trim().isEmpty()) {
                USER_DAO.updateFullName(currentUser.getUsername(), dentistModel.getFullName());
            }
        }
        return ok;
    }
}
