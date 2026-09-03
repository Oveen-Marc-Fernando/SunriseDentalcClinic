package view;

import controller.AppController;
import controller.OfficeStaffController;
import controller.ProfileSaveResult;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.User;

/**
 * Office Staff Dashboard View - NetBeans GUI Builder compatible.
 * Manages: Dentist Management, Patient Management, Appointment Management,
 * Billings, Help Desk, Approvals.
 *
 * @author oveen
 */
public class OfficeStaff_Dashboard extends javax.swing.JFrame {

    private final OfficeStaffController controller;

    // =========================================================================
    // Constructors
    // =========================================================================

    public OfficeStaff_Dashboard(User user) {
        this.controller = new OfficeStaffController(user);
        initComponents();
        lblSupportIcon.setIcon(IconFactory.headset(new Color(231, 115, 36), 30));
        lblAnalysisIcon.setIcon(IconFactory.barChart(new Color(0, 122, 255), 30));
        setSize(1016, 739);
        applyWelcome();
        IconFactory.roundCorners(pnlDentistMgmt, 18);
        IconFactory.roundCorners(pnlPatientMgmt, 18);
        IconFactory.roundCorners(pnlAppointmentMgmt, 18);
        IconFactory.roundCorners(pnlBillings, 18);
        IconFactory.roundCorners(pnlHelpDesk, 18);
        IconFactory.roundCorners(pnlApprovals, 18);
        IconFactory.roundCorners(pnlSupportDesk, 14);
        IconFactory.roundCorners(pnlAnalysis, 14);
        lblUserIcon.setIcon(IconFactory.userGlyph(java.awt.Color.WHITE, 26)); // crisp vector glyph, no backdrop — sits directly on the black pill
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        setupBadges();
        bindActions();
        setLocationRelativeTo(null);
    }

    public OfficeStaff_Dashboard() {
        this(null);
    }

    // =========================================================================
    // Custom setup
    // =========================================================================

    private void applyWelcome() {
        lblWelcome.setText(controller.getWelcomeMessage());
    }

    /**
     * Adds a small red count badge (e.g. "7") to each tile that has a grid
     * backing it, showing the same record count you'd see after clicking
     * into that grid — see OfficeStaffController's getters, which read
     * straight from each grid's static getRecordCount().
     *
     * Runs BEFORE bindActions() on purpose: addCardAction() wires its
     * hover/click listeners onto whatever children a tile already has, so
     * the badge needs to exist first to be included in that wiring.
     */
    private void setupBadges() {
        addBadge(pnlDentistMgmt, controller.getTotalDentists());
        addBadge(pnlPatientMgmt, controller.getTotalPatients());
        addBadge(pnlAppointmentMgmt, controller.getPendingAppointments());
        addBadge(pnlBillings, controller.getPendingBillings());
        addBadge(pnlHelpDesk, controller.getOpenHelpDesk());
        addBadge(pnlApprovals, controller.getPendingApprovals());
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

    private void bindActions() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblUserIcon.setCursor(hand);
        lblUserIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                IconFactory.showProfileMenu(OfficeStaff_Dashboard.this, lblUserIcon,
                        () -> {
                            controller.editProfile();
                            IconFactory.showEditProfileDialog(OfficeStaff_Dashboard.this, controller.getUsername(),
                                    (newUsername, newPassword) -> {
                                        ProfileSaveResult result = controller.saveProfileChanges(newUsername, newPassword);
                                        if (!result.anythingChanged() && result.isFullSuccess()) {
                                            return;
                                        }
                                        if (result.isFullSuccess()) {
                                            IconFactory.showSuccessDialog(OfficeStaff_Dashboard.this,
                                                    result.summarize(), null);
                                        } else {
                                            javax.swing.JOptionPane.showMessageDialog(OfficeStaff_Dashboard.this,
                                                    result.summarize(), "Update Failed", javax.swing.JOptionPane.ERROR_MESSAGE);
                                        }
                                    }, false);
                        },
                        () -> {
                            controller.logout();
                            AppController.logout(OfficeStaff_Dashboard.this);
                        });
            }
        });

        // Every grid's own "Back" button disposes itself before opening a
        // fresh dashboard (see e.g. OS_BM_Grid.btnBackActionPerformed) — so
        // navigating the other way has to follow the same rule, or the
        // dashboard instance we're leaving behind never gets disposed and
        // resurfaces later (e.g. behind the Public Dashboard after Logout).
        addCardAction(pnlDentistMgmt,     () -> { dispose(); controller.openDentistManagement(); });
        addCardAction(pnlPatientMgmt,     () -> { dispose(); controller.openPatientManagement(); });
        addCardAction(pnlAppointmentMgmt, () -> { dispose(); controller.openAppointmentManagement(); });
        addCardAction(pnlBillings,        () -> { dispose(); controller.openBillings(); });
        addCardAction(pnlHelpDesk,        () -> { dispose(); controller.openHelpDesk(); });
        addCardAction(pnlApprovals,       () -> { dispose(); controller.openApprovals(); });

        // The Help Desk and Analysis widgets don't navigate anywhere — they
        // open in-place popups, so neither disposes this dashboard the way
        // the tiles above do.
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

        javax.swing.JLabel lblHeaderTitle = new javax.swing.JLabel("Receptionist — Help Desk Guide");
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

        // Body — same scrollable Q&A content as before.
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
        html.append("<h2 style='color:#e77324; margin-bottom:4px;'>Receptionist Dashboard &mdash; Quick Guide</h2>");
        html.append("<p style='color:#555;'>Answers to common questions about each section of your dashboard.</p><hr>");

        html.append(qa("What does \"Dentist Management\" do?",
                "Add, update, or remove dentist profiles.",
                "View each dentist's specialization and contact details.",
                "The red badge shows how many dentists are on record."));

        html.append(qa("What does \"Patient Management\" do?",
                "Register new patients through a 4-step form (Personal, Contact, Medical, Dental info).",
                "Search existing patients and update their records.",
                "See each patient's last visit date at a glance."));

        html.append(qa("What does \"Appointment Management\" do?",
                "Book a new appointment for a patient with a chosen dentist, date and time.",
                "View, edit, or cancel upcoming appointments.",
                "The badge shows how many appointments are pending."));

        html.append(qa("What does \"Billing Management\" do?",
                "Click \"Generate Bill\" to create an invoice in 4 steps: appointment charges &rarr; clinical charges &rarr; medicine charges &rarr; total.",
                "Click \"Add Services\" to manage the clinic's service price list.",
                "Print, edit, or delete any bill from the billing table."));

        html.append(qa("What does \"Inventory Management\" do?",
                "Track medical supplies, dental tools, and medication stock.",
                "Add new products with supplier and pricing details.",
                "The badge flags how many items are on record."));

        html.append(qa("What does \"Approval Management\" do?",
                "Review pending requests awaiting sign-off (e.g. supply or leave requests).",
                "Approve or decline each request with one click.",
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
    // Analysis — a bar chart comparing every tile's live record count,
    // opened from the pnlAnalysis widget next to Help Desk. Drawn by hand
    // (Graphics2D, no charting library) the same way every other icon/graphic
    // in this app is built, via a small self-contained BarChartPanel below.
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

        javax.swing.JLabel lblHeaderTitle = new javax.swing.JLabel("Receptionist — Dashboard Analysis");
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
                "Live record count in every module — the same numbers as each tile's red badge.");
        lblSubtitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 100, 100));
        lblSubtitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 20, 0, 20));

        javax.swing.JPanel pnlTop = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(pnlHeader, java.awt.BorderLayout.NORTH);
        pnlTop.add(lblSubtitle, java.awt.BorderLayout.SOUTH);
        pnlModal.add(pnlTop, java.awt.BorderLayout.NORTH);

        java.util.List<String> labels = java.util.Arrays.asList(
                "Dentist", "Patient", "Appointment", "Billing", "Inventory", "Approval");
        java.util.List<Integer> counts = java.util.Arrays.asList(
                controller.getTotalDentists(), controller.getTotalPatients(), controller.getPendingAppointments(),
                controller.getPendingBillings(), controller.getOpenHelpDesk(), controller.getPendingApprovals());
        java.util.List<Color> colors = java.util.Arrays.asList(
                new Color(91, 108, 140), new Color(163, 141, 194), new Color(233, 178, 184),
                new Color(190, 165, 133), new Color(150, 165, 100), new Color(140, 200, 115));

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
        pnlSupportDesk = new javax.swing.JPanel();
        lblSupportIcon = new javax.swing.JLabel();
        lblSupportLabel = new javax.swing.JLabel();
        pnlAnalysis = new javax.swing.JPanel();
        lblAnalysisIcon = new javax.swing.JLabel();
        lblAnalysisLabel = new javax.swing.JLabel();
        lblWelcome = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        pnlDentistMgmt = new javax.swing.JPanel();
        lblDentistMgmt = new javax.swing.JLabel();
        pnlPatientMgmt = new javax.swing.JPanel();
        lblPatientMgmt1 = new javax.swing.JLabel();
        pnlAppointmentMgmt = new javax.swing.JPanel();
        lblAppointmentMgmt = new javax.swing.JLabel();
        pnlBillings = new javax.swing.JPanel();
        lblBillings = new javax.swing.JLabel();
        pnlHelpDesk = new javax.swing.JPanel();
        lblPatientMgmt = new javax.swing.JLabel();
        pnlApprovals = new javax.swing.JPanel();
        lblApprovals = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sunrise Dental - Receptionist");
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

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 3, 28)); // NOI18N
        lblWelcome.setText("Hii Receptionist, Welcome!!");
        mainPanel.add(lblWelcome);
        lblWelcome.setBounds(50, 120, 600, 40);

        cardPanel.setBackground(new java.awt.Color(242, 242, 242));
        cardPanel.setLayout(null);

        pnlDentistMgmt.setBackground(new java.awt.Color(91, 108, 140));
        pnlDentistMgmt.setLayout(null);

        lblDentistMgmt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblDentistMgmt.setForeground(new java.awt.Color(30, 30, 30));
        lblDentistMgmt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDentistMgmt.setText("<html><div style='text-align:center'>Dentist<br>Management</div></html>");
        pnlDentistMgmt.add(lblDentistMgmt);
        lblDentistMgmt.setBounds(0, 0, 220, 85);

        cardPanel.add(pnlDentistMgmt);
        pnlDentistMgmt.setBounds(70, 80, 220, 120);

        pnlPatientMgmt.setBackground(new java.awt.Color(184, 166, 206));
        pnlPatientMgmt.setLayout(null);

        lblPatientMgmt1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblPatientMgmt1.setForeground(new java.awt.Color(30, 30, 30));
        lblPatientMgmt1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPatientMgmt1.setText("<html><div style='text-align:center'>Patient<br>Management</div></html>");
        pnlPatientMgmt.add(lblPatientMgmt1);
        lblPatientMgmt1.setBounds(0, 0, 220, 85);

        cardPanel.add(pnlPatientMgmt);
        pnlPatientMgmt.setBounds(320, 80, 220, 120);

        pnlAppointmentMgmt.setBackground(new java.awt.Color(247, 217, 220));
        pnlAppointmentMgmt.setLayout(null);

        lblAppointmentMgmt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblAppointmentMgmt.setForeground(new java.awt.Color(30, 30, 30));
        lblAppointmentMgmt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAppointmentMgmt.setText("<html><div style='text-align:center'>Appointment<br>Management</div></html>");
        pnlAppointmentMgmt.add(lblAppointmentMgmt);
        lblAppointmentMgmt.setBounds(0, 0, 220, 85);

        cardPanel.add(pnlAppointmentMgmt);
        pnlAppointmentMgmt.setBounds(570, 80, 220, 120);

        pnlBillings.setBackground(new java.awt.Color(214, 198, 182));
        pnlBillings.setLayout(null);

        lblBillings.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblBillings.setForeground(new java.awt.Color(30, 30, 30));
        lblBillings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBillings.setText("<html><div style='text-align:center'>Billing<br>Management</div></html>");
        pnlBillings.add(lblBillings);
        lblBillings.setBounds(0, 0, 220, 90);

        cardPanel.add(pnlBillings);
        pnlBillings.setBounds(70, 260, 220, 120);

        pnlHelpDesk.setBackground(new java.awt.Color(189, 198, 151));
        pnlHelpDesk.setLayout(null);

        lblPatientMgmt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblPatientMgmt.setForeground(new java.awt.Color(30, 30, 30));
        lblPatientMgmt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPatientMgmt.setText("<html><div style='text-align:center'>Inventory<br>Management</div></html>");
        pnlHelpDesk.add(lblPatientMgmt);
        lblPatientMgmt.setBounds(0, 0, 220, 85);

        cardPanel.add(pnlHelpDesk);
        pnlHelpDesk.setBounds(320, 260, 220, 120);

        pnlApprovals.setBackground(new java.awt.Color(197, 232, 183));
        pnlApprovals.setLayout(null);

        lblApprovals.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblApprovals.setForeground(new java.awt.Color(30, 30, 30));
        lblApprovals.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblApprovals.setText("<html><div style='text-align:center'>Approval<br>Management</div></html>");
        pnlApprovals.add(lblApprovals);
        lblApprovals.setBounds(0, 0, 220, 85);

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
    private javax.swing.JPanel pnlAnalysis;
    private javax.swing.JLabel lblAnalysisIcon;
    private javax.swing.JLabel lblAnalysisLabel;
    private javax.swing.JLabel lblAppointmentMgmt;
    private javax.swing.JLabel lblApprovals;
    private javax.swing.JLabel lblBillings;
    private javax.swing.JLabel lblDentistMgmt;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblPatientMgmt;
    private javax.swing.JLabel lblPatientMgmt1;
    private javax.swing.JLabel lblSupportIcon;
    private javax.swing.JLabel lblSupportLabel;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlAppointmentMgmt;
    private javax.swing.JPanel pnlApprovals;
    private javax.swing.JPanel pnlBillings;
    private javax.swing.JPanel pnlDentistMgmt;
    private javax.swing.JPanel pnlHelpDesk;
    private javax.swing.JPanel pnlPatientMgmt;
    private javax.swing.JPanel pnlSupportDesk;
    // End of variables declaration//GEN-END:variables
}
