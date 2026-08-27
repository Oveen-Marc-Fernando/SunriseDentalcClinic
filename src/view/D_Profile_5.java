package view;

import controller.DentistProfileController;

/**
 * My Profile — Step 5: My Availability
 * View only — all logic delegated to DentistProfileController.
 */
public class D_Profile_5 extends javax.swing.JFrame {

    private final DentistProfileController controller;

    public D_Profile_5(DentistProfileController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        DateTimePicker.attachTime(txtStartTime);
        DateTimePicker.attachTime(txtEndTime);
        WeekdayPicker.attach(txtWorkingDays); // same tick-box picker as OS_DM_5 (Office Staff's Dentist Management)
        prefillFromModel();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void prefillFromModel() {
        model.DentistModel m = controller.getDentistModel();
        if (m == null) return;
        if (m.getWorkingDays() != null) txtWorkingDays.setText(m.getWorkingDays());
        if (m.getStartTime() != null)   txtStartTime.setText(m.getStartTime());
        if (m.getEndTime() != null)     txtEndTime.setText(m.getEndTime());
        if (m.getBreakTime() != null)   txtBreakTime.setText(m.getBreakTime());
        if (m.getRoomNo() != null)      txtRoomNo.setText(m.getRoomNo());
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
        lblRoomNo = new javax.swing.JLabel();
        txtRoomNo = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnSubmit = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – My Profile (Step 5)");
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
        lblTitle.setText("My Profile");
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
        lblSubtitle.setText("My Availability");
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
        lblBreakTime.setBounds(60, 380, 150, 25);

        txtBreakTime.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtBreakTime);
        txtBreakTime.setBounds(60, 410, 350, 35);

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

        btnSubmit.setBackground(new java.awt.Color(0, 204, 51));
        btnSubmit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSubmit.setForeground(new java.awt.Color(255, 255, 255));
        btnSubmit.setText("Submit");
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        cardPanel.add(btnSubmit);
        btnSubmit.setBounds(740, 420, 100, 36);

        btnUpdate.setBackground(new java.awt.Color(231, 115, 36));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Update");
        btnUpdate.setBorderPainted(false);
        btnUpdate.setFocusPainted(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        cardPanel.add(btnUpdate);
        btnUpdate.setBounds(490, 420, 110, 36);

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
        controller.submitFromStep5(
                txtWorkingDays.getText(),
                txtStartTime.getText(),
                txtEndTime.getText(),
                txtBreakTime.getText(),
                txtRoomNo.getText(),
                this
        );
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpdateActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new D_Profile_5(new controller.DentistProfileController(null)).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel cardPanel;
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
