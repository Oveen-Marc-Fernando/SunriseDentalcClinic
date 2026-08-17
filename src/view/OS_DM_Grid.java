package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 *
 * @author oveen
 */
@SuppressWarnings({"rawtypes", "unchecked"}) // raw Class[]/getColumnClass() below is NetBeans' own
// TableModelEditor codegen inside the GEN block; suppressing here (outside GEN) survives
// form regeneration instead of an in-block annotation, which NetBeans would strip.
public class OS_DM_Grid extends javax.swing.JFrame {

    // =========================================================================
    // Constants
    // =========================================================================
    private static final Font FONT_DEFAULT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Color COLOR_STRIPE = new Color(240, 240, 240);
    private static final Color COLOR_LINK = new Color(30, 144, 255);
    private static final Color COLOR_BTN_UPDATE = new Color(40, 167, 69);
    private static final Color COLOR_BTN_DELETE = new Color(220, 53, 69);
    private static final int TABLE_ROW_HEIGHT = 34;
    private static final int[] TABLE_COLUMN_WIDTHS = {130, 80, 80, 110, 140, 120, 90, 110, 80};

    // =========================================================================
    // Icon fields (loaded once, reused in renderers)
    // =========================================================================
    private javax.swing.Icon icoSearch;
    private javax.swing.Icon icoUpdate;
    private javax.swing.Icon icoDelete;

    // Table helpers
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // =========================================================================
    // Constructor
    // =========================================================================
    public OS_DM_Grid() {
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

    // =========================================================================
    // Icon loading  (scaled to correct sizes)
    // =========================================================================
    private void loadIcons() {
        icoSearch  = IconFactory.search(new Color(110, 110, 110), 16);
        icoUpdate  = IconFactory.pencil(Color.WHITE, 15);
        icoDelete  = IconFactory.trash(Color.WHITE, 15);
    }

    // =========================================================================
    // Custom Renderers
    // =========================================================================
    
    /**
     * Renderer for alternating row colors
     */
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

    /**
     * Renderer for Details column (blue hyperlink)
     */
    private class DetailsLinkRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JLabel lbl = new JLabel("<html><u>View details</u></html>");
            lbl.setForeground(COLOR_LINK);
            lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            return lbl;
        }
    }

    /**
     * Renderer for Action column (Update/Delete buttons with rounded color backgrounds)
     */
    private class ActionButtonsRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel panel = new JPanel(null);
            JButton btnUpdate = IconFactory.actionButton(icoUpdate, COLOR_BTN_UPDATE, "Update");
            JButton btnDelete = IconFactory.actionButton(icoDelete, COLOR_BTN_DELETE, "Delete");
            btnUpdate.setBounds(14, 3, 38, 28);
            btnDelete.setBounds(58, 3, 38, 28);
            panel.add(btnUpdate);
            panel.add(btnDelete);
            panel.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            return panel;
        }
    }

    /**
     * Renderer for License column \u2013 shows a small colored icon-only badge
     * (green check / red cross), no text.
     */
    private class LicenseIconRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            boolean active = isLicenseActive(v);
            JPanel panel = new JPanel(new java.awt.GridBagLayout());
            panel.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            panel.add(IconFactory.statusDot(active));
            return panel;
        }
    }

    /** True when the given License cell value represents an active/verified license. */
    private static boolean isLicenseActive(Object v) {
        return v != null && (Boolean.TRUE.equals(v) || v.toString().equalsIgnoreCase("Active")
                || v.toString().equalsIgnoreCase("Verified") || v.toString().equalsIgnoreCase("true"));
    }

    // =========================================================================
    // Event Listeners
    // =========================================================================

    /**
     * Document listener for real-time search filtering
     */
    private class SearchDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            applyFilter();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            applyFilter();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            applyFilter();
        }

        private void applyFilter() {
            if (sorter == null) return;
            String text = IconFactory.isPlaceholderShowing(txtSearch) ? "" : txtSearch.getText().trim();
            sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
        }
    }

    /**
     * Mouse adapter for table cell interactions
     */
    private class TableClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = tblDentists.columnAtPoint(e.getPoint());
            int row = tblDentists.rowAtPoint(e.getPoint());
            if (row < 0) return;

            int modelRow = tblDentists.convertRowIndexToModel(row);
            String dentistName = tableModel.getValueAt(modelRow, 0).toString();

            switch (col) {
                case 6: // Details column
                    handleDetailsClick(dentistName);
                    break;
                case 7: // Action column
                    handleActionClick(e, row, col, dentistName, modelRow);
                    break;
                case 8: // License column
                    handleLicenseToggle(modelRow);
                    break;
            }
        }

        private void handleDetailsClick(String name) {
            JOptionPane.showMessageDialog(OS_DM_Grid.this,
                    "Details for: " + name, "Dentist Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        private void handleActionClick(MouseEvent e, int row, int col, String name, int modelRow) {
            java.awt.Rectangle cellRect = tblDentists.getCellRect(row, col, false);
            int xInCell = e.getX() - cellRect.x;

            if (xInCell < 52) { // Update button
                JOptionPane.showMessageDialog(OS_DM_Grid.this,
                        "Update: " + name, "Update Dentist",
                        JOptionPane.INFORMATION_MESSAGE);
            } else { // Delete button
                int confirmed = JOptionPane.showConfirmDialog(OS_DM_Grid.this,
                        "Delete dentist: " + name + "?", "Delete",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmed == JOptionPane.YES_OPTION) {
                    String doctorId = String.valueOf(tableModel.getValueAt(modelRow, 1));
                    if (controller.DentistManagementController.deleteDentist(doctorId)) {
                        tableModel.removeRow(modelRow);
                    } else {
                        JOptionPane.showMessageDialog(OS_DM_Grid.this,
                                "Couldn't delete " + name + " — they still have appointments on record. "
                                + "Reassign or remove those first.",
                                "Delete Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        private void handleLicenseToggle(int modelRow) {
            Object val = tableModel.getValueAt(modelRow, 8);
            boolean currentStatus = isLicenseActive(val);
            tableModel.setValueAt(currentStatus ? "Inactive" : "Active", modelRow, 8);
        }
    }

    // =========================================================================
    // Apply search icon to the label inside search panel
    // =========================================================================
    private void applySearchIcon() {
        txtSearch.setBorder(null);
        IconFactory.installPlaceholder(txtSearch, "Search here...");
        if (icoSearch != null) {
            lblSearchIcon.setIcon(icoSearch);
            lblSearchIcon.setText("");
        }
    }

    // =========================================================================
    // Table setup  – runs OUTSIDE the GEN block so NetBeans won't overwrite it
    // =========================================================================
    private void setupTable() {
        tableModel = (DefaultTableModel) tblDentists.getModel();
        sorter = new TableRowSorter<>(tableModel);
        tblDentists.setRowSorter(sorter);

        // ── Table appearance ──────────────────────────────────────────────────
        tblDentists.setFont(FONT_DEFAULT);
        tblDentists.setRowHeight(TABLE_ROW_HEIGHT);
        tblDentists.setShowGrid(false);
        tblDentists.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tblDentists.getTableHeader().setFont(FONT_DEFAULT);
        tblDentists.getTableHeader().setBackground(new Color(245, 245, 245));
        tblDentists.getTableHeader().setReorderingAllowed(false);

        // Apply default renderer with alternating row colors
        tblDentists.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        // ── Column widths ─────────────────────────────────────────────────────
        for (int i = 0; i < TABLE_COLUMN_WIDTHS.length; i++) {
            tblDentists.getColumnModel().getColumn(i).setPreferredWidth(TABLE_COLUMN_WIDTHS[i]);
        }

        // ── Custom renderers for specific columns ─────────────────────────────
        tblDentists.getColumnModel().getColumn(6).setCellRenderer(new DetailsLinkRenderer());
        tblDentists.getColumnModel().getColumn(7).setCellRenderer(new ActionButtonsRenderer());
        tblDentists.getColumnModel().getColumn(8).setCellRenderer(new LicenseIconRenderer());

        // ── Mouse click handler ───────────────────────────────────────────────
        tblDentists.addMouseListener(new TableClickHandler());

        // ── Live search ───────────────────────────────────────────────────────
        txtSearch.getDocument().addDocumentListener(new SearchDocumentListener());
    }

    // =========================================================================
    // Backed by the real "dentists" table (db/schema.sql), via
    // DentistManagementController — the same directory Appointment
    // Management's Step 2 and every dentist's own "My Profile" read/write.
    // =========================================================================
    private static final String DETAILS_LINK =
            "<html><div align='center'><font color='#1E90FF'><u>View details</u></font></div></html>";

    /** Number of dentist records — used by the Office Staff dashboard tile badge. */
    public static int getRecordCount() {
        return controller.DentistManagementController.getDirectory().size();
    }

    private void populateSampleData() {
        if (tableModel.getRowCount() == 0) {
            for (model.DentistModel d : controller.DentistManagementController.getDirectory().values()) {
                tableModel.addRow(new Object[]{
                    d.getFullName(), d.getDoctorId(), d.getSlmcNo(), d.getMobileNo(), d.getEmail(),
                    d.getConsultationFee(), DETAILS_LINK, "", d.getLicenseStatus()
                });
            }
        }
    }

    // =========================================================================
    // Generated Code  – layout only, no custom model/renderer code here
    // =========================================================================
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
        tblDentists = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Dentist Management");
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
        lblTitle.setText("Dentist Management");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 400, 40);

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

        tblDentists.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblDentists.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Dentist Name", "Dentist ID", "SLMC No", "Mobile No", "Email", "Consultation Fee", "Details", "Action", "License"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDentists.setRowHeight(34);
        tblDentists.setShowGrid(false);
        scrollPane.setViewportView(tblDentists);
        if (tblDentists.getColumnModel().getColumnCount() > 0) {
            tblDentists.getColumnModel().getColumn(0).setPreferredWidth(130);
            tblDentists.getColumnModel().getColumn(1).setPreferredWidth(80);
            tblDentists.getColumnModel().getColumn(2).setPreferredWidth(80);
            tblDentists.getColumnModel().getColumn(3).setPreferredWidth(110);
            tblDentists.getColumnModel().getColumn(4).setPreferredWidth(140);
            tblDentists.getColumnModel().getColumn(5).setPreferredWidth(120);
            tblDentists.getColumnModel().getColumn(6).setPreferredWidth(90);
            tblDentists.getColumnModel().getColumn(7).setPreferredWidth(110);
            tblDentists.getColumnModel().getColumn(8).setPreferredWidth(80);
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
        new OS_DM_1().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        new OfficeStaff_Dashboard().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    @SuppressWarnings("unused")
    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // No-op: SearchDocumentListener already filters live on every keystroke,
        // so pressing Enter here has nothing extra to do.
    }//GEN-LAST:event_txtSearchActionPerformed

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
    private javax.swing.JTable tblDentists;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
