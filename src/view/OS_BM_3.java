package view;

import controller.BillingManagementController;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Step 3: Medicine Charges Form
 * View only — logic delegated to BillingManagementController.
 *
 * Medicines here are real Inventory stock (product_type "Medication"), not
 * a separate price list — each row's Quantity dropdown is capped to
 * whatever's actually on hand for the selected medicine, and confirming
 * this step deducts that stock for real once the bill is saved.
 *
 * @author oveen
 */
public class OS_BM_3 extends javax.swing.JFrame {

    private static final int MAX_QTY_PER_ROW = 20;

    private final BillingManagementController controller;
    private javax.swing.JComboBox<String>[] cmbMedicine;
    private javax.swing.JComboBox<String>[] cmbQty;
    private javax.swing.JLabel[] lblPrice;
    private javax.swing.JLabel[] lblCharge;

    public OS_BM_3() {
        this(new BillingManagementController());
    }

    @SuppressWarnings("unchecked")
    public OS_BM_3(BillingManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height

        cmbMedicine = new javax.swing.JComboBox[]{cmbMedicine1, cmbMedicine2, cmbMedicine3, cmbMedicine4, cmbMedicine5};
        cmbQty = new javax.swing.JComboBox[]{cmbQty1, cmbQty2, cmbQty3, cmbQty4, cmbQty5};
        lblPrice = new javax.swing.JLabel[]{lblPrice1, lblPrice2, lblPrice3, lblPrice4, lblPrice5};
        lblCharge = new javax.swing.JLabel[]{lblCharge1, lblCharge2, lblCharge3, lblCharge4, lblCharge5};

        for (int i = 0; i < 5; i++) {
            cmbMedicine[i].setModel(new javax.swing.DefaultComboBoxModel<>(controller.getMedicineNames()));
            final int row = i;
            cmbMedicine[i].addActionListener(evt -> {
                refreshQtyOptions(row);
                recalcRow(row);
            });
            cmbQty[i].addActionListener(evt -> recalcRow(row));
            refreshQtyOptions(i);
        }

        // Pre-fill row 1 with a sample line item, matching the mockup —
        // only if there's actually stock for it.
        if (controller.getMedicineStock("Pain Killers") > 0) {
            cmbMedicine[0].setSelectedItem("Pain Killers");
            refreshQtyOptions(0);
            cmbQty[0].setSelectedItem(String.valueOf(Math.min(12, controller.getMedicineStock("Pain Killers"))));
        }
        recalcTotal();

        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    /**
     * Rebuilds row {@code i}'s Quantity dropdown to cap out at whatever
     * stock is actually on hand for the currently-selected medicine (never
     * more than {@link #MAX_QTY_PER_ROW} at once). Keeps the previously
     * chosen quantity selected if it's still valid under the new cap.
     */
    private void refreshQtyOptions(int i) {
        String medicine = (String) cmbMedicine[i].getSelectedItem();
        String previous = (String) cmbQty[i].getSelectedItem();
        int stock = (medicine != null && !medicine.isEmpty()) ? controller.getMedicineStock(medicine) : 0;
        int cap = Math.min(stock, MAX_QTY_PER_ROW);

        java.util.List<String> options = new ArrayList<>();
        for (int q = 1; q <= cap; q++) {
            options.add(String.valueOf(q));
        }
        if (options.isEmpty()) {
            options.add("0"); // no stock — nothing sellable at this row
        }
        cmbQty[i].setModel(new javax.swing.DefaultComboBoxModel<>(options.toArray(new String[0])));
        cmbQty[i].setSelectedItem(options.contains(previous) ? previous : options.get(0));
    }

    private void recalcRow(int i) {
        String medicine = (String) cmbMedicine[i].getSelectedItem();
        double price = controller.getMedicinePrice(medicine);
        int qty = parseInt((String) cmbQty[i].getSelectedItem());
        boolean active = medicine != null && !medicine.isEmpty();
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
            String medicine = (String) cmbMedicine[i].getSelectedItem();
            if (medicine == null || medicine.isEmpty()) continue;
            double price = controller.getMedicinePrice(medicine);
            int qty = parseInt((String) cmbQty[i].getSelectedItem());
            total += price * qty;
        }
        return total;
    }

    private List<String> collectLines() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String medicine = (String) cmbMedicine[i].getSelectedItem();
            int qty = parseInt((String) cmbQty[i].getSelectedItem());
            if (medicine == null || medicine.isEmpty() || qty <= 0) continue;
            double price = controller.getMedicinePrice(medicine);
            lines.add(medicine + " x" + qty + " = " + BillingManagementController.formatCurrency(price * qty));
        }
        return lines;
    }

    /** productId -> total quantity billed, aggregated across rows (same medicine picked twice still adds up). */
    private Map<String, Integer> collectDeductions() {
        Map<String, Integer> deductions = new LinkedHashMap<>();
        for (int i = 0; i < 5; i++) {
            String medicine = (String) cmbMedicine[i].getSelectedItem();
            int qty = parseInt((String) cmbQty[i].getSelectedItem());
            if (medicine == null || medicine.isEmpty() || qty <= 0) continue;
            String productId = controller.getMedicineProductId(medicine);
            if (productId == null) continue;
            deductions.merge(productId, qty, Integer::sum);
        }
        return deductions;
    }

    /**
     * Defense-in-depth: refreshQtyOptions() already caps each row to that
     * row's own stock, but the same medicine can be picked on two different
     * rows — this catches their combined total exceeding real stock before
     * letting the bill proceed.
     */
    private boolean hasEnoughStockForAllLines() {
        for (Map.Entry<String, Integer> line : collectDeductions().entrySet()) {
            int available = 0;
            for (int i = 0; i < 5; i++) {
                String medicine = (String) cmbMedicine[i].getSelectedItem();
                if (medicine != null && line.getKey().equals(controller.getMedicineProductId(medicine))) {
                    available = controller.getMedicineStock(medicine);
                    break;
                }
            }
            if (line.getValue() > available) {
                return false;
            }
        }
        return true;
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
        lblColMedicine = new javax.swing.JLabel();
        lblColQty = new javax.swing.JLabel();
        lblColPrice = new javax.swing.JLabel();
        lblColCharge = new javax.swing.JLabel();
        cmbMedicine1 = new javax.swing.JComboBox<String>();
        cmbQty1 = new javax.swing.JComboBox<String>();
        lblPrice1 = new javax.swing.JLabel();
        lblCharge1 = new javax.swing.JLabel();
        cmbMedicine2 = new javax.swing.JComboBox<String>();
        cmbQty2 = new javax.swing.JComboBox<String>();
        lblPrice2 = new javax.swing.JLabel();
        lblCharge2 = new javax.swing.JLabel();
        cmbMedicine3 = new javax.swing.JComboBox<String>();
        cmbQty3 = new javax.swing.JComboBox<String>();
        lblPrice3 = new javax.swing.JLabel();
        lblCharge3 = new javax.swing.JLabel();
        cmbMedicine4 = new javax.swing.JComboBox<String>();
        cmbQty4 = new javax.swing.JComboBox<String>();
        lblPrice4 = new javax.swing.JLabel();
        lblCharge4 = new javax.swing.JLabel();
        cmbMedicine5 = new javax.swing.JComboBox<String>();
        cmbQty5 = new javax.swing.JComboBox<String>();
        lblPrice5 = new javax.swing.JLabel();
        lblCharge5 = new javax.swing.JLabel();
        sepTotal = new javax.swing.JSeparator();
        lblTotalCaption = new javax.swing.JLabel();
        lblTotalValue = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Billing Management (Step 3)");
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
        lblSubtitle.setText("Medicine Charges");
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

        lblColMedicine.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblColMedicine.setText("Select Medicine");
        cardPanel.add(lblColMedicine);
        lblColMedicine.setBounds(100, 150, 220, 22);

        lblColQty.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblColQty.setText("Quantity");
        cardPanel.add(lblColQty);
        lblColQty.setBounds(320, 150, 110, 22);

        lblColPrice.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblColPrice.setText("Per Unit Price");
        cardPanel.add(lblColPrice);
        lblColPrice.setBounds(480, 150, 190, 22);

        lblColCharge.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblColCharge.setText("Medicine Charges");
        cardPanel.add(lblColCharge);
        lblColCharge.setBounds(680, 150, 190, 22);

        cmbMedicine1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbMedicine1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMedicine1ActionPerformed(evt);
            }
        });
        cardPanel.add(cmbMedicine1);
        cmbMedicine1.setBounds(60, 180, 220, 40);

        cmbQty1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty1.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cardPanel.add(cmbQty1);
        cmbQty1.setBounds(300, 180, 120, 40);

        lblPrice1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice1);
        lblPrice1.setBounds(500, 189, 120, 26);

        lblCharge1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge1);
        lblCharge1.setBounds(720, 189, 110, 26);

        cmbMedicine2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbMedicine2);
        cmbMedicine2.setBounds(60, 220, 220, 40);

        cmbQty2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty2.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cmbQty2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbQty2ActionPerformed(evt);
            }
        });
        cardPanel.add(cmbQty2);
        cmbQty2.setBounds(300, 220, 120, 40);

        lblPrice2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice2);
        lblPrice2.setBounds(500, 230, 120, 26);

        lblCharge2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge2);
        lblCharge2.setBounds(720, 230, 110, 26);

        cmbMedicine3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbMedicine3);
        cmbMedicine3.setBounds(60, 260, 220, 40);

        cmbQty3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty3.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cmbQty3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbQty3ActionPerformed(evt);
            }
        });
        cardPanel.add(cmbQty3);
        cmbQty3.setBounds(300, 260, 120, 40);

        lblPrice3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice3);
        lblPrice3.setBounds(500, 270, 120, 26);

        lblCharge3.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge3);
        lblCharge3.setBounds(720, 270, 110, 26);

        cmbMedicine4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbMedicine4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMedicine4ActionPerformed(evt);
            }
        });
        cardPanel.add(cmbMedicine4);
        cmbMedicine4.setBounds(60, 300, 220, 40);

        cmbQty4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty4.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cardPanel.add(cmbQty4);
        cmbQty4.setBounds(300, 300, 120, 40);

        lblPrice4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice4);
        lblPrice4.setBounds(500, 310, 120, 26);

        lblCharge4.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge4);
        lblCharge4.setBounds(720, 310, 110, 26);

        cmbMedicine5.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbMedicine5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMedicine5ActionPerformed(evt);
            }
        });
        cardPanel.add(cmbMedicine5);
        cmbMedicine5.setBounds(60, 340, 220, 40);

        cmbQty5.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbQty5.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));
        cmbQty5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbQty5ActionPerformed(evt);
            }
        });
        cardPanel.add(cmbQty5);
        cmbQty5.setBounds(300, 340, 120, 40);

        lblPrice5.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(lblPrice5);
        lblPrice5.setBounds(500, 350, 120, 26);

        lblCharge5.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cardPanel.add(lblCharge5);
        lblCharge5.setBounds(720, 350, 110, 30);

        sepTotal.setForeground(new java.awt.Color(0, 0, 0));
        cardPanel.add(sepTotal);
        sepTotal.setBounds(60, 398, 770, 2);

        lblTotalCaption.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalCaption.setForeground(new java.awt.Color(220, 53, 69));
        lblTotalCaption.setText("Total Medicine Charges");
        cardPanel.add(lblTotalCaption);
        lblTotalCaption.setBounds(430, 412, 190, 25);

        lblTotalValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalValue.setForeground(new java.awt.Color(220, 53, 69));
        lblTotalValue.setText("Rs 0");
        cardPanel.add(lblTotalValue);
        lblTotalValue.setBounds(710, 412, 120, 25);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        controller.goBackFromStep3(this);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if (!hasEnoughStockForAllLines()) {
            JOptionPane.showMessageDialog(this,
                    "One of the selected medicines doesn't have enough stock left — "
                    + "please reduce the quantity or pick a different medicine.",
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.goNextFromStep3(collectLines(), computeTotal(), collectDeductions(), this);
    }//GEN-LAST:event_btnNextActionPerformed

    private void cmbQty3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbQty3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbQty3ActionPerformed

    private void cmbMedicine1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMedicine1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbMedicine1ActionPerformed

    private void cmbMedicine4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMedicine4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbMedicine4ActionPerformed

    private void cmbQty2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbQty2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbQty2ActionPerformed

    private void cmbQty5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbQty5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbQty5ActionPerformed

    private void cmbMedicine5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMedicine5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbMedicine5ActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_BM_3().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox<String> cmbMedicine1;
    private javax.swing.JComboBox<String> cmbMedicine2;
    private javax.swing.JComboBox<String> cmbMedicine3;
    private javax.swing.JComboBox<String> cmbMedicine4;
    private javax.swing.JComboBox<String> cmbMedicine5;
    private javax.swing.JComboBox<String> cmbQty1;
    private javax.swing.JComboBox<String> cmbQty2;
    private javax.swing.JComboBox<String> cmbQty3;
    private javax.swing.JComboBox<String> cmbQty4;
    private javax.swing.JComboBox<String> cmbQty5;
    private javax.swing.JLabel lblCharge1;
    private javax.swing.JLabel lblCharge2;
    private javax.swing.JLabel lblCharge3;
    private javax.swing.JLabel lblCharge4;
    private javax.swing.JLabel lblCharge5;
    private javax.swing.JLabel lblColCharge;
    private javax.swing.JLabel lblColMedicine;
    private javax.swing.JLabel lblColPrice;
    private javax.swing.JLabel lblColQty;
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
