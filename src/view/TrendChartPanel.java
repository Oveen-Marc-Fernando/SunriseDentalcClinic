package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.function.DoubleFunction;
import javax.swing.JPanel;

/**
 * Hand-drawn two-series line/trend chart — a title, a top-right dot legend,
 * a real labeled dual Y-axis (left axis for series 0, right axis for series
 * 1 — both start at 0), faint horizontal gridlines shared by both axes, and
 * sparse date labels along the bottom. Same plain-Graphics2D convention as
 * {@link BarChartPanel} (no charting library).
 *
 * The two series are scaled independently to their own 0..max range — the
 * dashboard's pairing (billed income in Rs, and a plain appointment count)
 * live on wildly different scales, so a shared single axis would flatten
 * the smaller one into a barely-visible line. Each line's own axis (with
 * its own labeled numbers) is what makes that still readable instead of
 * just being a shape with no real values attached to it.
 *
 * @author oveen
 */
public final class TrendChartPanel extends JPanel {

    /** Formats a raw series value into the text shown at each axis tick, e.g. v -&gt; "Rs " + (int) v. */
    public interface AxisFormatter extends DoubleFunction<String> {
    }

    private final String title;
    private final List<String> xLabels;
    private final List<double[]> series;
    private final List<String> seriesLabels;
    private final List<Color> seriesColors;
    private final AxisFormatter leftAxisFormatter;
    private final AxisFormatter rightAxisFormatter;

    /** Two-series constructor with no axis labels — kept for any future single/unlabeled use. */
    public TrendChartPanel(String title, List<String> xLabels, List<double[]> series,
            List<String> seriesLabels, List<Color> seriesColors) {
        this(title, xLabels, series, seriesLabels, seriesColors, null, null);
    }

    /**
     * @param leftAxisFormatter   formats series 0's tick labels (drawn on the left) — null hides that axis
     * @param rightAxisFormatter  formats series 1's tick labels (drawn on the right) — null hides that axis
     */
    public TrendChartPanel(String title, List<String> xLabels, List<double[]> series,
            List<String> seriesLabels, List<Color> seriesColors,
            AxisFormatter leftAxisFormatter, AxisFormatter rightAxisFormatter) {
        this.title = title;
        this.xLabels = xLabels;
        this.series = series;
        this.seriesLabels = seriesLabels;
        this.seriesColors = seriesColors;
        this.leftAxisFormatter = leftAxisFormatter;
        this.rightAxisFormatter = rightAxisFormatter;
        setBackground(Color.WHITE);
    }

    private static final int GRID_LINES = 4;
    private static final Font AXIS_FONT = new Font("Segoe UI", Font.PLAIN, 10);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int n = xLabels.size();
        if (w <= 0 || h <= 0 || n == 0 || series.isEmpty()) {
            g2.dispose();
            return;
        }

        double leftMax = seriesMax(series.get(0));
        double rightMax = series.size() > 1 ? seriesMax(series.get(1)) : 0;

        g2.setFont(AXIS_FONT);
        FontMetrics axisFm = g2.getFontMetrics();
        int marginLeft = 16, marginRight = 16;
        if (leftAxisFormatter != null) {
            marginLeft = axisFm.stringWidth(leftAxisFormatter.apply(leftMax)) + 14;
        }
        if (rightAxisFormatter != null && series.size() > 1) {
            marginRight = axisFm.stringWidth(rightAxisFormatter.apply(rightMax)) + 14;
        }
        int marginTop = 40, marginBottom = 28;
        int chartW = w - marginLeft - marginRight;
        int chartH = h - marginTop - marginBottom;
        if (chartW <= 0 || chartH <= 0) {
            g2.dispose();
            return;
        }

        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(new Color(30, 30, 30));
        g2.drawString(title, marginLeft, 20);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int totalLegendWidth = 0;
        for (String label : seriesLabels) totalLegendWidth += 14 + fm.stringWidth(label) + 16;
        int lx = Math.max(marginLeft, w - marginRight - totalLegendWidth);
        for (int s = 0; s < seriesLabels.size(); s++) {
            g2.setColor(seriesColors.get(s));
            g2.fillOval(lx, 10, 8, 8);
            g2.setColor(new Color(70, 70, 70));
            g2.drawString(seriesLabels.get(s), lx + 12, 19);
            lx += 14 + fm.stringWidth(seriesLabels.get(s)) + 16;
        }

        // Gridlines + axis tick labels — both axes share the same Y position
        // per tick (tick i sits at height fraction i/GRID_LINES for BOTH
        // scales at once, since each series is independently normalized to
        // its own 0..max over the exact same chartH), so one set of
        // horizontal lines correctly serves two differently-scaled axes.
        g2.setFont(AXIS_FONT);
        for (int i = 0; i <= GRID_LINES; i++) {
            int y = marginTop + chartH - (chartH * i / GRID_LINES);
            g2.setColor(new Color(238, 238, 238));
            g2.drawLine(marginLeft, y, marginLeft + chartW, y);

            if (leftAxisFormatter != null) {
                String label = leftAxisFormatter.apply(leftMax * i / GRID_LINES);
                g2.setColor(new Color(120, 120, 120));
                g2.drawString(label, marginLeft - g2.getFontMetrics().stringWidth(label) - 8, y + 4);
            }
            if (rightAxisFormatter != null && series.size() > 1) {
                String label = rightAxisFormatter.apply(rightMax * i / GRID_LINES);
                g2.setColor(new Color(120, 120, 120));
                g2.drawString(label, marginLeft + chartW + 8, y + 4);
            }
        }

        float[] xs = new float[n];
        for (int i = 0; i < n; i++) {
            xs[i] = marginLeft + (n == 1 ? chartW / 2f : chartW * i / (float) (n - 1));
        }

        for (int s = 0; s < series.size(); s++) {
            double[] vals = series.get(s);
            double max = seriesMax(vals);
            if (max <= 0) max = 1;

            Color color = seriesColors.get(s);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.2f));
            GeneralPath path = new GeneralPath();
            for (int i = 0; i < n; i++) {
                float y = (float) (marginTop + chartH - (vals[i] / max) * chartH);
                if (i == 0) path.moveTo(xs[i], y); else path.lineTo(xs[i], y);
            }
            g2.draw(path);
            g2.setStroke(new BasicStroke(1f));

            for (int i = 0; i < n; i++) {
                float y = (float) (marginTop + chartH - (vals[i] / max) * chartH);
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Float(xs[i] - 3, y - 3, 6, 6));
                g2.setColor(color);
                g2.draw(new Ellipse2D.Float(xs[i] - 3, y - 3, 6, 6));
            }
        }

        g2.setColor(new Color(120, 120, 120));
        g2.drawLine(marginLeft, marginTop + chartH, marginLeft + chartW, marginTop + chartH);

        g2.setFont(AXIS_FONT);
        g2.setColor(new Color(110, 110, 110));
        // Evenly-spaced label positions (always including both endpoints)
        // instead of "every Nth point plus the last one tacked on" — that
        // tacked-on version could land right next to the previous label and
        // visually overlap/crowd against the panel's right edge.
        int labelCount = Math.min(n, 6);
        int lastDrawnIndex = -1;
        for (int k = 0; k < labelCount; k++) {
            int i = labelCount == 1 ? 0 : Math.round(k * (n - 1) / (float) (labelCount - 1));
            if (i == lastDrawnIndex) continue;
            drawXLabel(g2, xLabels.get(i), xs[i], marginTop + chartH, marginLeft, w - marginRight);
            lastDrawnIndex = i;
        }

        g2.dispose();
    }

    private static double seriesMax(double[] values) {
        double max = 0;
        for (double v : values) max = Math.max(max, v);
        return max;
    }

    private void drawXLabel(Graphics2D g2, String label, float xCenter, int axisY, int minX, int maxX) {
        int lw = g2.getFontMetrics().stringWidth(label);
        float x = Math.max(minX, Math.min(xCenter - lw / 2f, maxX - lw));
        g2.drawString(label, x, axisY + 16);
    }
}
