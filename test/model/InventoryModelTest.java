package model;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import support.ConsolePrintingRule;

/** Tests for {@link InventoryModel} — plain fields, plus the {@code published} default. */
public class InventoryModelTest {

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Test
    public void freshModel_isPublishedByDefault() {
        // So any existing wizard step that never touches "published" still
        // leaves the product visible to staff (see the field's own comment).
        InventoryModel m = new InventoryModel();
        assertTrue(m.isPublished());
    }

    @Test
    public void published_canBeSetToFalse() {
        InventoryModel m = new InventoryModel();
        m.setPublished(false);
        assertFalse(m.isPublished());
    }

    @Test
    public void step1Fields_roundTrip() {
        InventoryModel m = new InventoryModel();
        m.setProductId("P1");
        m.setQuantity("50");
        m.setProductType("Medicine");
        m.setManufactureDate("2026-01-01");
        m.setProductName("Paracetamol");
        m.setExpireDate("2028-01-01");
        m.setDescription("Pain relief");

        assertEquals("P1", m.getProductId());
        assertEquals("50", m.getQuantity());
        assertEquals("Medicine", m.getProductType());
        assertEquals("2026-01-01", m.getManufactureDate());
        assertEquals("Paracetamol", m.getProductName());
        assertEquals("2028-01-01", m.getExpireDate());
        assertEquals("Pain relief", m.getDescription());
    }

    @Test
    public void step2Fields_roundTrip() {
        InventoryModel m = new InventoryModel();
        m.setSupplierName("MedSupply Co");
        m.setBuyingPrice("10");
        m.setContactNumber("0771234567");
        m.setSellingPrice("15");
        m.setCompanyName("MedCo");

        assertEquals("MedSupply Co", m.getSupplierName());
        assertEquals("10", m.getBuyingPrice());
        assertEquals("0771234567", m.getContactNumber());
        assertEquals("15", m.getSellingPrice());
        assertEquals("MedCo", m.getCompanyName());
    }
}
