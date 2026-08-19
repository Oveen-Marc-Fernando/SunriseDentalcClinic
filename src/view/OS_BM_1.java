package view;

import controller.BillingManagementController;

/**
 * Step 1: Appointment Charges Form
 * View only — logic delegated to BillingManagementController.
 *
 * @author oveen
 */
public class OS_BM_1 extends javax.swing.JFrame {

    private final BillingManagementController controller;

    public OS_BM_1() {
        this(new BillingManagementController());
    }

    public OS_BM_1(BillingManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        populateAppointmentDropdown();
        applyAppointmentSelection();
        cmbApptId.addActionListener(evt -> applyAppointmentSelection());
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void populateAppointmentDropdown() {
        cmbApptId.setModel(new javax.swing.DefaultComboBoxModel<>(controller.getAppointmentIds()));
    }

    /** Auto-fills Dentist / Patient / Date / default Charges from the selected Appointment ID. */
    private void applyAppointmentSelection() {
        String id = (String) cmbApptId.getSelectedItem();
        if (id == null) return;
        txtDentist.setText(controller.getDentistFor(id));
        txtPatient.setText(controller.getPatientFor(id));
        txtApptDate.setText(controller.getDateFor(id));
        double charge = controller.getDefaultChargeFor(id);
        txtCharges.setText(charge > 0 ? String.format("%,.0f", charge) : "");
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
        lblApptId = new javax.swing.JLabel();
        cmbApptId = new javax.swing.JComboBox();
        lblDentist = new javax.swing.JLabel();
        txtDentist = new javax.swing.JTextField();
        lblPatient = new javax.swing.JLabel();
        txtPatient = new javax.swing.JTextField();
        lblApptDate = new javax.swing.JLabel();
        txtApptDate = new javax.swing.JTextField();
        lblCharges = new javax.swing.JLabel();
        txtCharges = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Billing Management (Step 1)");
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
        lblTitle.setText("Billing Management");
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
        lblSubtitle.setText("Appointment Charges");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblApptId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblApptId.setText("Appointment ID:");
        cardPanel.add(lblApptId);
        lblApptId.setBounds(60, 150, 150, 25);

        cmbApptId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbApptId);
        cmbApptId.setBounds(60, 180, 320, 35);

        lblDentist.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDentist.setText("Dentist Name:");
        cardPanel.add(lblDentist);
        lblDentist.setBounds(60, 230, 150, 25);

        txtDentist.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtDentist.setEnabled(false);
        txtDentist.setBackground(new java.awt.Color(235, 235, 235));
        cardPanel.add(txtDentist);
        txtDentist.setBounds(60, 260, 320, 35);

        lblPatient.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPatient.setText("Patient Name:");
        cardPanel.add(lblPatient);
        lblPatient.setBounds(60, 310, 150, 25);

        txtPatient.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtPatient.setEnabled(false);
        txtPatient.setBackground(new java.awt.Color(235, 235, 235));
        cardPanel.add(txtPatient);
        txtPatient.setBounds(60, 340, 320, 35);

        lblApptDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblApptDate.setText("Appointment Date:");
        cardPanel.add(lblApptDate);
        lblApptDate.setBounds(60, 390, 150, 25);

        txtApptDate.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtApptDate.setEnabled(false);
        txtApptDate.setBackground(new java.awt.Color(235, 235, 235));
        cardPanel.add(txtApptDate);
        txtApptDate.setBounds(60, 420, 320, 35);

        lblCharges.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCharges.setForeground(new java.awt.Color(220, 53, 69));
        lblCharges.setText("Appointment Charges:");
        cardPanel.add(lblCharges);
        lblCharges.setBounds(450, 184, 190, 25);

        txtCharges.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtCharges.setForeground(new java.awt.Color(220, 53, 69));
        txtCharges.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCharges.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtCharges);
        txtCharges.setBounds(650, 178, 190, 35);

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
            new OS_BM_Grid().setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        String apptId = (String) cmbApptId.getSelectedItem();
        // Patient ID isn't shown on this screen, but is still looked up and
        // carried through to the bill (used later e.g. by "Email Bill" to
        // find the right patient's address on file).
        controller.goNextFromStep1(
                apptId,
                controller.getPatientIdFor(apptId),
                txtDentist.getText(),
                txtPatient.getText(),
                txtApptDate.getText(),
                txtCharges.getText(),
                this);
    }//GEN-LAST:event_btnNextActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_BM_1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox cmbApptId;
    private javax.swing.JLabel lblApptDate;
    private javax.swing.JLabel lblApptId;
    private javax.swing.JLabel lblCharges;
    private javax.swing.JLabel lblDentist;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblPatient;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtApptDate;
    private javax.swing.JTextField txtCharges;
    private javax.swing.JTextField txtDentist;
    private javax.swing.JTextField txtPatient;
    // End of variables declaration//GEN-END:variables
}
