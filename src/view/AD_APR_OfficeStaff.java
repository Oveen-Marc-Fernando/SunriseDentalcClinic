package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Administration &gt; Approvals &gt; Office Staff — the Administration-only
 * approve/decline screen for the {@code approvals} table Office Staff
 * submits into via OS_APM_Add (see ApprovalController's own javadoc: office
 * staff could only ever view/create requests here, never decide them — this
 * screen is that missing "elsewhere").
 *
 * @author oveen
 */
public class AD_APR_OfficeStaff extends javax.swing.JFrame {

    private static final Font FONT_DEFAULT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Color COLOR_STRIPE = new Color(240, 240, 240);
    private static final int ACTIONS_COLUMN = 5;

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public AD_APR_OfficeStaff() {
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40));
        IconFactory.roundCorners(navBar, 30);
        installTabBar();
        applySearchIcon();
        setupTable();
        populateData();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void installTabBar() {
        TabBarPanel tabs = new TabBarPanel(
                new String[]{"Office Staff Approvals", "Dentist Leave", "User Login", "Supply Requests"}, null, 0);
        tabs.setBounds(400, 155, tabs.getPreferredSize().width, 40);
        mainPanel.add(tabs);
        tabs.setOnTabClick(idx -> {
            dispose();
            switch (idx) {
                case 0: new AD_APR_OfficeStaff().setVisible(true); break;
                case 1: new AD_APR_DentistLeave().setVisible(true); break;
                case 2: new AD_APR_UserLogins().setVisible(true); break;
                case 3: new AD_APR_SupplyRequest().setVisible(true); break;
                default: break;
            }
        });
        mainPanel.setComponentZOrder(tabs, 0);
    }

    private class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if (!sel) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            }
            return c;
        }
    }

    /**
     * Actions column — a green "Approve" / red "Reject" button pair while
     * the request is still Pending; once it's been decided, that pair
     * collapses into a single white, disabled, no-op button that just
     * states the outcome ("Approved" / "Declined") instead of leaving both
     * buttons sitting there.
     */
    private class ActionButtonsRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel panel = new JPanel(null);
            int modelRow = t.convertRowIndexToModel(row);
            String status = String.valueOf(t.getModel().getValueAt(modelRow, 3));
            boolean pending = "Pending".equalsIgnoreCase(status);

            if (pending) {
                javax.swing.JButton btnApprove = IconFactory.actionButton(null, new Color(0, 168, 84), "Approve");
                btnApprove.setText("Approve");
                btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 10));
                btnApprove.setForeground(Color.WHITE);
                btnApprove.setBounds(4, 3, 68, 28);

                javax.swing.JButton btnReject = IconFactory.actionButton(null, new Color(220, 53, 69), "Reject");
                btnReject.setText("Reject");
                btnReject.setFont(new Font("Segoe UI", Font.BOLD, 10));
                btnReject.setForeground(Color.WHITE);
                btnReject.setBounds(76, 3, 60, 28);

                panel.add(btnApprove);
                panel.add(btnReject);
            } else {
                javax.swing.JButton btnDecided = IconFactory.actionButton(null, Color.WHITE, null);
                btnDecided.setText(status);
                btnDecided.setFont(new Font("Segoe UI", Font.BOLD, 10));
                btnDecided.setForeground(new Color(140, 140, 140));
                btnDecided.setEnabled(false);
                btnDecided.setBounds(4, 3, 132, 28);
                panel.add(btnDecided);
            }
            panel.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            return panel;
        }
    }

    private class TableClickHandler extends java.awt.event.MouseAdapter {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            int viewCol = tblGrid.columnAtPoint(e.getPoint());
            int viewRow = tblGrid.rowAtPoint(e.getPoint());
            if (viewRow < 0 || viewCol != ACTIONS_COLUMN) return;
            int modelRow = tblGrid.convertRowIndexToModel(viewRow);
            if (!"Pending".equalsIgnoreCase(String.valueOf(tableModel.getValueAt(modelRow, 3)))) {
                return; // already decided — buttons are locked/white, not clickable
            }
            String approvalId = String.valueOf(tableModel.getValueAt(modelRow, 0));

            java.awt.Rectangle cellRect = tblGrid.getCellRect(viewRow, viewCol, false);
            int xInCell = e.getX() - cellRect.x;
            boolean approve = xInCell < 74;

            boolean ok = approve ? controller.ApprovalController.approve(approvalId)
                                  : controller.ApprovalController.decline(approvalId);
            if (ok) {
                tableModel.setValueAt(approve ? "Approved" : "Declined", modelRow, 3);
            } else {
                IconFactory.showErrorDialog(AD_APR_OfficeStaff.this,
                        "Couldn't update approval " + approvalId + " — the database may be unreachable.", null);
            }
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
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
    }

    private void applySearchIcon() {
        txtSearch.setBorder(null);
        IconFactory.installPlaceholder(txtSearch, "Search here...");
        lblSearchIcon.setIcon(IconFactory.search(new Color(110, 110, 110), 16));
        lblSearchIcon.setText("");
        lblSearchIcon.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        lblSearchIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { applyFilter(); }
        });
    }

    private void setupTable() {
        tableModel = (DefaultTableModel) tblGrid.getModel();
        sorter = new TableRowSorter<>(tableModel);
        tblGrid.setRowSorter(sorter);

        tblGrid.setFont(FONT_DEFAULT);
        tblGrid.setRowHeight(34);
        tblGrid.setShowGrid(false);
        tblGrid.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tblGrid.getTableHeader().setFont(FONT_DEFAULT);
        tblGrid.getTableHeader().setBackground(new Color(245, 245, 245));
        tblGrid.getTableHeader().setReorderingAllowed(false);

        tblGrid.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        tblGrid.getColumnModel().getColumn(ACTIONS_COLUMN).setCellRenderer(new ActionButtonsRenderer());
        tblGrid.addMouseListener(new TableClickHandler());

        txtSearch.getDocument().addDocumentListener(new SearchDocumentListener());
    }

    // Backed by the real "approvals" table (db/schema.sql), via ApprovalController.
    private void populateData() {
        tableModel.setRowCount(0);
        for (model.ApprovalModel a : controller.ApprovalController.getAll()) {
            tableModel.addRow(new Object[]{
                dash(a.getApprovalId()), dash(a.getDescription()), dash(a.getRemarks()),
                dash(a.getStatus()), dash(a.getApprovalDate()), ""
            });
        }
    }

    private static String dash(String v) {
        return v == null || v.trim().isEmpty() ? "-" : v;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        lblSectionTitle = new javax.swing.JLabel();
        pnlSearchWrap = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        lblSearchIcon = new javax.swing.JLabel();
        pnlTableWrap = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tblGrid = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental — Approvals — Office Staff");
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
        lblTitle.setText("Approvals");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 300, 40);

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
        btnBack.setBounds(50, 215, 100, 36);

        lblSectionTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSectionTitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSectionTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSectionTitle.setText("Office Staff Approvals");
        mainPanel.add(lblSectionTitle);
        lblSectionTitle.setBounds(330, 215, 300, 36);

        pnlSearchWrap.setBackground(new java.awt.Color(255, 255, 255));
        pnlSearchWrap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlSearchWrap.setLayout(null);

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtSearch.setBorder(null);
        pnlSearchWrap.add(txtSearch);
        txtSearch.setBounds(8, 6, 190, 24);

        lblSearchIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlSearchWrap.add(lblSearchIcon);
        lblSearchIcon.setBounds(200, 4, 26, 26);

        mainPanel.add(pnlSearchWrap);
        pnlSearchWrap.setBounds(720, 215, 230, 36);

        pnlTableWrap.setBackground(new java.awt.Color(255, 255, 255));
        pnlTableWrap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlTableWrap.setLayout(null);

        tblGrid.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblGrid.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Approval ID", "Description", "Remarks", "Approval Status", "Approval Date", "Actions"
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
        tblGrid.setRowHeight(34);
        tblGrid.setShowGrid(false);
        scrollPane.setViewportView(tblGrid);
        if (tblGrid.getColumnModel().getColumnCount() > 0) {
            tblGrid.getColumnModel().getColumn(0).setPreferredWidth(100);
            tblGrid.getColumnModel().getColumn(1).setPreferredWidth(190);
            tblGrid.getColumnModel().getColumn(2).setPreferredWidth(180);
            tblGrid.getColumnModel().getColumn(3).setPreferredWidth(120);
            tblGrid.getColumnModel().getColumn(4).setPreferredWidth(120);
            tblGrid.getColumnModel().getColumn(5).setPreferredWidth(150);
        }

        pnlTableWrap.add(scrollPane);
        scrollPane.setBounds(1, 1, 898, 400);

        mainPanel.add(pnlTableWrap);
        pnlTableWrap.setBounds(50, 265, 900, 402);

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

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new AD_APR_OfficeStaff().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSearchIcon;
    private javax.swing.JLabel lblSectionTitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlSearchWrap;
    private javax.swing.JPanel pnlTableWrap;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblGrid;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
