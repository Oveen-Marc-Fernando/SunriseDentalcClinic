package view;

import controller.PatientProfileController;

/**
 * Patient &gt; My Profile — Step 2: Contact Information.
 *
 * @author oveen
 */
public class PA_PR_2 extends javax.swing.JFrame {

    private final PatientProfileController controller;

    public PA_PR_2() {
        this(new PatientProfileController(null));
    }

    public PA_PR_2(PatientProfileController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40));
        IconFactory.roundCorners(navBar, 30);
        populateFieldsFromModel();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void populateFieldsFromModel() {
        model.PatientModel m = controller.getPatientModel();
        if (m == null) return;
        if (m.getAddressLine1() != null) txtAddressLine1.setText(m.getAddressLine1());
        if (m.getAddressLine2() != null) txtAddressLine2.setText(m.getAddressLine2());
        if (m.getCity() != null) txtCity.setText(m.getCity());
        if (m.getMobileNo() != null) txtMobileNo.setText(m.getMobileNo());
        if (m.getLandlineNo() != null) txtLandlineNo.setText(m.getLandlineNo());
        if (m.getEmail() != null) txtEmail.setText(m.getEmail());
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
        lblSubtitle = new javax.swing.JLabel();
        lblAddress = new javax.swing.JLabel();
        txtAddressLine1 = new javax.swing.JTextField();
        lblAddressLine2 = new javax.swing.JLabel();
        txtAddressLine2 = new javax.swing.JTextField();
        lblCity = new javax.swing.JLabel();
        txtCity = new javax.swing.JTextField();
        lblMobileNo = new javax.swing.JLabel();
        txtMobileNo = new javax.swing.JTextField();
        lblLandlineNo = new javax.swing.JLabel();
        txtLandlineNo = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental — My Profile (Step 2)");
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
        sepLine.setBounds(220, 50, 460, 4);

        lblStep1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep1.setForeground(new java.awt.Color(255, 255, 255));
        lblStep1.setBackground(new java.awt.Color(0, 0, 0));
        lblStep1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep1.setText("1");
        lblStep1.setOpaque(true);
        cardPanel.add(lblStep1);
        lblStep1.setBounds(220, 30, 40, 40);

        lblStep2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep2.setForeground(new java.awt.Color(255, 255, 255));
        lblStep2.setBackground(new java.awt.Color(231, 115, 36));
        lblStep2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep2.setText("2");
        lblStep2.setOpaque(true);
        cardPanel.add(lblStep2);
        lblStep2.setBounds(360, 30, 40, 40);

        lblStep3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep3.setForeground(new java.awt.Color(255, 255, 255));
        lblStep3.setBackground(new java.awt.Color(0, 0, 0));
        lblStep3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep3.setText("3");
        lblStep3.setOpaque(true);
        cardPanel.add(lblStep3);
        lblStep3.setBounds(500, 30, 40, 40);

        lblStep4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep4.setForeground(new java.awt.Color(255, 255, 255));
        lblStep4.setBackground(new java.awt.Color(0, 0, 0));
        lblStep4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep4.setText("4");
        lblStep4.setOpaque(true);
        cardPanel.add(lblStep4);
        lblStep4.setBounds(640, 30, 40, 40);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Contact Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblAddress.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAddress.setText("Address Line 1");
        cardPanel.add(lblAddress);
        lblAddress.setBounds(60, 140, 150, 20);

        txtAddressLine1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtAddressLine1);
        txtAddressLine1.setBounds(60, 165, 350, 35);

        lblAddressLine2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAddressLine2.setText("Address Line 2");
        cardPanel.add(lblAddressLine2);
        lblAddressLine2.setBounds(60, 210, 150, 20);

        txtAddressLine2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtAddressLine2);
        txtAddressLine2.setBounds(60, 235, 350, 35);

        lblCity.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCity.setText("City");
        cardPanel.add(lblCity);
        lblCity.setBounds(60, 280, 100, 20);

        txtCity.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtCity);
        txtCity.setBounds(60, 305, 350, 35);

        lblMobileNo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblMobileNo.setText("Mobile No");
        cardPanel.add(lblMobileNo);
        lblMobileNo.setBounds(490, 140, 120, 20);

        txtMobileNo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtMobileNo);
        txtMobileNo.setBounds(490, 165, 350, 35);

        lblLandlineNo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLandlineNo.setText("Landline No");
        cardPanel.add(lblLandlineNo);
        lblLandlineNo.setBounds(490, 210, 120, 20);

        txtLandlineNo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtLandlineNo);
        txtLandlineNo.setBounds(490, 235, 350, 35);

        lblEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblEmail.setText("Email");
        cardPanel.add(lblEmail);
        lblEmail.setBounds(490, 280, 100, 20);

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtEmail);
        txtEmail.setBounds(490, 305, 350, 35);

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
        btnBack.setBounds(540, 420, 100, 36);

        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setBackground(new java.awt.Color(255, 193, 7));
        btnUpdate.setForeground(new java.awt.Color(30, 30, 30));
        btnUpdate.setBorderPainted(false);
        btnUpdate.setFocusPainted(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        cardPanel.add(btnUpdate);
        btnUpdate.setBounds(650, 420, 100, 36);

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
        btnNext.setBounds(760, 420, 100, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> new PA_PR_1(controller).setVisible(true));
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        boolean ok = controller.saveStep2(txtAddressLine1.getText(), txtAddressLine2.getText(), txtCity.getText(),
                txtMobileNo.getText(), txtLandlineNo.getText(), txtEmail.getText());
        if (ok) {
            IconFactory.showSuccessDialog(this, "Profile Updated Successfully!", null);
        } else {
            IconFactory.showErrorDialog(this, "Couldn't save — Mobile No is required.", null);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        boolean ok = controller.saveStep2(txtAddressLine1.getText(), txtAddressLine2.getText(), txtCity.getText(),
                txtMobileNo.getText(), txtLandlineNo.getText(), txtEmail.getText());
        if (!ok) {
            IconFactory.showErrorDialog(this, "Mobile No is required.", null);
            return;
        }
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> new PA_PR_3(controller).setVisible(true));
    }//GEN-LAST:event_btnNextActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new PA_PR_2().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblAddressLine2;
    private javax.swing.JLabel lblCity;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblLandlineNo;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMobileNo;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtAddressLine1;
    private javax.swing.JTextField txtAddressLine2;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtLandlineNo;
    private javax.swing.JTextField txtMobileNo;
    // End of variables declaration//GEN-END:variables
}
