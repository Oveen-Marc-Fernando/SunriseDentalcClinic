package view;

import controller.PatientProfileController;

/**
 * Patient &gt; My Profile — Step 3: Medical Information.
 *
 * @author oveen
 */
public class PA_PR_3 extends javax.swing.JFrame {

    private final PatientProfileController controller;

    public PA_PR_3() {
        this(new PatientProfileController(null));
    }

    public PA_PR_3(PatientProfileController controller) {
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
        if (m.getBloodGroup() != null && !m.getBloodGroup().isEmpty()) cmbBloodGroup.setSelectedItem(m.getBloodGroup());
        if (m.getAllergies() != null) txtAllergies.setText(m.getAllergies());
        if (m.getMedicalConditions() != null) txtMedicalConditions.setText(m.getMedicalConditions());
        if (m.getCurrentMedications() != null) txtCurrentMedications.setText(m.getCurrentMedications());
        if (m.getPreviousSurgeries() != null) txtPreviousSurgeries.setText(m.getPreviousSurgeries());
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
        lblBloodGroup = new javax.swing.JLabel();
        cmbBloodGroup = new javax.swing.JComboBox();
        lblAllergies = new javax.swing.JLabel();
        txtAllergies = new javax.swing.JTextField();
        lblMedicalConditions = new javax.swing.JLabel();
        txtMedicalConditions = new javax.swing.JTextField();
        lblCurrentMedications = new javax.swing.JLabel();
        txtCurrentMedications = new javax.swing.JTextField();
        lblPreviousSurgeries = new javax.swing.JLabel();
        txtPreviousSurgeries = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental — My Profile (Step 3)");
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
        lblStep2.setBackground(new java.awt.Color(0, 0, 0));
        lblStep2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep2.setText("2");
        lblStep2.setOpaque(true);
        cardPanel.add(lblStep2);
        lblStep2.setBounds(360, 30, 40, 40);

        lblStep3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep3.setForeground(new java.awt.Color(255, 255, 255));
        lblStep3.setBackground(new java.awt.Color(231, 115, 36));
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
        lblSubtitle.setText("Medical Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblBloodGroup.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblBloodGroup.setText("Blood Group");
        cardPanel.add(lblBloodGroup);
        lblBloodGroup.setBounds(60, 135, 120, 20);

        cmbBloodGroup.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbBloodGroup.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-" }));
        cardPanel.add(cmbBloodGroup);
        cmbBloodGroup.setBounds(60, 160, 350, 35);

        lblAllergies.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAllergies.setText("Allergies");
        cardPanel.add(lblAllergies);
        lblAllergies.setBounds(60, 205, 120, 20);

        txtAllergies.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtAllergies);
        txtAllergies.setBounds(60, 230, 350, 35);

        lblMedicalConditions.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblMedicalConditions.setText("Medical Conditions");
        cardPanel.add(lblMedicalConditions);
        lblMedicalConditions.setBounds(60, 275, 160, 20);

        txtMedicalConditions.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtMedicalConditions);
        txtMedicalConditions.setBounds(60, 300, 350, 35);

        lblCurrentMedications.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCurrentMedications.setText("Current Medications");
        cardPanel.add(lblCurrentMedications);
        lblCurrentMedications.setBounds(490, 135, 160, 20);

        txtCurrentMedications.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtCurrentMedications);
        txtCurrentMedications.setBounds(490, 160, 350, 35);

        lblPreviousSurgeries.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPreviousSurgeries.setText("Previous Surgeries");
        cardPanel.add(lblPreviousSurgeries);
        lblPreviousSurgeries.setBounds(490, 205, 160, 20);

        txtPreviousSurgeries.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtPreviousSurgeries);
        txtPreviousSurgeries.setBounds(490, 230, 350, 35);

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
        javax.swing.SwingUtilities.invokeLater(() -> new PA_PR_2(controller).setVisible(true));
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        boolean ok = save();
        if (ok) {
            IconFactory.showSuccessDialog(this, "Profile Updated Successfully!", null);
        } else {
            IconFactory.showErrorDialog(this, "Couldn't save this profile section — the database may be unreachable.", null);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        save();
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> new PA_PR_4(controller).setVisible(true));
    }//GEN-LAST:event_btnNextActionPerformed

    private boolean save() {
        Object bg = cmbBloodGroup.getSelectedItem();
        return controller.saveStep3(
                bg != null && !"Select".equals(bg) ? bg.toString() : "",
                txtAllergies.getText(),
                txtMedicalConditions.getText(),
                txtCurrentMedications.getText(),
                txtPreviousSurgeries.getText());
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new PA_PR_3().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox cmbBloodGroup;
    private javax.swing.JLabel lblAllergies;
    private javax.swing.JLabel lblBloodGroup;
    private javax.swing.JLabel lblCurrentMedications;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMedicalConditions;
    private javax.swing.JLabel lblPreviousSurgeries;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtAllergies;
    private javax.swing.JTextField txtCurrentMedications;
    private javax.swing.JTextField txtMedicalConditions;
    private javax.swing.JTextField txtPreviousSurgeries;
    // End of variables declaration//GEN-END:variables
}
