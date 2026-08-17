package view;

import controller.DentistManagementController;

/**
 * Step 1: Dentist Information Form
 * View only — all logic delegated to DentistManagementController.
 */
public class OS_DM_1 extends javax.swing.JFrame {

    private final DentistManagementController controller;

    /** Called from OS_DM_Grid — starts a brand new wizard. */
    public OS_DM_1() {
        this(new DentistManagementController());
    }

    /** Called from the controller when navigating back from Step 2. */
    public OS_DM_1(DentistManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        DateTimePicker.attachDate(txtDOB);
        FilePicker.attachImageUpload(txtPortalPN);
        setupDoctorId();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    /** Doctor ID is auto-generated (e.g. "D104") and read-only, same convention as Appointment No / Approval ID. */
    private void setupDoctorId() {
        txtDoctorId.setEditable(false);
        txtDoctorId.setBackground(new java.awt.Color(235, 235, 235));
        String existing = controller.getDentistModel().getDoctorId();
        txtDoctorId.setText(existing != null && !existing.trim().isEmpty()
                ? existing
                : DentistManagementController.nextDoctorId());
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
        lblTitlePrefix = new javax.swing.JLabel();
        chkDr = new javax.swing.JCheckBox();
        chkMr = new javax.swing.JCheckBox();
        chkMrs = new javax.swing.JCheckBox();
        lblGender = new javax.swing.JLabel();
        chkMale = new javax.swing.JCheckBox();
        chkFemale = new javax.swing.JCheckBox();
        lblFullName = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        lblDoctorId = new javax.swing.JLabel();
        txtDoctorId = new javax.swing.JTextField();
        lblDOB = new javax.swing.JLabel();
        txtDOB = new javax.swing.JTextField();
        lblNIC = new javax.swing.JLabel();
        txtNIC = new javax.swing.JTextField();
        lblPortalPN = new javax.swing.JLabel();
        txtPortalPN = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Dentist Management (Step 1)");
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
        lblStep1.setBackground(new java.awt.Color(231, 115, 36));
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
        lblStep5.setBackground(new java.awt.Color(0, 0, 0));
        lblStep5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep5.setText("5");
        lblStep5.setOpaque(true);
        cardPanel.add(lblStep5);
        lblStep5.setBounds(710, 30, 40, 40);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Dentist Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblTitlePrefix.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTitlePrefix.setText("Title");
        cardPanel.add(lblTitlePrefix);
        lblTitlePrefix.setBounds(60, 140, 80, 25);

        chkDr.setText("Dr");
        cardPanel.add(chkDr);
        chkDr.setBounds(150, 140, 50, 25);

        chkMr.setText("Mr");
        cardPanel.add(chkMr);
        chkMr.setBounds(205, 140, 50, 25);

        chkMrs.setText("Mrs");
        cardPanel.add(chkMrs);
        chkMrs.setBounds(260, 140, 55, 25);

        lblGender.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblGender.setText("Gender");
        cardPanel.add(lblGender);
        lblGender.setBounds(60, 180, 80, 25);

        chkMale.setText("Male");
        cardPanel.add(chkMale);
        chkMale.setBounds(150, 180, 60, 25);

        chkFemale.setText("Female");
        cardPanel.add(chkFemale);
        chkFemale.setBounds(220, 180, 75, 25);

        lblFullName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblFullName.setText("Full Name");
        cardPanel.add(lblFullName);
        lblFullName.setBounds(60, 220, 100, 25);

        txtFullName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtFullName);
        txtFullName.setBounds(60, 250, 350, 35);

        lblDoctorId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDoctorId.setText("Doctor ID");
        cardPanel.add(lblDoctorId);
        lblDoctorId.setBounds(60, 300, 100, 25);

        txtDoctorId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDoctorId);
        txtDoctorId.setBounds(60, 330, 350, 35);

        lblDOB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDOB.setText("DOB");
        cardPanel.add(lblDOB);
        lblDOB.setBounds(490, 140, 100, 25);

        txtDOB.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDOB);
        txtDOB.setBounds(490, 170, 350, 35);

        lblNIC.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNIC.setText("NIC");
        cardPanel.add(lblNIC);
        lblNIC.setBounds(490, 220, 100, 25);

        txtNIC.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtNIC);
        txtNIC.setBounds(490, 250, 350, 35);

        lblPortalPN.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPortalPN.setText("Profile Pic");
        cardPanel.add(lblPortalPN);
        lblPortalPN.setBounds(490, 300, 140, 25);

        txtPortalPN.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtPortalPN);
        txtPortalPN.setBounds(490, 330, 350, 35);

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
        btnBack.setBounds(620, 420, 100, 36);

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
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_DM_Grid().setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // Resolve title selection
        String title = chkDr.isSelected() ? "Dr" : chkMr.isSelected() ? "Mr" : chkMrs.isSelected() ? "Mrs" : "";
        String gender = chkMale.isSelected() ? "Male" : chkFemale.isSelected() ? "Female" : "";

        controller.goNextFromStep1(
                title,
                gender,
                txtFullName.getText(),
                txtDoctorId.getText(),
                txtDOB.getText(),
                txtNIC.getText(),
                txtPortalPN.getText(),
                this
        );
    }//GEN-LAST:event_btnNextActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_DM_1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox chkDr;
    private javax.swing.JCheckBox chkFemale;
    private javax.swing.JCheckBox chkMale;
    private javax.swing.JCheckBox chkMr;
    private javax.swing.JCheckBox chkMrs;
    private javax.swing.JLabel lblDOB;
    private javax.swing.JLabel lblDoctorId;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblNIC;
    private javax.swing.JLabel lblPortalPN;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblStep5;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitlePrefix;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtDOB;
    private javax.swing.JTextField txtDoctorId;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtNIC;
    private javax.swing.JTextField txtPortalPN;
    // End of variables declaration//GEN-END:variables
}
