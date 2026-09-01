package model;

/**
 * Data Model for Patient Management.
 * Holds information gathered across the 4-step Patient registration wizard.
 *
 * @author oveen
 */
public class PatientModel {

    // Step 1: Personal Information
    private String title;
    private String gender;
    private String fullName;
    private String patientId;
    private String dob;
    private String age;
    private String nic;

    // Step 2: Contact Information
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String mobileNo;
    private String landlineNo;
    private String email;

    // Step 3: Medical Information
    private String bloodGroup;
    private String allergies;
    private String medicalConditions;
    private String currentMedications;
    private String previousSurgeries;
    private String generalMedicalNotes;

    // Step 4: Dental Information
    private String lastDentalVisit;
    private String dentalHistory;
    private String dentalProblems;
    private String oralHygiene; // Good / Fair / Poor
    private String dentalMedicalNotes;

    public PatientModel() {
    }

    // ===========================================================
    // Step 1 Getters and Setters
    // ===========================================================
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    // ===========================================================
    // Step 2 Getters and Setters
    // ===========================================================
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getLandlineNo() { return landlineNo; }
    public void setLandlineNo(String landlineNo) { this.landlineNo = landlineNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // ===========================================================
    // Step 3 Getters and Setters
    // ===========================================================
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }

    public String getCurrentMedications() { return currentMedications; }
    public void setCurrentMedications(String currentMedications) { this.currentMedications = currentMedications; }

    public String getPreviousSurgeries() { return previousSurgeries; }
    public void setPreviousSurgeries(String previousSurgeries) { this.previousSurgeries = previousSurgeries; }

    public String getGeneralMedicalNotes() { return generalMedicalNotes; }
    public void setGeneralMedicalNotes(String generalMedicalNotes) { this.generalMedicalNotes = generalMedicalNotes; }

    // ===========================================================
    // Step 4 Getters and Setters
    // ===========================================================
    public String getLastDentalVisit() { return lastDentalVisit; }
    public void setLastDentalVisit(String lastDentalVisit) { this.lastDentalVisit = lastDentalVisit; }

    public String getDentalHistory() { return dentalHistory; }
    public void setDentalHistory(String dentalHistory) { this.dentalHistory = dentalHistory; }

    public String getDentalProblems() { return dentalProblems; }
    public void setDentalProblems(String dentalProblems) { this.dentalProblems = dentalProblems; }

    public String getOralHygiene() { return oralHygiene; }
    public void setOralHygiene(String oralHygiene) { this.oralHygiene = oralHygiene; }

    public String getDentalMedicalNotes() { return dentalMedicalNotes; }
    public void setDentalMedicalNotes(String dentalMedicalNotes) { this.dentalMedicalNotes = dentalMedicalNotes; }
}
