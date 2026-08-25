package view;

import controller.AppointmentManagementController;
import controller.BillingManagementController;
import controller.DentistManagementController;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import model.DentistModel;

/**
 * Patient &gt; My Appointments &gt; Add — lets a logged-in patient book their
 * own appointment, reached from PA_APO_Grid's "Add" button.
 *
 * A single-screen version of Office Staff's OS_AM_2 (Dentist &amp; Schedule
 * Information) — the patient identity doesn't need its own step here since
 * it's already known from the logged-in session (unlike OS_AM_1, which lets
 * office staff pick *which* patient an appointment is for). Dentist Name is
 * a dropdown of every dentist registered through Dentist Management; picking
 * one works out their upcoming working dates and open hourly slots the same
 * way OS_AM_2 does — see {@link AppointmentManagementController} for that
 * logic. New appointments booked here start out "Pending", same as every
 * other appointment in the system — they still need Office Staff/Dentist
 * approval, this screen doesn't skip that.
 */
public class PA_APO_Add extends javax.swing.JFrame {

    private static final String PLACEHOLDER = "-- Select --";
    private static final DateTimeFormatter DATE_DISPLAY = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_DISPLAY = DateTimeFormatter.ofPattern("hh:mm a");
    private static final int DATES_TO_OFFER = 4;

    private final model.User currentUser;
    private final String patientName;

    /** Backward-compatible no-arg entry point (e.g. {@code main()}) — Save is disabled without a logged-in patient. */
    public PA_APO_Add() {
        this(null);
    }

    public PA_APO_Add(model.User user) {
        this.currentUser = user;
        this.patientName = (user != null && user.getFullName() != null) ? user.getFullName() : null;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        setupDropdowns();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void setupDropdowns() {
        cmbDentistName.addItem(PLACEHOLDER);
        for (String name : DentistManagementController.getDirectory().keySet()) {
            cmbDentistName.addItem(name);
        }

        cmbTreatmentType.setModel(new javax.swing.DefaultComboBoxModel<>(
                new BillingManagementController().getServiceNames()));

        lockField(txtRoomNo);
        lockField(txtAppointmentNo);
        txtAppointmentNo.setText(AppointmentManagementController.nextAppointmentId()); // auto-generated up front

        cmbDentistName.addActionListener(e -> onDentistChanged());
        cmbAppDate.addActionListener(e -> refreshTimes());

        refreshDates(); // starts empty — no dentist chosen yet
    }

    private void lockField(javax.swing.JTextField field) {
        field.setEditable(false);
        field.setBackground(new Color(235, 235, 235));
    }

    private DentistModel selectedDentist() {
        Object name = cmbDentistName.getSelectedItem();
        if (name == null || PLACEHOLDER.equals(name)) {
            return null;
        }
        return DentistManagementController.getDirectory().get(name);
    }

    private void onDentistChanged() {
        DentistModel dentist = selectedDentist();
        txtRoomNo.setText(dentist != null && dentist.getRoomNo() != null ? dentist.getRoomNo() : "");
        refreshDates();
    }

    /** Rebuilds the Appointment Date dropdown from the selected dentist's Working Days. */
    private void refreshDates() {
        cmbAppDate.removeAllItems();
        cmbAppDate.addItem(PLACEHOLDER);
        DentistModel dentist = selectedDentist();
        if (dentist != null) {
            for (LocalDate date : AppointmentManagementController.upcomingWorkingDates(dentist.getWorkingDays(), DATES_TO_OFFER)) {
                cmbAppDate.addItem(date.format(DATE_DISPLAY));
            }
        }
        refreshTimes();
    }

    /** Rebuilds the Appointment Time dropdown from the selected dentist's Start/End/Break Time. */
    private void refreshTimes() {
        cmbAppTime.removeAllItems();
        cmbAppTime.addItem(PLACEHOLDER);
        DentistModel dentist = selectedDentist();
        Object dateText = cmbAppDate.getSelectedItem();
        if (dentist != null && dateText != null && !PLACEHOLDER.equals(dateText)) {
            String dentistName = (String) cmbDentistName.getSelectedItem();
            LocalDate date = LocalDate.parse((String) dateText, DATE_DISPLAY);
            for (LocalTime time : AppointmentManagementController.hourlySlots(
                    dentist.getStartTime(), dentist.getEndTime(), dentist.getBreakTime())) {
                if (!AppointmentManagementController.isBooked(dentistName, date, time)) {
                    cmbAppTime.addItem(time.format(TIME_DISPLAY));
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblUserIcon = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        lblSubtitle = new javax.swing.JLabel();
        lblDentistName = new javax.swing.JLabel();
        cmbDentistName = new javax.swing.JComboBox();
        lblAppDate = new javax.swing.JLabel();
        cmbTreatmentType = new javax.swing.JComboBox();
        lblAppTime = new javax.swing.JLabel();
        cmbAppDate = new javax.swing.JComboBox();
        lblReason = new javax.swing.JLabel();
        cmbAppTime = new javax.swing.JComboBox();
        btnBack = new javax.swing.JButton();
        btnSubmit = new javax.swing.JButton();
        lblReason3 = new javax.swing.JLabel();
        txtRoomNo = new javax.swing.JTextField();
        lblReason4 = new javax.swing.JLabel();
        txtAppointmentNo = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental — Add Appointment");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 160, 40);

        lblUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/login_scaled.png"))); // NOI18N
        lblUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        navBar.add(lblUserIcon);
        lblUserIcon.setBounds(850, 10, 40, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("Add Appointments");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 420, 40);

        cardPanel.setBackground(new java.awt.Color(248, 249, 250));
        cardPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.setLayout(null);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Dentist Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 30, 400, 30);

        lblDentistName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDentistName.setText("Dentist Name");
        cardPanel.add(lblDentistName);
        lblDentistName.setBounds(60, 95, 120, 25);

        cmbDentistName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbDentistName);
        cmbDentistName.setBounds(60, 125, 350, 35);

        lblAppDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAppDate.setText("Treatment Type");
        cardPanel.add(lblAppDate);
        lblAppDate.setBounds(60, 175, 140, 25);

        cmbTreatmentType.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbTreatmentType);
        cmbTreatmentType.setBounds(60, 205, 350, 35);

        lblAppTime.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAppTime.setText("Appointment Date");
        cardPanel.add(lblAppTime);
        lblAppTime.setBounds(490, 95, 140, 25);

        cmbAppDate.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbAppDate);
        cmbAppDate.setBounds(490, 125, 350, 35);

        lblReason.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblReason.setText("Appoinment Time");
        cardPanel.add(lblReason);
        lblReason.setBounds(490, 175, 150, 25);

        cmbAppTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbAppTime);
        cmbAppTime.setBounds(490, 205, 350, 35);

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
        cardPanel.add(btnBack);
        btnBack.setBounds(620, 355, 100, 36);

        btnSubmit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSubmit.setText("Save");
        btnSubmit.setBackground(new java.awt.Color(231, 115, 36));
        btnSubmit.setForeground(new java.awt.Color(255, 255, 255));
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        cardPanel.add(btnSubmit);
        btnSubmit.setBounds(740, 355, 100, 36);

        lblReason3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblReason3.setForeground(new java.awt.Color(204, 0, 0));
        lblReason3.setText("Room No");
        cardPanel.add(lblReason3);
        lblReason3.setBounds(490, 255, 150, 25);

        txtRoomNo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtRoomNo);
        txtRoomNo.setBounds(490, 285, 350, 35);

        lblReason4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblReason4.setForeground(new java.awt.Color(204, 0, 0));
        lblReason4.setText("Appoinment No");
        cardPanel.add(lblReason4);
        lblReason4.setBounds(60, 255, 150, 25);

        txtAppointmentNo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtAppointmentNo);
        txtAppointmentNo.setBounds(60, 285, 350, 35);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 170, 900, 425);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new PA_APO_Grid(currentUser).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        Object dentist = cmbDentistName.getSelectedItem();
        Object date = cmbAppDate.getSelectedItem();
        Object time = cmbAppTime.getSelectedItem();
        Object treatment = cmbTreatmentType.getSelectedItem();

        if (dentist == null || PLACEHOLDER.equals(dentist)
                || date == null || PLACEHOLDER.equals(date)
                || time == null || PLACEHOLDER.equals(time)
                || treatment == null || treatment.toString().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a dentist, treatment type, date, and time.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (patientName == null) {
            IconFactory.showErrorDialog(this,
                    "You're not logged in as a patient — please log in again before booking.", null);
            return;
        }

        LocalDate pickedDate = LocalDate.parse((String) date, DATE_DISPLAY);
        LocalTime pickedTime = LocalTime.parse((String) time, TIME_DISPLAY);
        boolean saved = AppointmentManagementController.bookAppointment(txtAppointmentNo.getText(), patientName,
                (String) dentist, (String) treatment, pickedDate, pickedTime, "Pending");

        if (!saved) {
            IconFactory.showErrorDialog(this,
                    "Couldn't save this appointment — that slot may have just been booked by someone else. "
                    + "Pick a different date or time and try again.", null);
            return;
        }

        IconFactory.showSuccessDialog(this, "Appointment added successfully!", () -> {
            dispose();
            javax.swing.SwingUtilities.invokeLater(() -> {
                new PA_APO_Grid(currentUser).setVisible(true);
            });
        });
    }//GEN-LAST:event_btnSubmitActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PA_APO_Add().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox cmbAppDate;
    private javax.swing.JComboBox cmbAppTime;
    private javax.swing.JComboBox cmbDentistName;
    private javax.swing.JComboBox cmbTreatmentType;
    private javax.swing.JLabel lblAppDate;
    private javax.swing.JLabel lblAppTime;
    private javax.swing.JLabel lblDentistName;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblReason;
    private javax.swing.JLabel lblReason3;
    private javax.swing.JLabel lblReason4;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JTextField txtAppointmentNo;
    private javax.swing.JTextField txtRoomNo;
    // End of variables declaration//GEN-END:variables
}
