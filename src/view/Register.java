package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

/**
 * Register Form - borderless, draggable registration modal.
 * Features role selection (Office Staff, Patient, Dentist), NIC validation,
 * password confirmation, and show/hide password toggles.
 *
 * @author oveen
 */
public class Register extends javax.swing.JFrame {

    private static final Color COLOR_PRIMARY_ORANGE = new Color(231, 115, 36);
    private static final Color COLOR_INACTIVE_BG     = new Color(234, 234, 234);
    private static final Color COLOR_INACTIVE_TEXT   = new Color(51, 51, 51);

    private final javax.swing.JFrame parentFrame;

    private String selectedRole = "Office Staff";
    private boolean passVisible = false;
    private boolean confirmPassVisible = false;

    private int dragStartX;
    private int dragStartY;

    public Register() {
        this(null);
    }

    public Register(javax.swing.JFrame parent) {
        super("Register");
        this.parentFrame = resolveParentFrame(parent);

        setUndecorated(true);
        initComponents();
        setSize(550, 560);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblErrorMsg.setText(""); // Initially clear error message
        blockParent();
        if (parentFrame != null) {
            parentFrame.setVisible(true);
            parentFrame.toFront();
        }
        setLocationRelativeTo(parentFrame);
        setupDragging();
        setupCursors();

        setVisible(true);
        toFront();
        requestFocus();
    }

    private static javax.swing.JFrame resolveParentFrame(javax.swing.JFrame parent) {
        if (parent == null) {
            for (java.awt.Frame f : java.awt.Frame.getFrames()) {
                if (f instanceof Public_Dashboard && f.isDisplayable()) {
                    parent = (javax.swing.JFrame) f;
                    break;
                }
            }
        }
        if (parent == null) {
            parent = new Public_Dashboard();
        }
        if (!parent.isVisible()) {
            parent.setVisible(true);
        }
        parent.toFront();
        return parent;
    }

    private void blockParent() {
        if (parentFrame == null) return;
        parentFrame.setEnabled(false);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { restoreParent(); }
            @Override public void windowClosed(WindowEvent e)  { restoreParent(); }
        });
    }

    private void restoreParent() {
        if (parentFrame != null) {
            parentFrame.setEnabled(true);
            parentFrame.toFront();
        }
    }

    private void setupDragging() {
        panel.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                dragStartX = e.getXOnScreen();
                dragStartY = e.getYOnScreen();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getXOnScreen() - dragStartX,
                            getY() + e.getYOnScreen() - dragStartY);
                dragStartX = e.getXOnScreen();
                dragStartY = e.getYOnScreen();
            }
        });
    }

    private void setupCursors() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblCloseBtn.setCursor(hand);
        btnRoleStaff.setCursor(hand);
        btnRolePatient.setCursor(hand);
        btnRoleDentist.setCursor(hand);
        btnSaveDetails.setCursor(hand);
        btnShowHidePass.setCursor(hand);
        btnShowHideConfirm.setCursor(hand);
    }

    private void updateRoleButtons() {
        btnRoleStaff.setBackground(selectedRole.equals("Office Staff") ? COLOR_PRIMARY_ORANGE : COLOR_INACTIVE_BG);
        btnRoleStaff.setForeground(selectedRole.equals("Office Staff") ? Color.WHITE : COLOR_INACTIVE_TEXT);

        btnRolePatient.setBackground(selectedRole.equals("Patient") ? COLOR_PRIMARY_ORANGE : COLOR_INACTIVE_BG);
        btnRolePatient.setForeground(selectedRole.equals("Patient") ? Color.WHITE : COLOR_INACTIVE_TEXT);

        btnRoleDentist.setBackground(selectedRole.equals("Dentist") ? COLOR_PRIMARY_ORANGE : COLOR_INACTIVE_BG);
        btnRoleDentist.setForeground(selectedRole.equals("Dentist") ? Color.WHITE : COLOR_INACTIVE_TEXT);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        lblCloseBtn = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        btnRoleStaff = new javax.swing.JButton();
        btnRolePatient = new javax.swing.JButton();
        btnRoleDentist = new javax.swing.JButton();
        lblFullName = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        lblNic = new javax.swing.JLabel();
        txtNic = new javax.swing.JTextField();
        lblContactNumber = new javax.swing.JLabel();
        txtContactNumber = new javax.swing.JTextField();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnShowHidePass = new javax.swing.JButton();
        lblConfirmPassword = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        btnShowHideConfirm = new javax.swing.JButton();
        lblErrorMsg = new javax.swing.JLabel();
        btnSaveDetails = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Register");
        setResizable(false);
        getContentPane().setLayout(null);

        panel.setBackground(new java.awt.Color(255, 255, 255));
        panel.setLayout(null);

        lblCloseBtn.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblCloseBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCloseBtn.setText("X");
        lblCloseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCloseBtnMouseClicked(evt);
            }
        });
        panel.add(lblCloseBtn);
        lblCloseBtn.setBounds(495, 15, 30, 30);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Register");
        panel.add(lblTitle);
        lblTitle.setBounds(0, 20, 550, 40);

        btnRoleStaff.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRoleStaff.setText("Office Staff");
        btnRoleStaff.setBackground(new java.awt.Color(231, 115, 36));
        btnRoleStaff.setForeground(new java.awt.Color(255, 255, 255));
        btnRoleStaff.setBorderPainted(false);
        btnRoleStaff.setFocusPainted(false);
        btnRoleStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRoleStaffActionPerformed(evt);
            }
        });
        panel.add(btnRoleStaff);
        btnRoleStaff.setBounds(130, 80, 105, 34);

        btnRolePatient.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRolePatient.setText("Patient");
        btnRolePatient.setBackground(new java.awt.Color(234, 234, 234));
        btnRolePatient.setForeground(new java.awt.Color(51, 51, 51));
        btnRolePatient.setBorderPainted(false);
        btnRolePatient.setFocusPainted(false);
        btnRolePatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRolePatientActionPerformed(evt);
            }
        });
        panel.add(btnRolePatient);
        btnRolePatient.setBounds(240, 80, 95, 34);

        btnRoleDentist.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRoleDentist.setText("Dentist");
        btnRoleDentist.setBackground(new java.awt.Color(234, 234, 234));
        btnRoleDentist.setForeground(new java.awt.Color(51, 51, 51));
        btnRoleDentist.setBorderPainted(false);
        btnRoleDentist.setFocusPainted(false);
        btnRoleDentist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRoleDentistActionPerformed(evt);
            }
        });
        panel.add(btnRoleDentist);
        btnRoleDentist.setBounds(340, 80, 95, 34);

        lblFullName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblFullName.setText("Full Name:");
        panel.add(lblFullName);
        lblFullName.setBounds(90, 140, 130, 30);

        txtFullName.setBackground(new java.awt.Color(242, 242, 242));
        txtFullName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        panel.add(txtFullName);
        txtFullName.setBounds(220, 138, 240, 32);

        lblNic.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNic.setText("Nic:");
        panel.add(lblNic);
        lblNic.setBounds(90, 185, 130, 30);

        txtNic.setBackground(new java.awt.Color(242, 242, 242));
        txtNic.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        panel.add(txtNic);
        txtNic.setBounds(220, 183, 240, 32);

        lblContactNumber.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblContactNumber.setText("Contact Number:");
        panel.add(lblContactNumber);
        lblContactNumber.setBounds(90, 230, 130, 30);

        txtContactNumber.setBackground(new java.awt.Color(242, 242, 242));
        txtContactNumber.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        panel.add(txtContactNumber);
        txtContactNumber.setBounds(220, 228, 240, 32);

        lblUsername.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblUsername.setText("CreateUsername:");
        panel.add(lblUsername);
        lblUsername.setBounds(90, 275, 130, 30);

        txtUsername.setBackground(new java.awt.Color(242, 242, 242));
        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        panel.add(txtUsername);
        txtUsername.setBounds(220, 273, 240, 32);

        lblPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblPassword.setText("Create Password:");
        panel.add(lblPassword);
        lblPassword.setBounds(90, 320, 130, 30);

        txtPassword.setBackground(new java.awt.Color(242, 242, 242));
        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        panel.add(txtPassword);
        txtPassword.setBounds(220, 318, 200, 32);

        btnShowHidePass.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnShowHidePass.setText("Show");
        btnShowHidePass.setBackground(new java.awt.Color(220, 220, 220));
        btnShowHidePass.setBorderPainted(false);
        btnShowHidePass.setFocusPainted(false);
        btnShowHidePass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowHidePassActionPerformed(evt);
            }
        });
        panel.add(btnShowHidePass);
        btnShowHidePass.setBounds(425, 318, 35, 32);

        lblConfirmPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblConfirmPassword.setText("Confirm Password:");
        panel.add(lblConfirmPassword);
        lblConfirmPassword.setBounds(90, 365, 130, 30);

        txtConfirmPassword.setBackground(new java.awt.Color(242, 242, 242));
        txtConfirmPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        panel.add(txtConfirmPassword);
        txtConfirmPassword.setBounds(220, 363, 200, 32);

        btnShowHideConfirm.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnShowHideConfirm.setText("Show");
        btnShowHideConfirm.setBackground(new java.awt.Color(220, 220, 220));
        btnShowHideConfirm.setBorderPainted(false);
        btnShowHideConfirm.setFocusPainted(false);
        btnShowHideConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowHideConfirmActionPerformed(evt);
            }
        });
        panel.add(btnShowHideConfirm);
        btnShowHideConfirm.setBounds(425, 363, 35, 32);

        lblErrorMsg.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblErrorMsg.setForeground(new java.awt.Color(255, 59, 48));
        lblErrorMsg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblErrorMsg.setText("This Nic already has a login!!!");
        panel.add(lblErrorMsg);
        lblErrorMsg.setBounds(0, 415, 550, 25);

        btnSaveDetails.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnSaveDetails.setText("Save Details!");
        btnSaveDetails.setBackground(new java.awt.Color(231, 115, 36));
        btnSaveDetails.setForeground(new java.awt.Color(255, 255, 255));
        btnSaveDetails.setBorderPainted(false);
        btnSaveDetails.setFocusPainted(false);
        btnSaveDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveDetailsActionPerformed(evt);
            }
        });
        panel.add(btnSaveDetails);
        btnSaveDetails.setBounds(180, 455, 190, 44);

        getContentPane().add(panel);
        panel.setBounds(0, 0, 560, 590);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRoleStaffActionPerformed(java.awt.event.ActionEvent evt) {
        selectedRole = "Office Staff";
        updateRoleButtons();
    }

    private void btnRolePatientActionPerformed(java.awt.event.ActionEvent evt) {
        selectedRole = "Patient";
        updateRoleButtons();
    }

    private void btnRoleDentistActionPerformed(java.awt.event.ActionEvent evt) {
        selectedRole = "Dentist";
        updateRoleButtons();
    }

    private void btnShowHidePassActionPerformed(java.awt.event.ActionEvent evt) {
        passVisible = !passVisible;
        if (passVisible) {
            txtPassword.setEchoChar((char) 0);
            btnShowHidePass.setText("Hide");
        } else {
            txtPassword.setEchoChar('\u2022');
            btnShowHidePass.setText("Show");
        }
    }

    private void btnShowHideConfirmActionPerformed(java.awt.event.ActionEvent evt) {
        confirmPassVisible = !confirmPassVisible;
        if (confirmPassVisible) {
            txtConfirmPassword.setEchoChar((char) 0);
            btnShowHideConfirm.setText("Hide");
        } else {
            txtConfirmPassword.setEchoChar('\u2022');
            btnShowHideConfirm.setText("Show");
        }
    }

    private void lblCloseBtnMouseClicked(java.awt.event.MouseEvent evt) {
        for (java.awt.event.WindowListener wl : getWindowListeners()) {
            removeWindowListener(wl);
        }
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginForm(parentFrame).setVisible(true);
        });
    }

    private void btnSaveDetailsActionPerformed(java.awt.event.ActionEvent evt) {
        String fullName = txtFullName.getText().trim();
        String nic      = txtNic.getText().trim();
        String contact  = txtContactNumber.getText().trim();
        String username = txtUsername.getText().trim();
        String pass     = String.valueOf(txtPassword.getPassword());
        String confirm  = String.valueOf(txtConfirmPassword.getPassword());

        if (fullName.isEmpty() || nic.isEmpty() || contact.isEmpty() || username.isEmpty() || pass.isEmpty()) {
            lblErrorMsg.setText("Please fill in all registration fields.");
            return;
        }

        // Demo NIC duplication check
        if (nic.equalsIgnoreCase("123456789V") || nic.equalsIgnoreCase("200012345678")) {
            lblErrorMsg.setText("This Nic already has a login!!!");
            txtNic.requestFocusInWindow();
            return;
        }

        if (!pass.equals(confirm)) {
            lblErrorMsg.setText("Passwords do not match!");
            txtConfirmPassword.requestFocusInWindow();
            return;
        }

        lblErrorMsg.setText("");
        JOptionPane.showMessageDialog(this,
                "Registration successful for " + selectedRole + ": " + fullName,
                "Registration Success", JOptionPane.INFORMATION_MESSAGE);

        for (java.awt.event.WindowListener wl : getWindowListeners()) {
            removeWindowListener(wl);
        }
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginForm(parentFrame).setVisible(true);
        });
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Register().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRoleDentist;
    private javax.swing.JButton btnRolePatient;
    private javax.swing.JButton btnRoleStaff;
    private javax.swing.JButton btnSaveDetails;
    private javax.swing.JButton btnShowHideConfirm;
    private javax.swing.JButton btnShowHidePass;
    private javax.swing.JLabel lblCloseBtn;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblContactNumber;
    private javax.swing.JLabel lblErrorMsg;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblNic;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel panel;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtContactNumber;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtNic;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
