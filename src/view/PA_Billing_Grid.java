package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Patient &gt; My Billings — this patient's own billing history, reached
 * from the Patient Dashboard's "My Billings" tile. Same best-effort
 * name-match convention as the dashboard's own badge count
 * (PatientController.getMyBillingCount()) — there's no real
 * login-to-patient-record foreign key in this app.
 *
 * @author oveen
 */
public class PA_Billing_Grid extends javax.swing.JFrame {

    private static final Font FONT_DEFAULT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Color COLOR_STRIPE = new Color(240, 240, 240);
    private static final Color COLOR_DOWNLOAD = new Color(0, 122, 255);
    private static final int RECEIPT_COLUMN = 6;

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private final String patientName;

    /** Backward-compatible no-arg entry point (e.g. {@code main()}) — shows no rows without a logged-in patient. */
    public PA_Billing_Grid() {
        this(null);
    }

    public PA_Billing_Grid(model.User user) {
        this.patientName = (user != null && user.getFullName() != null) ? user.getFullName() : null;
        initComponents();
        lblLogo.setIcon(IconFactory.brandLogo(130, 40));
        IconFactory.roundCorners(navBar, 30);
        applySearchIcon();
        setupTable();
        populateData();
        setSize(1016, 739);
        setLocationRelativeTo(null);
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

    /** "Receipt" column — a solid blue "Download" button, same chrome as OS_BM_Grid's Print button. */
    private class ReceiptButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel panel = new JPanel(null);
            JButton btnDownload = IconFactory.actionButton(null, COLOR_DOWNLOAD, "Download receipt");
            btnDownload.setText("Download");
            btnDownload.setForeground(Color.WHITE);
            btnDownload.setFont(FONT_DEFAULT.deriveFont(Font.BOLD));
            btnDownload.setBounds((t.getColumnModel().getColumn(col).getWidth() - 90) / 2, 3, 90, 28);
            panel.add(btnDownload);
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
        tableModel = (DefaultTableModel) tblBillings.getModel();
        sorter = new TableRowSorter<>(tableModel);
        tblBillings.setRowSorter(sorter);

        tblBillings.setFont(FONT_DEFAULT);
        tblBillings.setRowHeight(34);
        tblBillings.setShowGrid(false);
        tblBillings.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tblBillings.getTableHeader().setFont(FONT_DEFAULT);
        tblBillings.getTableHeader().setBackground(new Color(245, 245, 245));
        tblBillings.getTableHeader().setReorderingAllowed(false);

        tblBillings.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        tblBillings.getColumnModel().getColumn(RECEIPT_COLUMN).setCellRenderer(new ReceiptButtonRenderer());

        tblBillings.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int viewRow = tblBillings.rowAtPoint(e.getPoint());
                int viewCol = tblBillings.columnAtPoint(e.getPoint());
                if (viewRow < 0 || viewCol != RECEIPT_COLUMN) return;
                int modelRow = tblBillings.convertRowIndexToModel(viewRow);
                openReceipt(String.valueOf(tableModel.getValueAt(modelRow, 0)));
            }
        });

        txtSearch.getDocument().addDocumentListener(new SearchDocumentListener());
    }

    private void openReceipt(String billingId) {
        model.BillingModel bill = controller.BillingManagementController.getAll().stream()
                .filter(b -> billingId.equals(b.getBillingId()))
                .findFirst()
                .orElse(null);
        if (bill == null) {
            IconFactory.showErrorDialog(this, "Couldn't find bill " + billingId + " in the database.", null);
            return;
        }
        new BillPreviewDialog(this, bill, false, null).setVisible(true);
    }

    // Backed by the real "billings" table (db/schema.sql), via
    // BillingManagementController — filtered down to this logged-in
    // patient's own rows (best-effort exact name match, same convention as
    // the dashboard's own badge count).
    private void populateData() {
        tableModel.setRowCount(0);
        if (patientName == null) {
            return; // no logged-in patient context (e.g. main()) — nothing to show
        }
        for (model.BillingModel b : controller.BillingManagementController.getAll()) {
            if (!patientName.equalsIgnoreCase(b.getPatientName())) continue;
            tableModel.addRow(new Object[]{
                dash(b.getBillingId()), dash(b.getAppointmentId()),
                currency(b.getAppointmentCharges()), currency(b.getClinicalTotal()), currency(b.getMedicineTotal()),
                currency(b.getTotalBillAmount()), "Download"
            });
        }
    }

    private static String currency(double v) {
        return controller.BillingManagementController.formatCurrency(v);
    }

    private static String dash(String v) {
        return v == null || v.trim().isEmpty() ? "-" : v;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblUserIcon = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        pnlSearchWrap = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        lblSearchIcon = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        pnlTableWrap = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tblBillings = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental — My Billings");
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 10, 160, 40);

        lblUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/login_scaled.png"))); // NOI18N
        lblUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        navBar.add(lblUserIcon);
        lblUserIcon.setBounds(850, 10, 40, 40);

        mainPanel.add(navBar);
        navBar.setBounds(40, 30, 920, 60);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setText("My Billings");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 110, 420, 40);

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
        txtSearch.setBounds(8, 6, 190, 24);

        lblSearchIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlSearchWrap.add(lblSearchIcon);
        lblSearchIcon.setBounds(200, 4, 26, 26);

        mainPanel.add(pnlSearchWrap);
        pnlSearchWrap.setBounds(720, 175, 230, 36);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(231, 115, 36));
        lblSubtitle.setText("Billing History");
        mainPanel.add(lblSubtitle);
        lblSubtitle.setBounds(370, 170, 250, 40);

        pnlTableWrap.setBackground(new java.awt.Color(255, 255, 255));
        pnlTableWrap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlTableWrap.setLayout(null);

        tblBillings.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblBillings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Billing ID", "Appointment ID", "Appointment Charges", "Clinical Charges", "Medicine Charges", "Total Amount", "Receipt"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBillings.setRowHeight(34);
        tblBillings.setShowGrid(false);
        scrollPane.setViewportView(tblBillings);
        if (tblBillings.getColumnModel().getColumnCount() > 0) {
            tblBillings.getColumnModel().getColumn(0).setPreferredWidth(90);
            tblBillings.getColumnModel().getColumn(1).setPreferredWidth(110);
            tblBillings.getColumnModel().getColumn(2).setPreferredWidth(140);
            tblBillings.getColumnModel().getColumn(3).setPreferredWidth(120);
            tblBillings.getColumnModel().getColumn(4).setPreferredWidth(130);
            tblBillings.getColumnModel().getColumn(5).setPreferredWidth(120);
            tblBillings.getColumnModel().getColumn(6).setPreferredWidth(120);
        }

        pnlTableWrap.add(scrollPane);
        scrollPane.setBounds(1, 1, 898, 350);

        mainPanel.add(pnlTableWrap);
        pnlTableWrap.setBounds(50, 225, 900, 352);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Patient_Dashboard(controller.AppController.getCurrentUser()).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new PA_Billing_Grid().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSearchIcon;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlSearchWrap;
    private javax.swing.JPanel pnlTableWrap;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblBillings;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
