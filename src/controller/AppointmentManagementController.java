package controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.AppointmentModel;

/**
 * Controller for Appointment Management — the single place every Appointment
 * screen (office-wide OS_AM_Grid, the OS_AM_1/OS_AM_2 "Add Appointment"
 * wizard, the dentist's own D_APP_Grid, and D_RS_Leave's conflict check)
 * talks to instead of reaching into {@link dao.AppointmentDAO} directly.
 *
 * Also owns the scheduling business logic that used to live inside the
 * OS_AM_2 View (working-days text → upcoming calendar dates, start/end/break
 * time → open hourly slots) — that's Controller-layer logic, not View logic.
 *
 * @author oveen
 */
public class AppointmentManagementController {

    private static final dao.AppointmentDAO APPOINTMENT_DAO = new dao.AppointmentDAO();

    // ── Reads ────────────────────────────────────────────────────────────────

    /** Every appointment on record — OS_AM_Grid's office-wide view. */
    public static List<AppointmentModel> getAll() {
        return APPOINTMENT_DAO.findAll();
    }

    /** Just this dentist's own appointments — D_APP_Grid's "My Appointments" view. */
    public static List<AppointmentModel> getForDentist(String dentistName) {
        return APPOINTMENT_DAO.findByDentist(dentistName);
    }

    /** Single appointment by ID, or null — Billing Step 1's auto-fill. */
    public static AppointmentModel getById(String appointmentId) {
        return APPOINTMENT_DAO.findById(appointmentId);
    }

    public static int countAll() {
        return APPOINTMENT_DAO.countAll();
    }

    public static int countForDentist(String dentistName) {
        return APPOINTMENT_DAO.countForDentist(dentistName);
    }

    /** True if this dentist has any non-Rejected appointment on the given date (any time). */
    public static boolean hasAppointmentOnDate(String dentistName, LocalDate date) {
        return APPOINTMENT_DAO.hasAppointmentOnDate(dentistName, date);
    }

    /** True if this dentist already has a non-Rejected appointment at this exact date+time. */
    public static boolean isBooked(String dentistName, LocalDate date, LocalTime time) {
        return APPOINTMENT_DAO.isBooked(dentistName, date, time);
    }

    public static String nextAppointmentId() {
        return APPOINTMENT_DAO.nextAppointmentId();
    }

    // ── Writes ───────────────────────────────────────────────────────────────

    /**
     * Books a new appointment — OS_AM_2's "Save" button.
     * {@code address}/{@code contactNo} are a snapshot of the patient's own
     * details at the moment of booking, stored permanently with this
     * appointment. Returns {@code true} only if it was actually persisted.
     */
    public static boolean bookAppointment(String appointmentId, String patientName, String dentistName,
            String treatmentType, LocalDate date, LocalTime time, String status, String address, String contactNo) {
        return APPOINTMENT_DAO.insert(appointmentId, patientName, dentistName, treatmentType, date, time, status,
                address, contactNo);
    }

    /** Updates just the status — D_APP_Grid's clickable status pill. */
    public static void updateStatus(String appointmentId, String newStatus) {
        APPOINTMENT_DAO.updateStatus(appointmentId, newStatus);
    }

    /** Updates every real column on this appointment — OS_AM_Grid's Edit popup. */
    public static boolean updateAppointment(String appointmentId, String patientName, String dentistName,
            String treatmentType, LocalDate date, LocalTime time, String status) {
        return APPOINTMENT_DAO.update(appointmentId, patientName, dentistName, treatmentType, date, time, status);
    }

    /** Deletes an appointment by Appointment ID — OS_AM_Grid's Delete button. */
    public static boolean delete(String appointmentId) {
        return APPOINTMENT_DAO.delete(appointmentId);
    }

    // =========================================================================
    // Scheduling logic — moved here from OS_AM_2 (was View-layer business
    // logic; belongs in the Controller). Working Days text -> upcoming
    // calendar dates.
    // =========================================================================

    private static final DayOfWeek[] DAY_VALUES = {
        DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
    };
    private static final String[] DAY_ABBR = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};

    private static DayOfWeek matchDay(String token) {
        String t = token.trim().toLowerCase();
        if (t.length() < 3) {
            return null;
        }
        String abbr = t.substring(0, 3);
        for (int i = 0; i < DAY_ABBR.length; i++) {
            if (DAY_ABBR[i].equals(abbr)) {
                return DAY_VALUES[i];
            }
        }
        return null;
    }

    /** Parses free text like "Monday, Wednesday, Friday" or "Mon-Fri" into a day set. */
    public static Set<DayOfWeek> parseWorkingDays(String text) {
        Set<DayOfWeek> days = new LinkedHashSet<>();
        if (text == null || text.trim().isEmpty()) {
            return days;
        }
        Matcher range = Pattern.compile("([A-Za-z]+)\\s*-\\s*([A-Za-z]+)").matcher(text);
        if (range.find()) {
            DayOfWeek from = matchDay(range.group(1));
            DayOfWeek to = matchDay(range.group(2));
            if (from != null && to != null) {
                int i = from.getValue();
                int end = to.getValue();
                while (true) {
                    days.add(DayOfWeek.of(i));
                    if (i == end) {
                        break;
                    }
                    i = (i % 7) + 1;
                }
            }
        }
        for (String token : text.split("[^A-Za-z]+")) {
            DayOfWeek d = matchDay(token);
            if (d != null) {
                days.add(d);
            }
        }
        return days;
    }

    /** Next {@code count} calendar dates (starting today) that fall on one of the given weekdays. */
    public static List<LocalDate> upcomingWorkingDates(String workingDaysText, int count) {
        List<LocalDate> dates = new ArrayList<>();
        Set<DayOfWeek> days = parseWorkingDays(workingDaysText);
        if (days.isEmpty()) {
            return dates;
        }
        LocalDate d = LocalDate.now();
        int guard = 0;
        while (dates.size() < count && guard < 60) { // 60-day lookahead safety cap
            if (days.contains(d.getDayOfWeek())) {
                dates.add(d);
            }
            d = d.plusDays(1);
            guard++;
        }
        return dates;
    }

    // =========================================================================
    // Start/End/Break time -> hourly slots
    // =========================================================================

    private static final java.time.format.DateTimeFormatter TIME_PARSE =
            java.time.format.DateTimeFormatter.ofPattern("HH:mm");

    /** Hourly slots between start and end time (both "HH:mm"), skipping a parseable break window. */
    public static List<LocalTime> hourlySlots(String startText, String endText, String breakText) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime start = parseTimeOrNull(startText);
        LocalTime end = parseTimeOrNull(endText);
        if (start == null || end == null || !start.isBefore(end)) {
            return slots;
        }
        LocalTime[] breakRange = parseTimeRangeOrNull(breakText);

        LocalTime t = start;
        while (t.isBefore(end)) {
            boolean onBreak = breakRange != null
                    && !t.isBefore(breakRange[0]) && t.isBefore(breakRange[1]);
            if (!onBreak) {
                slots.add(t);
            }
            t = t.plusHours(1);
        }
        return slots;
    }

    private static LocalTime parseTimeOrNull(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(text.trim(), TIME_PARSE);
        } catch (Exception ex) {
            return null;
        }
    }

    private static LocalTime[] parseTimeRangeOrNull(String text) {
        if (text == null) {
            return null;
        }
        Matcher m = Pattern.compile("(\\d{1,2}:\\d{2})\\s*-\\s*(\\d{1,2}:\\d{2})").matcher(text);
        if (!m.find()) {
            return null;
        }
        LocalTime from = parseTimeOrNull(m.group(1));
        LocalTime to = parseTimeOrNull(m.group(2));
        if (from == null || to == null) {
            return null;
        }
        return new LocalTime[]{from, to};
    }
}
