package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * "Request Supplies" — browse current Inventory, then either add a new
 * supply request (D_RS_Add) or review requests already made (D_RS_History).
 * Reached from the Dentist Dashboard's "Request Supplies" tile.
 * Layout defined in D_RS_Grid.form (NetBeans GUI Builder compatible).
 *
 * @author oveen
 */
public class D_RS_Grid extends javax.swing.JFrame {

    private static final java.awt.Font FONT_DEFAULT = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
    private static final Color COLOR_STRIPE = new Color(240, 240, 240);
    private static final Color COLOR_LOW_STOCK = new Color(198, 40, 40);
    private static final Color COLOR_REORDER_SOON = new Color(191, 108, 0);
    private static final Color COLOR_STOCK_OK = new Color(30, 30, 30);

    // Quantity column + the thresholds that drive its low-stock coloring —
    // this table is view-only (see setupTable()): the dentist checks what's
    // on hand here and decides what to request, they never edit a row here.
    private static final int QUANTITY_COLUMN = 4;
    private static final int LOW_STOCK_MAX = 10;
    private static final int REORDER_SOON_MAX = 30;

    // Backed by the real "inventory" table (db/schema.sql), via
    // InventoryManagementController — a dentist decides what to request by
    // looking at what's on hand. SupplyRequestController's Add Request
    // dropdowns read this same table, so what's browsable here always
    // matches what's pickable there.

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public D_RS_Grid() {
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        lblUserIcon.setIcon(IconFactory.userGlyph(Color.WHITE, 26)); // crisp vector glyph, no backdrop — sits directly on the black pill
        IconFactory.roundCorners(navBar, 30); // fully rounded pill — radius = half the bar's height
        bindUserIcon();
        applySearchIcon();
        setupTable();
        populateData();
        setSize(1016, 739);
        setLocationRelativeTo(null);
    }

    private void bindUserIcon() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblUserIcon.setCursor(hand);
        lblUserIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                IconFactory.showProfileMenu(D_RS_Grid.this, lblUserIcon,
                        () -> javax.swing.SwingUtilities.invokeLater(() -> {
                            dispose();
                            new D_Profile_1((model.User) null).setVisible(true);
                        }),
                        () -> {
                            dispose();
                            controller.AppController.showPublicDashboard();
                        });
            }
        });
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
     * Quantity column only — colors the stock count so a dentist can spot
     * what needs requesting at a glance: red at/under {@link #LOW_STOCK_MAX},
     * amber up to {@link #REORDER_SOON_MAX}, plain otherwise. Purely a
     * viewing aid; it never makes the cell editable.
     */
    private class StockLevelRenderer extends AlternatingRowRenderer {
        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            int qty = parseQuantity(v);
            if (qty >= 0 && qty <= LOW_STOCK_MAX) {
                c.setForeground(COLOR_LOW_STOCK);
                c.setFont(c.getFont().deriveFont(java.awt.Font.BOLD));
            } else if (qty >= 0 && qty <= REORDER_SOON_MAX) {
                c.setForeground(COLOR_REORDER_SOON);
                c.setFont(c.getFont().deriveFont(java.awt.Font.BOLD));
            } else {
                c.setForeground(COLOR_STOCK_OK);
                c.setFont(c.getFont().deriveFont(java.awt.Font.PLAIN));
            }
            setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            return c;
        }
    }

    private static int parseQuantity(Object v) {
        try {
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (NumberFormatException e) {
            return -1;
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
        lblSearchIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSearchIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { applyFilter(); }
        });
    }

    private void setupTable() {
        tableModel = (DefaultTableModel) tblInventory.getModel();
        sorter = new TableRowSorter<>(tableModel);
        tblInventory.setRowSorter(sorter);

        tblInventory.setFont(FONT_DEFAULT);
        tblInventory.setRowHeight(34);
        tblInventory.setShowGrid(false);
        tblInventory.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tblInventory.getTableHeader().setFont(FONT_DEFAULT);
        tblInventory.getTableHeader().setBackground(new Color(245, 245, 245));
        tblInventory.getTableHeader().setReorderingAllowed(false);

        tblInventory.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        tblInventory.getColumnModel().getColumn(QUANTITY_COLUMN).setCellRenderer(new StockLevelRenderer());

        // View-only: this table is for checking what's in stock before
        // deciding what to request, never for selecting/editing a row —
        // there's no click handler and no way to end up in a cell editor,
        // but disabling selection too means it never even *looks*
        // interactive (no blue highlight suggesting you can act on a row).
        tblInventory.setRowSelectionAllowed(false);
        tblInventory.setColumnSelectionAllowed(false);
        tblInventory.setCellSelectionEnabled(false);
        tblInventory.setFocusable(false);

        txtSearch.getDocument().addDocumentListener(new SearchDocumentListener());
    }

    private void populateData() {
        tableModel.setRowCount(0);
        for (model.InventoryModel item : controller.InventoryManagementController.getAll()) {
            tableModel.addRow(new Object[]{
                item.getProductId(), item.getProductType(), item.getProductName(),
                item.getDescription(), item.getQuantity(), item.getExpireDate(), item.getManufactureDate()
            });
        }
    }

    /** Number of inventory records — mirrors the sibling grids' getRecordCount() convention. */
    public static int getRecordCount() {
        return controller.InventoryManagementController.count();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblUserIcon = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        btnAddRequest = new javax.swing.JButton();
        btnRequestHistory = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        pnlSearchWrap = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        lblSearchIcon = new javax.swing.JLabel();
        lblInventoryHeader = new javax.swing.JLabel();
        pnlTableWrap = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tblInventory = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Request Supplies");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 170, 40);

        lblUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/login_scaled.png"))); // NOI18N
        lblUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        navBar.add(lblUserIcon);
        lblUserIcon.setBounds(850, 10, 40, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("Request Supplies");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 320, 40);

        btnAddRequest.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAddRequest.setText("Add Request");
        btnAddRequest.setBackground(new java.awt.Color(231, 115, 36));
        btnAddRequest.setForeground(new java.awt.Color(255, 255, 255));
        btnAddRequest.setBorderPainted(false);
        btnAddRequest.setFocusPainted(false);
        btnAddRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRequestActionPerformed(evt);
            }
        });
        mainPanel.add(btnAddRequest);
        btnAddRequest.setBounds(660, 114, 130, 36);

        btnRequestHistory.setBackground(new java.awt.Color(0, 153, 0));
        btnRequestHistory.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRequestHistory.setForeground(new java.awt.Color(255, 255, 255));
        btnRequestHistory.setText("Request History");
        btnRequestHistory.setBorderPainted(false);
        btnRequestHistory.setFocusPainted(false);
        btnRequestHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRequestHistoryActionPerformed(evt);
            }
        });
        mainPanel.add(btnRequestHistory);
        btnRequestHistory.setBounds(800, 114, 150, 36);

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

        lblInventoryHeader.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblInventoryHeader.setForeground(new java.awt.Color(30, 30, 30));
        lblInventoryHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInventoryHeader.setText("Inventory");
        mainPanel.add(lblInventoryHeader);
        lblInventoryHeader.setBounds(50, 222, 880, 30);

        pnlTableWrap.setBackground(new java.awt.Color(255, 255, 255));
        pnlTableWrap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlTableWrap.setLayout(null);

        tblInventory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Inventory ID", "Product Type", "Product Name", "Description", "Quantity", "Expiry Date", "Manufacture Date"
            }
        ) {
            Class<?>[] types = new Class<?>[] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class<?> getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInventory.setRowHeight(34);
        tblInventory.setShowGrid(false);
        scrollPane.setViewportView(tblInventory);
        if (tblInventory.getColumnModel().getColumnCount() > 0) {
            tblInventory.getColumnModel().getColumn(0).setPreferredWidth(90);
            tblInventory.getColumnModel().getColumn(1).setPreferredWidth(110);
            tblInventory.getColumnModel().getColumn(2).setPreferredWidth(140);
            tblInventory.getColumnModel().getColumn(3).setPreferredWidth(220);
            tblInventory.getColumnModel().getColumn(4).setPreferredWidth(80);
            tblInventory.getColumnModel().getColumn(5).setPreferredWidth(110);
            tblInventory.getColumnModel().getColumn(6).setPreferredWidth(110);
        }

        pnlTableWrap.add(scrollPane);
        scrollPane.setBounds(1, 1, 878, 350);

        mainPanel.add(pnlTableWrap);
        pnlTableWrap.setBounds(50, 262, 880, 352);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRequestActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new D_RS_Add().setVisible(true);
        });
    }//GEN-LAST:event_btnAddRequestActionPerformed

    private void btnRequestHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRequestHistoryActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new D_RS_History().setVisible(true);
        });
    }//GEN-LAST:event_btnRequestHistoryActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        // Must carry the real signed-in user back — the no-arg constructor
        // passes null, which silently breaks every "my own data" screen
        // reachable from that dashboard afterward (empty grids/schedules).
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Dentist_Dashboard(controller.AppController.getCurrentUser()).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new D_RS_Grid().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRequest;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRequestHistory;
    private javax.swing.JLabel lblInventoryHeader;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSearchIcon;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlSearchWrap;
    private javax.swing.JPanel pnlTableWrap;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblInventory;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
