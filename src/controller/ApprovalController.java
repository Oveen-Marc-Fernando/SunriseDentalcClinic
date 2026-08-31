package controller;

import java.util.List;
import model.ApprovalModel;

/**
 * Controller for Approval Management (OS_APM_Grid read-only list + the
 * OS_APM_Add submission form). Office staff can only view approval status
 * here — approving/declining is an Administration-only action performed
 * elsewhere, so this controller only ever creates new "Pending" requests.
 *
 * @author oveen
 */
public class ApprovalController {

    private static final dao.ApprovalDAO APPROVAL_DAO = new dao.ApprovalDAO();

    public static List<ApprovalModel> getAll() {
        return APPROVAL_DAO.findAll();
    }

    public static int getRecordCount() {
        return APPROVAL_DAO.count();
    }

    public static String nextApprovalId() {
        return APPROVAL_DAO.nextApprovalId();
    }

    /** Approves a pending request — AD_APR_OfficeStaff's Approve button. */
    public static boolean approve(String approvalId) {
        return APPROVAL_DAO.updateStatus(approvalId, "Approved");
    }

    /** Declines a pending request — AD_APR_OfficeStaff's Reject button. */
    public static boolean decline(String approvalId) {
        return APPROVAL_DAO.updateStatus(approvalId, "Declined");
    }

    /**
     * Submits a new approval request (OS_APM_Add's "Send" button) — always
     * starts life as "Pending". {@code submittedBy} is the currently
     * logged-in Office Staff username (see AppController#getCurrentUser()) —
     * the person filling this form never types it themselves.
     */
    public static void submitApproval(String approvalId, String description, String approvalDate, String amount,
            String submittedBy) {
        ApprovalModel m = new ApprovalModel();
        m.setApprovalId(approvalId);
        m.setDescription(description);
        m.setApprovalDate(approvalDate);
        m.setAmount(amount);
        m.setStatus("Pending");
        m.setSubmittedBy(submittedBy);
        APPROVAL_DAO.insert(m);
    }
}
