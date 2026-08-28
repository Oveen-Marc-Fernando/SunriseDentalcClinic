package view;

import controller.AppController;
import controller.PatientController;
import controller.ProfileSaveResult;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.User;

/**
 * Patient Dashboard View — NetBeans GUI Builder compatible.
 *
 * My Appointments / My Billings / My Profile are this dashboard's three
 * tiles; the first two show a live badge count of this patient's own
 * records (see PatientController's getMy*Count() — best-effort matched by
 * name, same convention Billing's own email lookup elsewhere in the app
 * uses, since there's no real login-to-patient-record foreign key). My
 * Profile is the only one that actually navigates — into the PA_PR_1..4
 * self-service wizard — Appointments/Billings don't have a patient-facing
 * grid built yet.
 *
 * @author oveen
 */
public class Patient_Dashboard extends javax.swing.JFrame {

    private final PatientController controller;

    public Patient_Dashboard(User user) {
        this.controller = new PatientController(user);
        initComponents();
        lblSupportIcon.setIcon(IconFactory.headset(new Color(231, 115, 36), 30));
        IconFactory.roundCorners(pnlSupportDesk, 14);
        IconFactory.roundCorners(pnlAppointments, 14);
        IconFactory.roundCorners(pnlBillings, 14);
        IconFactory.roundCorners(pnlProfile, 14);
        IconFactory.roundCorners(pnlHighlights, 14);
        lblUserIcon.setIcon(IconFactory.userGlyph(java.awt.Color.WHITE, 26));
        lblLogo.setIcon(IconFactory.brandLogo(130, 40));
        IconFactory.roundCorners(navBar, 30);
        setSize(1016, 739);
        applyWelcome();
        setupBadges();
        applyHighlights();
        bindActions();
        setLocationRelativeTo(null);
        forcePasswordChangeIfNeeded();
    }

    public Patient_Dashboard() {
        this(null);
    }

    /**
     * A login Office Staff created (Patient Management) starts with a
     * system-generated temporary password and a flag that stays set until
     * this patient picks their own — see {@link PatientController#mustChangePassword()}.
     * Deferred to after the constructor finishes (this frame isn't even
     * visible yet — the caller does {@code setVisible(true)} right after
     * constructing it) so the popup actually appears once there's a real
     * window on screen to center itself on and dim behind.
     */
    private void forcePasswordChangeIfNeeded() {
        if (!controller.mustChangePassword()) {
            return;
        }
        javax.swing.SwingUtilities.invokeLater(() -> {
            IconFactory.showSuccessDialog(this,
                    "Welcome! Please set your own password to finish setting up your account.",
                    () -> openEditProfilePopup(true));
        });
    }

    // =========================================================================
    // Custom setup
    // =========================================================================

    private void applyWelcome() {
        lblWelcome.setText(controller.getWelcomeMessage());
    }

    private void setupBadges() {
        addBadge(pnlAppointments, controller.getMyAppointmentCount());
        addBadge(pnlBillings, controller.getMyBillingCount());
        // My Profile deliberately has no badge — it's not a count of anything.
    }

    private void addBadge(javax.swing.JPanel tile, int count) {
        javax.swing.JLabel badge = IconFactory.countBadge(count);
        if (badge == null) {
            return;
        }
        int size = badge.getWidth();
        int x = (tile.getWidth() - size) / 2;
        int y = tile.getHeight() - size - 40;
        badge.setBounds(x, y, size, size);
        tile.add(badge);
        tile.setComponentZOrder(badge, 0);
    }

    // A small fixed set of dental-health tips — one is picked at random each
    // time the dashboard loads. Purely static UI content (no DB involved),
    // unlike the reminder half of this panel.
    private static final String[] TEETH_HEALTH_QUOTES = {
        "Brushing twice a day keeps the dentist's chair away.",
        "Floss like nobody's watching — your gums will thank you.",
        "A healthy smile starts with two minutes, twice a day.",
        "Sugar is a treat, not a habit — your teeth remember every bite.",
        "Regular check-ups catch small problems before they become big ones.",
        "Your toothbrush has a shelf life too — replace it every 3 months.",
    };

    /** Live "Next Appointment" reminder + a rotating teeth-health tip — the dashboard's highlights panel. */
    private void applyHighlights() {
        lblReminderValue.setText(wrapHtml(controller.getAppointmentReminder()));
        String quote = TEETH_HEALTH_QUOTES[new java.util.Random().nextInt(TEETH_HEALTH_QUOTES.length)];
        lblQuoteValue.setText(wrapHtml("“" + quote + "”"));
    }

    /** Wraps plain text in an HTML block at a fixed pixel width so a JLabel word-wraps instead of truncating with "…". */
    private static String wrapHtml(String text) {
        return "<html><div style='width:720px;'>" + text + "</div></html>";
    }

    /**
     * Shared by the navbar profile-menu's "Edit Profile" click and the
     * forced first-login password change ({@link #forcePasswordChangeIfNeeded()})
     * — same popup, same save handling, but {@code mandatory} strips the X
     * button and requires a new password before it'll let the user out.
     */
    private void openEditProfilePopup(boolean mandatory) {
        controller.editProfile();
        IconFactory.showEditProfileDialog(Patient_Dashboard.this, controller.getUsername(),
                (newUsername, newPassword) -> {
                    ProfileSaveResult result = controller.saveProfileChanges(newUsername, newPassword);
                    if (!result.anythingChanged() && result.isFullSuccess()) {
                        return;
                    }
                    if (result.isFullSuccess()) {
                        IconFactory.showSuccessDialog(Patient_Dashboard.this,
                                result.summarize(), null);
                    } else {
                        IconFactory.showErrorDialog(Patient_Dashboard.this, result.summarize(), null);
                    }
                }, mandatory);
    }

    private void bindActions() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblUserIcon.setCursor(hand);
        lblUserIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                IconFactory.showProfileMenu(Patient_Dashboard.this, lblUserIcon,
                        () -> openEditProfilePopup(false),
                        () -> {
                            controller.logout();
                            AppController.logout(Patient_Dashboard.this);
                        });
            }
        });

        addCardAction(pnlProfile, () -> {
            controller.openProfile();
            dispose();
            new PA_PR_1(controller.newProfileController()).setVisible(true);
        });

        addCardAction(pnlAppointments, () -> {
            controller.openAppointments();
            dispose();
            new PA_APO_Grid(AppController.getCurrentUser()).setVisible(true);
        });
        addCardAction(pnlBillings, () -> {
            controller.openBillings();
            dispose();
            new PA_Billing_Grid(AppController.getCurrentUser()).setVisible(true);
        });

        addCardAction(pnlSupportDesk, this::showHelpDeskDialog);
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
    // Help Desk — same in-place popup convention as Administration_Dashboard.
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

        javax.swing.JLabel lblHeaderTitle = new javax.swing.JLabel("Patient — Help Desk Guide");
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
        html.append("<h2 style='color:#e77324; margin-bottom:4px;'>Patient Dashboard &mdash; Quick Guide</h2>");
        html.append("<p style='color:#555;'>Answers to common questions about each section of your dashboard.</p><hr>");

        html.append(qa("What does the number on \"My Appointments\"/\"My Billings\" mean?",
                "A live count of your own appointments/bills on record.",
                "It only counts records that match your name — if something looks off, contact the front desk."));

        html.append(qa("What does \"My Profile\" do?",
                "Opens your 4-step profile — Personal, Contact, Medical, and Dental Information.",
                "Each step has its own \"Update\" button — you can save just that section without finishing the whole form.",
                "Your Patient ID is fixed and can't be changed."));

        html.append(qa("What is the highlights panel below the tiles showing?",
                "Left: a reminder for your soonest upcoming appointment, or an invitation to book one if you have none.",
                "Right: a rotating tip for healthy teeth — just a friendly reminder, refreshes each time you open the dashboard."));

        html.append(qa("How do I edit my login or log out?",
                "Click your profile icon in the top-right corner.",
                "Choose \"Edit Profile\" or \"Logout\" from the menu."));

        html.append("</body></html>");
        return html.toString();
    }

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
        pnlAppointments = new javax.swing.JPanel();
        lblAppointments = new javax.swing.JLabel();
        pnlBillings = new javax.swing.JPanel();
        lblBillings = new javax.swing.JLabel();
        pnlProfile = new javax.swing.JPanel();
        lblProfile = new javax.swing.JLabel();
        pnlHighlights = new javax.swing.JPanel();
        lblReminderTitle = new javax.swing.JLabel();
        lblReminderValue = new javax.swing.JLabel();
        sepHighlights = new javax.swing.JSeparator();
        lblQuoteTitle = new javax.swing.JLabel();
        lblQuoteValue = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sunrise Dental — Patient");
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
        pnlSupportDesk.setBounds(860, 120, 110, 90);

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblWelcome.setText("Hii Patient, Welcome!!");
        mainPanel.add(lblWelcome);
        lblWelcome.setBounds(50, 120, 600, 40);

        cardPanel.setBackground(new java.awt.Color(242, 242, 242));
        cardPanel.setLayout(null);

        pnlAppointments.setBackground(new java.awt.Color(178, 155, 205));
        pnlAppointments.setLayout(null);

        lblAppointments.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblAppointments.setForeground(new java.awt.Color(30, 30, 30));
        lblAppointments.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAppointments.setText("My Appointments");
        pnlAppointments.add(lblAppointments);
        lblAppointments.setBounds(0, 25, 230, 50);

        cardPanel.add(pnlAppointments);
        pnlAppointments.setBounds(60, 30, 230, 140);

        pnlBillings.setBackground(new java.awt.Color(235, 185, 190));
        pnlBillings.setLayout(null);

        lblBillings.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblBillings.setForeground(new java.awt.Color(30, 30, 30));
        lblBillings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBillings.setText("My Billings");
        pnlBillings.add(lblBillings);
        lblBillings.setBounds(0, 25, 230, 50);

        cardPanel.add(pnlBillings);
        pnlBillings.setBounds(335, 30, 230, 140);

        pnlProfile.setBackground(new java.awt.Color(150, 195, 145));
        pnlProfile.setLayout(null);

        lblProfile.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblProfile.setForeground(new java.awt.Color(30, 30, 30));
        lblProfile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProfile.setText("My Profile");
        pnlProfile.add(lblProfile);
        lblProfile.setBounds(0, 45, 230, 50);

        cardPanel.add(pnlProfile);
        pnlProfile.setBounds(610, 30, 230, 140);

        pnlHighlights.setBackground(new java.awt.Color(189, 198, 151));
        pnlHighlights.setLayout(null);

        lblReminderTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblReminderTitle.setForeground(new java.awt.Color(30, 30, 30));
        lblReminderTitle.setText("Next Appointment:");
        pnlHighlights.add(lblReminderTitle);
        lblReminderTitle.setBounds(25, 8, 300, 20);

        lblReminderValue.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblReminderValue.setForeground(new java.awt.Color(220, 53, 69));
        lblReminderValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblReminderValue.setText("<html><div style='width:720px;'>You have no upcoming appointments — book one anytime from My Appointments!</div></html>");
        pnlHighlights.add(lblReminderValue);
        lblReminderValue.setBounds(25, 28, 730, 36);

        sepHighlights.setOrientation(javax.swing.SwingConstants.HORIZONTAL);
        sepHighlights.setForeground(new java.awt.Color(160, 168, 128));
        pnlHighlights.add(sepHighlights);
        sepHighlights.setBounds(25, 68, 730, 2);

        lblQuoteTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblQuoteTitle.setForeground(new java.awt.Color(30, 30, 30));
        lblQuoteTitle.setText("Tip for Healthy Teeth:");
        pnlHighlights.add(lblQuoteTitle);
        lblQuoteTitle.setBounds(25, 78, 300, 20);

        lblQuoteValue.setFont(new java.awt.Font("Segoe UI", 2, 13)); // NOI18N
        lblQuoteValue.setForeground(new java.awt.Color(70, 70, 70));
        lblQuoteValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblQuoteValue.setText("<html><div style='width:720px;'>“Brushing twice a day keeps the dentist's chair away.”</div></html>");
        pnlHighlights.add(lblQuoteValue);
        lblQuoteValue.setBounds(25, 98, 730, 36);

        cardPanel.add(pnlHighlights);
        pnlHighlights.setBounds(60, 210, 780, 140);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 250, 900, 380);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblAppointments;
    private javax.swing.JLabel lblBillings;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblProfile;
    private javax.swing.JLabel lblQuoteTitle;
    private javax.swing.JLabel lblQuoteValue;
    private javax.swing.JLabel lblReminderTitle;
    private javax.swing.JLabel lblReminderValue;
    private javax.swing.JLabel lblSupportIcon;
    private javax.swing.JLabel lblSupportLabel;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlAppointments;
    private javax.swing.JPanel pnlBillings;
    private javax.swing.JPanel pnlHighlights;
    private javax.swing.JPanel pnlProfile;
    private javax.swing.JPanel pnlSupportDesk;
    private javax.swing.JSeparator sepHighlights;
    // End of variables declaration//GEN-END:variables
}
