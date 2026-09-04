package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/** Getter/setter round-trip tests for {@link SupplyRequestModel} — a plain data holder. */
public class SupplyRequestModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void everyFieldRoundTripsThroughItsOwnGetterAndSetter() {
        SupplyRequestModel m = new SupplyRequestModel();
        m.setTrackingId("T1");
        m.setProductId("P1");
        m.setProductType("Medicine");
        m.setProductName("Paracetamol");
        m.setDescription("Pain relief");
        m.setQuantity("20");
        m.setExpiryDate("2028-01-01");
        m.setManufactureDate("2026-01-01");
        m.setStatus("Pending");
        m.setDentistName("Dr. Smith");

        assertEquals("T1", m.getTrackingId());
        assertEquals("P1", m.getProductId());
        assertEquals("Medicine", m.getProductType());
        assertEquals("Paracetamol", m.getProductName());
        assertEquals("Pain relief", m.getDescription());
        assertEquals("20", m.getQuantity());
        assertEquals("2028-01-01", m.getExpiryDate());
        assertEquals("2026-01-01", m.getManufactureDate());
        assertEquals("Pending", m.getStatus());
        assertEquals("Dr. Smith", m.getDentistName());
    }

    @Test
    public void toString_containsTheCoreIdentifyingFields() {
        SupplyRequestModel m = new SupplyRequestModel();
        m.setTrackingId("T1");
        m.setProductId("P1");
        m.setProductName("Paracetamol");
        m.setQuantity("20");

        String text = m.toString();
        assertTrue(text.contains("T1"));
        assertTrue(text.contains("P1"));
        assertTrue(text.contains("Paracetamol"));
        assertTrue(text.contains("20"));
    }
}
