package controller;

import dao.ApprovalDAO;
import dao.AppointmentDAO;
import dao.BillingDAO;
import dao.DentistDAO;
import dao.InventoryDAO;
import dao.PatientDAO;
import dao.UserDAO;
import java.util.Collections;
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
 * Unit tests for {@link OfficeStaffController}. Its dashboard badge counts
 * each delegate through a specific Grid view's static {@code getRecordCount()}
 * to a specific controller — real wiring worth verifying, since a copy-paste
 * mistake here (e.g. {@code getTotalPatients()} accidentally reading the
 * dentist count) is exactly the kind of bug this catches. Every DAO those
 * controllers touch is faked via {@code mockConstruction} (see
 * RegisterControllerTest); the two whose delegation chain dereferences an
 * unstubbed mock's return value ({@code DentistDAO.findAll()} is a Map,
 * {@code InventoryDAO.findAll()} is a List) are given a safe empty default
 * during warmup so construction itself doesn't NPE.
 *
 * {@code openXxx()} action methods are not covered — each one constructs a
 * real Swing view ({@code new view.OS_DM_Grid().setVisible(true)}), which is
 * UI behavior, not something a unit test should trigger.
 */
public class OfficeStaffControllerTest {

    private static MockedConstruction<DentistDAO> dentistConstruction;
    private static MockedConstruction<PatientDAO> patientConstruction;
    private static MockedConstruction<AppointmentDAO> appointmentConstruction;
    private static MockedConstruction<BillingDAO> billingConstruction;
    private static MockedConstruction<ApprovalDAO> approvalConstruction;
    private static MockedConstruction<InventoryDAO> inventoryConstruction;
    private static MockedConstruction<UserDAO> userConstruction;

    private static DentistDAO mockDentistDao;
    private static PatientDAO mockPatientDao;
    private static AppointmentDAO mockAppointmentDao;
    private static BillingDAO mockBillingDao;
    private static ApprovalDAO mockApprovalDao;
    private static InventoryDAO mockInventoryDao;

    private static java.util.function.Consumer<UserDAO> onNextUserDaoConstructed = dao -> {};

    @BeforeClass
    public static void mockAllDaoConstruction() {
        dentistConstruction = mockConstruction(DentistDAO.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(Collections.emptyMap()));
        patientConstruction = mockConstruction(PatientDAO.class);
        appointmentConstruction = mockConstruction(AppointmentDAO.class);
        billingConstruction = mockConstruction(BillingDAO.class);
        approvalConstruction = mockConstruction(ApprovalDAO.class);
        inventoryConstruction = mockConstruction(InventoryDAO.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(Collections.emptyList()));
        userConstruction = mockConstruction(UserDAO.class, (mock, context) -> onNextUserDaoConstructed.accept(mock));

        // One call per controller, each just to force its static DAO field's
        // construction now, inside these still-open scopes. InventoryManagementController
        // is warmed up BEFORE BillingManagementController deliberately —
        // BillingManagementController has its own separate InventoryDAO field
        // (for stock deduction), so calling it first would make *that* one
        // index 0 in inventoryConstruction.constructed() instead of
        // InventoryManagementController's own.
        DentistManagementController.count();
        PatientManagementController.count();
        AppointmentManagementController.countAll();
        InventoryManagementController.count();
        BillingManagementController.count();
        ApprovalController.getRecordCount();

        mockDentistDao = dentistConstruction.constructed().get(0);
        mockPatientDao = patientConstruction.constructed().get(0);
        mockAppointmentDao = appointmentConstruction.constructed().get(0);
        mockBillingDao = billingConstruction.constructed().get(0);
        mockApprovalDao = approvalConstruction.constructed().get(0);
        mockInventoryDao = inventoryConstruction.constructed().get(0);
    }

    @AfterClass
    public static void closeConstructionMocks() {
        dentistConstruction.close();
        patientConstruction.close();
        appointmentConstruction.close();
        billingConstruction.close();
        approvalConstruction.close();
        inventoryConstruction.close();
        userConstruction.close();
    }

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Before
    public void resetMocks() {
        reset(mockDentistDao, mockPatientDao, mockAppointmentDao, mockBillingDao, mockApprovalDao, mockInventoryDao);
        when(mockDentistDao.findAll()).thenReturn(Collections.emptyMap()); // restore the safe default reset() just cleared
        when(mockInventoryDao.findAll()).thenReturn(Collections.emptyList());
        for (UserDAO u : userConstruction.constructed()) {
            reset(u);
        }
        onNextUserDaoConstructed = dao -> {};
    }

    private static User officeStaff(String username, String fullName) {
        return new User(username, "pw", fullName, User.Role.OFFICE_STAFF);
    }

    // ── Pure display logic ───────────────────────────────────────────────────

    @Test
    public void getWelcomeMessage_isAlwaysTheSameGenericGreeting() {
        // Unlike PatientController's/DentistController's own welcome message,
        // this one deliberately doesn't include the name.
        assertEquals("Hii Office Staff, Welcome!!", new OfficeStaffController(officeStaff("staff1", "Jane")).getWelcomeMessage());
    }

    @Test
    public void getDisplayName_nullUser_fallsBackToGenericOfficeStaff() {
        assertEquals("Office Staff", new OfficeStaffController(null).getDisplayName());
    }

    @Test
    public void getUsername_nullUser_returnsEmptyString() {
        assertEquals("", new OfficeStaffController(null).getUsername());
    }

    // ── Dashboard badge counts — each is a specific Grid's own count ────────

    @Test
    public void getTotalDentists_readsFromDentistDirectorySize() {
        model.DentistModel d = new model.DentistModel();
        d.setFullName("Dr. Smith");
        java.util.Map<String, model.DentistModel> directory = new java.util.LinkedHashMap<>();
        directory.put("Dr. Smith", d);
        when(mockDentistDao.findAll()).thenReturn(directory);

        assertEquals(1, new OfficeStaffController(officeStaff("staff1", "Jane")).getTotalDentists());
    }

    @Test
    public void getTotalPatients_readsFromPatientCount() {
        when(mockPatientDao.count()).thenReturn(128);
        assertEquals(128, new OfficeStaffController(officeStaff("staff1", "Jane")).getTotalPatients());
    }

    @Test
    public void getPendingAppointments_readsFromAppointmentCount() {
        when(mockAppointmentDao.countAll()).thenReturn(14);
        assertEquals(14, new OfficeStaffController(officeStaff("staff1", "Jane")).getPendingAppointments());
    }

    @Test
    public void getPendingBillings_readsFromBillingCount() {
        when(mockBillingDao.count()).thenReturn(7);
        assertEquals(7, new OfficeStaffController(officeStaff("staff1", "Jane")).getPendingBillings());
    }

    @Test
    public void getPendingApprovals_readsFromApprovalCount() {
        when(mockApprovalDao.count()).thenReturn(3);
        assertEquals(3, new OfficeStaffController(officeStaff("staff1", "Jane")).getPendingApprovals());
    }

    @Test
    public void getOpenHelpDesk_readsFromInventoryListSize() {
        when(mockInventoryDao.findAll()).thenReturn(java.util.Arrays.asList(new model.InventoryModel(), new model.InventoryModel()));
        assertEquals(2, new OfficeStaffController(officeStaff("staff1", "Jane")).getOpenHelpDesk());
    }

    // ── saveProfileChanges ───────────────────────────────────────────────────

    @Test
    public void saveProfileChanges_bothBlank_bothReportedUnchanged() {
        OfficeStaffController c = new OfficeStaffController(officeStaff("staff1", "Jane"));
        ProfileSaveResult result = c.saveProfileChanges("", "");
        assertEquals(ProfileSaveResult.Field.UNCHANGED, result.usernameOutcome);
        assertEquals(ProfileSaveResult.Field.UNCHANGED, result.passwordOutcome);
    }

    @Test
    public void saveProfileChanges_usernameRenameSucceeds_updatesGetUsernameGoingForward() {
        OfficeStaffController c = new OfficeStaffController(officeStaff("staff1", "Jane"));
        onNextUserDaoConstructed = dao -> when(dao.renameUsername("staff1", "newname")).thenReturn(UserDAO.RenameOutcome.SUCCESS);

        ProfileSaveResult result = c.saveProfileChanges("newname", "");

        assertEquals(ProfileSaveResult.Field.SUCCESS, result.usernameOutcome);
        assertEquals("newname", c.getUsername());
    }

    @Test
    public void saveProfileChanges_usernameTaken_reportsUsernameTaken() {
        OfficeStaffController c = new OfficeStaffController(officeStaff("staff1", "Jane"));
        onNextUserDaoConstructed = dao -> when(dao.renameUsername("staff1", "taken")).thenReturn(UserDAO.RenameOutcome.TAKEN);

        ProfileSaveResult result = c.saveProfileChanges("taken", "");
        assertEquals(ProfileSaveResult.Field.USERNAME_TAKEN, result.usernameOutcome);
    }

    @Test
    public void saveProfileChanges_passwordChangeSucceeds() {
        OfficeStaffController c = new OfficeStaffController(officeStaff("staff1", "Jane"));
        onNextUserDaoConstructed = dao -> when(dao.updatePassword("staff1", "newpw123")).thenReturn(true);

        ProfileSaveResult result = c.saveProfileChanges("", "newpw123");
        assertEquals(ProfileSaveResult.Field.SUCCESS, result.passwordOutcome);
    }

    @Test
    public void saveProfileChanges_passwordChangeFails() {
        OfficeStaffController c = new OfficeStaffController(officeStaff("staff1", "Jane"));
        onNextUserDaoConstructed = dao -> when(dao.updatePassword(eq("staff1"), anyString())).thenReturn(false);

        ProfileSaveResult result = c.saveProfileChanges("", "newpw123");
        assertEquals(ProfileSaveResult.Field.FAILED, result.passwordOutcome);
    }
}
