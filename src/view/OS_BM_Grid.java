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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * OS_BM_Grid – Office Staff: Billing Management grid view.
 * Layout defined in OS_BM_Grid.form (NetBeans GUI Builder compatible).
 *
 * Lists bills with a one-click Print action and the same Edit / Delete
 * action-button pattern used by every other management grid in the app,
 * plus a working calculator docked next to the table so office staff can
 * total up services before generating a bill.
 *
 * @author oveen
 */
public class OS_BM_Grid extends javax.swing.JFrame {

    private static final Font FONT_DEFAULT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Color COLOR_STRIPE = new Color(240, 240, 240);
    private static final Color COLOR_BTN_UPDATE = new Color(40, 167, 69);
    private static final Color COLOR_BTN_DELETE = new Color(220, 53, 69);
    private static final Color COLOR_BTN_PRINT = new Color(0, 122, 255);
    private static final int TABLE_ROW_HEIGHT = 34;
    private static final int[] TABLE_COLUMN_WIDTHS = {110, 190, 130, 100, 110};

    private javax.swing.Icon icoSearch;
    private javax.swing.Icon icoUpdate;
    private javax.swing.Icon icoDelete;

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // ── Calculator state ────────────────────────────────────────────────────
    private String calcDisplayText = "0";
    private double calcAccumulator = 0;
    private String calcPendingOp = null;
    private boolean calcStartNew = true;
    private double calcMemory = 0;

    public OS_BM_Grid() {
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

    private class PrintButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel panel = new JPanel(null);
            JButton btnPrint = IconFactory.actionButton(null, COLOR_BTN_PRINT, "Print bill");
            btnPrint.setText("Print");
            btnPrint.setForeground(Color.WHITE);
            btnPrint.setFont(FONT_DEFAULT.deriveFont(Font.BOLD));
            btnPrint.setBounds(10, 3, 78, 28);
            panel.add(btnPrint);
            panel.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_STRIPE);
            return panel;
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

        private void applyFilter() {
            if (sorter == null) return;
            String text = IconFactory.isPlaceholderShowing(txtSearch) ? "" : txtSearch.getText().trim();
            sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
        }
    }

    private class TableClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = tblBillings.columnAtPoint(e.getPoint());
            int row = tblBillings.rowAtPoint(e.getPoint());
            if (row < 0) return;

            int modelRow = tblBillings.convertRowIndexToModel(row);
            String billingId = tableModel.getValueAt(modelRow, 0).toString();
            String patientName = tableModel.getValueAt(modelRow, 1).toString();

            if (col == 3) { // Print column
                printBill(billingId);
            } else if (col == 4) { // Action column
                handleActionClick(e, row, col, billingId, patientName, modelRow);
            }
        }

        private void handleActionClick(MouseEvent e, int row, int col, String billingId, String name, int modelRow) {
            java.awt.Rectangle cellRect = tblBillings.getCellRect(row, col, false);
            int xInCell = e.getX() - cellRect.x;

            if (xInCell < 48) { // Edit
                handleEditBill(billingId, modelRow);
            } else { // Delete
                IconFactory.showConfirmDialog(OS_BM_Grid.this, "Delete bill " + billingId + " for " + name + "?", "Delete", () -> {
                    if (controller.BillingManagementController.delete(billingId)) {
                        tableModel.removeRow(modelRow);
                    } else {
                        IconFactory.showErrorDialog(OS_BM_Grid.this,
                                "Couldn't delete bill " + billingId + " from the database.", null);
                    }
                });
            }
        }

        /**
         * A scrollable Edit popup over just the bill's identifying/reference
         * fields — Dentist Name, Patient Name, Appointment Date. The money
         * fields (Appointment Charges, Clinical Total, Medicine Total, Total
         * Bill Amount) are wizard-computed from services/medicines actually
         * picked at billing time (stock gets deducted against them), so
         * they're shown locked/read-only here rather than free-text
         * editable — see BillingDAO.updateReferenceFields's javadoc.
         */
        private void handleEditBill(String billingId, int modelRow) {
            model.BillingModel b = controller.BillingManagementController.getAll().stream()
                    .filter(x -> billingId.equals(x.getBillingId()))
                    .findFirst()
                    .orElse(null);
            if (b == null) {
                IconFactory.showErrorDialog(OS_BM_Grid.this,
                        "Couldn't find that bill anymore — try refreshing the grid.", null);
                return;
            }

            final int x = 20, w = IconFactory.FORM_DIALOG_CONTENT_WIDTH;
            JPanel content = new JPanel(null);
            int y = 10;

            y = IconFactory.addFormSectionHeader(content, "Bill Reference", x, y, w);
            javax.swing.JTextField txtBillingId = new javax.swing.JTextField(dash(b.getBillingId()));
            txtBillingId.setEditable(false);
            y = IconFactory.addFormField(content, "Billing ID:", txtBillingId, x, y, w);
            javax.swing.JTextField txtDentistName = new javax.swing.JTextField(dash(b.getDentistName()));
            y = IconFactory.addFormField(content, "Dentist Name:", txtDentistName, x, y, w);
            javax.swing.JTextField txtPatientName = new javax.swing.JTextField(dash(b.getPatientName()));
            y = IconFactory.addFormField(content, "Patient Name:", txtPatientName, x, y, w);
            javax.swing.JTextField txtAppointmentDate = new javax.swing.JTextField(dash(b.getAppointmentDate()));
            DateTimePicker.attachDate(txtAppointmentDate);
            y = IconFactory.addFormField(content, "Appointment Date (yyyy-MM-dd):", txtAppointmentDate, x, y, w);

            y = IconFactory.addFormSectionHeader(content, "Amounts (read-only)", x, y, w);
            javax.swing.JTextField txtCharges = new javax.swing.JTextField(
                    controller.BillingManagementController.formatCurrency(b.getAppointmentCharges()));
            txtCharges.setEditable(false);
            y = IconFactory.addFormField(content, "Appointment Charges:", txtCharges, x, y, w);
            javax.swing.JTextField txtClinical = new javax.swing.JTextField(
                    controller.BillingManagementController.formatCurrency(b.getClinicalTotal()));
            txtClinical.setEditable(false);
            y = IconFactory.addFormField(content, "Clinical Total:", txtClinical, x, y, w);
            javax.swing.JTextField txtMedicine = new javax.swing.JTextField(
                    controller.BillingManagementController.formatCurrency(b.getMedicineTotal()));
            txtMedicine.setEditable(false);
            y = IconFactory.addFormField(content, "Medicine Total:", txtMedicine, x, y, w);
            javax.swing.JTextField txtTotal = new javax.swing.JTextField(
                    controller.BillingManagementController.formatCurrency(b.getTotalBillAmount()));
            txtTotal.setEditable(false);
            y = IconFactory.addFormField(content, "Total Bill Amount:", txtTotal, x, y, w);

            IconFactory.showScrollableFormDialog(OS_BM_Grid.this, "Edit Bill", content, y, () -> {
                if (txtDentistName.getText().trim().isEmpty() || txtPatientName.getText().trim().isEmpty()) {
                    return "Dentist Name and Patient Name are required.";
                }
                String dateText = txtAppointmentDate.getText().trim();
                if (!dateText.isEmpty()) {
                    try {
                        java.time.LocalDate.parse(dateText);
                    } catch (Exception ex) {
                        return "Appointment Date doesn't look valid — pick it from the calendar icon.";
                    }
                }
                boolean ok = controller.BillingManagementController.updateReferenceFields(
                        billingId, txtDentistName.getText().trim(), txtPatientName.getText().trim(), dateText);
                if (!ok) {
                    return "Couldn't save — check the console for details and try again.";
                }
                tableModel.setValueAt(txtPatientName.getText().trim(), modelRow, 1);
                IconFactory.showSuccessDialog(OS_BM_Grid.this, "Bill updated successfully!", null);
                return null;
            });
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
        tableModel = (DefaultTableModel) tblBillings.getModel();
        sorter = new TableRowSorter<>(tableModel);
        tblBillings.setRowSorter(sorter);

        tblBillings.setFont(FONT_DEFAULT);
        tblBillings.setRowHeight(TABLE_ROW_HEIGHT);
        tblBillings.setShowGrid(false);
        tblBillings.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tblBillings.getTableHeader().setFont(FONT_DEFAULT);
        tblBillings.getTableHeader().setBackground(new Color(245, 245, 245));
        tblBillings.getTableHeader().setReorderingAllowed(false);

        tblBillings.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        for (int i = 0; i < TABLE_COLUMN_WIDTHS.length; i++) {
            tblBillings.getColumnModel().getColumn(i).setPreferredWidth(TABLE_COLUMN_WIDTHS[i]);
        }

        tblBillings.getColumnModel().getColumn(3).setCellRenderer(new PrintButtonRenderer());
        tblBillings.getColumnModel().getColumn(4).setCellRenderer(new ActionButtonsRenderer());
        tblBillings.addMouseListener(new TableClickHandler());
        txtSearch.getDocument().addDocumentListener(new SearchDocumentListener());
    }

    // Backed by the real "billings" table (db/schema.sql), via
    // BillingManagementController.

    /** Number of billing records — used by the Office Staff dashboard tile badge. */
    public static int getRecordCount() {
        return controller.BillingManagementController.count();
    }

    /**
     * "Print" column — reopens the same real bill preview/print/download
     * popup Step 4 uses (previously just a fake "Printing bill..." message
     * box that didn't actually show anything). Looks the bill back up fresh
     * from the database rather than trusting only what's in the table row.
     */
    private void printBill(String billingId) {
        model.BillingModel bill = controller.BillingManagementController.getAll().stream()
                .filter(b -> billingId.equals(b.getBillingId()))
                .findFirst()
                .orElse(null);
        if (bill == null) {
            JOptionPane.showMessageDialog(this, "Couldn't find bill " + billingId + " in the database.",
                    "Bill Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new BillPreviewDialog(this, bill, false, null).setVisible(true);
    }

    private void populateSampleData() {
        tableModel.setRowCount(0);
        for (model.BillingModel b : controller.BillingManagementController.getAll()) {
            tableModel.addRow(new Object[]{
                b.getBillingId(), b.getPatientName(),
                controller.BillingManagementController.formatCurrency(b.getTotalBillAmount()), "", ""
            });
        }
    }

    // =========================================================================
    // Calculator — every key on the pnlCalculator keypad is a plain,
    // standard-property JButton/JLabel declared in OS_BM_Grid.form (same
    // background/foreground/font/text recipe as btnBack, btnAddServices,
    // etc.), so it renders identically in the GUI Builder's Design view and
    // at runtime — NetBeans' Design canvas can't reliably preview components
    // built via custom factory code, only plain bean properties. All 19 keys
    // share the calcKeyActionPerformed handler below; the logic just reads
    // whichever button fired the event.
    // =========================================================================

    private void onCalcKey(String key) {
        switch (key) {
            case "MC":
                calcDisplayText = "0";
                calcAccumulator = 0;
                calcPendingOp = null;
                calcMemory = 0;
                calcStartNew = true;
                break;
            case "M+":
                calcMemory += parseCalcDisplay();
                calcStartNew = true;
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                calcApplyPending();
                calcPendingOp = key;
                calcStartNew = true;
                break;
            case "=":
                calcApplyPending();
                calcPendingOp = null;
                calcStartNew = true;
                break;
            case ".":
                if (calcStartNew) {
                    calcDisplayText = "0.";
                    calcStartNew = false;
                } else if (!calcDisplayText.contains(".")) {
                    calcDisplayText += ".";
                }
                break;
            default: // digits, including "00"
                if (calcStartNew || "0".equals(calcDisplayText)) {
                    calcDisplayText = "00".equals(key) ? "0" : key;
                    calcStartNew = false;
                } else {
                    calcDisplayText += key;
                }
                break;
        }
        lblCalcDisplay.setText(calcDisplayText);
    }

    private double parseCalcDisplay() {
        try {
            return Double.parseDouble(calcDisplayText);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void calcApplyPending() {
        double value = parseCalcDisplay();
        if (calcPendingOp == null) {
            calcAccumulator = value;
        } else {
            switch (calcPendingOp) {
                case "+": calcAccumulator += value; break;
                case "-": calcAccumulator -= value; break;
                case "*": calcAccumulator *= value; break;
                case "/": calcAccumulator = value != 0 ? calcAccumulator / value : 0; break;
                default: break;
            }
        }
        calcDisplayText = formatCalcResult(calcAccumulator);
    }

    private String formatCalcResult(double v) {
        if (!Double.isInfinite(v) && !Double.isNaN(v) && v == Math.floor(v) && Math.abs(v) < 1.0e12) {
            return String.valueOf((long) v);
        }
        return String.format("%.2f", v);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        btnAddServices = new javax.swing.JButton();
        btnGenerateBill = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        pnlSearchWrap = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        lblSearchIcon = new javax.swing.JLabel();
        pnlTableWrap = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tblBillings = new javax.swing.JTable();
        pnlCalculator = new javax.swing.JPanel();
        lblCalcDisplay = new javax.swing.JLabel();
        btnCalcMC = new javax.swing.JButton();
        btnCalcDivide = new javax.swing.JButton();
        btnCalcMultiply = new javax.swing.JButton();
        btnCalc7 = new javax.swing.JButton();
        btnCalc8 = new javax.swing.JButton();
        btnCalc9 = new javax.swing.JButton();
        btnCalcMinus = new javax.swing.JButton();
        btnCalc4 = new javax.swing.JButton();
        btnCalc5 = new javax.swing.JButton();
        btnCalc6 = new javax.swing.JButton();
        btnCalcPlus = new javax.swing.JButton();
        btnCalc1 = new javax.swing.JButton();
        btnCalc2 = new javax.swing.JButton();
        btnCalc3 = new javax.swing.JButton();
        btnCalcEquals = new javax.swing.JButton();
        btnCalcDot = new javax.swing.JButton();
        btnCalc0 = new javax.swing.JButton();
        btnCalc00 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sunrise Dental – Billing Management");
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
        lblTitle.setText("Billing Management");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(50, 120, 400, 40);

        btnAddServices.setBackground(new java.awt.Color(255, 193, 7));
        btnAddServices.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAddServices.setForeground(new java.awt.Color(255, 255, 255));
        btnAddServices.setText("Services");
        btnAddServices.setBorderPainted(false);
        btnAddServices.setFocusPainted(false);
        btnAddServices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddServicesActionPerformed(evt);
            }
        });
        mainPanel.add(btnAddServices);
        btnAddServices.setBounds(610, 122, 150, 36);

        btnGenerateBill.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGenerateBill.setText("Generate Bill");
        btnGenerateBill.setBackground(new java.awt.Color(40, 167, 69));
        btnGenerateBill.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerateBill.setBorderPainted(false);
        btnGenerateBill.setFocusPainted(false);
        btnGenerateBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateBillActionPerformed(evt);
            }
        });
        mainPanel.add(btnGenerateBill);
        btnGenerateBill.setBounds(770, 122, 150, 36);

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

        tblBillings.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblBillings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Billing ID", "Patient Name", "Total Amount", "Print", "Actions"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
            tblBillings.getColumnModel().getColumn(0).setPreferredWidth(110);
            tblBillings.getColumnModel().getColumn(1).setPreferredWidth(190);
            tblBillings.getColumnModel().getColumn(2).setPreferredWidth(130);
            tblBillings.getColumnModel().getColumn(3).setPreferredWidth(100);
            tblBillings.getColumnModel().getColumn(4).setPreferredWidth(110);
        }

        pnlTableWrap.add(scrollPane);
        scrollPane.setBounds(1, 1, 648, 350);

        mainPanel.add(pnlTableWrap);
        pnlTableWrap.setBounds(50, 225, 650, 352);

        pnlCalculator.setBackground(new java.awt.Color(255, 255, 255));
        pnlCalculator.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlCalculator.setLayout(null);

        lblCalcDisplay.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblCalcDisplay.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCalcDisplay.setText("0");
        lblCalcDisplay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblCalcDisplay.setOpaque(true);
        lblCalcDisplay.setBackground(new java.awt.Color(255, 255, 255));
        pnlCalculator.add(lblCalcDisplay);
        lblCalcDisplay.setBounds(14, 20, 202, 54);

        btnCalcMC.setBackground(new java.awt.Color(30, 30, 30));
        btnCalcMC.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnCalcMC.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcMC.setText("MC");
        btnCalcMC.setBorderPainted(false);
        btnCalcMC.setFocusPainted(false);
        btnCalcMC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalcMC);
        btnCalcMC.setBounds(14, 90, 100, 46);

        btnCalcDivide.setBackground(new java.awt.Color(255, 152, 0));
        btnCalcDivide.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalcDivide.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcDivide.setText("/");
        btnCalcDivide.setBorderPainted(false);
        btnCalcDivide.setFocusPainted(false);
        btnCalcDivide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalcDivide);
        btnCalcDivide.setBounds(120, 90, 44, 46);

        btnCalcMultiply.setBackground(new java.awt.Color(255, 152, 0));
        btnCalcMultiply.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalcMultiply.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcMultiply.setText("*");
        btnCalcMultiply.setBorderPainted(false);
        btnCalcMultiply.setFocusPainted(false);
        btnCalcMultiply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalcMultiply);
        btnCalcMultiply.setBounds(172, 90, 44, 46);

        btnCalc7.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc7.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc7.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc7.setText("7");
        btnCalc7.setBorderPainted(false);
        btnCalc7.setFocusPainted(false);
        btnCalc7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc7);
        btnCalc7.setBounds(14, 142, 46, 40);

        btnCalc8.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc8.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc8.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc8.setText("8");
        btnCalc8.setBorderPainted(false);
        btnCalc8.setFocusPainted(false);
        btnCalc8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc8);
        btnCalc8.setBounds(66, 142, 46, 40);

        btnCalc9.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc9.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc9.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc9.setText("9");
        btnCalc9.setBorderPainted(false);
        btnCalc9.setFocusPainted(false);
        btnCalc9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc9);
        btnCalc9.setBounds(118, 142, 46, 40);

        btnCalcMinus.setBackground(new java.awt.Color(120, 120, 120));
        btnCalcMinus.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalcMinus.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcMinus.setText("-");
        btnCalcMinus.setBorderPainted(false);
        btnCalcMinus.setFocusPainted(false);
        btnCalcMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalcMinus);
        btnCalcMinus.setBounds(170, 142, 46, 40);

        btnCalc4.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc4.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc4.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc4.setText("4");
        btnCalc4.setBorderPainted(false);
        btnCalc4.setFocusPainted(false);
        btnCalc4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc4);
        btnCalc4.setBounds(14, 188, 46, 40);

        btnCalc5.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc5.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc5.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc5.setText("5");
        btnCalc5.setBorderPainted(false);
        btnCalc5.setFocusPainted(false);
        btnCalc5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc5);
        btnCalc5.setBounds(66, 188, 46, 40);

        btnCalc6.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc6.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc6.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc6.setText("6");
        btnCalc6.setBorderPainted(false);
        btnCalc6.setFocusPainted(false);
        btnCalc6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc6);
        btnCalc6.setBounds(118, 188, 46, 40);

        btnCalcPlus.setBackground(new java.awt.Color(120, 120, 120));
        btnCalcPlus.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalcPlus.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcPlus.setText("+");
        btnCalcPlus.setBorderPainted(false);
        btnCalcPlus.setFocusPainted(false);
        btnCalcPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalcPlus);
        btnCalcPlus.setBounds(170, 188, 46, 40);

        btnCalc1.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc1.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc1.setText("1");
        btnCalc1.setBorderPainted(false);
        btnCalc1.setFocusPainted(false);
        btnCalc1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc1);
        btnCalc1.setBounds(14, 234, 46, 40);

        btnCalc2.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc2.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc2.setText("2");
        btnCalc2.setBorderPainted(false);
        btnCalc2.setFocusPainted(false);
        btnCalc2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc2);
        btnCalc2.setBounds(66, 234, 46, 40);

        btnCalc3.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc3.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc3.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc3.setText("3");
        btnCalc3.setBorderPainted(false);
        btnCalc3.setFocusPainted(false);
        btnCalc3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc3);
        btnCalc3.setBounds(118, 234, 46, 40);

        btnCalcEquals.setBackground(new java.awt.Color(150, 150, 150));
        btnCalcEquals.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalcEquals.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcEquals.setText("=");
        btnCalcEquals.setBorderPainted(false);
        btnCalcEquals.setFocusPainted(false);
        btnCalcEquals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalcEquals);
        btnCalcEquals.setBounds(170, 236, 46, 90);

        btnCalcDot.setBackground(new java.awt.Color(255, 152, 0));
        btnCalcDot.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalcDot.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcDot.setText(".");
        btnCalcDot.setBorderPainted(false);
        btnCalcDot.setFocusPainted(false);
        btnCalcDot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalcDot);
        btnCalcDot.setBounds(14, 280, 46, 40);

        btnCalc0.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc0.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc0.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc0.setText("0");
        btnCalc0.setBorderPainted(false);
        btnCalc0.setFocusPainted(false);
        btnCalc0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc0);
        btnCalc0.setBounds(66, 280, 46, 40);

        btnCalc00.setBackground(new java.awt.Color(255, 152, 0));
        btnCalc00.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCalc00.setForeground(new java.awt.Color(255, 255, 255));
        btnCalc00.setText("00");
        btnCalc00.setBorderPainted(false);
        btnCalc00.setFocusPainted(false);
        btnCalc00.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcKeyActionPerformed(evt);
            }
        });
        pnlCalculator.add(btnCalc00);
        btnCalc00.setBounds(118, 280, 46, 40);

        mainPanel.add(pnlCalculator);
        pnlCalculator.setBounds(720, 225, 230, 352);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddServicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddServicesActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OS_BM_Service().setVisible(true);
        });
    }//GEN-LAST:event_btnAddServicesActionPerformed

    private void btnGenerateBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateBillActionPerformed
        dispose();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OS_BM_1().setVisible(true);
        });
    }//GEN-LAST:event_btnGenerateBillActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dispose();
        // Must carry the real signed-in user back — the no-arg constructor
        // passes null, which silently breaks any "who's logged in" UI
        // reachable from that dashboard afterward (e.g. Edit Profile).
        javax.swing.SwingUtilities.invokeLater(() -> {
            new OfficeStaff_Dashboard(controller.AppController.getCurrentUser()).setVisible(true);
        });
    }//GEN-LAST:event_btnBackActionPerformed

    private void calcKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcKeyActionPerformed
        onCalcKey(((javax.swing.JButton) evt.getSource()).getText());
    }//GEN-LAST:event_calcKeyActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OS_BM_Grid().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddServices;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCalc0;
    private javax.swing.JButton btnCalc00;
    private javax.swing.JButton btnCalc1;
    private javax.swing.JButton btnCalc2;
    private javax.swing.JButton btnCalc3;
    private javax.swing.JButton btnCalc4;
    private javax.swing.JButton btnCalc5;
    private javax.swing.JButton btnCalc6;
    private javax.swing.JButton btnCalc7;
    private javax.swing.JButton btnCalc8;
    private javax.swing.JButton btnCalc9;
    private javax.swing.JButton btnCalcDivide;
    private javax.swing.JButton btnCalcDot;
    private javax.swing.JButton btnCalcEquals;
    private javax.swing.JButton btnCalcMC;
    private javax.swing.JButton btnCalcMinus;
    private javax.swing.JButton btnCalcMultiply;
    private javax.swing.JButton btnCalcPlus;
    private javax.swing.JButton btnGenerateBill;
    private javax.swing.JLabel lblCalcDisplay;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSearchIcon;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JPanel pnlCalculator;
    private javax.swing.JPanel pnlSearchWrap;
    private javax.swing.JPanel pnlTableWrap;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblBillings;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
