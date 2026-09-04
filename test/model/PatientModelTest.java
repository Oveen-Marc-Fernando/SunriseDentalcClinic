package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/** Getter/setter round-trip tests for {@link PatientModel} — a plain 4-step wizard data holder. */
public class PatientModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void step1Fields_roundTrip() {
        PatientModel m = new PatientModel();
        m.setTitle("Mr");
        m.setGender("Male");
        m.setFullName("John Doe");
        m.setPatientId("PT1");
        m.setDob("1995-05-05");
        m.setAge("31");
        m.setNic("951234567V");

        assertEquals("Mr", m.getTitle());
        assertEquals("Male", m.getGender());
        assertEquals("John Doe", m.getFullName());
        assertEquals("PT1", m.getPatientId());
        assertEquals("1995-05-05", m.getDob());
        assertEquals("31", m.getAge());
        assertEquals("951234567V", m.getNic());
    }

    @Test
    public void step2Fields_roundTrip() {
        PatientModel m = new PatientModel();
        m.setAddressLine1("123 Main St");
        m.setAddressLine2("Apt 4");
        m.setCity("Colombo");
        m.setMobileNo("0771234567");
        m.setLandlineNo("0112345678");
        m.setEmail("john@example.com");

        assertEquals("123 Main St", m.getAddressLine1());
        assertEquals("Apt 4", m.getAddressLine2());
        assertEquals("Colombo", m.getCity());
        assertEquals("0771234567", m.getMobileNo());
        assertEquals("0112345678", m.getLandlineNo());
        assertEquals("john@example.com", m.getEmail());
    }

    @Test
    public void step3Fields_roundTrip() {
        PatientModel m = new PatientModel();
        m.setBloodGroup("O+");
        m.setAllergies("Penicillin");
        m.setMedicalConditions("None");
        m.setCurrentMedications("None");
        m.setPreviousSurgeries("None");
        m.setGeneralMedicalNotes("Healthy");

        assertEquals("O+", m.getBloodGroup());
        assertEquals("Penicillin", m.getAllergies());
        assertEquals("None", m.getMedicalConditions());
        assertEquals("None", m.getCurrentMedications());
        assertEquals("None", m.getPreviousSurgeries());
        assertEquals("Healthy", m.getGeneralMedicalNotes());
    }

    @Test
    public void step4Fields_roundTrip() {
        PatientModel m = new PatientModel();
        m.setLastDentalVisit("2025-01-01");
        m.setDentalHistory("Regular checkups");
        m.setDentalProblems("None");
        m.setOralHygiene("Good");
        m.setDentalMedicalNotes("No issues");

        assertEquals("2025-01-01", m.getLastDentalVisit());
        assertEquals("Regular checkups", m.getDentalHistory());
        assertEquals("None", m.getDentalProblems());
        assertEquals("Good", m.getOralHygiene());
        assertEquals("No issues", m.getDentalMedicalNotes());
    }
}
