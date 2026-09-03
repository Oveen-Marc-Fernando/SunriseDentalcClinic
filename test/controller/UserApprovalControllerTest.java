package controller;

import dao.UserDAO;
import java.util.Collections;
import model.UserApprovalModel;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedConstruction;
import support.ConsolePrintingRule;

/**
 * Unit tests for {@link UserApprovalController}, faking out {@code UserDAO}
 * via Mockito's {@code mockConstruction} — same pattern (and same reasoning)
 * as {@link RegisterControllerTest}, since this controller also holds its
 * DAO in a {@code private static final} field.
 *
 * {@code getEmail(...)} is left unstubbed everywhere here, which makes it
 * return {@code null} — that's what keeps {@code approve()}'s fire-and-forget
 * email threads from ever starting during these tests (see the controller's
 * own null/blank-email guard).
 */
public class UserApprovalControllerTest {

    private static MockedConstruction<UserDAO> construction;
    private static UserDAO mockUserDao;

    @BeforeClass
    public static void mockUserDaoConstruction() {
        construction = mockConstruction(UserDAO.class);
        UserApprovalController.reject("warmup"); // forces the static field's construction now, inside this scope
        mockUserDao = construction.constructed().get(0);
    }

    @AfterClass
    public static void closeConstructionMock() {
        construction.close();
    }

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Before
    public void resetMock() {
        reset(mockUserDao);
    }

    @Test
    public void getAll_delegatesToFindAllForApproval() {
        UserApprovalModel row = new UserApprovalModel();
        row.setUsername("pendinguser");
        when(mockUserDao.findAllForApproval()).thenReturn(Collections.singletonList(row));

        assertEquals(1, UserApprovalController.getAll().size());
        assertEquals("pendinguser", UserApprovalController.getAll().get(0).getUsername());
    }

    @Test
    public void approve_selfSignupAccount_noDentistId_approvesDirectly() {
        when(mockUserDao.getDentistId("selfsignup")).thenReturn(null);
        when(mockUserDao.approve("selfsignup")).thenReturn(true);

        assertTrue(UserApprovalController.approve("selfsignup"));

        verify(mockUserDao).approve("selfsignup");
        verify(mockUserDao, never()).approveDentistLoginWithTempPassword(any(), any());
    }

    @Test
    public void approve_selfSignupAccount_blankDentistId_stillTreatedAsSelfSignup() {
        when(mockUserDao.getDentistId("selfsignup2")).thenReturn("   "); // blank, not null — same "no real dentist link" case
        when(mockUserDao.approve("selfsignup2")).thenReturn(true);

        assertTrue(UserApprovalController.approve("selfsignup2"));
        verify(mockUserDao).approve("selfsignup2");
    }

    @Test
    public void approve_dentistLoginRequest_hasDentistId_generatesTempPasswordInstead() {
        when(mockUserDao.getDentistId("dentistlogin")).thenReturn("D1");
        when(mockUserDao.approveDentistLoginWithTempPassword(eq("dentistlogin"), anyString())).thenReturn(true);

        assertTrue(UserApprovalController.approve("dentistlogin"));

        verify(mockUserDao).approveDentistLoginWithTempPassword(eq("dentistlogin"), anyString());
        verify(mockUserDao, never()).approve(any());
    }

    @Test
    public void approve_daoReturnsFalse_propagatesFalse() {
        when(mockUserDao.getDentistId("failcase")).thenReturn(null);
        when(mockUserDao.approve("failcase")).thenReturn(false);

        assertFalse(UserApprovalController.approve("failcase"));
    }

    @Test
    public void reject_delegatesToRejectAndReturnsItsResult() {
        when(mockUserDao.reject("someone")).thenReturn(true);
        assertTrue(UserApprovalController.reject("someone"));

        when(mockUserDao.reject("someoneelse")).thenReturn(false);
        assertFalse(UserApprovalController.reject("someoneelse"));
    }
}
