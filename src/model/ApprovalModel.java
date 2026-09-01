package model;

/**
 * Data Model for an approval record — mirrors the {@code approvals} table
 * (db/schema.sql) 1:1. Backs OS_APM_Grid (read) and OS_APM_Add (write).
 *
 * @author oveen
 */
public class ApprovalModel {

    private String approvalId;
    private String description;
    private String remarks;
    private String approvalDate;
    private String amount;
    private String status; // Approved / Pending / Declined
    // Which Office Staff login submitted this request — links Approval back
    // to User (see UserDAO), so this record isn't an orphan: it always
    // belongs to whoever was logged in when they clicked "Send".
    private String submittedBy;

    public ApprovalModel() {
    }

    public String getApprovalId()          { return approvalId; }
    public void   setApprovalId(String v)  { this.approvalId = v; }

    public String getDescription()         { return description; }
    public void   setDescription(String v) { this.description = v; }

    public String getRemarks()             { return remarks; }
    public void   setRemarks(String v)     { this.remarks = v; }

    public String getApprovalDate()        { return approvalDate; }
    public void   setApprovalDate(String v){ this.approvalDate = v; }

    public String getAmount()              { return amount; }
    public void   setAmount(String v)      { this.amount = v; }

    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status = v; }

    public String getSubmittedBy()         { return submittedBy; }
    public void   setSubmittedBy(String v) { this.submittedBy = v; }
}
