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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
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
        IconFactory.roundCorners(pnlLeft, 28);
        lblLeftLogo.setIcon(IconFactory.brandLogo(130, 40)); // 130x40 matches this app's standard logo size everywhere else
        lblIconMail.setIcon(IconFactory.mailBrand(22));
        lblIconInstagram.setIcon(IconFactory.instagramBrand(22));
        lblIconTiktok.setIcon(IconFactory.tiktokBrand(22));
        lblIconWhatsapp.setIcon(IconFactory.whatsappBrand(22));
        setSize(640, 460);
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
        btnRegisterLeft.setCursor(hand);

        btnRegisterLeft.addActionListener(e -> openRegister());
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
        pnlLeft = new javax.swing.JPanel();
        lblLeftLogo = new javax.swing.JLabel();
        lblWelcome = new javax.swing.JLabel();
        lblWelcomeSub = new javax.swing.JLabel();
        btnRegisterLeft = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnShowHide = new javax.swing.JButton();
        chkRememberMe = new javax.swing.JCheckBox();
        btnLogin = new javax.swing.JButton();
        lblConnectWith = new javax.swing.JLabel();
        lblIconMail = new javax.swing.JLabel();
        lblIconInstagram = new javax.swing.JLabel();
        lblIconTiktok = new javax.swing.JLabel();
        lblIconWhatsapp = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login");
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
        lblCloseBtn.setBounds(594, 12, 30, 30);

        pnlLeft.setBackground(new java.awt.Color(0, 0, 0));
        pnlLeft.setLayout(null);

        lblLeftLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlLeft.add(lblLeftLogo);
        lblLeftLogo.setBounds(50, 90, 130, 40);

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblWelcome.setForeground(new java.awt.Color(255, 255, 255));
        lblWelcome.setText("Hello, Welcome!");
        pnlLeft.add(lblWelcome);
        lblWelcome.setBounds(20, 190, 200, 34);

        lblWelcomeSub.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblWelcomeSub.setForeground(new java.awt.Color(255, 235, 220));
        lblWelcomeSub.setText("<html>Please login to your account</html>");
        pnlLeft.add(lblWelcomeSub);
        lblWelcomeSub.setBounds(30, 230, 200, 44);

        btnRegisterLeft.setBackground(new java.awt.Color(231, 115, 36));
        btnRegisterLeft.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRegisterLeft.setForeground(new java.awt.Color(255, 255, 255));
        btnRegisterLeft.setText("Register");
        btnRegisterLeft.setBorderPainted(false);
        btnRegisterLeft.setFocusPainted(false);
        pnlLeft.add(btnRegisterLeft);
        btnRegisterLeft.setBounds(40, 300, 130, 42);

        panel.add(pnlLeft);
        pnlLeft.setBounds(30, 30, 230, 400);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTitle.setText("Login");
        panel.add(lblTitle);
        lblTitle.setBounds(400, 50, 130, 44);

        lblUsername.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(90, 90, 90));
        lblUsername.setText("Username");
        panel.add(lblUsername);
        lblUsername.setBounds(300, 112, 200, 20);

        txtUsername.setBackground(new java.awt.Color(240, 240, 240));
        txtUsername.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12));
        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        panel.add(txtUsername);
        txtUsername.setBounds(300, 136, 290, 38);

        lblPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(90, 90, 90));
        lblPassword.setText("Password");
        panel.add(lblPassword);
        lblPassword.setBounds(300, 186, 200, 20);

        txtPassword.setBackground(new java.awt.Color(240, 240, 240));
        txtPassword.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 40));
        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        panel.add(txtPassword);
        txtPassword.setBounds(300, 210, 250, 38);

        btnShowHide.setToolTipText("Show / hide password");
        btnShowHide.setBorderPainted(false);
        btnShowHide.setFocusPainted(false);
        btnShowHide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowHideActionPerformed(evt);
            }
        });
        panel.add(btnShowHide);
        btnShowHide.setBounds(550, 210, 40, 38);

        chkRememberMe.setBackground(new java.awt.Color(255, 255, 255));
        chkRememberMe.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        chkRememberMe.setText("Remember me");
        chkRememberMe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRememberMeActionPerformed(evt);
            }
        });
        panel.add(chkRememberMe);
        chkRememberMe.setBounds(380, 260, 160, 24);

        btnLogin.setBackground(new java.awt.Color(231, 115, 36));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Login");
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        panel.add(btnLogin);
        btnLogin.setBounds(300, 300, 290, 46);

        lblConnectWith.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblConnectWith.setForeground(new java.awt.Color(140, 140, 140));
        lblConnectWith.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblConnectWith.setText("or connect with");
        panel.add(lblConnectWith);
        lblConnectWith.setBounds(300, 358, 290, 20);

        lblIconMail.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panel.add(lblIconMail);
        lblIconMail.setBounds(360, 386, 26, 26);

        lblIconInstagram.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panel.add(lblIconInstagram);
        lblIconInstagram.setBounds(410, 386, 26, 26);

        lblIconTiktok.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panel.add(lblIconTiktok);
        lblIconTiktok.setBounds(460, 386, 26, 26);

        lblIconWhatsapp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panel.add(lblIconWhatsapp);
        lblIconWhatsapp.setBounds(510, 386, 26, 26);

        getContentPane().add(panel);
        panel.setBounds(0, 0, 640, 460);

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
            IconFactory.showErrorDialog(this, "Please enter both username and password.", () -> {
                if (username.isEmpty()) txtUsername.requestFocusInWindow();
                else                    txtPassword.requestFocusInWindow();
            });
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
            String status = loginController.getAccountStatus(username);
            String message = "PENDING".equals(status)
                    ? "Your account is still pending Administrator approval — you'll be able to log in once it's approved."
                    : "REJECTED".equals(status)
                            ? "This account's registration was rejected. Contact an Administrator for details."
                            : "Invalid username or password. Please try again.";
            IconFactory.showErrorDialog(this, message, () -> {
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
            });
        }
    }

    private void openDashboard(User user) {
        // Delegate to AppController — the single source of navigation truth
        controller.AppController.openDashboardForUser(user);
    }

    private void lblCloseBtnMouseClicked(java.awt.event.MouseEvent evt) {
        dispose();
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
    private javax.swing.JButton btnRegisterLeft;
    private javax.swing.JButton btnShowHide;
    private javax.swing.JCheckBox chkRememberMe;
    private javax.swing.JLabel lblCloseBtn;
    private javax.swing.JLabel lblConnectWith;
    private javax.swing.JLabel lblIconInstagram;
    private javax.swing.JLabel lblIconMail;
    private javax.swing.JLabel lblIconTiktok;
    private javax.swing.JLabel lblIconWhatsapp;
    private javax.swing.JLabel lblLeftLogo;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JLabel lblWelcomeSub;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
