package controller;

import dao.AppointmentDAO;
import dao.BillingDAO;
import dao.DentistDAO;
import dao.InventoryDAO;
import dao.PatientDAO;
import dao.ServiceDAO;
import dao.UserDAO;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import model.AppointmentModel;
import model.BillingModel;
import model.DentistModel;
import model.PatientModel;
import model.User;
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
 * Unit tests for {@link PatientController} — a controller with real,
 * UI-free business logic, unlike the wizard controllers skipped elsewhere
 * in this test suite. It reaches into several other controllers
 * ({@link AppointmentManagementController}, {@link BillingManagementController},
 * {@link DentistManagementController}), each of which owns its own
 * {@code private static final} DAO field — so this test opens a
 * {@code mockConstruction} scope for every DAO class in that whole chain,
 * plus its own ({@code PatientDAO}, {@code UserDAO}), and does one warmup
 * call per class to force each static field's construction before any
 * {@code @Test} runs (see RegisterControllerTest for the full reasoning).
 *
 * Each warmup call is deliberately one that doesn't dereference an
 * unstubbed mock's default (null) return value, to avoid an incidental NPE
 * during warmup itself — e.g. {@code DentistManagementController.findById(null)}
 * short-circuits on its own null-guard before ever touching its DAO.
 */
public class PatientControllerTest {

    private static MockedConstruction<PatientDAO> patientConstruction;
    private static MockedConstruction<UserDAO> userConstruction;
    private static MockedConstruction<AppointmentDAO> appointmentConstruction;
    private static MockedConstruction<DentistDAO> dentistConstruction;
    private static MockedConstruction<BillingDAO> billingConstruction;
    private static MockedConstruction<ServiceDAO> serviceConstruction;
    private static MockedConstruction<InventoryDAO> inventoryConstruction;

    private static PatientDAO mockPatientDao;
    private static UserDAO mockStaticUserDao; // PatientController's own static USER_DAO field
    private static AppointmentDAO mockAppointmentDao;
    private static DentistDAO mockDentistDao;
    private static BillingDAO mockBillingDao;

    // saveProfileChanges() calls "new dao.UserDAO()" itself (a fresh instance
    // every single call, not the class's static field) — a test that wants to
    // stub *that* instance can't pre-stub-then-call, since the call makes a
    // brand new unstubbed mock every time. Instead, each such test points this
    // at a stubber to run the moment the *next* UserDAO gets constructed,
    // before saveProfileChanges() goes on to use it. Reset to a no-op after
    // every test (see resetMocks()) so it never leaks into an unrelated one.
    private static java.util.function.Consumer<UserDAO> onNextUserDaoConstructed = dao -> {};

    @BeforeClass
    public static void mockAllDaoConstruction() {
        patientConstruction = mockConstruction(PatientDAO.class);
        userConstruction = mockConstruction(UserDAO.class, (mock, context) -> onNextUserDaoConstructed.accept(mock));
        appointmentConstruction = mockConstruction(AppointmentDAO.class);
        dentistConstruction = mockConstruction(DentistDAO.class);
        billingConstruction = mockConstruction(BillingDAO.class);
        serviceConstruction = mockConstruction(ServiceDAO.class);
        inventoryConstruction = mockConstruction(InventoryDAO.class);

        AppointmentManagementController.countAll(); // constructs AppointmentDAO
        DentistManagementController.findById(null);  // constructs DentistDAO + its own UserDAO — short-circuits before touching either
        BillingManagementController.formatCurrency(0); // constructs BillingDAO + ServiceDAO + InventoryDAO — pure method, touches none of them
        new PatientController(null).mustChangePassword(); // constructs PatientController's own PatientDAO + UserDAO

        mockPatientDao = patientConstruction.constructed().get(0);
        mockStaticUserDao = userConstruction.constructed().get(0); // the first UserDAO built is DentistManagementController's; PatientController's own is the 2nd — see resetMocks()
        mockAppointmentDao = appointmentConstruction.constructed().get(0);
        mockDentistDao = dentistConstruction.constructed().get(0);
        mockBillingDao = billingConstruction.constructed().get(0);
    }

    @AfterClass
    public static void closeConstructionMocks() {
        patientConstruction.close();
        userConstruction.close();
        appointmentConstruction.close();
        dentistConstruction.close();
        billingConstruction.close();
        serviceConstruction.close();
        inventoryConstruction.close();
    }

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Before
    public void resetMocks() {
        // Every UserDAO built so far (DentistManagementController's during
        // warmup, PatientController's own right after) shares the same mock
        // maker — reset them all so no stubbing leaks between tests.
        for (UserDAO u : userConstruction.constructed()) {
            reset(u);
        }
        reset(mockPatientDao, mockAppointmentDao, mockDentistDao, mockBillingDao);
        onNextUserDaoConstructed = dao -> {};
    }

    /** PatientController's own static USER_DAO — the 2nd UserDAO ever constructed (see mockAllDaoConstruction). */
    private static UserDAO patientControllersOwnUserDao() {
        return userConstruction.constructed().get(1);
    }

    private static User patient(String username, String fullName) {
        return new User(username, "pw", fullName, User.Role.PATIENT);
    }

    // ── Pure display logic (no DAO involved) ────────────────────────────────

    @Test
    public void getWelcomeMessage_usesTheRealPatientsName() {
        PatientController c = new PatientController(patient("jdoe", "John Doe"));
        assertEquals("Hii John Doe, Welcome!!", c.getWelcomeMessage());
    }

    @Test
    public void getDisplayName_nullUser_fallsBackToGenericPatient() {
        assertEquals("Patient", new PatientController(null).getDisplayName());
    }

    @Test
    public void getUsername_beforeAnyRename_returnsTheLoggedInUsername() {
        PatientController c = new PatientController(patient("jdoe", "John Doe"));
        assertEquals("jdoe", c.getUsername());
    }

    @Test
    public void getUsername_nullUser_returnsEmptyString() {
        assertEquals("", new PatientController(null).getUsername());
    }

    @Test
    public void dashboardGetters_reflectTheFreshModelsDefaults() {
        // PatientController seeds a brand-new PatientDashboardModel — its
        // documented defaults are already covered by PatientDashboardModelTest;
        // this just confirms PatientController's getters actually expose them.
        PatientController c = new PatientController(patient("jdoe", "John Doe"));
        assertEquals(2, c.getUpcomingAppointments());
        assertEquals(8, c.getPastAppointments());
        assertEquals(125.00, c.getOutstandingBill(), 0.0001);
        assertEquals(0, c.getOpenHelpDesk());
        assertEquals(3, c.getAvailableReports());
    }

    // ── mustChangePassword / getOralHygiene ──────────────────────────────────

    @Test
    public void mustChangePassword_delegatesToDaoForThisUsername() {
        when(patientControllersOwnUserDao().getMustChangePassword("jdoe")).thenReturn(true);
        assertTrue(new PatientController(patient("jdoe", "John Doe")).mustChangePassword());
    }

    @Test
    public void getOralHygiene_linkedPatientId_looksUpByIdFirst() {
        when(patientControllersOwnUserDao().getPatientId("jdoe")).thenReturn("P1");
        PatientModel m = new PatientModel();
        m.setOralHygiene("Good");
        when(mockPatientDao.findById("P1")).thenReturn(m);

        assertEquals("Good", new PatientController(patient("jdoe", "John Doe")).getOralHygiene());
        verify(mockPatientDao, never()).findByFullName(any());
    }

    @Test
    public void getOralHygiene_noLinkedId_fallsBackToNameMatch() {
        when(patientControllersOwnUserDao().getPatientId("jdoe")).thenReturn(null);
        PatientModel m = new PatientModel();
        m.setOralHygiene("Fair");
        when(mockPatientDao.findByFullName("John Doe")).thenReturn(m);

        assertEquals("Fair", new PatientController(patient("jdoe", "John Doe")).getOralHygiene());
    }

    @Test
    public void getOralHygiene_noMatchAtAll_returnsNull() {
        when(patientControllersOwnUserDao().getPatientId("jdoe")).thenReturn(null);
        when(mockPatientDao.findByFullName("John Doe")).thenReturn(null);
        assertNull(new PatientController(patient("jdoe", "John Doe")).getOralHygiene());
    }

    @Test
    public void getOralHygiene_nullUser_returnsNullWithoutQueryingAnything() {
        assertNull(new PatientController(null).getOralHygiene());
    }

    // ── getMyAppointmentCount / getMyBillingCount ───────────────────────────

    @Test
    public void getMyAppointmentCount_countsOnlyRowsMatchingThisPatientsName() {
        AppointmentModel mine1 = new AppointmentModel();
        mine1.setPatientName("John Doe");
        AppointmentModel mine2 = new AppointmentModel();
        mine2.setPatientName("john doe"); // case-insensitive match
        AppointmentModel someoneElses = new AppointmentModel();
        someoneElses.setPatientName("Jane Smith");
        when(mockAppointmentDao.findAll()).thenReturn(java.util.Arrays.asList(mine1, mine2, someoneElses));

        assertEquals(2, new PatientController(patient("jdoe", "John Doe")).getMyAppointmentCount());
    }

    @Test
    public void getMyAppointmentCount_nullUser_returnsZeroWithoutQueryingAnything() {
        assertEquals(0, new PatientController(null).getMyAppointmentCount());
        verifyNoInteractions(mockAppointmentDao);
    }

    @Test
    public void getMyBillingCount_countsOnlyRowsMatchingThisPatientsName() {
        BillingModel mine = new BillingModel();
        mine.setPatientName("John Doe");
        BillingModel someoneElses = new BillingModel();
        someoneElses.setPatientName("Jane Smith");
        when(mockBillingDao.findAll()).thenReturn(java.util.Arrays.asList(mine, someoneElses));

        assertEquals(1, new PatientController(patient("jdoe", "John Doe")).getMyBillingCount());
    }

    // ── getAppointmentReminder ───────────────────────────────────────────────

    @Test
    public void getAppointmentReminder_nullUser_returnsGenericMessage() {
        assertEquals("No upcoming appointments on file.", new PatientController(null).getAppointmentReminder());
    }

    @Test
    public void getAppointmentReminder_noMatchingRows_invitesBooking() {
        when(mockAppointmentDao.findAll()).thenReturn(Collections.emptyList());
        assertEquals("You have no upcoming appointments — book one anytime from My Appointments!",
                new PatientController(patient("jdoe", "John Doe")).getAppointmentReminder());
    }

    @Test
    public void getAppointmentReminder_picksTheSoonestFutureNonRejectedAppointment() {
        java.time.LocalDate today = java.time.LocalDate.now();

        AppointmentModel later = new AppointmentModel();
        later.setPatientName("John Doe");
        later.setStatus("Confirmed");
        later.setDate(today.plusDays(10).toString());
        later.setTreatmentType("Cleaning");
        later.setDentistName("Smith");
        later.setTime("10:00");

        AppointmentModel soonest = new AppointmentModel();
        soonest.setPatientName("John Doe");
        soonest.setStatus("Confirmed");
        soonest.setDate(today.plusDays(2).toString());
        soonest.setTreatmentType("Whitening");
        soonest.setDentistName("Jones");
        soonest.setTime("09:00");

        AppointmentModel rejected = new AppointmentModel();
        rejected.setPatientName("John Doe");
        rejected.setStatus("Rejected");
        rejected.setDate(today.plusDays(1).toString()); // sooner than "soonest" above, but Rejected — must be skipped

        AppointmentModel past = new AppointmentModel();
        past.setPatientName("John Doe");
        past.setStatus("Confirmed");
        past.setDate(today.minusDays(1).toString()); // in the past — must be skipped

        when(mockAppointmentDao.findAll()).thenReturn(java.util.Arrays.asList(later, rejected, past, soonest));
        when(mockDentistDao.findAll()).thenReturn(new LinkedHashMap<>()); // no directory match — falls back to default "Dr" title

        String reminder = new PatientController(patient("jdoe", "John Doe")).getAppointmentReminder();

        assertTrue(reminder.contains("Whitening"));
        assertTrue(reminder.contains("Dr Jones"));
        assertTrue(reminder.contains(today.plusDays(2).toString()));
        assertFalse(reminder.contains("Cleaning"));
    }

    @Test
    public void getAppointmentReminder_dentistInDirectory_usesTheirOwnTitle() {
        java.time.LocalDate today = java.time.LocalDate.now();
        AppointmentModel a = new AppointmentModel();
        a.setPatientName("John Doe");
        a.setStatus("Confirmed");
        a.setDate(today.plusDays(1).toString());
        a.setTreatmentType("Cleaning");
        a.setDentistName("Jones");
        a.setTime("09:00");
        when(mockAppointmentDao.findAll()).thenReturn(Collections.singletonList(a));

        DentistModel jones = new DentistModel();
        jones.setFullName("Jones");
        jones.setTitle("Prof");
        Map<String, DentistModel> directory = new LinkedHashMap<>();
        directory.put("Jones", jones);
        when(mockDentistDao.findAll()).thenReturn(directory);

        String reminder = new PatientController(patient("jdoe", "John Doe")).getAppointmentReminder();
        assertTrue(reminder.contains("Prof Jones"));
    }

    // ── newProfileController ─────────────────────────────────────────────────

    @Test
    public void newProfileController_returnsAFreshController() {
        // PatientProfileController exposes no public accessor for the User it
        // was built with, so this just confirms one is actually returned.
        PatientProfileController profile = new PatientController(patient("jdoe", "John Doe")).newProfileController();
        assertNotNull(profile);
    }

    // ── saveProfileChanges ───────────────────────────────────────────────────
    // Uses a fresh, method-local "new dao.UserDAO()" each call, captured as
    // the most-recently-constructed UserDAO mock right after invoking it.

    @Test
    public void saveProfileChanges_bothBlank_bothReportedUnchanged() {
        PatientController c = new PatientController(patient("jdoe", "John Doe"));
        ProfileSaveResult result = c.saveProfileChanges("", "");

        assertEquals(ProfileSaveResult.Field.UNCHANGED, result.usernameOutcome);
        assertEquals(ProfileSaveResult.Field.UNCHANGED, result.passwordOutcome);
    }

    @Test
    public void saveProfileChanges_sameUsernameAsCurrent_reportedUnchanged_caseInsensitively() {
        PatientController c = new PatientController(patient("jdoe", "John Doe"));
        ProfileSaveResult result = c.saveProfileChanges("JDOE", "");
        assertEquals(ProfileSaveResult.Field.UNCHANGED, result.usernameOutcome);
    }

    @Test
    public void saveProfileChanges_usernameRenameSucceeds_updatesGetUsernameGoingForward() {
        PatientController c = new PatientController(patient("jdoe", "John Doe"));
        onNextUserDaoConstructed = dao -> when(dao.renameUsername("jdoe", "newname")).thenReturn(UserDAO.RenameOutcome.SUCCESS);

        ProfileSaveResult result = c.saveProfileChanges("newname", "");

        assertEquals(ProfileSaveResult.Field.SUCCESS, result.usernameOutcome);
        assertEquals("newname", c.getUsername());
    }

    @Test
    public void saveProfileChanges_usernameTaken_reportsUsernameTaken_andGetUsernameUnchanged() {
        PatientController c = new PatientController(patient("jdoe", "John Doe"));
        onNextUserDaoConstructed = dao -> when(dao.renameUsername("jdoe", "taken")).thenReturn(UserDAO.RenameOutcome.TAKEN);

        ProfileSaveResult result = c.saveProfileChanges("taken", "");

        assertEquals(ProfileSaveResult.Field.USERNAME_TAKEN, result.usernameOutcome);
        assertEquals("jdoe", c.getUsername());
    }

    @Test
    public void saveProfileChanges_passwordChangeSucceeds_clearsMustChangePasswordFlag() {
        PatientController c = new PatientController(patient("jdoe", "John Doe"));
        UserDAO[] captured = new UserDAO[1];
        onNextUserDaoConstructed = dao -> {
            captured[0] = dao;
            when(dao.updatePassword("jdoe", "newpw123")).thenReturn(true);
        };

        ProfileSaveResult result = c.saveProfileChanges("", "newpw123");

        assertEquals(ProfileSaveResult.Field.SUCCESS, result.passwordOutcome);
        verify(captured[0]).clearMustChangePassword("jdoe");
    }

    @Test
    public void saveProfileChanges_passwordChangeFails_doesNotClearMustChangePasswordFlag() {
        PatientController c = new PatientController(patient("jdoe", "John Doe"));
        UserDAO[] captured = new UserDAO[1];
        onNextUserDaoConstructed = dao -> {
            captured[0] = dao;
            when(dao.updatePassword(eq("jdoe"), anyString())).thenReturn(false);
        };

        ProfileSaveResult result = c.saveProfileChanges("", "newpw123");

        assertEquals(ProfileSaveResult.Field.FAILED, result.passwordOutcome);
        verify(captured[0], never()).clearMustChangePassword(any());
    }
}
