package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * OS_PM_Grid – Office Staff: Patient Management grid view.
 * Layout defined in OS_PM_Grid.form (NetBeans GUI Builder compatible).
 *
 * @author oveen
 */
public class OS_PM_Grid extends javax.swing.JFrame {

    private static final Font FONT_DEFAULT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Color COLOR_STRIPE = new Color(240, 240, 240);
    private static final Color COLOR_BTN_UPDATE = new Color(40, 167, 69);
    private static final Color COLOR_BTN_DELETE = new Color(220, 53, 69);
    private static final Color COLOR_LINK = new Color(30, 144, 255);
    private static final int TABLE_ROW_HEIGHT = 34;
    private static final int[] TABLE_COLUMN_WIDTHS = {110, 150, 140, 200, 130, 110};

    private javax.swing.Icon icoSearch;
    private javax.swing.Icon icoUpdate;
    private javax.swing.Icon icoDelete;

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public OS_PM_Grid() {
        loadIcons();
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        txtSearch.setText("");
        applySearchIcon();
        setupTable();
        populateSampleData();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void loadIcons() {
        icoSearch = IconFactory.search(new Color(110, 110, 110), 16);
        icoUpdate = IconFactory.pencil(Color.WHITE, 15);
        icoDelete = IconFactory.trash(Color.WHITE, 15);
    }

    private class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if (!sel) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            }
            return c;
        }
    }

    private class ActionButtonsRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel panel = new JPanel(null);
            JButton btnUpdate = IconFactory.actionButton(icoUpdate, COLOR_BTN_UPDATE, "Edit");
            JButton btnDelete = IconFactory.actionButton(icoDelete, COLOR_BTN_DELETE, "Delete");
            btnUpdate.setBounds(14, 3, 38, 28);
            btnDelete.setBounds(58, 3, 38, 28);
            panel.add(btnUpdate);
            panel.add(btnDelete);
            panel.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            return panel;
        }
    }

    private class DetailsLinkRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            setText("<html><u>View details</u></html>");
            setForeground(COLOR_LINK);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(CENTER);
            setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            return this;
        }
    }

    private class SearchDocumentListener implements DocumentListener {
        @Override public void insertUpdate(DocumentEvent e)  { applyFilter(); }
        @Override public void removeUpdate(DocumentEvent e)  { applyFilter(); }
        @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }

        private void applyFilter() {
            if (sorter == null) return;
            String text = IconFactory.isPlaceholderShowing(txtSearch) ? "" : txtSearch.getText().trim();
            sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
        }
    }

    private class TableClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = tblPatients.columnAtPoint(e.getPoint());
            int row = tblPatients.rowAtPoint(e.getPoint());
            if (row < 0) return;

            int modelRow = tblPatients.convertRowIndexToModel(row);
            String patientName = tableModel.getValueAt(modelRow, 1).toString();

            String patientId = String.valueOf(tableModel.getValueAt(modelRow, 0));
            if (col == 4) { // "View details" link — opens the Edit popup
                handleEditPatient(patientId, modelRow);
            } else if (col == 5) { // Action column
                handleActionClick(e, row, col, patientName, patientId, modelRow);
            }
        }

        private void handleActionClick(MouseEvent e, int row, int col, String name, String patientId, int modelRow) {
            java.awt.Rectangle cellRect = tblPatients.getCellRect(row, col, false);
            int xInCell = e.getX() - cellRect.x;

            if (xInCell < 48) { // Edit button — always opens the Edit popup.
                // Create Login was dropped from here: patients now set their
                // username at Add time (OS_PM_1), so this grid no longer
                // needs a separate login-creation path.
                handleEditPatient(patientId, modelRow);
            } else { // Delete button
                IconFactory.showConfirmDialog(OS_PM_Grid.this, "Delete patient: " + name + "?", "Delete", () -> {
                    if (controller.PatientManagementController.delete(patientId)) {
                        tableModel.removeRow(modelRow);
                    } else {
                        IconFactory.showErrorDialog(OS_PM_Grid.this,
                                "Couldn't delete " + name + " from the database.", null);
                    }
                });
            }
        }

        /**
         * The real Edit — a scrollable popup covering this patient's
         * Personal and Contact Information (the same two sections OS_PM_1/2
         * collect), pre-filled from their actual record and saved back with
         * a single "Save Details!" button instead of walking the wizard.
         */
        private void handleEditPatient(String patientId, int modelRow) {
            model.PatientModel p = controller.PatientManagementController.findById(patientId);
            if (p == null) {
                IconFactory.showErrorDialog(OS_PM_Grid.this,
                        "Couldn't find that patient record anymore — try refreshing the grid.", null);
                return;
            }

            final int x = 20, w = IconFactory.FORM_DIALOG_CONTENT_WIDTH;
            JPanel content = new JPanel(null);
            int y = 10;

            y = IconFactory.addFormSectionHeader(content, "Personal Information", x, y, w);
            javax.swing.JCheckBox[] titleBoxes = new javax.swing.JCheckBox[3];
            y = IconFactory.addFormCheckboxRow(content, "Title:", new String[]{"Mr", "Mrs", "Dr"}, titleBoxes, p.getTitle(), x, y, w);
            javax.swing.JCheckBox[] genderBoxes = new javax.swing.JCheckBox[2];
            y = IconFactory.addFormCheckboxRow(content, "Gender:", new String[]{"Male", "Female"}, genderBoxes, p.getGender(), x, y, w);
            JTextField txtFullName = new JTextField(dash(p.getFullName()));
            y = IconFactory.addFormField(content, "Full Name:", txtFullName, x, y, w);
            JTextField txtPatientId = new JTextField(p.getPatientId());
            txtPatientId.setEditable(false);
            y = IconFactory.addFormField(content, "Patient ID:", txtPatientId, x, y, w);
            JTextField txtDob = new JTextField(dash(p.getDob()));
            y = IconFactory.addFormField(content, "DOB:", txtDob, x, y, w);
            JTextField txtNic = new JTextField(dash(p.getNic()));
            y = IconFactory.addFormField(content, "NIC/Passport:", txtNic, x, y, w);
            JTextField txtAge = new JTextField(dash(p.getAge()));
            y = IconFactory.addFormField(content, "Age:", txtAge, x, y, w);

            y += 6;
            y = IconFactory.addFormSectionHeader(content, "Contact Information", x, y, w);
            JTextField txtAddress = new JTextField(dash(p.getAddressLine1()));
            y = IconFactory.addFormField(content, "Address Line 1:", txtAddress, x, y, w);
            JTextField txtAddress2 = new JTextField(dash(p.getAddressLine2()));
            y = IconFactory.addFormField(content, "Address Line 2:", txtAddress2, x, y, w);
            JTextField txtCity = new JTextField(dash(p.getCity()));
            y = IconFactory.addFormField(content, "City:", txtCity, x, y, w);
            JTextField txtMobile = new JTextField(dash(p.getMobileNo()));
            y = IconFactory.addFormField(content, "Mobile No:", txtMobile, x, y, w);
            JTextField txtLandline = new JTextField(dash(p.getLandlineNo()));
            y = IconFactory.addFormField(content, "Landline No:", txtLandline, x, y, w);
            JTextField txtEmail = new JTextField(dash(p.getEmail()));
            y = IconFactory.addFormField(content, "Email:", txtEmail, x, y, w);

            y += 6;
            y = IconFactory.addFormSectionHeader(content, "Medical Information", x, y, w);
            JTextField txtBloodGroup = new JTextField(dash(p.getBloodGroup()));
            y = IconFactory.addFormField(content, "Blood Group:", txtBloodGroup, x, y, w);
            JTextField txtAllergies = new JTextField(dash(p.getAllergies()));
            y = IconFactory.addFormField(content, "Allergies:", txtAllergies, x, y, w);
            JTextField txtMedicalConditions = new JTextField(dash(p.getMedicalConditions()));
            y = IconFactory.addFormField(content, "Medical Conditions:", txtMedicalConditions, x, y, w);
            JTextField txtCurrentMedications = new JTextField(dash(p.getCurrentMedications()));
            y = IconFactory.addFormField(content, "Current Medications:", txtCurrentMedications, x, y, w);
            JTextField txtPreviousSurgeries = new JTextField(dash(p.getPreviousSurgeries()));
            y = IconFactory.addFormField(content, "Previous Surgeries:", txtPreviousSurgeries, x, y, w);
            JTextField txtGeneralMedicalNotes = new JTextField(dash(p.getGeneralMedicalNotes()));
            y = IconFactory.addFormField(content, "General Medical Notes:", txtGeneralMedicalNotes, x, y, w);

            y += 6;
            y = IconFactory.addFormSectionHeader(content, "Dental Information", x, y, w);
            JTextField txtLastDentalVisit = new JTextField(dash(p.getLastDentalVisit()));
            y = IconFactory.addFormField(content, "Last Dental Visit:", txtLastDentalVisit, x, y, w);
            JTextField txtDentalHistory = new JTextField(dash(p.getDentalHistory()));
            y = IconFactory.addFormField(content, "Dental History:", txtDentalHistory, x, y, w);
            JTextField txtDentalProblems = new JTextField(dash(p.getDentalProblems()));
            y = IconFactory.addFormField(content, "Dental Problems:", txtDentalProblems, x, y, w);
            javax.swing.JCheckBox[] oralHygieneBoxes = new javax.swing.JCheckBox[3];
            y = IconFactory.addFormCheckboxRow(content, "Oral Hygiene:", new String[]{"Good", "Fair", "Poor"}, oralHygieneBoxes,
                    p.getOralHygiene(), x, y, w);
            JTextField txtDentalMedicalNotes = new JTextField(dash(p.getDentalMedicalNotes()));
            y = IconFactory.addFormField(content, "Dental Medical Notes:", txtDentalMedicalNotes, x, y, w);

            IconFactory.showScrollableFormDialog(OS_PM_Grid.this, "Edit Patient", content, y, () -> {
                if (txtFullName.getText().trim().isEmpty()) {
                    return "Full Name is required.";
                }
                if (txtMobile.getText().trim().isEmpty()) {
                    return "Mobile No is required.";
                }
                p.setTitle(firstChecked(titleBoxes));
                p.setGender(firstChecked(genderBoxes));
                p.setFullName(txtFullName.getText().trim());
                p.setDob(txtDob.getText().trim());
                p.setNic(txtNic.getText().trim());
                p.setAge(txtAge.getText().trim());
                p.setAddressLine1(txtAddress.getText().trim());
                p.setAddressLine2(txtAddress2.getText().trim());
                p.setCity(txtCity.getText().trim());
                p.setMobileNo(txtMobile.getText().trim());
                p.setLandlineNo(txtLandline.getText().trim());
                p.setEmail(txtEmail.getText().trim());
                p.setBloodGroup(txtBloodGroup.getText().trim());
                p.setAllergies(txtAllergies.getText().trim());
                p.setMedicalConditions(txtMedicalConditions.getText().trim());
                p.setCurrentMedications(txtCurrentMedications.getText().trim());
                p.setPreviousSurgeries(txtPreviousSurgeries.getText().trim());
                p.setGeneralMedicalNotes(txtGeneralMedicalNotes.getText().trim());
                p.setLastDentalVisit(txtLastDentalVisit.getText().trim());
                p.setDentalHistory(txtDentalHistory.getText().trim());
                p.setDentalProblems(txtDentalProblems.getText().trim());
                p.setOralHygiene(firstChecked(oralHygieneBoxes));
                p.setDentalMedicalNotes(txtDentalMedicalNotes.getText().trim());
                if (!controller.PatientManagementController.updatePatient(p)) {
                    return "Couldn't save — check the console for details and try again.";
                }
                tableModel.setValueAt(p.getPatientId(), modelRow, 0);
                tableModel.setValueAt(p.getFullName(), modelRow, 1);
                tableModel.setValueAt(p.getLastDentalVisit(), modelRow, 2);
                tableModel.setValueAt(p.getEmail(), modelRow, 3);
                IconFactory.showSuccessDialog(OS_PM_Grid.this, "Patient updated successfully!", null);
                return null;
            });
        }

        private String firstChecked(javax.swing.JCheckBox[] boxes) {
            for (javax.swing.JCheckBox box : boxes) {
                if (box.isSelected()) {
                    return box.getText();
                }
            }
            return "";
        }

        private String dash(String v) {
            return v == null || "Nil".equalsIgnoreCase(v.trim()) ? "" : v;
        }
    }

    private void applySearchIcon() {
        txtSearch.setBorder(null);
        IconFactory.installPlaceholder(txtSearch, "Search here...");
        if (icoSearch != null) {
            lblSearchIcon.setIcon(icoSearch);
            lblSearchIcon.setText("");
        }
    }

    private void setupTable() {
        tableModel = (DefaultTableModel) tblPatients.getModel();
        sorter = new TableRowSorter<>(tableModel);
        tblPatients.setRowSorter(sorter);

        tblPatients.setFont(FONT_DEFAULT);
        tblPatients.setRowHeight(TABLE_ROW_HEIGHT);
        tblPatients.setShowGrid(false);
        tblPatients.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tblPatients.getTableHeader().setFont(FONT_DEFAULT);
        tblPatients.getTableHeader().setBackground(new Color(245, 245, 245));
        tblPatients.getTableHeader().setReorderingAllowed(false);

        tblPatients.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        for (int i = 0; i < TABLE_COLUMN_WIDTHS.length; i++) {
            tblPatients.getColumnModel().getColumn(i).setPreferredWidth(TABLE_COLUMN_WIDTHS[i]);
        }

        tblPatients.getColumnModel().getColumn(4).setCellRenderer(new DetailsLinkRenderer());
        tblPatients.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonsRenderer());
        tblPatients.addMouseListener(new TableClickHandler());
        txtSearch.getDocument().addDocumentListener(new SearchDocumentListener());
    }

    // Backed by the real "patients" table (db/schema.sql), via
    // PatientManagementController.

    /** Number of patient records — used by the Office Staff dashboard tile badge. */
    public static int getRecordCount() {
        return controller.PatientManagementController.count();
    }

    private void populateSampleData() {
        tableModel.setRowCount(0);
        for (model.PatientModel p : controller.PatientManagementController.getAll()) {
            tableModel.addRow(new Object[]{
                p.getPatientId(), p.getFullName(), p.getLastDentalVisit(), p.getEmail(), "", ""
            });
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        pnlSearchWrap = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        lblSearchIcon = new javax.swing.JLabel();
        pnlTableWrap = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tblPatients = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Patient Management");
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
        lblTitle.setText("Patient Management");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 120, 400, 40);

        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setBackground(new java.awt.Color(231, 115, 36));
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setBorderPainted(false);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        mainPanel.add(btnAdd);
        btnAdd.setBounds(820, 122, 100, 36);

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
        mainPanel.add(btnBack);
        btnBack.setBounds(50, 175, 100, 36);

        pnlSearchWrap.setBackground(new java.awt.Color(255, 255, 255));
        pnlSearchWrap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlSearchWrap.setLayout(null);

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtSearch.setBorder(null);
        pnlSearchWrap.add(txtSearch);
        txtSearch.setBounds(8, 6, 140, 24);

        lblSearchIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/search_icon.png"))); // NOI18N
        lblSearchIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlSearchWrap.add(lblSearchIcon);
        lblSearchIcon.setBounds(150, 4, 26, 26);

        mainPanel.add(pnlSearchWrap);
        pnlSearchWrap.setBounds(165, 175, 180, 36);

        pnlTableWrap.setBackground(new java.awt.Color(255, 255, 255));
        pnlTableWrap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlTableWrap.setLayout(null);

        tblPatients.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblPatients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Patient ID", "Patient Name", "Last Dental Visit", "Email", "Details", "Action"
            }
        ) {
            Class<?>[] types = new Class<?>[] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class<?> getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPatients.setRowHeight(34);
        tblPatients.setShowGrid(false);
        scrollPane.setViewportView(tblPatients);
        if (tblPatients.getColumnModel().getColumnCount() > 0) {
            tblPatients.getColumnModel().getColumn(0).setPreferredWidth(110);
            tblPatients.getColumnModel().getColumn(1).setPreferredWidth(150);
            tblPatients.getColumnModel().getColumn(2).setPreferredWidth(140);
            tblPatients.getColumnModel().getColumn(3).setPreferredWidth(200);
            tblPatients.getColumnModel().getColumn(4).setPreferredWidth(130);
            tblPatients.getColumnModel().getColumn(5).setPreferredWidth(110);
        }

        pnlTableWrap.add(scrollPane);
        scrollPane.setBounds(1, 1, 878, 350);

        mainPanel.add(pnlTableWrap);
        pnlTableWrap.setBounds(50, 225, 880, 352);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        new OS_PM_1().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // Must carry the real signed-in user back — the no-arg constructor
        // passes null, which silently breaks any "who's logged in" UI
        // reachable from that dashboard afterward (e.g. Edit Profile).
        new OfficeStaff_Dashboard(controller.AppController.getCurrentUser()).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_PM_Grid().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBack;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSearchIcon;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlSearchWrap;
    private javax.swing.JPanel pnlTableWrap;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblPatients;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
