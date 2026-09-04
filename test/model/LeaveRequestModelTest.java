package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/** Getter/setter round-trip tests for {@link LeaveRequestModel} — a plain data holder. */
public class LeaveRequestModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void everyFieldRoundTripsThroughItsOwnGetterAndSetter() {
        LeaveRequestModel m = new LeaveRequestModel();
        m.setLeaveRequestId(7);
        m.setDentistName("Dr. Smith");
        m.setLeaveDate("2026-09-01");
        m.setStatus("Pending");

        assertEquals(7, m.getLeaveRequestId());
        assertEquals("Dr. Smith", m.getDentistName());
        assertEquals("2026-09-01", m.getLeaveDate());
        assertEquals("Pending", m.getStatus());
    }

    @Test
    public void toString_containsTheCoreIdentifyingFields() {
        LeaveRequestModel m = new LeaveRequestModel();
        m.setDentistName("Dr. Smith");
        m.setLeaveDate("2026-09-01");
        m.setStatus("Approved");

        String text = m.toString();
        assertTrue(text.contains("Dr. Smith"));
        assertTrue(text.contains("2026-09-01"));
        assertTrue(text.contains("Approved"));
    }
}
