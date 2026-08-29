package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Attaches a small calendar/clock icon to the right edge of a plain
 * {@link JTextField} — clicking it pops up a date, year, or time picker
 * instead of the user having to type the value by hand.
 *
 * This is deliberately built to work entirely from OUTSIDE NetBeans'
 * GEN-BEGIN/GEN-END blocks: it only reads a field's existing bounds/parent
 * (already set by initComponents()) and adds a sibling icon component next
 * to it. Call it from a view's constructor right after initComponents(),
 * the same way search-bar setup already does — never from inside generated
 * code, or the GUI Builder will silently strip it the next time the paired
 * .form file is regenerated.
 *
 * @author oveen
 */
public final class DateTimePicker {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final Color ICON_COLOR = new Color(90, 90, 90);
    private static final Color ACCENT = new Color(0, 122, 255);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final int ICON_SIZE = 15;

    private DateTimePicker() {
    }

    // =========================================================================
    // Public entry points
    // =========================================================================

    /** Adds a calendar icon that opens a month-calendar popup; writes yyyy-MM-dd. */
    public static void attachDate(JTextField field) {
        addIcon(field, IconFactory.calendar(ICON_COLOR, ICON_SIZE), () -> showDatePopup(field));
    }

    /** Adds a calendar icon that opens a decade year-grid popup; writes yyyy. */
    public static void attachYear(JTextField field) {
        addIcon(field, IconFactory.calendar(ICON_COLOR, ICON_SIZE), () -> showYearPopup(field));
    }

    /** Adds a clock icon that opens an hour/minute popup; writes HH:mm. */
    public static void attachTime(JTextField field) {
        addIcon(field, IconFactory.clock(ICON_COLOR, ICON_SIZE), () -> showTimePopup(field));
    }

    // =========================================================================
    // Icon placement (right edge of the field, docked on top of it)
    // =========================================================================

    private static void addIcon(JTextField field, javax.swing.Icon glyph, Runnable onClick) {
        IconFactory.dockIconInField(field, glyph, "Pick", onClick);
    }

    // =========================================================================
    // Date popup — drills down Year -> Month -> Day, in that order, so
    // picking a birthdate decades back never needs month-by-month clicking.
    // =========================================================================

    private static void showDatePopup(JTextField field) {
        LocalDate initial = parseDateOrNull(field.getText());
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        DrillDownDatePanel panel = new DrillDownDatePanel(initial, date -> {
            field.setText(date.format(DATE_FORMAT));
            popup.setVisible(false);
        }, popup::pack);
        popup.add(panel);
        popup.show(field, 0, field.getHeight());
    }

    private static LocalDate parseDateOrNull(String text) {
        if (text == null) {
            return null;
        }
        String t = text.trim();
        if (t.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(t, DATE_FORMAT);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Three-level date picker: pick a year, then a month within that year,
     * then a day within that month — the natural order for a birthdate
     * that may be decades in the past. The header doubles as a "back up a
     * level" button once inside Month/Day; prev/next step within the
     * current level (decade / year / month).
     */
    private static final class DrillDownDatePanel extends JPanel {
        private enum Mode { YEAR, MONTH, DAY }

        private static final String[] MONTH_NAMES = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        private Mode mode = Mode.YEAR;
        private int year;
        private int month; // 1-12, meaningful once inside DAY mode
        private int decadeStart;
        private final LocalDate selected; // nullable: null means field had no valid date yet
        private final Consumer<LocalDate> onPick;
        private final Runnable onResize;

        private final JButton header = new JButton();
        private final JPanel grid = new JPanel();

        DrillDownDatePanel(LocalDate initial, Consumer<LocalDate> onPick, Runnable onResize) {
            this.selected = initial;
            this.onPick = onPick;
            this.onResize = onResize;

            LocalDate anchor = initial != null ? initial : LocalDate.now();
            this.year = anchor.getYear();
            this.month = anchor.getMonthValue();
            this.decadeStart = (year / 10) * 10 - 1;

            setLayout(new BorderLayout(4, 4));
            setBorder(new EmptyBorder(8, 8, 8, 8));
            setBackground(Color.WHITE);

            JButton prev = navButton("‹");
            JButton next = navButton("›");
            prev.addActionListener(e -> step(-1));
            next.addActionListener(e -> step(1));

            header.setFocusPainted(false);
            header.setBorderPainted(false);
            header.setContentAreaFilled(false);
            header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));
            header.setForeground(ACCENT);
            header.setToolTipText("Back to year / month selection");
            header.addActionListener(e -> goUp());

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(prev, BorderLayout.WEST);
            top.add(header, BorderLayout.CENTER);
            top.add(next, BorderLayout.EAST);

            grid.setOpaque(false);
            add(top, BorderLayout.NORTH);
            add(grid, BorderLayout.CENTER);

            render();
        }

        private void step(int direction) {
            if (mode == Mode.YEAR) {
                decadeStart += direction * 12;
            } else if (mode == Mode.MONTH) {
                year += direction;
            } else {
                YearMonth ym = YearMonth.of(year, month).plusMonths(direction);
                year = ym.getYear();
                month = ym.getMonthValue();
            }
            render();
        }

        private void goUp() {
            if (mode == Mode.DAY) {
                mode = Mode.MONTH;
            } else if (mode == Mode.MONTH) {
                mode = Mode.YEAR;
            }
            render();
        }

        private void render() {
            if (mode == Mode.YEAR) {
                renderYear();
            } else if (mode == Mode.MONTH) {
                renderMonth();
            } else {
                renderDay();
            }
            revalidate();
            repaint();
            if (onResize != null) {
                onResize.run();
            }
        }

        private void renderYear() {
            header.setText((decadeStart + 1) + " – " + (decadeStart + 12));
            grid.removeAll();
            grid.setLayout(new GridLayout(4, 3, 4, 4));
            int currentYear = LocalDate.now().getYear();
            for (int i = 0; i < 12; i++) {
                int y = decadeStart + 1 + i;
                boolean isSelected = selected != null && y == selected.getYear();
                JButton cell = dayCell(String.valueOf(y), isSelected, y == currentYear);
                cell.addActionListener(e -> {
                    year = y;
                    mode = Mode.MONTH;
                    render();
                });
                grid.add(cell);
            }
        }

        private void renderMonth() {
            header.setText(String.valueOf(year));
            grid.removeAll();
            grid.setLayout(new GridLayout(4, 3, 4, 4));
            LocalDate today = LocalDate.now();
            for (int m = 1; m <= 12; m++) {
                boolean isSelected = selected != null && selected.getYear() == year && selected.getMonthValue() == m;
                boolean isCurrent = today.getYear() == year && today.getMonthValue() == m;
                JButton cell = dayCell(MONTH_NAMES[m - 1], isSelected, isCurrent);
                int pickedMonth = m;
                cell.addActionListener(e -> {
                    month = pickedMonth;
                    mode = Mode.DAY;
                    render();
                });
                grid.add(cell);
            }
        }

        private void renderDay() {
            YearMonth ym = YearMonth.of(year, month);
            header.setText(ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year);
            grid.removeAll();
            grid.setLayout(new GridLayout(0, 7, 2, 2));

            for (String d : new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"}) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(l.getFont().deriveFont(Font.BOLD, 10.5f));
                l.setForeground(new Color(140, 140, 140));
                grid.add(l);
            }

            LocalDate first = ym.atDay(1);
            int leadingBlanks = first.getDayOfWeek().getValue() % 7; // ISO Sunday=7 -> Sunday-first grid
            LocalDate today = LocalDate.now();

            for (int i = 0; i < leadingBlanks; i++) {
                grid.add(new JLabel(""));
            }
            for (int day = 1; day <= ym.lengthOfMonth(); day++) {
                LocalDate date = ym.atDay(day);
                boolean isSelected = date.equals(selected);
                boolean isToday = date.equals(today);
                JButton cell = dayCell(String.valueOf(day), isSelected, isToday);
                cell.addActionListener(e -> onPick.accept(date));
                grid.add(cell);
            }
        }
    }

    // =========================================================================
    // Year popup (decade grid)
    // =========================================================================

    private static void showYearPopup(JTextField field) {
        int initial = parseYearOrCurrent(field.getText());
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        popup.add(new YearPanel(initial, year -> {
            field.setText(String.valueOf(year));
            popup.setVisible(false);
        }));
        popup.show(field, 0, field.getHeight());
    }

    private static int parseYearOrCurrent(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception ex) {
            return LocalDate.now().getYear();
        }
    }

    /** Compact decade-grid year picker with prev/next decade navigation. */
    private static final class YearPanel extends JPanel {
        private int decadeStart;
        private final int selected;
        private final Consumer<Integer> onPick;
        private final JLabel header = new JLabel("", SwingConstants.CENTER);
        private final JPanel grid = new JPanel(new GridLayout(4, 3, 4, 4));

        YearPanel(int initialYear, Consumer<Integer> onPick) {
            this.decadeStart = (initialYear / 10) * 10 - 1;
            this.selected = initialYear;
            this.onPick = onPick;

            setLayout(new BorderLayout(4, 4));
            setBorder(new EmptyBorder(8, 8, 8, 8));
            setBackground(Color.WHITE);

            JButton prev = navButton("‹");
            JButton next = navButton("›");
            prev.addActionListener(e -> {
                decadeStart -= 12;
                render();
            });
            next.addActionListener(e -> {
                decadeStart += 12;
                render();
            });

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(prev, BorderLayout.WEST);
            top.add(header, BorderLayout.CENTER);
            top.add(next, BorderLayout.EAST);
            header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));

            grid.setOpaque(false);
            add(top, BorderLayout.NORTH);
            add(grid, BorderLayout.CENTER);
            render();
        }

        private void render() {
            header.setText((decadeStart + 1) + " – " + (decadeStart + 12));
            grid.removeAll();
            for (int i = 0; i < 12; i++) {
                int year = decadeStart + 1 + i;
                JButton cell = dayCell(String.valueOf(year), year == selected, false);
                cell.addActionListener(e -> onPick.accept(year));
                grid.add(cell);
            }
            revalidate();
            repaint();
        }
    }

    // =========================================================================
    // Time popup (hour / minute spinners)
    // =========================================================================

    private static void showTimePopup(JTextField field) {
        LocalTime initial = parseTimeOrNow(field.getText());
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(initial.getHour(), 0, 23, 1));
        JSpinner minSpinner = new JSpinner(new SpinnerNumberModel(initial.getMinute(), 0, 59, 5));
        formatSpinner(hourSpinner);
        formatSpinner(minSpinner);

        JButton ok = new JButton("Set");
        ok.setFocusPainted(false);
        ok.setBackground(ACCENT);
        ok.setForeground(Color.WHITE);
        ok.setBorderPainted(false);
        ok.setOpaque(true);
        ok.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ok.addActionListener(e -> {
            LocalTime t = LocalTime.of((Integer) hourSpinner.getValue(), (Integer) minSpinner.getValue());
            field.setText(t.format(TIME_FORMAT));
            popup.setVisible(false);
        });

        panel.add(hourSpinner);
        panel.add(new JLabel(":"));
        panel.add(minSpinner);
        panel.add(ok);

        popup.add(panel);
        popup.show(field, 0, field.getHeight());
    }

    private static void formatSpinner(JSpinner spinner) {
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "00"));
        spinner.setPreferredSize(new Dimension(50, 26));
    }

    private static LocalTime parseTimeOrNow(String text) {
        try {
            return LocalTime.parse(text.trim(), TIME_FORMAT);
        } catch (Exception ex) {
            return LocalTime.now().withSecond(0).withNano(0);
        }
    }

    // =========================================================================
    // Shared cell button style (used by both the day grid and the year grid)
    // =========================================================================

    private static JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        return b;
    }

    private static JButton dayCell(String text, boolean selected, boolean today) {
        JButton cell = new JButton(text);
        cell.setMargin(new Insets(2, 2, 2, 2));
        cell.setFocusPainted(false);
        cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cell.setFont(cell.getFont().deriveFont(11.5f));
        if (selected) {
            cell.setOpaque(true);
            cell.setContentAreaFilled(true);
            cell.setBorderPainted(false);
            cell.setBackground(ACCENT);
            cell.setForeground(Color.WHITE);
        } else {
            cell.setContentAreaFilled(false);
            cell.setBorderPainted(today);
            if (today) {
                cell.setBorder(BorderFactory.createLineBorder(ACCENT));
            }
        }
        return cell;
    }
}
