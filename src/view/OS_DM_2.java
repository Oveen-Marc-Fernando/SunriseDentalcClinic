package view;

import controller.DentistManagementController;

/**
 * Step 2: Professional Information Form
 * View only — all logic delegated to DentistManagementController.
 */
public class OS_DM_2 extends javax.swing.JFrame {

    private final DentistManagementController controller;

    public OS_DM_2(DentistManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        DateTimePicker.attachYear(txtGradYear);
        setSize(1016, 739);
        setLocationRelativeTo(null);
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
        lblSlmcNo = new javax.swing.JLabel();
        txtSlmcNo = new javax.swing.JTextField();
        lblQualification = new javax.swing.JLabel();
        txtQualification = new javax.swing.JTextField();
        lblUniversity = new javax.swing.JLabel();
        txtUniversity = new javax.swing.JTextField();
        lblGradYear = new javax.swing.JLabel();
        txtGradYear = new javax.swing.JTextField();
        lblSpecialization = new javax.swing.JLabel();
        txtSpecialization = new javax.swing.JTextField();
        lblExperience = new javax.swing.JLabel();
        txtExperience = new javax.swing.JTextField();
        lblLicenseStatus = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        chkInactive = new javax.swing.JCheckBox();
        btnBack = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Dentist Management (Step 2)");
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
        lblStep2.setBackground(new java.awt.Color(231, 115, 36));
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
        lblStep5.setBackground(new java.awt.Color(0, 0, 0));
        lblStep5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep5.setText("5");
        lblStep5.setOpaque(true);
        cardPanel.add(lblStep5);
        lblStep5.setBounds(710, 30, 40, 40);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Professional Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblSlmcNo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSlmcNo.setText("SLMC Reg NO");
        cardPanel.add(lblSlmcNo);
        lblSlmcNo.setBounds(60, 140, 150, 25);

        txtSlmcNo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtSlmcNo);
        txtSlmcNo.setBounds(60, 170, 350, 35);

        lblQualification.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblQualification.setText("Qualification");
        cardPanel.add(lblQualification);
        lblQualification.setBounds(60, 220, 150, 25);

        txtQualification.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtQualification);
        txtQualification.setBounds(60, 250, 350, 35);

        lblUniversity.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblUniversity.setText("University");
        cardPanel.add(lblUniversity);
        lblUniversity.setBounds(60, 300, 150, 25);

        txtUniversity.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtUniversity);
        txtUniversity.setBounds(60, 330, 350, 35);

        lblGradYear.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblGradYear.setText("Graduation Year");
        cardPanel.add(lblGradYear);
        lblGradYear.setBounds(60, 380, 150, 25);

        txtGradYear.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtGradYear);
        txtGradYear.setBounds(60, 410, 350, 35);

        lblSpecialization.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSpecialization.setText("Specialization");
        cardPanel.add(lblSpecialization);
        lblSpecialization.setBounds(490, 140, 150, 25);

        txtSpecialization.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtSpecialization);
        txtSpecialization.setBounds(490, 170, 350, 35);

        lblExperience.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblExperience.setText("Experience");
        cardPanel.add(lblExperience);
        lblExperience.setBounds(490, 220, 150, 25);

        txtExperience.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtExperience);
        txtExperience.setBounds(490, 250, 350, 35);

        lblLicenseStatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLicenseStatus.setText("License Status");
        cardPanel.add(lblLicenseStatus);
        lblLicenseStatus.setBounds(490, 300, 150, 25);

        chkActive.setText("Active");
        cardPanel.add(chkActive);
        chkActive.setBounds(490, 335, 75, 25);

        chkInactive.setText("Inactive");
        cardPanel.add(chkInactive);
        chkInactive.setBounds(575, 335, 90, 25);

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

        btnNext.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNext.setText("Next");
        btnNext.setBackground(new java.awt.Color(0, 200, 83));
        btnNext.setForeground(new java.awt.Color(255, 255, 255));
        btnNext.setBorderPainted(false);
        btnNext.setFocusPainted(false);
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        cardPanel.add(btnNext);
        btnNext.setBounds(740, 420, 100, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        controller.goBackFromStep2(this);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        String licenseStatus = chkActive.isSelected() ? "Active" : chkInactive.isSelected() ? "Inactive" : "";
        controller.goNextFromStep2(
                txtSlmcNo.getText(),
                txtQualification.getText(),
                txtUniversity.getText(),
                txtGradYear.getText(),
                txtSpecialization.getText(),
                txtExperience.getText(),
                licenseStatus,
                this
        );
    }//GEN-LAST:event_btnNextActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new OS_DM_2(new controller.DentistManagementController()).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkInactive;
    private javax.swing.JLabel lblExperience;
    private javax.swing.JLabel lblGradYear;
    private javax.swing.JLabel lblLicenseStatus;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblQualification;
    private javax.swing.JLabel lblSlmcNo;
    private javax.swing.JLabel lblSpecialization;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblStep5;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUniversity;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtExperience;
    private javax.swing.JTextField txtGradYear;
    private javax.swing.JTextField txtQualification;
    private javax.swing.JTextField txtSlmcNo;
    private javax.swing.JTextField txtSpecialization;
    private javax.swing.JTextField txtUniversity;
    // End of variables declaration//GEN-END:variables
}
