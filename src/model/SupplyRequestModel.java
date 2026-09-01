package model;

/**
 * A single dentist-submitted supply request.
 *
 * One shared list of these (see {@link controller.SupplyRequestController})
 * backs both D_RS_Grid (full product detail) and D_RS_History (compact log)
 * — each view just projects a different subset of columns from it.
 *
 * @author oveen
 */
public class SupplyRequestModel {

    private String trackingId; // shown as "Tracking ID" in the Grid, "Request ID" in History
    private String productId;
    private String productType;
    private String productName;
    private String description;
    private String quantity;
    private String expiryDate;
    private String manufactureDate;
    private String status; // "Pending" / "Approved" / "Rejected" — see AD_APR_SupplyRequest
    private String dentistName; // who requested it — null for legacy pre-attribution rows

    public SupplyRequestModel() {
        // default empty constructor
    }

    public String getTrackingId()            { return trackingId; }
    public void   setTrackingId(String v)    { this.trackingId = v; }

    public String getProductId()             { return productId; }
    public void   setProductId(String v)     { this.productId = v; }

    public String getProductType()           { return productType; }
    public void   setProductType(String v)   { this.productType = v; }

    public String getProductName()           { return productName; }
    public void   setProductName(String v)   { this.productName = v; }

    public String getDescription()           { return description; }
    public void   setDescription(String v)   { this.description = v; }

    public String getQuantity()              { return quantity; }
    public void   setQuantity(String v)      { this.quantity = v; }

    public String getExpiryDate()            { return expiryDate; }
    public void   setExpiryDate(String v)    { this.expiryDate = v; }

    public String getManufactureDate()       { return manufactureDate; }
    public void   setManufactureDate(String v){ this.manufactureDate = v; }

    public String getStatus()                { return status; }
    public void   setStatus(String v)        { this.status = v; }

    public String getDentistName()           { return dentistName; }
    public void   setDentistName(String v)   { this.dentistName = v; }

    @Override
    public String toString() {
        return "SupplyRequestModel{"
                + "trackingId='" + trackingId + '\''
                + ", productId='" + productId + '\''
                + ", productName='" + productName + '\''
                + ", quantity='" + quantity + '\''
                + '}';
    }
}
