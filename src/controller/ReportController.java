package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.AppointmentModel;
import model.BillingModel;
import model.DentistModel;
import model.InventoryModel;
import model.PatientModel;
import model.SupplyRequestModel;
import util.ReportPdfGenerator;

/**
 * Administration &gt; Reports' report builder — turns a (Time, Category,
 * Report) picker selection into a real PDF, following the fixed clinic
 * report format ({@link ReportPdfGenerator}). Each of the 10 reports pulls
 * live data through the same controllers every other screen in the app
 * already reads from, so a report always matches what's on record.
 *
 * Reports built on genuinely dated records (Appointment/Billing/the 3
 * financial reports derived from billings) honor the Time filter; reports
 * over data that was never date-stamped (Patient/Dentist/Inventory, and the
 * two reports built from Inventory/Supply Requests) are documented as
 * full-snapshot instead of silently ignoring the filter.
 *
 * @author oveen
 */
public final class ReportController {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ReportController() {
    }

    public static byte[] generate(String timeFilter, String reportName) throws IOException {
        LocalDate[] range = resolveRange(timeFilter);
        String periodLabel = humanPeriodLabel(timeFilter, range);
        // Same period phrasing, but honest that the listing below it isn't
        // actually filtered by it — for the 5 reports built on data that was
        // never date-stamped in the first place (Patient/Dentist/Inventory,
        // and Inventory Sales/Total Expenses).
        String snapshotLabel = periodLabel + " (showing all records on file — this data isn't date-stamped)";

        switch (reportName) {
            case "Patient Report": return patientReport(snapshotLabel);
            case "Dentist Report": return dentistReport(snapshotLabel);
            case "Appointment Report": return appointmentReport(periodLabel, range);
            case "Inventory Report": return inventoryReport(snapshotLabel);
            case "Billing Report": return billingReport(periodLabel, range);
            case "Inventory Sales Report": return inventorySalesReport(snapshotLabel);
            case "Services Income Report": return servicesIncomeReport(periodLabel, range);
            case "Total Income Report": return totalIncomeReport(periodLabel, range);
            case "Total Expenses Report": return totalExpensesReport(snapshotLabel);
            case "Dentist Consultation Fee Report": return dentistFeeReport(periodLabel, range);
            default: throw new IllegalArgumentException("Unknown report: " + reportName);
        }
    }

    /**
     * Turns the Time picker's selection into the phrasing every report's
     * header states, e.g. "Weekly — August 3rd Week, 2026",
     * "Monthly — August 2026", "Yearly — 2026" — a real calendar period
     * instead of a raw ISO date range.
     */
    private static String humanPeriodLabel(String timeFilter, LocalDate[] range) {
        LocalDate end = range[1];
        String month = end.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        if ("Weekly".equals(timeFilter)) {
            int weekOfMonth = ((end.getDayOfMonth() - 1) / 7) + 1;
            return "Weekly — " + month + " " + ordinal(weekOfMonth) + " Week, " + end.getYear();
        } else if ("Yearly".equals(timeFilter)) {
            return "Yearly — " + end.getYear();
        } else { // "Monthly" (and any unrecognized value defaults to it)
            return "Monthly — " + month + " " + end.getYear();
        }
    }

    private static String ordinal(int n) {
        if (n >= 11 && n <= 13) return n + "th";
        switch (n % 10) {
            case 1: return n + "st";
            case 2: return n + "nd";
            case 3: return n + "rd";
            default: return n + "th";
        }
    }

    /** Suggested file name, e.g. "Patient_Report_Weekly_2026-08-20.pdf". */
    public static String suggestedFileName(String timeFilter, String reportName) {
        return reportName.replace(" ", "_") + "_" + timeFilter + "_" + LocalDate.now().format(ISO) + ".pdf";
    }

    // =========================================================================
    // Time window
    // =========================================================================

    private static LocalDate[] resolveRange(String timeFilter) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        if ("Weekly".equals(timeFilter)) {
            start = today.minusDays(6);
        } else if ("Yearly".equals(timeFilter)) {
            start = today.withDayOfYear(1);
        } else { // "Monthly" (and any unrecognized value defaults to it)
            start = today.withDayOfMonth(1);
        }
        return new LocalDate[]{start, today};
    }

    private static boolean inRange(String dateStr, LocalDate[] range) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate d = LocalDate.parse(dateStr.trim(), ISO);
            return !d.isBefore(range[0]) && !d.isAfter(range[1]);
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================================
    // Operational reports
    // =========================================================================

    private static byte[] patientReport(String periodLabel) throws IOException {
        String[] headers = {"Patient ID", "Name", "Age", "Mobile No", "Email", "Last Dental Visit"};
        float[] widths = ReportPdfGenerator.columnWidths(1.1f, 1.6f, 0.7f, 1.3f, 1.8f, 1.3f);
        List<String[]> rows = new ArrayList<>();
        for (PatientModel p : PatientManagementController.getAll()) {
            rows.add(new String[]{dash(p.getPatientId()), dash(p.getFullName()), dash(p.getAge()),
                    dash(p.getMobileNo()), dash(p.getEmail()), dash(p.getLastDentalVisit())});
        }
        return ReportPdfGenerator.generate("Patient Report", periodLabel,
                headers, widths, rows, "Total Patients", String.valueOf(rows.size()), null);
    }

    private static byte[] dentistReport(String periodLabel) throws IOException {
        String[] headers = {"Dentist ID", "Name", "SLMC No", "Mobile No", "Consultation Fee", "Employment Type", "License"};
        float[] widths = ReportPdfGenerator.columnWidths(1f, 1.3f, 1f, 1.1f, 1.1f, 1.1f, 0.8f);
        List<String[]> rows = new ArrayList<>();
        for (DentistModel d : DentistManagementController.getDirectory().values()) {
            rows.add(new String[]{dash(d.getDentistId()), dash(d.getFullName()), dash(d.getSlmcNo()),
                    dash(d.getMobileNo()), currency(parseDouble(d.getConsultationFee())),
                    dash(d.getEmploymentType()), dash(d.getLicenseStatus())});
        }
        return ReportPdfGenerator.generate("Dentist Report", periodLabel,
                headers, widths, rows, "Total Dentists", String.valueOf(rows.size()), null);
    }

    private static byte[] appointmentReport(String periodLabel, LocalDate[] range) throws IOException {
        String[] headers = {"Appt ID", "Patient", "Dentist", "Treatment", "Date", "Time", "Status"};
        float[] widths = ReportPdfGenerator.columnWidths(0.9f, 1.2f, 1.1f, 1.3f, 0.9f, 0.7f, 0.9f);
        List<String[]> rows = new ArrayList<>();
        for (AppointmentModel a : AppointmentManagementController.getAll()) {
            if (!inRange(a.getDate(), range)) continue;
            rows.add(new String[]{dash(a.getAppointmentId()), dash(a.getPatientName()), dash(a.getDentistName()),
                    dash(a.getTreatmentType()), dash(a.getDate()), dash(a.getTime()), dash(a.getStatus())});
        }
        return ReportPdfGenerator.generate("Appointment Report", periodLabel,
                headers, widths, rows, "Total Appointments", String.valueOf(rows.size()), null);
    }

    private static byte[] inventoryReport(String periodLabel) throws IOException {
        String[] headers = {"Inventory ID", "Product Name", "Type", "Quantity", "Expiry Date", "Supplier"};
        float[] widths = ReportPdfGenerator.columnWidths(1f, 1.5f, 1.1f, 0.9f, 1.1f, 1.3f);
        List<String[]> rows = new ArrayList<>();
        for (InventoryModel i : InventoryManagementController.getAll()) {
            rows.add(new String[]{dash(i.getProductId()), dash(i.getProductName()), dash(i.getProductType()),
                    dash(i.getQuantity()), dash(i.getExpireDate()), dash(i.getSupplierName())});
        }
        return ReportPdfGenerator.generate("Inventory Report", periodLabel,
                headers, widths, rows, "Total Items", String.valueOf(rows.size()), null);
    }

    private static byte[] billingReport(String periodLabel, LocalDate[] range) throws IOException {
        String[] headers = {"Billing ID", "Patient", "Dentist", "Date", "Total Amount"};
        float[] widths = ReportPdfGenerator.columnWidths(1f, 1.4f, 1.2f, 1f, 1.2f);
        List<String[]> rows = new ArrayList<>();
        double total = 0;
        for (BillingModel b : BillingManagementController.getAll()) {
            if (!inRange(b.getAppointmentDate(), range)) continue;
            rows.add(new String[]{dash(b.getBillingId()), dash(b.getPatientName()), dash(b.getDentistName()),
                    dash(b.getAppointmentDate()), currency(b.getTotalBillAmount())});
            total += b.getTotalBillAmount();
        }
        return ReportPdfGenerator.generate("Billing Report", periodLabel,
                headers, widths, rows, "Grand Total", currency(total), null);
    }

    // =========================================================================
    // Financial reports
    // =========================================================================

    private static byte[] inventorySalesReport(String periodLabel) throws IOException {
        String[] headers = {"Inventory ID", "Product Name", "Quantity", "Unit Price", "Value"};
        float[] widths = ReportPdfGenerator.columnWidths(1f, 1.8f, 1f, 1.2f, 1.3f);
        List<String[]> rows = new ArrayList<>();
        double total = 0;
        for (InventoryModel i : InventoryManagementController.getAll()) {
            double price = parseDouble(i.getSellingPrice());
            int qty = parseInt(i.getQuantity());
            double value = price * qty;
            total += value;
            rows.add(new String[]{dash(i.getProductId()), dash(i.getProductName()), dash(i.getQuantity()),
                    price > 0 ? currency(price) : "-", value > 0 ? currency(value) : "-"});
        }
        return ReportPdfGenerator.generate("Inventory Sales Report", periodLabel,
                headers, widths, rows, "Total Inventory Value", currency(total),
                "Valuation = quantity on hand x recorded unit selling price; items with no recorded price are excluded from the total.");
    }

    private static byte[] servicesIncomeReport(String periodLabel, LocalDate[] range) throws IOException {
        String[] headers = {"Billing ID", "Patient", "Date", "Appt. Charges", "Clinical Charges", "Service Income"};
        float[] widths = ReportPdfGenerator.columnWidths(1f, 1.4f, 1f, 1.3f, 1.2f, 1.3f);
        List<String[]> rows = new ArrayList<>();
        double total = 0;
        for (BillingModel b : BillingManagementController.getAll()) {
            if (!inRange(b.getAppointmentDate(), range)) continue;
            double income = b.getAppointmentCharges() + b.getClinicalTotal();
            total += income;
            rows.add(new String[]{dash(b.getBillingId()), dash(b.getPatientName()), dash(b.getAppointmentDate()),
                    currency(b.getAppointmentCharges()), currency(b.getClinicalTotal()), currency(income)});
        }
        return ReportPdfGenerator.generate("Services Income Report", periodLabel,
                headers, widths, rows, "Grand Total", currency(total),
                "Service income = Appointment Charges + Clinical Charges (medicine sales excluded).");
    }

    private static byte[] totalIncomeReport(String periodLabel, LocalDate[] range) throws IOException {
        String[] headers = {"Billing ID", "Patient", "Date", "Total Amount"};
        float[] widths = ReportPdfGenerator.columnWidths(1f, 1.6f, 1.1f, 1.3f);
        List<String[]> rows = new ArrayList<>();
        double total = 0;
        for (BillingModel b : BillingManagementController.getAll()) {
            if (!inRange(b.getAppointmentDate(), range)) continue;
            rows.add(new String[]{dash(b.getBillingId()), dash(b.getPatientName()), dash(b.getAppointmentDate()),
                    currency(b.getTotalBillAmount())});
            total += b.getTotalBillAmount();
        }
        return ReportPdfGenerator.generate("Total Income Report", periodLabel,
                headers, widths, rows, "Grand Total Income", currency(total), null);
    }

    private static byte[] totalExpensesReport(String periodLabel) throws IOException {
        String[] headers = {"Tracking ID", "Product Name", "Type", "Quantity", "Est. Cost"};
        float[] widths = ReportPdfGenerator.columnWidths(1.1f, 1.6f, 1.1f, 0.9f, 1.2f);
        Map<String, InventoryModel> byId = new HashMap<>();
        for (InventoryModel i : InventoryManagementController.getAll()) {
            byId.put(i.getProductId(), i);
        }
        List<String[]> rows = new ArrayList<>();
        double total = 0;
        for (SupplyRequestModel r : SupplyRequestController.getAll()) {
            InventoryModel matched = byId.get(r.getProductId());
            double unitPrice = matched != null ? parseDouble(matched.getSellingPrice()) : 0;
            int qty = parseInt(r.getQuantity());
            double cost = unitPrice * qty;
            total += cost;
            rows.add(new String[]{dash(r.getTrackingId()), dash(r.getProductName()), dash(r.getProductType()),
                    dash(r.getQuantity()), cost > 0 ? currency(cost) : "-"});
        }
        return ReportPdfGenerator.generate("Total Expenses Report", periodLabel,
                headers, widths, rows, "Estimated Total Expense", currency(total),
                "Supply requests aren't timestamped and no separate supplier cost is recorded, so this is an "
                        + "estimate using each item's recorded selling price, not an actual purchase invoice total.");
    }

    private static byte[] dentistFeeReport(String periodLabel, LocalDate[] range) throws IOException {
        String[] headers = {"Dentist ID", "Name", "Consultation Fee", "Appointments", "Est. Revenue"};
        float[] widths = ReportPdfGenerator.columnWidths(1f, 1.5f, 1.3f, 1.1f, 1.3f);
        Map<String, Integer> apptCounts = new HashMap<>();
        for (AppointmentModel a : AppointmentManagementController.getAll()) {
            if (!inRange(a.getDate(), range)) continue;
            apptCounts.merge(a.getDentistName(), 1, Integer::sum);
        }
        List<String[]> rows = new ArrayList<>();
        double total = 0;
        for (DentistModel d : DentistManagementController.getDirectory().values()) {
            double fee = parseDouble(d.getConsultationFee());
            int count = apptCounts.getOrDefault(d.getFullName(), 0);
            double revenue = fee * count;
            total += revenue;
            rows.add(new String[]{dash(d.getDentistId()), dash(d.getFullName()), currency(fee),
                    String.valueOf(count), currency(revenue)});
        }
        return ReportPdfGenerator.generate("Dentist Consultation Fee Report", periodLabel,
                headers, widths, rows, "Estimated Total Revenue", currency(total),
                "Estimated Revenue = Consultation Fee x appointments in the selected period (excludes clinical/medicine charges).");
    }

    // =========================================================================
    // Small shared helpers
    // =========================================================================

    private static double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try {
            return Double.parseDouble(s.replaceAll("[^0-9.\\-]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int parseInt(String s) {
        return (int) Math.round(parseDouble(s));
    }

    private static String currency(double v) {
        return BillingManagementController.formatCurrency(v);
    }

    private static String dash(String v) {
        return v == null || v.trim().isEmpty() ? "-" : v;
    }
}
