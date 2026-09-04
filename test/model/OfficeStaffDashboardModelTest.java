package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/**
 * Tests for {@link OfficeStaffDashboardModel} — verifies both its default
 * seed values and that every setter clamps negative input to 0.
 */
public class OfficeStaffDashboardModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void freshModel_hasTheDocumentedDefaultValues() {
        OfficeStaffDashboardModel m = new OfficeStaffDashboardModel();
        assertEquals(5, m.getTotalDentists());
        assertEquals(128, m.getTotalPatients());
        assertEquals(14, m.getPendingAppointments());
        assertEquals(7, m.getPendingBillings());
        assertEquals(3, m.getPendingApprovals());
        assertEquals(2, m.getOpenHelpDesk());
    }

    @Test
    public void setters_acceptValidPositiveValues() {
        OfficeStaffDashboardModel m = new OfficeStaffDashboardModel();
        m.setTotalDentists(9);
        m.setTotalPatients(200);
        m.setPendingAppointments(1);
        m.setPendingBillings(0);
        m.setPendingApprovals(5);
        m.setOpenHelpDesk(10);

        assertEquals(9, m.getTotalDentists());
        assertEquals(200, m.getTotalPatients());
        assertEquals(1, m.getPendingAppointments());
        assertEquals(0, m.getPendingBillings());
        assertEquals(5, m.getPendingApprovals());
        assertEquals(10, m.getOpenHelpDesk());
    }

    @Test
    public void setters_clampNegativeValuesToZero() {
        OfficeStaffDashboardModel m = new OfficeStaffDashboardModel();
        m.setTotalDentists(-1);
        m.setTotalPatients(-1);
        m.setPendingAppointments(-1);
        m.setPendingBillings(-1);
        m.setPendingApprovals(-1);
        m.setOpenHelpDesk(-1);

        assertEquals(0, m.getTotalDentists());
        assertEquals(0, m.getTotalPatients());
        assertEquals(0, m.getPendingAppointments());
        assertEquals(0, m.getPendingBillings());
        assertEquals(0, m.getPendingApprovals());
        assertEquals(0, m.getOpenHelpDesk());
    }
}
