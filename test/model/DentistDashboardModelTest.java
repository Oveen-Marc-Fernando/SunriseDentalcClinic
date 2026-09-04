package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/**
 * Tests for {@link DentistDashboardModel} — verifies both its default seed
 * values and that every setter clamps negative input to 0 rather than
 * allowing a nonsensical negative count onto the dashboard.
 */
public class DentistDashboardModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void freshModel_hasTheDocumentedDefaultValues() {
        DentistDashboardModel m = new DentistDashboardModel();
        assertEquals(2, m.getMyAppointments());
        assertEquals(1, m.getMyPatients());
        assertEquals(4, m.getRequestSupplies());
        assertEquals(1, m.getRequestLeaves());
    }

    @Test
    public void setters_acceptValidPositiveValues() {
        DentistDashboardModel m = new DentistDashboardModel();
        m.setMyAppointments(10);
        m.setMyPatients(20);
        m.setRequestSupplies(30);
        m.setRequestLeaves(40);

        assertEquals(10, m.getMyAppointments());
        assertEquals(20, m.getMyPatients());
        assertEquals(30, m.getRequestSupplies());
        assertEquals(40, m.getRequestLeaves());
    }

    @Test
    public void setters_clampNegativeValuesToZero() {
        DentistDashboardModel m = new DentistDashboardModel();
        m.setMyAppointments(-5);
        m.setMyPatients(-1);
        m.setRequestSupplies(-100);
        m.setRequestLeaves(-1);

        assertEquals(0, m.getMyAppointments());
        assertEquals(0, m.getMyPatients());
        assertEquals(0, m.getRequestSupplies());
        assertEquals(0, m.getRequestLeaves());
    }

    @Test
    public void setters_acceptZero() {
        DentistDashboardModel m = new DentistDashboardModel();
        m.setMyAppointments(0);
        assertEquals(0, m.getMyAppointments());
    }
}
