package view;

import controller.SupplyRequestController;

/**
 * "Request Supplies" — Add Request form. Reached from D_RS_Grid's
 * "Request Supplies" button. A single-step form (no wizard steps) — all
 * validation/persistence is delegated to SupplyRequestController.
 *
 * Request ID and Product ID are both auto-filled and locked (never typed by
 * the dentist): Request ID is generated on open, and Product ID is looked up
 * the moment a Product Type + Product Name pair is chosen. Only Product
 * Type, Product Name, and Quantity are things the dentist actually picks.
 *
 * @author oveen
 */
public class D_RS_Add extends javax.swing.JFrame {

    private static final java.awt.Color COLOR_READONLY_BG = new java.awt.Color(238, 238, 238);
    private static final java.awt.Color COLOR_READONLY_FG = new java.awt.Color(90, 90, 90);

    private final SupplyRequestController controller;
    private final model.User user;

    /** Backward-compatible no-arg entry point (e.g. {@code main()}) — submits without a dentist attribution. */
    public D_RS_Add() {
        this((model.User) null);
    }

    public D_RS_Add(model.User user) {
        this.user = user;
        this.controller = new SupplyRequestController();
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        prefillRequestId();
        setupProductLookup();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    /** Request ID is never typed — it's generated the moment this screen opens. */
    private void prefillRequestId() {
        txtRequestId.setText(SupplyRequestController.generateNextRequestId());
        txtRequestId.setEditable(false);
        txtRequestId.setBackground(COLOR_READONLY_BG);
        txtRequestId.setForeground(COLOR_READONLY_FG);
    }

    /**
     * Wires the Product Type → Product Name → Product ID chain: picking a
     * type narrows which names are offered, and picking a name looks up its
     * Product ID automatically. Product ID stays locked the whole time —
     * it's never something the dentist types in directly.
     */
    private void setupProductLookup() {
        txtProductId.setEditable(false);
        txtProductId.setBackground(COLOR_READONLY_BG);
        txtProductId.setForeground(COLOR_READONLY_FG);

        cmbProductType.setModel(new javax.swing.DefaultComboBoxModel<>(
                SupplyRequestController.getProductTypes().toArray(new String[0])));
        cmbProductType.addActionListener(e -> refreshProductNames());
        cmbProductName.addActionListener(e -> refreshProductId());

        refreshProductNames(); // populate Product Name (and then Product ID) for the initial type
    }

    private void refreshProductNames() {
        String type = (String) cmbProductType.getSelectedItem();
        java.util.List<String> names = type == null
                ? java.util.Collections.emptyList()
                : SupplyRequestController.getProductNames(type);
        cmbProductName.setModel(new javax.swing.DefaultComboBoxModel<>(names.toArray(new String[0])));
        refreshProductId();
    }

    private void refreshProductId() {
        String type = (String) cmbProductType.getSelectedItem();
        String name = (String) cmbProductName.getSelectedItem();
        txtProductId.setText(SupplyRequestController.lookupProductId(type, name));
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        lblSubtitle = new javax.swing.JLabel();
        lblRequestId = new javax.swing.JLabel();
        txtRequestId = new javax.swing.JTextField();
        lblProductId = new javax.swing.JLabel();
        txtProductId = new javax.swing.JTextField();
        lblProductType = new javax.swing.JLabel();
        cmbProductType = new javax.swing.JComboBox<String>();
        lblQuantity = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        lblProductName = new javax.swing.JLabel();
        cmbProductName = new javax.swing.JComboBox<String>();
        btnBack = new javax.swing.JButton();
        btnRequest = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Request Supplies (Add Request)");
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
        lblTitle.setText("Request Supplies");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 400, 40);

        cardPanel.setBackground(new java.awt.Color(248, 249, 250));
        cardPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.setLayout(null);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Add Request");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 35, 400, 30);

        lblRequestId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblRequestId.setText("Request ID");
        cardPanel.add(lblRequestId);
        lblRequestId.setBounds(60, 100, 150, 25);

        txtRequestId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtRequestId);
        txtRequestId.setBounds(60, 130, 350, 35);

        lblProductId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblProductId.setText("Product ID");
        cardPanel.add(lblProductId);
        lblProductId.setBounds(490, 100, 150, 25);

        txtProductId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtProductId);
        txtProductId.setBounds(490, 130, 350, 35);

        lblProductType.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblProductType.setText("Product Type");
        cardPanel.add(lblProductType);
        lblProductType.setBounds(60, 180, 150, 25);

        cmbProductType.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbProductType);
        cmbProductType.setBounds(60, 210, 350, 35);

        lblQuantity.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblQuantity.setText("Quantity");
        cardPanel.add(lblQuantity);
        lblQuantity.setBounds(490, 180, 150, 25);

        txtQuantity.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtQuantity);
        txtQuantity.setBounds(490, 210, 350, 35);

        lblProductName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblProductName.setText("Product Name");
        cardPanel.add(lblProductName);
        lblProductName.setBounds(60, 260, 150, 25);

        cmbProductName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbProductName);
        cmbProductName.setBounds(60, 290, 350, 35);

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

        btnRequest.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRequest.setText("Request");
        btnRequest.setBackground(new java.awt.Color(0, 200, 83));
        btnRequest.setForeground(new java.awt.Color(255, 255, 255));
        btnRequest.setBorderPainted(false);
        btnRequest.setFocusPainted(false);
        btnRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRequestActionPerformed(evt);
            }
        });
        cardPanel.add(btnRequest);
        btnRequest.setBounds(740, 420, 100, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new D_RS_Grid(user).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRequestActionPerformed
        boolean ok = controller.submitRequest(
                txtRequestId.getText(),
                txtProductId.getText(),
                (String) cmbProductType.getSelectedItem(),
                (String) cmbProductName.getSelectedItem(),
                txtQuantity.getText(),
                user != null ? user.getFullName() : null,
                this
        );
        if (!ok) {
            return;
        }
        IconFactory.showSuccessDialog(this, "Product Added\nSuccessfully!", () -> {
            dispose();
            javax.swing.SwingUtilities.invokeLater(() -> {
                new D_RS_Grid(user).setVisible(true);
            });
        });
    }//GEN-LAST:event_btnRequestActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new D_RS_Add().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRequest;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox<String> cmbProductName;
    private javax.swing.JComboBox<String> cmbProductType;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblProductId;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblProductType;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblRequestId;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JTextField txtProductId;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtRequestId;
    // End of variables declaration//GEN-END:variables
}
