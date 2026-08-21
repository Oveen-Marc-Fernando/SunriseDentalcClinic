package view;

import controller.DentistManagementController;
import controller.LeaveRequestController;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.util.Set;
import model.DentistModel;
import model.User;

/**
 * "Request Leaves" — reached from the Dentist Dashboard's "Request Leaves"
 * tile. Shows the dentist's own Working Days / Start Time / End Time / Break
 * Time as read-only reference (pulled from the same DentistManagementController
 * directory D_Profile and Appointment Management both read from), and lets
 * them pick a date to request off.
 *
 * @author oveen
 */
public class D_RS_Leave extends javax.swing.JFrame {

    private static final Color COLOR_READONLY_BG = new Color(238, 238, 238);
    private static final Color COLOR_READONLY_FG = new Color(90, 90, 90);
    private static final Color COLOR_NEUTRAL = new Color(204, 204, 255);
    private static final Color COLOR_CAN_APPLY = new Color(0, 200, 83);
    private static final Color COLOR_CANNOT_APPLY = new Color(211, 47, 47);
    private static final java.time.format.DateTimeFormatter LEAVE_DATE_FMT = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final User user;
    private final String dentistName;

    private Set<DayOfWeek> workingDays = java.util.Collections.emptySet();
    private boolean leaveApplicable = false; // safety net behind the disabled button — Request never submits unless this is true

    public D_RS_Leave(User user) {
        this.user = user;
        this.dentistName = (user != null && user.getFullName() != null) ? user.getFullName() : "Dentist";
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        lblUserIcon.setIcon(IconFactory.userGlyph(Color.WHITE, 26)); // crisp vector glyph, no backdrop — sits directly on the black pill
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        bindUserIcon();
        prefillFromProfile();
        DateTimePicker.attachDate(txtLeaveDate);
        setupEligibilityCheck();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    /**
     * btnRequest ("Check Availability" / "Can Apply Leave" / "Cannot Apply
     * Leave") gates btnRequest1 ("Request"): the Request button starts
     * disabled and only turns on after a check confirms the picked date is
     * both one of the dentist's working days and free of an existing
     * appointment. Changing the date invalidates whatever check already ran.
     */
    private void setupEligibilityCheck() {
        resetEligibilityState();
        txtLeaveDate.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { resetEligibilityState(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { resetEligibilityState(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { resetEligibilityState(); }
        });
    }

    private void resetEligibilityState() {
        leaveApplicable = false;
        btnRequest1.setEnabled(false);
        btnRequest.setText("Check Availability");
        btnRequest.setBackground(COLOR_NEUTRAL);
    }

    private void bindUserIcon() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblUserIcon.setCursor(hand);
        lblUserIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                IconFactory.showProfileMenu(D_RS_Leave.this, lblUserIcon,
                        () -> javax.swing.SwingUtilities.invokeLater(() -> {
                            dispose();
                            new D_Profile_1(user).setVisible(true);
                        }),
                        () -> {
                            dispose();
                            controller.AppController.showPublicDashboard();
                        });
            }
        });
    }

    /** Pulls the dentist's own schedule (read-only reference) and last approved leave from shared directories. */
    private void prefillFromProfile() {
        lockField(txtStartTime);
        lockField(txtEndTime);
        lockField(txtBreakTime);
        lockField(txtLeaveApproved);

        DentistModel dentist = DentistManagementController.getDirectory().get(dentistName);
        if (dentist != null) {
            txtStartTime.setText(dentist.getStartTime());
            txtEndTime.setText(dentist.getEndTime());
            txtBreakTime.setText(dentist.getBreakTime());
            workingDays = controller.AppointmentManagementController.parseWorkingDays(dentist.getWorkingDays());
            checkWorkingDays(workingDays);
        }

        String lastApproved = LeaveRequestController.getLastApprovedDate(dentistName);
        txtLeaveApproved.setText(lastApproved.isEmpty() ? "None yet" : lastApproved);
    }

    private void lockField(javax.swing.JTextField field) {
        field.setEditable(false);
        field.setBackground(COLOR_READONLY_BG);
        field.setForeground(COLOR_READONLY_FG);
    }

    /** Pre-checks each day's box to show — for reference only — which days are this dentist's normal working days. */
    private void checkWorkingDays(Set<DayOfWeek> days) {
        chkSunday.setSelected(days.contains(DayOfWeek.SUNDAY));
        chkMonday.setSelected(days.contains(DayOfWeek.MONDAY));
        chkTuesday.setSelected(days.contains(DayOfWeek.TUESDAY));
        chkWednesday.setSelected(days.contains(DayOfWeek.WEDNESDAY));
        chkThursday.setSelected(days.contains(DayOfWeek.THURSDAY));
        chkFriday.setSelected(days.contains(DayOfWeek.FRIDAY));
        chkSaturday.setSelected(days.contains(DayOfWeek.SATURDAY));
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblUserIcon = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        lblLeaveApprovedCaption = new javax.swing.JLabel();
        txtLeaveApproved = new javax.swing.JTextField();
        cardPanel = new javax.swing.JPanel();
        lblWorkingDaysHeader = new javax.swing.JLabel();
        chkSunday = new javax.swing.JCheckBox();
        chkMonday = new javax.swing.JCheckBox();
        chkTuesday = new javax.swing.JCheckBox();
        chkWednesday = new javax.swing.JCheckBox();
        chkThursday = new javax.swing.JCheckBox();
        chkFriday = new javax.swing.JCheckBox();
        chkSaturday = new javax.swing.JCheckBox();
        lblLeaveDetailsHeader = new javax.swing.JLabel();
        lblStartTime = new javax.swing.JLabel();
        txtStartTime = new javax.swing.JTextField();
        lblEndTime = new javax.swing.JLabel();
        txtEndTime = new javax.swing.JTextField();
        lblBreakTime = new javax.swing.JLabel();
        txtBreakTime = new javax.swing.JTextField();
        lblCheckForLeave = new javax.swing.JLabel();
        txtLeaveDate = new javax.swing.JTextField();
        lblWarning = new javax.swing.JLabel();
        btnRequest = new javax.swing.JButton();
        btnRequest1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Request Leaves");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 170, 40);

        lblUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/login_scaled.png"))); // NOI18N
        lblUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        navBar.add(lblUserIcon);
        lblUserIcon.setBounds(850, 10, 40, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("Request Leaves");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 320, 40);

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

        lblLeaveApprovedCaption.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblLeaveApprovedCaption.setText("Leave Approved:");
        mainPanel.add(lblLeaveApprovedCaption);
        lblLeaveApprovedCaption.setBounds(490, 183, 150, 20);

        txtLeaveApproved.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        mainPanel.add(txtLeaveApproved);
        txtLeaveApproved.setBounds(650, 178, 180, 30);

        cardPanel.setBackground(new java.awt.Color(248, 249, 250));
        cardPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.setLayout(null);

        lblWorkingDaysHeader.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblWorkingDaysHeader.setForeground(new java.awt.Color(231, 115, 36));
        lblWorkingDaysHeader.setText("Working Days");
        cardPanel.add(lblWorkingDaysHeader);
        lblWorkingDaysHeader.setBounds(40, 20, 300, 30);

        chkSunday.setText("Sunday");
        chkSunday.setEnabled(false);
        cardPanel.add(chkSunday);
        chkSunday.setBounds(40, 100, 300, 24);

        chkMonday.setText("Monday");
        chkMonday.setEnabled(false);
        cardPanel.add(chkMonday);
        chkMonday.setBounds(40, 130, 300, 24);

        chkTuesday.setText("Tuesday");
        chkTuesday.setEnabled(false);
        cardPanel.add(chkTuesday);
        chkTuesday.setBounds(40, 160, 300, 24);

        chkWednesday.setText("Wednesday");
        chkWednesday.setEnabled(false);
        cardPanel.add(chkWednesday);
        chkWednesday.setBounds(40, 190, 300, 24);

        chkThursday.setText("Thursday");
        chkThursday.setEnabled(false);
        cardPanel.add(chkThursday);
        chkThursday.setBounds(40, 220, 300, 24);

        chkFriday.setText("Friday");
        chkFriday.setEnabled(false);
        cardPanel.add(chkFriday);
        chkFriday.setBounds(40, 250, 300, 24);

        chkSaturday.setText("Saturday");
        chkSaturday.setEnabled(false);
        cardPanel.add(chkSaturday);
        chkSaturday.setBounds(40, 280, 300, 24);

        lblLeaveDetailsHeader.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLeaveDetailsHeader.setForeground(new java.awt.Color(231, 115, 36));
        lblLeaveDetailsHeader.setText("Working Times");
        cardPanel.add(lblLeaveDetailsHeader);
        lblLeaveDetailsHeader.setBounds(430, 20, 300, 30);

        lblStartTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblStartTime.setText("Start Time");
        cardPanel.add(lblStartTime);
        lblStartTime.setBounds(430, 65, 150, 20);

        txtStartTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtStartTime);
        txtStartTime.setBounds(430, 88, 350, 32);

        lblEndTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblEndTime.setText("End Time");
        cardPanel.add(lblEndTime);
        lblEndTime.setBounds(430, 130, 150, 20);

        txtEndTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtEndTime);
        txtEndTime.setBounds(430, 153, 350, 32);

        lblBreakTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblBreakTime.setText("Break Time");
        cardPanel.add(lblBreakTime);
        lblBreakTime.setBounds(430, 195, 150, 20);

        txtBreakTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtBreakTime);
        txtBreakTime.setBounds(430, 218, 350, 32);

        lblCheckForLeave.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblCheckForLeave.setText("Check For Leave");
        cardPanel.add(lblCheckForLeave);
        lblCheckForLeave.setBounds(430, 260, 200, 20);

        txtLeaveDate.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtLeaveDate);
        txtLeaveDate.setBounds(430, 283, 350, 32);

        lblWarning.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblWarning.setForeground(new java.awt.Color(198, 48, 48));
        lblWarning.setText("<html>Please note: only request leave for a day you don't already have an appointment on, and apply a few days ahead so it can be reviewed in time.</html>");
        cardPanel.add(lblWarning);
        lblWarning.setBounds(50, 350, 380, 55);

        btnRequest.setBackground(new java.awt.Color(204, 204, 255));
        btnRequest.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRequest.setForeground(new java.awt.Color(255, 255, 255));
        btnRequest.setBorderPainted(false);
        btnRequest.setFocusPainted(false);
        btnRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRequestActionPerformed(evt);
            }
        });
        cardPanel.add(btnRequest);
        btnRequest.setBounds(520, 340, 190, 36);

        btnRequest1.setBackground(new java.awt.Color(0, 200, 83));
        btnRequest1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRequest1.setForeground(new java.awt.Color(255, 255, 255));
        btnRequest1.setText("Request");
        btnRequest1.setBorderPainted(false);
        btnRequest1.setFocusPainted(false);
        btnRequest1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRequest1ActionPerformed(evt);
            }
        });
        cardPanel.add(btnRequest1);
        btnRequest1.setBounds(700, 400, 110, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 225, 880, 460);

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
            new Dentist_Dashboard(user).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    /**
     * "Check Availability" — validates the picked date against this
     * dentist's own Working Days and existing appointments, then relabels
     * itself with the result. Only a "Can Apply Leave" result unlocks the
     * Request button below.
     */
    private void btnRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRequestActionPerformed
        String dateText = txtLeaveDate.getText();
        if (dateText == null || dateText.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please choose a date first.", "Validation Error",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.time.LocalDate date;
        try {
            date = java.time.LocalDate.parse(dateText.trim(), LEAVE_DATE_FMT);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "That date doesn't look valid — please pick it from the calendar.", "Validation Error",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isWorkingDay = workingDays.contains(date.getDayOfWeek());
        boolean hasAppointment = controller.AppointmentManagementController.hasAppointmentOnDate(dentistName, date);
        leaveApplicable = isWorkingDay && !hasAppointment;

        if (leaveApplicable) {
            btnRequest.setText("Can Apply Leave");
            btnRequest.setBackground(COLOR_CAN_APPLY);
        } else {
            btnRequest.setText("Cannot Apply Leave");
            btnRequest.setBackground(COLOR_CANNOT_APPLY);
        }
        btnRequest1.setEnabled(leaveApplicable);
    }//GEN-LAST:event_btnRequestActionPerformed

    /** "Request" — only reachable once the check above has confirmed the date is applicable. */
    private void btnRequest1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRequest1ActionPerformed
        if (!leaveApplicable) {
            return; // belt-and-braces: the button is disabled in this state, this should never fire
        }
        boolean ok = new LeaveRequestController().submitLeaveRequest(dentistName, txtLeaveDate.getText(), this);
        if (!ok) {
            return;
        }
        IconFactory.showSuccessDialog(this, "Leave Request\nSubmitted!", () -> {
            dispose();
            javax.swing.SwingUtilities.invokeLater(() -> {
                new Dentist_Dashboard(user).setVisible(true);
            });
        });
    }//GEN-LAST:event_btnRequest1ActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new D_RS_Leave((User) null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRequest;
    private javax.swing.JButton btnRequest1;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox chkFriday;
    private javax.swing.JCheckBox chkMonday;
    private javax.swing.JCheckBox chkSaturday;
    private javax.swing.JCheckBox chkSunday;
    private javax.swing.JCheckBox chkThursday;
    private javax.swing.JCheckBox chkTuesday;
    private javax.swing.JCheckBox chkWednesday;
    private javax.swing.JLabel lblBreakTime;
    private javax.swing.JLabel lblCheckForLeave;
    private javax.swing.JLabel lblEndTime;
    private javax.swing.JLabel lblLeaveApprovedCaption;
    private javax.swing.JLabel lblLeaveDetailsHeader;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblStartTime;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JLabel lblWarning;
    private javax.swing.JLabel lblWorkingDaysHeader;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JTextField txtBreakTime;
    private javax.swing.JTextField txtEndTime;
    private javax.swing.JTextField txtLeaveApproved;
    private javax.swing.JTextField txtLeaveDate;
    private javax.swing.JTextField txtStartTime;
    // End of variables declaration//GEN-END:variables
}
