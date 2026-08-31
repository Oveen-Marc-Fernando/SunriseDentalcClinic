package controller;

import model.PatientModel;
import model.User;

/**
 * Controller for the Patient's own "My Profile" wizard (PA_PR_1 – PA_PR_4)
 * — the self-service counterpart to {@link PatientManagementController}
 * (which is Office Staff's registration/edit wizard over every patient).
 *
 * This app has no real login-to-patient-record foreign key (same gap noted
 * elsewhere — Billing's patient email lookup, Dentist login vs. profile),
 * so on construction this resolves "which patient record belongs to the
 * logged-in user" by a best-effort exact name match, same convention as
 * {@code PatientDAO.findEmailByFullName}. If nothing matches (e.g. a
 * self-registered account that never got a matching {@code patients} row),
 * a fresh record is started instead of failing — the first Update anywhere
 * in the wizard then creates it.
 *
 * Unlike the registration wizard's "accumulate across 4 screens, persist
 * once at the very end" flow, every step here persists immediately (each
 * screen has its own "Update" button) since the record already exists and
 * each section is independently editable at any time.
 *
 * @author oveen
 */
public class PatientProfileController {

    private static final dao.PatientDAO PATIENT_DAO = new dao.PatientDAO();
    private static final dao.UserDAO USER_DAO = new dao.UserDAO();

    private final PatientModel patientModel;
    private final String username; // null if there's no logged-in user (e.g. main())

    public PatientProfileController(User currentUser) {
        this.username = currentUser != null ? currentUser.getUsername() : null;
        String fullName = currentUser != null ? currentUser.getFullName() : null;

        // Prefer the durable link (set by a previous save's persist() below)
        // — immune to the patient later editing their own Full Name here,
        // unlike a name-match lookup would be.
        PatientModel existing = null;
        if (username != null) {
            String linkedId = USER_DAO.getPatientId(username);
            if (linkedId != null && !linkedId.trim().isEmpty()) {
                existing = PATIENT_DAO.findById(linkedId);
            }
        }
        // One-time fallback for accounts that predate this link (or whose
        // link is somehow stale) — best-effort by name, same as before.
        if (existing == null && fullName != null) {
            existing = PATIENT_DAO.findByFullName(fullName);
        }
        if (existing == null) {
            existing = new PatientModel();
            existing.setFullName(fullName != null ? fullName : "");
            existing.setPatientId(PATIENT_DAO.nextPatientId());
            // Pre-fill from what was already collected at signup
            // (Register.java) — one less thing to retype across My
            // Profile's Personal/Contact Information steps.
            if (username != null) {
                setIfPresent(USER_DAO.getEmail(username), existing::setEmail);
                setIfPresent(USER_DAO.getNic(username), existing::setNic);
                setIfPresent(USER_DAO.getContactNumber(username), existing::setMobileNo);
            }
        }
        this.patientModel = existing;
    }

    private static void setIfPresent(String value, java.util.function.Consumer<String> setter) {
        if (value != null && !value.trim().isEmpty()) {
            setter.accept(value.trim());
        }
    }

    public PatientModel getPatientModel() {
        return patientModel;
    }

    /** This login's own username, for PA_PR_1's locked reference display (null if there's no logged-in user). */
    public String getUsername() {
        return username;
    }

    // =========================================================================
    // Step 1 – Personal Information
    // =========================================================================

    public boolean saveStep1(String title, String gender, String fullName, String dob, String age, String nic) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        patientModel.setTitle(title);
        patientModel.setGender(gender);
        patientModel.setFullName(fullName.trim());
        patientModel.setDob(dob != null ? dob.trim() : ""); // a DATE column — blank stays blank, "Nil" wouldn't parse
        patientModel.setAge(age != null ? age.trim() : "");  // numeric-ish — same reasoning as DOB
        patientModel.setNic(nilIfBlank(nic));
        return persist();
    }

    // =========================================================================
    // Step 2 – Contact Information
    // =========================================================================

    /** Same 6 fields as Office Staff's own Step 2 (PatientManagementController#goNextFromStep2) — kept in sync so both wizards collect identical Contact Information. */
    public boolean saveStep2(String addressLine1, String addressLine2, String city,
            String mobileNo, String landlineNo, String email) {
        if (mobileNo == null || mobileNo.trim().isEmpty()) {
            return false;
        }
        patientModel.setAddressLine1(nilIfBlank(addressLine1));
        patientModel.setAddressLine2(addressLine2 != null ? addressLine2.trim() : ""); // genuinely optional 2nd line — blank is normal
        patientModel.setCity(nilIfBlank(city));
        patientModel.setMobileNo(mobileNo.trim());
        patientModel.setLandlineNo(landlineNo != null ? landlineNo.trim() : ""); // optional alt. contact — blank is normal
        patientModel.setEmail(email != null ? email.trim() : ""); // required in practice (login/receipts) — left blank, not "Nil", if truly skipped
        return persist();
    }

    // =========================================================================
    // Step 3 – Medical Information
    // =========================================================================

    public boolean saveStep3(String bloodGroup, String allergies, String medicalConditions,
            String currentMedications, String previousSurgeries) {
        // Blood Group is a dropdown with its own "Select" placeholder for
        // "not chosen" — leave it blank, not "Nil", so that placeholder still applies.
        patientModel.setBloodGroup(bloodGroup != null ? bloodGroup.trim() : "");
        patientModel.setAllergies(nilIfBlank(allergies));
        patientModel.setMedicalConditions(nilIfBlank(medicalConditions));
        patientModel.setCurrentMedications(nilIfBlank(currentMedications));
        patientModel.setPreviousSurgeries(nilIfBlank(previousSurgeries));
        return persist();
    }

    // =========================================================================
    // Step 4 – Dental Information
    // =========================================================================

    /**
     * @param oralHygiene "Good"/"Fair"/"Poor" from the exclusive pill group,
     *                    stored as-is in {@link model.PatientModel#getOralHygiene()}.
     */
    public boolean saveStep4(String lastDentalVisit, String dentalHistory, String dentalProblems,
            String oralHygiene, String medicalNotes) {
        patientModel.setLastDentalVisit(lastDentalVisit != null ? lastDentalVisit.trim() : ""); // DATE column
        patientModel.setDentalHistory(nilIfBlank(dentalHistory));
        patientModel.setDentalProblems(nilIfBlank(dentalProblems));
        patientModel.setOralHygiene(oralHygiene);
        patientModel.setDentalMedicalNotes(nilIfBlank(medicalNotes));
        return persist();
    }

    /**
     * A left-blank optional text field is saved as the literal word "Nil"
     * instead of an empty string — so the next person to open this profile
     * sees an honest "this was reviewed and nothing applies" instead of a
     * blank box that looks unfinished/broken (same convention this app
     * already used for Medical Notes; now applied to every comparable
     * free-text field, not dates/numbers/dropdowns/choice groups where the
     * word "Nil" wouldn't make sense or would break parsing).
     */
    private static String nilIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? "Nil" : s.trim();
    }

    private boolean persist() {
        boolean ok = PATIENT_DAO.upsert(patientModel);
        // (Re)confirm the link on every successful save, not just the first
        // one — cheap, and guarantees it's always pointing at this exact
        // patient_id even if something upstream ever got out of sync.
        if (ok && username != null) {
            USER_DAO.linkPatientId(username, patientModel.getPatientId());
            // Keep the login's own display name in sync too — same fix
            // applied to DentistProfileController for the identical bug:
            // "patients" just got whatever Full Name was typed on Step 1,
            // but "users.full_name" has no FK to follow along, and every
            // "my own data" screen that filters by getFullName() (dashboard
            // badges, My Appointments/Billings) would otherwise silently
            // stop matching the moment a patient renamed themselves here.
            if (patientModel.getFullName() != null && !patientModel.getFullName().trim().isEmpty()) {
                USER_DAO.updateFullName(username, patientModel.getFullName());
            }
        }
        return ok;
    }
}
