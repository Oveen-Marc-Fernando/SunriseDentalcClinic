package view;

/**
 * Step 1: Patient Information for Appointment Management.
 *
 * Patient Name is a dropdown of existing patients — selecting one
 * auto-fills Patient ID / Address / Contact No from that patient's record.
 * Those three fields are read-only; they're never typed directly, only
 * ever populated from whichever patient gets selected in the dropdown.
 */
public class OS_AM_1 extends javax.swing.JFrame {

    // Real patient directory — same "patients" table OS_PM_Grid reads, via
    // PatientManagementController. Loaded once per screen open (small
    // table, simple reads).
    private final java.util.List<model.PatientModel> patients = controller.PatientManagementController.getAll();

    private static final String PLACEHOLDER = "-- Select Patient --";

    public OS_AM_1() {
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        setupPatientDropdown();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    /**
     * Populates the Patient Name dropdown and locks Patient ID / Address /
     * Contact No as read-only — they're only ever filled in from whichever
     * patient is selected here, never typed by hand.
     */
    private void setupPatientDropdown() {
        cmbFullName.addItem(PLACEHOLDER);
        for (model.PatientModel patient : patients) {
            cmbFullName.addItem(patient.getFullName());
        }

        lockField(txtPatientId);
        lockField(txtAddress);
        lockField(txtContactNo);

        cmbFullName.addActionListener(e -> {
            int index = cmbFullName.getSelectedIndex() - 1; // -1 to skip the placeholder row
            if (index < 0 || index >= patients.size()) {
                txtPatientId.setText("");
                txtAddress.setText("");
                txtContactNo.setText("");
                return;
            }
            model.PatientModel patient = patients.get(index);
            txtPatientId.setText(patient.getPatientId());
            String address = patient.getAddressLine1() == null ? "" : patient.getAddressLine1();
            if (patient.getCity() != null && !patient.getCity().isEmpty()) {
                address += (address.isEmpty() ? "" : ", ") + patient.getCity();
            }
            txtAddress.setText(address);
            txtContactNo.setText(patient.getMobileNo());
        });
    }

    private void lockField(javax.swing.JTextField field) {
        field.setEditable(false);
        field.setBackground(new java.awt.Color(235, 235, 235));
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        sepLine = new javax.swing.JSeparator();
        lblStep1 = new javax.swing.JLabel();
        lblStep2 = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblFullName = new javax.swing.JLabel();
        cmbFullName = new javax.swing.JComboBox<String>();
        lblPatientId = new javax.swing.JLabel();
        txtPatientId = new javax.swing.JTextField();
        lblAddress = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        lblContactNo = new javax.swing.JLabel();
        txtContactNo = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Appointment Management (Step 1)");
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
        lblTitle.setText("Appointment Management");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 420, 40);

        cardPanel.setBackground(new java.awt.Color(248, 249, 250));
        cardPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cardPanel.setLayout(null);

        sepLine.setForeground(new java.awt.Color(0, 0, 0));
        cardPanel.add(sepLine);
        sepLine.setBounds(360, 50, 180, 4);

        lblStep1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep1.setForeground(new java.awt.Color(255, 255, 255));
        lblStep1.setBackground(new java.awt.Color(231, 115, 36));
        lblStep1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep1.setText("1");
        lblStep1.setOpaque(true);
        cardPanel.add(lblStep1);
        lblStep1.setBounds(360, 30, 40, 40);

        lblStep2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStep2.setForeground(new java.awt.Color(255, 255, 255));
        lblStep2.setBackground(new java.awt.Color(0, 0, 0));
        lblStep2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStep2.setText("2");
        lblStep2.setOpaque(true);
        cardPanel.add(lblStep2);
        lblStep2.setBounds(500, 30, 40, 40);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Patient Information");
        cardPanel.add(lblSubtitle);
        lblSubtitle.setBounds(60, 95, 400, 30);

        lblFullName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblFullName.setText("Patient Name");
        cardPanel.add(lblFullName);
        lblFullName.setBounds(60, 160, 100, 25);

        cmbFullName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(cmbFullName);
        cmbFullName.setBounds(60, 190, 350, 35);

        lblPatientId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPatientId.setText("Patient ID");
        cardPanel.add(lblPatientId);
        lblPatientId.setBounds(60, 240, 100, 25);

        txtPatientId.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtPatientId);
        txtPatientId.setBounds(60, 270, 350, 35);

        lblAddress.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAddress.setText("Address");
        cardPanel.add(lblAddress);
        lblAddress.setBounds(490, 160, 120, 25);

        txtAddress.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtAddress);
        txtAddress.setBounds(490, 190, 350, 35);

        lblContactNo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblContactNo.setText("Contact No");
        cardPanel.add(lblContactNo);
        lblContactNo.setBounds(490, 240, 150, 25);

        txtContactNo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cardPanel.add(txtContactNo);
        txtContactNo.setBounds(490, 270, 350, 35);

        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.setBackground(new java.awt.Color(0, 122, 255));
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        cardPanel.add(btnBack);
        btnBack.setBounds(620, 420, 100, 36);

        btnNext.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNext.setText("Next");
        btnNext.setBackground(new java.awt.Color(0, 200, 83));
        btnNext.setForeground(new java.awt.Color(255, 255, 255));
        btnNext.setBorderPainted(false);
        btnNext.setFocusPainted(false);
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        cardPanel.add(btnNext);
        btnNext.setBounds(740, 420, 100, 36);

        mainPanel.add(cardPanel);
        cardPanel.setBounds(50, 160, 900, 490);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OS_AM_Grid().setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        Object selected = cmbFullName.getSelectedItem();
        if (selected == null || PLACEHOLDER.equals(selected)) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please select a patient before continuing.",
                    "Validation Error", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        String patientName = selected.toString();
        String address = txtAddress.getText();
        String contactNo = txtContactNo.getText();
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OS_AM_2(patientName, address, contactNo).setVisible(true);
        });
    }//GEN-LAST:event_btnNextActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_AM_1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox<String> cmbFullName;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblPatientId;
    private javax.swing.JLabel lblContactNo;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtPatientId;
    private javax.swing.JTextField txtContactNo;
    // End of variables declaration//GEN-END:variables
}
