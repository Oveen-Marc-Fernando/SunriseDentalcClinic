package view;

/**
 * The Home page's hero content — banner photo, tagline, and the 3 teal
 * highlight boxes — as a real, reusable custom JPanel component built with
 * NetBeans's GUI Builder (see HomePagePanel.form), so it has a working
 * Design tab. Public_Dashboard.java instantiates this and adds it into its
 * scrollable content area exactly like any other component; this isn't a
 * disconnected mockup — it's the actual Home page content that renders in
 * the real app.
 */
public class HomePagePanel extends javax.swing.JPanel {

    public HomePagePanel() {
        initComponents();
        // Rounded corners — same technique as every other rounded panel on this
        // page (navBar, footer, etc.): called here, not inside the guarded
        // initComponents() block, so it round-trips cleanly with the .form file.
        IconFactory.roundCorners(pnlBoxCare, 10);
        IconFactory.roundCorners(pnlBoxDoctors, 10);
        IconFactory.roundCorners(pnlBoxTreatment, 10);
    }

    /**
     * The hero banner's real, curved-corner clipping — same technique
     * Public_Dashboard.java's buildPhotoBox uses for every other photo on
     * this page. Not representable as a plain form-declared JLabel icon
     * (that's why this was still square-cornered before), so it's built
     * here in code and just added into the panel the guarded block builds.
     */
    private javax.swing.JPanel buildRoundedBanner(int x, int y, int w, int h, int radius) {
        final java.awt.Image photo = new javax.swing.ImageIcon(getClass().getResource("/resources/banner_scaled.jpg")).getImage();
        javax.swing.JPanel box = new javax.swing.JPanel(null) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                int iw = photo.getWidth(this);
                int ih = photo.getHeight(this);
                if (iw <= 0 || ih <= 0) {
                    return;
                }
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, radius, radius));
                double scale = Math.max((double) w / iw, (double) h / ih); // cover-fit: fill the box, crop the overflow
                int dw = (int) Math.ceil(iw * scale);
                int dh = (int) Math.ceil(ih * scale);
                g2.drawImage(photo, (w - dw) / 2, (h - dh) / 2, dw, dh, this);
                g2.dispose();
            }
        };
        box.setOpaque(true);
        box.setBackground(new java.awt.Color(220, 220, 220));
        box.setBounds(x, y, w, h);
        return box;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTagline = new javax.swing.JLabel();
        pnlBoxCare = new javax.swing.JPanel();
        lblCareIcon = new javax.swing.JLabel();
        lblCareTitle = new javax.swing.JLabel();
        lblCareSub = new javax.swing.JLabel();
        pnlBoxDoctors = new javax.swing.JPanel();
        lblDoctorsIcon = new javax.swing.JLabel();
        lblDoctorsTitle = new javax.swing.JLabel();
        lblDoctorsSub = new javax.swing.JLabel();
        pnlBoxTreatment = new javax.swing.JPanel();
        lblTreatmentIcon = new javax.swing.JLabel();
        lblTreatmentTitle = new javax.swing.JLabel();
        lblTreatmentSub = new javax.swing.JLabel();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(920, 545));
        setLayout(null);

        add(buildRoundedBanner(85, 20, 750, 260, 20));

        lblTagline.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTagline.setForeground(new java.awt.Color(80, 80, 80));
        lblTagline.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTagline.setText("Comprehensive Dental Care You Can Trust");
        add(lblTagline);
        lblTagline.setBounds(0, 295, 920, 30);

        pnlBoxCare.setBackground(new java.awt.Color(0x3C, 0x78, 0x78));
        pnlBoxCare.setLayout(null);

        lblCareIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCareIcon.setIcon(IconFactory.tooth(java.awt.Color.WHITE, 40));
        pnlBoxCare.add(lblCareIcon);
        lblCareIcon.setBounds(0, 34, 293, 44);

        lblCareTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblCareTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblCareTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCareTitle.setText("Dental Care");
        pnlBoxCare.add(lblCareTitle);
        lblCareTitle.setBounds(0, 90, 293, 24);

        lblCareSub.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblCareSub.setForeground(new java.awt.Color(255, 255, 255));
        lblCareSub.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCareSub.setText("Comprehensive care for your whole family");
        pnlBoxCare.add(lblCareSub);
        lblCareSub.setBounds(20, 122, 253, 20);

        add(pnlBoxCare);
        pnlBoxCare.setBounds(0, 340, 293, 200);

        pnlBoxDoctors.setBackground(new java.awt.Color(0x3C, 0x78, 0x78));
        pnlBoxDoctors.setLayout(null);

        lblDoctorsIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDoctorsIcon.setIcon(IconFactory.userGlyph(java.awt.Color.WHITE, 40));
        pnlBoxDoctors.add(lblDoctorsIcon);
        lblDoctorsIcon.setBounds(0, 34, 293, 44);

        lblDoctorsTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblDoctorsTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblDoctorsTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDoctorsTitle.setText("Expert Doctors");
        pnlBoxDoctors.add(lblDoctorsTitle);
        lblDoctorsTitle.setBounds(0, 90, 293, 24);

        lblDoctorsSub.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblDoctorsSub.setForeground(new java.awt.Color(255, 255, 255));
        lblDoctorsSub.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDoctorsSub.setText("Experienced, certified dental professionals");
        pnlBoxDoctors.add(lblDoctorsSub);
        lblDoctorsSub.setBounds(20, 122, 253, 20);

        add(pnlBoxDoctors);
        pnlBoxDoctors.setBounds(313, 340, 293, 200);

        pnlBoxTreatment.setBackground(new java.awt.Color(0x3C, 0x78, 0x78));
        pnlBoxTreatment.setLayout(null);

        lblTreatmentIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTreatmentIcon.setIcon(IconFactory.barChart(java.awt.Color.WHITE, 40));
        pnlBoxTreatment.add(lblTreatmentIcon);
        lblTreatmentIcon.setBounds(0, 34, 293, 44);

        lblTreatmentTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTreatmentTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTreatmentTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTreatmentTitle.setText("Modern Treatment");
        pnlBoxTreatment.add(lblTreatmentTitle);
        lblTreatmentTitle.setBounds(0, 90, 293, 24);

        lblTreatmentSub.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblTreatmentSub.setForeground(new java.awt.Color(255, 255, 255));
        lblTreatmentSub.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTreatmentSub.setText("Advanced technology for lasting results");
        pnlBoxTreatment.add(lblTreatmentSub);
        lblTreatmentSub.setBounds(20, 122, 253, 20);

        add(pnlBoxTreatment);
        pnlBoxTreatment.setBounds(626, 340, 293, 200);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblCareIcon;
    private javax.swing.JLabel lblCareSub;
    private javax.swing.JLabel lblCareTitle;
    private javax.swing.JLabel lblDoctorsIcon;
    private javax.swing.JLabel lblDoctorsSub;
    private javax.swing.JLabel lblDoctorsTitle;
    private javax.swing.JLabel lblTagline;
    private javax.swing.JLabel lblTreatmentIcon;
    private javax.swing.JLabel lblTreatmentSub;
    private javax.swing.JLabel lblTreatmentTitle;
    private javax.swing.JPanel pnlBoxCare;
    private javax.swing.JPanel pnlBoxDoctors;
    private javax.swing.JPanel pnlBoxTreatment;
    // End of variables declaration//GEN-END:variables
}
