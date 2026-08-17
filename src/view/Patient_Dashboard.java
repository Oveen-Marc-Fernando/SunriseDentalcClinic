package view;

import controller.AppController;
import controller.PatientController;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.User;

/**
 * Patient Dashboard View - NetBeans GUI Builder compatible.
 * Provides: Profile, Appointments, Billings, Reports, Help Desk.
 *
 * @author oveen
 */
public class Patient_Dashboard extends javax.swing.JFrame {

    private final PatientController controller;

    // Non-generated popup components (declared outside GEN block)
    // =========================================================================
    // Constructors
    // =========================================================================

    public Patient_Dashboard(User user) {
        this.controller = new PatientController(user);
        initComponents();
        lblUserIcon.setIcon(IconFactory.userGlyph(java.awt.Color.WHITE, 26)); // crisp vector glyph, no backdrop — sits directly on the black pill
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        setSize(1016, 739);
        applyWelcome();
        bindActions();
        setLocationRelativeTo(null);
    }

    public Patient_Dashboard() {
        this(null);
    }

    // =========================================================================
    // Custom setup
    // =========================================================================

    private void applyWelcome() {
        lblWelcome.setText(controller.getWelcomeMessage());
    }

    private void bindActions() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblUserIcon.setCursor(hand);
        lblUserIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                IconFactory.showProfileMenu(Patient_Dashboard.this, lblUserIcon,
                        () -> {
                            controller.editProfile();
                            IconFactory.showEditProfileDialog(Patient_Dashboard.this, controller.getUsername(),
                                    newPassword -> {
                                        if (newPassword == null || newPassword.isEmpty()) {
                                            return;
                                        }
                                        if (controller.saveProfileChanges(newPassword)) {
                                            IconFactory.showSuccessDialog(Patient_Dashboard.this,
                                                    "Password updated successfully!", null);
                                        } else {
                                            javax.swing.JOptionPane.showMessageDialog(Patient_Dashboard.this,
                                                    "Couldn't update the password — the database may be unreachable.",
                                                    "Update Failed", javax.swing.JOptionPane.ERROR_MESSAGE);
                                        }
                                    });
                        },
                        () -> {
                            controller.logout();
                            AppController.logout(Patient_Dashboard.this);
                        });
            }
        });

        // NOTE: these controller methods are still println-only stubs (no
        // grid views built yet for Patient), so — unlike
        // OfficeStaff_Dashboard — this dashboard must NOT dispose itself on
        // click: there's nothing that would replace it, and the user would
        // be left looking at no window at all.
        addCardAction(pnlProfile,      () -> controller.openProfile());
        addCardAction(pnlAppointments, () -> controller.openAppointments());
        addCardAction(pnlBillings,     () -> controller.openBillings());
        addCardAction(pnlReports,      () -> controller.openReports());
        addCardAction(pnlHelpDesk,     () -> controller.openHelpDesk());
    }

    private void addCardAction(javax.swing.JPanel card, Runnable action) {
        final Color base  = card.getBackground();
        final Color hover = new Color(
                Math.min(255, (int)(base.getRed()   * 1.12)),
                Math.min(255, (int)(base.getGreen() * 1.12)),
                Math.min(255, (int)(base.getBlue()  * 1.12)));

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { action.run(); }
            @Override public void mouseEntered(MouseEvent e)  { card.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)   { card.setBackground(base); }
        };
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(ma);

        for (java.awt.Component c : card.getComponents()) {
            c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            c.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e)  { action.run(); }
                @Override public void mouseEntered(MouseEvent e)  { card.setBackground(hover); }
                @Override public void mouseExited(MouseEvent e)   { card.setBackground(base); }
            });
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblUserIcon = new javax.swing.JLabel();
        lblWelcome = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        pnlProfile = new javax.swing.JPanel();
        lblProfile = new javax.swing.JLabel();
        pnlAppointments = new javax.swing.JPanel();
        lblAppointments = new javax.swing.JLabel();
        pnlBillings = new javax.swing.JPanel();
        lblBillings = new javax.swing.JLabel();
        pnlReports = new javax.swing.JPanel();
        lblReports = new javax.swing.JLabel();
        pnlHelpDesk = new javax.swing.JPanel();
        lblHelpDesk = new javax.swing.JLabel();
        pnlEmpty = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sunrise Dental - Patient");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 160, 40);

        lblUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/login_scaled.png"))); // NOI18N
        lblUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        navBar.add(lblUserIcon);
        lblUserIcon.setBounds(850, 10, 40, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblWelcome.setText("Hii Patient, Welcome!!");
        mainPanel.add(lblWelcome);
        lblWelcome.setBounds(50, 120, 600, 40);

        cardPanel.setBackground(new java.awt.Color(242, 242, 242));
        cardPanel.setLayout(null);

        pnlProfile.setBackground(new java.awt.Color(91, 108, 140));
        pnlProfile.setLayout(null);

        lblProfile.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblProfile.setForeground(new java.awt.Color(30, 30, 30));
        lblProfile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProfile.setText("Profile");
        pnlProfile.add(lblProfile);
        lblProfile.setBounds(0, 0, 220, 120);

        cardPanel.add(pnlProfile);
        pnlProfile.setBounds(70, 80, 220, 120);

        pnlAppointments.setBackground(new java.awt.Color(184, 166, 206));
        pnlAppointments.setLayout(null);

        lblAppointments.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblAppointments.setForeground(new java.awt.Color(30, 30, 30));
        lblAppointments.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAppointments.setText("Appointments");
        pnlAppointments.add(lblAppointments);
        lblAppointments.setBounds(0, 0, 220, 120);

        cardPanel.add(pnlAppointments);
        pnlAppointments.setBounds(320, 80, 220, 120);

        pnlBillings.setBackground(new java.awt.Color(247, 217, 220));
        pnlBillings.setLayout(null);

        lblBillings.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblBillings.setForeground(new java.awt.Color(30, 30, 30));
        lblBillings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBillings.setText("Billings");
        pnlBillings.add(lblBillings);
        lblBillings.setBounds(0, 0, 220, 120);

        cardPanel.add(pnlBillings);
        pnlBillings.setBounds(570, 80, 220, 120);

        pnlReports.setBackground(new java.awt.Color(214, 198, 182));
        pnlReports.setLayout(null);

        lblReports.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblReports.setForeground(new java.awt.Color(30, 30, 30));
        lblReports.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReports.setText("Reports");
        pnlReports.add(lblReports);
        lblReports.setBounds(0, 0, 220, 120);

        cardPanel.add(pnlReports);
        pnlReports.setBounds(70, 260, 220, 120);

        pnlHelpDesk.setBackground(new java.awt.Color(189, 198, 151));
        pnlHelpDesk.setLayout(null);

        lblHelpDesk.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblHelpDesk.setForeground(new java.awt.Color(30, 30, 30));
        lblHelpDesk.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHelpDesk.setText("Help Desk");
        pnlHelpDesk.add(lblHelpDesk);
        lblHelpDesk.setBounds(0, 0, 220, 120);

        cardPanel.add(pnlHelpDesk);
        pnlHelpDesk.setBounds(320, 260, 220, 120);

        pnlEmpty.setBackground(new java.awt.Color(197, 232, 183));
        pnlEmpty.setLayout(null);
        cardPanel.add(pnlEmpty);
        pnlEmpty.setBounds(570, 260, 220, 120);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 190, 860, 460);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblAppointments;
    private javax.swing.JLabel lblBillings;
    private javax.swing.JLabel lblHelpDesk;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblProfile;
    private javax.swing.JLabel lblReports;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlAppointments;
    private javax.swing.JPanel pnlBillings;
    private javax.swing.JPanel pnlEmpty;
    private javax.swing.JPanel pnlHelpDesk;
    private javax.swing.JPanel pnlProfile;
    private javax.swing.JPanel pnlReports;
    // End of variables declaration//GEN-END:variables
}
