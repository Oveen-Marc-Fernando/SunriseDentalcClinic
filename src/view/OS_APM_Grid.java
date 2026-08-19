package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Office Staff - Approval Management Grid View.
 * Read-only: office staff can see whether a request is Approved / Declined /
 * Pending, but only Administration can act on approvals — so this grid shows
 * a single status icon per row instead of Approve/Decline controls.
 *
 * @author oveen
 */
public class OS_APM_Grid extends javax.swing.JFrame {

    private static final Font FONT_DEFAULT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Color COLOR_STRIPE = new Color(240, 240, 240);
    private static final int TABLE_ROW_HEIGHT = 36;

    private javax.swing.Icon icoSearch;

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public OS_APM_Grid() {
        loadIcons();
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        txtSearch.setText("");
        applySearchIcon();
        setupTable();
        populateSampleData();
        setSize(1016, 739);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void loadIcons() {
        icoSearch = IconFactory.search(new Color(110, 110, 110), 16);
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

    /**
     * Renderer for the Status column — a single read-only icon (green check
     * / red cross / amber clock) reflecting the "Approval Status" column.
     * Office staff can only view this; approving or declining is an
     * Administration-only action performed elsewhere.
     */
    private class StatusIconRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            int modelRow = t.convertRowIndexToModel(row);
            String status = String.valueOf(tableModel.getValueAt(modelRow, 3));
            JPanel panel = new JPanel(new java.awt.GridBagLayout());
            panel.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            panel.add(IconFactory.statusDot(status));
            return panel;
        }
    }

    private void applySearchIcon() {
        IconFactory.installPlaceholder(txtSearch, "Search here...");
        if (icoSearch != null) {
            lblSearchIcon.setIcon(icoSearch);
            lblSearchIcon.setText("");
        } else {
            lblSearchIcon.setText("Q");
        }
    }

    private void setupTable() {
        String[] cols = {"Approval ID", "Description", "Remarks", "Approval Status", "Approval Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // view-only grid — approving/declining is an Administration-only action
            }
        };
        tblApprovals.setModel(tableModel);
        tblApprovals.setFont(FONT_DEFAULT);
        tblApprovals.setRowHeight(TABLE_ROW_HEIGHT);
        tblApprovals.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblApprovals.getTableHeader().setReorderingAllowed(false);

        sorter = new TableRowSorter<>(tableModel);
        tblApprovals.setRowSorter(sorter);

        AlternatingRowRenderer altRenderer = new AlternatingRowRenderer();
        for (int i = 0; i < 5; i++) {
            tblApprovals.getColumnModel().getColumn(i).setCellRenderer(altRenderer);
        }

        tblApprovals.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblApprovals.getColumnModel().getColumn(1).setPreferredWidth(220);
        tblApprovals.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblApprovals.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblApprovals.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblApprovals.getColumnModel().getColumn(5).setPreferredWidth(110);

        tblApprovals.getColumnModel().getColumn(5).setCellRenderer(new StatusIconRenderer());

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { filter(); }
            @Override public void removeUpdate(DocumentEvent e)  { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String q = IconFactory.isPlaceholderShowing(txtSearch) ? "" : txtSearch.getText().trim();
                if (q.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + q));
            }
        });
    }

    // Backed by the real "approvals" table (db/schema.sql), via
    // ApprovalController.

    /** Number of approval records — used by the Office Staff dashboard tile badge. */
    public static int getRecordCount() {
        return controller.ApprovalController.getRecordCount();
    }

    private void populateSampleData() {
        tableModel.setRowCount(0);
        for (model.ApprovalModel a : controller.ApprovalController.getAll()) {
            tableModel.addRow(new Object[]{
                a.getApprovalId(), a.getDescription(), a.getRemarks(), a.getStatus(), a.getApprovalDate(), ""
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
        pnlSearch = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        lblSearchIcon = new javax.swing.JLabel();
        pnlTableWrap = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tblApprovals = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Approval Management");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 165, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("Approval Management");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 420, 40);

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

        pnlSearch.setBackground(new java.awt.Color(255, 255, 255));
        pnlSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlSearch.setLayout(null);

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtSearch.setBorder(null);
        pnlSearch.add(txtSearch);
        txtSearch.setBounds(8, 6, 140, 24);

        lblSearchIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/search_icon.png"))); // NOI18N
        lblSearchIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlSearch.add(lblSearchIcon);
        lblSearchIcon.setBounds(150, 4, 26, 26);

        mainPanel.add(pnlSearch);
        pnlSearch.setBounds(165, 175, 180, 36);

        pnlTableWrap.setBackground(new java.awt.Color(255, 255, 255));
        pnlTableWrap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlTableWrap.setLayout(null);

        tblApprovals.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblApprovals.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Approval ID", "Description", "Remarks", "Approval Status", "Approval Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblApprovals.setRowHeight(36);
        scrollPane.setViewportView(tblApprovals);
        if (tblApprovals.getColumnModel().getColumnCount() > 0) {
            tblApprovals.getColumnModel().getColumn(0).setPreferredWidth(100);
            tblApprovals.getColumnModel().getColumn(1).setPreferredWidth(220);
            tblApprovals.getColumnModel().getColumn(2).setPreferredWidth(200);
            tblApprovals.getColumnModel().getColumn(3).setPreferredWidth(130);
            tblApprovals.getColumnModel().getColumn(4).setPreferredWidth(120);
            tblApprovals.getColumnModel().getColumn(5).setPreferredWidth(110);
        }

        pnlTableWrap.add(scrollPane);
        scrollPane.setBounds(1, 1, 878, 350);

        mainPanel.add(pnlTableWrap);
        pnlTableWrap.setBounds(50, 225, 880, 352);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        // Must carry the real signed-in user back — the no-arg constructor
        // passes null, which silently breaks any "who's logged in" UI
        // reachable from that dashboard afterward (e.g. Edit Profile).
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OfficeStaff_Dashboard(controller.AppController.getCurrentUser()).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OS_APM_Add().setVisible(true);
        });
    }//GEN-LAST:event_btnAddActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_APM_Grid().setVisible(true);
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
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlTableWrap;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblApprovals;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
