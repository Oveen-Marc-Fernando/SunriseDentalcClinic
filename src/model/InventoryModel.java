package model;

/**
 * Data Model for Inventory Management.
 * Holds information gathered across the 2-step Inventory registration wizard.
 *
 * @author oveen
 */
public class InventoryModel {

    // Step 1: Product Information
    private String productId;
    private String quantity;
    private String productType;
    private String manufactureDate;
    private String productName;
    private String expireDate;
    private String description;

    // Step 2: Supplier Information
    private String supplierName;
    private String buyingPrice;
    private String contactNumber;
    private String sellingPrice;
    private String companyName;

    // AD_OP_Inventory's "Publish Status" column — whether this product is
    // currently visible to staff. Defaults true so any model built without
    // touching it (e.g. every existing wizard step) stays visible.
    private boolean published = true;

    public InventoryModel() {
    }

    // ===========================================================
    // Step 1 Getters and Setters
    // ===========================================================
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(String manufactureDate) { this.manufactureDate = manufactureDate; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getExpireDate() { return expireDate; }
    public void setExpireDate(String expireDate) { this.expireDate = expireDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // ===========================================================
    // Step 2 Getters and Setters
    // ===========================================================
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getBuyingPrice() { return buyingPrice; }
    public void setBuyingPrice(String buyingPrice) { this.buyingPrice = buyingPrice; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(String sellingPrice) { this.sellingPrice = sellingPrice; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}
