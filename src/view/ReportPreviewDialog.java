package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Administration &gt; Reports' "View" popup — same convention as
 * {@link BillPreviewDialog}: renders the already-generated report PDF to an
 * image (via PDFBox's {@link PDFRenderer}) inside a scrollable read-only
 * popup with Download/View/Close actions, instead of shelling out to
 * whatever PDF viewer happens to be installed. Every page is rendered and
 * stacked (not just page 1), since a report can genuinely run long.
 *
 * @author oveen
 */
public class ReportPreviewDialog extends JDialog {

    private final byte[] pdfBytes;
    private final String suggestedFileName;

    /**
     * @param owner              the screen this popup is opened from
     * @param reportTitle        shown in the popup's banner, e.g. "Patient Report — Weekly"
     * @param pdfBytes           the already-generated report PDF
     * @param suggestedFileName  default file name offered by the Download button
     */
    public ReportPreviewDialog(Window owner, String reportTitle, byte[] pdfBytes, String suggestedFileName) {
        super(owner, "Report Preview", ModalityType.APPLICATION_MODAL);
        this.pdfBytes = pdfBytes;
        this.suggestedFileName = suggestedFileName;

        setSize(660, 800);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(buildBanner(reportTitle), BorderLayout.NORTH);
        content.add(buildPreviewPanel(), BorderLayout.CENTER);
        content.add(buildButtonBar(), BorderLayout.SOUTH);
        setContentPane(content);
    }

    private JComponent buildBanner(String reportTitle) {
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 14));
        banner.setBackground(new Color(231, 115, 36));
        banner.setPreferredSize(new Dimension(0, 50));

        JLabel text = new JLabel(reportTitle);
        text.setFont(new Font("Segoe UI", Font.BOLD, 16));
        text.setForeground(Color.WHITE);
        banner.add(text);
        return banner;
    }

    /** Renders every page of the generated PDF to an image, stacked and scrollable. */
    private JComponent buildPreviewPanel() {
        JPanel pagesPanel = new JPanel();
        pagesPanel.setLayout(new BoxLayout(pagesPanel, BoxLayout.Y_AXIS));
        pagesPanel.setBackground(new Color(245, 245, 245));

        if (pdfBytes != null) {
            try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                PDFRenderer renderer = new PDFRenderer(doc);
                int targetWidth = 590;
                for (int i = 0; i < doc.getNumberOfPages(); i++) {
                    BufferedImage rendered = renderer.renderImageWithDPI(i, 110);
                    int targetHeight = Math.round((float) rendered.getHeight() / rendered.getWidth() * targetWidth);
                    Image scaled = rendered.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

                    JLabel pageLabel = new JLabel(new ImageIcon(scaled));
                    pageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    pageLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                    pagesPanel.add(pageLabel);
                }
            } catch (IOException e) {
                pagesPanel.add(new JLabel("Preview unavailable: " + e.getMessage()));
            }
        } else {
            pagesPanel.add(new JLabel("Preview unavailable."));
        }

        JScrollPane scroll = new JScrollPane(pagesPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(new Color(245, 245, 245));
        scroll.getViewport().setBackground(new Color(245, 245, 245));
        return scroll;
    }

    private JComponent buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        bar.setBackground(new Color(248, 249, 250));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnDownload = styledButton("Download", new Color(0, 122, 255));
        btnDownload.addActionListener(e -> downloadPdf());
        bar.add(btnDownload);

        JButton btnView = styledButton("View", new Color(255, 193, 7));
        btnView.setForeground(new Color(30, 30, 30)); // dark text — white-on-yellow reads poorly
        btnView.addActionListener(e -> viewInBrowser());
        bar.add(btnView);

        JButton btnClose = styledButton("Close", new Color(100, 100, 100));
        btnClose.addActionListener(e -> dispose());
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
            JOptionPane.showMessageDialog(this, "No report PDF to download.", "Download", JOptionPane.WARNING_MESSAGE);
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
            IconFactory.showSuccessDialog(this, "Report saved to " + target.getAbsolutePath(), null);
        } catch (IOException e) {
            IconFactory.showErrorDialog(this, "Couldn't save the file — " + e.getMessage(), null);
        }
    }

    /**
     * Opens the generated report PDF in the system's default browser — same
     * convention as {@link AppointmentPreviewDialog}/{@link BillPreviewDialog}'s
     * own View button. Written to a delete-on-exit temp file first, since
     * {@link Desktop#browse} needs a real file on disk to hand to the browser.
     */
    private void viewInBrowser() {
        if (pdfBytes == null) {
            JOptionPane.showMessageDialog(this, "No report PDF to view.", "View", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            File temp = File.createTempFile("Report_", ".pdf");
            temp.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(temp)) {
                fos.write(pdfBytes);
            }
            Desktop.getDesktop().browse(temp.toURI());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't open the report in your browser:\n" + e.getMessage(),
                    "View Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
