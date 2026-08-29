package view;

import controller.AppController;
import controller.DentistController;
import controller.ProfileSaveResult;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.User;

/**
 * Dentist Dashboard View - NetBeans GUI Builder compatible.
 *
 * @author oveen
 */
public class Dentist_Dashboard extends javax.swing.JFrame {

    private final DentistController controller;

    // =========================================================================
    // Constructors
    // =========================================================================

    public Dentist_Dashboard(User user) {
        this.controller = new DentistController(user);
        initComponents();
        lblUserIcon.setIcon(IconFactory.userGlyph(java.awt.Color.WHITE, 26)); // crisp vector glyph, no backdrop — sits directly on the black pill
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        lblSupportIcon.setIcon(IconFactory.headset(new Color(231, 115, 36), 30));
        IconFactory.roundCorners(pnlSupportDesk, 14);
        lblAnalysisIcon.setIcon(IconFactory.barChart(new Color(0, 122, 255), 30));
        IconFactory.roundCorners(pnlAnalysis, 14);
        setSize(1016, 739);
        applyWelcome();
        setupBadges();
        bindActions();
        setLocationRelativeTo(null);
        forcePasswordChangeIfNeeded();
    }

    /**
     * A login Office Staff created (Dentist Management) starts with a
     * system-generated temporary password and a flag that stays set until
     * this dentist picks their own — see {@link DentistController#mustChangePassword()}.
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

    public Dentist_Dashboard() {
        this(null);
    }

    // =========================================================================
    // Custom setup
    // =========================================================================

    private void applyWelcome() {
        lblWelcome.setText(controller.getWelcomeMessage());
    }

    /**
     * Adds a small red count badge (e.g. "2") to each tile that has a live
     * count behind it. Runs BEFORE bindActions() on purpose: addCardAction()
     * wires its hover/click listeners onto whatever children a tile already
     * has, so the badge needs to exist first to be included in that wiring.
     */
    private void setupBadges() {
        addBadge(pnlMyAppointments, controller.getMyAppointments());
        addBadge(pnlMyPatients, controller.getMyPatients());
        addBadge(pnlRequestSupplies, controller.getRequestSupplies());
        addBadge(pnlRequestLeaves, controller.getRequestLeaves());
    }

    private void addBadge(javax.swing.JPanel tile, int count) {
        javax.swing.JLabel badge = IconFactory.countBadge(count);
        if (badge == null) {
            return;
        }
        int size = badge.getWidth();
        int x = (tile.getWidth() - size) / 2;
        int y = tile.getHeight() - size - 14;
        badge.setBounds(x, y, size, size);
        tile.add(badge);
        tile.setComponentZOrder(badge, 0);
    }

    /**
     * Shared by the navbar profile-menu's "Edit Profile" click and the
     * forced first-login password change ({@link #forcePasswordChangeIfNeeded()})
     * — same popup, same save handling, but {@code mandatory} strips the X
     * button and requires a new password before it'll let the user out.
     */
    private void openEditProfilePopup(boolean mandatory) {
        controller.editProfile();
        IconFactory.showEditProfileDialog(Dentist_Dashboard.this, controller.getUsername(),
                (newUsername, newPassword) -> {
                    ProfileSaveResult result = controller.saveProfileChanges(newUsername, newPassword);
                    if (!result.anythingChanged() && result.isFullSuccess()) {
                        return;
                    }
                    if (result.isFullSuccess()) {
                        IconFactory.showSuccessDialog(Dentist_Dashboard.this,
                                result.summarize(), null);
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(Dentist_Dashboard.this,
                                result.summarize(), "Update Failed", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }, mandatory);
    }

    private void bindActions() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblUserIcon.setCursor(hand);
        lblUserIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                IconFactory.showProfileMenu(Dentist_Dashboard.this, lblUserIcon,
                        () -> openEditProfilePopup(false),
                        () -> {
                            controller.logout();
                            AppController.logout(Dentist_Dashboard.this);
                        });
            }
        });

        // Every tile now has a real view to navigate to, so — same rule as
        // every other grid's own "Back" button — dispose this dashboard
        // before opening the next one, or the instance we're leaving behind
        // never gets disposed and resurfaces later.
        addCardAction(pnlMyAppointments,  () -> { dispose(); controller.openMyAppointments(); });
        addCardAction(pnlMyPatients,      () -> { dispose(); controller.openMyPatients(); });
        addCardAction(pnlMySchedule,      () -> { dispose(); controller.openMySchedule(); });
        addCardAction(pnlMyProfile,       () -> { dispose(); controller.openMyProfile(); });
        addCardAction(pnlRequestSupplies, () -> { dispose(); controller.openRequestSupplies(); });
        addCardAction(pnlRequestLeaves,   () -> { dispose(); controller.openRequestLeaves(); });

        // The Help Desk and Analysis widgets don't navigate anywhere — they
        // open in-place popups, so neither disposes this dashboard.
        addCardAction(pnlSupportDesk, this::showHelpDeskDialog);
        addCardAction(pnlAnalysis, this::showAnalysisDialog);
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
    // Help Desk — a scrollable Q&A guide covering every tile on this
    // dashboard, opened from the pnlSupportDesk widget next to the profile
    // icon.
    // =========================================================================

    private void showHelpDeskDialog() {
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, true);
        dialog.setUndecorated(true); // no OS title bar — we draw our own header below
        dialog.setSize(640, 560);
        dialog.setLocationRelativeTo(this);

        javax.swing.JPanel pnlModal = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlModal.setBackground(Color.WHITE);
        pnlModal.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));

        // Header — same black navbar look every other screen uses, with our
        // own close glyph instead of the OS window's X.
        javax.swing.JPanel pnlHeader = new javax.swing.JPanel(null);
        pnlHeader.setBackground(Color.BLACK);
        pnlHeader.setPreferredSize(new java.awt.Dimension(10, 54));

        javax.swing.JLabel lblHeaderIcon = new javax.swing.JLabel(IconFactory.headset(Color.WHITE, 22));
        lblHeaderIcon.setBounds(18, 15, 24, 24);
        pnlHeader.add(lblHeaderIcon);

        javax.swing.JLabel lblHeaderTitle = new javax.swing.JLabel("Dentist — Help Desk Guide");
        lblHeaderTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(52, 0, 480, 54);
        pnlHeader.add(lblHeaderTitle);

        javax.swing.JButton btnCloseX = IconFactory.actionButton(IconFactory.cross(Color.WHITE, 12), new Color(220, 53, 69), "Close");
        btnCloseX.setBounds(640 - 44, 13, 28, 28);
        btnCloseX.addActionListener(e -> dialog.dispose());
        pnlHeader.add(btnCloseX);

        // Undecorated windows lose the native title bar's drag-to-move, so
        // dragging the custom header moves the dialog instead.
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

        // Body — scrollable Q&A content.
        javax.swing.JEditorPane editor = new javax.swing.JEditorPane();
        editor.setContentType("text/html");
        editor.setEditable(false);
        editor.setText(buildHelpDeskHtml());
        editor.setCaretPosition(0);

        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(editor);
        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        IconFactory.styleScrollBar(scroll); // flat thumb, no arrow buttons — not the OS default look
        pnlModal.add(scroll, java.awt.BorderLayout.CENTER);

        // Footer
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
        html.append("<h2 style='color:#e77324; margin-bottom:4px;'>Dentist Dashboard &mdash; Quick Guide</h2>");
        html.append("<p style='color:#555;'>Answers to common questions about each section of your dashboard.</p><hr>");

        html.append(qa("What does \"My Appointments\" do?",
                "View every appointment booked with you.",
                "See patient name, date, time, and treatment type at a glance.",
                "The badge shows how many appointments are coming up."));

        html.append(qa("What does \"My Patients\" do?",
                "Browse the list of patients currently under your care.",
                "Open a patient's record to review their dental history.",
                "The badge shows how many patients are on your list."));

        html.append(qa("What does \"My Schedule\" do?",
                "See your working days, hours, and assigned room for the week.",
                "Check for any schedule changes made by Administration."));

        html.append(qa("What does \"Request Supplies\" do?",
                "Request dental tools or materials you're running low on.",
                "Track the status of supply requests you've already sent.",
                "The badge shows how many requests are still pending."));

        html.append(qa("What does \"My Profile\" do?",
                "View and update your professional details (qualifications, specialization, contact info).",
                "Check your employment status and consultation fee on record."));

        html.append(qa("What does \"Request Leaves\" do?",
                "Submit a leave request for Administration to review.",
                "Check whether your recent leave requests were approved.",
                "The badge shows how many requests are still pending."));

        html.append(qa("What do the red numbers on each tile mean?",
                "Each badge is a live count pulled from that section's own records.",
                "It always matches what you'd see after clicking into that section."));

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

    // =========================================================================
    // Analysis — a bar chart comparing this dentist's own live counts
    // (Appointments / Patients / Supply requests / Leave requests), opened
    // from the pnlAnalysis widget next to Help Desk. Uses the same
    // hand-drawn BarChartPanel as the Office Staff dashboard's Analysis
    // popup — see that class for how it's drawn.
    // =========================================================================

    private void showAnalysisDialog() {
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(640, 520);
        dialog.setLocationRelativeTo(this);

        javax.swing.JPanel pnlModal = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlModal.setBackground(Color.WHITE);
        pnlModal.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));

        javax.swing.JPanel pnlHeader = new javax.swing.JPanel(null);
        pnlHeader.setBackground(Color.BLACK);
        pnlHeader.setPreferredSize(new java.awt.Dimension(10, 54));

        javax.swing.JLabel lblHeaderIcon = new javax.swing.JLabel(IconFactory.barChart(Color.WHITE, 20));
        lblHeaderIcon.setBounds(18, 15, 24, 24);
        pnlHeader.add(lblHeaderIcon);

        javax.swing.JLabel lblHeaderTitle = new javax.swing.JLabel("Dentist — Dashboard Analysis");
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

        javax.swing.JLabel lblSubtitle = new javax.swing.JLabel(
                "Your own live record count — the same numbers as each tile's red badge.");
        lblSubtitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 100, 100));
        lblSubtitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 20, 0, 20));

        javax.swing.JPanel pnlTop = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(pnlHeader, java.awt.BorderLayout.NORTH);
        pnlTop.add(lblSubtitle, java.awt.BorderLayout.SOUTH);
        pnlModal.add(pnlTop, java.awt.BorderLayout.NORTH);

        // Only the tiles that actually carry a live count — My Schedule and
        // My Profile have no badge (no record count behind them), so they're
        // left out here the same way they're left out of setupBadges().
        java.util.List<String> labels = java.util.Arrays.asList(
                "Appointments", "Patients", "Supplies", "Leaves");
        java.util.List<Integer> counts = java.util.Arrays.asList(
                controller.getMyAppointments(), controller.getMyPatients(),
                controller.getRequestSupplies(), controller.getRequestLeaves());
        java.util.List<Color> colors = java.util.Arrays.asList(
                new Color(91, 108, 140), new Color(163, 141, 194),
                new Color(190, 165, 133), new Color(140, 200, 115));

        BarChartPanel chart = new BarChartPanel(labels, counts, colors);
        chart.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        pnlModal.add(chart, java.awt.BorderLayout.CENTER);

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

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblUserIcon = new javax.swing.JLabel();
        lblWelcome = new javax.swing.JLabel();
        pnlSupportDesk = new javax.swing.JPanel();
        lblSupportIcon = new javax.swing.JLabel();
        lblSupportLabel = new javax.swing.JLabel();
        pnlAnalysis = new javax.swing.JPanel();
        lblAnalysisIcon = new javax.swing.JLabel();
        lblAnalysisLabel = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        pnlMyAppointments = new javax.swing.JPanel();
        lblMyAppointments = new javax.swing.JLabel();
        pnlMyPatients = new javax.swing.JPanel();
        lblMyPatients = new javax.swing.JLabel();
        pnlMySchedule = new javax.swing.JPanel();
        lblMySchedule = new javax.swing.JLabel();
        pnlRequestSupplies = new javax.swing.JPanel();
        lblRequestSupplies = new javax.swing.JLabel();
        pnlMyProfile = new javax.swing.JPanel();
        lblMyProfile = new javax.swing.JLabel();
        pnlRequestLeaves = new javax.swing.JPanel();
        lblRequestLeaves = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sunrise Dental — Dentist");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 170, 40);

        lblUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/login_scaled.png"))); // NOI18N
        lblUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        navBar.add(lblUserIcon);
        lblUserIcon.setBounds(850, 10, 40, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 3, 28)); // NOI18N
        lblWelcome.setText("Hii Dentist, Welcome!!");
        mainPanel.add(lblWelcome);
        lblWelcome.setBounds(50, 120, 600, 40);

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

        pnlAnalysis.setBackground(new java.awt.Color(255, 255, 255));
        pnlAnalysis.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlAnalysis.setLayout(null);

        lblAnalysisIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlAnalysis.add(lblAnalysisIcon);
        lblAnalysisIcon.setBounds(45, 10, 36, 36);

        lblAnalysisLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblAnalysisLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAnalysisLabel.setText("ANALYSIS");
        pnlAnalysis.add(lblAnalysisLabel);
        lblAnalysisLabel.setBounds(0, 52, 110, 25);

        mainPanel.add(pnlAnalysis);
        pnlAnalysis.setBounds(740, 95, 110, 90);

        cardPanel.setBackground(new java.awt.Color(242, 242, 242));
        cardPanel.setLayout(null);

        pnlMyAppointments.setBackground(new java.awt.Color(91, 108, 140));
        pnlMyAppointments.setLayout(null);

        lblMyAppointments.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblMyAppointments.setForeground(new java.awt.Color(30, 30, 30));
        lblMyAppointments.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMyAppointments.setText("My Appointments");
        pnlMyAppointments.add(lblMyAppointments);
        lblMyAppointments.setBounds(0, 0, 220, 100);

        cardPanel.add(pnlMyAppointments);
        pnlMyAppointments.setBounds(70, 80, 220, 120);

        pnlMyPatients.setBackground(new java.awt.Color(184, 166, 206));
        pnlMyPatients.setLayout(null);

        lblMyPatients.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblMyPatients.setForeground(new java.awt.Color(30, 30, 30));
        lblMyPatients.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMyPatients.setText("My Patients");
        pnlMyPatients.add(lblMyPatients);
        lblMyPatients.setBounds(0, 0, 220, 100);

        cardPanel.add(pnlMyPatients);
        pnlMyPatients.setBounds(320, 80, 220, 120);

        pnlMySchedule.setBackground(new java.awt.Color(247, 217, 220));
        pnlMySchedule.setLayout(null);

        lblMySchedule.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblMySchedule.setForeground(new java.awt.Color(30, 30, 30));
        lblMySchedule.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMySchedule.setText("My Schedule");
        pnlMySchedule.add(lblMySchedule);
        lblMySchedule.setBounds(0, 0, 220, 120);

        cardPanel.add(pnlMySchedule);
        pnlMySchedule.setBounds(570, 80, 220, 120);

        pnlRequestSupplies.setBackground(new java.awt.Color(214, 198, 182));
        pnlRequestSupplies.setLayout(null);

        lblRequestSupplies.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblRequestSupplies.setForeground(new java.awt.Color(30, 30, 30));
        lblRequestSupplies.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRequestSupplies.setText("Request Supplies");
        pnlRequestSupplies.add(lblRequestSupplies);
        lblRequestSupplies.setBounds(0, 0, 220, 100);

        cardPanel.add(pnlRequestSupplies);
        pnlRequestSupplies.setBounds(70, 260, 220, 120);

        pnlMyProfile.setBackground(new java.awt.Color(189, 198, 151));
        pnlMyProfile.setLayout(null);

        lblMyProfile.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblMyProfile.setForeground(new java.awt.Color(30, 30, 30));
        lblMyProfile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMyProfile.setText("My Profile");
        pnlMyProfile.add(lblMyProfile);
        lblMyProfile.setBounds(0, 0, 220, 120);

        cardPanel.add(pnlMyProfile);
        pnlMyProfile.setBounds(320, 260, 220, 120);

        pnlRequestLeaves.setBackground(new java.awt.Color(197, 232, 183));
        pnlRequestLeaves.setLayout(null);

        lblRequestLeaves.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblRequestLeaves.setForeground(new java.awt.Color(30, 30, 30));
        lblRequestLeaves.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRequestLeaves.setText("Request Leaves");
        pnlRequestLeaves.add(lblRequestLeaves);
        lblRequestLeaves.setBounds(0, 0, 220, 100);

        cardPanel.add(pnlRequestLeaves);
        pnlRequestLeaves.setBounds(570, 260, 220, 120);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 190, 860, 460);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JPanel pnlAnalysis;
    private javax.swing.JLabel lblAnalysisIcon;
    private javax.swing.JLabel lblAnalysisLabel;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMyAppointments;
    private javax.swing.JLabel lblMyPatients;
    private javax.swing.JLabel lblMyProfile;
    private javax.swing.JLabel lblMySchedule;
    private javax.swing.JLabel lblRequestLeaves;
    private javax.swing.JLabel lblRequestSupplies;
    private javax.swing.JLabel lblSupportIcon;
    private javax.swing.JLabel lblSupportLabel;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlMyAppointments;
    private javax.swing.JPanel pnlMyPatients;
    private javax.swing.JPanel pnlMyProfile;
    private javax.swing.JPanel pnlMySchedule;
    private javax.swing.JPanel pnlRequestLeaves;
    private javax.swing.JPanel pnlRequestSupplies;
    private javax.swing.JPanel pnlSupportDesk;
    // End of variables declaration//GEN-END:variables
}
