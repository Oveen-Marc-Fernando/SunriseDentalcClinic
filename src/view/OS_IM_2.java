package view;

import controller.InventoryManagementController;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Step 2: Supplier Information Form & Final Submission Dialog.
 * View only — all logic delegated to InventoryManagementController.
 *
 * @author oveen
 */
public class OS_IM_2 extends javax.swing.JFrame {

    private final InventoryManagementController controller;

    public OS_IM_2(InventoryManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void showSuccessDialog() {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(380, 220);
        dialog.setLocationRelativeTo(this);

        JPanel pnlModal = new JPanel(null);
        pnlModal.setBackground(Color.WHITE);
        pnlModal.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));

        // Green checkmark icon — drawn as a vector circle + glyph (via
        // IconFactory) rather than an upscaled PNG, so it stays crisp at
        // any size instead of blurring like a stretched raster icon would.
        JLabel lblCheckIcon = new JLabel(IconFactory.check(Color.WHITE, 26), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 200, 83));
                g2.fill(new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblCheckIcon.setBounds(162, 20, 55, 55);
        pnlModal.add(lblCheckIcon);

        JLabel lblMessage = new JLabel("Product Added Successfully!");
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMessage.setForeground(new Color(30, 30, 30));
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMessage.setBounds(20, 90, 340, 30);
        pnlModal.add(lblMessage);

        JButton btnDone = new JButton("Done");
        btnDone.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDone.setBackground(new Color(0, 122, 255));
        btnDone.setForeground(Color.WHITE);
        btnDone.setBorderPainted(false);
        btnDone.setFocusPainted(false);
        btnDone.setBounds(135, 145, 110, 36);
        btnDone.addActionListener(e -> {
            dialog.dispose();
            javax.swing.SwingUtilities.invokeLater(() -> new OS_IM_Grid().setVisible(true));
            this.dispose();
        });
        pnlModal.add(btnDone);

        dialog.getContentPane().add(pnlModal);
        dialog.setVisible(true);
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
        lblSupplierName = new javax.swing.JLabel();
        txtSupplierName = new javax.swing.JTextField();
        lblBuyingPrice = new javax.swing.JLabel();
        txtBuyingPrice = new javax.swing.JTextField();
        lblContactNumber = new javax.swing.JLabel();
        txtContactNumber = new javax.swing.JTextField();
        lblSellingPrice = new javax.swing.JLabel();
        txtSellingPrice = new javax.swing.JTextField();
        lblCompanyName = new javax.swing.JLabel();
        txtCompanyName = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Inventory Management (Step 2)");
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
        lblStep1.setBackground(new java.awt.Color(0, 0, 0));
        lblStep1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep1.setText("1");
        lblStep1.setOpaque(true);
        cardPanel.add(lblStep1);
        lblStep1.setBounds(360, 30, 40, 40);

        lblStep2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep2.setForeground(new java.awt.Color(255, 255, 255));
        lblStep2.setBackground(new java.awt.Color(231, 115, 36));
        lblStep2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep2.setText("2");
        lblStep2.setOpaque(true);
        cardPanel.add(lblStep2);
        lblStep2.setBounds(500, 30, 40, 40);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Supplier Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblSupplierName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSupplierName.setText("Supplier Name");
        cardPanel.add(lblSupplierName);
        lblSupplierName.setBounds(60, 140, 150, 25);

        txtSupplierName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtSupplierName);
        txtSupplierName.setBounds(60, 170, 350, 35);

        lblBuyingPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblBuyingPrice.setText("Buying Price");
        cardPanel.add(lblBuyingPrice);
        lblBuyingPrice.setBounds(490, 140, 150, 25);

        txtBuyingPrice.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtBuyingPrice);
        txtBuyingPrice.setBounds(490, 170, 350, 35);

        lblContactNumber.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblContactNumber.setText("Contact Number");
        cardPanel.add(lblContactNumber);
        lblContactNumber.setBounds(60, 210, 150, 25);

        txtContactNumber.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtContactNumber);
        txtContactNumber.setBounds(60, 240, 350, 35);

        lblSellingPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSellingPrice.setText("Selling Price");
        cardPanel.add(lblSellingPrice);
        lblSellingPrice.setBounds(490, 210, 150, 25);

        txtSellingPrice.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtSellingPrice);
        txtSellingPrice.setBounds(490, 240, 350, 35);

        lblCompanyName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCompanyName.setText("Company Name");
        cardPanel.add(lblCompanyName);
        lblCompanyName.setBounds(60, 280, 150, 25);

        txtCompanyName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtCompanyName);
        txtCompanyName.setBounds(60, 310, 350, 35);

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
        btnSave.setBounds(740, 420, 100, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        controller.goBackFromStep2(this);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        boolean ok = controller.submitFromStep2(
                txtSupplierName.getText(),
                txtBuyingPrice.getText(),
                txtContactNumber.getText(),
                txtSellingPrice.getText(),
                txtCompanyName.getText(),
                this);
        if (ok) {
            showSuccessDialog();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_IM_2(new InventoryManagementController()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblBuyingPrice;
    private javax.swing.JLabel lblCompanyName;
    private javax.swing.JLabel lblContactNumber;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSellingPrice;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblSupplierName;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtBuyingPrice;
    private javax.swing.JTextField txtCompanyName;
    private javax.swing.JTextField txtContactNumber;
    private javax.swing.JTextField txtSellingPrice;
    private javax.swing.JTextField txtSupplierName;
    // End of variables declaration//GEN-END:variables
}
