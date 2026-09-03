package controller;

import model.LoginModel;
import model.User;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/**
 * Unit tests for {@link LogInController}'s validation/session logic, using a
 * fake {@link LoginModel} subclass instead of the real database-backed one
 * (see {@link LoginModel#authenticate}) — {@code LogInController} already
 * accepts its model via constructor injection specifically so this works
 * without touching a real database.
 */
public class LogInControllerTest {

    /** Records what it was asked to authenticate and returns a canned result — no database involved. */
    private static class FakeLoginModel extends LoginModel {
        String lastUsername;
        String lastPassword;
        User result;

        @Override
        public User authenticate(String username, String password) {
            this.lastUsername = username;
            this.lastPassword = password;
            return result;
        }
    }

    // Prints "PASS: ..." / "FAIL: ..." to the console for every test below —
    // see ConsolePrintingRule.
    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    private FakeLoginModel fakeModel;
    private LogInController controller;

    @Before
    public void setUp() {
        fakeModel = new FakeLoginModel();
        controller = new LogInController(fakeModel);
    }

    @Test
    public void attemptLogin_nullUsernameOrPassword_returnsNullWithoutCallingModel() {
        assertNull(controller.attemptLogin(null, "pw"));
        assertNull(controller.attemptLogin("user", null));
        assertNull(fakeModel.lastUsername); // never reached the model
    }

    @Test
    public void attemptLogin_blankUsernameOrPassword_returnsNullWithoutCallingModel() {
        assertNull(controller.attemptLogin("   ", "pw"));
        assertNull(controller.attemptLogin("user", ""));
        assertNull(fakeModel.lastUsername);
    }

    @Test
    public void attemptLogin_trimsUsernameBeforeDelegatingToModel() {
        fakeModel.result = new User("dentist", "dent456", "Dentist", User.Role.DENTIST);
        controller.attemptLogin("  dentist  ", "dent456");
        assertEquals("dentist", fakeModel.lastUsername);
        assertEquals("dent456", fakeModel.lastPassword); // password passed through untouched
    }

    @Test
    public void attemptLogin_success_returnsUserAndStoresAsCurrentUser() {
        User expected = new User("admin", "admin789", "Admin", User.Role.ADMINISTRATION);
        fakeModel.result = expected;

        User result = controller.attemptLogin("admin", "admin789");

        assertEquals(expected, result);
        assertEquals(expected, controller.getCurrentUser());
    }

    @Test
    public void attemptLogin_modelReturnsNull_currentUserStaysNull() {
        fakeModel.result = null;
        User result = controller.attemptLogin("baduser", "wrongpassword");
        assertNull(result);
        assertNull(controller.getCurrentUser());
    }

    @Test
    public void logout_clearsCurrentUser() {
        fakeModel.result = new User("patient", "pat000", "Patient", User.Role.PATIENT);
        controller.attemptLogin("patient", "pat000");
        assertNotNull(controller.getCurrentUser());

        controller.logout();
        assertNull(controller.getCurrentUser());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_nullModel_throws() {
        new LogInController(null);
    }

    @Test(expected = NullPointerException.class)
    public void setLoginModel_null_throws() {
        controller.setLoginModel(null);
    }

    @Test
    public void setLoginModel_swapsModelUsedByFutureLogins() {
        FakeLoginModel secondModel = new FakeLoginModel();
        secondModel.result = new User("officestaff", "staff123", "Staff", User.Role.OFFICE_STAFF);
        controller.setLoginModel(secondModel);

        controller.attemptLogin("officestaff", "staff123");

        assertEquals("officestaff", secondModel.lastUsername);
        assertNull(fakeModel.lastUsername); // the original model was never touched again
    }
}
