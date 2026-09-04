package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/** Getter/setter round-trip tests for {@link ApprovalModel} — a plain data holder. */
public class ApprovalModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void everyFieldRoundTripsThroughItsOwnGetterAndSetter() {
        ApprovalModel m = new ApprovalModel();
        m.setApprovalId("AP1");
        m.setDescription("New chairs");
        m.setRemarks("Urgent");
        m.setApprovalDate("2026-08-24");
        m.setAmount("15000");
        m.setStatus("Pending");
        m.setSubmittedBy("officestaff");

        assertEquals("AP1", m.getApprovalId());
        assertEquals("New chairs", m.getDescription());
        assertEquals("Urgent", m.getRemarks());
        assertEquals("2026-08-24", m.getApprovalDate());
        assertEquals("15000", m.getAmount());
        assertEquals("Pending", m.getStatus());
        assertEquals("officestaff", m.getSubmittedBy());
    }
}
