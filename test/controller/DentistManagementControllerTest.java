package controller;

import dao.DentistDAO;
import dao.UserDAO;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import model.DentistModel;
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
 * Unit tests for {@link DentistManagementController}'s static, DAO-facing
 * methods, faking out both {@code DentistDAO} and {@code UserDAO} via
 * {@code mockConstruction} (see RegisterControllerTest). The instance wizard
 * methods ({@code goNextFromStep1..5}, {@code submitFromStep5}, etc.) are
 * deliberately not covered — every one of them pops a real
 * {@code JOptionPane}/{@code IconFactory} dialog or constructs a real Swing
 * view ({@code new view.OS_DM_2(...)}), which is UI behavior a unit test
 * shouldn't be driving.
 */
public class DentistManagementControllerTest {

    private static MockedConstruction<DentistDAO> dentistConstruction;
    private static MockedConstruction<UserDAO> userConstruction;
    private static DentistDAO mockDentistDao;
    private static UserDAO mockUserDao;

    @BeforeClass
    public static void mockDaoConstruction() {
        dentistConstruction = mockConstruction(DentistDAO.class);
        userConstruction = mockConstruction(UserDAO.class);
        // Both static fields are declared on this one class, so any static
        // method call initializes both together, inside this still-open scope.
        DentistManagementController.count();
        mockDentistDao = dentistConstruction.constructed().get(0);
        mockUserDao = userConstruction.constructed().get(0);
    }

    @AfterClass
    public static void closeConstructionMocks() {
        dentistConstruction.close();
        userConstruction.close();
    }

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Before
    public void resetMocks() {
        reset(mockDentistDao, mockUserDao);
    }

    private static Map<String, DentistModel> directoryOf(DentistModel... dentists) {
        Map<String, DentistModel> map = new LinkedHashMap<>();
        for (DentistModel d : dentists) {
            map.put(d.getFullName(), d);
        }
        return map;
    }

    @Test
    public void getDirectory_delegatesToFindAll() {
        DentistModel d = new DentistModel();
        d.setFullName("Dr. Smith");
        when(mockDentistDao.findAll()).thenReturn(directoryOf(d));

        assertEquals(1, DentistManagementController.getDirectory().size());
        assertTrue(DentistManagementController.getDirectory().containsKey("Dr. Smith"));
    }

    @Test
    public void count_isTheSizeOfFindAll() {
        DentistModel a = new DentistModel(); a.setFullName("A");
        DentistModel b = new DentistModel(); b.setFullName("B");
        when(mockDentistDao.findAll()).thenReturn(directoryOf(a, b));
        assertEquals(2, DentistManagementController.count());
    }

    @Test
    public void findById_matchesCaseInsensitively() {
        DentistModel d = new DentistModel();
        d.setFullName("Dr. Smith");
        d.setDentistId("D101");
        when(mockDentistDao.findAll()).thenReturn(directoryOf(d));

        assertSame(d, DentistManagementController.findById("d101"));
    }

    @Test
    public void findById_noMatch_returnsNull() {
        when(mockDentistDao.findAll()).thenReturn(Collections.emptyMap());
        assertNull(DentistManagementController.findById("D999"));
    }

    @Test
    public void findById_nullOrBlankId_returnsNullWithoutQueryingDao() {
        assertNull(DentistManagementController.findById(null));
        assertNull(DentistManagementController.findById("   "));
        verifyNoInteractions(mockDentistDao);
    }

    @Test
    public void registerDentist_validModel_delegatesToUpsert() {
        DentistModel d = new DentistModel();
        d.setFullName("Dr. Smith");
        when(mockDentistDao.upsert(d)).thenReturn(true);

        assertTrue(DentistManagementController.registerDentist(d));
    }

    @Test
    public void registerDentist_nullModel_returnsFalseWithoutQueryingDao() {
        assertFalse(DentistManagementController.registerDentist(null));
        verifyNoInteractions(mockDentistDao);
    }

    @Test
    public void registerDentist_blankFullName_returnsFalseWithoutQueryingDao() {
        DentistModel d = new DentistModel();
        d.setFullName("   ");
        assertFalse(DentistManagementController.registerDentist(d));
        verifyNoInteractions(mockDentistDao);
    }

    @Test
    public void deleteDentist_delegatesToDaoAndReturnsItsResult() {
        when(mockDentistDao.delete("D101")).thenReturn(true);
        assertTrue(DentistManagementController.deleteDentist("D101"));

        when(mockDentistDao.delete("D102")).thenReturn(false); // e.g. rejected by a RESTRICT foreign key
        assertFalse(DentistManagementController.deleteDentist("D102"));
    }

    @Test
    public void nextDentistId_delegatesToDao() {
        when(mockDentistDao.nextDentistId()).thenReturn("D104");
        assertEquals("D104", DentistManagementController.nextDentistId());
    }

    // ── createLoginForDentist ────────────────────────────────────────────────

    @Test
    public void createLoginForDentist_blankUsername_returnsFailedWithoutQueryingDao() {
        assertEquals(DentistManagementController.LoginOutcome.FAILED,
                DentistManagementController.createLoginForDentist("D101", "  "));
        verifyNoInteractions(mockDentistDao);
        verifyNoInteractions(mockUserDao);
    }

    @Test
    public void createLoginForDentist_dentistNotFound_returnsNotFound() {
        when(mockDentistDao.findAll()).thenReturn(Collections.emptyMap());
        assertEquals(DentistManagementController.LoginOutcome.NOT_FOUND,
                DentistManagementController.createLoginForDentist("D999", "newuser"));
    }

    @Test
    public void createLoginForDentist_usernameAlreadyTaken_returnsUsernameTaken() {
        DentistModel d = new DentistModel();
        d.setFullName("Dr. Smith");
        d.setDentistId("D101");
        when(mockDentistDao.findAll()).thenReturn(directoryOf(d));
        when(mockUserDao.getStatus("taken")).thenReturn("APPROVED");

        assertEquals(DentistManagementController.LoginOutcome.USERNAME_TAKEN,
                DentistManagementController.createLoginForDentist("D101", "taken"));
    }

    @Test
    public void createLoginForDentist_success_insertsPendingLoginAndReturnsSuccess() {
        DentistModel d = new DentistModel();
        d.setFullName("Dr. Smith");
        d.setDentistId("D101");
        d.setEmail("smith@example.com");
        d.setNic("901234567V");
        d.setMobileNo("0771234567");
        when(mockDentistDao.findAll()).thenReturn(directoryOf(d));
        when(mockUserDao.getStatus("newuser")).thenReturn(null);
        when(mockUserDao.insertPendingDentistLogin("newuser", "Dr. Smith", "smith@example.com", "901234567V", "0771234567", "D101"))
                .thenReturn(true);

        assertEquals(DentistManagementController.LoginOutcome.SUCCESS,
                DentistManagementController.createLoginForDentist("D101", "newuser"));
    }

    @Test
    public void createLoginForDentist_daoInsertFails_returnsFailed() {
        DentistModel d = new DentistModel();
        d.setFullName("Dr. Smith");
        d.setDentistId("D101");
        when(mockDentistDao.findAll()).thenReturn(directoryOf(d));
        when(mockUserDao.getStatus("newuser")).thenReturn(null);
        when(mockUserDao.insertPendingDentistLogin(any(), any(), any(), any(), any(), any())).thenReturn(false);

        assertEquals(DentistManagementController.LoginOutcome.FAILED,
                DentistManagementController.createLoginForDentist("D101", "newuser"));
    }

    // ── Instance model construction (no DAO/UI involved) ────────────────────

    @Test
    public void defaultConstructor_startsWithAFreshEmptyModel() {
        assertNotNull(new DentistManagementController().getDentistModel());
    }

    @Test
    public void modelConstructor_keepsTheGivenModelInstance() {
        DentistModel existing = new DentistModel();
        existing.setDentistId("D99");
        assertSame(existing, new DentistManagementController(existing).getDentistModel());
    }

    @Test
    public void freshController_hasNoPendingUsernameYet() {
        assertNull(new DentistManagementController().getPendingUsername());
    }
}
