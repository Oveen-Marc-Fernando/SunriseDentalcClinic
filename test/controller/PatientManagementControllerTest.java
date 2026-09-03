package controller;

import dao.PatientDAO;
import dao.UserDAO;
import java.util.Collections;
import model.PatientModel;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedConstruction;
import support.ConsolePrintingRule;

/**
 * Unit tests for {@link PatientManagementController}'s static, DAO-facing
 * methods, faking out both {@code PatientDAO} and {@code UserDAO} via
 * {@code mockConstruction} (see RegisterControllerTest). The instance wizard
 * methods ({@code goNextFromStep1..3}, {@code submitFromStep4}, etc.) are
 * deliberately not covered — same reasoning as
 * DentistManagementControllerTest: they pop real dialogs and construct real
 * Swing views.
 *
 * Every {@link PatientModel} built here leaves {@code email} blank/null —
 * {@code createLoginForPatient}'s success path fires a real (fire-and-forget)
 * temporary-password email when one's on file, and a blank email is what
 * keeps that from ever starting.
 */
public class PatientManagementControllerTest {

    private static MockedConstruction<PatientDAO> patientConstruction;
    private static MockedConstruction<UserDAO> userConstruction;
    private static PatientDAO mockPatientDao;
    private static UserDAO mockUserDao;

    @BeforeClass
    public static void mockDaoConstruction() {
        patientConstruction = mockConstruction(PatientDAO.class);
        userConstruction = mockConstruction(UserDAO.class);
        PatientManagementController.count();
        mockPatientDao = patientConstruction.constructed().get(0);
        mockUserDao = userConstruction.constructed().get(0);
    }

    @AfterClass
    public static void closeConstructionMocks() {
        patientConstruction.close();
        userConstruction.close();
    }

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Before
    public void resetMocks() {
        reset(mockPatientDao, mockUserDao);
    }

    @Test
    public void getAll_delegatesToFindAll() {
        PatientModel row = new PatientModel();
        row.setPatientId("P1");
        when(mockPatientDao.findAll()).thenReturn(Collections.singletonList(row));
        assertEquals(1, PatientManagementController.getAll().size());
    }

    @Test
    public void count_delegatesToDao() {
        when(mockPatientDao.count()).thenReturn(42);
        assertEquals(42, PatientManagementController.count());
    }

    @Test
    public void delete_delegatesToDaoAndReturnsItsResult() {
        when(mockPatientDao.delete("P1")).thenReturn(true);
        assertTrue(PatientManagementController.delete("P1"));
    }

    @Test
    public void updatePatient_validModel_delegatesToUpsert() {
        PatientModel m = new PatientModel();
        m.setPatientId("P1");
        when(mockPatientDao.upsert(m)).thenReturn(true);
        assertTrue(PatientManagementController.updatePatient(m));
    }

    @Test
    public void updatePatient_nullModel_returnsFalseWithoutQueryingDao() {
        assertFalse(PatientManagementController.updatePatient(null));
        verifyNoInteractions(mockPatientDao);
    }

    @Test
    public void updatePatient_blankPatientId_returnsFalseWithoutQueryingDao() {
        PatientModel m = new PatientModel();
        m.setPatientId("   ");
        assertFalse(PatientManagementController.updatePatient(m));
        verifyNoInteractions(mockPatientDao);
    }

    @Test
    public void lookupMethods_eachDelegatesToItsOwnDaoMethod() {
        when(mockPatientDao.findEmailByFullName("John Doe")).thenReturn("john@example.com");
        when(mockPatientDao.findEmailById("P1")).thenReturn("john2@example.com");
        when(mockPatientDao.findIdByFullName("John Doe")).thenReturn("P1");
        PatientModel byName = new PatientModel();
        when(mockPatientDao.findByFullName("John Doe")).thenReturn(byName);
        when(mockPatientDao.nextPatientId()).thenReturn("P104");
        PatientModel byId = new PatientModel();
        when(mockPatientDao.findById("P1")).thenReturn(byId);

        assertEquals("john@example.com", PatientManagementController.findEmailByFullName("John Doe"));
        assertEquals("john2@example.com", PatientManagementController.findEmailById("P1"));
        assertEquals("P1", PatientManagementController.findIdByFullName("John Doe"));
        assertSame(byName, PatientManagementController.findByFullName("John Doe"));
        assertEquals("P104", PatientManagementController.nextPatientId());
        assertSame(byId, PatientManagementController.findById("P1"));
    }

    // ── createLoginForPatient ────────────────────────────────────────────────

    @Test
    public void createLoginForPatient_blankUsername_returnsFailedWithoutQueryingDao() {
        assertEquals(PatientManagementController.LoginOutcome.FAILED,
                PatientManagementController.createLoginForPatient("P1", "  "));
        verifyNoInteractions(mockPatientDao);
        verifyNoInteractions(mockUserDao);
    }

    @Test
    public void createLoginForPatient_patientNotFound_returnsNotFound() {
        when(mockPatientDao.findById("P999")).thenReturn(null);
        assertEquals(PatientManagementController.LoginOutcome.NOT_FOUND,
                PatientManagementController.createLoginForPatient("P999", "newuser"));
    }

    @Test
    public void createLoginForPatient_usernameAlreadyTaken_returnsUsernameTaken() {
        PatientModel p = new PatientModel();
        p.setFullName("John Doe");
        when(mockPatientDao.findById("P1")).thenReturn(p);
        when(mockUserDao.getStatus("taken")).thenReturn("APPROVED");

        assertEquals(PatientManagementController.LoginOutcome.USERNAME_TAKEN,
                PatientManagementController.createLoginForPatient("P1", "taken"));
    }

    @Test
    public void createLoginForPatient_success_linksPatientIdAndReturnsSuccess() {
        PatientModel p = new PatientModel();
        p.setFullName("John Doe");
        // email left blank deliberately — see class javadoc
        when(mockPatientDao.findById("P1")).thenReturn(p);
        when(mockUserDao.getStatus("newuser")).thenReturn(null);
        when(mockUserDao.insertApprovedWithTempPassword(eq("newuser"), anyString(), eq("John Doe"), any(), any(), any(), eq(model.User.Role.PATIENT)))
                .thenReturn(true);

        assertEquals(PatientManagementController.LoginOutcome.SUCCESS,
                PatientManagementController.createLoginForPatient("P1", "newuser"));

        verify(mockUserDao).linkPatientId("newuser", "P1");
    }

    @Test
    public void createLoginForPatient_daoInsertFails_returnsFailed_andNeverLinksPatientId() {
        PatientModel p = new PatientModel();
        p.setFullName("John Doe");
        when(mockPatientDao.findById("P1")).thenReturn(p);
        when(mockUserDao.getStatus("newuser")).thenReturn(null);
        when(mockUserDao.insertApprovedWithTempPassword(any(), any(), any(), any(), any(), any(), any())).thenReturn(false);

        assertEquals(PatientManagementController.LoginOutcome.FAILED,
                PatientManagementController.createLoginForPatient("P1", "newuser"));

        verify(mockUserDao, never()).linkPatientId(any(), any());
    }

    // ── Instance model construction (no DAO/UI involved) ────────────────────

    @Test
    public void defaultConstructor_startsWithAFreshEmptyModel() {
        assertNotNull(new PatientManagementController().getPatientModel());
    }

    @Test
    public void modelConstructor_keepsTheGivenModelInstance() {
        PatientModel existing = new PatientModel();
        existing.setPatientId("P99");
        assertSame(existing, new PatientManagementController(existing).getPatientModel());
    }
}
