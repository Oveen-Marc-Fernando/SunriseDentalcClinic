package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/**
 * Tests for {@link BillingModel} — beyond plain getters/setters, this model
 * has real behavior worth verifying: the list/map setters clear-then-copy
 * (so passing null doesn't NPE, and an old external list mutated later
 * doesn't silently change the model), and the list/map getters return the
 * model's own live collection rather than a defensive copy.
 */
public class BillingModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void step1Fields_roundTrip() {
        BillingModel m = new BillingModel();
        m.setAppointmentId("A1");
        m.setPatientId("P1");
        m.setDentistName("Dr. Smith");
        m.setPatientName("John Doe");
        m.setAppointmentDate("2026-08-24");
        m.setAppointmentCharges(2500.0);

        assertEquals("A1", m.getAppointmentId());
        assertEquals("P1", m.getPatientId());
        assertEquals("Dr. Smith", m.getDentistName());
        assertEquals("John Doe", m.getPatientName());
        assertEquals("2026-08-24", m.getAppointmentDate());
        assertEquals(2500.0, m.getAppointmentCharges(), 0.0001);
    }

    @Test
    public void step4Fields_roundTrip() {
        BillingModel m = new BillingModel();
        m.setBillingId("B1");
        m.setTotalBillAmount(5000.0);
        m.setEmailSentCount(2);

        assertEquals("B1", m.getBillingId());
        assertEquals(5000.0, m.getTotalBillAmount(), 0.0001);
        assertEquals(2, m.getEmailSentCount());
    }

    @Test
    public void setClinicalLines_copiesGivenList_notJustAReference() {
        BillingModel m = new BillingModel();
        List<String> source = new ArrayList<>();
        source.add("Filling - 1000");
        m.setClinicalLines(source);

        source.add("Extraction - 2000"); // mutate the original list afterward

        assertEquals(1, m.getClinicalLines().size()); // model's copy is unaffected
        assertEquals("Filling - 1000", m.getClinicalLines().get(0));
    }

    @Test
    public void setClinicalLines_null_clearsInsteadOfThrowing() {
        BillingModel m = new BillingModel();
        m.setClinicalLines(java.util.Arrays.asList("X-ray - 500"));
        m.setClinicalLines(null);
        assertTrue(m.getClinicalLines().isEmpty());
    }

    @Test
    public void setClinicalLines_replacesPreviousContentsRatherThanAppending() {
        BillingModel m = new BillingModel();
        m.setClinicalLines(java.util.Arrays.asList("First"));
        m.setClinicalLines(java.util.Arrays.asList("Second", "Third"));

        assertEquals(2, m.getClinicalLines().size());
        assertEquals("Second", m.getClinicalLines().get(0));
        assertEquals("Third", m.getClinicalLines().get(1));
    }

    @Test
    public void setMedicineLines_behavesTheSameWayAsClinicalLines() {
        BillingModel m = new BillingModel();
        m.setMedicineLines(java.util.Arrays.asList("Paracetamol x10"));
        assertEquals(1, m.getMedicineLines().size());

        m.setMedicineLines(null);
        assertTrue(m.getMedicineLines().isEmpty());
    }

    @Test
    public void setMedicineDeductions_null_clearsInsteadOfThrowing() {
        BillingModel m = new BillingModel();
        Map<String, Integer> initial = new LinkedHashMap<>();
        initial.put("MED1", 3);
        m.setMedicineDeductions(initial);
        assertEquals(1, m.getMedicineDeductions().size());

        m.setMedicineDeductions(null);
        assertTrue(m.getMedicineDeductions().isEmpty());
    }

    @Test
    public void setMedicineDeductions_replacesRatherThanMerges() {
        BillingModel m = new BillingModel();
        Map<String, Integer> first = new LinkedHashMap<>();
        first.put("MED1", 3);
        m.setMedicineDeductions(first);

        Map<String, Integer> second = new LinkedHashMap<>();
        second.put("MED2", 5);
        m.setMedicineDeductions(second);

        assertEquals(1, m.getMedicineDeductions().size());
        assertFalse(m.getMedicineDeductions().containsKey("MED1"));
        assertEquals(Integer.valueOf(5), m.getMedicineDeductions().get("MED2"));
    }

    @Test
    public void freshModel_startsWithEmptyListsAndMap() {
        BillingModel m = new BillingModel();
        assertTrue(m.getClinicalLines().isEmpty());
        assertTrue(m.getMedicineLines().isEmpty());
        assertTrue(m.getMedicineDeductions().isEmpty());
    }
}
