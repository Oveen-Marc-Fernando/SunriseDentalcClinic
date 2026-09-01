package model;

/**
 * Model that holds all wizard data collected across Steps 1-5
 * of the Dentist Management registration wizard.
 *
 * This single shared object is passed between the controller and all views.
 *
 * @author oveen
 */
public class DentistModel {

    // ── Step 1: Dentist Information ───────────────────────────────────────────
    private String title;       // Dr / Mr / Mrs
    private String gender;      // Male / Female
    private String fullName;
    private String dentistId;
    private String dob;
    private String nic;

    // ── Step 2: Professional Information ─────────────────────────────────────
    private String slmcNo;
    private String qualification;
    private String university;
    private String graduationYear;
    private String specialization;
    private String experience;
    private String licenseStatus; // Active / Inactive

    // ── Step 3: Contact Information ───────────────────────────────────────────
    private String mobileNo;
    private String email;
    private String address;
    private String emergencyNo;

    // ── Step 4: Employment Information ────────────────────────────────────────
    private String joinedDate;
    private String employmentType; // Full Time / Part Time / Contract
    private String consultationFee;
    private String employmentStatus;

    // ── Step 5: Availability ──────────────────────────────────────────────────
    private String workingDays;
    private String startTime;
    private String endTime;
    private String breakTime;
    private String roomNo;

    public DentistModel() {
        // default empty constructor
    }

    // ── Step 1 Getters & Setters ──────────────────────────────────────────────

    public String getTitle()            { return title; }
    public void   setTitle(String v)    { this.title = v; }

    public String getGender()           { return gender; }
    public void   setGender(String v)   { this.gender = v; }

    public String getFullName()         { return fullName; }
    public void   setFullName(String v) { this.fullName = v; }

    public String getDentistId()         { return dentistId; }
    public void   setDentistId(String v) { this.dentistId = v; }

    public String getDob()              { return dob; }
    public void   setDob(String v)      { this.dob = v; }

    public String getNic()              { return nic; }
    public void   setNic(String v)      { this.nic = v; }

    // ── Step 2 Getters & Setters ──────────────────────────────────────────────

    public String getSlmcNo()              { return slmcNo; }
    public void   setSlmcNo(String v)      { this.slmcNo = v; }

    public String getQualification()       { return qualification; }
    public void   setQualification(String v){ this.qualification = v; }

    public String getUniversity()          { return university; }
    public void   setUniversity(String v)  { this.university = v; }

    public String getGraduationYear()      { return graduationYear; }
    public void   setGraduationYear(String v){ this.graduationYear = v; }

    public String getSpecialization()      { return specialization; }
    public void   setSpecialization(String v){ this.specialization = v; }

    public String getExperience()          { return experience; }
    public void   setExperience(String v)  { this.experience = v; }

    public String getLicenseStatus()       { return licenseStatus; }
    public void   setLicenseStatus(String v){ this.licenseStatus = v; }

    // ── Step 3 Getters & Setters ──────────────────────────────────────────────

    public String getMobileNo()            { return mobileNo; }
    public void   setMobileNo(String v)    { this.mobileNo = v; }

    public String getEmail()               { return email; }
    public void   setEmail(String v)       { this.email = v; }

    public String getAddress()             { return address; }
    public void   setAddress(String v)     { this.address = v; }

    public String getEmergencyNo()         { return emergencyNo; }
    public void   setEmergencyNo(String v) { this.emergencyNo = v; }

    // ── Step 4 Getters & Setters ──────────────────────────────────────────────

    public String getJoinedDate()           { return joinedDate; }
    public void   setJoinedDate(String v)   { this.joinedDate = v; }

    public String getEmploymentType()       { return employmentType; }
    public void   setEmploymentType(String v){ this.employmentType = v; }

    public String getConsultationFee()      { return consultationFee; }
    public void   setConsultationFee(String v){ this.consultationFee = v; }

    public String getEmploymentStatus()     { return employmentStatus; }
    public void   setEmploymentStatus(String v){ this.employmentStatus = v; }

    // ── Step 5 Getters & Setters ──────────────────────────────────────────────

    public String getWorkingDays()          { return workingDays; }
    public void   setWorkingDays(String v)  { this.workingDays = v; }

    public String getStartTime()            { return startTime; }
    public void   setStartTime(String v)    { this.startTime = v; }

    public String getEndTime()              { return endTime; }
    public void   setEndTime(String v)      { this.endTime = v; }

    public String getBreakTime()            { return breakTime; }
    public void   setBreakTime(String v)    { this.breakTime = v; }

    public String getRoomNo()               { return roomNo; }
    public void   setRoomNo(String v)       { this.roomNo = v; }

    // ── Utility ───────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "DentistModel{"
                + "fullName='" + fullName + '\''
                + ", dentistId='" + dentistId + '\''
                + ", slmcNo='" + slmcNo + '\''
                + ", email='" + email + '\''
                + '}';
    }
}
