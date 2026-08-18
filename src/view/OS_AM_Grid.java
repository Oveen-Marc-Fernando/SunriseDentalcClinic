package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Appointment Management Grid View.
 * Displays list of appointments with real-time search filtering and wizard navigation.
 */
public class OS_AM_Grid extends javax.swing.JFrame {

    private static final Font FONT_DEFAULT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Color COLOR_STRIPE = new Color(240, 240, 240);
    private static final Color COLOR_BTN_UPDATE = new Color(40, 167, 69);
    private static final Color COLOR_BTN_DELETE = new Color(220, 53, 69);
    private static final int TABLE_ROW_HEIGHT = 34;

    private javax.swing.Icon icoSearch;
    private javax.swing.Icon icoUpdate;
    private javax.swing.Icon icoDelete;

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public OS_AM_Grid() {
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

    private class SearchDocumentListener implements DocumentListener {
        @Override public void insertUpdate(DocumentEvent e)  { applyFilter(); }
        @Override public void removeUpdate(DocumentEvent e)  { applyFilter(); }
        @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
    }

    private void applyFilter() {
        if (sorter == null) return;
        String text = IconFactory.isPlaceholderShowing(txtSearch) ? "" : txtSearch.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
        }
    }

    private class TableClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = tblAppointments.columnAtPoint(e.getPoint());
            int row = tblAppointments.rowAtPoint(e.getPoint());
            if (row < 0) return;

            int modelRow = tblAppointments.convertRowIndexToModel(row);
            String appNo = tableModel.getValueAt(modelRow, 0).toString();

            if (col == 7) { // Action column
                handleActionClick(e, row, col, appNo, modelRow);
            }
        }

        private void handleActionClick(MouseEvent e, int row, int col, String appNo, int modelRow) {
            java.awt.Rectangle cellRect = tblAppointments.getCellRect(row, col, false);
            int xInCell = e.getX() - cellRect.x;

            if (xInCell < 48) { // Edit button
                JOptionPane.showMessageDialog(OS_AM_Grid.this,
                        "Edit Appointment: " + appNo, "Edit Appointment",
                        JOptionPane.INFORMATION_MESSAGE);
            } else { // Delete button
                int confirmed = JOptionPane.showConfirmDialog(OS_AM_Grid.this,
                        "Delete appointment: " + appNo + "?", "Delete Appointment",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmed == JOptionPane.YES_OPTION) {
                    if (controller.AppointmentManagementController.delete(appNo)) {
                        tableModel.removeRow(modelRow);
                    } else {
                        JOptionPane.showMessageDialog(OS_AM_Grid.this,
                                "Couldn't delete appointment " + appNo + " from the database.",
                                "Delete Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void applySearchIcon() {
        txtSearch.setBorder(null);
        IconFactory.installPlaceholder(txtSearch, "Search here...");
        if (icoSearch != null) {
            lblSearchIcon.setIcon(icoSearch);
            lblSearchIcon.setText("");
        }
        lblSearchIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSearchIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                applyFilter();
            }
        });
    }

    private void setupTable() {
        tableModel = (DefaultTableModel) tblAppointments.getModel();
        sorter = new TableRowSorter<>(tableModel);
        tblAppointments.setRowSorter(sorter);

        tblAppointments.setFont(FONT_DEFAULT);
        tblAppointments.setRowHeight(TABLE_ROW_HEIGHT);
        tblAppointments.setShowGrid(false);
        tblAppointments.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tblAppointments.getTableHeader().setFont(FONT_DEFAULT);
        tblAppointments.getTableHeader().setBackground(new Color(245, 245, 245));
        tblAppointments.getTableHeader().setReorderingAllowed(false);

        tblAppointments.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        tblAppointments.getColumnModel().getColumn(7).setCellRenderer(new ActionButtonsRenderer());
        tblAppointments.addMouseListener(new TableClickHandler());
        txtSearch.getDocument().addDocumentListener(new SearchDocumentListener());
    }

    // Backed by the real "appointments" table (db/schema.sql), via
    // AppointmentManagementController — every dentist's appointments,
    // office-wide. D_APP_Grid (a dentist's own "My Appointments") reads the
    // same table, filtered.

    /** Number of appointment records — used by the Office Staff dashboard tile badge. */
    public static int getRecordCount() {
        return controller.AppointmentManagementController.countAll();
    }

    private void populateSampleData() {
        tableModel.setRowCount(0);
        for (model.AppointmentModel a : controller.AppointmentManagementController.getAll()) {
            tableModel.addRow(new Object[]{
                a.getAppointmentId(), a.getPatientName(), a.getCity(), a.getMobileNo(), a.getDentistName(),
                a.getTreatmentType(), a.getDate() + " / " + a.getTime(), ""
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
        tblAppointments = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Appointment Management");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 170, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("Appointment Management");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 420, 40);

        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setBackground(new java.awt.Color(255, 152, 0));
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setBorderPainted(false);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        mainPanel.add(btnAdd);
        btnAdd.setBounds(800, 120, 120, 36);

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

        lblSearchIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSearchIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/search_icon.png"))); // NOI18N
        pnlSearchWrap.add(lblSearchIcon);
        lblSearchIcon.setBounds(150, 4, 26, 26);

        mainPanel.add(pnlSearchWrap);
        pnlSearchWrap.setBounds(165, 175, 180, 36);

        pnlTableWrap.setBackground(new java.awt.Color(255, 255, 255));
        pnlTableWrap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlTableWrap.setLayout(null);

        tblAppointments.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblAppointments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Appointment ID", "Patient Name", "Address", "Contact No", "Dentist Name", "Treatment Type", "Date & Time", "Action"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAppointments.setRowHeight(34);
        tblAppointments.setShowGrid(false);
        scrollPane.setViewportView(tblAppointments);

        pnlTableWrap.add(scrollPane);
        scrollPane.setBounds(1, 1, 878, 350);

        mainPanel.add(pnlTableWrap);
        pnlTableWrap.setBounds(50, 225, 880, 352);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OS_AM_1().setVisible(true);
        });
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        // Must carry the real signed-in user back — the no-arg constructor
        // passes null, which silently breaks any "who's logged in" UI
        // reachable from that dashboard afterward (e.g. Edit Profile).
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OfficeStaff_Dashboard(controller.AppController.getCurrentUser()).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_AM_Grid().setVisible(true);
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
    private javax.swing.JTable tblAppointments;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
