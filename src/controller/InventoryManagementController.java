package controller;

import model.InventoryModel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

/**
 * Controller for the Inventory Management registration wizard (OS_IM_1 – OS_IM_2).
 *
 * Responsibilities:
 *  - Owns the single shared {@link InventoryModel} across both steps.
 *  - Collects data from each wizard step View.
 *  - Validates each step before allowing navigation forward.
 *  - Orchestrates opening the next / previous View.
 *  - Saves (persists) the completed inventory record on final submission.
 *
 * @author oveen
 */
public class InventoryManagementController {

    // ── Shared inventory reads ──────────────────────────────────────────────
    // Backed by the real "inventory" table (db/schema.sql) via InventoryDAO —
    // OS_IM_Grid and D_RS_Grid both read through this instead of touching
    // the DAO directly.
    private static final dao.InventoryDAO INVENTORY_DAO = new dao.InventoryDAO();

    public static java.util.List<InventoryModel> getAll() {
        return INVENTORY_DAO.findAll();
    }

    public static int count() {
        return INVENTORY_DAO.findAll().size();
    }

    /** Deletes an inventory item by Product ID — OS_IM_Grid's Delete button. */
    public static boolean delete(String productId) {
        return INVENTORY_DAO.delete(productId);
    }

    /** Toggles a product's Publish Status — AD_OP_Inventory's status icon column. */
    public static boolean setPublished(String productId, boolean published) {
        return INVENTORY_DAO.setPublished(productId, published);
    }

    /** Overwrites an existing product's full record — OS_IM_Grid's Edit popup (same upsert the Add wizard's own final save uses). */
    public static void updateProduct(InventoryModel model) {
        INVENTORY_DAO.upsert(model);
    }

    /** Next auto-generated Product ID, e.g. "I104" — OS_IM_1's read-only Product ID field. */
    public static String nextProductId() {
        return INVENTORY_DAO.nextProductId();
    }

    private final InventoryModel inventoryModel;

    public InventoryManagementController() {
        this.inventoryModel = new InventoryModel();
    }

    public InventoryManagementController(InventoryModel existingModel) {
        this.inventoryModel = existingModel;
    }

    public InventoryModel getInventoryModel() {
        return inventoryModel;
    }

    // =========================================================================
    // STEP 1 – Product Information
    // =========================================================================

    public boolean goNextFromStep1(
            String productId,
            String quantity,
            String productType,
            String manufactureDate,
            String productName,
            String expireDate,
            String description,
            JFrame currentView) {

        if (productName == null || productName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Product Name is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (productId == null || productId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Product ID is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        inventoryModel.setProductId(productId.trim());
        inventoryModel.setQuantity(quantity != null ? quantity.trim() : "");
        inventoryModel.setProductType(productType != null ? productType.trim() : "");
        inventoryModel.setManufactureDate(manufactureDate != null ? manufactureDate.trim() : "");
        inventoryModel.setProductName(productName.trim());
        inventoryModel.setExpireDate(expireDate != null ? expireDate.trim() : "");
        inventoryModel.setDescription(description != null ? description.trim() : "");

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_IM_2(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    // =========================================================================
    // STEP 2 – Supplier Information (Final Submit)
    // =========================================================================

    public boolean submitFromStep2(
            String supplierName,
            String buyingPrice,
            String contactNumber,
            String sellingPrice,
            String companyName,
            JFrame currentView) {

        if (supplierName == null || supplierName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Supplier Name is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        inventoryModel.setSupplierName(supplierName.trim());
        inventoryModel.setBuyingPrice(buyingPrice != null ? buyingPrice.trim() : "");
        inventoryModel.setContactNumber(contactNumber != null ? contactNumber.trim() : "");
        inventoryModel.setSellingPrice(sellingPrice != null ? sellingPrice.trim() : "");
        inventoryModel.setCompanyName(companyName != null ? companyName.trim() : "");

        saveProduct();
        return true;
    }

    public void goBackFromStep2(JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_IM_1(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // Persistence
    // =========================================================================

    /** Saves the completed {@link InventoryModel} to the {@code inventory} table. */
    private void saveProduct() {
        System.out.println("[CONTROLLER] Saving new inventory record: " + inventoryModel.getProductName()
                + " (" + inventoryModel.getProductId() + ")");
        INVENTORY_DAO.upsert(inventoryModel);
    }
}
