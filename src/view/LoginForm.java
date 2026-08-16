package view;

import controller.LogInController;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import model.User;

/**
 * Login form - borderless, draggable, modal-like login window.
 * Features: show/hide password toggle, Remember Me persistence.
 *
 * @author oveen
 */
public class LoginForm extends javax.swing.JFrame {

    // =========================================================================
    // Fields
    // =========================================================================

    private final LogInController loginController;
    private final javax.swing.JFrame parentFrame;

    private static final Preferences PREFS =
            Preferences.userRoot().node("SunriseDentalSystem/login");
    private static final String PREF_USERNAME    = "savedUsername";
    private static final String PREF_PASSWORD    = "savedPassword";
    private static final String PREF_REMEMBER_ME = "rememberMe";

    private static final java.awt.Color EYE_COLOR = new java.awt.Color(90, 90, 90);
    private static final int EYE_SIZE = 16;

    private boolean passwordVisible = false;

    // Set right before disposing this form on a successful login, so the
    // dispose()-triggered restoreParent() below skips its undim call — the
    // success dialog is about to dim the very same parent frame itself, and
    // letting both fire would race (dispose() queues its WINDOW_CLOSED event
    // rather than firing it synchronously, so restoreParent()'s undim can
    // land AFTER showSuccessDialog's dim call, silently cancelling it).
    private boolean keepParentDimmed = false;

    private int dragStartX;
    private int dragStartY;

    // =========================================================================
    // Constructors
    // =========================================================================

    public LoginForm() {
        this(null);
    }

    public LoginForm(javax.swing.JFrame parent) {
        super("Login");
        this.parentFrame     = resolveParentFrame(parent);
        this.loginController = new LogInController();

        setUndecorated(true);
        initComponents();
        btnShowHide.setText(null);
        btnShowHide.setIcon(IconFactory.eye(EYE_COLOR, EYE_SIZE)); // crisp vector eye — replaces "Show"/"Hide" text that was ellipsis-truncated in this button's narrow 35px width
        setSize(500, 440);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        blockParent();
        if (parentFrame != null) {
            parentFrame.setVisible(true);
            parentFrame.toFront();
        }
        setLocationRelativeTo(null); // center on the screen itself, not just the (possibly off-center) parent window
        setupDragging();
        setupCursorsAndKeys();
        loadSavedCredentials();

        setVisible(true);
        toFront();
        requestFocus();
    }

    // =========================================================================
    // Remember Me helpers
    // =========================================================================

    private void loadSavedCredentials() {
        if (PREFS.getBoolean(PREF_REMEMBER_ME, false)) {
            txtUsername.setText(PREFS.get(PREF_USERNAME, ""));
            txtPassword.setText(PREFS.get(PREF_PASSWORD, ""));
            chkRememberMe.setSelected(true);
        }
    }

    private void persistCredentials(String username, String password) {
        if (chkRememberMe.isSelected()) {
            PREFS.putBoolean(PREF_REMEMBER_ME, true);
            PREFS.put(PREF_USERNAME, username);
            PREFS.put(PREF_PASSWORD, password);
        } else {
            PREFS.putBoolean(PREF_REMEMBER_ME, false);
            PREFS.remove(PREF_USERNAME);
            PREFS.remove(PREF_PASSWORD);
        }
    }

    // =========================================================================
    // Window helpers
    // =========================================================================

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
        IconFactory.setDimmed(parentFrame, true);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { restoreParent(); }
            @Override public void windowClosed(WindowEvent e)  { restoreParent(); }
        });
    }

    private void restoreParent() {
        if (parentFrame != null) {
            parentFrame.setEnabled(true);
            if (!keepParentDimmed) {
                IconFactory.setDimmed(parentFrame, false);
            }
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

    private void setupCursorsAndKeys() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblCloseBtn.setCursor(hand);
        btnLogin.setCursor(hand);
        btnShowHide.setCursor(hand);
        chkRememberMe.setCursor(hand);
        lblRegister.setCursor(hand);

        lblRegister.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { openRegister(); }
        });
        lblCloseBtn.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dispose(); }
        });

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) btnLogin.doClick();
            }
        });

        txtUsername.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocusInWindow();
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        lblCloseBtn = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnShowHide = new javax.swing.JButton();
        chkRememberMe = new javax.swing.JCheckBox();
        btnLogin = new javax.swing.JButton();
        lblRegister = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login");
        setResizable(false);
        getContentPane().setLayout(null);

        panel.setBackground(new java.awt.Color(255, 255, 255));
        panel.setLayout(null);

        lblCloseBtn.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblCloseBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCloseBtn.setText("X");
        lblCloseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCloseBtnMouseClicked(evt);
            }
        });
        panel.add(lblCloseBtn);
        lblCloseBtn.setBounds(440, 20, 30, 30);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Login");
        panel.add(lblTitle);
        lblTitle.setBounds(0, 50, 500, 50);

        lblUsername.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblUsername.setText("Username:");
        panel.add(lblUsername);
        lblUsername.setBounds(80, 140, 90, 25);

        txtUsername.setBackground(new java.awt.Color(240, 240, 240));
        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        panel.add(txtUsername);
        txtUsername.setBounds(170, 140, 250, 30);

        lblPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPassword.setText("Password:");
        panel.add(lblPassword);
        lblPassword.setBounds(80, 190, 90, 25);

        txtPassword.setBackground(new java.awt.Color(240, 240, 240));
        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        panel.add(txtPassword);
        txtPassword.setBounds(170, 190, 215, 30);

        btnShowHide.setBackground(new java.awt.Color(220, 220, 220));
        btnShowHide.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnShowHide.setText("Show");
        btnShowHide.setToolTipText("Show / hide password");
        btnShowHide.setBorderPainted(false);
        btnShowHide.setFocusPainted(false);
        btnShowHide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowHideActionPerformed(evt);
            }
        });
        panel.add(btnShowHide);
        btnShowHide.setBounds(380, 190, 35, 30);

        chkRememberMe.setBackground(new java.awt.Color(255, 255, 255));
        chkRememberMe.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        chkRememberMe.setText("Remember me");
        chkRememberMe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRememberMeActionPerformed(evt);
            }
        });
        panel.add(chkRememberMe);
        chkRememberMe.setBounds(180, 230, 130, 25);

        btnLogin.setBackground(new java.awt.Color(255, 140, 0));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        panel.add(btnLogin);
        btnLogin.setBounds(170, 270, 130, 40);

        lblRegister.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblRegister.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRegister.setText("<html>Don't have an account? <u><font color='#0066cc'>Register</font></u></html>");
        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRegisterMouseClicked(evt);
            }
        });
        panel.add(lblRegister);
        lblRegister.setBounds(50, 340, 360, 25);

        getContentPane().add(panel);
        panel.setBounds(0, 0, 500, 440);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkRememberMeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRememberMeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkRememberMeActionPerformed

    // =========================================================================
    // Event Handlers
    // =========================================================================

    private void btnShowHideActionPerformed(java.awt.event.ActionEvent evt) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            txtPassword.setEchoChar((char) 0);
            btnShowHide.setIcon(IconFactory.eyeOff(EYE_COLOR, EYE_SIZE));
        } else {
            txtPassword.setEchoChar('\u2022');
            btnShowHide.setIcon(IconFactory.eye(EYE_COLOR, EYE_SIZE));
        }
        txtPassword.requestFocusInWindow();
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String username = txtUsername.getText().trim();
        String password = String.valueOf(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            if (username.isEmpty()) txtUsername.requestFocusInWindow();
            else                    txtPassword.requestFocusInWindow();
            return;
        }

        User user = loginController.attemptLogin(username, password);

        if (user != null) {
            persistCredentials(username, password);
            final User authenticatedUser = user;
            final javax.swing.JFrame successOwner = parentFrame;

            // Close the login card itself right away — it shouldn't still be
            // sitting there (just hidden behind the success popup) while the
            // confirmation is up. Public_Dashboard stays exactly as it looks
            // right now; only ownership of the success dialog — and its own
            // dimming of that same frame — moves to it.
            keepParentDimmed = true;
            dispose();

            IconFactory.showSuccessDialog(successOwner,
                    "Login Successful!\nWelcome, " + user.getFullName() + "!",
                    () -> {
                        if (successOwner != null) successOwner.dispose();
                        SwingUtilities.invokeLater(() -> openDashboard(authenticatedUser));
                    });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password. Please try again.",
                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }

    private void openDashboard(User user) {
        // Delegate to AppController — the single source of navigation truth
        controller.AppController.openDashboardForUser(user);
    }

    private void lblCloseBtnMouseClicked(java.awt.event.MouseEvent evt) {
        dispose();
    }

    private void lblRegisterMouseClicked(java.awt.event.MouseEvent evt) {
        openRegister();
    }

    private boolean registerOpened = false;
    private void openRegister() {
        if (registerOpened) return;
        registerOpened = true;

        for (java.awt.event.WindowListener wl : getWindowListeners()) {
            removeWindowListener(wl);
        }
        dispose();
        SwingUtilities.invokeLater(() -> {
            new Register(parentFrame).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnShowHide;
    private javax.swing.JCheckBox chkRememberMe;
    private javax.swing.JLabel lblCloseBtn;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRegister;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel panel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
