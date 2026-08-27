package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * "My Schedule" — a read-only monthly calendar showing this dentist's
 * working days, with any day that already has a booked appointment
 * highlighted in red. Clicking a booked (red) day shows who it's booked
 * for and at what time — pulled from this dentist's own real rows in the
 * "appointments" table, not sample data. Reached from the Dentist
 * Dashboard's "My Schedule" tile.
 */
public class D_SCH_Calendar extends javax.swing.JFrame {

    private static final Color TEAL = new Color(30, 179, 172);
    private static final Color RED = new Color(220, 53, 69);
    private static final Color WEEKDAY_HEADER = new Color(110, 110, 110);
    private static final int CELL_SIZE = 46;
    private static final DateTimeFormatter DATE_DISPLAY = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_STORED = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_STORED = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME_DISPLAY = DateTimeFormatter.ofPattern("hh:mm a");

    /** One booked slot's details, shown when its calendar day is clicked. */
    private static final class BookedAppointment {
        final String time;
        final String patientName;
        BookedAppointment(String time, String patientName) {
            this.time = time;
            this.patientName = patientName;
        }
    }

    private final String dentistName;
    private final Map<LocalDate, List<BookedAppointment>> busyDates = new LinkedHashMap<>();

    private int viewYear;
    private int viewMonth; // 1-12

    /** Backward-compatible no-arg entry point (e.g. {@code main()}) — shows an empty calendar without a logged-in dentist. */
    public D_SCH_Calendar() {
        this(null);
    }

    public D_SCH_Calendar(model.User user) {
        this.dentistName = (user != null && user.getFullName() != null) ? user.getFullName() : null;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height

        // Vector chevrons instead of the "‹"/"›" text glyphs — those render
        // as "..." depending on the active font/JRE instead of an arrow.
        btnPrevYear.setText(null);
        btnPrevYear.setIcon(IconFactory.chevronLeft(Color.BLACK, 16));
        btnNextYear.setText(null);
        btnNextYear.setIcon(IconFactory.chevronRight(Color.BLACK, 16));
        btnPrevMonth.setText(null);
        btnPrevMonth.setIcon(IconFactory.chevronLeft(Color.BLACK, 16));
        btnNextMonth.setText(null);
        btnNextMonth.setIcon(IconFactory.chevronRight(Color.BLACK, 16));

        LocalDate today = LocalDate.now();
        viewYear = today.getYear();
        viewMonth = today.getMonthValue();

        loadBusyDates();
        renderCalendar();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    /** Reads this dentist's own real appointments (db/schema.sql) and groups them by date. */
    private void loadBusyDates() {
        if (dentistName == null) {
            return; // no logged-in dentist context (e.g. main()) — nothing to show
        }
        for (model.AppointmentModel a : controller.AppointmentManagementController.getForDentist(dentistName)) {
            LocalDate date = parseDate(a.getDate());
            if (date == null) continue;
            String time = formatTime(a.getTime());
            String patientName = (a.getPatientName() == null || a.getPatientName().trim().isEmpty())
                    ? "Unknown" : a.getPatientName().trim();
            busyDates.computeIfAbsent(date, d -> new ArrayList<>()).add(new BookedAppointment(time, patientName));
        }
    }

    private static LocalDate parseDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(raw.trim(), DATE_STORED);
        } catch (Exception e) {
            return null;
        }
    }

    /** "HH:mm" -&gt; "hh:mm a" for display; falls back to whatever was stored if it doesn't parse. */
    private static String formatTime(String raw) {
        if (raw == null || raw.trim().isEmpty()) return "-";
        try {
            return java.time.LocalTime.parse(raw.trim(), TIME_STORED).format(TIME_DISPLAY);
        } catch (Exception e) {
            return raw.trim();
        }
    }

    private void renderCalendar() {
        lblYear.setText(String.valueOf(viewYear));
        lblMonth.setText(YearMonth.of(viewYear, viewMonth).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

        pnlCalendarWrap.removeAll();

        JPanel grid = new JPanel(new java.awt.GridLayout(0, 7, 6, 14));
        grid.setOpaque(false);

        String[] weekdays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (String day : weekdays) {
            JLabel header = new JLabel(day, SwingConstants.CENTER);
            header.setFont(new Font("Segoe UI", Font.BOLD, 12));
            header.setForeground(WEEKDAY_HEADER);
            grid.add(header);
        }

        YearMonth ym = YearMonth.of(viewYear, viewMonth);
        LocalDate first = ym.atDay(1);
        int leadingBlanks = first.getDayOfWeek().getValue() % 7; // ISO Sunday=7 -> Sunday-first grid
        for (int i = 0; i < leadingBlanks; i++) {
            grid.add(new JLabel(""));
        }
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = ym.atDay(day);
            grid.add(dayCell(date, busyDates.get(date)));
        }

        pnlCalendarWrap.setLayout(new java.awt.BorderLayout());
        javax.swing.JPanel padded = new javax.swing.JPanel(new java.awt.BorderLayout());
        padded.setOpaque(false);
        padded.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        padded.add(grid, java.awt.BorderLayout.CENTER);
        pnlCalendarWrap.add(padded, java.awt.BorderLayout.CENTER);

        pnlCalendarWrap.revalidate();
        pnlCalendarWrap.repaint();
    }

    /**
     * A single day number drawn as a filled rounded-square button — teal
     * for a free day, red for one that already has appointments booked.
     * Booked days are clickable: they list every appointment that day —
     * time and patient name each — not just a single one.
     */
    private JLabel dayCell(LocalDate date, List<BookedAppointment> appts) {
        boolean busy = appts != null && !appts.isEmpty();
        Color color = busy ? RED : TEAL;
        JLabel cell = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                int d = Math.min(getWidth(), getHeight());
                float x = (getWidth() - d) / 2f;
                float y = (getHeight() - d) / 2f;
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(x, y, d, d, d * 0.3f, d * 0.3f));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cell.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cell.setForeground(Color.WHITE);
        cell.setPreferredSize(new java.awt.Dimension(CELL_SIZE, CELL_SIZE));

        if (busy) {
            cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cell.setToolTipText(appts.size() == 1
                    ? appts.get(0).patientName + " — " + appts.get(0).time
                    : appts.size() + " appointments");
            cell.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    showDayScheduleDialog(date, appts);
                }
            });
        }
        return cell;
    }

    // =========================================================================
    // Day Schedule dialog — a scrollable list of every appointment on the
    // clicked day, same "build our own popup" header/scroll/footer pattern
    // as the Help Desk guide elsewhere in this app. A plain fixed-size
    // popup doesn't work here since a day can have anywhere from one to a
    // full day's worth of appointments.
    // =========================================================================

    private void showDayScheduleDialog(LocalDate date, List<BookedAppointment> appts) {
        final int dialogWidth = 420;
        final int dialogHeight = 480;

        javax.swing.JDialog dialog = new javax.swing.JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(this);

        javax.swing.JPanel pnlModal = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlModal.setBackground(Color.WHITE);
        pnlModal.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));

        // Header — same black navbar look every other custom dialog in this
        // app uses, with our own close glyph instead of the OS window's X.
        javax.swing.JPanel pnlHeader = new javax.swing.JPanel(null);
        pnlHeader.setBackground(Color.BLACK);
        pnlHeader.setPreferredSize(new java.awt.Dimension(10, 54));

        javax.swing.JLabel lblHeaderTitle = new javax.swing.JLabel(
                "Day Schedule on \"" + date.format(DATE_DISPLAY) + "\"");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(18, 0, dialogWidth - 60, 54);
        pnlHeader.add(lblHeaderTitle);

        javax.swing.JButton btnCloseX = IconFactory.actionButton(
                IconFactory.cross(Color.WHITE, 12), new Color(220, 53, 69), "Close");
        btnCloseX.setBounds(dialogWidth - 44, 13, 28, 28);
        btnCloseX.addActionListener(e -> dialog.dispose());
        pnlHeader.add(btnCloseX);

        // Undecorated windows lose the native title bar's drag-to-move, so
        // dragging the custom header moves the dialog instead.
        final java.awt.Point[] dragStart = {null};
        pnlHeader.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
        });
        pnlHeader.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragStart[0] == null) return;
                java.awt.Point loc = dialog.getLocation();
                dialog.setLocation(loc.x + e.getX() - dragStart[0].x, loc.y + e.getY() - dragStart[0].y);
            }
        });

        pnlModal.add(pnlHeader, java.awt.BorderLayout.NORTH);

        // Body — one row per appointment, scrollable once there are enough
        // to overflow the visible area.
        final int rowWidth = dialogWidth - 64;
        final int rowHeight = 56;
        final int rowGap = 10;

        javax.swing.JPanel listPanel = new javax.swing.JPanel(null);
        listPanel.setBackground(Color.WHITE);
        int y = 14;
        for (BookedAppointment appt : appts) {
            javax.swing.JPanel row = appointmentRow(appt, rowWidth, rowHeight);
            row.setBounds(16, y, rowWidth, rowHeight);
            listPanel.add(row);
            y += rowHeight + rowGap;
        }
        listPanel.setPreferredSize(new java.awt.Dimension(dialogWidth - 20, y));

        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(listPanel);
        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        IconFactory.styleScrollBar(scroll); // flat thumb, no arrow buttons — not the OS default look
        pnlModal.add(scroll, java.awt.BorderLayout.CENTER);

        // Footer
        javax.swing.JPanel pnlFooter = new javax.swing.JPanel();
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0));
        javax.swing.JButton btnClose = new javax.swing.JButton("OK");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBackground(new Color(231, 115, 36));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dialog.dispose());
        pnlFooter.add(btnClose);
        pnlModal.add(pnlFooter, java.awt.BorderLayout.SOUTH);

        dialog.getContentPane().add(pnlModal);
        dialog.setVisible(true);
    }

    /** One appointment row in the Day Schedule dialog: time (bold) above patient name. */
    private JPanel appointmentRow(BookedAppointment appt, int width, int height) {
        JPanel row = new JPanel(null);
        row.setBackground(new Color(245, 245, 245));
        IconFactory.roundCorners(row, 10);

        JLabel lblTime = new JLabel(appt.time);
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTime.setForeground(new Color(30, 30, 30));
        lblTime.setBounds(16, 8, width - 32, 22);
        row.add(lblTime);

        JLabel lblPatient = new JLabel(appt.patientName);
        lblPatient.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPatient.setForeground(new Color(100, 100, 100));
        lblPatient.setBounds(16, 30, width - 32, 20);
        row.add(lblPatient);

        return row;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        btnPrevYear = new javax.swing.JButton();
        lblYear = new javax.swing.JLabel();
        btnNextYear = new javax.swing.JButton();
        btnPrevMonth = new javax.swing.JButton();
        lblMonth = new javax.swing.JLabel();
        btnNextMonth = new javax.swing.JButton();
        pnlCalendarWrap = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – My Schedule");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 170, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("My Schedule");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 300, 40);

        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.setBackground(new java.awt.Color(0, 122, 255));
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        mainPanel.add(btnBack);
        btnBack.setBounds(50, 175, 100, 36);

        btnPrevYear.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnPrevYear.setText("‹");
        btnPrevYear.setBorderPainted(false);
        btnPrevYear.setContentAreaFilled(false);
        btnPrevYear.setFocusPainted(false);
        btnPrevYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevYearActionPerformed(evt);
            }
        });
        mainPanel.add(btnPrevYear);
        btnPrevYear.setBounds(720, 160, 30, 30);

        lblYear.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblYear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblYear.setText("2026");
        mainPanel.add(lblYear);
        lblYear.setBounds(750, 160, 90, 30);

        btnNextYear.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnNextYear.setText("›");
        btnNextYear.setBorderPainted(false);
        btnNextYear.setContentAreaFilled(false);
        btnNextYear.setFocusPainted(false);
        btnNextYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextYearActionPerformed(evt);
            }
        });
        mainPanel.add(btnNextYear);
        btnNextYear.setBounds(840, 160, 30, 30);

        btnPrevMonth.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnPrevMonth.setText("‹");
        btnPrevMonth.setBorderPainted(false);
        btnPrevMonth.setContentAreaFilled(false);
        btnPrevMonth.setFocusPainted(false);
        btnPrevMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevMonthActionPerformed(evt);
            }
        });
        mainPanel.add(btnPrevMonth);
        btnPrevMonth.setBounds(340, 160, 30, 30);

        lblMonth.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblMonth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMonth.setText("August");
        mainPanel.add(lblMonth);
        lblMonth.setBounds(370, 160, 170, 30);

        btnNextMonth.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnNextMonth.setText("›");
        btnNextMonth.setBorderPainted(false);
        btnNextMonth.setContentAreaFilled(false);
        btnNextMonth.setFocusPainted(false);
        btnNextMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextMonthActionPerformed(evt);
            }
        });
        mainPanel.add(btnNextMonth);
        btnNextMonth.setBounds(540, 160, 30, 30);

        pnlCalendarWrap.setBackground(new java.awt.Color(255, 255, 255));
        pnlCalendarWrap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlCalendarWrap.setLayout(null);
        mainPanel.add(pnlCalendarWrap);
        pnlCalendarWrap.setBounds(50, 225, 900, 440);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        // Must carry the real signed-in user back — the no-arg constructor
        // passes null, which silently breaks every "my own data" screen
        // reachable from that dashboard afterward (empty grids/schedules).
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Dentist_Dashboard(controller.AppController.getCurrentUser()).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnPrevYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevYearActionPerformed
        viewYear--;
        renderCalendar();
    }//GEN-LAST:event_btnPrevYearActionPerformed

    private void btnNextYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextYearActionPerformed
        viewYear++;
        renderCalendar();
    }//GEN-LAST:event_btnNextYearActionPerformed

    private void btnPrevMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevMonthActionPerformed
        YearMonth prev = YearMonth.of(viewYear, viewMonth).minusMonths(1);
        viewYear = prev.getYear();
        viewMonth = prev.getMonthValue();
        renderCalendar();
    }//GEN-LAST:event_btnPrevMonthActionPerformed

    private void btnNextMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextMonthActionPerformed
        YearMonth next = YearMonth.of(viewYear, viewMonth).plusMonths(1);
        viewYear = next.getYear();
        viewMonth = next.getMonthValue();
        renderCalendar();
    }//GEN-LAST:event_btnNextMonthActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new D_SCH_Calendar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNextMonth;
    private javax.swing.JButton btnNextYear;
    private javax.swing.JButton btnPrevMonth;
    private javax.swing.JButton btnPrevYear;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMonth;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblYear;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlCalendarWrap;
    // End of variables declaration//GEN-END:variables
}
