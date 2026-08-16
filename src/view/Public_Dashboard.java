package view;

import controller.AppController;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 * Public_Dashboard View — Home screen for unauthenticated visitors.
 * NetBeans GUI Builder compatible.
 *
 * @author oveen
 */
public class Public_Dashboard extends javax.swing.JFrame {

    private static final String[] SERVICES = {
        "Aligners",
        "Teeth Whitening",
        "Cleaning & Polishing",
        "Dental Implants",
        "Smile Design",
        "Root Canal Treatment",
        "Fillings",
        "Dental Bridges",
        "Crowns",
        "Dentures"
    };

    private static final int MENU_ITEM_WIDTH  = 250;
    private static final int MENU_ITEM_HEIGHT = 40;

    private JPopupMenu servicesMenu;

    public Public_Dashboard() {
        initComponents();
        lblUserIcon.setIcon(IconFactory.userGlyph(java.awt.Color.WHITE, 26)); // crisp vector glyph, no backdrop — sits directly on the black pill
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 28); // fully rounded pill — radius = half the bar's height
        setSize(1016, 739);
        setLocationRelativeTo(null);
        setupEventListeners();
    }

    private void setupEventListeners() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblUserIcon.setCursor(hand);
        lblServices.setCursor(hand);
        lblHome.setCursor(hand);
        lblAbout.setCursor(hand);

        lblUserIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AppController.showLogin(Public_Dashboard.this);
            }
        });

        lblServices.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showServicesMenu();
            }
        });

        lblHome.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(Public_Dashboard.this,
                        "You are currently on the Home page.",
                        "Navigation Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        lblAbout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(Public_Dashboard.this,
                        "Sunrise Dental Clinic — Providing world-class dental care since 2026.\nContact us: info@sunrisedental.com",
                        "About Us",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Add hover effects for service cards
        addCardHoverEffect(card1);
        addCardHoverEffect(card2);
        addCardHoverEffect(card3);
    }

    private void addCardHoverEffect(javax.swing.JPanel card) {
        final Color defaultBg = card.getBackground();
        final Color hoverBg   = new Color(225, 240, 255);
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

        card.setCursor(hand);
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e)  { card.setBackground(defaultBg); }
            @Override public void mouseClicked(MouseEvent e) {
                showServicesMenu();
            }
        });

        for (java.awt.Component comp : card.getComponents()) {
            comp.setCursor(hand);
            comp.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { card.setBackground(hoverBg); }
                @Override public void mouseExited(MouseEvent e)  { card.setBackground(defaultBg); }
                @Override public void mouseClicked(MouseEvent e) {
                    showServicesMenu();
                }
            });
        }
    }

    private void showServicesMenu() {
        if (servicesMenu == null) {
            servicesMenu = buildServicesMenu();
        }
        servicesMenu.show(lblServices, 0, lblServices.getHeight());
    }

    private JPopupMenu buildServicesMenu() {
        JPopupMenu menu = new JPopupMenu();
        Dimension itemSize = new Dimension(MENU_ITEM_WIDTH, MENU_ITEM_HEIGHT);
        for (String service : SERVICES) {
            JMenuItem item = new JMenuItem(service);
            item.setPreferredSize(itemSize);
            item.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "Service selected: " + service + "\nPlease log in to book an appointment.",
                    service,
                    JOptionPane.INFORMATION_MESSAGE));
            menu.add(item);
        }
        return menu;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblHome = new javax.swing.JLabel();
        lblServices = new javax.swing.JLabel();
        lblAbout = new javax.swing.JLabel();
        lblUserIcon = new javax.swing.JLabel();
        lblBanner = new javax.swing.JLabel();
        lblServiceTitle = new javax.swing.JLabel();
        card1 = new javax.swing.JPanel();
        lblCard1 = new javax.swing.JLabel();
        card2 = new javax.swing.JPanel();
        lblCard2 = new javax.swing.JLabel();
        card3 = new javax.swing.JPanel();
        lblCard3 = new javax.swing.JLabel();
        lblFooter = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sunrise Dental Clinic");
        setName("Public_Dashboard"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblLogo.setForeground(new java.awt.Color(255, 255, 255));
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 7, 160, 40);

        lblHome.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        lblHome.setForeground(new java.awt.Color(255, 255, 255));
        lblHome.setText("Home");
        navBar.add(lblHome);
        lblHome.setBounds(450, 18, 70, 25);

        lblServices.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        lblServices.setForeground(new java.awt.Color(255, 255, 255));
        lblServices.setText("Services");
        navBar.add(lblServices);
        lblServices.setBounds(560, 18, 90, 25);

        lblAbout.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        lblAbout.setForeground(new java.awt.Color(255, 255, 255));
        lblAbout.setText("About Us");
        navBar.add(lblAbout);
        lblAbout.setBounds(700, 18, 100, 25);

        lblUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/login_scaled.png"))); // NOI18N
        lblUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        navBar.add(lblUserIcon);
        lblUserIcon.setBounds(880, 10, 40, 40);

        mainPanel.add(navBar);
        navBar.setBounds(30, 20, 940, 55);

        lblBanner.setBackground(new java.awt.Color(220, 220, 220));
        lblBanner.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBanner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/banner_scaled.jpg"))); // NOI18N
        lblBanner.setOpaque(true);
        mainPanel.add(lblBanner);
        lblBanner.setBounds(120, 110, 750, 300);

        lblServiceTitle.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        lblServiceTitle.setText("Our Dental Services");
        mainPanel.add(lblServiceTitle);
        lblServiceTitle.setBounds(380, 430, 240, 40);

        card1.setBackground(new java.awt.Color(102, 218, 218));
        card1.setLayout(null);

        lblCard1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblCard1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCard1.setText("Dental Care");
        card1.add(lblCard1);
        lblCard1.setBounds(10, 30, 200, 30);

        mainPanel.add(card1);
        card1.setBounds(100, 480, 220, 100);

        card2.setBackground(new java.awt.Color(102, 218, 218));
        card2.setLayout(null);

        lblCard2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblCard2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCard2.setText("Cosmetic Dentistry");
        card2.add(lblCard2);
        lblCard2.setBounds(10, 30, 200, 30);

        mainPanel.add(card2);
        card2.setBounds(390, 480, 220, 100);

        card3.setBackground(new java.awt.Color(102, 218, 218));
        card3.setLayout(null);

        lblCard3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblCard3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCard3.setText("Modern Treatment");
        card3.add(lblCard3);
        lblCard3.setBounds(10, 30, 200, 30);

        mainPanel.add(card3);
        card3.setBounds(670, 480, 220, 100);

        lblFooter.setForeground(new java.awt.Color(128, 128, 128));
        lblFooter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFooter.setText("© 2026 Sunrise Dental Clinic | Your Smile, Our Priority");
        mainPanel.add(lblFooter);
        lblFooter.setBounds(200, 610, 600, 30);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel card1;
    private javax.swing.JPanel card2;
    private javax.swing.JPanel card3;
    private javax.swing.JLabel lblAbout;
    private javax.swing.JLabel lblBanner;
    private javax.swing.JLabel lblCard1;
    private javax.swing.JLabel lblCard2;
    private javax.swing.JLabel lblCard3;
    private javax.swing.JLabel lblFooter;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblServiceTitle;
    private javax.swing.JLabel lblServices;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    // End of variables declaration//GEN-END:variables
}
