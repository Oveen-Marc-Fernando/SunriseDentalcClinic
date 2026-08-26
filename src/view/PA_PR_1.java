package view;

import controller.AppController;
import controller.PatientProfileController;

/**
 * Patient &gt; My Profile — Step 1: Personal Information. Same 4-step
 * wizard shell as OS_PM_1 (Office Staff's own Patient registration wizard),
 * but self-service over the logged-in patient's own already-existing
 * record — pre-filled fields, and an "Update" button that saves this step
 * in place instead of only accumulating in memory until a final submit.
 *
 * @author oveen
 */
public class PA_PR_1 extends javax.swing.JFrame {

    private final PatientProfileController controller;

    public PA_PR_1() {
        this(new PatientProfileController(null));
    }

    public PA_PR_1(PatientProfileController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40));
        IconFactory.roundCorners(navBar, 30);
        populateFieldsFromModel();
        setupPatientId();
        setupUsername();
        DateTimePicker.attachDate(txtDOB);
        setSize(1016, 769);
        setLocationRelativeTo(null);
    }

    private void populateFieldsFromModel() {
        model.PatientModel m = controller.getPatientModel();
        if (m == null) return;

        if ("Mr".equalsIgnoreCase(m.getTitle())) chkMr.setSelected(true);
        else if ("Mrs".equalsIgnoreCase(m.getTitle())) chkMrs.setSelected(true);

        if ("Male".equalsIgnoreCase(m.getGender())) chkMale.setSelected(true);
        else if ("Female".equalsIgnoreCase(m.getGender())) chkFemale.setSelected(true);

        if (m.getFullName() != null) txtFullName.setText(m.getFullName());
        if (m.getDob() != null) txtDOB.setText(m.getDob());
        if (m.getAge() != null) txtAge.setText(m.getAge());
        if (m.getNic() != null) txtNic.setText(m.getNic());
    }

    /** Patient ID is auto-generated/resolved by the controller and read-only, same convention as everywhere else in the app. */
    private void setupPatientId() {
        txtPatientId.setEditable(false);
        txtPatientId.setBackground(new java.awt.Color(235, 235, 235));
        txtPatientId.setText(controller.getPatientModel().getPatientId());
    }

    /**
     * Username is locked — only Office Staff sets/changes it (Patient
     * Management), same reasoning as the dentist side's D_Profile_1: a
     * patient's own login identity shouldn't drift out from under whoever
     * manages accounts. Shown here purely as read-only reference.
     */
    private void setupUsername() {
        txtUsername.setEditable(false);
        txtUsername.setBackground(new java.awt.Color(235, 235, 235));
        String username = controller.getUsername();
        if (username != null) {
            txtUsername.setText(username);
        }
    }

    private String selectedTitle() {
        return chkMr.isSelected() ? "Mr" : chkMrs.isSelected() ? "Mrs" : "";
    }

    private String selectedGender() {
        return chkMale.isSelected() ? "Male" : chkFemale.isSelected() ? "Female" : "";
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
        lblTitlePrefix = new javax.swing.JLabel();
        chkMr = new javax.swing.JCheckBox();
        chkMrs = new javax.swing.JCheckBox();
        lblGender = new javax.swing.JLabel();
        chkMale = new javax.swing.JCheckBox();
        chkFemale = new javax.swing.JCheckBox();
        lblFullName = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        lblPatientId = new javax.swing.JLabel();
        txtPatientId = new javax.swing.JTextField();
        lblDOB = new javax.swing.JLabel();
        txtDOB = new javax.swing.JTextField();
        lblNic = new javax.swing.JLabel();
        txtNic = new javax.swing.JTextField();
        lblAge = new javax.swing.JLabel();
        txtAge = new javax.swing.JTextField();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental — My Profile (Step 1)");
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
        lblStep1.setBackground(new java.awt.Color(231, 115, 36));
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
        lblSubtitle.setText("Personal Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblTitlePrefix.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTitlePrefix.setText("Title");
        cardPanel.add(lblTitlePrefix);
        lblTitlePrefix.setBounds(60, 140, 80, 25);

        chkMr.setText("Mr");
        cardPanel.add(chkMr);
        chkMr.setBounds(150, 140, 50, 25);

        chkMrs.setText("Mrs");
        cardPanel.add(chkMrs);
        chkMrs.setBounds(220, 140, 55, 25);

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

        lblPatientId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPatientId.setText("Patient ID");
        cardPanel.add(lblPatientId);
        lblPatientId.setBounds(60, 300, 100, 25);

        txtPatientId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtPatientId);
        txtPatientId.setBounds(60, 330, 350, 35);

        lblDOB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDOB.setText("DOB");
        cardPanel.add(lblDOB);
        lblDOB.setBounds(490, 140, 100, 25);

        txtDOB.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDOB);
        txtDOB.setBounds(490, 170, 350, 35);

        lblAge.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAge.setText("Age");
        cardPanel.add(lblAge);
        lblAge.setBounds(490, 220, 100, 25);

        txtAge.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtAge);
        txtAge.setBounds(490, 250, 350, 35);

        lblNic.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNic.setText("NIC");
        cardPanel.add(lblNic);
        lblNic.setBounds(490, 300, 120, 25);

        txtNic.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtNic);
        txtNic.setBounds(490, 330, 350, 35);

        lblUsername.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblUsername.setText("Username");
        cardPanel.add(lblUsername);
        lblUsername.setBounds(60, 380, 150, 25);

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtUsername);
        txtUsername.setBounds(60, 410, 350, 35);

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
        btnBack.setBounds(540, 480, 100, 36);

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
        btnUpdate.setBounds(650, 480, 100, 36);

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
        btnNext.setBounds(760, 480, 100, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 540);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 730);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Patient_Dashboard(AppController.getCurrentUser()).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        boolean ok = controller.saveStep1(selectedTitle(), selectedGender(), txtFullName.getText(),
                txtDOB.getText(), txtAge.getText(), txtNic.getText());
        if (ok) {
            IconFactory.showSuccessDialog(this, "Profile Updated Successfully!", null);
        } else {
            IconFactory.showErrorDialog(this, "Couldn't save — Full Name is required.", null);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        boolean ok = controller.saveStep1(selectedTitle(), selectedGender(), txtFullName.getText(),
                txtDOB.getText(), txtAge.getText(), txtNic.getText());
        if (!ok) {
            IconFactory.showErrorDialog(this, "Full Name is required.", null);
            return;
        }
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> new PA_PR_2(controller).setVisible(true));
    }//GEN-LAST:event_btnNextActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new PA_PR_1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox chkFemale;
    private javax.swing.JCheckBox chkMale;
    private javax.swing.JCheckBox chkMr;
    private javax.swing.JCheckBox chkMrs;
    private javax.swing.JLabel lblAge;
    private javax.swing.JLabel lblDOB;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblNic;
    private javax.swing.JLabel lblPatientId;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitlePrefix;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtDOB;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtNic;
    private javax.swing.JTextField txtPatientId;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
