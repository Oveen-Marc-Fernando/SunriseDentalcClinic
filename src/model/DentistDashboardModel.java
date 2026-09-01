package model;

/**
 * Model holding Dentist Dashboard metrics and appointment state.
 *
 * @author oveen
 */
public class DentistDashboardModel {

    private int myAppointments;
    private int myPatients;
    private int requestSupplies;
    private int requestLeaves;

    public DentistDashboardModel() {
        this.myAppointments  = 2;
        this.myPatients      = 1;
        this.requestSupplies = 4;
        this.requestLeaves   = 1;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int getMyAppointments()  { return myAppointments; }
    public int getMyPatients()      { return myPatients; }
    public int getRequestSupplies() { return requestSupplies; }
    public int getRequestLeaves()   { return requestLeaves; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setMyAppointments(int val)  { this.myAppointments = Math.max(0, val); }
    public void setMyPatients(int val)      { this.myPatients = Math.max(0, val); }
    public void setRequestSupplies(int val) { this.requestSupplies = Math.max(0, val); }
    public void setRequestLeaves(int val)   { this.requestLeaves = Math.max(0, val); }
}
