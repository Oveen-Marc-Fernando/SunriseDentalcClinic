package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Model for Billing Management.
 * Holds information gathered across the 4-step "Generate Bill" wizard
 * (OS_BM_1 – OS_BM_4).
 *
 * @author oveen
 */
public class BillingModel {

    // Step 1: Appointment Charges
    private String appointmentId;
    private String patientId;
    private String dentistName;
    private String patientName;
    private String appointmentDate;
    private double appointmentCharges;

    // Step 2: Clinical Charges
    private final List<String> clinicalLines = new ArrayList<>();
    private double clinicalTotal;

    // Step 3: Medicine Charges
    private final List<String> medicineLines = new ArrayList<>();
    private double medicineTotal;
    // productId -> quantity billed, aggregated across all 5 rows (same
    // medicine picked twice on one bill still adds up correctly). Used to
    // deduct real inventory stock once the bill is actually saved.
    private final Map<String, Integer> medicineDeductions = new LinkedHashMap<>();

    // Step 4: Total Bill Amount
    private String billingId;
    private double totalBillAmount;

    // AD_OP_Billings' "Sent Email Count" column — incremented each time
    // BillPreviewDialog's Email button successfully sends this bill's PDF.
    private int emailSentCount;

    public BillingModel() {
    }

    // ===========================================================
    // Step 1 Getters and Setters
    // ===========================================================
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public double getAppointmentCharges() { return appointmentCharges; }
    public void setAppointmentCharges(double appointmentCharges) { this.appointmentCharges = appointmentCharges; }

    // ===========================================================
    // Step 2 Getters and Setters
    // ===========================================================
    public List<String> getClinicalLines() { return clinicalLines; }
    public void setClinicalLines(List<String> lines) {
        clinicalLines.clear();
        if (lines != null) clinicalLines.addAll(lines);
    }

    public double getClinicalTotal() { return clinicalTotal; }
    public void setClinicalTotal(double clinicalTotal) { this.clinicalTotal = clinicalTotal; }

    // ===========================================================
    // Step 3 Getters and Setters
    // ===========================================================
    public List<String> getMedicineLines() { return medicineLines; }
    public void setMedicineLines(List<String> lines) {
        medicineLines.clear();
        if (lines != null) medicineLines.addAll(lines);
    }

    public double getMedicineTotal() { return medicineTotal; }
    public void setMedicineTotal(double medicineTotal) { this.medicineTotal = medicineTotal; }

    public Map<String, Integer> getMedicineDeductions() { return medicineDeductions; }
    public void setMedicineDeductions(Map<String, Integer> deductions) {
        medicineDeductions.clear();
        if (deductions != null) medicineDeductions.putAll(deductions);
    }

    // ===========================================================
    // Step 4 Getters and Setters
    // ===========================================================
    public String getBillingId() { return billingId; }
    public void setBillingId(String billingId) { this.billingId = billingId; }

    public double getTotalBillAmount() { return totalBillAmount; }
    public void setTotalBillAmount(double totalBillAmount) { this.totalBillAmount = totalBillAmount; }

    public int getEmailSentCount() { return emailSentCount; }
    public void setEmailSentCount(int emailSentCount) { this.emailSentCount = emailSentCount; }
}
