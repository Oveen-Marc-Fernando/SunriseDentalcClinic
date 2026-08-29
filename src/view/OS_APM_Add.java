package view;

import controller.AppController;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Office Staff - Add Approval View.
 * Displays form to submit new approval request with custom success confirmation dialog.
 *
 * @author oveen
 */
public class OS_APM_Add extends javax.swing.JFrame {

    public OS_APM_Add() {
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        txtApprovalId.setText(controller.ApprovalController.nextApprovalId()); // auto-generated up front, like AP-xxx elsewhere
        txtDate.setText("2026-08-08");
        DateTimePicker.attachDate(txtDate);
        setSize(1016, 739);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void showSuccessModal() {
        final JDialog dialog = new JDialog(this, "Success", true);
        dialog.setUndecorated(true);
        dialog.setSize(380, 240);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.setColor(new Color(210, 210, 210));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
            }
        };
        panel.setLayout(null);
        panel.setBackground(new Color(245, 245, 245));

        // Green checkmark icon circle
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 167, 69));
                g2.fillOval(0, 0, getWidth(), getHeight());

                // White checkmark lines
                g2.setColor(Color.WHITE);
                g2.setStroke(new java.awt.BasicStroke(4, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g2.drawLine(18, 30, 26, 38);
                g2.drawLine(26, 38, 42, 20);
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setBounds(160, 25, 60, 60);
        panel.add(iconCircle);

        JLabel lblMsg = new JLabel("Approval Sent Successfully!", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMsg.setForeground(new Color(30, 30, 30));
        lblMsg.setBounds(20, 100, 340, 30);
        panel.add(lblMsg);

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(100, 100, 100));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setBounds(145, 155, 90, 32);
        btnClose.addActionListener(e -> dialog.dispose());
        panel.add(btnClose);

        dialog.getContentPane().add(panel);
        dialog.setVisible(true);

        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OS_APM_Grid().setVisible(true);
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        btnBack = new javax.swing.JButton();
        lblSubtitle = new javax.swing.JLabel();
        lblApprovalId = new javax.swing.JLabel();
        txtApprovalId = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        lblDate = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        lblAmount = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        btnSend = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Approval Management (Add Approval)");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 165, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("Approval Management");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 420, 40);

        cardPanel.setBackground(new java.awt.Color(248, 249, 250));
        cardPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.setLayout(null);

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
        btnBack.setBounds(40, 30, 100, 36);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Add Approval");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(40, 85, 300, 35);

        lblApprovalId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblApprovalId.setText("Approval ID:");
        cardPanel.add(lblApprovalId);
        lblApprovalId.setBounds(60, 140, 120, 25);

        txtApprovalId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtApprovalId);
        txtApprovalId.setBounds(190, 135, 350, 35);

        lblDescription.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDescription.setText("Description:");
        cardPanel.add(lblDescription);
        lblDescription.setBounds(60, 200, 120, 25);

        txtDescription.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDescription);
        txtDescription.setBounds(190, 195, 350, 35);

        lblDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDate.setText("Date:");
        cardPanel.add(lblDate);
        lblDate.setBounds(60, 260, 120, 25);

        txtDate.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtDate);
        txtDate.setBounds(190, 255, 350, 35);

        lblAmount.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAmount.setText("Amount:");
        cardPanel.add(lblAmount);
        lblAmount.setBounds(60, 320, 120, 25);

        txtAmount.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtAmount);
        txtAmount.setBounds(190, 315, 350, 35);

        btnSend.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnSend.setText("Send");
        btnSend.setBackground(new java.awt.Color(40, 167, 69));
        btnSend.setForeground(new java.awt.Color(255, 255, 255));
        btnSend.setBorderPainted(false);
        btnSend.setFocusPainted(false);
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });
        cardPanel.add(btnSend);
        btnSend.setBounds(730, 410, 120, 40);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OS_APM_Grid().setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        String desc   = txtDescription.getText().trim();
        String amount = txtAmount.getText().trim();
        if (desc.isEmpty() || amount.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter Description and Amount before sending.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String submittedBy = AppController.getCurrentUser() != null
                ? AppController.getCurrentUser().getUsername() : null;
        controller.ApprovalController.submitApproval(
                txtApprovalId.getText().trim(), desc, txtDate.getText().trim(), amount, submittedBy);

        showSuccessModal();
    }//GEN-LAST:event_btnSendActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_APM_Add().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSend;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblAmount;
    private javax.swing.JLabel lblApprovalId;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtApprovalId;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDescription;
    // End of variables declaration//GEN-END:variables
}
