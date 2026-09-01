package util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import model.AppointmentModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Builds the one-page "Appointment Receipt" PDF shown in
 * {@link view.AppointmentPreviewDialog} — same layout conventions as
 * {@link BillPdfGenerator} (logo header, label/value block, hairline
 * dividers, footer thank-you line) so the two documents read as one family.
 *
 * @author oveen
 */
public final class AppointmentPdfGenerator {

    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final String LOGO_RESOURCE = "/resources/logo_scaled.png";

    private AppointmentPdfGenerator() {
    }

    /** Builds the receipt as a PDF and returns its raw bytes (nothing written to disk here). */
    public static byte[] generate(AppointmentModel appt) throws IOException {
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

                // ── "APPOINTMENT RECEIPT" + appointment ID / generated-on ──
                writeText(cs, PDType1Font.HELVETICA_BOLD, 16, MARGIN, y, "APPOINTMENT RECEIPT");
                String genDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                writeTextRightAligned(cs, PDType1Font.HELVETICA_BOLD, 11, PAGE_WIDTH - MARGIN, y,
                        "Appointment No: " + dash(appt.getAppointmentId()));
                writeTextRightAligned(cs, PDType1Font.HELVETICA, 9, PAGE_WIDTH - MARGIN, y - 14,
                        "Generated: " + genDate);
                y -= 36;

                // ── Patient / dentist / schedule details ─────────────────
                y = writeLabelValue(cs, "Patient Name:", dash(appt.getPatientName()), y);
                y = writeLabelValue(cs, "Dentist Name:", dash(appt.getDentistName()), y);
                y = writeLabelValue(cs, "Treatment Type:", dash(appt.getTreatmentType()), y);
                y = writeLabelValue(cs, "Appointment Date:", dash(appt.getDate()), y);
                y = writeLabelValue(cs, "Appointment Time:", dash(appt.getTime()), y);
                y = writeLabelValue(cs, "Room No:", dash(appt.getRoomNo()), y);
                y = writeLabelValue(cs, "Status:", dash(appt.getStatus()), y);

                y -= 8;
                y = hLine(cs, y);

                y -= 50;
                writeText(cs, PDType1Font.HELVETICA_OBLIQUE, 9, MARGIN, y,
                        "Please arrive 10 minutes early. Thank you for choosing Sunrise Dental Clinic.");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private static PDImageXObject loadLogo(PDDocument document) {
        try (InputStream in = AppointmentPdfGenerator.class.getResourceAsStream(LOGO_RESOURCE)) {
            if (in == null) return null;
            BufferedImage image = ImageIO.read(in);
            return image != null ? LosslessFactory.createFromImage(document, image) : null;
        } catch (IOException e) {
            System.err.println("[AppointmentPdfGenerator] logo load failed: " + e.getMessage());
            return null;
        }
    }

    private static float writeLabelValue(PDPageContentStream cs, String label, String value, float y) throws IOException {
        writeText(cs, PDType1Font.HELVETICA_BOLD, 10, MARGIN, y, label);
        writeText(cs, PDType1Font.HELVETICA, 10, MARGIN + 130, y, value);
        return y - 18;
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

    /** PDFBox's base-14 fonts only support WinAnsi-encodable characters — swap glyphs that could break rendering. */
    private static String sanitize(String text) {
        return text == null ? "" : text.replace("₹", "Rs").replace("–", "-").replace("—", "-");
    }

    private static String dash(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value;
    }
}
