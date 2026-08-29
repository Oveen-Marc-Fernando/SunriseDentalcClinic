package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.JPanel;

/**
 * Hand-drawn bar chart — gridlines, a value label above each bar, and a
 * category label below it. No charting library; built the same way every
 * other graphic in this app is (plain Graphics2D), matching IconFactory's
 * vector-icon convention instead of pulling in a new dependency for what's
 * ultimately a handful of numbers.
 *
 * Shared by every dashboard's "Analysis" popup (Office Staff, Dentist, ...)
 * — each just supplies its own labels/values/colors.
 *
 * @author oveen
 */
public final class BarChartPanel extends JPanel {
    private final List<String> labels;
    private final List<Integer> values;
    private final List<Color> colors;

    public BarChartPanel(List<String> labels, List<Integer> values, List<Color> colors) {
        this.labels = labels;
        this.values = values;
        this.colors = colors;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int marginLeft = 44, marginRight = 16, marginTop = 30, marginBottom = 54;
        int chartW = w - marginLeft - marginRight;
        int chartH = h - marginTop - marginBottom;
        if (chartW <= 0 || chartH <= 0) {
            g2.dispose();
            return;
        }

        int maxValue = values.stream().max(Integer::compareTo).orElse(1);
        int axisMax = Math.max(5, ((maxValue / 5) + 1) * 5);
        int gridLines = 5;

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        for (int i = 0; i <= gridLines; i++) {
            int y = marginTop + chartH - (chartH * i / gridLines);
            g2.setColor(new Color(235, 235, 235));
            g2.drawLine(marginLeft, y, marginLeft + chartW, y);
            g2.setColor(new Color(120, 120, 120));
            String label = String.valueOf(axisMax * i / gridLines);
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, marginLeft - labelWidth - 8, y + 4);
        }

        g2.setColor(new Color(90, 90, 90));
        g2.drawLine(marginLeft, marginTop, marginLeft, marginTop + chartH);
        g2.drawLine(marginLeft, marginTop + chartH, marginLeft + chartW, marginTop + chartH);

        int n = values.size();
        int slot = chartW / Math.max(1, n);
        int barWidth = (int) (slot * 0.48);

        for (int i = 0; i < n; i++) {
            int val = values.get(i);
            int barHeight = (int) Math.round((double) val / axisMax * chartH);
            int x = marginLeft + i * slot + (slot - barWidth) / 2;
            int y = marginTop + chartH - barHeight;

            Color fill = colors.get(i % colors.size());
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(x, y, barWidth, Math.max(barHeight, 1), 8, 8));
            g2.setColor(fill.darker());
            g2.draw(new RoundRectangle2D.Float(x, y, barWidth, Math.max(barHeight, 1), 8, 8));

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(30, 30, 30));
            String valStr = String.valueOf(val);
            int vw = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, x + (barWidth - vw) / 2f, y - 8);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(60, 60, 60));
            String catLabel = labels.get(i);
            int lw = g2.getFontMetrics().stringWidth(catLabel);
            g2.drawString(catLabel, x + (barWidth - lw) / 2f, marginTop + chartH + 20);
        }

        g2.dispose();
    }
}
