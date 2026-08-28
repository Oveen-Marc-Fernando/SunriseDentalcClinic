package view;

import controller.BillingManagementController;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Add Services — the master service price-list editor for Billing
 * Management. Opened from OS_BM_Grid's "Add Services" button.
 *
 * Each row's price is read from / written back to
 * {@link BillingManagementController}'s shared service catalog, so changes
 * made here are immediately reflected in OS_BM_2's ("Clinical Charges")
 * service dropdown.
 *
 * @author oveen
 */
public class OS_BM_Service extends javax.swing.JFrame {

    private final BillingManagementController controller;
    private List<ServiceRow> rows;

    private static final class ServiceRow {
        final String name;
        final JCheckBox checkbox;
        final JTextField priceField;
        ServiceRow(String name, JCheckBox checkbox, JTextField priceField) {
            this.name = name;
            this.checkbox = checkbox;
            this.priceField = priceField;
        }
    }

    public OS_BM_Service() {
        this(new BillingManagementController());
    }

    public OS_BM_Service(BillingManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height

        rows = Arrays.asList(
            new ServiceRow("Appointment charges", chkAppointmentCharges, txtAppointmentCharges),
            new ServiceRow("Aligners", chkAligners, txtAligners),
            new ServiceRow("Teeth Whitening", chkTeethWhitening, txtTeethWhitening),
            new ServiceRow("Cleaning & Polishing", chkCleaningPolishing, txtCleaningPolishing),
            new ServiceRow("Dental Implants", chkDentalImplants, txtDentalImplants),
            new ServiceRow("Smile Design & Cosmetics", chkSmileDesignCosmetics, txtSmileDesignCosmetics),
            new ServiceRow("Root Dental Treatment", chkRootDentalTreatment, txtRootDentalTreatment),
            new ServiceRow("Tooth Colored Fillings", chkToothColoredFillings, txtToothColoredFillings),
            new ServiceRow("Dental Bridges", chkDentalBridges, txtDentalBridges),
            new ServiceRow("Dental Crowns", chkDentalCrowns, txtDentalCrowns),
            new ServiceRow("Dentures", chkDentures, txtDentures)
        );

        populatePricesFromCatalog();

        // Price fields start locked (read-only, greyed out) and only unlock
        // once you tick that row's checkbox — ticking is how you pick which
        // service(s) you're about to Update or Delete.
        for (ServiceRow row : rows) {
            row.checkbox.addActionListener(evt -> applyRowLockState(row));
            applyRowLockState(row);
        }

        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void populatePricesFromCatalog() {
        for (ServiceRow row : rows) {
            row.priceField.setText(formatPrice(controller.getServicePrice(row.name)));
        }
    }

    private void applyRowLockState(ServiceRow row) {
        boolean unlocked = row.checkbox.isSelected();
        row.priceField.setEditable(unlocked);
        row.priceField.setBackground(unlocked ? Color.WHITE : new Color(235, 235, 235));
    }

    private List<ServiceRow> checkedRows() {
        List<ServiceRow> checked = new ArrayList<>();
        for (ServiceRow row : rows) {
            if (row.checkbox.isSelected()) checked.add(row);
        }
        return checked;
    }

    private static String formatPrice(double v) {
        return BillingManagementController.formatCurrency(v) + "/=";
    }

    private static double parsePrice(String text) {
        if (text == null) return 0;
        try {
            return Double.parseDouble(text.replace("Rs", "").replace(",", "").replace("/=", "").trim());
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
        btnBack = new javax.swing.JButton();
        cardPanel = new javax.swing.JPanel();
        lblSubtitle = new javax.swing.JLabel();
        chkAppointmentCharges = new javax.swing.JCheckBox();
        lblAppointmentCharges = new javax.swing.JLabel();
        txtAppointmentCharges = new javax.swing.JTextField();
        chkAligners = new javax.swing.JCheckBox();
        lblAligners = new javax.swing.JLabel();
        txtAligners = new javax.swing.JTextField();
        chkTeethWhitening = new javax.swing.JCheckBox();
        lblTeethWhitening = new javax.swing.JLabel();
        txtTeethWhitening = new javax.swing.JTextField();
        chkCleaningPolishing = new javax.swing.JCheckBox();
        lblCleaningPolishing = new javax.swing.JLabel();
        txtCleaningPolishing = new javax.swing.JTextField();
        chkDentalImplants = new javax.swing.JCheckBox();
        lblDentalImplants = new javax.swing.JLabel();
        txtDentalImplants = new javax.swing.JTextField();
        chkSmileDesignCosmetics = new javax.swing.JCheckBox();
        lblSmileDesignCosmetics = new javax.swing.JLabel();
        txtSmileDesignCosmetics = new javax.swing.JTextField();
        chkRootDentalTreatment = new javax.swing.JCheckBox();
        lblRootDentalTreatment = new javax.swing.JLabel();
        txtRootDentalTreatment = new javax.swing.JTextField();
        chkToothColoredFillings = new javax.swing.JCheckBox();
        lblToothColoredFillings = new javax.swing.JLabel();
        txtToothColoredFillings = new javax.swing.JTextField();
        chkDentalBridges = new javax.swing.JCheckBox();
        lblDentalBridges = new javax.swing.JLabel();
        txtDentalBridges = new javax.swing.JTextField();
        chkDentalCrowns = new javax.swing.JCheckBox();
        lblDentalCrowns = new javax.swing.JLabel();
        txtDentalCrowns = new javax.swing.JTextField();
        chkDentures = new javax.swing.JCheckBox();
        lblDentures = new javax.swing.JLabel();
        txtDentures = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Billing Management (Add Services)");
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
        lblTitle.setBounds(50, 120, 400, 40);

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
        mainPanel.add(btnBack);
        btnBack.setBounds(50, 175, 100, 36);

        cardPanel.setBackground(new java.awt.Color(248, 249, 250));
        cardPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.setLayout(null);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Add Services");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(40, 20, 300, 30);
        cardPanel.add(chkAppointmentCharges);
        chkAppointmentCharges.setBounds(40, 76, 18, 18);

        lblAppointmentCharges.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblAppointmentCharges.setText("Appointment charges:");
        cardPanel.add(lblAppointmentCharges);
        lblAppointmentCharges.setBounds(68, 70, 170, 25);

        txtAppointmentCharges.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtAppointmentCharges.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtAppointmentCharges.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtAppointmentCharges);
        txtAppointmentCharges.setBounds(245, 66, 150, 32);
        cardPanel.add(chkAligners);
        chkAligners.setBounds(40, 118, 18, 18);

        lblAligners.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblAligners.setText("Aligners:");
        cardPanel.add(lblAligners);
        lblAligners.setBounds(68, 112, 170, 25);

        txtAligners.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtAligners.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtAligners.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtAligners);
        txtAligners.setBounds(245, 108, 150, 32);
        cardPanel.add(chkTeethWhitening);
        chkTeethWhitening.setBounds(40, 160, 18, 18);

        lblTeethWhitening.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTeethWhitening.setText("Teeth Whitening:");
        cardPanel.add(lblTeethWhitening);
        lblTeethWhitening.setBounds(68, 154, 170, 25);

        txtTeethWhitening.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtTeethWhitening.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTeethWhitening.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtTeethWhitening);
        txtTeethWhitening.setBounds(245, 150, 150, 32);
        cardPanel.add(chkCleaningPolishing);
        chkCleaningPolishing.setBounds(40, 202, 18, 18);

        lblCleaningPolishing.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblCleaningPolishing.setText("Cleaning & Polishing:");
        cardPanel.add(lblCleaningPolishing);
        lblCleaningPolishing.setBounds(68, 196, 170, 25);

        txtCleaningPolishing.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtCleaningPolishing.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCleaningPolishing.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtCleaningPolishing);
        txtCleaningPolishing.setBounds(245, 192, 150, 32);
        cardPanel.add(chkDentalImplants);
        chkDentalImplants.setBounds(40, 244, 18, 18);

        lblDentalImplants.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDentalImplants.setText("Dental Implants:");
        cardPanel.add(lblDentalImplants);
        lblDentalImplants.setBounds(68, 238, 170, 25);

        txtDentalImplants.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtDentalImplants.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDentalImplants.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtDentalImplants);
        txtDentalImplants.setBounds(245, 234, 150, 32);

        chkSmileDesignCosmetics.setSelected(true);
        cardPanel.add(chkSmileDesignCosmetics);
        chkSmileDesignCosmetics.setBounds(40, 286, 18, 18);

        lblSmileDesignCosmetics.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSmileDesignCosmetics.setText("Smile Design & Cosmetics:");
        cardPanel.add(lblSmileDesignCosmetics);
        lblSmileDesignCosmetics.setBounds(68, 280, 170, 25);

        txtSmileDesignCosmetics.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtSmileDesignCosmetics.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSmileDesignCosmetics.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtSmileDesignCosmetics);
        txtSmileDesignCosmetics.setBounds(245, 276, 150, 32);
        cardPanel.add(chkRootDentalTreatment);
        chkRootDentalTreatment.setBounds(430, 76, 18, 18);

        lblRootDentalTreatment.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblRootDentalTreatment.setText("Root Dental Treatment:");
        cardPanel.add(lblRootDentalTreatment);
        lblRootDentalTreatment.setBounds(458, 70, 170, 25);

        txtRootDentalTreatment.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtRootDentalTreatment.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRootDentalTreatment.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtRootDentalTreatment);
        txtRootDentalTreatment.setBounds(655, 66, 150, 32);
        cardPanel.add(chkToothColoredFillings);
        chkToothColoredFillings.setBounds(430, 118, 18, 18);

        lblToothColoredFillings.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblToothColoredFillings.setText("Tooth Colored Fillings:");
        cardPanel.add(lblToothColoredFillings);
        lblToothColoredFillings.setBounds(458, 112, 170, 25);

        txtToothColoredFillings.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtToothColoredFillings.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtToothColoredFillings.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtToothColoredFillings);
        txtToothColoredFillings.setBounds(655, 108, 150, 32);
        cardPanel.add(chkDentalBridges);
        chkDentalBridges.setBounds(430, 160, 18, 18);

        lblDentalBridges.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDentalBridges.setText("Dental Bridges:");
        cardPanel.add(lblDentalBridges);
        lblDentalBridges.setBounds(458, 154, 170, 25);

        txtDentalBridges.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtDentalBridges.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDentalBridges.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtDentalBridges);
        txtDentalBridges.setBounds(655, 150, 150, 32);
        cardPanel.add(chkDentalCrowns);
        chkDentalCrowns.setBounds(430, 202, 18, 18);

        lblDentalCrowns.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDentalCrowns.setText("Dental Crowns:");
        cardPanel.add(lblDentalCrowns);
        lblDentalCrowns.setBounds(458, 196, 170, 25);

        txtDentalCrowns.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtDentalCrowns.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDentalCrowns.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtDentalCrowns);
        txtDentalCrowns.setBounds(655, 192, 150, 32);
        cardPanel.add(chkDentures);
        chkDentures.setBounds(430, 244, 18, 18);

        lblDentures.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDentures.setText("Dentures:");
        cardPanel.add(lblDentures);
        lblDentures.setBounds(458, 238, 170, 25);

        txtDentures.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtDentures.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDentures.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.add(txtDentures);
        txtDentures.setBounds(655, 234, 150, 32);

        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setBackground(new java.awt.Color(0, 200, 83));
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setBorderPainted(false);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        cardPanel.add(btnAdd);
        btnAdd.setBounds(560, 350, 90, 36);

        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setBackground(new java.awt.Color(255, 193, 7));
        btnUpdate.setBorderPainted(false);
        btnUpdate.setFocusPainted(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        cardPanel.add(btnUpdate);
        btnUpdate.setBounds(660, 350, 90, 36);

        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setBackground(new java.awt.Color(220, 53, 69));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setBorderPainted(false);
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        cardPanel.add(btnDelete);
        btnDelete.setBounds(760, 350, 90, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 225, 900, 420);

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

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        List<ServiceRow> checked = checkedRows();
        if (checked.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one service first.", "No Service Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder names = new StringBuilder();
        for (ServiceRow row : checked) {
            double price = parsePrice(row.priceField.getText());
            controller.setServicePrice(row.name, price);
            row.priceField.setText(formatPrice(price));
            if (names.length() > 0) names.append(", ");
            names.append(row.name);
        }
        JOptionPane.showMessageDialog(this, "Added to price list: " + names, "Services Added",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        List<ServiceRow> checked = checkedRows();
        if (checked.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one service first.", "No Service Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder names = new StringBuilder();
        for (ServiceRow row : checked) {
            double price = parsePrice(row.priceField.getText());
            controller.setServicePrice(row.name, price);
            row.priceField.setText(formatPrice(price));
            if (names.length() > 0) names.append(", ");
            names.append(row.name);
        }
        JOptionPane.showMessageDialog(this, "Updated price for: " + names, "Prices Updated",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        List<ServiceRow> checked = checkedRows();
        if (checked.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one service first.", "No Service Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder names = new StringBuilder();
        for (ServiceRow row : checked) {
            if (names.length() > 0) names.append(", ");
            names.append(row.name);
        }
        IconFactory.showConfirmDialog(this, "Remove " + names + " from the price list?", "Delete", () -> {
            for (ServiceRow row : checked) {
                controller.removeService(row.name);
                row.priceField.setText(formatPrice(0));
                row.checkbox.setSelected(false);
            }
            JOptionPane.showMessageDialog(this, "Removed from price list: " + names, "Services Removed",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }//GEN-LAST:event_btnDeleteActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_BM_Service().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox chkAligners;
    private javax.swing.JCheckBox chkAppointmentCharges;
    private javax.swing.JCheckBox chkCleaningPolishing;
    private javax.swing.JCheckBox chkDentalBridges;
    private javax.swing.JCheckBox chkDentalCrowns;
    private javax.swing.JCheckBox chkDentalImplants;
    private javax.swing.JCheckBox chkDentures;
    private javax.swing.JCheckBox chkRootDentalTreatment;
    private javax.swing.JCheckBox chkSmileDesignCosmetics;
    private javax.swing.JCheckBox chkTeethWhitening;
    private javax.swing.JCheckBox chkToothColoredFillings;
    private javax.swing.JLabel lblAligners;
    private javax.swing.JLabel lblAppointmentCharges;
    private javax.swing.JLabel lblCleaningPolishing;
    private javax.swing.JLabel lblDentalBridges;
    private javax.swing.JLabel lblDentalCrowns;
    private javax.swing.JLabel lblDentalImplants;
    private javax.swing.JLabel lblDentures;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblRootDentalTreatment;
    private javax.swing.JLabel lblSmileDesignCosmetics;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTeethWhitening;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblToothColoredFillings;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JTextField txtAligners;
    private javax.swing.JTextField txtAppointmentCharges;
    private javax.swing.JTextField txtCleaningPolishing;
    private javax.swing.JTextField txtDentalBridges;
    private javax.swing.JTextField txtDentalCrowns;
    private javax.swing.JTextField txtDentalImplants;
    private javax.swing.JTextField txtDentures;
    private javax.swing.JTextField txtRootDentalTreatment;
    private javax.swing.JTextField txtSmileDesignCosmetics;
    private javax.swing.JTextField txtTeethWhitening;
    private javax.swing.JTextField txtToothColoredFillings;
    // End of variables declaration//GEN-END:variables
}

