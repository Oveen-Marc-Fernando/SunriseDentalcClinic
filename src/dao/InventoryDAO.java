package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import model.InventoryModel;

/**
 * Data access for the {@code inventory} table.
 *
 * Backs two previously-inconsistent sample sources at once: D_RS_Grid's own
 * hardcoded "Inventory" browse rows, and SupplyRequestController's separate
 * Product Type/Name catalog used by D_RS_Add's dropdowns (they used to carry
 * different IDs — I0001-I0003 vs. P1001-P1003 — because neither one read
 * from a real shared store). Both now read this same table.
 *
 * @author oveen
 */
public class InventoryDAO {

    private static final String SELECT_ALL_SQL =
            "SELECT product_id, product_name, product_type, quantity, manufacture_date, expire_date, "
            + "description, supplier_name, buying_price, contact_number, selling_price, company_name, published "
            + "FROM inventory ORDER BY product_id";

    /** Every inventory item on record — feeds D_RS_Grid's stock-browse table. */
    public List<InventoryModel> findAll() {
        List<InventoryModel> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] findAll failed: " + e.getMessage());
        }
        return items;
    }

    /** Distinct product types on record, for D_RS_Add's "Product Type" dropdown. */
    public List<String> distinctProductTypes() {
        LinkedHashSet<String> types = new LinkedHashSet<>();
        String sql = "SELECT DISTINCT product_type FROM inventory WHERE product_type IS NOT NULL ORDER BY product_type";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                types.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] distinctProductTypes failed: " + e.getMessage());
        }
        return new ArrayList<>(types);
    }

    /** Product names on record for the given type, for D_RS_Add's "Product Name" dropdown. */
    public List<String> productNamesForType(String productType) {
        List<String> names = new ArrayList<>();
        String sql = "SELECT product_name FROM inventory WHERE product_type = ? ORDER BY product_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] productNamesForType failed: " + e.getMessage());
        }
        return names;
    }

    /**
     * Every inventory item of a given type, e.g. "Medication" — backs
     * Billing's "Medicine Charges" step (OS_BM_3), which sells directly out
     * of real inventory stock instead of a separate static price list.
     */
    public List<InventoryModel> findByType(String productType) {
        List<InventoryModel> items = new ArrayList<>();
        String sql = "SELECT product_id, product_name, product_type, quantity, manufacture_date, expire_date, "
                + "description, supplier_name, buying_price, contact_number, selling_price, company_name, published "
                + "FROM inventory WHERE product_type = ? ORDER BY product_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] findByType failed: " + e.getMessage());
        }
        return items;
    }

    /**
     * Deducts sold quantity from stock — Billing's Medicine Charges step,
     * on final bill submission. The {@code quantity >= ?} guard makes this
     * safe against overselling even under concurrent requests: it only
     * succeeds if enough stock is actually still on hand at the moment the
     * UPDATE runs, and reports back whether it did.
     */
    public boolean deductStock(String productId, int qty) {
        if (productId == null || productId.trim().isEmpty() || qty <= 0) {
            return false;
        }
        String sql = "UPDATE inventory SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setString(2, productId.trim());
            ps.setInt(3, qty);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] deductStock failed for " + productId + ": " + e.getMessage());
            return false;
        }
    }

    /** Looks up the Product ID for a type+name pair, or "" if there's no match. */
    public String lookupProductId(String productType, String productName) {
        String sql = "SELECT product_id FROM inventory WHERE product_type = ? AND product_name = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productType);
            ps.setString(2, productName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : "";
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] lookupProductId failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * Registers/updates an inventory record (insert if the Product ID is
     * new, update in place if it already exists) — keyed by product_id,
     * same upsert pattern as DentistDAO/PatientDAO. Guards against a blank
     * Product ID with a logged skip rather than a broken row.
     */
    public void upsert(InventoryModel m) {
        if (m == null || m.getProductId() == null || m.getProductId().trim().isEmpty()) {
            System.err.println("[InventoryDAO] upsert skipped: missing Product ID");
            return;
        }
        String sql = "INSERT INTO inventory (product_id, product_name, product_type, quantity, "
                + "manufacture_date, expire_date, description, supplier_name, buying_price, "
                + "contact_number, selling_price, company_name) VALUES (?,?,?,?,?,?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE product_name = VALUES(product_name), "
                + "product_type = VALUES(product_type), quantity = VALUES(quantity), "
                + "manufacture_date = VALUES(manufacture_date), expire_date = VALUES(expire_date), "
                + "description = VALUES(description), supplier_name = VALUES(supplier_name), "
                + "buying_price = VALUES(buying_price), contact_number = VALUES(contact_number), "
                + "selling_price = VALUES(selling_price), company_name = VALUES(company_name)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getProductId().trim());
            ps.setString(2, m.getProductName());
            ps.setString(3, SqlUtil.blankToNull(m.getProductType()));
            ps.setInt(4, parseQuantity(m.getQuantity()));
            ps.setDate(5, SqlUtil.parseDate(m.getManufactureDate()));
            ps.setDate(6, SqlUtil.parseDate(m.getExpireDate()));
            ps.setString(7, SqlUtil.blankToNull(m.getDescription()));
            ps.setString(8, SqlUtil.blankToNull(m.getSupplierName()));
            ps.setBigDecimal(9, parsePrice(m.getBuyingPrice()));
            ps.setString(10, SqlUtil.blankToNull(m.getContactNumber()));
            ps.setBigDecimal(11, parsePrice(m.getSellingPrice()));
            ps.setString(12, SqlUtil.blankToNull(m.getCompanyName()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] upsert failed for " + m.getProductId() + ": " + e.getMessage());
        }
    }

    /** Next auto-generated Product ID, e.g. "I104" — one past the highest numeric suffix on record. */
    public String nextProductId() {
        String sql = "SELECT product_id FROM inventory WHERE product_id LIKE 'I%'";
        int max = 100;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    int n = Integer.parseInt(rs.getString(1).substring(1));
                    max = Math.max(max, n);
                } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                    // non-numeric suffix — skip it, doesn't affect the running max
                }
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] nextProductId failed: " + e.getMessage());
        }
        return "I" + (max + 1);
    }

    /** Deletes an inventory item by Product ID. Returns true if a row was actually removed. */
    public boolean delete(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }
        String sql = "DELETE FROM inventory WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] delete failed for " + productId + ": " + e.getMessage());
            return false;
        }
    }

    private static int parseQuantity(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static java.math.BigDecimal parsePrice(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return new java.math.BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static InventoryModel mapRow(ResultSet rs) throws SQLException {
        InventoryModel m = new InventoryModel();
        m.setProductId(rs.getString("product_id"));
        m.setProductName(rs.getString("product_name"));
        m.setProductType(rs.getString("product_type"));
        m.setQuantity(String.valueOf(rs.getInt("quantity")));
        m.setManufactureDate(SqlUtil.formatDate(rs.getDate("manufacture_date")));
        m.setExpireDate(SqlUtil.formatDate(rs.getDate("expire_date")));
        m.setDescription(rs.getString("description"));
        m.setSupplierName(rs.getString("supplier_name"));
        java.math.BigDecimal buying = rs.getBigDecimal("buying_price");
        m.setBuyingPrice(buying == null ? "" : buying.toPlainString());
        m.setContactNumber(rs.getString("contact_number"));
        java.math.BigDecimal selling = rs.getBigDecimal("selling_price");
        m.setSellingPrice(selling == null ? "" : selling.toPlainString());
        m.setCompanyName(rs.getString("company_name"));
        m.setPublished(rs.getBoolean("published"));
        return m;
    }

    /** Toggles a product's Publish Status — AD_OP_Inventory's status icon column. */
    public boolean setPublished(String productId, boolean published) {
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE inventory SET published = ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, published);
            ps.setString(2, productId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] setPublished failed for " + productId + ": " + e.getMessage());
            return false;
        }
    }
}
