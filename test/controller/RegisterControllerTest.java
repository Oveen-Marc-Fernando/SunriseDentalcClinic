package controller;

import dao.UserDAO;
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
 * Unit tests for {@link RegisterController}, faking out the database via
 * Mockito's {@code mockConstruction} instead of a real connection.
 *
 * {@code RegisterController} holds its {@code UserDAO} in a
 * {@code private static final} field — created exactly once, the moment the
 * class is first touched by any code in this JVM. Ant's JUnit runner forks a
 * fresh JVM per test <em>class</em>, so as long as {@link #mockUserDaoConstruction()}
 * (which opens the {@code mockConstruction} scope) runs before any
 * {@code @Test} method here ever calls {@code RegisterController}, that
 * first call's static initialization happens *inside* the mock scope and
 * gets captured — every test after that reuses (and {@link Mockito#reset resets})
 * that same captured mock rather than touching a real database.
 *
 * The {@code email} argument is always left blank in these tests —
 * {@code RegisterController} genuinely sends a real confirmation email via
 * Gmail (see {@code mail/mail.properties}) on the success path, and a blank
 * email is the one input that short-circuits that before any network call.
 */
public class RegisterControllerTest {

    private static MockedConstruction<UserDAO> construction;
    private static UserDAO mockUserDao;

    @BeforeClass
    public static void mockUserDaoConstruction() {
        construction = mockConstruction(UserDAO.class);
        // RegisterController's UserDAO field only gets constructed the first
        // time anything actively calls into the class — force that now,
        // inside this still-open mock scope, so mockUserDao is real and
        // ready before any @Before/@Test below runs. The return value here
        // is irrelevant (an unstubbed mock's methods just return defaults).
        RegisterController.register("warmup", "pw", "Warmup", "", "nic0", "000", "Patient");
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
    public void register_patientRole_autoApproves_viaInsertApproved() {
        when(mockUserDao.insertApproved(eq("patient1"), eq("pw"), eq("Pat"), anyString(), anyString(), anyString(), eq(User.Role.PATIENT)))
                .thenReturn(true);

        RegisterController.RegisterOutcome outcome =
                RegisterController.register("patient1", "pw", "Pat", "", "nic1", "0771234567", "Patient");

        assertEquals(RegisterController.RegisterOutcome.SUCCESS, outcome);
        verify(mockUserDao).insertApproved(eq("patient1"), eq("pw"), eq("Pat"), anyString(), anyString(), anyString(), eq(User.Role.PATIENT));
        verify(mockUserDao, never()).insertPending(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void register_dentistRole_notAutoApproved_viaInsertPending() {
        when(mockUserDao.insertPending(eq("dentist1"), eq("pw"), eq("Dent"), anyString(), anyString(), anyString(), eq(User.Role.DENTIST)))
                .thenReturn(true);

        RegisterController.RegisterOutcome outcome =
                RegisterController.register("dentist1", "pw", "Dent", "", "nic2", "0771234567", "Dentist");

        assertEquals(RegisterController.RegisterOutcome.SUCCESS, outcome);
        verify(mockUserDao).insertPending(eq("dentist1"), eq("pw"), eq("Dent"), anyString(), anyString(), anyString(), eq(User.Role.DENTIST));
        verify(mockUserDao, never()).insertApproved(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void register_officeStaffRole_notAutoApproved_viaInsertPending() {
        when(mockUserDao.insertPending(eq("staff1"), anyString(), anyString(), anyString(), anyString(), anyString(), eq(User.Role.OFFICE_STAFF)))
                .thenReturn(true);

        RegisterController.RegisterOutcome outcome =
                RegisterController.register("staff1", "pw", "Staff", "", "nic3", "0771234567", "Office Staff");

        assertEquals(RegisterController.RegisterOutcome.SUCCESS, outcome);
    }

    @Test
    public void register_unrecognizedRoleLabel_mapsToUnknown_stillGoesThroughInsertPending() {
        when(mockUserDao.insertPending(eq("user1"), anyString(), anyString(), anyString(), anyString(), anyString(), eq(User.Role.UNKNOWN)))
                .thenReturn(true);

        RegisterController.RegisterOutcome outcome =
                RegisterController.register("user1", "pw", "User", "", "nic4", "0771234567", "Not A Real Role");

        assertEquals(RegisterController.RegisterOutcome.SUCCESS, outcome);
    }

    @Test
    public void register_insertFails_usernameAlreadyExists_returnsUsernameTaken() {
        when(mockUserDao.insertPending(eq("taken"), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(false);
        when(mockUserDao.getStatus("taken")).thenReturn("APPROVED"); // username exists

        RegisterController.RegisterOutcome outcome =
                RegisterController.register("taken", "pw", "Someone", "", "nic5", "0771234567", "Dentist");

        assertEquals(RegisterController.RegisterOutcome.USERNAME_TAKEN, outcome);
    }

    @Test
    public void register_insertFails_noExistingUsername_returnsFailed() {
        when(mockUserDao.insertPending(eq("newuser"), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(false);
        when(mockUserDao.getStatus("newuser")).thenReturn(null); // real DB error, not a duplicate

        RegisterController.RegisterOutcome outcome =
                RegisterController.register("newuser", "pw", "Someone", "", "nic6", "0771234567", "Dentist");

        assertEquals(RegisterController.RegisterOutcome.FAILED, outcome);
    }
}
