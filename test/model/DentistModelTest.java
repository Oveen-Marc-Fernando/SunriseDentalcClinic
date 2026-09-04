package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/** Getter/setter round-trip tests for {@link DentistModel} — a plain 5-step wizard data holder. */
public class DentistModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void step1Fields_roundTrip() {
        DentistModel m = new DentistModel();
        m.setTitle("Dr");
        m.setGender("Female");
        m.setFullName("Jane Smith");
        m.setDentistId("D1");
        m.setDob("1990-01-01");
        m.setNic("901234567V");

        assertEquals("Dr", m.getTitle());
        assertEquals("Female", m.getGender());
        assertEquals("Jane Smith", m.getFullName());
        assertEquals("D1", m.getDentistId());
        assertEquals("1990-01-01", m.getDob());
        assertEquals("901234567V", m.getNic());
    }

    @Test
    public void step2Fields_roundTrip() {
        DentistModel m = new DentistModel();
        m.setSlmcNo("SLMC123");
        m.setQualification("BDS");
        m.setUniversity("University of Colombo");
        m.setGraduationYear("2015");
        m.setSpecialization("Orthodontics");
        m.setExperience("10 years");
        m.setLicenseStatus("Active");

        assertEquals("SLMC123", m.getSlmcNo());
        assertEquals("BDS", m.getQualification());
        assertEquals("University of Colombo", m.getUniversity());
        assertEquals("2015", m.getGraduationYear());
        assertEquals("Orthodontics", m.getSpecialization());
        assertEquals("10 years", m.getExperience());
        assertEquals("Active", m.getLicenseStatus());
    }

    @Test
    public void step3Fields_roundTrip() {
        DentistModel m = new DentistModel();
        m.setMobileNo("0771234567");
        m.setEmail("jane@example.com");
        m.setAddress("123 Main St");
        m.setEmergencyNo("0777654321");

        assertEquals("0771234567", m.getMobileNo());
        assertEquals("jane@example.com", m.getEmail());
        assertEquals("123 Main St", m.getAddress());
        assertEquals("0777654321", m.getEmergencyNo());
    }

    @Test
    public void step4Fields_roundTrip() {
        DentistModel m = new DentistModel();
        m.setJoinedDate("2020-06-01");
        m.setEmploymentType("Full Time");
        m.setConsultationFee("3000");
        m.setEmploymentStatus("Active");

        assertEquals("2020-06-01", m.getJoinedDate());
        assertEquals("Full Time", m.getEmploymentType());
        assertEquals("3000", m.getConsultationFee());
        assertEquals("Active", m.getEmploymentStatus());
    }

    @Test
    public void step5Fields_roundTrip() {
        DentistModel m = new DentistModel();
        m.setWorkingDays("Mon-Fri");
        m.setStartTime("09:00");
        m.setEndTime("17:00");
        m.setBreakTime("12:00-13:00");
        m.setRoomNo("R2");

        assertEquals("Mon-Fri", m.getWorkingDays());
        assertEquals("09:00", m.getStartTime());
        assertEquals("17:00", m.getEndTime());
        assertEquals("12:00-13:00", m.getBreakTime());
        assertEquals("R2", m.getRoomNo());
    }

    @Test
    public void toString_containsTheCoreIdentifyingFields() {
        DentistModel m = new DentistModel();
        m.setFullName("Jane Smith");
        m.setDentistId("D1");
        m.setSlmcNo("SLMC123");
        m.setEmail("jane@example.com");

        String text = m.toString();
        assertTrue(text.contains("Jane Smith"));
        assertTrue(text.contains("D1"));
        assertTrue(text.contains("SLMC123"));
        assertTrue(text.contains("jane@example.com"));
    }
}
