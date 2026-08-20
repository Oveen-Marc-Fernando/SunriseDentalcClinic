package view;

import controller.InventoryManagementController;

/**
 * Step 1: Product Information Form
 * View only — all logic delegated to InventoryManagementController.
 *
 * @author oveen
 */
public class OS_IM_1 extends javax.swing.JFrame {

    private final InventoryManagementController controller;

    public OS_IM_1() {
        this(new InventoryManagementController());
    }

    public OS_IM_1(InventoryManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        DateTimePicker.attachDate(txtManufactureDate);
        DateTimePicker.attachDate(txtExpireDate);
        setupProductId();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    /** Product ID is auto-generated (e.g. "I104") and read-only, same convention as Appointment No / Approval ID. */
    private void setupProductId() {
        txtProductId.setEditable(false);
        txtProductId.setBackground(new java.awt.Color(235, 235, 235));
        String existing = controller.getInventoryModel().getProductId();
        txtProductId.setText(existing != null && !existing.trim().isEmpty()
                ? existing
                : InventoryManagementController.nextProductId());
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
        lblSubtitle = new javax.swing.JLabel();
        lblProductId = new javax.swing.JLabel();
        txtProductId = new javax.swing.JTextField();
        lblQuantity = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        lblProductType = new javax.swing.JLabel();
        cmbProductType = new javax.swing.JComboBox<String>();
        lblManufactureDate = new javax.swing.JLabel();
        txtManufactureDate = new javax.swing.JTextField();
        lblProductName = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        lblExpireDate = new javax.swing.JLabel();
        txtExpireDate = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Inventory Management (Step 1)");
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
        lblTitle.setText("Inventory Management");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 420, 40);

        cardPanel.setBackground(new java.awt.Color(248, 249, 250));
        cardPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.setLayout(null);

        sepLine.setForeground(new java.awt.Color(0, 0, 0));
        cardPanel.add(sepLine);
        sepLine.setBounds(360, 50, 180, 4);

        lblStep1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep1.setForeground(new java.awt.Color(255, 255, 255));
        lblStep1.setBackground(new java.awt.Color(231, 115, 36));
        lblStep1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep1.setText("1");
        lblStep1.setOpaque(true);
        cardPanel.add(lblStep1);
        lblStep1.setBounds(360, 30, 40, 40);

        lblStep2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep2.setForeground(new java.awt.Color(255, 255, 255));
        lblStep2.setBackground(new java.awt.Color(0, 0, 0));
        lblStep2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep2.setText("2");
        lblStep2.setOpaque(true);
        cardPanel.add(lblStep2);
        lblStep2.setBounds(500, 30, 40, 40);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Product Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblProductId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblProductId.setText("Product ID");
        cardPanel.add(lblProductId);
        lblProductId.setBounds(60, 140, 150, 25);

        txtProductId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtProductId);
        txtProductId.setBounds(60, 170, 350, 35);

        lblQuantity.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblQuantity.setText("Quantity");
        cardPanel.add(lblQuantity);
        lblQuantity.setBounds(490, 140, 150, 25);

        txtQuantity.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtQuantity);
        txtQuantity.setBounds(490, 170, 350, 35);

        lblProductType.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblProductType.setText("Product Type");
        cardPanel.add(lblProductType);
        lblProductType.setBounds(60, 210, 150, 25);

        cmbProductType.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbProductType.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "Medical Supplies", "Dental Tools", "Equipment", "Consumables", "Medication" }));
        cardPanel.add(cmbProductType);
        cmbProductType.setBounds(60, 240, 350, 35);

        lblManufactureDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblManufactureDate.setText("Manufacture Date");
        cardPanel.add(lblManufactureDate);
        lblManufactureDate.setBounds(490, 210, 150, 25);

        txtManufactureDate.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtManufactureDate);
        txtManufactureDate.setBounds(490, 240, 350, 35);

        lblProductName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblProductName.setText("Product Name");
        cardPanel.add(lblProductName);
        lblProductName.setBounds(60, 280, 150, 25);

        txtProductName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtProductName);
        txtProductName.setBounds(60, 310, 350, 35);

        lblExpireDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblExpireDate.setText("Expire Date");
        cardPanel.add(lblExpireDate);
        lblExpireDate.setBounds(490, 280, 150, 25);

        txtExpireDate.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtExpireDate);
        txtExpireDate.setBounds(490, 310, 350, 35);

        lblDescription.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDescription.setText("Description");
        cardPanel.add(lblDescription);
        lblDescription.setBounds(60, 350, 150, 25);

        txtDescription.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDescription);
        txtDescription.setBounds(60, 380, 350, 35);

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
            new OS_IM_Grid().setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        controller.goNextFromStep1(
                txtProductId.getText(),
                txtQuantity.getText(),
                (String) cmbProductType.getSelectedItem(),
                txtManufactureDate.getText(),
                txtProductName.getText(),
                txtExpireDate.getText(),
                txtDescription.getText(),
                this);
    }//GEN-LAST:event_btnNextActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_IM_1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox<String> cmbProductType;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblExpireDate;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblManufactureDate;
    private javax.swing.JLabel lblProductId;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblProductType;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtExpireDate;
    private javax.swing.JTextField txtManufactureDate;
    private javax.swing.JTextField txtProductId;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables
}
