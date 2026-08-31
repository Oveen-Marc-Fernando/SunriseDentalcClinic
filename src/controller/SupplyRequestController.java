package controller;

import dao.InventoryDAO;
import dao.SupplyRequestDAO;
import model.SupplyRequestModel;
import javax.swing.JOptionPane;

/**
 * Controller for the Dentist "Request Supplies" feature (D_RS_Grid,
 * D_RS_Add, D_RS_History), reached from the Dentist Dashboard's "Request
 * Supplies" tile.
 *
 * Backed by the real {@code supply_requests} and {@code inventory} tables
 * (see db/schema.sql) via {@link SupplyRequestDAO} / {@link InventoryDAO} —
 * D_RS_Grid (full product detail) and D_RS_History (compact log) both read
 * the same {@code supply_requests} rows, they just project different
 * columns of them.
 *
 * @author oveen
 */
public class SupplyRequestController {

    private static final SupplyRequestDAO REQUEST_DAO = new SupplyRequestDAO();
    private static final InventoryDAO INVENTORY_DAO = new InventoryDAO();

    /** Read-only snapshot of every supply request on record — Administration's unscoped approval screen. */
    public static java.util.List<SupplyRequestModel> getAll() {
        return REQUEST_DAO.findAll();
    }

    /** Only the requests this dentist submitted — D_RS_History's own "my requests" view. */
    public static java.util.List<SupplyRequestModel> getForDentist(String dentistName) {
        return REQUEST_DAO.findByDentist(dentistName);
    }

    /** Number of supply requests on record — mirrors the sibling grids' getRecordCount() convention. */
    public static int getRecordCount() {
        return REQUEST_DAO.count();
    }

    /** Number of supply requests this dentist submitted — Dentist Dashboard's own "Request Supplies" badge. */
    public static int getRecordCountForDentist(String dentistName) {
        return REQUEST_DAO.countForDentist(dentistName);
    }

    /** Next auto-generated Request ID, e.g. "SR-1003" — one past the last request on record. */
    public static String generateNextRequestId() {
        return "SR-" + (1000 + REQUEST_DAO.count() + 1);
    }

    /** Approves a pending supply request — AD_APR_SupplyRequest's Approve button. */
    public static boolean approve(String trackingId) {
        return REQUEST_DAO.updateStatus(trackingId, "Approved");
    }

    /** Rejects a pending supply request — AD_APR_SupplyRequest's Reject button. */
    public static boolean reject(String trackingId) {
        return REQUEST_DAO.updateStatus(trackingId, "Rejected");
    }

    // =========================================================================
    // Inventory catalog — feeds D_RS_Add's "Product Type" / "Product Name"
    // dropdowns and the automatic Product ID lookup that follows picking
    // them. Reads the same "inventory" table D_RS_Grid's stock-browse view
    // reads, so both screens finally agree on what's actually in stock.
    // =========================================================================

    /** Distinct product types in the Inventory table. */
    public static java.util.List<String> getProductTypes() {
        return INVENTORY_DAO.distinctProductTypes();
    }

    /** Product names on record for the given type. */
    public static java.util.List<String> getProductNames(String productType) {
        return INVENTORY_DAO.productNamesForType(productType);
    }

    /** Looks up the Inventory/Product ID for a type+name pair, or "" if there's no match. */
    public static String lookupProductId(String productType, String productName) {
        return INVENTORY_DAO.lookupProductId(productType, productName);
    }

    /**
     * Validates and appends a new supply request. Called from D_RS_Add's
     * "Request" button.
     *
     * @param dentistName the submitting dentist's own name (attributes this
     *      request to them — see D_RS_History/the dashboard badge); null or
     *      blank leaves it unattributed, same as the legacy pre-attribution rows.
     * @return true if the request was added
     */
    public boolean submitRequest(
            String trackingId,
            String productId,
            String productType,
            String productName,
            String quantity,
            String dentistName,
            javax.swing.JFrame currentView) {

        if (trackingId == null || trackingId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Request ID is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (productId == null || productId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Product ID is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (productName == null || productName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Product Name is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (quantity == null || quantity.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Quantity is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        SupplyRequestModel model = new SupplyRequestModel();
        model.setTrackingId(trackingId.trim());
        model.setProductId(productId.trim());
        model.setProductType(productType);
        model.setProductName(productName.trim());
        model.setQuantity(quantity.trim());
        // Description / Expiry / Manufacture Date aren't collected on this
        // form — they'd normally be looked up from the matching Inventory
        // record via a join once D_RS_Add fetches full row detail instead of
        // just an ID. Left blank for now, same as before this was DB-backed.
        model.setDescription("");
        model.setExpiryDate("");
        model.setManufactureDate("");
        model.setDentistName(dentistName != null ? dentistName.trim() : null);

        REQUEST_DAO.insert(model);
        System.out.println("[CONTROLLER] Supply request submitted: " + model);
        return true;
    }
}
