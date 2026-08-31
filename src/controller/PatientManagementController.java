package controller;

import model.PatientModel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

/**
 * Controller for the Patient Management registration wizard (OS_PM_1 – OS_PM_4).
 *
 * Responsibilities:
 *  - Owns the single shared {@link PatientModel} across all 4 steps.
 *  - Collects data from each wizard step View.
 *  - Validates each step before allowing navigation forward.
 *  - Orchestrates opening the next / previous View.
 *  - Saves (persists) the completed patient record on final submission.
 *
 * @author oveen
 */
public class PatientManagementController {

    // ── Shared patient directory reads ──────────────────────────────────────
    // Backed by the real "patients" table (db/schema.sql) via PatientDAO —
    // OS_PM_Grid, D_PA_Grid, and OS_AM_1's patient dropdown all read through
    // these instead of touching the DAO directly.
    private static final dao.PatientDAO PATIENT_DAO = new dao.PatientDAO();
    // Login creation — see createLoginForPatient()'s javadoc; mirrors
    // DentistManagementController's own USER_DAO field/reasoning exactly.
    private static final dao.UserDAO USER_DAO = new dao.UserDAO();

    public static java.util.List<PatientModel> getAll() {
        return PATIENT_DAO.findAll();
    }

    public static int count() {
        return PATIENT_DAO.count();
    }

    /** Deletes a patient by Patient ID — OS_PM_Grid's Delete button. */
    public static boolean delete(String patientId) {
        return PATIENT_DAO.delete(patientId);
    }

    /** Overwrites an existing patient's full record — OS_PM_Grid's Edit popup (an upsert keyed on Patient ID, same as the wizard's own final save). */
    public static boolean updatePatient(PatientModel model) {
        if (model == null || model.getPatientId() == null || model.getPatientId().trim().isEmpty()) {
            return false;
        }
        return PATIENT_DAO.upsert(model);
    }

    /** Best-effort email lookup by patient name — Billing's "Email" button uses this to find who to send the bill to. */
    public static String findEmailByFullName(String fullName) {
        return PATIENT_DAO.findEmailByFullName(fullName);
    }

    /** Reliable email lookup by Patient ID — preferred over {@link #findEmailByFullName} whenever a billing row has one. */
    public static String findEmailById(String patientId) {
        return PATIENT_DAO.findEmailById(patientId);
    }

    /** Best-effort Patient ID lookup by name — Billing Step 1 uses this to link a bill to a real patient record. */
    public static String findIdByFullName(String fullName) {
        return PATIENT_DAO.findIdByFullName(fullName);
    }

    /** Full patient record by exact name match — D_PA_Grid uses this to look up each of a dentist's own patients. */
    public static model.PatientModel findByFullName(String fullName) {
        return PATIENT_DAO.findByFullName(fullName);
    }

    /** Next auto-generated Patient ID, e.g. "P104" — OS_PM_1's read-only Patient ID field. */
    public static String nextPatientId() {
        return PATIENT_DAO.nextPatientId();
    }

    /** A single patient's full record by Patient ID, or null — OS_PM_Grid's "Create Login"/Edit lookup. */
    public static PatientModel findById(String patientId) {
        return PATIENT_DAO.findById(patientId);
    }

    /** Outcome of {@link #createLoginForPatient}, distinguishing why it didn't succeed. */
    public enum LoginOutcome { SUCCESS, USERNAME_TAKEN, NOT_FOUND, FAILED }

    /**
     * Creates a login for a patient who was added straight through this
     * wizard and so never went through Register.java — e.g. backfilling an
     * existing patient via OS_PM_Grid's "Create Login" action. Office Staff
     * only picks the username; a random temporary password is generated
     * here and emailed to the patient (never shown on screen), and the
     * account is flagged so the Edit Profile popup forces itself open on
     * their first dashboard visit until they pick their own password.
     * Auto-approved (no admin review): Office Staff already vetted this
     * person by adding their record here directly — same trust level as
     * self-registration's own auto-approve for Patient accounts.
     */
    public static LoginOutcome createLoginForPatient(String patientId, String username) {
        if (username == null || username.trim().isEmpty()) {
            return LoginOutcome.FAILED;
        }
        PatientModel patient = findById(patientId);
        if (patient == null) {
            return LoginOutcome.NOT_FOUND;
        }
        if (USER_DAO.getStatus(username.trim()) != null) {
            return LoginOutcome.USERNAME_TAKEN;
        }
        String tempPassword = generateTempPassword();
        boolean ok = USER_DAO.insertApprovedWithTempPassword(username.trim(), tempPassword, patient.getFullName(),
                patient.getEmail(), patient.getNic(), patient.getMobileNo(), model.User.Role.PATIENT);
        if (!ok) {
            return LoginOutcome.FAILED;
        }
        USER_DAO.linkPatientId(username.trim(), patientId);
        sendTemporaryPasswordEmailAsync(patient.getEmail(), patient.getFullName(), username.trim(), tempPassword);
        return LoginOutcome.SUCCESS;
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
     * Fire-and-forget, same convention as RegisterController's own
     * confirmation emails — the login itself already succeeded in the
     * database, so a slow/unreachable SMTP server must never block or fail
     * the save. Silently skipped if there's no email on file or mail isn't
     * configured.
     */
    private static void sendTemporaryPasswordEmailAsync(String email, String fullName, String username, String tempPassword) {
        if (email == null || email.trim().isEmpty() || !mail.MailSender.isConfigured()) {
            return;
        }
        String trimmedEmail = email.trim();
        new Thread(() -> {
            try {
                mail.MailSender.sendTemporaryPasswordEmail(trimmedEmail, fullName, username, tempPassword);
            } catch (Exception e) {
                System.err.println("[CONTROLLER] Temporary password email failed: " + e.getMessage());
            }
        }, "temp-password-email").start();
    }

    private final PatientModel patientModel;

    // Step 1's chosen username — held here (not on PatientModel) until Step
    // 4's final submit actually writes it to "users", with a freshly
    // generated temporary password (see createPendingLogin()).
    private String pendingUsername;

    public PatientManagementController() {
        this.patientModel = new PatientModel();
    }

    public PatientManagementController(PatientModel existingModel) {
        this.patientModel = existingModel;
    }

    public PatientModel getPatientModel() {
        return patientModel;
    }

    /** Step 1's chosen username, so navigating Back then Next again doesn't lose it. */
    public String getPendingUsername() {
        return pendingUsername;
    }

    // =========================================================================
    // STEP 1 – Personal Information
    // =========================================================================

    public boolean goNextFromStep1(
            String title,
            String gender,
            String fullName,
            String patientId,
            String dob,
            String age,
            String nic,
            String username,
            JFrame currentView) {

        if (fullName == null || fullName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Full Name is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // The login this patient will use to reach the Patient Dashboard —
        // Office Staff only picks the username; a temporary password gets
        // generated and emailed once the whole wizard is submitted (see
        // createPendingLogin()), same idea as an Administrator issuing a new
        // Office Staff login rather than that person self-registering.
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Username is required so this patient can log in.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // Skip the taken-username check if we're just re-entering this same
        // step (Back then Next again) with a username already accepted.
        if (!username.trim().equalsIgnoreCase(pendingUsername) && USER_DAO.getStatus(username.trim()) != null) {
            JOptionPane.showMessageDialog(currentView,
                    "That username is already taken.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        patientModel.setTitle(title);
        patientModel.setGender(gender);
        patientModel.setFullName(fullName.trim());
        patientModel.setPatientId(patientId != null ? patientId.trim() : "");
        patientModel.setDob(dob != null ? dob.trim() : ""); // DATE column — blank stays blank
        patientModel.setAge(age != null ? age.trim() : "");  // numeric-ish — same reasoning
        patientModel.setNic(nilIfBlank(nic));
        pendingUsername = username.trim();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_PM_2(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    // =========================================================================
    // STEP 2 – Contact Information
    // =========================================================================

    public boolean goNextFromStep2(
            String addressLine1,
            String addressLine2,
            String city,
            String mobileNo,
            String landlineNo,
            String email,
            JFrame currentView) {

        if (mobileNo == null || mobileNo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Mobile No is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        patientModel.setAddressLine1(nilIfBlank(addressLine1));
        patientModel.setAddressLine2(addressLine2 != null ? addressLine2.trim() : ""); // a genuinely optional 2nd line — blank is normal, not "left ignored"
        patientModel.setCity(nilIfBlank(city));
        patientModel.setMobileNo(mobileNo.trim());
        patientModel.setLandlineNo(landlineNo != null ? landlineNo.trim() : ""); // optional alt. contact — blank is normal
        patientModel.setEmail(email != null ? email.trim() : "");

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_PM_3(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    public void goBackFromStep2(JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_PM_1(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 3 – Medical Information
    // =========================================================================

    public boolean goNextFromStep3(
            String bloodGroup,
            String allergies,
            String medicalConditions,
            String currentMedications,
            String previousSurgeries,
            JFrame currentView) {

        patientModel.setBloodGroup(bloodGroup != null ? bloodGroup.trim() : ""); // dropdown, has its own "Select" placeholder
        patientModel.setAllergies(nilIfBlank(allergies));
        patientModel.setMedicalConditions(nilIfBlank(medicalConditions));
        patientModel.setCurrentMedications(nilIfBlank(currentMedications));
        patientModel.setPreviousSurgeries(nilIfBlank(previousSurgeries));

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_PM_4(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    public void goBackFromStep3(JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_PM_2(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 4 – Dental Information (Final Submit)
    // =========================================================================

    public boolean submitFromStep4(
            String lastDentalVisit,
            String dentalHistory,
            String dentalProblems,
            String oralHygiene,
            String dentalMedicalNotes,
            JFrame currentView) {

        patientModel.setLastDentalVisit(lastDentalVisit != null ? lastDentalVisit.trim() : ""); // DATE column
        patientModel.setDentalHistory(nilIfBlank(dentalHistory));
        patientModel.setDentalProblems(nilIfBlank(dentalProblems));
        patientModel.setOralHygiene(oralHygiene);
        patientModel.setDentalMedicalNotes(nilIfBlank(dentalMedicalNotes));

        if (!savePatient()) {
            view.IconFactory.showErrorDialog(currentView,
                    "Couldn't save this patient — check the console for details and try again.", null);
            return false;
        }

        boolean loginCreated = createPendingLogin();
        String message = "Patient \"" + patientModel.getFullName() + "\" registered successfully!"
                + (loginCreated
                        ? "\n\nLogin created — a temporary password was emailed to " + patientModel.getEmail()
                                + " (username \"" + pendingUsername + "\"). They'll be asked to set their own "
                                + "password the first time they log in."
                        : "\n\n(Login wasn't created — check the console for details; use \"Create Login\" from this grid to add one.)");
        view.IconFactory.showSuccessDialog(currentView, message, () -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                new view.OS_PM_Grid().setVisible(true);
            });
            currentView.dispose();
        });
        return true;
    }

    public void goBackFromStep4(JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_PM_3(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // Persistence (stub – replace with DAO/DB logic when ready)
    // =========================================================================

    private boolean savePatient() {
        System.out.println("[CONTROLLER] Saving patient record: " + patientModel.getFullName()
                + " (" + patientModel.getPatientId() + ")");
        return PATIENT_DAO.upsert(patientModel);
    }

    /**
     * Writes Step 1's chosen username into "users" with a freshly generated
     * temporary password (emailed to the patient, never shown on screen —
     * see {@link #createLoginForPatient}'s javadoc for the full reasoning),
     * and links it to the just-saved Patient ID, once the patient record
     * itself is safely on disk. Re-checks the username is still free first:
     * it was already validated back on Step 1, but the wizard can sit open
     * for a while before Step 4, so someone else could have taken it in the
     * meantime.
     */
    private boolean createPendingLogin() {
        if (pendingUsername == null || pendingUsername.isEmpty()) {
            return false;
        }
        if (USER_DAO.getStatus(pendingUsername) != null) {
            System.err.println("[CONTROLLER] Username \"" + pendingUsername
                    + "\" was taken between Step 1 and Step 4 — patient saved without a login.");
            return false;
        }
        String tempPassword = generateTempPassword();
        boolean ok = USER_DAO.insertApprovedWithTempPassword(pendingUsername, tempPassword, patientModel.getFullName(),
                patientModel.getEmail(), patientModel.getNic(), patientModel.getMobileNo(), model.User.Role.PATIENT);
        if (ok) {
            USER_DAO.linkPatientId(pendingUsername, patientModel.getPatientId());
            sendTemporaryPasswordEmailAsync(patientModel.getEmail(), patientModel.getFullName(), pendingUsername, tempPassword);
        }
        return ok;
    }

    /**
     * A left-blank optional text field is saved as the literal word "Nil"
     * instead of an empty string — same convention as PatientProfileController
     * (Patient's own self-service wizard), applied here too so Office Staff's
     * admin wizard behaves identically: the next person to open this record
     * sees an honest "nothing applies here" instead of a blank box that
     * looks unfinished. Not applied to dates/numbers/dropdowns/choice groups,
     * where the word "Nil" wouldn't make sense or would break parsing.
     */
    private static String nilIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? "Nil" : s.trim();
    }
}
