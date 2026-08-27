package view;

import controller.DentistProfileController;
import model.User;

/**
 * My Profile — Step 1: My Information
 * View only — all logic delegated to DentistProfileController.
 */
public class D_Profile_1 extends javax.swing.JFrame {

    private final DentistProfileController controller;

    /** Called from Dentist_Dashboard — opens the signed-in dentist's own profile. */
    public D_Profile_1(User user) {
        this(new DentistProfileController(user));
    }

    /** Called from the controller when navigating back from Step 2. */
    public D_Profile_1(DentistProfileController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        DateTimePicker.attachDate(txtDOB);
        setupDentistId();
        setupUsername();
        prefillFromModel();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    /** Dentist ID is auto-generated and locked — same read-only convention as Patient ID elsewhere in this app. */
    private void setupDentistId() {
        txtDentistId.setEditable(false);
        txtDentistId.setBackground(new java.awt.Color(235, 235, 235));
    }

    /**
     * Username is locked — only Office Staff sets/changes it (Dentist
     * Management), same reasoning as the temp-password flow: a dentist's own
     * login identity shouldn't drift out from under whoever manages accounts.
     * Shown here purely as read-only reference to what to log in with.
     */
    private void setupUsername() {
        txtUsername.setEditable(false);
        txtUsername.setBackground(new java.awt.Color(235, 235, 235));
        model.User user = controller.getCurrentUser();
        if (user != null && user.getUsername() != null) {
            txtUsername.setText(user.getUsername());
        }
    }

    private void prefillFromModel() {
        model.DentistModel m = controller.getDentistModel();
        if (m == null) return;
        if ("Dr".equals(m.getTitle()))  chkDr.setSelected(true);
        if ("Mr".equals(m.getTitle()))  chkMr.setSelected(true);
        if ("Mrs".equals(m.getTitle())) chkMrs.setSelected(true);
        if ("Male".equals(m.getGender()))   chkMale.setSelected(true);
        if ("Female".equals(m.getGender())) chkFemale.setSelected(true);
        if (m.getFullName() != null) txtFullName.setText(m.getFullName());
        if (m.getDentistId() != null) txtDentistId.setText(m.getDentistId());
        if (m.getDob() != null)      txtDOB.setText(m.getDob());
        if (m.getNic() != null)      txtNIC.setText(m.getNic());
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
        lblDentistId = new javax.swing.JLabel();
        txtDentistId = new javax.swing.JTextField();
        lblDOB = new javax.swing.JLabel();
        txtDOB = new javax.swing.JTextField();
        lblNIC = new javax.swing.JLabel();
        txtNIC = new javax.swing.JTextField();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – My Profile (Step 1)");
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
        lblSubtitle.setText("My Information");
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

        lblDentistId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDentistId.setText("Dentist ID");
        cardPanel.add(lblDentistId);
        lblDentistId.setBounds(60, 300, 100, 25);

        txtDentistId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDentistId);
        txtDentistId.setBounds(60, 330, 350, 35);

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

        lblUsername.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblUsername.setText("Username");
        cardPanel.add(lblUsername);
        lblUsername.setBounds(490, 300, 140, 25);

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtUsername);
        txtUsername.setBounds(490, 330, 350, 35);

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
        controller.goBackToDashboard(this);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // Resolve title selection
        String title = chkDr.isSelected() ? "Dr" : chkMr.isSelected() ? "Mr" : chkMrs.isSelected() ? "Mrs" : "";
        String gender = chkMale.isSelected() ? "Male" : chkFemale.isSelected() ? "Female" : "";

        controller.goNextFromStep1(
                title,
                gender,
                txtFullName.getText(),
                txtDentistId.getText(),
                txtDOB.getText(),
                txtNIC.getText(),
                this
        );
    }//GEN-LAST:event_btnNextActionPerformed

    // Saves just this page immediately — no need to walk the rest of the wizard.
    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        String title = chkDr.isSelected() ? "Dr" : chkMr.isSelected() ? "Mr" : chkMrs.isSelected() ? "Mrs" : "";
        String gender = chkMale.isSelected() ? "Male" : chkFemale.isSelected() ? "Female" : "";

        controller.updateFromStep1(
                title,
                gender,
                txtFullName.getText(),
                txtDentistId.getText(),
                txtDOB.getText(),
                txtNIC.getText(),
                this
        );
    }//GEN-LAST:event_btnUpdateActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new D_Profile_1((User) null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox chkDr;
    private javax.swing.JCheckBox chkFemale;
    private javax.swing.JCheckBox chkMale;
    private javax.swing.JCheckBox chkMr;
    private javax.swing.JCheckBox chkMrs;
    private javax.swing.JLabel lblDOB;
    private javax.swing.JLabel lblDentistId;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblNIC;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblStep5;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitlePrefix;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtDOB;
    private javax.swing.JTextField txtDentistId;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtNIC;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
