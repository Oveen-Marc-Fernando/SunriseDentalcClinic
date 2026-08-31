package controller;

import dao.LeaveRequestDAO;
import model.LeaveRequestModel;
import javax.swing.JOptionPane;

/**
 * Controller for the Dentist "Request Leaves" feature (D_RS_Leave), reached
 * from the Dentist Dashboard's "Request Leaves" tile.
 *
 * Backed by the real {@code leave_requests} table (see db/schema.sql) via
 * {@link LeaveRequestDAO} — the dashboard's badge count and the "Leave
 * Approved" reference field on D_RS_Leave both read straight from it.
 *
 * @author oveen
 */
public class LeaveRequestController {

    private static final LeaveRequestDAO LEAVE_DAO = new LeaveRequestDAO();

    /** Read-only snapshot of every leave request on record. */
    public static java.util.List<LeaveRequestModel> getAll() {
        return LEAVE_DAO.findAll();
    }

    /** Number of leave requests on record — mirrors the sibling grids' getRecordCount() convention. */
    public static int getRecordCount() {
        return LEAVE_DAO.count();
    }

    /** Number of leave requests on record for one dentist — Dentist Dashboard's own "Request Leaves" badge. */
    public static int getRecordCountForDentist(String dentistName) {
        return LEAVE_DAO.countForDentist(dentistName);
    }

    /** Approves a pending leave request — AD_APR_DentistLeave's Approve button. */
    public static boolean approve(int leaveRequestId) {
        return LEAVE_DAO.updateStatus(leaveRequestId, "Approved");
    }

    /** Rejects a pending leave request — AD_APR_DentistLeave's Reject button. */
    public static boolean reject(int leaveRequestId) {
        return LEAVE_DAO.updateStatus(leaveRequestId, "Rejected");
    }

    /** Most recent Approved leave date on record for the given dentist, or "" if none. */
    public static String getLastApprovedDate(String dentistName) {
        if (dentistName == null) {
            return "";
        }
        return LEAVE_DAO.lastApprovedDate(dentistName.trim());
    }

    /**
     * True if this dentist already has a request sitting Pending — D_RS_Leave
     * uses this to block a second request until the first is resolved
     * (Approved/Rejected) by an Administrator, one at a time.
     */
    public static boolean hasPendingRequest(String dentistName) {
        if (dentistName == null) {
            return false;
        }
        return LEAVE_DAO.hasPendingRequest(dentistName.trim());
    }

    /**
     * Validates and appends a new leave request (always starts Pending —
     * approval isn't modeled yet). Called from D_RS_Leave's "Request" button.
     *
     * @return true if the request was added
     */
    public boolean submitLeaveRequest(String dentistName, String leaveDate, javax.swing.JFrame currentView) {
        if (leaveDate == null || leaveDate.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Please choose a date to request leave for.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // Belt-and-braces: D_RS_Leave already locks the whole form once a
        // Pending request exists, but re-check here too in case that state
        // went stale (e.g. the form was left open across an approval).
        if (hasPendingRequest(dentistName)) {
            JOptionPane.showMessageDialog(currentView,
                    "You already have a leave request awaiting approval. You can only have one "
                            + "pending at a time — please wait for it to be reviewed before requesting another.",
                    "Request Already Pending", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        LeaveRequestModel model = new LeaveRequestModel();
        model.setDentistName(dentistName != null ? dentistName.trim() : "");
        model.setLeaveDate(leaveDate.trim());
        model.setStatus("Pending");

        LEAVE_DAO.insert(model);
        System.out.println("[CONTROLLER] Leave request submitted: " + model);
        return true;
    }
}
