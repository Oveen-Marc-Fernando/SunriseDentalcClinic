package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/** Getter/setter round-trip tests for {@link UserApprovalModel} — a plain data holder. */
public class UserApprovalModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void everyFieldRoundTripsThroughItsOwnGetterAndSetter() {
        UserApprovalModel m = new UserApprovalModel();
        m.setLoginId("L101");
        m.setUsername("officestaff");
        m.setFullName("Jane Doe");
        m.setLoginType("OFFICE_STAFF");
        m.setStatus("PENDING");
        m.setApprovedDate("2026-08-24");

        assertEquals("L101", m.getLoginId());
        assertEquals("officestaff", m.getUsername());
        assertEquals("Jane Doe", m.getFullName());
        assertEquals("OFFICE_STAFF", m.getLoginType());
        assertEquals("PENDING", m.getStatus());
        assertEquals("2026-08-24", m.getApprovedDate());
    }
}
