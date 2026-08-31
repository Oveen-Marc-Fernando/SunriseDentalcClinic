package controller;

import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.AppointmentModel;
import model.BillingModel;

/**
 * Controller for the Billing Management "Generate Bill" wizard (OS_BM_1 – OS_BM_4).
 *
 * Responsibilities:
 *  - Owns the single shared {@link BillingModel} across all 4 steps.
 *  - Supplies the lookup data (appointments / services / medicines) each
 *    step's dropdowns are built from — appointments read live from
 *    AppointmentManagementController, services from the "services" table
 *    via ServiceDAO (previously a static in-memory Map here, so edits from
 *    OS_BM_Service were lost on every restart; now it persists). Medicines
 *    read straight from real Inventory stock (product_type = 'Medication')
 *    instead of a separate price list — billing a medicine deducts real
 *    stock via InventoryDAO.deductStock() when the bill is saved.
 *  - Collects data from each wizard step View.
 *  - Validates each step before allowing navigation forward.
 *  - Orchestrates opening the next / previous View.
 *  - Saves (persists) the completed bill on final submission.
 *
 * @author oveen
 */
public class BillingManagementController {

    // ── Shared billing reads ────────────────────────────────────────────────
    // Backed by the real "billings" table (db/schema.sql) via BillingDAO —
    // OS_BM_Grid reads through these instead of touching the DAO directly.
    private static final dao.BillingDAO BILLING_DAO = new dao.BillingDAO();

    public static java.util.List<BillingModel> getAll() {
        return BILLING_DAO.findAll();
    }

    public static int count() {
        return BILLING_DAO.count();
    }

    /** Deletes a bill by Billing ID — OS_BM_Grid's Delete button. */
    public static boolean delete(String billingId) {
        return BILLING_DAO.delete(billingId);
    }

    /** Bumps a bill's Sent Email Count by one — called after a genuinely successful send. */
    public static boolean incrementEmailSentCount(String billingId) {
        return BILLING_DAO.incrementEmailSentCount(billingId);
    }

    /**
     * Updates a bill's identifying/reference fields — OS_BM_Grid's Edit
     * popup. The money fields aren't editable through here — see
     * {@link dao.BillingDAO#updateReferenceFields}'s javadoc for why.
     */
    public static boolean updateReferenceFields(String billingId, String dentistName, String patientName, String appointmentDate) {
        java.sql.Date parsedDate = null;
        if (appointmentDate != null && !appointmentDate.trim().isEmpty()) {
            try {
                parsedDate = java.sql.Date.valueOf(appointmentDate.trim());
            } catch (IllegalArgumentException ignored) {
                // Not a valid yyyy-MM-dd — leave it null rather than fail the whole save.
            }
        }
        return BILLING_DAO.updateReferenceFields(billingId, dentistName, patientName, parsedDate);
    }

    private final BillingModel billingModel;

    public BillingManagementController() {
        this.billingModel = new BillingModel();
    }

    public BillingManagementController(BillingModel existingModel) {
        this.billingModel = existingModel;
    }

    public BillingModel getBillingModel() {
        return billingModel;
    }

    // =========================================================================
    // Lookup data — appointments read live from AppointmentManagementController
    // (the real "appointments" table), so Step 1's auto-fill always lines up
    // with what you'd see in Appointment Management for the same Appointment
    // ID. Services read from the "services" table; medicines read from real
    // Inventory stock (see the Medicine Charges section further down).
    // =========================================================================

    private static final dao.ServiceDAO SERVICE_DAO = new dao.ServiceDAO();
    private static final dao.InventoryDAO INVENTORY_DAO = new dao.InventoryDAO();
    private static final String MEDICINE_TYPE = "Medication";

    public String[] getAppointmentIds() {
        List<AppointmentModel> all = AppointmentManagementController.getAll();
        String[] ids = new String[all.size()];
        for (int i = 0; i < all.size(); i++) {
            ids[i] = all.get(i).getAppointmentId();
        }
        return ids;
    }
    public String getDentistFor(String appointmentId) {
        AppointmentModel a = AppointmentManagementController.getById(appointmentId);
        return a != null ? a.getDentistName() : "";
    }
    public String getPatientFor(String appointmentId) {
        AppointmentModel a = AppointmentManagementController.getById(appointmentId);
        return a != null ? a.getPatientName() : "";
    }

    /**
     * Real Patient ID for the appointment's patient — best-effort match by
     * name (appointments.patient_name is free text, not a real foreign
     * key), via PatientManagementController. "" if there's no match.
     */
    public String getPatientIdFor(String appointmentId) {
        AppointmentModel a = AppointmentManagementController.getById(appointmentId);
        return a != null ? PatientManagementController.findIdByFullName(a.getPatientName()) : "";
    }

    public String getDateFor(String appointmentId) {
        AppointmentModel a = AppointmentManagementController.getById(appointmentId);
        return a != null ? (a.getDate() + " / " + a.getTime()) : "";
    }

    /**
     * Default Appointment Charges to pre-fill Step 1 with — there's no
     * stored "charge" on an appointment itself, so this uses the treating
     * dentist's own registered Consultation Fee (Dentist Management Step 4)
     * as a real, linked starting figure instead of an invented constant.
     * Office staff can still edit it before continuing.
     */
    public double getDefaultChargeFor(String appointmentId) {
        AppointmentModel a = AppointmentManagementController.getById(appointmentId);
        if (a == null || a.getDentistName() == null) {
            return 0;
        }
        model.DentistModel dentist = DentistManagementController.getDirectory().get(a.getDentistName());
        return dentist != null ? parseCurrency(dentist.getConsultationFee()) : 0;
    }

    public String[] getServiceNames() {
        Map<String, Double> services = SERVICE_DAO.findAll();
        String[] names = new String[services.size() + 1];
        names[0] = "";
        int i = 1;
        for (String n : services.keySet()) names[i++] = n;
        return names;
    }
    public double getServicePrice(String name) {
        Double p = SERVICE_DAO.findAll().get(name);
        return p != null ? p : 0;
    }

    /** Adds a new service, or updates the price of an existing one, in the master price list. */
    public void setServicePrice(String name, double price) {
        if (name != null && !name.trim().isEmpty()) {
            SERVICE_DAO.upsert(name.trim(), price);
        }
    }

    /** Removes a service from the master price list (e.g. via OS_BM_Service's Delete). */
    public void removeService(String name) {
        if (name != null) {
            SERVICE_DAO.delete(name);
        }
    }

    /** Every Medication-type inventory item, sold as "Medicine Charges" — real stock, not a separate catalog. */
    private List<model.InventoryModel> medicineStock() {
        return INVENTORY_DAO.findByType(MEDICINE_TYPE);
    }

    private model.InventoryModel findMedicine(String name) {
        if (name == null) return null;
        for (model.InventoryModel m : medicineStock()) {
            if (m.getProductName().equals(name)) return m;
        }
        return null;
    }

    public String[] getMedicineNames() {
        List<model.InventoryModel> medicines = medicineStock();
        String[] names = new String[medicines.size() + 1];
        names[0] = "";
        for (int i = 0; i < medicines.size(); i++) names[i + 1] = medicines.get(i).getProductName();
        return names;
    }
    public double getMedicinePrice(String name) {
        model.InventoryModel m = findMedicine(name);
        if (m == null || m.getSellingPrice() == null || m.getSellingPrice().isEmpty()) return 0;
        try {
            return Double.parseDouble(m.getSellingPrice());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Units currently on hand for a medicine — OS_BM_3 caps its Quantity dropdown to this. */
    public int getMedicineStock(String name) {
        model.InventoryModel m = findMedicine(name);
        if (m == null || m.getQuantity() == null) return 0;
        try {
            return Integer.parseInt(m.getQuantity());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Product ID for a medicine name — used to deduct real stock once the bill is saved. */
    public String getMedicineProductId(String name) {
        model.InventoryModel m = findMedicine(name);
        return m != null ? m.getProductId() : null;
    }

    /** Formats a currency amount the same way OS_BM_Grid's sample rows do, e.g. "Rs 24,500". */
    public static String formatCurrency(double amount) {
        return "Rs " + String.format("%,.0f", amount);
    }

    // =========================================================================
    // STEP 1 – Appointment Charges
    // =========================================================================

    public boolean goNextFromStep1(
            String appointmentId,
            String patientId,
            String dentistName,
            String patientName,
            String appointmentDate,
            String appointmentChargesText,
            JFrame currentView) {

        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentView,
                    "Please select an Appointment ID.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        double charges = parseCurrency(appointmentChargesText);
        if (charges <= 0) {
            JOptionPane.showMessageDialog(currentView,
                    "Appointment Charges must be greater than zero.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        billingModel.setAppointmentId(appointmentId.trim());
        billingModel.setPatientId(patientId != null ? patientId.trim() : "");
        billingModel.setDentistName(dentistName != null ? dentistName.trim() : "");
        billingModel.setPatientName(patientName != null ? patientName.trim() : "");
        billingModel.setAppointmentDate(appointmentDate != null ? appointmentDate.trim() : "");
        billingModel.setAppointmentCharges(charges);

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_BM_2(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    // =========================================================================
    // STEP 2 – Clinical Charges
    // =========================================================================

    public boolean goNextFromStep2(List<String> clinicalLines, double clinicalTotal, JFrame currentView) {
        billingModel.setClinicalLines(clinicalLines);
        billingModel.setClinicalTotal(clinicalTotal);

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_BM_3(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    public void goBackFromStep2(JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_BM_1(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 3 – Medicine Charges
    // =========================================================================

    public boolean goNextFromStep3(List<String> medicineLines, double medicineTotal,
            Map<String, Integer> medicineDeductions, JFrame currentView) {
        billingModel.setMedicineLines(medicineLines);
        billingModel.setMedicineTotal(medicineTotal);
        billingModel.setMedicineDeductions(medicineDeductions);
        billingModel.setBillingId(generateBillingId());
        billingModel.setTotalBillAmount(
                billingModel.getAppointmentCharges() + billingModel.getClinicalTotal() + medicineTotal);

        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_BM_4(this).setVisible(true);
        });
        currentView.dispose();
        return true;
    }

    public void goBackFromStep3(JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_BM_2(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // STEP 4 – Total Bill Amount (Final Submit)
    // =========================================================================

    public boolean submitFromStep4(JFrame currentView) {
        return saveBill();
    }

    public void goBackFromStep4(JFrame currentView) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.OS_BM_3(this).setVisible(true);
        });
        currentView.dispose();
    }

    // =========================================================================
    // Persistence (stub – replace with DAO/DB logic when ready)
    // =========================================================================

    private String generateBillingId() {
        return BILLING_DAO.nextBillingId();
    }

    private double parseCurrency(String text) {
        if (text == null) return 0;
        try {
            return Double.parseDouble(text.replace("Rs", "").replace(",", "").trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /** Returns true only if the bill was actually written to the database — a failed insert (e.g. a duplicate ID) must never be reported as a success. */
    private boolean saveBill() {
        System.out.println("[CONTROLLER] Saving bill " + billingModel.getBillingId()
                + " for " + billingModel.getPatientName() + " — total " + formatCurrency(billingModel.getTotalBillAmount()));
        boolean saved = BILLING_DAO.insert(billingModel);
        if (!saved) {
            System.err.println("[CONTROLLER] Bill " + billingModel.getBillingId() + " was NOT saved — skipping stock deduction.");
            return false;
        }

        // Bill is charged either way — the appointment/clinical/medicine
        // charges were already agreed with the patient by this point. Stock
        // deduction failures (e.g. someone else sold the last unit in the
        // meantime) are logged rather than blocking the bill itself.
        for (Map.Entry<String, Integer> line : billingModel.getMedicineDeductions().entrySet()) {
            boolean ok = INVENTORY_DAO.deductStock(line.getKey(), line.getValue());
            if (!ok) {
                System.err.println("[CONTROLLER] Stock deduction failed for " + line.getKey()
                        + " x" + line.getValue() + " on bill " + billingModel.getBillingId()
                        + " — insufficient stock or unknown product ID.");
            }
        }
        return true;
    }
}
