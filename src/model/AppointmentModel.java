package model;

/**
 * Data Model for an appointment — mirrors the {@code appointments} table
 * (db/schema.sql) 1:1, including {@code address}/{@code contactNo}, a real
 * stored snapshot of the patient's details taken at booking time. It also
 * carries a few convenience fields ({@code city}, {@code mobileNo},
 * {@code roomNo}) that AppointmentDAO backfills via LEFT JOINs to
 * {@code patients} and {@code dentists} for grids/receipts that want the
 * dentist's *current* room or a live-refreshed contact detail — those three
 * are never written back to the appointments table itself.
 *
 * Like every other *Model class, this is a plain data holder — dates/times
 * are kept as Strings; DAO does the java.sql conversion at the boundary.
 *
 * @author oveen
 */
public class AppointmentModel {

    private String appointmentId;
    private String patientName;
    private String dentistName;
    private String treatmentType;
    private String date;
    private String time;
    private String status;

    // A real, stored snapshot of the patient's address/contact number at the
    // moment this appointment was booked — unlike city/mobileNo below, these
    // ARE persisted as real columns on the appointments table itself.
    private String address;
    private String contactNo;

    // Joined in from patients — display-only, not persisted via this model.
    private String city;
    private String mobileNo;

    // Joined in from dentists — display-only, same as city/mobileNo above;
    // appointments has no room_no column of its own, so this always reflects
    // whichever room is on that dentist's record *right now* (see OS_AM_2's
    // javadoc: the room simply follows whichever dentist is picked).
    private String roomNo;

    public AppointmentModel() {
    }

    public String getAppointmentId()            { return appointmentId; }
    public void   setAppointmentId(String v)     { this.appointmentId = v; }

    public String getPatientName()               { return patientName; }
    public void   setPatientName(String v)        { this.patientName = v; }

    public String getDentistName()               { return dentistName; }
    public void   setDentistName(String v)        { this.dentistName = v; }

    public String getTreatmentType()             { return treatmentType; }
    public void   setTreatmentType(String v)      { this.treatmentType = v; }

    public String getDate()                       { return date; }
    public void   setDate(String v)               { this.date = v; }

    public String getTime()                       { return time; }
    public void   setTime(String v)               { this.time = v; }

    public String getStatus()                     { return status; }
    public void   setStatus(String v)              { this.status = v; }

    public String getAddress()                    { return address; }
    public void   setAddress(String v)             { this.address = v; }

    public String getContactNo()                  { return contactNo; }
    public void   setContactNo(String v)           { this.contactNo = v; }

    public String getCity()                       { return city; }
    public void   setCity(String v)               { this.city = v; }

    public String getMobileNo()                   { return mobileNo; }
    public void   setMobileNo(String v)            { this.mobileNo = v; }

    public String getRoomNo()                      { return roomNo; }
    public void   setRoomNo(String v)               { this.roomNo = v; }

    @Override
    public String toString() {
        return "AppointmentModel{"
                + "appointmentId='" + appointmentId + '\''
                + ", patientName='" + patientName + '\''
                + ", dentistName='" + dentistName + '\''
                + ", date='" + date + '\''
                + ", time='" + time + '\''
                + ", status='" + status + '\''
                + '}';
    }
}
