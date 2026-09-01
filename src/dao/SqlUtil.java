package dao;

import java.sql.Date;
import java.sql.Time;

/**
 * Null-safe String &lt;-&gt; java.sql.Date/Time conversion, shared by every
 * DAO in this package.
 *
 * Every model class in this app (DentistModel, LeaveRequestModel, ...)
 * stores dates/times as plain Strings — that's a deliberate choice already
 * made throughout the view layer (DateTimePicker writes "yyyy-MM-dd" /
 * "HH:mm" text into a JTextField, nothing fancier), so DAOs convert at the
 * boundary instead of pushing java.time types up into the model/view layers.
 * Blank or unparseable input becomes SQL NULL rather than throwing — DAOs
 * deal with the same "the dentist hasn't filled this field in yet" reality
 * the rest of the app already tolerates.
 *
 * @author oveen
 */
final class SqlUtil {

    private SqlUtil() {
    }

    /** "yyyy-MM-dd" (or blank) -> java.sql.Date, or null if blank/unparseable. */
    static Date parseDate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(text.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** java.sql.Date -> "yyyy-MM-dd", or "" if null. */
    static String formatDate(Date date) {
        return date == null ? "" : date.toString();
    }

    /** "HH:mm" (or blank) -> java.sql.Time, or null if blank/unparseable. */
    static Time parseTime(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            String t = text.trim();
            return Time.valueOf(t.length() == 5 ? t + ":00" : t); // "09:00" -> "09:00:00"
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** java.sql.Time -> "HH:mm", or "" if null. */
    static String formatTime(Time time) {
        return time == null ? "" : time.toString().substring(0, 5);
    }

    /** Empty string -> null (so optional text columns store SQL NULL instead of ""), otherwise unchanged. */
    static String blankToNull(String text) {
        return (text == null || text.trim().isEmpty()) ? null : text.trim();
    }

    /**
     * "yyyy" -> Integer year, or null if blank/unparseable — for YEAR
     * columns. Also tolerates a stray "yyyy-MM-dd" shape (MySQL Connector/J's
     * default {@code yearIsDateType} setting makes {@code ResultSet.getString()}
     * on a YEAR column return e.g. "2020-01-01" instead of plain "2020"), so
     * a value read back out of the DB always round-trips cleanly on the next
     * write instead of getting rejected as truncated.
     */
    static Integer parseYear(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String t = text.trim();
        int dash = t.indexOf('-');
        if (dash > 0) {
            t = t.substring(0, dash);
        }
        try {
            return Integer.valueOf(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** null -> "" (so callers always get a non-null String back), otherwise unchanged. */
    static String nullToBlank(String text) {
        return text == null ? "" : text;
    }
}
