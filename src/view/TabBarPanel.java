package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.IntConsumer;
import javax.swing.JPanel;

/**
 * A row of rounded black pill tabs (Dentists / Patients / Appointments /
 * ...), the active one highlighted orange, each optionally showing a small
 * red notification dot — shared by every Administration "Operations" /
 * "Approvals" screen instead of hand-laying-out a JButton per tab in each
 * one's own initComponents() (same reuse convention as {@link BarChartPanel}).
 *
 * Each tab is sized to its own label (with a minimum width) rather than one
 * fixed width for every tab, so a longer label like "Office Staff Approvals"
 * doesn't get clipped just because it sits next to short ones like "Dentist
 * Leave".
 *
 * Self-contained Graphics2D drawing + its own click routing (see
 * {@link #setOnTabClick}), so a NetBeans .form only ever needs to declare
 * one component for the whole tab row, not five-to-eight individual buttons.
 *
 * @author oveen
 */
public final class TabBarPanel extends JPanel {

    private static final int MIN_TAB_WIDTH = 110;
    private static final int TAB_HEIGHT = 34;
    private static final int GAP = 10;
    private static final int H_PADDING = 22; // each side
    private static final Color COLOR_ACTIVE = new Color(231, 115, 36);
    private static final Color COLOR_DOT = new Color(220, 53, 69);
    private static final Font FONT = new Font("Segoe UI", Font.BOLD, 12);

    private final String[] labels;
    private final int[] counts; // same length as labels; a dot shows where counts[i] > 0. May be null.
    private final int activeIndex;
    private final int[] tabWidths;
    private final int[] tabX;
    private IntConsumer onTabClick;

    public TabBarPanel(String[] labels, int[] counts, int activeIndex) {
        this.labels = labels;
        this.counts = counts;
        this.activeIndex = activeIndex;
        this.tabWidths = new int[labels.length];
        this.tabX = new int[labels.length];

        FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics(FONT);
        int x = 0;
        for (int i = 0; i < labels.length; i++) {
            tabWidths[i] = Math.max(MIN_TAB_WIDTH, fm.stringWidth(labels[i]) + H_PADDING * 2);
            tabX[i] = x;
            x += tabWidths[i] + GAP;
        }
        int totalWidth = x - GAP;

        setOpaque(false);
        setPreferredSize(new Dimension(totalWidth, TAB_HEIGHT + 6));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < TabBarPanel.this.labels.length; i++) {
                    if (e.getX() >= tabX[i] && e.getX() < tabX[i] + tabWidths[i] && onTabClick != null) {
                        onTabClick.accept(i);
                        return;
                    }
                }
            }
        });
    }

    /** Runs with the clicked tab's index (0-based). */
    public void setOnTabClick(IntConsumer onTabClick) {
        this.onTabClick = onTabClick;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(FONT);
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < labels.length; i++) {
            int x = tabX[i];
            int w = tabWidths[i];
            g2.setColor(i == activeIndex ? COLOR_ACTIVE : Color.BLACK);
            g2.fill(new RoundRectangle2D.Float(x, 3, w, TAB_HEIGHT, TAB_HEIGHT, TAB_HEIGHT));

            g2.setColor(Color.WHITE);
            String label = labels[i];
            int tx = x + (w - fm.stringWidth(label)) / 2;
            int ty = 3 + (TAB_HEIGHT + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(label, tx, ty);

            if (counts != null && i < counts.length && counts[i] > 0) {
                g2.setColor(COLOR_DOT);
                g2.fillOval(x + w - 14, 0, 12, 12);
            }
        }
        g2.dispose();
    }
}
