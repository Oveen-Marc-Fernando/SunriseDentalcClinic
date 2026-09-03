package controller;

import dao.ApprovalDAO;
import java.util.Collections;
import model.ApprovalModel;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import support.ConsolePrintingRule;

/** Unit tests for {@link ApprovalController}, faking out {@code ApprovalDAO} via {@code mockConstruction} (see RegisterControllerTest for why). */
public class ApprovalControllerTest {

    private static MockedConstruction<ApprovalDAO> construction;
    private static ApprovalDAO mockApprovalDao;

    @BeforeClass
    public static void mockApprovalDaoConstruction() {
        construction = mockConstruction(ApprovalDAO.class);
        ApprovalController.getRecordCount(); // forces the static field's construction now, inside this scope
        mockApprovalDao = construction.constructed().get(0);
    }

    @AfterClass
    public static void closeConstructionMock() {
        construction.close();
    }

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Before
    public void resetMock() {
        reset(mockApprovalDao);
    }

    @Test
    public void getAll_delegatesToFindAll() {
        ApprovalModel row = new ApprovalModel();
        row.setApprovalId("AP1");
        when(mockApprovalDao.findAll()).thenReturn(Collections.singletonList(row));

        assertEquals(1, ApprovalController.getAll().size());
        assertEquals("AP1", ApprovalController.getAll().get(0).getApprovalId());
    }

    @Test
    public void getRecordCount_delegatesToCount() {
        when(mockApprovalDao.count()).thenReturn(7);
        assertEquals(7, ApprovalController.getRecordCount());
    }

    @Test
    public void nextApprovalId_delegatesToDao() {
        when(mockApprovalDao.nextApprovalId()).thenReturn("AP42");
        assertEquals("AP42", ApprovalController.nextApprovalId());
    }

    @Test
    public void approve_updatesStatusToApproved() {
        when(mockApprovalDao.updateStatus("AP1", "Approved")).thenReturn(true);
        assertTrue(ApprovalController.approve("AP1"));
        verify(mockApprovalDao).updateStatus("AP1", "Approved");
    }

    @Test
    public void decline_updatesStatusToDeclined() {
        when(mockApprovalDao.updateStatus("AP1", "Declined")).thenReturn(true);
        assertTrue(ApprovalController.decline("AP1"));
        verify(mockApprovalDao).updateStatus("AP1", "Declined");
    }

    @Test
    public void approveOrDecline_daoReturnsFalse_propagatesFalse() {
        when(mockApprovalDao.updateStatus(anyString(), anyString())).thenReturn(false);
        assertFalse(ApprovalController.approve("AP1"));
        assertFalse(ApprovalController.decline("AP1"));
    }

    @Test
    public void submitApproval_buildsAPendingModelWithAllGivenFields() {
        ArgumentCaptor<ApprovalModel> captor = ArgumentCaptor.forClass(ApprovalModel.class);

        ApprovalController.submitApproval("AP1", "New chairs", "2026-08-24", "15000", "officestaff");

        verify(mockApprovalDao).insert(captor.capture());
        ApprovalModel inserted = captor.getValue();
        assertEquals("AP1", inserted.getApprovalId());
        assertEquals("New chairs", inserted.getDescription());
        assertEquals("2026-08-24", inserted.getApprovalDate());
        assertEquals("15000", inserted.getAmount());
        assertEquals("officestaff", inserted.getSubmittedBy());
        // Always starts life "Pending" — approving/declining happens elsewhere (Administration), never at submission time.
        assertEquals("Pending", inserted.getStatus());
    }
}
