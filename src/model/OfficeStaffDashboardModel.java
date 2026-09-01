package model;

/**
 * Model holding Office Staff Dashboard business metrics and system state.
 *
 * @author oveen
 */
public class OfficeStaffDashboardModel {

    private int totalDentists;
    private int totalPatients;
    private int pendingAppointments;
    private int pendingBillings;
    private int pendingApprovals;
    private int openHelpDesk;

    public OfficeStaffDashboardModel() {
        this.totalDentists       = 5;
        this.totalPatients       = 128;
        this.pendingAppointments = 14;
        this.pendingBillings     = 7;
        this.pendingApprovals    = 3;
        this.openHelpDesk        = 2;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int getTotalDentists()       { return totalDentists; }
    public int getTotalPatients()       { return totalPatients; }
    public int getPendingAppointments() { return pendingAppointments; }
    public int getPendingBillings()     { return pendingBillings; }
    public int getPendingApprovals()    { return pendingApprovals; }
    public int getOpenHelpDesk()        { return openHelpDesk; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setTotalDentists(int val)       { this.totalDentists = Math.max(0, val); }
    public void setTotalPatients(int val)       { this.totalPatients = Math.max(0, val); }
    public void setPendingAppointments(int val) { this.pendingAppointments = Math.max(0, val); }
    public void setPendingBillings(int val)     { this.pendingBillings = Math.max(0, val); }
    public void setPendingApprovals(int val)    { this.pendingApprovals = Math.max(0, val); }
    public void setOpenHelpDesk(int val)        { this.openHelpDesk = Math.max(0, val); }
}
