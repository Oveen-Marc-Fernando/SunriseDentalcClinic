package view;

import controller.AdministrationController;
import controller.AppController;
import controller.BillingManagementController;
import controller.ProfileSaveResult;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.User;

/**
 * Administration Dashboard View - NetBeans GUI Builder compatible.
 *
 * Operations / Approvals / Reports (top-right, stacked) are this dashboard's
 * actual navigation buttons. Dentists / Patients / Appointments / Inventory
 * / Billings (bottom row) are read-only live analytics, not navigation —
 * each one's red badge is a real count read straight from the database,
 * same as every other dashboard's own tile badges (see
 * AdministrationController's getTotal* methods), not a clickable action.
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
        lblSupportIcon.setIcon(IconFactory.headset(new Color(231, 115, 36), 30));
        IconFactory.roundCorners(pnlSupportDesk, 14);
        IconFactory.roundCorners(pnlOperations, 18);
        IconFactory.roundCorners(pnlApprovals, 18);
        IconFactory.roundCorners(pnlReports, 18);
        IconFactory.roundCorners(pnlDentists, 14);
        IconFactory.roundCorners(pnlPatients, 14);
        IconFactory.roundCorners(pnlAppointments, 14);
        IconFactory.roundCorners(pnlInventory, 14);
        IconFactory.roundCorners(pnlBillings, 14);
        lblUserIcon.setIcon(IconFactory.userGlyph(java.awt.Color.WHITE, 26)); // crisp vector glyph, no backdrop — sits directly on the black pill
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        setSize(1016, 739);
        applyWelcome();
        setupBadges();
        installTrendChart();
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

    /**
     * Adds a small red count badge (e.g. "7") to each of the five analytic
     * tiles — a live count read straight from the database, same convention
     * OfficeStaff_Dashboard's own tile badges use. Runs BEFORE bindActions()
     * on purpose: these tiles are read-only (no click action at all), so
     * ordering only matters for the buttons above, but keeping setup in one
     * consistent order avoids surprises.
     */
    private void setupBadges() {
        addBadge(pnlDentists, controller.getTotalDentists());
        addBadge(pnlPatients, controller.getTotalPatients());
        addBadge(pnlAppointments, controller.getTotalAppointments());
        addBadge(pnlInventory, controller.getTotalInventory());
        addBadge(pnlBillings, controller.getTotalBillings());
    }

    /**
     * Fills the empty space to the left of the Operations/Approvals/Reports
     * buttons with a real "28 Day Trend" line chart — billed income and
     * appointments booked, both read straight from the database (see
     * AdministrationController's getTrend* methods), not sample/placeholder
     * numbers. Not a NetBeans-form-tracked component, same convention as
     * TabBarPanel/BarChartPanel elsewhere in this app — added here in code
     * because it's a hand-drawn Graphics2D component, not something the GUI
     * builder's palette has a control for.
     */
    private void installTrendChart() {
        java.util.List<String> labels = controller.getTrendDayLabels();
        java.util.List<double[]> series = java.util.Arrays.asList(
                controller.getTrendIncome(), controller.getTrendAppointments());
        java.util.List<String> seriesLabels = java.util.Arrays.asList("Income (Rs)", "Appointments");
        java.util.List<Color> seriesColors = java.util.Arrays.asList(
                new Color(231, 115, 36), new Color(0, 122, 255));

        TrendChartPanel chart = new TrendChartPanel("28 Day Trend", labels, series, seriesLabels, seriesColors,
                v -> BillingManagementController.formatCurrency(v),
                v -> String.valueOf(Math.round(v)));
        chart.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(225, 225, 225), 1, true));
        chart.setBounds(40, 20, 560, 260);
        cardPanel.add(chart);
    }

    private void addBadge(javax.swing.JPanel tile, int count) {
        javax.swing.JLabel badge = IconFactory.countBadge(count);
        if (badge == null) {
            return;
        }
        int size = badge.getWidth();
        int x = (tile.getWidth() - size) / 2;
        int y = tile.getHeight() - size - 10;
        badge.setBounds(x, y, size, size);
        tile.add(badge);
        tile.setComponentZOrder(badge, 0);
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
                                    (newUsername, newPassword) -> {
                                        ProfileSaveResult result = controller.saveProfileChanges(newUsername, newPassword);
                                        if (!result.anythingChanged() && result.isFullSuccess()) {
                                            return;
                                        }
                                        if (result.isFullSuccess()) {
                                            IconFactory.showSuccessDialog(Administration_Dashboard.this,
                                                    result.summarize(), null);
                                        } else {
                                            IconFactory.showErrorDialog(Administration_Dashboard.this, result.summarize(), null);
                                        }
                                    }, false);
                        },
                        () -> {
                            controller.logout();
                            AppController.logout(Administration_Dashboard.this);
                        });
            }
        });

        addCardAction(pnlOperations, () -> { controller.openOperations(); dispose(); new AD_OP_Dentists().setVisible(true); });
        addCardAction(pnlApprovals,  () -> { controller.openApprovals(); dispose(); new AD_APR_OfficeStaff().setVisible(true); });
        addCardAction(pnlReports,    () -> { controller.openReports(); dispose(); new AD_Reports().setVisible(true); });

        // Help Desk opens an in-place popup — doesn't navigate anywhere, so
        // it doesn't dispose this dashboard the way the buttons above would.
        addCardAction(pnlSupportDesk, this::showHelpDeskDialog);

        // The five analytic tiles are deliberately read-only — no
        // addCardAction() wiring at all, so they don't hover/highlight or
        // respond to clicks like the actual buttons above do.
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

    // =========================================================================
    // Help Desk — same in-place popup convention as OfficeStaff_Dashboard /
    // Dentist_Dashboard, opened from the pnlSupportDesk widget next to the
    // profile icon.
    // =========================================================================

    private void showHelpDeskDialog() {
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(640, 480);
        dialog.setLocationRelativeTo(this);

        javax.swing.JPanel pnlModal = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlModal.setBackground(Color.WHITE);
        pnlModal.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));

        javax.swing.JPanel pnlHeader = new javax.swing.JPanel(null);
        pnlHeader.setBackground(Color.BLACK);
        pnlHeader.setPreferredSize(new java.awt.Dimension(10, 54));

        javax.swing.JLabel lblHeaderIcon = new javax.swing.JLabel(IconFactory.headset(Color.WHITE, 22));
        lblHeaderIcon.setBounds(18, 15, 24, 24);
        pnlHeader.add(lblHeaderIcon);

        javax.swing.JLabel lblHeaderTitle = new javax.swing.JLabel("Administration — Help Desk Guide");
        lblHeaderTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(52, 0, 480, 54);
        pnlHeader.add(lblHeaderTitle);

        javax.swing.JButton btnCloseX = IconFactory.actionButton(IconFactory.cross(Color.WHITE, 12), new Color(220, 53, 69), "Close");
        btnCloseX.setBounds(640 - 44, 13, 28, 28);
        btnCloseX.addActionListener(e -> dialog.dispose());
        pnlHeader.add(btnCloseX);

        final java.awt.Point[] dragStart = {null};
        pnlHeader.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
        });
        pnlHeader.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragStart[0] == null) return;
                java.awt.Point loc = dialog.getLocation();
                dialog.setLocation(loc.x + e.getX() - dragStart[0].x, loc.y + e.getY() - dragStart[0].y);
            }
        });

        pnlModal.add(pnlHeader, java.awt.BorderLayout.NORTH);

        javax.swing.JEditorPane editor = new javax.swing.JEditorPane();
        editor.setContentType("text/html");
        editor.setEditable(false);
        editor.setText(buildHelpDeskHtml());
        editor.setCaretPosition(0);

        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(editor);
        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        IconFactory.styleScrollBar(scroll);
        pnlModal.add(scroll, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel pnlFooter = new javax.swing.JPanel();
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0));
        javax.swing.JButton btnClose = new javax.swing.JButton("OK");
        btnClose.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        btnClose.setBackground(new Color(231, 115, 36));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dialog.dispose());
        pnlFooter.add(btnClose);
        pnlModal.add(pnlFooter, java.awt.BorderLayout.SOUTH);

        dialog.getContentPane().add(pnlModal);
        dialog.setVisible(true);
    }

    private String buildHelpDeskHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Segoe UI, sans-serif; font-size:12px; padding:14px; color:#1e1e1e;'>");
        html.append("<h2 style='color:#e77324; margin-bottom:4px;'>Administration Dashboard &mdash; Quick Guide</h2>");
        html.append("<p style='color:#555;'>Answers to common questions about each section of your dashboard.</p><hr>");

        html.append(qa("What do the five tiles at the bottom mean?",
                "Dentists, Patients, Appointments, Inventory, and Billings each show a live count "
                        + "pulled straight from the database.",
                "They're read-only at a glance — click into the relevant management screen for detail or to make changes.",
                "The red number always matches what you'd count in that section's own grid right now."));

        html.append(qa("What does \"Operations\" do?",
                "Opens the day-to-day clinic operations area (in progress)."));

        html.append(qa("What does \"Approvals\" do?",
                "Review and decide on pending requests raised by Office Staff (e.g. supply or leave requests) (in progress)."));

        html.append(qa("What does \"Reports\" do?",
                "Opens clinic-wide reporting (in progress)."));

        html.append(qa("How do I edit my profile or log out?",
                "Click your profile icon in the top-right corner.",
                "Choose \"Edit Profile\" or \"Logout\" from the menu."));

        html.append("</body></html>");
        return html.toString();
    }

    /** One Q&amp;A block: a bold question followed by arrow-bulleted answer points. */
    private String qa(String question, String... answers) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p style='margin-top:14px; margin-bottom:4px;'><b>Q: ").append(question).append("</b></p>");
        sb.append("<ul style='margin-top:2px; margin-bottom:2px; padding-left:18px;'>");
        for (String a : answers) {
            sb.append("<li style='margin-bottom:3px;'>&#10148; ").append(a).append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblUserIcon = new javax.swing.JLabel();
        pnlSupportDesk = new javax.swing.JPanel();
        lblSupportIcon = new javax.swing.JLabel();
        lblSupportLabel = new javax.swing.JLabel();
        lblWelcome = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        pnlOperations = new javax.swing.JPanel();
        lblOperations = new javax.swing.JLabel();
        pnlApprovals = new javax.swing.JPanel();
        lblApprovals = new javax.swing.JLabel();
        pnlReports = new javax.swing.JPanel();
        lblReports = new javax.swing.JLabel();
        pnlDentists = new javax.swing.JPanel();
        lblDentists = new javax.swing.JLabel();
        pnlPatients = new javax.swing.JPanel();
        lblPatients = new javax.swing.JLabel();
        pnlAppointments = new javax.swing.JPanel();
        lblAppointments = new javax.swing.JLabel();
        pnlInventory = new javax.swing.JPanel();
        lblInventory = new javax.swing.JLabel();
        pnlBillings = new javax.swing.JPanel();
        lblBillings = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

        pnlSupportDesk.setBackground(new java.awt.Color(255, 255, 255));
        pnlSupportDesk.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlSupportDesk.setLayout(null);

        lblSupportIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlSupportDesk.add(lblSupportIcon);
        lblSupportIcon.setBounds(37, 10, 36, 36);

        lblSupportLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSupportLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSupportLabel.setText("HELP DESK");
        pnlSupportDesk.add(lblSupportLabel);
        lblSupportLabel.setBounds(0, 52, 110, 25);

        mainPanel.add(pnlSupportDesk);
        pnlSupportDesk.setBounds(855, 95, 110, 90);

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblWelcome.setText("Hii Administration, Welcome!!");
        mainPanel.add(lblWelcome);
        lblWelcome.setBounds(50, 120, 600, 40);

        cardPanel.setBackground(new java.awt.Color(242, 242, 242));
        cardPanel.setLayout(null);

        pnlOperations.setBackground(new java.awt.Color(150, 195, 145));
        pnlOperations.setLayout(null);

        lblOperations.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblOperations.setForeground(new java.awt.Color(30, 30, 30));
        lblOperations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOperations.setText("Operations");
        pnlOperations.add(lblOperations);
        lblOperations.setBounds(0, 0, 240, 75);

        cardPanel.add(pnlOperations);
        pnlOperations.setBounds(620, 30, 240, 75);

        pnlApprovals.setBackground(new java.awt.Color(178, 155, 205));
        pnlApprovals.setLayout(null);

        lblApprovals.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblApprovals.setForeground(new java.awt.Color(30, 30, 30));
        lblApprovals.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblApprovals.setText("Approvals");
        pnlApprovals.add(lblApprovals);
        lblApprovals.setBounds(0, 0, 240, 75);

        cardPanel.add(pnlApprovals);
        pnlApprovals.setBounds(620, 120, 240, 75);

        pnlReports.setBackground(new java.awt.Color(235, 185, 190));
        pnlReports.setLayout(null);

        lblReports.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblReports.setForeground(new java.awt.Color(30, 30, 30));
        lblReports.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReports.setText("Reports");
        pnlReports.add(lblReports);
        lblReports.setBounds(0, 0, 240, 75);

        cardPanel.add(pnlReports);
        pnlReports.setBounds(620, 210, 240, 75);

        pnlDentists.setBackground(new java.awt.Color(91, 108, 140));
        pnlDentists.setLayout(null);

        lblDentists.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDentists.setForeground(new java.awt.Color(255, 255, 255));
        lblDentists.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDentists.setText("Dentists");
        pnlDentists.add(lblDentists);
        lblDentists.setBounds(0, 15, 148, 30);

        cardPanel.add(pnlDentists);
        pnlDentists.setBounds(40, 300, 148, 90);

        pnlPatients.setBackground(new java.awt.Color(184, 166, 206));
        pnlPatients.setLayout(null);

        lblPatients.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPatients.setForeground(new java.awt.Color(30, 30, 30));
        lblPatients.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPatients.setText("Patients");
        pnlPatients.add(lblPatients);
        lblPatients.setBounds(0, 15, 148, 30);

        cardPanel.add(pnlPatients);
        pnlPatients.setBounds(208, 300, 148, 90);

        pnlAppointments.setBackground(new java.awt.Color(247, 217, 220));
        pnlAppointments.setLayout(null);

        lblAppointments.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblAppointments.setForeground(new java.awt.Color(30, 30, 30));
        lblAppointments.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAppointments.setText("Appointments");
        pnlAppointments.add(lblAppointments);
        lblAppointments.setBounds(0, 15, 148, 30);

        cardPanel.add(pnlAppointments);
        pnlAppointments.setBounds(376, 300, 148, 90);

        pnlInventory.setBackground(new java.awt.Color(189, 198, 151));
        pnlInventory.setLayout(null);

        lblInventory.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblInventory.setForeground(new java.awt.Color(30, 30, 30));
        lblInventory.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInventory.setText("Inventory");
        pnlInventory.add(lblInventory);
        lblInventory.setBounds(0, 15, 148, 30);

        cardPanel.add(pnlInventory);
        pnlInventory.setBounds(544, 300, 148, 90);

        pnlBillings.setBackground(new java.awt.Color(214, 198, 182));
        pnlBillings.setLayout(null);

        lblBillings.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblBillings.setForeground(new java.awt.Color(30, 30, 30));
        lblBillings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBillings.setText("Billings");
        pnlBillings.add(lblBillings);
        lblBillings.setBounds(0, 15, 148, 30);

        cardPanel.add(pnlBillings);
        pnlBillings.setBounds(712, 300, 148, 90);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 190, 900, 420);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblAppointments;
    private javax.swing.JLabel lblApprovals;
    private javax.swing.JLabel lblBillings;
    private javax.swing.JLabel lblDentists;
    private javax.swing.JLabel lblInventory;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblOperations;
    private javax.swing.JLabel lblPatients;
    private javax.swing.JLabel lblReports;
    private javax.swing.JLabel lblSupportIcon;
    private javax.swing.JLabel lblSupportLabel;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlAppointments;
    private javax.swing.JPanel pnlApprovals;
    private javax.swing.JPanel pnlBillings;
    private javax.swing.JPanel pnlDentists;
    private javax.swing.JPanel pnlInventory;
    private javax.swing.JPanel pnlOperations;
    private javax.swing.JPanel pnlPatients;
    private javax.swing.JPanel pnlReports;
    private javax.swing.JPanel pnlSupportDesk;
    // End of variables declaration//GEN-END:variables
}
