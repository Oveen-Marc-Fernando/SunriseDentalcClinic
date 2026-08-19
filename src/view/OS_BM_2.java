package view;

import controller.BillingManagementController;
import java.util.ArrayList;
import java.util.List;

/**
 * Step 2: Clinical Charges Form
 * View only — logic delegated to BillingManagementController.
 *
 * @author oveen
 */
public class OS_BM_2 extends javax.swing.JFrame {

    private static final String[] QTY_OPTIONS = {
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
    };

    private final BillingManagementController controller;
    private javax.swing.JComboBox<String>[] cmbService;
    private javax.swing.JComboBox<String>[] cmbQty;
    private javax.swing.JLabel[] lblPrice;
    private javax.swing.JLabel[] lblCharge;

    public OS_BM_2() {
        this(new BillingManagementController());
    }

    @SuppressWarnings("unchecked")
    public OS_BM_2(BillingManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height

        cmbService = new javax.swing.JComboBox[]{cmbService1, cmbService2, cmbService3, cmbService4, cmbService5};
        cmbQty = new javax.swing.JComboBox[]{cmbQty1, cmbQty2, cmbQty3, cmbQty4, cmbQty5};
        lblPrice = new javax.swing.JLabel[]{lblPrice1, lblPrice2, lblPrice3, lblPrice4, lblPrice5};
        lblCharge = new javax.swing.JLabel[]{lblCharge1, lblCharge2, lblCharge3, lblCharge4, lblCharge5};

        for (int i = 0; i < 5; i++) {
            cmbService[i].setModel(new javax.swing.DefaultComboBoxModel<>(controller.getServiceNames()));
            final int row = i;
            cmbService[i].addActionListener(evt -> recalcRow(row));
            cmbQty[i].addActionListener(evt -> recalcRow(row));
        }

        // Pre-fill row 1 with a sample line item, matching the mockup.
        cmbService[0].setSelectedItem("Dental Implants");
        cmbQty[0].setSelectedItem("2");
        recalcTotal();

        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void recalcRow(int i) {
        String service = (String) cmbService[i].getSelectedItem();
        double price = controller.getServicePrice(service);
        int qty = parseInt((String) cmbQty[i].getSelectedItem());
        boolean active = service != null && !service.isEmpty();
        double charge = active ? price * qty : 0;

        lblPrice[i].setText(active ? BillingManagementController.formatCurrency(price) : "");
        lblCharge[i].setText(active ? BillingManagementController.formatCurrency(charge) : "");
        recalcTotal();
    }

    private void recalcTotal() {
        lblTotalValue.setText(BillingManagementController.formatCurrency(computeTotal()));
    }

    private double computeTotal() {
        double total = 0;
        for (int i = 0; i < 5; i++) {
            String service = (String) cmbService[i].getSelectedItem();
            if (service == null || service.isEmpty()) continue;
            double price = controller.getServicePrice(service);
            int qty = parseInt((String) cmbQty[i].getSelectedItem());
            total += price * qty;
        }
        return total;
    }

    private List<String> collectLines() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String service = (String) cmbService[i].getSelectedItem();
            if (service == null || service.isEmpty()) continue;
            String qty = (String) cmbQty[i].getSelectedItem();
            double price = controller.getServicePrice(service);
            lines.add(service + " x" + qty + " = " + BillingManagementController.formatCurrency(price * parseInt(qty)));
        }
        return lines;
    }

    private static int parseInt(String s) {
        try {
            return s != null ? Integer.parseInt(s) : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
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
        btnNext = new javax.swing.JButton();
        lblColService = new javax.swing.JLabel();
        lblColQty = new javax.swing.JLabel();
        lblColPrice = new javax.swing.JLabel();
        lblColCharge = new javax.swing.JLabel();
        cmbService1 = new javax.swing.JComboBox();
        cmbQty1 = new javax.swing.JComboBox();
        lblPrice1 = new javax.swing.JLabel();
        lblCharge1 = new javax.swing.JLabel();
        cmbService2 = new javax.swing.JComboBox();
        cmbQty2 = new javax.swing.JComboBox();
        lblPrice2 = new javax.swing.JLabel();
        lblCharge2 = new javax.swing.JLabel();
        cmbService3 = new javax.swing.JComboBox();
        cmbQty3 = new javax.swing.JComboBox();
        lblPrice3 = new javax.swing.JLabel();
        lblCharge3 = new javax.swing.JLabel();
        cmbService4 = new javax.swing.JComboBox();
        cmbQty4 = new javax.swing.JComboBox();
        lblPrice4 = new javax.swing.JLabel();
        lblCharge4 = new javax.swing.JLabel();
        cmbService5 = new javax.swing.JComboBox();
        cmbQty5 = new javax.swing.JComboBox();
        lblPrice5 = new javax.swing.JLabel();
        lblCharge5 = new javax.swing.JLabel();
        sepTotal = new javax.swing.JSeparator();
        lblTotalCaption = new javax.swing.JLabel();
        lblTotalValue = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Billing Management (Step 2)");
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
        lblSubtitle.setText("Clinical Charges");
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
        btnNext.setBounds(790, 88, 90, 32);

        lblColService.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblColService.setText("Select Service");
        cardPanel.add(lblColService);
        lblColService.setBounds(120, 150, 100, 22);

        lblColQty.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblColQty.setText("Quantity");
        cardPanel.add(lblColQty);
        lblColQty.setBounds(320, 150, 60, 22);

        lblColPrice.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblColPrice.setText("Per service Price");
        cardPanel.add(lblColPrice);
        lblColPrice.setBounds(470, 150, 190, 22);

        lblColCharge.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblColCharge.setText("Clinical Charges");
        cardPanel.add(lblColCharge);
        lblColCharge.setBounds(680, 150, 190, 22);

        cmbService1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbService1);
        cmbService1.setBounds(60, 185, 220, 34);

        cmbQty1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cardPanel.add(cmbQty1);
        cmbQty1.setBounds(300, 185, 120, 34);

        lblPrice1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice1);
        lblPrice1.setBounds(490, 189, 130, 26);

        lblCharge1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge1);
        lblCharge1.setBounds(700, 189, 130, 26);

        cmbService2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbService2);
        cmbService2.setBounds(60, 227, 220, 34);

        cmbQty2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cardPanel.add(cmbQty2);
        cmbQty2.setBounds(300, 227, 120, 34);

        lblPrice2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice2);
        lblPrice2.setBounds(490, 231, 130, 26);

        lblCharge2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge2);
        lblCharge2.setBounds(700, 231, 130, 26);

        cmbService3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbService3);
        cmbService3.setBounds(60, 269, 220, 34);

        cmbQty3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cardPanel.add(cmbQty3);
        cmbQty3.setBounds(300, 269, 120, 34);

        lblPrice3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice3);
        lblPrice3.setBounds(490, 273, 130, 26);

        lblCharge3.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge3);
        lblCharge3.setBounds(700, 273, 130, 26);

        cmbService4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbService4);
        cmbService4.setBounds(60, 311, 220, 34);

        cmbQty4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cardPanel.add(cmbQty4);
        cmbQty4.setBounds(300, 311, 120, 34);

        lblPrice4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice4);
        lblPrice4.setBounds(490, 315, 130, 26);

        lblCharge4.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge4);
        lblCharge4.setBounds(700, 315, 130, 26);

        cmbService5.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbService5);
        cmbService5.setBounds(60, 353, 220, 34);

        cmbQty5.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cardPanel.add(cmbQty5);
        cmbQty5.setBounds(300, 353, 120, 34);

        lblPrice5.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice5);
        lblPrice5.setBounds(490, 357, 130, 26);

        lblCharge5.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge5);
        lblCharge5.setBounds(700, 357, 130, 26);

        sepTotal.setForeground(new java.awt.Color(0, 0, 0));
        cardPanel.add(sepTotal);
        sepTotal.setBounds(60, 398, 770, 2);

        lblTotalCaption.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalCaption.setForeground(new java.awt.Color(220, 53, 69));
        lblTotalCaption.setText("Total Clinical Charges");
        cardPanel.add(lblTotalCaption);
        lblTotalCaption.setBounds(430, 412, 190, 25);

        lblTotalValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalValue.setForeground(new java.awt.Color(220, 53, 69));
        lblTotalValue.setText("Rs 0");
        cardPanel.add(lblTotalValue);
        lblTotalValue.setBounds(700, 412, 130, 25);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        controller.goBackFromStep2(this);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        controller.goNextFromStep2(collectLines(), computeTotal(), this);
    }//GEN-LAST:event_btnNextActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_BM_2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox cmbQty1;
    private javax.swing.JComboBox cmbQty2;
    private javax.swing.JComboBox cmbQty3;
    private javax.swing.JComboBox cmbQty4;
    private javax.swing.JComboBox cmbQty5;
    private javax.swing.JComboBox cmbService1;
    private javax.swing.JComboBox cmbService2;
    private javax.swing.JComboBox cmbService3;
    private javax.swing.JComboBox cmbService4;
    private javax.swing.JComboBox cmbService5;
    private javax.swing.JLabel lblCharge1;
    private javax.swing.JLabel lblCharge2;
    private javax.swing.JLabel lblCharge3;
    private javax.swing.JLabel lblCharge4;
    private javax.swing.JLabel lblCharge5;
    private javax.swing.JLabel lblColCharge;
    private javax.swing.JLabel lblColPrice;
    private javax.swing.JLabel lblColQty;
    private javax.swing.JLabel lblColService;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblPrice1;
    private javax.swing.JLabel lblPrice2;
    private javax.swing.JLabel lblPrice3;
    private javax.swing.JLabel lblPrice4;
    private javax.swing.JLabel lblPrice5;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalCaption;
    private javax.swing.JLabel lblTotalValue;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JSeparator sepTotal;
    // End of variables declaration//GEN-END:variables
}
