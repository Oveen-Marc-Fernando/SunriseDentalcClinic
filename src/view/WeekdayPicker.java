package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Attaches a small calendar icon to the right edge of a plain
 * {@link JTextField} — clicking it pops up a Monday-first checkbox list of
 * the week's days instead of the user having to type them by hand. Hitting
 * "Apply" writes the checked days back as a comma-separated list (e.g.
 * "Monday, Wednesday, Friday") — the same free-text shape
 * {@code AppointmentManagementController.parseWorkingDays()} already
 * parses, so nothing downstream needs to change.
 *
 * Built the same way as {@link DateTimePicker}: entirely from OUTSIDE
 * NetBeans GEN-BEGIN/GEN-END blocks — call it from a view's constructor
 * right after initComponents(), or the GUI Builder will strip it on the
 * next .form regeneration.
 *
 * @author oveen
 */
public final class WeekdayPicker {

    private static final Color ICON_COLOR = new Color(90, 90, 90);
    private static final Color ACCENT = new Color(0, 122, 255);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final int ICON_SIZE = 15;

    private static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    private WeekdayPicker() {
    }

    /**
     * Makes the field selection-only (typing disabled) and adds a calendar
     * icon that opens the day-of-week checkbox popup.
     */
    public static void attach(JTextField field) {
        field.setEditable(false);
        IconFactory.dockIconInField(field, IconFactory.calendar(ICON_COLOR, ICON_SIZE), "Pick working days", () -> showPopup(field));
    }

    private static void showPopup(JTextField field) {
        Set<String> initial = parseSelected(field.getText());

        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel grid = new JPanel(new GridLayout(DAYS.length, 1, 0, 4));
        grid.setOpaque(false);
        JCheckBox[] boxes = new JCheckBox[DAYS.length];
        for (int i = 0; i < DAYS.length; i++) {
            JCheckBox box = new JCheckBox(DAYS[i]);
            box.setOpaque(false);
            box.setFont(box.getFont().deriveFont(13f));
            box.setSelected(initial.contains(DAYS[i]));
            box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boxes[i] = box;
            grid.add(box);
        }

        JButton apply = new JButton("Apply");
        apply.setFocusPainted(false);
        apply.setBackground(ACCENT);
        apply.setForeground(Color.WHITE);
        apply.setBorderPainted(false);
        apply.setOpaque(true);
        apply.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        apply.setFont(apply.getFont().deriveFont(Font.BOLD, 13f));
        apply.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (JCheckBox box : boxes) {
                if (box.isSelected()) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(box.getText());
                }
            }
            field.setText(sb.toString());
            popup.setVisible(false);
        });

        JPanel applyWrap = new JPanel(new BorderLayout());
        applyWrap.setOpaque(false);
        applyWrap.add(apply, BorderLayout.EAST);

        panel.add(grid, BorderLayout.CENTER);
        panel.add(applyWrap, BorderLayout.SOUTH);

        popup.add(panel);
        popup.show(field, 0, field.getHeight());
    }

    /** Parses the field's current comma-separated day list back into a lookup set, so re-opening the popup pre-checks the right boxes. */
    private static Set<String> parseSelected(String text) {
        Set<String> set = new LinkedHashSet<>();
        if (text == null || text.trim().isEmpty()) {
            return set;
        }
        for (String token : text.split(",")) {
            String t = token.trim();
            for (String day : DAYS) {
                if (day.equalsIgnoreCase(t)) {
                    set.add(day);
                    break;
                }
            }
        }
        return set;
    }
}
