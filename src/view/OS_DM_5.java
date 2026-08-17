package view;

import controller.DentistManagementController;

/**
 * Step 5: Availability Form
 * View only — all logic delegated to DentistManagementController.
 */
public class OS_DM_5 extends javax.swing.JFrame {

    private static final java.time.format.DateTimeFormatter TIME_FMT = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
    private static final java.time.format.DateTimeFormatter TIME_DISPLAY = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");

    private final DentistManagementController controller;

    public OS_DM_5(DentistManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        DateTimePicker.attachTime(txtStartTime);
        DateTimePicker.attachTime(txtEndTime);
        WeekdayPicker.attach(txtWorkingDays);
        IconFactory.installPlaceholder(txtBreakTime, "e.g. 30");
        setupBreakPreview();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    /**
     * Break Time is entered as a plain duration in minutes — the actual
     * break window is centered in the middle of Start Time–End Time and
     * shown here as a live preview so office staff can see exactly what
     * gets blocked before submitting. AppointmentManagementController's
     * slot generator already excludes whatever "HH:mm-HH:mm" range lands
     * in the model's breakTime field, so nothing downstream needs to
     * change — this just computes that range instead of asking staff to
     * type it by hand.
     */
    private void setupBreakPreview() {
        javax.swing.event.DocumentListener listener = new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateBreakPreview(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateBreakPreview(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateBreakPreview(); }
        };
        txtStartTime.getDocument().addDocumentListener(listener);
        txtEndTime.getDocument().addDocumentListener(listener);
        txtBreakTime.getDocument().addDocumentListener(listener);
        updateBreakPreview();
    }

    private void updateBreakPreview() {
        String range = computeBreakRange(txtStartTime.getText(), txtEndTime.getText(), parseDurationMinutes());
        if (range.isEmpty()) {
            lblBreakPreview.setText(" ");
            return;
        }
        String[] parts = range.split("-");
        java.time.LocalTime from = java.time.LocalTime.parse(parts[0], TIME_FMT);
        java.time.LocalTime to = java.time.LocalTime.parse(parts[1], TIME_FMT);
        lblBreakPreview.setText("Break window: " + from.format(TIME_DISPLAY) + " – " + to.format(TIME_DISPLAY)
                + " — no appointments will be offered in this window");
    }

    private int parseDurationMinutes() {
        if (IconFactory.isPlaceholderShowing(txtBreakTime)) {
            return 0;
        }
        try {
            return Integer.parseInt(txtBreakTime.getText().trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * Centers a break of the given duration in the middle of the working
     * day (Start Time–End Time), clamped so it never spills outside it.
     * Returns "" if the inputs aren't usable yet (no break applied).
     */
    private static String computeBreakRange(String startText, String endText, int durationMinutes) {
        java.time.LocalTime start = parseTimeOrNull(startText);
        java.time.LocalTime end = parseTimeOrNull(endText);
        if (start == null || end == null || !start.isBefore(end) || durationMinutes <= 0) {
            return "";
        }
        long totalMinutes = java.time.Duration.between(start, end).toMinutes();
        long duration = Math.min(durationMinutes, Math.max(1, totalMinutes - 1)); // always leave >=1 min of working time
        long midpoint = totalMinutes / 2;
        long breakStartMin = Math.max(0, Math.min(midpoint - duration / 2, totalMinutes - duration));
        java.time.LocalTime breakStart = start.plusMinutes(breakStartMin);
        java.time.LocalTime breakEnd = breakStart.plusMinutes(duration);
        return breakStart.format(TIME_FMT) + "-" + breakEnd.format(TIME_FMT);
    }

    private static java.time.LocalTime parseTimeOrNull(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return java.time.LocalTime.parse(text.trim(), TIME_FMT);
        } catch (Exception ex) {
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        sepLine = new javax.swing.JSeparator();
        lblStep1 = new javax.swing.JLabel();
        lblStep2 = new javax.swing.JLabel();
        lblStep3 = new javax.swing.JLabel();
        lblStep4 = new javax.swing.JLabel();
        lblStep5 = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblWorkingDays = new javax.swing.JLabel();
        txtWorkingDays = new javax.swing.JTextField();
        lblStartTime = new javax.swing.JLabel();
        txtStartTime = new javax.swing.JTextField();
        lblEndTime = new javax.swing.JLabel();
        txtEndTime = new javax.swing.JTextField();
        lblBreakTime = new javax.swing.JLabel();
        txtBreakTime = new javax.swing.JTextField();
        lblBreakPreview = new javax.swing.JLabel();
        lblRoomNo = new javax.swing.JLabel();
        txtRoomNo = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnSubmit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Dentist Management (Step 5)");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 160, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("Dentist Management");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 400, 40);

        cardPanel.setBackground(new java.awt.Color(248, 249, 250));
        cardPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.setLayout(null);

        sepLine.setForeground(new java.awt.Color(0, 0, 0));
        cardPanel.add(sepLine);
        sepLine.setBounds(150, 50, 600, 4);

        lblStep1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep1.setForeground(new java.awt.Color(255, 255, 255));
        lblStep1.setBackground(new java.awt.Color(0, 0, 0));
        lblStep1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep1.setText("1");
        lblStep1.setOpaque(true);
        cardPanel.add(lblStep1);
        lblStep1.setBounds(150, 30, 40, 40);

        lblStep2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep2.setForeground(new java.awt.Color(255, 255, 255));
        lblStep2.setBackground(new java.awt.Color(0, 0, 0));
        lblStep2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep2.setText("2");
        lblStep2.setOpaque(true);
        cardPanel.add(lblStep2);
        lblStep2.setBounds(290, 30, 40, 40);

        lblStep3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep3.setForeground(new java.awt.Color(255, 255, 255));
        lblStep3.setBackground(new java.awt.Color(0, 0, 0));
        lblStep3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep3.setText("3");
        lblStep3.setOpaque(true);
        cardPanel.add(lblStep3);
        lblStep3.setBounds(430, 30, 40, 40);

        lblStep4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep4.setForeground(new java.awt.Color(255, 255, 255));
        lblStep4.setBackground(new java.awt.Color(0, 0, 0));
        lblStep4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep4.setText("4");
        lblStep4.setOpaque(true);
        cardPanel.add(lblStep4);
        lblStep4.setBounds(570, 30, 40, 40);

        lblStep5.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep5.setForeground(new java.awt.Color(255, 255, 255));
        lblStep5.setBackground(new java.awt.Color(231, 115, 36));
        lblStep5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep5.setText("5");
        lblStep5.setOpaque(true);
        cardPanel.add(lblStep5);
        lblStep5.setBounds(710, 30, 40, 40);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Availability");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblWorkingDays.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblWorkingDays.setText("Working Days");
        cardPanel.add(lblWorkingDays);
        lblWorkingDays.setBounds(60, 140, 150, 25);

        txtWorkingDays.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtWorkingDays);
        txtWorkingDays.setBounds(60, 170, 350, 35);

        lblStartTime.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblStartTime.setText("Start Time");
        cardPanel.add(lblStartTime);
        lblStartTime.setBounds(60, 220, 150, 25);

        txtStartTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtStartTime);
        txtStartTime.setBounds(60, 250, 350, 35);

        lblEndTime.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblEndTime.setText("End Time");
        cardPanel.add(lblEndTime);
        lblEndTime.setBounds(60, 300, 150, 25);

        txtEndTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtEndTime);
        txtEndTime.setBounds(60, 330, 350, 35);

        lblBreakTime.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblBreakTime.setText("Break Duration (mins)");
        cardPanel.add(lblBreakTime);
        lblBreakTime.setBounds(60, 380, 200, 25);

        txtBreakTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtBreakTime);
        txtBreakTime.setBounds(60, 410, 350, 35);

        lblBreakPreview.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblBreakPreview.setForeground(new java.awt.Color(110, 110, 110));
        cardPanel.add(lblBreakPreview);
        lblBreakPreview.setBounds(60, 448, 400, 20);

        lblRoomNo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblRoomNo.setText("Room No");
        cardPanel.add(lblRoomNo);
        lblRoomNo.setBounds(490, 140, 150, 25);

        txtRoomNo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtRoomNo);
        txtRoomNo.setBounds(490, 170, 350, 35);

        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.setBackground(new java.awt.Color(30, 144, 255));
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        cardPanel.add(btnBack);
        btnBack.setBounds(625, 420, 100, 36);

        btnSubmit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSubmit.setText("Submit");
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
        btnSubmit.setBounds(740, 420, 100, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        controller.goBackFromStep5(this);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        String breakRange = computeBreakRange(txtStartTime.getText(), txtEndTime.getText(), parseDurationMinutes());
        controller.submitFromStep5(
                txtWorkingDays.getText(),
                txtStartTime.getText(),
                txtEndTime.getText(),
                breakRange,
                txtRoomNo.getText(),
                this
        );
    }//GEN-LAST:event_btnSubmitActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new OS_DM_5(new controller.DentistManagementController()).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblBreakPreview;
    private javax.swing.JLabel lblBreakTime;
    private javax.swing.JLabel lblEndTime;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblRoomNo;
    private javax.swing.JLabel lblStartTime;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblStep5;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblWorkingDays;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtBreakTime;
    private javax.swing.JTextField txtEndTime;
    private javax.swing.JTextField txtRoomNo;
    private javax.swing.JTextField txtStartTime;
    private javax.swing.JTextField txtWorkingDays;
    // End of variables declaration//GEN-END:variables
}
