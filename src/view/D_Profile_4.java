package view;

import controller.DentistProfileController;

/**
 * My Profile — Step 4: My Employment Information
 * View only — all logic delegated to DentistProfileController.
 */
public class D_Profile_4 extends javax.swing.JFrame {

    private final DentistProfileController controller;

    public D_Profile_4(DentistProfileController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        DateTimePicker.attachDate(txtJoinedDate);
        prefillFromModel();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void prefillFromModel() {
        model.DentistModel m = controller.getDentistModel();
        if (m == null) return;
        if (m.getJoinedDate() != null)       txtJoinedDate.setText(m.getJoinedDate());
        if (m.getEmploymentType() != null)   txtEmploymentType.setText(m.getEmploymentType());
        if (m.getConsultationFee() != null)  txtConsultationFee.setText(m.getConsultationFee());
        if ("Full Time".equals(m.getEmploymentStatus())) chkFullTime.setSelected(true);
        if ("Part Time".equals(m.getEmploymentStatus())) chkPartTime.setSelected(true);
        if ("Contract".equals(m.getEmploymentStatus()))  chkContract.setSelected(true);
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
        lblJoinedDate = new javax.swing.JLabel();
        txtJoinedDate = new javax.swing.JTextField();
        lblEmploymentType = new javax.swing.JLabel();
        txtEmploymentType = new javax.swing.JTextField();
        lblConsultationFee = new javax.swing.JLabel();
        txtConsultationFee = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        chkFullTime = new javax.swing.JCheckBox();
        chkPartTime = new javax.swing.JCheckBox();
        chkContract = new javax.swing.JCheckBox();
        btnBack = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – My Profile (Step 4)");
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
        lblStep4.setBackground(new java.awt.Color(231, 115, 36));
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
        lblSubtitle.setText("My Employment Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblJoinedDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblJoinedDate.setText("Joined Date");
        cardPanel.add(lblJoinedDate);
        lblJoinedDate.setBounds(60, 140, 150, 25);

        txtJoinedDate.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtJoinedDate);
        txtJoinedDate.setBounds(60, 170, 350, 35);

        lblEmploymentType.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblEmploymentType.setText("Employment Type");
        cardPanel.add(lblEmploymentType);
        lblEmploymentType.setBounds(60, 220, 150, 25);

        txtEmploymentType.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtEmploymentType);
        txtEmploymentType.setBounds(60, 250, 350, 35);

        lblConsultationFee.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblConsultationFee.setText("Consultation Fee");
        cardPanel.add(lblConsultationFee);
        lblConsultationFee.setBounds(60, 300, 150, 25);

        txtConsultationFee.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtConsultationFee);
        txtConsultationFee.setBounds(60, 330, 350, 35);

        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblStatus.setText("Status");
        cardPanel.add(lblStatus);
        lblStatus.setBounds(490, 140, 150, 25);

        chkFullTime.setText("Full Time");
        cardPanel.add(chkFullTime);
        chkFullTime.setBounds(490, 175, 90, 25);

        chkPartTime.setText("Part Time");
        cardPanel.add(chkPartTime);
        chkPartTime.setBounds(590, 175, 90, 25);

        chkContract.setText("Contract");
        cardPanel.add(chkContract);
        chkContract.setBounds(690, 175, 90, 25);

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

        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setBackground(new java.awt.Color(231, 115, 36));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setBorderPainted(false);
        btnUpdate.setFocusPainted(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        cardPanel.add(btnUpdate);
        btnUpdate.setBounds(490, 420, 110, 36);

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
        controller.goBackFromStep4(this);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        String status = chkFullTime.isSelected() ? "Full Time"
                : chkPartTime.isSelected() ? "Part Time"
                : chkContract.isSelected() ? "Contract" : "";
        controller.goNextFromStep4(
                txtJoinedDate.getText(),
                txtEmploymentType.getText(),
                txtConsultationFee.getText(),
                status,
                this
        );
    }//GEN-LAST:event_btnNextActionPerformed

    // Saves just this page immediately — no need to walk the rest of the wizard.
    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        String status = chkFullTime.isSelected() ? "Full Time"
                : chkPartTime.isSelected() ? "Part Time"
                : chkContract.isSelected() ? "Contract" : "";
        controller.updateFromStep4(
                txtJoinedDate.getText(),
                txtEmploymentType.getText(),
                txtConsultationFee.getText(),
                status,
                this
        );
    }//GEN-LAST:event_btnUpdateActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new D_Profile_4(new controller.DentistProfileController(null)).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox chkContract;
    private javax.swing.JCheckBox chkFullTime;
    private javax.swing.JCheckBox chkPartTime;
    private javax.swing.JLabel lblConsultationFee;
    private javax.swing.JLabel lblEmploymentType;
    private javax.swing.JLabel lblJoinedDate;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblStep5;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtConsultationFee;
    private javax.swing.JTextField txtEmploymentType;
    private javax.swing.JTextField txtJoinedDate;
    // End of variables declaration//GEN-END:variables
}
