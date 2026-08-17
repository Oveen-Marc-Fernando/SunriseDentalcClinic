package view;

import controller.AdministrationController;
import controller.AppController;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.User;

/**
 * Administration Dashboard View - NetBeans GUI Builder compatible.
 *
 * @author oveen
 */
public class Administration_Dashboard extends javax.swing.JFrame {

    private final AdministrationController controller;

    // =========================================================================
    // Constructors
    // =========================================================================

    public Administration_Dashboard(User user) {
        this.controller = new AdministrationController(user);
        initComponents();
        lblUserIcon.setIcon(IconFactory.userGlyph(java.awt.Color.WHITE, 26)); // crisp vector glyph, no backdrop — sits directly on the black pill
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        setSize(1016, 739);
        applyWelcome();
        bindActions();
        setLocationRelativeTo(null);
    }

    public Administration_Dashboard() {
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
                IconFactory.showProfileMenu(Administration_Dashboard.this, lblUserIcon,
                        () -> {
                            controller.editProfile();
                            IconFactory.showEditProfileDialog(Administration_Dashboard.this, controller.getUsername(),
                                    newPassword -> controller.saveProfileChanges(newPassword));
                        },
                        () -> {
                            controller.logout();
                            AppController.logout(Administration_Dashboard.this);
                        });
            }
        });

        // NOTE: these controller methods are still println-only stubs (no
        // grid views built yet for Administration), so — unlike
        // OfficeStaff_Dashboard — this dashboard must NOT dispose itself on
        // click: there's nothing that would replace it, and the user would
        // be left looking at no window at all.
        addCardAction(pnlUserMgmt,         () -> controller.openUserManagement());
        addCardAction(pnlReceptionistMgmt, () -> controller.openReceptionistManagement());
        addCardAction(pnlDentistMgmt,      () -> controller.openDentistManagement());
        addCardAction(pnlReports,          () -> controller.openReports());
        addCardAction(pnlHelpDesk,         () -> controller.openHelpDesk());
        addCardAction(pnlApprovals,        () -> controller.openApprovals());
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
        pnlUserMgmt = new javax.swing.JPanel();
        lblUserMgmt = new javax.swing.JLabel();
        pnlReceptionistMgmt = new javax.swing.JPanel();
        lblReceptionistMgmt = new javax.swing.JLabel();
        pnlDentistMgmt = new javax.swing.JPanel();
        lblDentistMgmt = new javax.swing.JLabel();
        pnlReports = new javax.swing.JPanel();
        lblReports = new javax.swing.JLabel();
        pnlHelpDesk = new javax.swing.JPanel();
        lblHelpDesk = new javax.swing.JLabel();
        pnlApprovals = new javax.swing.JPanel();
        lblApprovals = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sunrise Dental — Administration");
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
        lblWelcome.setText("Hii Administration, Welcome!!");
        mainPanel.add(lblWelcome);
        lblWelcome.setBounds(50, 120, 600, 40);

        cardPanel.setBackground(new java.awt.Color(242, 242, 242));
        cardPanel.setLayout(null);

        pnlUserMgmt.setBackground(new java.awt.Color(91, 108, 140));
        pnlUserMgmt.setLayout(null);

        lblUserMgmt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblUserMgmt.setForeground(new java.awt.Color(30, 30, 30));
        lblUserMgmt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUserMgmt.setText("<html><div style='text-align:center'>User<br>Management</div></html>");
        pnlUserMgmt.add(lblUserMgmt);
        lblUserMgmt.setBounds(0, 0, 220, 120);

        cardPanel.add(pnlUserMgmt);
        pnlUserMgmt.setBounds(70, 80, 220, 120);

        pnlReceptionistMgmt.setBackground(new java.awt.Color(184, 166, 206));
        pnlReceptionistMgmt.setLayout(null);

        lblReceptionistMgmt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblReceptionistMgmt.setForeground(new java.awt.Color(30, 30, 30));
        lblReceptionistMgmt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReceptionistMgmt.setText("<html><div style='text-align:center'>Receptionist<br>Management</div></html>");
        pnlReceptionistMgmt.add(lblReceptionistMgmt);
        lblReceptionistMgmt.setBounds(0, 0, 220, 120);

        cardPanel.add(pnlReceptionistMgmt);
        pnlReceptionistMgmt.setBounds(320, 80, 220, 120);

        pnlDentistMgmt.setBackground(new java.awt.Color(247, 217, 220));
        pnlDentistMgmt.setLayout(null);

        lblDentistMgmt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDentistMgmt.setForeground(new java.awt.Color(30, 30, 30));
        lblDentistMgmt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDentistMgmt.setText("<html><div style='text-align:center'>Dentist<br>Management</div></html>");
        pnlDentistMgmt.add(lblDentistMgmt);
        lblDentistMgmt.setBounds(0, 0, 240, 120);

        cardPanel.add(pnlDentistMgmt);
        pnlDentistMgmt.setBounds(570, 80, 220, 120);

        pnlReports.setBackground(new java.awt.Color(214, 198, 182));
        pnlReports.setLayout(null);

        lblReports.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblReports.setForeground(new java.awt.Color(30, 30, 30));
        lblReports.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReports.setText("Reports");
        pnlReports.add(lblReports);
        lblReports.setBounds(0, 0, 240, 120);

        cardPanel.add(pnlReports);
        pnlReports.setBounds(70, 260, 220, 120);

        pnlHelpDesk.setBackground(new java.awt.Color(189, 198, 151));
        pnlHelpDesk.setLayout(null);

        lblHelpDesk.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblHelpDesk.setForeground(new java.awt.Color(30, 30, 30));
        lblHelpDesk.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHelpDesk.setText("Help Desk");
        pnlHelpDesk.add(lblHelpDesk);
        lblHelpDesk.setBounds(0, 0, 240, 120);

        cardPanel.add(pnlHelpDesk);
        pnlHelpDesk.setBounds(320, 260, 220, 120);

        pnlApprovals.setBackground(new java.awt.Color(197, 232, 183));
        pnlApprovals.setLayout(null);

        lblApprovals.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblApprovals.setForeground(new java.awt.Color(30, 30, 30));
        lblApprovals.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblApprovals.setText("Approvals");
        pnlApprovals.add(lblApprovals);
        lblApprovals.setBounds(0, 0, 240, 120);

        cardPanel.add(pnlApprovals);
        pnlApprovals.setBounds(570, 260, 220, 120);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 190, 860, 460);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblApprovals;
    private javax.swing.JLabel lblDentistMgmt;
    private javax.swing.JLabel lblHelpDesk;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblReceptionistMgmt;
    private javax.swing.JLabel lblReports;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JLabel lblUserMgmt;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlApprovals;
    private javax.swing.JPanel pnlDentistMgmt;
    private javax.swing.JPanel pnlHelpDesk;
    private javax.swing.JPanel pnlReceptionistMgmt;
    private javax.swing.JPanel pnlReports;
    private javax.swing.JPanel pnlUserMgmt;
    // End of variables declaration//GEN-END:variables
}
