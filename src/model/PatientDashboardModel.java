package model;

/**
 * Model holding Patient Dashboard self-service metrics and state.
 *
 * @author oveen
 */
public class PatientDashboardModel {

    private String patientName;
    private int    upcomingAppointments;
    private int    pastAppointments;
    private double outstandingBill;
    private int    openHelpDesk;
    private int    availableReports;

    public PatientDashboardModel() {
        this.patientName          = "Patient";
        this.upcomingAppointments = 2;
        this.pastAppointments     = 8;
        this.outstandingBill      = 125.00;
        this.openHelpDesk         = 0;
        this.availableReports     = 3;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getPatientName()          { return patientName; }
    public int    getUpcomingAppointments() { return upcomingAppointments; }
    public int    getPastAppointments()     { return pastAppointments; }
    public double getOutstandingBill()      { return outstandingBill; }
    public int    getOpenHelpDesk()         { return openHelpDesk; }
    public int    getAvailableReports()     { return availableReports; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setPatientName(String patientName)        { this.patientName = (patientName != null && !patientName.isEmpty()) ? patientName : "Patient"; }
    public void setUpcomingAppointments(int val)          { this.upcomingAppointments = Math.max(0, val); }
    public void setPastAppointments(int val)              { this.pastAppointments = Math.max(0, val); }
    public void setOutstandingBill(double val)            { this.outstandingBill = Math.max(0.0, val); }
    public void setOpenHelpDesk(int val)                  { this.openHelpDesk = Math.max(0, val); }
    public void setAvailableReports(int val)              { this.availableReports = Math.max(0, val); }
}
