package model;

/**
 * A single row of the {@code users} table as shown on AD_APR_UserLogins —
 * {@code loginId} is a synthetic display-only ID ("L101", "L102", ...)
 * assigned at query time (see {@code dao.UserDAO#findAllForApproval}); the
 * real primary key is still {@code username}.
 *
 * @author oveen
 */
public class UserApprovalModel {

    private String loginId;
    private String username;
    private String fullName;
    private String loginType;   // the account's role (OFFICE_STAFF / DENTIST / ADMINISTRATION / PATIENT)
    private String status;      // PENDING / APPROVED / REJECTED
    private String approvedDate;

    public UserApprovalModel() {
    }

    public String getLoginId()               { return loginId; }
    public void   setLoginId(String v)       { this.loginId = v; }

    public String getUsername()              { return username; }
    public void   setUsername(String v)      { this.username = v; }

    public String getFullName()              { return fullName; }
    public void   setFullName(String v)      { this.fullName = v; }

    public String getLoginType()             { return loginType; }
    public void   setLoginType(String v)     { this.loginType = v; }

    public String getStatus()                { return status; }
    public void   setStatus(String v)        { this.status = v; }

    public String getApprovedDate()          { return approvedDate; }
    public void   setApprovedDate(String v)  { this.approvedDate = v; }
}
