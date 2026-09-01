package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Renders Administration &gt; Reports' tabular reports (Patient/Dentist/
 * Appointment/Inventory/Billing and the 5 Financial reports) as a PDF that
 * follows the clinic's fixed report format guideline:
 *
 * <pre>
 *   Paper           A4
 *   Margins         1.5" left, 1" right/top/bottom
 *   Line spacing    1.5
 *   Font            Times New Roman — Headings 14pt Bold, Normal 12pt
 *   Page numbers    bottom-right
 * </pre>
 *
 * A single {@link #generate} call handles the whole document: title block,
 * a simple fixed-column table, an optional grand-total summary line, an
 * optional footnote, and automatic multi-page pagination (re-printing the
 * table header + a running "Page N" footer on every page).
 *
 * @author oveen
 */
public final class ReportPdfGenerator {

    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float MARGIN_LEFT = 108f;   // 1.5"
    private static final float MARGIN_RIGHT = 72f;   // 1"
    private static final float MARGIN_TOP = 72f;     // 1"
    private static final float MARGIN_BOTTOM = 72f;  // 1"
    private static final float CONTENT_WIDTH = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT;

    private static final float HEADING_SIZE = 14f;
    private static final float NORMAL_SIZE = 12f;
    private static final float LINE_SPACING = 1.5f;
    private static final float HEADING_LINE_HEIGHT = HEADING_SIZE * LINE_SPACING;
    private static final float NORMAL_LINE_HEIGHT = NORMAL_SIZE * LINE_SPACING;
    private static final float FOOTER_RESERVE = 26f; // keeps the "Page N" stamp clear of the last content row
    // Gap between a horizontal rule and the text baseline right below it —
    // needs to clear that text's ascent (~0.75em for Times) or the rule
    // visually strikes through the first line under it.
    private static final float POST_RULE_GAP = NORMAL_SIZE + 3f;

    private ReportPdfGenerator() {
    }

    /**
     * @param reportTitle    e.g. "Patient Report"
     * @param periodLabel    e.g. "Weekly — 2026-08-14 to 2026-08-20" or "All records on file"
     * @param columnHeaders  table column names
     * @param columnWidths   points per column, must sum to {@link #CONTENT_WIDTH} (415.28pt)
     * @param rows           each entry's length must match columnHeaders.length
     * @param summaryLabel   e.g. "Grand Total" — pass null to omit the summary line entirely
     * @param summaryValue   right-aligned value paired with summaryLabel
     * @param note           an optional italic footnote (e.g. a data-limitation disclaimer) — null to omit
     */
    public static byte[] generate(
            String reportTitle,
            String periodLabel,
            String[] columnHeaders,
            float[] columnWidths,
            List<String[]> rows,
            String summaryLabel,
            String summaryValue,
            String note) throws IOException {

        try (PDDocument document = new PDDocument()) {
            Ctx ctx = new Ctx(document);
            ctx.startPage();

            ctx.writeCentered(PDType1Font.TIMES_BOLD, HEADING_SIZE, "SUNRISE DENTAL CLINIC");
            ctx.y -= HEADING_LINE_HEIGHT;
            ctx.writeCentered(PDType1Font.TIMES_BOLD, HEADING_SIZE, sanitize(reportTitle));
            ctx.y -= HEADING_LINE_HEIGHT;
            // Word-wrapped, not forced onto one line — some period labels
            // (e.g. the "(showing all records on file...)" snapshot
            // caveat) are too wide for one centered 12pt line and would
            // otherwise overflow past the margins and crowd the title above.
            for (String line : wrap(sanitize(periodLabel), PDType1Font.TIMES_ROMAN, NORMAL_SIZE, CONTENT_WIDTH)) {
                ctx.writeCentered(PDType1Font.TIMES_ROMAN, NORMAL_SIZE, line);
                ctx.y -= NORMAL_LINE_HEIGHT;
            }
            String genDate = "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            ctx.writeCentered(PDType1Font.TIMES_ROMAN, NORMAL_SIZE, genDate);
            ctx.y -= NORMAL_LINE_HEIGHT * 1.4f;

            ctx.hLine();
            ctx.y -= POST_RULE_GAP;
            ctx.writeRow(PDType1Font.TIMES_BOLD, columnHeaders, columnWidths);
            ctx.y -= 4;
            ctx.hLine();
            ctx.y -= POST_RULE_GAP;

            if (rows.isEmpty()) {
                ctx.ensureRowSpace(columnHeaders, columnWidths);
                ctx.writeText(PDType1Font.TIMES_ROMAN, NORMAL_SIZE, MARGIN_LEFT, "No records found for this period.");
                ctx.y -= NORMAL_LINE_HEIGHT;
            } else {
                for (String[] row : rows) {
                    ctx.ensureRowSpace(columnHeaders, columnWidths);
                    ctx.writeRow(PDType1Font.TIMES_ROMAN, row, columnWidths);
                }
            }

            if (summaryLabel != null) {
                ctx.y -= 4;
                ctx.hLine();
                ctx.y -= POST_RULE_GAP;
                ctx.ensureRowSpace(null, null);
                ctx.writeText(PDType1Font.TIMES_BOLD, NORMAL_SIZE, MARGIN_LEFT, sanitize(summaryLabel));
                ctx.writeTextRightAligned(PDType1Font.TIMES_BOLD, NORMAL_SIZE, sanitize(summaryValue));
                ctx.y -= NORMAL_LINE_HEIGHT;
            }

            if (note != null && !note.trim().isEmpty()) {
                ctx.y -= NORMAL_LINE_HEIGHT * 0.5f;
                for (String line : wrap(note, PDType1Font.TIMES_ITALIC, NORMAL_SIZE - 1, CONTENT_WIDTH)) {
                    ctx.ensureRowSpace(null, null);
                    ctx.writeText(PDType1Font.TIMES_ITALIC, NORMAL_SIZE - 1, MARGIN_LEFT, sanitize(line));
                    ctx.y -= NORMAL_LINE_HEIGHT;
                }
            }

            ctx.finish();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    /** Mutable per-document drawing state: current page/content-stream, cursor Y, and page counter. */
    private static final class Ctx {
        final PDDocument document;
        PDPage page;
        PDPageContentStream cs;
        float y;
        int pageNumber;

        Ctx(PDDocument document) {
            this.document = document;
        }

        void startPage() throws IOException {
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            cs = new PDPageContentStream(document, page);
            y = PAGE_HEIGHT - MARGIN_TOP;
            pageNumber++;
        }

        /** Closes the current page's content stream, stamping "Page N" bottom-right first. */
        void closePage() throws IOException {
            writeTextRightAligned(PDType1Font.TIMES_ROMAN, 10, "Page " + pageNumber);
            cs.close();
        }

        void finish() throws IOException {
            closePage();
        }

        /** Advances to a new page if the next row (and, when given, its re-printed header) wouldn't fit. */
        void ensureRowSpace(String[] headerToRepeat, float[] widths) throws IOException {
            float needed = NORMAL_LINE_HEIGHT + (headerToRepeat != null ? NORMAL_LINE_HEIGHT * 1.5f : 0);
            if (y - needed < MARGIN_BOTTOM + FOOTER_RESERVE) {
                closePage();
                startPage();
                if (headerToRepeat != null) {
                    writeRow(PDType1Font.TIMES_BOLD, headerToRepeat, widths);
                    y -= 4;
                    hLine();
                    y -= POST_RULE_GAP;
                }
            }
        }

        void hLine() throws IOException {
            cs.setLineWidth(0.75f);
            cs.moveTo(MARGIN_LEFT, y);
            cs.lineTo(PAGE_WIDTH - MARGIN_RIGHT, y);
            cs.stroke();
        }

        void writeRow(PDFont font, String[] cells, float[] widths) throws IOException {
            float x = MARGIN_LEFT;
            for (int i = 0; i < cells.length; i++) {
                float w = widths[i];
                String fitted = fit(sanitize(cells[i]), font, NORMAL_SIZE, w - 6);
                writeText(font, NORMAL_SIZE, x, fitted);
                x += w;
            }
            y -= NORMAL_LINE_HEIGHT;
        }

        void writeText(PDFont font, float size, float x, String text) throws IOException {
            cs.beginText();
            cs.setFont(font, size);
            cs.newLineAtOffset(x, y);
            cs.showText(text == null ? "" : text);
            cs.endText();
        }

        void writeTextRightAligned(PDFont font, float size, String text) throws IOException {
            String safe = text == null ? "" : text;
            float width = font.getStringWidth(safe) / 1000 * size;
            writeText(font, size, PAGE_WIDTH - MARGIN_RIGHT - width, safe);
        }

        void writeCentered(PDFont font, float size, String text) throws IOException {
            String safe = text == null ? "" : text;
            float width = font.getStringWidth(safe) / 1000 * size;
            writeText(font, size, MARGIN_LEFT + (CONTENT_WIDTH - width) / 2, safe);
        }
    }

    /** Truncates text with a trailing "..." so it never overruns its column width. */
    private static String fit(String text, PDFont font, float size, float maxWidth) throws IOException {
        if (text == null) return "";
        if (font.getStringWidth(text) / 1000 * size <= maxWidth) return text;
        String ellipsis = "...";
        String out = text;
        while (!out.isEmpty() && font.getStringWidth(out + ellipsis) / 1000 * size > maxWidth) {
            out = out.substring(0, out.length() - 1);
        }
        return out.isEmpty() ? ellipsis : out + ellipsis;
    }

    /** Word-wraps a footnote to the content width — used only for the short disclaimer line, not table cells. */
    private static java.util.List<String> wrap(String text, PDFont font, float size, float maxWidth) throws IOException {
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split("\\s+")) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (font.getStringWidth(candidate) / 1000 * size > maxWidth && line.length() > 0) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }

    /** PDFBox's base-14 fonts only support WinAnsi-encodable characters. */
    private static String sanitize(String text) {
        return text == null ? "" : text.replace("₹", "Rs").replace("–", "-").replace("—", "-");
    }

    /** Points-per-column helper — pass proportions (they don't need to sum to 1) and get widths summing to the usable content width. */
    public static float[] columnWidths(float... proportions) {
        float sum = 0;
        for (float p : proportions) sum += p;
        float[] widths = new float[proportions.length];
        for (int i = 0; i < proportions.length; i++) {
            widths[i] = CONTENT_WIDTH * (proportions[i] / sum);
        }
        return widths;
    }
}
