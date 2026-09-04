package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/** Getter/setter round-trip tests for {@link AppointmentModel} — a plain data holder. */
public class AppointmentModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void everyFieldRoundTripsThroughItsOwnGetterAndSetter() {
        AppointmentModel m = new AppointmentModel();
        m.setAppointmentId("A100");
        m.setPatientName("John Doe");
        m.setDentistName("Dr. Smith");
        m.setTreatmentType("Cleaning");
        m.setDate("2026-08-24");
        m.setTime("09:00");
        m.setStatus("Confirmed");
        m.setAddress("123 Main St");
        m.setContactNo("0771234567");
        m.setCity("Colombo");
        m.setMobileNo("0777654321");
        m.setRoomNo("R1");

        assertEquals("A100", m.getAppointmentId());
        assertEquals("John Doe", m.getPatientName());
        assertEquals("Dr. Smith", m.getDentistName());
        assertEquals("Cleaning", m.getTreatmentType());
        assertEquals("2026-08-24", m.getDate());
        assertEquals("09:00", m.getTime());
        assertEquals("Confirmed", m.getStatus());
        assertEquals("123 Main St", m.getAddress());
        assertEquals("0771234567", m.getContactNo());
        assertEquals("Colombo", m.getCity());
        assertEquals("0777654321", m.getMobileNo());
        assertEquals("R1", m.getRoomNo());
    }

    @Test
    public void toString_containsTheCoreIdentifyingFields() {
        AppointmentModel m = new AppointmentModel();
        m.setAppointmentId("A100");
        m.setPatientName("John Doe");
        m.setStatus("Confirmed");

        String text = m.toString();
        assertTrue(text.contains("A100"));
        assertTrue(text.contains("John Doe"));
        assertTrue(text.contains("Confirmed"));
    }
}
