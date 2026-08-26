package view;

import controller.AppController;
import controller.PatientProfileController;

/**
 * Patient &gt; My Profile — Step 4: Dental Information (last step, so no
 * "Next" — just Back/Update). Oral Hygiene is a Good/Fair/Poor exclusive
 * {@link PillToggle} group backed by {@link model.PatientModel#getOralHygiene()},
 * same as OS_PM_4 uses for the same rating.
 *
 * @author oveen
 */
public class PA_PR_4 extends javax.swing.JFrame {

    private final PatientProfileController controller;

    public PA_PR_4() {
        this(new PatientProfileController(null));
    }

    public PA_PR_4(PatientProfileController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40));
        IconFactory.roundCorners(navBar, 30);
        populateFieldsFromModel();
        PillToggle.attachExclusiveGroup(
                new javax.swing.JCheckBox[]{chkGood, chkFair, chkPoor},
                new String[]{"Good", "Fair", "Poor"});
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void populateFieldsFromModel() {
        model.PatientModel m = controller.getPatientModel();
        if (m == null) return;
        if (m.getLastDentalVisit() != null) txtLastDentalVisit.setText(m.getLastDentalVisit());
        if (m.getDentalHistory() != null) txtDentalHistory.setText(m.getDentalHistory());
        if (m.getDentalProblems() != null) txtDentalProblems.setText(m.getDentalProblems());
        String hygiene = m.getOralHygiene();
        chkGood.setSelected("Good".equals(hygiene));
        chkFair.setSelected("Fair".equals(hygiene));
        chkPoor.setSelected("Poor".equals(hygiene));
        if (m.getDentalMedicalNotes() != null) txtDentalMedicalNotes.setText(m.getDentalMedicalNotes());
    }

    private String selectedOralHygiene() {
        return chkGood.isSelected() ? "Good" : chkFair.isSelected() ? "Fair" : chkPoor.isSelected() ? "Poor" : "";
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
        lblLastDentalVisit = new javax.swing.JLabel();
        txtLastDentalVisit = new javax.swing.JTextField();
        lblDentalHistory = new javax.swing.JLabel();
        txtDentalHistory = new javax.swing.JTextField();
        lblDentalProblems = new javax.swing.JLabel();
        txtDentalProblems = new javax.swing.JTextField();
        lblOralHygiene = new javax.swing.JLabel();
        lblDentalMedicalNotes = new javax.swing.JLabel();
        txtDentalMedicalNotes = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        chkGood = new javax.swing.JCheckBox();
        chkFair = new javax.swing.JCheckBox();
        chkPoor = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental — My Profile (Step 4)");
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
        lblSubtitle.setText("Dental Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblLastDentalVisit.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLastDentalVisit.setText("Last Dental Visit");
        cardPanel.add(lblLastDentalVisit);
        lblLastDentalVisit.setBounds(60, 135, 150, 20);

        txtLastDentalVisit.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtLastDentalVisit);
        txtLastDentalVisit.setBounds(60, 160, 350, 35);

        lblDentalHistory.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDentalHistory.setText("Dental History");
        cardPanel.add(lblDentalHistory);
        lblDentalHistory.setBounds(60, 205, 150, 20);

        txtDentalHistory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDentalHistory);
        txtDentalHistory.setBounds(60, 230, 350, 35);

        lblDentalProblems.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDentalProblems.setText("Dental Problems");
        cardPanel.add(lblDentalProblems);
        lblDentalProblems.setBounds(60, 275, 150, 20);

        txtDentalProblems.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDentalProblems);
        txtDentalProblems.setBounds(60, 300, 350, 35);

        lblOralHygiene.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblOralHygiene.setText("Oral Hygiene");
        cardPanel.add(lblOralHygiene);
        lblOralHygiene.setBounds(490, 140, 150, 20);

        lblDentalMedicalNotes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDentalMedicalNotes.setText("Medical Notes");
        cardPanel.add(lblDentalMedicalNotes);
        lblDentalMedicalNotes.setBounds(490, 180, 120, 20);

        txtDentalMedicalNotes.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDentalMedicalNotes);
        txtDentalMedicalNotes.setBounds(490, 205, 350, 130);

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
        btnBack.setBounds(650, 420, 100, 36);

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
        btnUpdate.setBounds(760, 420, 100, 36);

        chkGood.setText("Good");
        cardPanel.add(chkGood);
        chkGood.setBounds(630, 140, 80, 25);

        chkFair.setText("Fair");
        cardPanel.add(chkFair);
        chkFair.setBounds(710, 140, 80, 25);

        chkPoor.setText("Poor");
        cardPanel.add(chkPoor);
        chkPoor.setBounds(790, 140, 80, 25);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> new PA_PR_3(controller).setVisible(true));
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        boolean ok = controller.saveStep4(txtLastDentalVisit.getText(), txtDentalHistory.getText(),
                txtDentalProblems.getText(), selectedOralHygiene(), txtDentalMedicalNotes.getText());
        if (ok) {
            // Last step of the wizard — once it's saved, closing this popup
            // returns to the dashboard (which re-reads the freshly-saved
            // record for its badges/Medical Notes panel) instead of leaving
            // the user stranded on a finished form with nowhere to go.
            IconFactory.showSuccessDialog(this, "Profile Updated Successfully!", () -> {
                dispose();
                new Patient_Dashboard(AppController.getCurrentUser()).setVisible(true);
            });
        } else {
            IconFactory.showErrorDialog(this, "Couldn't save this profile section — the database may be unreachable.", null);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new PA_PR_4().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox chkFair;
    private javax.swing.JCheckBox chkGood;
    private javax.swing.JCheckBox chkPoor;
    private javax.swing.JLabel lblDentalHistory;
    private javax.swing.JLabel lblDentalMedicalNotes;
    private javax.swing.JLabel lblDentalProblems;
    private javax.swing.JLabel lblLastDentalVisit;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblOralHygiene;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblStep3;
    private javax.swing.JLabel lblStep4;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtDentalHistory;
    private javax.swing.JTextField txtDentalMedicalNotes;
    private javax.swing.JTextField txtDentalProblems;
    private javax.swing.JTextField txtLastDentalVisit;
    // End of variables declaration//GEN-END:variables
}
