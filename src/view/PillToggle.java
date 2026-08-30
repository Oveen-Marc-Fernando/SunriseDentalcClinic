package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

/**
 * A rounded, chip-style toggle matching a modern tag/chip picker: unselected
 * options render as a soft lavender pill with italic gray text; the selected
 * option drops the pill background and shows a check mark with solid text
 * instead (e.g. "✓ Good   Fair   Poor").
 *
 * This restyles a plain {@link JCheckBox} without touching it: the pill is
 * added as a same-bounds overlay directly on top of the real checkbox, from
 * outside any NetBeans GEN-BEGIN/GEN-END block, and mirrors the checkbox's
 * selected state on every click. The real checkbox itself is hidden (not
 * just painted over) so it can never show through — pills are transparent
 * in their selected state by design, matching the reference look. Everything
 * else (controller/model code reading {@code chk*.isSelected()}) keeps
 * working completely unchanged — only what's visually drawn on screen
 * changes.
 *
 * @author oveen
 */
public final class PillToggle {

    private static final Color PILL_BG = new Color(233, 226, 246);
    private static final Color PILL_BG_HOVER = new Color(222, 213, 240);
    private static final Color UNSELECTED_TEXT = new Color(150, 145, 165);
    private static final Color SELECTED_TEXT = new Color(45, 45, 45);
    private static final Color CHECK_COLOR = new Color(40, 40, 40);

    private PillToggle() {
    }

    /**
     * Overlays a pill-style toggle on top of {@code realBox}, initialized to
     * its current selected state and keeping it in sync from then on.
     * Independent (multi-select) — use {@link #attachExclusiveGroup} instead
     * when only one of several options should ever be selected at a time.
     *
     * @return the pill button, in case the caller wants to attach its own
     *         additional listener too
     */
    public static JToggleButton attach(JCheckBox realBox, String text) {
        JToggleButton pill = createPill(realBox, text);
        pill.addActionListener(e -> realBox.setSelected(pill.isSelected()));
        return pill;
    }

    /**
     * Overlays mutually-exclusive pill toggles on top of several real
     * checkboxes (e.g. an Oral Hygiene "Good / Fair / Poor" rating) — picking
     * one visually deselects the others, and all real checkboxes are kept in
     * sync so at most one is ever {@code true}.
     */
    public static void attachExclusiveGroup(JCheckBox[] boxes, String[] labels) {
        if (boxes.length != labels.length) {
            throw new IllegalArgumentException("boxes and labels must be the same length");
        }
        ButtonGroup group = new ButtonGroup();
        JToggleButton[] pills = new JToggleButton[boxes.length];
        for (int i = 0; i < boxes.length; i++) {
            pills[i] = createPill(boxes[i], labels[i]);
            group.add(pills[i]);
        }
        for (int i = 0; i < pills.length; i++) {
            pills[i].addActionListener(e -> {
                // ButtonGroup has already resolved final selected state for
                // every pill in the group by the time this fires, so re-sync
                // all real checkboxes to match — not just the one clicked.
                for (int j = 0; j < pills.length; j++) {
                    boxes[j].setSelected(pills[j].isSelected());
                }
            });
        }
    }

    private static JToggleButton createPill(JCheckBox realBox, String text) {
        Container parent = realBox.getParent();

        JToggleButton pill = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                boolean sel = isSelected();

                if (!sel) {
                    boolean hover = getModel().isRollover();
                    g2.setColor(hover ? PILL_BG_HOVER : PILL_BG);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, h, h));
                }

                Icon check = sel ? IconFactory.check(CHECK_COLOR, 11) : null;
                int checkSlot = check != null ? check.getIconWidth() + 4 : 0;

                g2.setFont(getFont().deriveFont(sel ? Font.PLAIN : Font.ITALIC, 12f));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int startX = Math.max(2, (w - textWidth - checkSlot) / 2);
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

                if (check != null) {
                    check.paintIcon(this, g2, startX, (h - check.getIconHeight()) / 2);
                }
                g2.setColor(sel ? SELECTED_TEXT : UNSELECTED_TEXT);
                g2.drawString(text, startX + checkSlot, textY);

                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setContentAreaFilled(false);
        pill.setBorderPainted(false);
        pill.setFocusPainted(false);
        pill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Rectangle bounds = realBox.getBounds();
        pill.setBounds(bounds);
        pill.setSelected(realBox.isSelected());

        // Hide the real checkbox outright rather than just painting over it —
        // the pill is fully transparent when selected (by design, matching
        // the reference look), so anything left visible underneath would
        // show through and produce garbled double-rendered text.
        realBox.setVisible(false);

        parent.add(pill);
        parent.setComponentZOrder(pill, 0); // z-order 0 = frontmost

        return pill;
    }
}
