package util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import model.BillingModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 *
 * @author oveen
 */
public final class BillPdfGenerator {

    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final String LOGO_RESOURCE = "/resources/logo_scaled.png";

    private BillPdfGenerator() {
    }

    /** Builds the bill as a PDF and returns its raw bytes (nothing written to disk here). */
    public static byte[] generate(BillingModel bill) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                // ── Header: logo + clinic name ──────────────────────────
                PDImageXObject logo = loadLogo(document);
                float logoHeight = 40;
                float logoWidth = 0;
                if (logo != null) {
                    logoWidth = logo.getWidth() * (logoHeight / logo.getHeight());
                    cs.drawImage(logo, MARGIN, y - logoHeight, logoWidth, logoHeight);
                }

                float textX = MARGIN + logoWidth + (logo != null ? 14 : 0);
                writeText(cs, PDType1Font.HELVETICA_BOLD, 20, textX, y - 16, "SUNRISE DENTAL CLINIC");
                writeText(cs, PDType1Font.HELVETICA, 10, textX, y - 32, "Dental Care Excellence");

                y -= (logoHeight + 20);
                y = hLine(cs, y);
                y -= 26;

                // ── "BILL / INVOICE" + billing ID / generated-on ────────
                writeText(cs, PDType1Font.HELVETICA_BOLD, 16, MARGIN, y, "BILL / INVOICE");
                String genDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                writeTextRightAligned(cs, PDType1Font.HELVETICA_BOLD, 11, PAGE_WIDTH - MARGIN, y,
                        "Billing ID: " + dash(bill.getBillingId()));
                writeTextRightAligned(cs, PDType1Font.HELVETICA, 9, PAGE_WIDTH - MARGIN, y - 14,
                        "Generated: " + genDate);
                y -= 36;

                // ── Patient / dentist / appointment details ─────────────
                y = writeLabelValue(cs, "Patient Name:", dash(bill.getPatientName()), y);
                y = writeLabelValue(cs, "Patient ID:", dash(bill.getPatientId()), y);
                y = writeLabelValue(cs, "Dentist Name:", dash(bill.getDentistName()), y);
                y = writeLabelValue(cs, "Appointment ID:", dash(bill.getAppointmentId()), y);
                y = writeLabelValue(cs, "Appointment Date:", dash(bill.getAppointmentDate()), y);

                y -= 8;
                y = hLine(cs, y);
                y -= 22;

                // ── Itemized table ───────────────────────────────────────
                writeText(cs, PDType1Font.HELVETICA_BOLD, 11, MARGIN, y, "Description");
                writeTextRightAligned(cs, PDType1Font.HELVETICA_BOLD, 11, PAGE_WIDTH - MARGIN, y, "Amount");
                y -= 8;
                y = hLine(cs, y);
                y -= 18;

                y = writeItemRow(cs, "Appointment Charges", currency(bill.getAppointmentCharges()), y);

                // The itemized line-by-line breakdown only exists in memory
                // during the live Billing wizard — the "billings" table only
                // stores the aggregate totals, not each individual service/
                // medicine line. Re-printing a bill later (from OS_BM_Grid,
                // after the app restarted, etc.) has no line items to show,
                // so fall back to a single aggregate row per section instead
                // of silently omitting Clinical/Medicine Charges altogether.
                if (!bill.getClinicalLines().isEmpty()) {
                    y -= 4;
                    y = writeSectionLabel(cs, "Clinical Charges", y);
                    for (String line : bill.getClinicalLines()) {
                        String[] parts = splitLine(line);
                        y = writeItemRow(cs, "     " + parts[0], parts[1], y);
                    }
                } else if (bill.getClinicalTotal() > 0) {
                    y = writeItemRow(cs, "Clinical Charges", currency(bill.getClinicalTotal()), y);
                }

                if (!bill.getMedicineLines().isEmpty()) {
                    y -= 4;
                    y = writeSectionLabel(cs, "Medicine Charges", y);
                    for (String line : bill.getMedicineLines()) {
                        String[] parts = splitLine(line);
                        y = writeItemRow(cs, "     " + parts[0], parts[1], y);
                    }
                } else if (bill.getMedicineTotal() > 0) {
                    y = writeItemRow(cs, "Medicine Charges", currency(bill.getMedicineTotal()), y);
                }

                y -= 8;
                y = hLine(cs, y);
                y -= 26;

                writeText(cs, PDType1Font.HELVETICA_BOLD, 14, MARGIN, y, "TOTAL BILL AMOUNT");
                writeTextRightAligned(cs, PDType1Font.HELVETICA_BOLD, 14, PAGE_WIDTH - MARGIN, y,
                        currency(bill.getTotalBillAmount()));

                y -= 50;
                writeText(cs, PDType1Font.HELVETICA_OBLIQUE, 9, MARGIN, y,
                        "Thank you for choosing Sunrise Dental Clinic.");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private static PDImageXObject loadLogo(PDDocument document) {
        try (InputStream in = BillPdfGenerator.class.getResourceAsStream(LOGO_RESOURCE)) {
            if (in == null) return null;
            BufferedImage image = ImageIO.read(in);
            return image != null ? LosslessFactory.createFromImage(document, image) : null;
        } catch (IOException e) {
            System.err.println("[BillPdfGenerator] logo load failed: " + e.getMessage());
            return null;
        }
    }

    /** "<description> x<qty> = <currency>" -> {"<description> x<qty>", "<currency>"}. Falls back to {line, ""}. */
    private static String[] splitLine(String line) {
        int idx = line.lastIndexOf(" = ");
        return idx >= 0
                ? new String[]{line.substring(0, idx), line.substring(idx + 3)}
                : new String[]{line, ""};
    }

    private static float writeLabelValue(PDPageContentStream cs, String label, String value, float y) throws IOException {
        writeText(cs, PDType1Font.HELVETICA_BOLD, 10, MARGIN, y, label);
        writeText(cs, PDType1Font.HELVETICA, 10, MARGIN + 120, y, value);
        return y - 16;
    }

    private static float writeSectionLabel(PDPageContentStream cs, String label, float y) throws IOException {
        writeText(cs, PDType1Font.HELVETICA_BOLD, 10, MARGIN, y, label);
        return y - 16;
    }

    private static float writeItemRow(PDPageContentStream cs, String description, String amount, float y) throws IOException {
        writeText(cs, PDType1Font.HELVETICA, 10, MARGIN, y, description);
        if (amount != null && !amount.isEmpty()) {
            writeTextRightAligned(cs, PDType1Font.HELVETICA, 10, PAGE_WIDTH - MARGIN, y, amount);
        }
        return y - 16;
    }

    private static float hLine(PDPageContentStream cs, float y) throws IOException {
        cs.setLineWidth(0.75f);
        cs.moveTo(MARGIN, y);
        cs.lineTo(PAGE_WIDTH - MARGIN, y);
        cs.stroke();
        return y;
    }

    private static void writeText(PDPageContentStream cs, PDType1Font font, float size, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(sanitize(text));
        cs.endText();
    }

    private static void writeTextRightAligned(PDPageContentStream cs, PDType1Font font, float size, float rightX, float y, String text) throws IOException {
        String safe = sanitize(text);
        float width = font.getStringWidth(safe) / 1000 * size;
        writeText(cs, font, size, rightX - width, y, safe);
    }

    /** PDFBox's base-14 fonts only support WinAnsi-encodable characters — swap the "Rs" currency glyphs that could break rendering. */
    private static String sanitize(String text) {
        return text == null ? "" : text.replace("₹", "Rs").replace("–", "-").replace("—", "-");
    }

    private static String currency(double amount) {
        return controller.BillingManagementController.formatCurrency(amount);
    }

    private static String dash(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value;
    }
}
