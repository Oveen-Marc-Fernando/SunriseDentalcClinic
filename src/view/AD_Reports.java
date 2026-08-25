package view;

/**
 * Administration &gt; Reports — the report categories browser and
 * download/view picker. View opens a real generated PDF (A4, the clinic's
 * fixed report format — see {@link util.ReportPdfGenerator}) in the
 * system's default PDF viewer; Download saves that same PDF wherever the
 * user picks. Category only narrows which reports show in the Report
 * dropdown — the actual generation is keyed off Time + Report, via
 * {@link controller.ReportController}.
 *
 * @author oveen
 */
public class AD_Reports extends javax.swing.JFrame {

    public AD_Reports() {
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40));
        IconFactory.roundCorners(navBar, 30);
        installCategoryFilter();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private static final String[] OPERATIONAL_REPORTS = {
        "Patient Report", "Dentist Report", "Appointment Report", "Inventory Report", "Billing Report"
    };
    private static final String[] FINANCIAL_REPORTS = {
        "Inventory Sales Report", "Services Income Report", "Total Income Report",
        "Total Expenses Report", "Dentist Consultation Fee Report"
    };

    /** Keeps the Report dropdown limited to whichever Category is selected, instead of always listing all 10. */
    private void installCategoryFilter() {
        cmbCategoryFilter.addActionListener(e -> refreshReportOptions());
        refreshReportOptions();
    }

    private void refreshReportOptions() {
        String[] options = "Financial".equals(cmbCategoryFilter.getSelectedItem())
                ? FINANCIAL_REPORTS : OPERATIONAL_REPORTS;
        cmbReportFilter.setModel(new javax.swing.DefaultComboBoxModel(options));
    }

    /**
     * Generates the currently-picked report and hands the resulting bytes
     * to {@code whenReady} — a save-to-disk temp file for View, a real
     * save-dialog target for Download. Any failure (bad DB connection, an
     * unwritable path, ...) surfaces as the app's normal error dialog
     * instead of a stack trace or a silently missing file.
     */
    private void generateThen(java.util.function.Consumer<byte[]> whenReady) {
        String time = String.valueOf(cmbTimeFilter.getSelectedItem());
        String report = String.valueOf(cmbReportFilter.getSelectedItem());
        try {
            byte[] pdf = controller.ReportController.generate(time, report);
            whenReady.accept(pdf);
        } catch (Exception e) {
            IconFactory.showErrorDialog(this,
                    "Couldn't generate this report — " + e.getMessage(), null);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        lblSelectReports = new javax.swing.JLabel();
        lblDownloadReports = new javax.swing.JLabel();
        pnlCategories = new javax.swing.JPanel();
        lblCategoriesTitle = new javax.swing.JLabel();
        lblTimesList = new javax.swing.JLabel();
        lblOperationalList = new javax.swing.JLabel();
        lblFinancialList = new javax.swing.JLabel();
        pnlDownload = new javax.swing.JPanel();
        lblTimeFilter = new javax.swing.JLabel();
        cmbTimeFilter = new javax.swing.JComboBox();
        lblCategoryFilter = new javax.swing.JLabel();
        cmbCategoryFilter = new javax.swing.JComboBox();
        lblReportFilter = new javax.swing.JLabel();
        cmbReportFilter = new javax.swing.JComboBox();
        btnView = new javax.swing.JButton();
        btnDownload = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental — Reports");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 160, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("Reports");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 130, 300, 40);

        cardPanel.setBackground(new java.awt.Color(242, 242, 242));
        cardPanel.setLayout(null);

        lblSelectReports.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSelectReports.setForeground(new java.awt.Color(231, 115, 36));
        lblSelectReports.setText("Select Reports");
        cardPanel.add(lblSelectReports);
        lblSelectReports.setBounds(170, 10, 300, 30);

        lblDownloadReports.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblDownloadReports.setForeground(new java.awt.Color(231, 115, 36));
        lblDownloadReports.setText("Download Reports");
        cardPanel.add(lblDownloadReports);
        lblDownloadReports.setBounds(600, 10, 300, 30);

        pnlCategories.setBackground(new java.awt.Color(255, 255, 255));
        pnlCategories.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlCategories.setLayout(null);

        lblCategoriesTitle.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblCategoriesTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCategoriesTitle.setText("Report Categories");
        pnlCategories.add(lblCategoriesTitle);
        lblCategoriesTitle.setBounds(0, 16, 440, 25);

        lblTimesList.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTimesList.setText("<html><b>Times:</b><br>Weekly<br>Yearly<br>Monthly</html>");
        lblTimesList.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        pnlCategories.add(lblTimesList);
        lblTimesList.setBounds(70, 60, 150, 120);

        lblOperationalList.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblOperationalList.setText("<html><b>Operational:</b><br>Patient Report<br>Dentist Report<br>Appointment Report<br>Inventory Report<br>Billing Report</html>");
        lblOperationalList.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        pnlCategories.add(lblOperationalList);
        lblOperationalList.setBounds(220, 60, 230, 130);

        lblFinancialList.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblFinancialList.setText("<html><b>Financial:</b><br>Inventory Sales Report<br>Services Income Report<br>Total Income Report<br>Total Expenses Report<br>Dentist Consultation Fee Report</html>");
        lblFinancialList.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        pnlCategories.add(lblFinancialList);
        lblFinancialList.setBounds(220, 180, 380, 150);

        cardPanel.add(pnlCategories);
        pnlCategories.setBounds(30, 55, 440, 320);

        pnlDownload.setBackground(new java.awt.Color(255, 255, 255));
        pnlDownload.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlDownload.setLayout(null);

        lblTimeFilter.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTimeFilter.setText("Time:");
        pnlDownload.add(lblTimeFilter);
        lblTimeFilter.setBounds(30, 70, 90, 25);

        cmbTimeFilter.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbTimeFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Weekly", "Monthly", "Yearly" }));
        pnlDownload.add(cmbTimeFilter);
        cmbTimeFilter.setBounds(120, 70, 230, 32);

        lblCategoryFilter.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblCategoryFilter.setText("Category:");
        pnlDownload.add(lblCategoryFilter);
        lblCategoryFilter.setBounds(30, 120, 90, 25);

        cmbCategoryFilter.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbCategoryFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Operational", "Financial" }));
        pnlDownload.add(cmbCategoryFilter);
        cmbCategoryFilter.setBounds(120, 120, 230, 32);

        lblReportFilter.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblReportFilter.setText("Report:");
        pnlDownload.add(lblReportFilter);
        lblReportFilter.setBounds(30, 170, 90, 25);

        cmbReportFilter.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbReportFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Patient Report", "Dentist Report", "Appointment Report", "Inventory Report", "Billing Report", "Inventory Sales Report", "Services Income Report", "Total Income Report", "Total Expenses Report", "Dentist Consultation Fee Report" }));
        pnlDownload.add(cmbReportFilter);
        cmbReportFilter.setBounds(120, 170, 230, 32);

        btnView.setBackground(new java.awt.Color(255, 193, 7));
        btnView.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnView.setForeground(new java.awt.Color(30, 30, 30));
        btnView.setText("View");
        btnView.setBorderPainted(false);
        btnView.setFocusPainted(false);
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });
        pnlDownload.add(btnView);
        btnView.setBounds(240, 240, 110, 38);

        btnDownload.setBackground(new java.awt.Color(0, 168, 84));
        btnDownload.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDownload.setForeground(new java.awt.Color(255, 255, 255));
        btnDownload.setText("Download");
        btnDownload.setBorderPainted(false);
        btnDownload.setFocusPainted(false);
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });
        pnlDownload.add(btnDownload);
        btnDownload.setBounds(30, 240, 110, 38);

        btnClear.setBackground(new java.awt.Color(220, 53, 69));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.setBorderPainted(false);
        btnClear.setFocusPainted(false);
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        pnlDownload.add(btnClear);
        btnClear.setBounds(150, 240, 80, 38);

        cardPanel.add(pnlDownload);
        pnlDownload.setBounds(500, 55, 380, 320);

        btnBack.setBackground(new java.awt.Color(0, 122, 255));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setText("Back");
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        cardPanel.add(btnBack);
        btnBack.setBounds(30, 10, 100, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 210, 900, 400);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Administration_Dashboard(controller.AppController.getCurrentUser()).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        generateThen(pdf -> {
            String time = String.valueOf(cmbTimeFilter.getSelectedItem());
            String report = String.valueOf(cmbReportFilter.getSelectedItem());
            String title = report + " — " + time;
            String fileName = controller.ReportController.suggestedFileName(time, report);
            new ReportPreviewDialog(this, title, pdf, fileName).setVisible(true);
        });
    }//GEN-LAST:event_btnViewActionPerformed

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        generateThen(pdf -> {
            String suggested = controller.ReportController.suggestedFileName(
                    String.valueOf(cmbTimeFilter.getSelectedItem()), String.valueOf(cmbReportFilter.getSelectedItem()));
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
            chooser.setSelectedFile(new java.io.File(suggested));
            chooser.setDialogTitle("Save Report");
            if (chooser.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            java.io.File target = chooser.getSelectedFile();
            if (!target.getName().toLowerCase().endsWith(".pdf")) {
                target = new java.io.File(target.getParentFile(), target.getName() + ".pdf");
            }
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(target)) {
                out.write(pdf);
                IconFactory.showSuccessDialog(this, "Report saved to " + target.getAbsolutePath(), null);
            } catch (java.io.IOException e) {
                IconFactory.showErrorDialog(this, "Couldn't save the report — " + e.getMessage(), null);
            }
        });
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        cmbTimeFilter.setSelectedIndex(0);
        cmbCategoryFilter.setSelectedIndex(0);
        cmbReportFilter.setSelectedIndex(0);
    }//GEN-LAST:event_btnClearActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new AD_Reports().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnView;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox cmbCategoryFilter;
    private javax.swing.JComboBox cmbReportFilter;
    private javax.swing.JComboBox cmbTimeFilter;
    private javax.swing.JLabel lblCategoriesTitle;
    private javax.swing.JLabel lblCategoryFilter;
    private javax.swing.JLabel lblDownloadReports;
    private javax.swing.JLabel lblFinancialList;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblOperationalList;
    private javax.swing.JLabel lblReportFilter;
    private javax.swing.JLabel lblSelectReports;
    private javax.swing.JLabel lblTimeFilter;
    private javax.swing.JLabel lblTimesList;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlCategories;
    private javax.swing.JPanel pnlDownload;
    // End of variables declaration//GEN-END:variables
}
