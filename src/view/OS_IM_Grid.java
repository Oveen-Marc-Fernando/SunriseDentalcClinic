package view;

import java.awt.Color;
import java.awt.Component;
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
 * OS_IM_Grid – Office Staff: Inventory Management grid view.
 * Layout defined in OS_IM_Grid.form (NetBeans GUI Builder compatible).
 * Custom table behaviour (model, renderers, icons) set up outside GEN blocks.
 *
 * @author oveen
 */
public class OS_IM_Grid extends javax.swing.JFrame {

    private static final Font FONT_DEFAULT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Color COLOR_STRIPE = new Color(240, 240, 240);
    private static final Color COLOR_BTN_UPDATE = new Color(40, 167, 69);
    private static final Color COLOR_BTN_DELETE = new Color(220, 53, 69);
    private static final int TABLE_ROW_HEIGHT = 34;
    private static final int[] TABLE_COLUMN_WIDTHS = {85, 125, 95, 65, 95, 85, 105, 95, 105};

    private javax.swing.Icon icoSearch;
    private javax.swing.Icon icoUpdate;
    private javax.swing.Icon icoDelete;

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public OS_IM_Grid() {
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
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
    }

    private class TableClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = tblInventory.columnAtPoint(e.getPoint());
            int row = tblInventory.rowAtPoint(e.getPoint());
            if (row < 0) return;

            int modelRow = tblInventory.convertRowIndexToModel(row);
            String productName = tableModel.getValueAt(modelRow, 1).toString();

            if (col == 8) { // Action column
                handleActionClick(e, row, col, productName, modelRow);
            }
        }

        private void handleActionClick(MouseEvent e, int row, int col, String productName, int modelRow) {
            java.awt.Rectangle cellRect = tblInventory.getCellRect(row, col, false);
            int xInCell = e.getX() - cellRect.x;

            if (xInCell < 48) { // Edit button
                JOptionPane.showMessageDialog(OS_IM_Grid.this,
                        "Edit Product: " + productName, "Edit Product",
                        JOptionPane.INFORMATION_MESSAGE);
            } else { // Delete button
                int confirmed = JOptionPane.showConfirmDialog(OS_IM_Grid.this,
                        "Delete product: " + productName + "?", "Delete Product",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmed == JOptionPane.YES_OPTION) {
                    String productId = String.valueOf(tableModel.getValueAt(modelRow, 0));
                    if (controller.InventoryManagementController.delete(productId)) {
                        tableModel.removeRow(modelRow);
                    } else {
                        JOptionPane.showMessageDialog(OS_IM_Grid.this,
                                "Couldn't delete " + productName + " from the database.",
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
    }

    private void setupTable() {
        tableModel = (DefaultTableModel) tblInventory.getModel();
        sorter = new TableRowSorter<>(tableModel);
        tblInventory.setRowSorter(sorter);

        tblInventory.setFont(FONT_DEFAULT);
        tblInventory.setRowHeight(TABLE_ROW_HEIGHT);
        tblInventory.setShowGrid(false);
        tblInventory.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tblInventory.getTableHeader().setFont(FONT_DEFAULT);
        tblInventory.getTableHeader().setBackground(new Color(245, 245, 245));
        tblInventory.getTableHeader().setReorderingAllowed(false);

        tblInventory.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        for (int i = 0; i < TABLE_COLUMN_WIDTHS.length; i++) {
            tblInventory.getColumnModel().getColumn(i).setPreferredWidth(TABLE_COLUMN_WIDTHS[i]);
        }

        tblInventory.getColumnModel().getColumn(8).setCellRenderer(new ActionButtonsRenderer());
        tblInventory.addMouseListener(new TableClickHandler());
        txtSearch.getDocument().addDocumentListener(new SearchDocumentListener());
    }

    // Backed by the real "inventory" table (db/schema.sql), via
    // InventoryManagementController — the same catalog Request Supplies
    // (D_RS_Grid) and Supply Requests read from.

    /** Number of inventory records — used by the Office Staff dashboard tile badge. */
    public static int getRecordCount() {
        return controller.InventoryManagementController.count();
    }

    private void populateSampleData() {
        if (tableModel.getRowCount() == 0) {
            for (model.InventoryModel item : controller.InventoryManagementController.getAll()) {
                tableModel.addRow(new Object[]{
                    item.getProductId(), item.getProductName(), item.getProductType(), item.getQuantity(),
                    item.getManufactureDate(), item.getExpireDate(), item.getSupplierName(),
                    item.getContactNumber(), ""
                });
            }
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
        tblInventory = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Inventory Management");
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
        lblTitle.setText("Inventory Management");
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

        tblInventory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Product Name", "Product Type", "Quantity", "Manufacture Date", "Expire Date", "Supplier Name", "Contact Number", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInventory.setRowHeight(34);
        tblInventory.setShowGrid(false);
        scrollPane.setViewportView(tblInventory);

        pnlTableWrap.add(scrollPane);
        scrollPane.setBounds(1, 1, 878, 350);

        mainPanel.add(pnlTableWrap);
        pnlTableWrap.setBounds(50, 225, 880, 352);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        new OS_IM_1().setVisible(true);
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
                new OS_IM_Grid().setVisible(true);
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
    private javax.swing.JTable tblInventory;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
