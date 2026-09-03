package controller;

import dao.AppointmentDAO;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import model.AppointmentModel;
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
 * Unit tests for {@link AppointmentManagementController} — the DAO-delegating
 * methods via {@code mockConstruction} (see RegisterControllerTest), and the
 * pure scheduling logic (working-days parsing, upcoming dates, hourly slots)
 * directly, since none of that touches the database at all.
 */
public class AppointmentManagementControllerTest {

    private static MockedConstruction<AppointmentDAO> construction;
    private static AppointmentDAO mockAppointmentDao;

    @BeforeClass
    public static void mockAppointmentDaoConstruction() {
        construction = mockConstruction(AppointmentDAO.class);
        AppointmentManagementController.countAll(); // forces the static field's construction now, inside this scope
        mockAppointmentDao = construction.constructed().get(0);
    }

    @AfterClass
    public static void closeConstructionMock() {
        construction.close();
    }

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Before
    public void resetMock() {
        reset(mockAppointmentDao);
    }

    // ── DAO delegation ───────────────────────────────────────────────────────

    @Test
    public void getAll_delegatesToFindAll() {
        AppointmentModel row = new AppointmentModel();
        row.setAppointmentId("A1");
        when(mockAppointmentDao.findAll()).thenReturn(Collections.singletonList(row));
        assertEquals(1, AppointmentManagementController.getAll().size());
    }

    @Test
    public void getForDentist_delegatesToFindByDentist() {
        when(mockAppointmentDao.findByDentist("Dr. Smith")).thenReturn(Collections.emptyList());
        assertTrue(AppointmentManagementController.getForDentist("Dr. Smith").isEmpty());
        verify(mockAppointmentDao).findByDentist("Dr. Smith");
    }

    @Test
    public void bookAppointment_delegatesToInsert_andReturnsItsResult() {
        LocalDate date = LocalDate.of(2026, 8, 24);
        LocalTime time = LocalTime.of(9, 0);
        when(mockAppointmentDao.insert("A1", "John Doe", "Dr. Smith", "Cleaning", date, time, "Confirmed", "123 Main St", "0771234567"))
                .thenReturn(true);

        boolean ok = AppointmentManagementController.bookAppointment(
                "A1", "John Doe", "Dr. Smith", "Cleaning", date, time, "Confirmed", "123 Main St", "0771234567");

        assertTrue(ok);
    }

    @Test
    public void delete_delegatesToDaoAndReturnsItsResult() {
        when(mockAppointmentDao.delete("A1")).thenReturn(true);
        assertTrue(AppointmentManagementController.delete("A1"));

        when(mockAppointmentDao.delete("A2")).thenReturn(false);
        assertFalse(AppointmentManagementController.delete("A2"));
    }

    @Test
    public void updateStatus_delegatesToDao() {
        AppointmentManagementController.updateStatus("A1", "Cancelled");
        verify(mockAppointmentDao).updateStatus("A1", "Cancelled");
    }

    // ── parseWorkingDays ─────────────────────────────────────────────────────

    @Test
    public void parseWorkingDays_commaSeparatedFullNames() {
        Set<DayOfWeek> days = AppointmentManagementController.parseWorkingDays("Monday, Wednesday, Friday");
        assertEquals(3, days.size());
        assertTrue(days.contains(DayOfWeek.MONDAY));
        assertTrue(days.contains(DayOfWeek.WEDNESDAY));
        assertTrue(days.contains(DayOfWeek.FRIDAY));
        assertFalse(days.contains(DayOfWeek.TUESDAY));
    }

    @Test
    public void parseWorkingDays_range_expandsEveryDayInBetween() {
        Set<DayOfWeek> days = AppointmentManagementController.parseWorkingDays("Mon-Fri");
        assertEquals(5, days.size());
        assertTrue(days.containsAll(java.util.Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)));
        assertFalse(days.contains(DayOfWeek.SATURDAY));
        assertFalse(days.contains(DayOfWeek.SUNDAY));
    }

    @Test
    public void parseWorkingDays_rangeWrappingAcrossSunday() {
        // Sat-Mon must wrap around the week boundary rather than stopping dead.
        Set<DayOfWeek> days = AppointmentManagementController.parseWorkingDays("Sat-Mon");
        assertTrue(days.contains(DayOfWeek.SATURDAY));
        assertTrue(days.contains(DayOfWeek.SUNDAY));
        assertTrue(days.contains(DayOfWeek.MONDAY));
        assertFalse(days.contains(DayOfWeek.WEDNESDAY));
    }

    @Test
    public void parseWorkingDays_nullOrBlank_returnsEmptySet() {
        assertTrue(AppointmentManagementController.parseWorkingDays(null).isEmpty());
        assertTrue(AppointmentManagementController.parseWorkingDays("   ").isEmpty());
    }

    @Test
    public void parseWorkingDays_gibberish_returnsEmptySet() {
        assertTrue(AppointmentManagementController.parseWorkingDays("blah blah nonsense").isEmpty());
    }

    @Test
    public void parseWorkingDays_abbreviations_stillRecognized() {
        Set<DayOfWeek> days = AppointmentManagementController.parseWorkingDays("mon, wed, fri");
        assertEquals(3, days.size());
    }

    // ── upcomingWorkingDates ─────────────────────────────────────────────────

    @Test
    public void upcomingWorkingDates_returnsExactlyTheRequestedCount_allOnAWorkingDay() {
        List<LocalDate> dates = AppointmentManagementController.upcomingWorkingDates("Mon-Fri", 5);
        assertEquals(5, dates.size());
        for (LocalDate d : dates) {
            assertTrue(d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY);
        }
    }

    @Test
    public void upcomingWorkingDates_datesAreInAscendingOrder() {
        List<LocalDate> dates = AppointmentManagementController.upcomingWorkingDates("Mon-Fri", 5);
        for (int i = 1; i < dates.size(); i++) {
            assertTrue(dates.get(i).isAfter(dates.get(i - 1)));
        }
    }

    @Test
    public void upcomingWorkingDates_noRecognizedDays_returnsEmpty() {
        assertTrue(AppointmentManagementController.upcomingWorkingDates("", 5).isEmpty());
    }

    // ── hourlySlots ──────────────────────────────────────────────────────────

    @Test
    public void hourlySlots_noBreak_everyHourFromStartToEnd() {
        List<LocalTime> slots = AppointmentManagementController.hourlySlots("09:00", "12:00", "");
        assertEquals(java.util.Arrays.asList(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0)), slots);
    }

    @Test
    public void hourlySlots_withBreak_skipsSlotsInsideBreakWindow() {
        List<LocalTime> slots = AppointmentManagementController.hourlySlots("09:00", "17:00", "12:00-13:00");
        assertFalse(slots.contains(LocalTime.of(12, 0)));
        assertTrue(slots.contains(LocalTime.of(11, 0)));
        assertTrue(slots.contains(LocalTime.of(13, 0)));
    }

    @Test
    public void hourlySlots_startNotBeforeEnd_returnsEmpty() {
        assertTrue(AppointmentManagementController.hourlySlots("17:00", "09:00", "").isEmpty());
        assertTrue(AppointmentManagementController.hourlySlots("09:00", "09:00", "").isEmpty());
    }

    @Test
    public void hourlySlots_unparseableTimes_returnsEmptyInsteadOfThrowing() {
        assertTrue(AppointmentManagementController.hourlySlots("not-a-time", "17:00", "").isEmpty());
        assertTrue(AppointmentManagementController.hourlySlots(null, "17:00", "").isEmpty());
    }
}
