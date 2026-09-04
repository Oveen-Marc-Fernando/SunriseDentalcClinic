package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/**
 * Tests for {@link PatientDashboardModel} — default seed values, negative
 * clamping on the numeric setters, and {@code setPatientName}'s fallback to
 * "Patient" for a null/empty name.
 */
public class PatientDashboardModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void freshModel_hasTheDocumentedDefaultValues() {
        PatientDashboardModel m = new PatientDashboardModel();
        assertEquals("Patient", m.getPatientName());
        assertEquals(2, m.getUpcomingAppointments());
        assertEquals(8, m.getPastAppointments());
        assertEquals(125.00, m.getOutstandingBill(), 0.0001);
        assertEquals(0, m.getOpenHelpDesk());
        assertEquals(3, m.getAvailableReports());
    }

    @Test
    public void setPatientName_realName_isKept() {
        PatientDashboardModel m = new PatientDashboardModel();
        m.setPatientName("Jane Doe");
        assertEquals("Jane Doe", m.getPatientName());
    }

    @Test
    public void setPatientName_null_fallsBackToDefault() {
        PatientDashboardModel m = new PatientDashboardModel();
        m.setPatientName("Jane Doe");
        m.setPatientName(null);
        assertEquals("Patient", m.getPatientName());
    }

    @Test
    public void setPatientName_emptyString_fallsBackToDefault() {
        PatientDashboardModel m = new PatientDashboardModel();
        m.setPatientName("Jane Doe");
        m.setPatientName("");
        assertEquals("Patient", m.getPatientName());
    }

    @Test
    public void numericSetters_clampNegativeValuesToZero() {
        PatientDashboardModel m = new PatientDashboardModel();
        m.setUpcomingAppointments(-1);
        m.setPastAppointments(-1);
        m.setOutstandingBill(-50.0);
        m.setOpenHelpDesk(-1);
        m.setAvailableReports(-1);

        assertEquals(0, m.getUpcomingAppointments());
        assertEquals(0, m.getPastAppointments());
        assertEquals(0.0, m.getOutstandingBill(), 0.0001);
        assertEquals(0, m.getOpenHelpDesk());
        assertEquals(0, m.getAvailableReports());
    }

    @Test
    public void numericSetters_acceptValidPositiveValues() {
        PatientDashboardModel m = new PatientDashboardModel();
        m.setUpcomingAppointments(5);
        m.setPastAppointments(20);
        m.setOutstandingBill(999.99);
        m.setOpenHelpDesk(1);
        m.setAvailableReports(10);

        assertEquals(5, m.getUpcomingAppointments());
        assertEquals(20, m.getPastAppointments());
        assertEquals(999.99, m.getOutstandingBill(), 0.0001);
        assertEquals(1, m.getOpenHelpDesk());
        assertEquals(10, m.getAvailableReports());
    }
}
