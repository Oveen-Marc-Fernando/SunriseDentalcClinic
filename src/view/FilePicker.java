package view;

import java.awt.Color;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Attaches a small upload icon to the right edge of a plain
 * {@link JTextField} — clicking it opens a native file chooser and writes
 * the picked file's absolute path into the field, instead of the user
 * having to type a file path by hand.
 *
 * Built the same way as {@link DateTimePicker}: entirely from OUTSIDE
 * NetBeans GEN-BEGIN/GEN-END blocks (via {@link IconFactory#dockIconInField}),
 * so call it from a view's constructor right after initComponents().
 *
 * @author oveen
 */
public final class FilePicker {

    private static final Color ICON_COLOR = new Color(90, 90, 90);
    private static final int ICON_SIZE = 15;

    private FilePicker() {
    }

    /** Adds an upload icon that opens a file chooser filtered to common image types. */
    public static void attachImageUpload(JTextField field) {
        attachUpload(field, "Select Image",
                new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp"));
    }

    /** Adds an upload icon that opens a file chooser accepting any file type. */
    public static void attachUpload(JTextField field, String dialogTitle) {
        attachUpload(field, dialogTitle, null);
    }

    private static void attachUpload(JTextField field, String dialogTitle, FileNameExtensionFilter filter) {
        IconFactory.dockIconInField(field, IconFactory.upload(ICON_COLOR, ICON_SIZE), "Upload from file",
                () -> showFileChooser(field, dialogTitle, filter));
    }

    private static void showFileChooser(JTextField field, String dialogTitle, FileNameExtensionFilter filter) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(dialogTitle);
        if (filter != null) {
            chooser.setFileFilter(filter);
        }

        String current = field.getText();
        if (current != null && !current.isBlank()) {
            File existing = new File(current);
            File dir = existing.getParentFile();
            if (dir != null && dir.exists()) {
                chooser.setCurrentDirectory(dir);
            }
        }

        int result = chooser.showOpenDialog(SwingUtilities.getWindowAncestor(field));
        if (result == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
}
