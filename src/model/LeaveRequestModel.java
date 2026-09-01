package model;

/**
 * A single dentist-submitted leave request.
 *
 * @author oveen
 */
public class LeaveRequestModel {

    private int    leaveRequestId;
    private String dentistName;
    private String leaveDate;
    private String status; // Pending / Approved / Rejected

    public LeaveRequestModel() {
        // default empty constructor
    }

    public int    getLeaveRequestId()           { return leaveRequestId; }
    public void   setLeaveRequestId(int v)      { this.leaveRequestId = v; }

    public String getDentistName()      { return dentistName; }
    public void   setDentistName(String v) { this.dentistName = v; }

    public String getLeaveDate()        { return leaveDate; }
    public void   setLeaveDate(String v) { this.leaveDate = v; }

    public String getStatus()           { return status; }
    public void   setStatus(String v)   { this.status = v; }

    @Override
    public String toString() {
        return "LeaveRequestModel{"
                + "dentistName='" + dentistName + '\''
                + ", leaveDate='" + leaveDate + '\''
                + ", status='" + status + '\''
                + '}';
    }
}
