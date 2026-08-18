package view;

import controller.PatientManagementController;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Step 4: Dental Information Form & Final Submission Dialog.
 * View only — logic delegated to PatientManagementController.
 *
 * @author oveen
 */
public class OS_PM_4 extends javax.swing.JFrame {

    private final PatientManagementController controller;

    public OS_PM_4() {
        this(new PatientManagementController());
    }

    public OS_PM_4(PatientManagementController controller) {
        this.controller = controller;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        populateFieldsFromModel();
        PillToggle.attachExclusiveGroup(
                new javax.swing.JCheckBox[]{chkSmoker, chkAlcohol, chkOther},
                new String[]{"Good", "Fair", "Poor"});
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void populateFieldsFromModel() {
        if (controller.getPatientModel() == null) return;
        model.PatientModel m = controller.getPatientModel();
        if (m.getLastDentalVisit() != null) txtLastDentalVisit.setText(m.getLastDentalVisit());
        if (m.getDentalHistory() != null) txtDentalHistory.setText(m.getDentalHistory());
        if (m.getDentalProblems() != null) txtDentalProblems.setText(m.getDentalProblems());
        chkSmoker.setSelected(m.isSmoker());
        chkAlcohol.setSelected(m.isAlcohol());
        chkOther.setSelected(m.isOtherHabits());
        if (m.getDentalMedicalNotes() != null) txtDentalMedicalNotes.setText(m.getDentalMedicalNotes());
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

        // Success Text
        JLabel lblMessage = new JLabel("Patient Added Successfully!");
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMessage.setForeground(new Color(30, 30, 30));
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMessage.setBounds(20, 90, 340, 30);
        pnlModal.add(lblMessage);

        // Done button
        JButton btnDone = new JButton("Done");
        btnDone.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDone.setBackground(new Color(0, 122, 255));
        btnDone.setForeground(Color.WHITE);
        btnDone.setBorderPainted(false);
        btnDone.setFocusPainted(false);
        btnDone.setBounds(135, 145, 110, 36);
        btnDone.addActionListener(e -> {
            dialog.dispose();
            javax.swing.SwingUtilities.invokeLater(() -> new OS_PM_Grid().setVisible(true));
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
        lblStep3 = new javax.swing.JLabel();
        lblStep4 = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblLastDentalVisit = new javax.swing.JLabel();
        txtLastDentalVisit = new javax.swing.JTextField();
        lblDentalHistory = new javax.swing.JLabel();
        txtDentalHistory = new javax.swing.JTextField();
        lblDentalProblems = new javax.swing.JLabel();
        txtDentalProblems = new javax.swing.JTextField();
        chkSmoker = new javax.swing.JCheckBox();
        chkAlcohol = new javax.swing.JCheckBox();
        chkOther = new javax.swing.JCheckBox();
        lblDentalMedicalNotes = new javax.swing.JLabel();
        txtDentalMedicalNotes = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblLastDentalVisit1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Patient Management (Step 4)");
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
        lblTitle.setText("Patient Management");
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
        lblLastDentalVisit.setText("Oral Hygine");
        cardPanel.add(lblLastDentalVisit);
        lblLastDentalVisit.setBounds(490, 140, 150, 20);

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

        chkSmoker.setText("Good");
        cardPanel.add(chkSmoker);
        chkSmoker.setBounds(630, 140, 80, 25);

        chkAlcohol.setText("Fair");
        cardPanel.add(chkAlcohol);
        chkAlcohol.setBounds(710, 140, 80, 25);

        chkOther.setText("Poor");
        cardPanel.add(chkOther);
        chkOther.setBounds(790, 140, 80, 25);

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

        lblLastDentalVisit1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLastDentalVisit1.setText("Last Dental Visit");
        cardPanel.add(lblLastDentalVisit1);
        lblLastDentalVisit1.setBounds(60, 135, 150, 20);

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
        boolean success = controller.submitFromStep4(
                txtLastDentalVisit.getText(),
                txtDentalHistory.getText(),
                txtDentalProblems.getText(),
                chkSmoker.isSelected(),
                chkAlcohol.isSelected(),
                chkOther.isSelected(),
                txtDentalMedicalNotes.getText(),
                this
        );

        if (success) {
            showSuccessDialog();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_PM_4().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox chkAlcohol;
    private javax.swing.JCheckBox chkOther;
    private javax.swing.JCheckBox chkSmoker;
    private javax.swing.JLabel lblDentalHistory;
    private javax.swing.JLabel lblDentalMedicalNotes;
    private javax.swing.JLabel lblDentalProblems;
    private javax.swing.JLabel lblLastDentalVisit;
    private javax.swing.JLabel lblLastDentalVisit1;
    private javax.swing.JLabel lblLogo;
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
