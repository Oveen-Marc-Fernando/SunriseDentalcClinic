package dao;

import java.sql.Date;
import java.sql.Time;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/**
 * Unit tests for {@link SqlUtil} — pure String &lt;-&gt; SQL date/time
 * conversion helpers with no database dependency, so every DAO's boundary
 * conversion logic is verified directly. This test class lives in package
 * {@code dao} deliberately, matching {@link SqlUtil}'s package-private
 * access (same convention {@code test.src.dir} test roots always use to
 * reach package-private members of the class under test).
 */
public class SqlUtilTest {

    // Prints "PASS: ..." / "FAIL: ..." to the console for every test below —
    // see ConsolePrintingRule.
    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    // ── parseDate / formatDate ───────────────────────────────────────────────

    @Test
    public void parseDate_validDate_parsesCorrectly() {
        Date date = SqlUtil.parseDate("2026-08-24");
        assertEquals(Date.valueOf("2026-08-24"), date);
    }

    @Test
    public void parseDate_nullOrBlank_returnsNull() {
        assertNull(SqlUtil.parseDate(null));
        assertNull(SqlUtil.parseDate(""));
        assertNull(SqlUtil.parseDate("   "));
    }

    @Test
    public void parseDate_unparseable_returnsNullInsteadOfThrowing() {
        assertNull(SqlUtil.parseDate("not-a-date"));
    }

    @Test
    public void formatDate_nullDate_returnsEmptyString() {
        assertEquals("", SqlUtil.formatDate(null));
    }

    @Test
    public void formatDate_roundTripsWithParseDate() {
        Date date = SqlUtil.parseDate("2026-01-05");
        assertEquals("2026-01-05", SqlUtil.formatDate(date));
    }

    // ── parseTime / formatTime ───────────────────────────────────────────────

    @Test
    public void parseTime_fiveCharForm_appendsSeconds() {
        // DateTimePicker writes "HH:mm" (e.g. "09:00"), not "HH:mm:ss" —
        // this is the exact shape every real caller passes in.
        Time time = SqlUtil.parseTime("09:00");
        assertEquals(Time.valueOf("09:00:00"), time);
    }

    @Test
    public void parseTime_fullForm_parsesAsIs() {
        Time time = SqlUtil.parseTime("14:30:15");
        assertEquals(Time.valueOf("14:30:15"), time);
    }

    @Test
    public void parseTime_nullOrBlank_returnsNull() {
        assertNull(SqlUtil.parseTime(null));
        assertNull(SqlUtil.parseTime(""));
        assertNull(SqlUtil.parseTime("  "));
    }

    @Test
    public void parseTime_unparseable_returnsNullInsteadOfThrowing() {
        assertNull(SqlUtil.parseTime("not-a-time"));
    }

    @Test
    public void formatTime_nullTime_returnsEmptyString() {
        assertEquals("", SqlUtil.formatTime(null));
    }

    @Test
    public void formatTime_truncatesSecondsBackToHhMm() {
        Time time = Time.valueOf("09:05:00");
        assertEquals("09:05", SqlUtil.formatTime(time));
    }

    // ── blankToNull / nullToBlank ────────────────────────────────────────────

    @Test
    public void blankToNull_blankOrNull_becomesNull() {
        assertNull(SqlUtil.blankToNull(null));
        assertNull(SqlUtil.blankToNull(""));
        assertNull(SqlUtil.blankToNull("   "));
    }

    @Test
    public void blankToNull_realValue_isTrimmedButKept() {
        assertEquals("hello", SqlUtil.blankToNull("  hello  "));
    }

    @Test
    public void nullToBlank_nullBecomesEmptyString_otherwiseUnchanged() {
        assertEquals("", SqlUtil.nullToBlank(null));
        assertEquals("hello", SqlUtil.nullToBlank("hello"));
    }

    // ── parseYear ─────────────────────────────────────────────────────────────

    @Test
    public void parseYear_plainYear_parsesCorrectly() {
        assertEquals(Integer.valueOf(2026), SqlUtil.parseYear("2026"));
    }

    @Test
    public void parseYear_dateShapedValue_takesJustTheYear() {
        // MySQL Connector/J's yearIsDateType setting returns e.g. "2020-01-01"
        // for a YEAR column read back via getString() — must still round-trip.
        assertEquals(Integer.valueOf(2020), SqlUtil.parseYear("2020-01-01"));
    }

    @Test
    public void parseYear_nullOrBlank_returnsNull() {
        assertNull(SqlUtil.parseYear(null));
        assertNull(SqlUtil.parseYear(""));
        assertNull(SqlUtil.parseYear("   "));
    }

    @Test
    public void parseYear_unparseable_returnsNullInsteadOfThrowing() {
        assertNull(SqlUtil.parseYear("not-a-year"));
    }
}
