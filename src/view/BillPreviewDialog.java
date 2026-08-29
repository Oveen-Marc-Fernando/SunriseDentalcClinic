package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.BillingModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 *
 *
 * @author oveen
 */
public class BillPreviewDialog extends JDialog {

    private final BillingModel bill;
    private final byte[] pdfBytes;
    private final String suggestedFileName;

    /**
     * @param owner              the screen this popup is opened from
     * @param bill               the bill to render
     * @param showSuccessBanner  true right after a bill was just saved (Save button) — also the only case that shows an Email action; false for a plain re-preview (Print Bill button)
     * @param onClose            runs after this dialog closes — e.g. navigate back to the grid. May be null.
     */
    public BillPreviewDialog(Window owner, BillingModel bill, boolean showSuccessBanner, Runnable onClose) {
        super(owner, "Bill Preview", ModalityType.APPLICATION_MODAL);
        this.bill = bill;

        byte[] generated;
        try {
            generated = util.BillPdfGenerator.generate(bill);
        } catch (IOException e) {
            generated = null;
            JOptionPane.showMessageDialog(owner, "Couldn't generate the bill PDF:\n" + e.getMessage(),
                    "PDF Generation Failed", JOptionPane.ERROR_MESSAGE);
        }
        this.pdfBytes = generated;
        this.suggestedFileName = "Bill_" + (bill.getBillingId() != null ? bill.getBillingId() : "unknown") + ".pdf";

        setSize(660, 800);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        if (showSuccessBanner) {
            content.add(buildSuccessBanner(), BorderLayout.NORTH);
        }
        content.add(buildPreviewPanel(), BorderLayout.CENTER);
        content.add(buildButtonBar(onClose, showSuccessBanner), BorderLayout.SOUTH);
        setContentPane(content);
    }

    private JComponent buildSuccessBanner() {
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 14));
        banner.setBackground(new Color(0, 200, 83));
        banner.setPreferredSize(new Dimension(0, 50));

        JLabel checkmark = new JLabel("✓");
        checkmark.setFont(new Font("Segoe UI", Font.BOLD, 18));
        checkmark.setForeground(Color.WHITE);

        JLabel text = new JLabel("Bill Generated Successfully!");
        text.setFont(new Font("Segoe UI", Font.BOLD, 16));
        text.setForeground(Color.WHITE);

        banner.add(checkmark);
        banner.add(text);
        return banner;
    }

    /** Renders page 1 of the generated PDF to an image and shows it read-only, scrollable if it overflows. */
    private JComponent buildPreviewPanel() {
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        if (pdfBytes != null) {
            try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                PDFRenderer renderer = new PDFRenderer(doc);
                BufferedImage rendered = renderer.renderImageWithDPI(0, 110);
                int targetWidth = 590;
                int targetHeight = Math.round((float) rendered.getHeight() / rendered.getWidth() * targetWidth);
                Image scaled = rendered.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
            } catch (IOException e) {
                imgLabel.setText("Preview unavailable: " + e.getMessage());
            }
        } else {
            imgLabel.setText("Preview unavailable.");
        }

        JScrollPane scroll = new JScrollPane(imgLabel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(new Color(245, 245, 245));
        scroll.getViewport().setBackground(new Color(245, 245, 245));
        return scroll;
    }

    private JComponent buildButtonBar(Runnable onClose, boolean showEmail) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        bar.setBackground(new Color(248, 249, 250));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnDownload = styledButton("Download", new Color(0, 122, 255));
        btnDownload.addActionListener(e -> downloadPdf());
        bar.add(btnDownload);

        JButton btnPrint = styledButton("Print", new Color(255, 152, 0));
        btnPrint.addActionListener(e -> printPdf());
        bar.add(btnPrint);

        if (showEmail) {
            final JButton btnEmail = styledButton("Email", new Color(0, 153, 204));
            btnEmail.addActionListener(e -> emailPdf(btnEmail));
            bar.add(btnEmail);
        }

        JButton btnClose = styledButton("Close", new Color(100, 100, 100));
        btnClose.addActionListener(e -> {
            dispose();
            if (onClose != null) onClose.run();
        });
        bar.add(btnClose);

        return bar;
    }

    private static JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(120, 38));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void downloadPdf() {
        if (pdfBytes == null) {
            JOptionPane.showMessageDialog(this, "No bill PDF to download.", "Download", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(suggestedFileName));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File target = chooser.getSelectedFile();
        if (!target.getName().toLowerCase().endsWith(".pdf")) {
            target = new File(target.getParentFile(), target.getName() + ".pdf");
        }
        try (FileOutputStream fos = new FileOutputStream(target)) {
            fos.write(pdfBytes);
            JOptionPane.showMessageDialog(this, "Bill saved to:\n" + target.getAbsolutePath(),
                    "Downloaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Couldn't save the file:\n" + e.getMessage(),
                    "Download Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Looks up the patient's email on file (best-effort match by name) as a
     * starting point, but always shows it back to office staff to confirm
     * or change before anything actually sends — the auto-matched address
     * is never trusted silently, since a name match can resolve to the
     * wrong person. Runs the actual send off the EDT so a slow/unreachable
     * SMTP server doesn't freeze the popup.
     */
    private void emailPdf(JButton triggerButton) {
        if (pdfBytes == null) {
            JOptionPane.showMessageDialog(this, "No bill PDF to email.", "Email", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!mail.MailSender.isConfigured()) {
            JOptionPane.showMessageDialog(this,
                    "Email sending isn't set up yet — a Gmail App Password still needs to be added to "
                    + "src/mail/mail.properties before bills can be emailed. See that file's comments for the "
                    + "exact steps.",
                    "Email Not Configured", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String matchedEmail = controller.PatientManagementController.findEmailByFullName(bill.getPatientName());
        String promptText = (matchedEmail != null && !matchedEmail.trim().isEmpty()) ? matchedEmail.trim() : "";

        IconFactory.showEmailConfirmDialog(this,
                "Send bill " + bill.getBillingId() + " (" + bill.getPatientName() + ") to this email address:\n"
                + "(auto-filled from Patient Management where possible — check it's the right person before sending)",
                promptText,
                email -> sendConfirmedEmail(triggerButton, email));
    }

    private void sendConfirmedEmail(JButton triggerButton, String email) {
        final String toEmail = email.trim();
        if (!toEmail.contains("@") || !toEmail.contains(".")) {
            JOptionPane.showMessageDialog(this, "\"" + toEmail + "\" doesn't look like a valid email address.",
                    "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        triggerButton.setEnabled(false);
        String originalText = triggerButton.getText();
        triggerButton.setText("Sending...");

        new SwingWorker<Void, Void>() {
            private Exception failure;

            @Override
            protected Void doInBackground() {
                try {
                    mail.MailSender.sendBillEmail(toEmail, bill.getPatientName(), bill.getBillingId(), pdfBytes);
                } catch (Exception e) {
                    failure = e;
                }
                return null;
            }

            @Override
            protected void done() {
                triggerButton.setEnabled(true);
                triggerButton.setText(originalText);
                if (failure == null) {
                    controller.BillingManagementController.incrementEmailSentCount(bill.getBillingId());
                    IconFactory.showSuccessDialog(BillPreviewDialog.this,
                            "Bill " + bill.getBillingId() + " emailed to\n" + toEmail + "!", null);
                } else {
                    JOptionPane.showMessageDialog(BillPreviewDialog.this,
                            "Couldn't send the email:\n" + failure.getMessage(),
                            "Email Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void printPdf() {
        if (pdfBytes == null) {
            JOptionPane.showMessageDialog(this, "No bill PDF to print.", "Print", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(doc));
            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't print the bill:\n" + e.getMessage(),
                    "Print Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
