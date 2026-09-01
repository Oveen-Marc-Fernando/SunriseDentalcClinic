package view;

import controller.BillingManagementController;
import model.BillingModel;

/**
 * Step 4: Total Bill Amount Form & Final Submission Dialog.
 * View only — logic delegated to BillingManagementController.
 *
 * @author oveen
 */
public class OS_BM_4 extends javax.swing.JFrame {

    private final BillingManagementController controller;

    public OS_BM_4() {
        this(new BillingManagementController());
    }

    public OS_BM_4(BillingManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        populateFieldsFromModel();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void populateFieldsFromModel() {
        BillingModel m = controller.getBillingModel();
        if (m == null) return;
        txtBillingId.setText(m.getBillingId());
        txtApptCharges.setText(BillingManagementController.formatCurrency(m.getAppointmentCharges()));
        txtClinical.setText(BillingManagementController.formatCurrency(m.getClinicalTotal()));
        txtMedicine.setText(BillingManagementController.formatCurrency(m.getMedicineTotal()));
        txtTotal.setText(BillingManagementController.formatCurrency(m.getTotalBillAmount()));
    }

    /** Shown right after a successful Save — the popup itself carries the "Bill Generated Successfully!" banner. */
    private void showSuccessDialog() {
        new BillPreviewDialog(this, controller.getBillingModel(), true, () -> {
            javax.swing.SwingUtilities.invokeLater(() -> new OS_BM_Grid().setVisible(true));
            this.dispose();
        }).setVisible(true);
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
        btnBack = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblBillingIdCap = new javax.swing.JLabel();
        txtBillingId = new javax.swing.JTextField();
        lblApptChargesCap = new javax.swing.JLabel();
        txtApptCharges = new javax.swing.JTextField();
        lblClinicalCap = new javax.swing.JLabel();
        txtClinical = new javax.swing.JTextField();
        lblMedicineCap = new javax.swing.JLabel();
        txtMedicine = new javax.swing.JTextField();
        lblTotalCap = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Billing Management (Step 4)");
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
        lblStep3.setBackground(new java.awt.Color(0, 0, 0));
        lblStep3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep3.setText("3");
        lblStep3.setOpaque(true);
        cardPanel.add(lblStep3);
        lblStep3.setBounds(500, 30, 40, 40);

        lblStep4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep4.setForeground(new java.awt.Color(255, 255, 255));
        lblStep4.setBackground(new java.awt.Color(231, 115, 36));
        lblStep4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep4.setText("4");
        lblStep4.setOpaque(true);
        cardPanel.add(lblStep4);
        lblStep4.setBounds(640, 30, 40, 40);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Total Bill Amount");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 300, 30);

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
        btnBack.setBounds(690, 88, 90, 32);

        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSave.setText("Save");
        btnSave.setBackground(new java.awt.Color(231, 115, 36));
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setBorderPainted(false);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        cardPanel.add(btnSave);
        btnSave.setBounds(790, 88, 90, 32);

        lblBillingIdCap.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblBillingIdCap.setText("Billing ID:");
        cardPanel.add(lblBillingIdCap);
        lblBillingIdCap.setBounds(60, 150, 150, 25);

        txtBillingId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtBillingId.setEnabled(false);
        txtBillingId.setBackground(new java.awt.Color(235, 235, 235));
        cardPanel.add(txtBillingId);
        txtBillingId.setBounds(60, 180, 320, 35);

        lblApptChargesCap.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblApptChargesCap.setText("Appointment Charges:");
        cardPanel.add(lblApptChargesCap);
        lblApptChargesCap.setBounds(60, 230, 190, 25);

        txtApptCharges.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtApptCharges.setEnabled(false);
        txtApptCharges.setBackground(new java.awt.Color(235, 235, 235));
        cardPanel.add(txtApptCharges);
        txtApptCharges.setBounds(60, 260, 320, 35);

        lblClinicalCap.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblClinicalCap.setText("Clinical Charges:");
        cardPanel.add(lblClinicalCap);
        lblClinicalCap.setBounds(60, 310, 190, 25);

        txtClinical.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtClinical.setEnabled(false);
        txtClinical.setBackground(new java.awt.Color(235, 235, 235));
        cardPanel.add(txtClinical);
        txtClinical.setBounds(60, 340, 320, 35);

        lblMedicineCap.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblMedicineCap.setText("Medicine Charges:");
        cardPanel.add(lblMedicineCap);
        lblMedicineCap.setBounds(60, 390, 190, 25);

        txtMedicine.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtMedicine.setEnabled(false);
        txtMedicine.setBackground(new java.awt.Color(235, 235, 235));
        cardPanel.add(txtMedicine);
        txtMedicine.setBounds(60, 420, 320, 35);

        lblTotalCap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalCap.setForeground(new java.awt.Color(220, 53, 69));
        lblTotalCap.setText("Total Bill Amount:");
        cardPanel.add(lblTotalCap);
        lblTotalCap.setBounds(450, 184, 190, 25);

        txtTotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtTotal.setForeground(new java.awt.Color(220, 53, 69));
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTotal.setEnabled(false);
        txtTotal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtTotal);
        txtTotal.setBounds(650, 178, 190, 35);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        controller.goBackFromStep4(this);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // Disabled first, before anything else runs — a fast double-click
        // used to fire this handler twice with the same already-generated
        // Billing ID (it's only assigned once, back in Step 3), so the
        // second save attempt would hit a duplicate-key error against the
        // first one's now-inserted row. Re-enabled only if the save didn't
        // actually go through, so a genuine retry after a failure still works.
        btnSave.setEnabled(false);
        boolean success = controller.submitFromStep4(this);
        if (success) {
            showSuccessDialog();
        } else {
            btnSave.setEnabled(true);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Couldn't save this bill — it may have already been saved once already, "
                    + "or the database is unreachable. Check the Billing grid before trying again.",
                    "Save Failed", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_BM_4().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblApptChargesCap;
    private javax.swing.JLabel lblBillingIdCap;
    private javax.swing.JLabel lblClinicalCap;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMedicineCap;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalCap;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtApptCharges;
    private javax.swing.JTextField txtBillingId;
    private javax.swing.JTextField txtClinical;
    private javax.swing.JTextField txtMedicine;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
