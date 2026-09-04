package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/**
 * Unit tests for {@link User} — a pure value object with no database or UI
 * dependency, so it's tested directly with no fakes/mocks needed.
 */
public class UserTest {

    // Prints "PASS: ..." / "FAIL: ..." to the console for every test below —
    // see ConsolePrintingRule.
    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void fullConstructor_trimsUsernameAndFullName() {
        User user = new User("  officestaff  ", "staff123", "  Jane Doe  ", User.Role.OFFICE_STAFF);
        assertEquals("officestaff", user.getUsername());
        assertEquals("Jane Doe", user.getFullName());
    }

    @Test
    public void fullConstructor_passwordIsNotTrimmed() {
        // Passwords are taken verbatim — trimming a password would silently
        // change what the user typed, unlike a username or display name.
        User user = new User("admin", "  admin789  ", "Admin", User.Role.ADMINISTRATION);
        assertEquals("  admin789  ", user.getPassword());
    }

    @Test
    public void fullConstructor_blankButNonNullFullName_becomesEmptyString() {
        // Only a null fullName falls back to the username (fullName != null
        // ? fullName.trim() : username) — a blank-but-present value is just
        // trimmed down to "", not treated the same as absent.
        User user = new User("patient", "pat000", "   ", User.Role.PATIENT);
        assertEquals("", user.getFullName());
    }

    @Test
    public void fullConstructor_nullFullName_fallsBackToUsername() {
        User user = new User("patient", "pat000", null, User.Role.PATIENT);
        assertEquals("patient", user.getFullName());
    }

    @Test
    public void fullConstructor_nullRole_defaultsToUnknown() {
        User user = new User("someone", "pw", "Someone", null);
        assertEquals(User.Role.UNKNOWN, user.getRole());
    }

    @Test(expected = NullPointerException.class)
    public void fullConstructor_nullUsername_throws() {
        new User(null, "pw", "Name", User.Role.PATIENT);
    }

    @Test(expected = NullPointerException.class)
    public void fullConstructor_nullPassword_throws() {
        new User("someone", null, "Name", User.Role.PATIENT);
    }

    @Test
    public void legacyConstructor_defaultsToUnknownRole_andFullNameEqualsUsername() {
        User user = new User("legacyuser", "1234");
        assertEquals(User.Role.UNKNOWN, user.getRole());
        assertEquals("legacyuser", user.getFullName());
    }

    @Test
    public void rolePredicates_matchOnlyTheirOwnRole() {
        User dentist = new User("dentist", "dent456", "Dentist", User.Role.DENTIST);
        assertTrue(dentist.isDentist());
        assertFalse(dentist.isOfficeStaff());
        assertFalse(dentist.isAdministration());
        assertFalse(dentist.isPatient());
    }

    @Test
    public void equals_isCaseInsensitiveOnUsername_andRequiresSameRole() {
        User a = new User("Admin", "pw1", "Name A", User.Role.ADMINISTRATION);
        User b = new User("admin", "pw2", "Name B", User.Role.ADMINISTRATION); // different password/fullName — equals ignores both
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        User differentRole = new User("admin", "pw1", "Name A", User.Role.OFFICE_STAFF);
        assertNotEquals(a, differentRole);
    }

    @Test
    public void equals_isFalseForNullAndForOtherTypes() {
        User user = new User("admin", "pw", "Admin", User.Role.ADMINISTRATION);
        assertNotEquals(user, null);
        assertNotEquals(user, "admin");
    }

    @Test
    public void toString_containsUsernameFullNameAndRole() {
        User user = new User("dentist", "dent456", "Dr. Smith", User.Role.DENTIST);
        String text = user.toString();
        assertTrue(text.contains("dentist"));
        assertTrue(text.contains("Dr. Smith"));
        assertTrue(text.contains("DENTIST"));
    }
}
