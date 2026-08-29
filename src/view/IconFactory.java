package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 *
 * @author oveen
 */
public final class IconFactory {

    private IconFactory() {
    }
    /** Pencil glyph – used for "Edit" / "Update" actions. */
    public static Icon pencil(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            // Draw a horizontal pencil silhouette, then rotate 45° about the
            // icon's center so it reads as a diagonal "edit" pencil without
            // any coordinate ever leaving the icon's bounds.
            g2.rotate(-Math.PI / 4, w / 2.0, h / 2.0);

            float shaftLen = w * 0.86f;
            float shaftH = h * 0.24f;
            float x = (w - shaftLen) / 2f;
            float y = (h - shaftH) / 2f;
            float tipLen = shaftLen * 0.26f;
            float bodyLen = shaftLen - tipLen;

            g2.fill(new RoundRectangle2D.Float(x, y, bodyLen, shaftH, shaftH * 0.35f, shaftH * 0.35f));

            GeneralPath tip = new GeneralPath();
            tip.moveTo(x + bodyLen, y);
            tip.lineTo(x + shaftLen, y + shaftH / 2f);
            tip.lineTo(x + bodyLen, y + shaftH);
            tip.closePath();
            g2.fill(tip);
        });
    }

    /** Trash-can glyph – used for "Delete" actions. */
    public static Icon trash(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.4f, w * 0.07f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            float lidY = h * 0.28f;
            g2.drawLine((int) (w * 0.20f), (int) lidY, (int) (w * 0.80f), (int) lidY);
            g2.drawLine((int) (w * 0.38f), (int) (lidY - h * 0.10f), (int) (w * 0.62f), (int) (lidY - h * 0.10f));

            RoundRectangle2D.Float lid = new RoundRectangle2D.Float(
                    w * 0.36f, lidY - h * 0.15f, w * 0.28f, h * 0.10f, 3f, 3f);
            g2.draw(lid);

            GeneralPath bin = new GeneralPath();
            bin.moveTo(w * 0.24f, lidY + h * 0.03f);
            bin.lineTo(w * 0.30f, h * 0.88f);
            bin.quadTo(w * 0.30f, h * 0.94f, w * 0.36f, h * 0.94f);
            bin.lineTo(w * 0.64f, h * 0.94f);
            bin.quadTo(w * 0.70f, h * 0.94f, w * 0.70f, h * 0.88f);
            bin.lineTo(w * 0.76f, lidY + h * 0.03f);
            g2.draw(bin);

            g2.drawLine((int) (w * 0.41f), (int) (lidY + h * 0.14f), (int) (w * 0.41f), (int) (h * 0.80f));
            g2.drawLine((int) (w * 0.50f), (int) (lidY + h * 0.14f), (int) (w * 0.50f), (int) (h * 0.80f));
            g2.drawLine((int) (w * 0.59f), (int) (lidY + h * 0.14f), (int) (w * 0.59f), (int) (h * 0.80f));
        });
    }

    /** Checkmark glyph – used for "Approve" / active-status. */
    public static Icon check(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.6f, w * 0.13f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GeneralPath p = new GeneralPath();
            p.moveTo(w * 0.20f, h * 0.55f);
            p.lineTo(w * 0.42f, h * 0.75f);
            p.lineTo(w * 0.82f, h * 0.28f);
            g2.draw(p);
        });
    }

    /** Envelope glyph – used for "Email" actions / recipient-confirmation dialogs. */
    public static Icon mail(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.4f, w * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            RoundRectangle2D envelope = new RoundRectangle2D.Float(w * 0.08f, h * 0.20f, w * 0.84f, h * 0.60f, w * 0.08f, w * 0.08f);
            g2.draw(envelope);
            GeneralPath flap = new GeneralPath();
            flap.moveTo(w * 0.12f, h * 0.26f);
            flap.lineTo(w * 0.5f, h * 0.55f);
            flap.lineTo(w * 0.88f, h * 0.26f);
            g2.draw(flap);
        });
    }

    /** Simplified Instagram glyph (rounded-square frame, lens, flash dot) – "connect with" row on LoginForm. */
    public static Icon instagram(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.3f, w * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new RoundRectangle2D.Float(w * 0.08f, h * 0.08f, w * 0.84f, h * 0.84f, w * 0.28f, w * 0.28f));
            float lensD = w * 0.40f;
            g2.draw(new Ellipse2D.Float((w - lensD) / 2f, (h - lensD) / 2f, lensD, lensD));
            float dotD = w * 0.09f;
            g2.fill(new Ellipse2D.Float(w * 0.68f, h * 0.20f, dotD, dotD));
        });
    }

    /** Simplified TikTok glyph (musical note, the shape at the heart of the real logo) – "connect with" row on LoginForm. */
    public static Icon tiktok(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.4f, w * 0.10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int) (w * 0.42f), (int) (h * 0.16f), (int) (w * 0.42f), (int) (h * 0.68f));
            GeneralPath flag = new GeneralPath();
            flag.moveTo(w * 0.42f, h * 0.16f);
            flag.curveTo(w * 0.60f, h * 0.16f, w * 0.72f, h * 0.28f, w * 0.74f, h * 0.42f);
            g2.draw(flag);
            float noteD = w * 0.24f;
            g2.fill(new Ellipse2D.Float(w * 0.18f, h * 0.60f, noteD, noteD));
        });
    }

    /** Simplified WhatsApp glyph (chat-bubble outline with a phone-handset silhouette) – "connect with" row on LoginForm. */
    public static Icon whatsapp(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.3f, w * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GeneralPath bubble = new GeneralPath();
            bubble.moveTo(w * 0.20f, h * 0.85f);
            bubble.lineTo(w * 0.26f, h * 0.68f);
            bubble.curveTo(w * 0.12f, h * 0.52f, w * 0.14f, h * 0.26f, w * 0.36f, h * 0.14f);
            bubble.curveTo(w * 0.58f, h * 0.02f, w * 0.86f, h * 0.12f, w * 0.90f, h * 0.36f);
            bubble.curveTo(w * 0.94f, h * 0.60f, w * 0.76f, h * 0.82f, w * 0.50f, h * 0.80f);
            bubble.curveTo(w * 0.42f, h * 0.80f, w * 0.34f, h * 0.78f, w * 0.26f, h * 0.68f);
            g2.draw(bubble);

            GeneralPath handset = new GeneralPath();
            handset.moveTo(w * 0.38f, h * 0.36f);
            handset.curveTo(w * 0.38f, h * 0.30f, w * 0.46f, h * 0.30f, w * 0.46f, h * 0.36f);
            handset.curveTo(w * 0.46f, h * 0.42f, w * 0.40f, h * 0.40f, w * 0.42f, h * 0.48f);
            handset.curveTo(w * 0.46f, h * 0.58f, w * 0.52f, h * 0.62f, w * 0.60f, h * 0.58f);
            handset.curveTo(w * 0.68f, h * 0.54f, w * 0.66f, h * 0.60f, w * 0.66f, h * 0.62f);
            handset.curveTo(w * 0.66f, h * 0.68f, w * 0.56f, h * 0.68f, w * 0.48f, h * 0.62f);
            handset.curveTo(w * 0.40f, h * 0.56f, w * 0.36f, h * 0.46f, w * 0.38f, h * 0.36f);
            g2.draw(handset);
        });
    }

    /** Open-eye glyph – "show password" (click to reveal). */
    public static Icon eye(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.3f, w * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GeneralPath lens = new GeneralPath();
            lens.moveTo(w * 0.05f, h * 0.5f);
            lens.quadTo(w * 0.5f, h * 0.08f, w * 0.95f, h * 0.5f);
            lens.quadTo(w * 0.5f, h * 0.92f, w * 0.05f, h * 0.5f);
            lens.closePath();
            g2.draw(lens);
            float pupilD = h * 0.28f;
            g2.fill(new Ellipse2D.Float((w - pupilD) / 2f, (h - pupilD) / 2f, pupilD, pupilD));
        });
    }

    /** Eye-with-slash glyph – "hide password" (click to conceal). */
    public static Icon eyeOff(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.3f, w * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GeneralPath lens = new GeneralPath();
            lens.moveTo(w * 0.05f, h * 0.5f);
            lens.quadTo(w * 0.5f, h * 0.08f, w * 0.95f, h * 0.5f);
            lens.quadTo(w * 0.5f, h * 0.92f, w * 0.05f, h * 0.5f);
            lens.closePath();
            g2.draw(lens);
            float pupilD = h * 0.28f;
            g2.fill(new Ellipse2D.Float((w - pupilD) / 2f, (h - pupilD) / 2f, pupilD, pupilD));
            g2.drawLine((int) (w * 0.08f), (int) (h * 0.12f), (int) (w * 0.92f), (int) (h * 0.88f));
        });
    }

    /** Cross ("X") glyph – used for "Decline" / inactive-status. */
    public static Icon cross(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.6f, w * 0.13f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int) (w * 0.24f), (int) (h * 0.24f), (int) (w * 0.76f), (int) (h * 0.76f));
            g2.drawLine((int) (w * 0.76f), (int) (h * 0.24f), (int) (w * 0.24f), (int) (h * 0.76f));
        });
    }

    /** Left-pointing chevron ("‹") – used for "previous" navigation buttons.
     *  Drawn as a vector glyph rather than relying on the Unicode ‹/› characters
     *  in a JButton's text, which can render as "..." depending on the
     *  active font/JRE instead of the actual arrow. */
    public static Icon chevronLeft(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.6f, w * 0.14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GeneralPath p = new GeneralPath();
            p.moveTo(w * 0.62f, h * 0.20f);
            p.lineTo(w * 0.34f, h * 0.5f);
            p.lineTo(w * 0.62f, h * 0.80f);
            g2.draw(p);
        });
    }

    /** Right-pointing chevron ("›") – used for "next" navigation buttons. */
    public static Icon chevronRight(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.6f, w * 0.14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GeneralPath p = new GeneralPath();
            p.moveTo(w * 0.38f, h * 0.20f);
            p.lineTo(w * 0.66f, h * 0.5f);
            p.lineTo(w * 0.38f, h * 0.80f);
            g2.draw(p);
        });
    }

    /** Magnifying-glass glyph – used for search fields. Resolution-independent, unlike a raster icon. */
    public static Icon search(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.5f, w * 0.12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            float d = w * 0.58f;           // lens diameter
            float cx = w * 0.40f;
            float cy = h * 0.40f;
            g2.draw(new Ellipse2D.Float(cx - d / 2f, cy - d / 2f, d, d));

            float r = d / 2f;
            float handleX1 = cx + r * 0.72f;
            float handleY1 = cy + r * 0.72f;
            g2.drawLine((int) handleX1, (int) handleY1, (int) (w * 0.88f), (int) (h * 0.88f));
        });
    }

    /** Clock glyph – used for a neutral "Pending" status. */
    public static Icon clock(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.4f, w * 0.11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float pad = w * 0.14f;
            g2.draw(new Ellipse2D.Float(pad, pad, w - 2 * pad, h - 2 * pad));
            GeneralPath hands = new GeneralPath();
            hands.moveTo(w * 0.5f, h * 0.30f);
            hands.lineTo(w * 0.5f, h * 0.54f);
            hands.lineTo(w * 0.68f, h * 0.64f);
            g2.draw(hands);
        });
    }

    /** Calendar glyph – used for date/year picker fields. */
    public static Icon calendar(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.3f, w * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            float left = w * 0.12f, top = h * 0.20f, right = w * 0.88f, bottom = h * 0.90f;
            g2.draw(new RoundRectangle2D.Float(left, top, right - left, bottom - top, w * 0.12f, w * 0.12f));
            g2.draw(new java.awt.geom.Line2D.Float(left, top + h * 0.20f, right, top + h * 0.20f));

            float ringLen = h * 0.14f;
            g2.draw(new java.awt.geom.Line2D.Float(w * 0.32f, top - ringLen * 0.35f, w * 0.32f, top + ringLen * 0.35f));
            g2.draw(new java.awt.geom.Line2D.Float(w * 0.68f, top - ringLen * 0.35f, w * 0.68f, top + ringLen * 0.35f));
        });
    }

    /** Upload glyph (arrow into a tray) – used for file-upload fields, e.g. Profile Pic. */
    public static Icon upload(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.4f, w * 0.11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            float cx = w * 0.5f;
            GeneralPath arrow = new GeneralPath();
            arrow.moveTo(cx, h * 0.12f);
            arrow.lineTo(cx, h * 0.62f);
            g2.draw(arrow);

            GeneralPath head = new GeneralPath();
            head.moveTo(w * 0.30f, h * 0.34f);
            head.lineTo(cx, h * 0.12f);
            head.lineTo(w * 0.70f, h * 0.34f);
            g2.draw(head);

            // Tray
            GeneralPath tray = new GeneralPath();
            tray.moveTo(w * 0.16f, h * 0.72f);
            tray.lineTo(w * 0.16f, h * 0.88f);
            tray.lineTo(w * 0.84f, h * 0.88f);
            tray.lineTo(w * 0.84f, h * 0.72f);
            g2.draw(tray);
        });
    }

    /** Three ascending bars glyph — used for the "Analysis" shortcut on the Office Staff dashboard. */
    public static Icon barChart(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            float barWidth = w * 0.20f;
            float gap = w * 0.14f;
            float baseline = h * 0.88f;
            float[] heightFractions = {0.42f, 0.68f, 0.95f};
            float x = w * 0.08f;
            for (float hf : heightFractions) {
                float barHeight = baseline * hf;
                g2.fill(new RoundRectangle2D.Float(x, baseline - barHeight, barWidth, barHeight, 2.5f, 2.5f));
                x += barWidth + gap;
            }
        });
    }

    /** A simple headset glyph — used for the Help Desk shortcut on the Office Staff dashboard. */
    public static Icon headset(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.setStroke(new BasicStroke(Math.max(1.6f, w * 0.11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Headband
            g2.draw(new Arc2D.Float(w * 0.14f, h * 0.06f, w * 0.72f, h * 0.72f, 20, 140, Arc2D.OPEN));

            // Ear cups
            g2.fill(new RoundRectangle2D.Float(w * 0.12f, h * 0.46f, w * 0.18f, h * 0.32f, w * 0.09f, w * 0.09f));
            g2.fill(new RoundRectangle2D.Float(w * 0.70f, h * 0.46f, w * 0.18f, h * 0.32f, w * 0.09f, w * 0.09f));
        });
    }

    /**
     * A modern flat user-avatar glyph — a light circular badge with a
     * simple person silhouette — used for the navbar profile icon on every
     * screen. Drawn as a vector shape, like every other glyph in this
     * class, instead of loaded from a small raster file, so it stays crisp
     * at any display scale/DPI instead of blurring like an upscaled bitmap
     * would on a high-DPI monitor.
     */
    public static Icon userAvatar(int size) {
        return new VectorIcon(size, size, Color.BLACK, (g2, w, h) -> {
            Ellipse2D.Float outer = new Ellipse2D.Float(0, 0, w, h);
            g2.setColor(Color.BLACK);
            g2.fill(outer);

            g2.clip(outer);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(Math.max(1.6f, w * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Head — an outline circle, not filled.
            float headD = w * 0.30f;
            g2.draw(new Ellipse2D.Float((w - headD) / 2f, h * 0.20f, headD, headD));

            // Shoulders — an outline circle mostly below the frame, its
            // bottom naturally cropped by the clip above into a dome shape.
            float bodyD = w * 0.74f;
            g2.draw(new Ellipse2D.Float((w - bodyD) / 2f, h * 0.62f, bodyD, bodyD));
        });
    }

    /**
     * Same person glyph as {@link #userAvatar}, but with no background
     * circle — just the outline strokes, transparent everywhere else. Used
     * when the icon sits directly on an already-black surface (e.g. nested
     * in the navbar pill) instead of floating on its own as a badge.
     */
    public static Icon userGlyph(Color color, int size) {
        return new VectorIcon(size, size, color, (g2, w, h) -> {
            g2.clipRect(0, 0, w, h);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(Math.max(1.6f, w * 0.11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            float headD = w * 0.34f;
            g2.draw(new Ellipse2D.Float((w - headD) / 2f, h * 0.14f, headD, headD));

            float bodyD = w * 0.82f;
            g2.draw(new Ellipse2D.Float((w - bodyD) / 2f, h * 0.62f, bodyD, bodyD));
        });
    }

    // =========================================================================
    // Ready-made controls built from the glyphs above
    // =========================================================================

    /**
     * A flat, rounded, drop-shadowed action button with a centered vector
     * glyph — the shared look for every Update/Delete/Approve/Decline button
     * across all grids.
     */
    public static JButton actionButton(Icon icon, Color bgColor, String tooltip) {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                boolean pressed = getModel().isPressed();
                boolean hover = getModel().isRollover();

                // Soft shadow for a subtle "lifted" look.
                if (!pressed) {
                    g2.setColor(new Color(0, 0, 0, hover ? 45 : 25));
                    g2.fill(new RoundRectangle2D.Float(1, 2, w - 2, h - 2, 10, 10));
                }

                Color fill = pressed ? bgColor.darker() : (hover ? brighten(bgColor, 18) : bgColor);
                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Float(0, pressed ? 1 : 0, w - 1, h - 2, 10, 10));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        if (icon != null) {
            b.setIcon(icon);
        }
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText(tooltip);
        return b;
    }

    /**
     * A rounded status "pill" — a small colored badge with an icon and a
     * short label (e.g. "Active" / "Inactive"), replacing bare emoji-style
     * icons for Yes/No style columns.
     */
    public static JLabel statusPill(boolean positive, String positiveText, String negativeText) {
        Color color = positive ? new Color(40, 167, 69) : new Color(220, 53, 69);
        String text = positive ? positiveText : negativeText;
        Icon glyph = positive ? check(color, 12) : cross(color, 12);

        JLabel pill = new JLabel(text, glyph, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tint(color, 0.85f));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
                g2.setColor(tint(color, 0.55f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setIconTextGap(5);
        pill.setForeground(color.darker());
        pill.setFont(pill.getFont().deriveFont(java.awt.Font.BOLD, 11.5f));
        pill.setHorizontalAlignment(SwingConstants.CENTER);
        pill.setOpaque(false);
        pill.setPreferredSize(new Dimension(positive ? 74 : 82, 24));
        return pill;
    }

    /**
     * Three-state version of {@link #statusPill(boolean, String, String)} for
     * columns with more than a plain yes/no — "Completed" (green check),
     * "Pending" (amber clock), "Rejected" (red cross), falling back to a
     * neutral gray dot for anything else.
     */
    public static JLabel statusPill(String status) {
        String s = status == null ? "" : status.trim();
        Color color;
        Icon glyph;
        if (s.equalsIgnoreCase("Completed") || s.equalsIgnoreCase("Approved")) {
            color = new Color(40, 167, 69);
            glyph = check(color, 12);
        } else if (s.equalsIgnoreCase("Rejected")) {
            color = new Color(220, 53, 69);
            glyph = cross(color, 12);
        } else if (s.equalsIgnoreCase("Pending")) {
            color = new Color(255, 152, 0);
            glyph = clock(color, 12);
        } else {
            color = new Color(120, 120, 120);
            glyph = null;
        }

        JLabel pill = new JLabel(s, glyph, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tint(color, 0.85f));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
                g2.setColor(tint(color, 0.55f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setIconTextGap(5);
        pill.setForeground(color.darker());
        pill.setFont(pill.getFont().deriveFont(java.awt.Font.BOLD, 11.5f));
        pill.setHorizontalAlignment(SwingConstants.CENTER);
        pill.setOpaque(false);
        pill.setPreferredSize(new Dimension(90, 24));
        return pill;
    }

    /**
     * A small red circular badge showing a count (e.g. "7"), used on the
     * Office Staff dashboard tiles to show the record count of the grid
     * behind each tile. Returns {@code null} for a count of 0 so callers can
     * simply skip adding it (no badge shown).
     */
    public static JLabel countBadge(int count) {
        if (count <= 0) {
            return null;
        }
        String text = count > 99 ? "99+" : String.valueOf(count);
        Color color = new Color(239, 68, 68); // bright red

        JLabel badge = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                int d = Math.min(getWidth(), getHeight());
                g2.fill(new Ellipse2D.Float((getWidth() - d) / 2f, (getHeight() - d) / 2f, d, d));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(badge.getFont().deriveFont(Font.BOLD, count > 99 ? 9.5f : 12f));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(false);
        int size = text.length() > 2 ? 30 : 26;
        badge.setPreferredSize(new Dimension(size, size));
        badge.setSize(size, size);
        return badge;
    }

    /** A plain circular badge (icon only, no text) for compact license/status columns. */
    public static JLabel statusDot(boolean positive) {
        Color color = positive ? new Color(40, 167, 69) : new Color(220, 53, 69);
        Icon glyph = positive ? check(Color.WHITE, 13) : cross(Color.WHITE, 13);
        return buildDot(color, glyph, positive ? "Active" : "Inactive");
    }

    /**
     * A tri-state circular badge (icon only, no text) driven by a status
     * word: "Approved" → green check, "Declined"/"Rejected" → red cross,
     * anything else (e.g. "Pending") → neutral amber clock. Used for
     * read-only status columns where the viewer cannot act on the record.
     */
    public static JLabel statusDot(String status) {
        String s = status == null ? "" : status.trim().toLowerCase();
        Color color;
        Icon glyph;
        if (s.equals("approved") || s.equals("active") || s.equals("verified")) {
            color = new Color(40, 167, 69);
            glyph = check(Color.WHITE, 13);
        } else if (s.equals("declined") || s.equals("rejected") || s.equals("inactive")) {
            color = new Color(220, 53, 69);
            glyph = cross(Color.WHITE, 13);
        } else {
            color = new Color(245, 158, 11);
            glyph = clock(Color.WHITE, 13);
        }
        return buildDot(color, glyph, status == null || status.isBlank() ? "Pending" : status);
    }

    private static JLabel buildDot(Color color, Icon glyph, String tooltip) {
        JLabel dot = new JLabel(glyph, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - d) / 2, y = (getHeight() - d) / 2;
                g2.setColor(color);
                g2.fill(new Ellipse2D.Float(x, y, d, d));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        dot.setPreferredSize(new Dimension(26, 26));
        dot.setOpaque(false);
        dot.setToolTipText(tooltip);
        return dot;
    }

    // =========================================================================
    // Docked icon inside a plain JTextField (calendar / clock / upload, etc.)
    // =========================================================================

    private static final int DOCK_ICON_SLOT = 24;

    /**
     * Docks a small clickable icon on the right edge of a plain
     * {@link JTextField}, on top of it, so the field looks like it has a
     * built-in action icon (date picker, upload, …).
     *
     * Works entirely from OUTSIDE NetBeans GEN-BEGIN/GEN-END blocks: it only
     * reads the field's existing bounds/parent (already set by
     * initComponents()) and adds a sibling icon next to it — call it from a
     * view's constructor right after initComponents(), never from inside
     * generated code, or the GUI Builder will strip it on next regeneration.
     */
    public static void dockIconInField(JTextField field, Icon glyph, String tooltip, Runnable onClick) {
        Container parent = field.getParent();
        if (parent == null) {
            return;
        }

        // Keep the field's existing border, just pad the right side so typed
        // text never runs under the icon.
        Border original = field.getBorder();
        Border padding = new EmptyBorder(0, 4, 0, DOCK_ICON_SLOT);
        field.setBorder(original == null ? padding : BorderFactory.createCompoundBorder(original, padding));

        JLabel icon = new JLabel(glyph, SwingConstants.CENTER);
        icon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        icon.setToolTipText(tooltip);
        Rectangle b = field.getBounds();
        icon.setBounds(b.x + b.width - DOCK_ICON_SLOT, b.y, DOCK_ICON_SLOT, b.height);
        parent.add(icon);
        parent.setComponentZOrder(icon, 0); // z-order 0 = frontmost, so it sits visibly on top of the field

        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
    }

    // =========================================================================
    // Brand logo (navbar wordmark) — vector-drawn to replace the blurry
    // raster logo_scaled.png, which is only 130×40px and visibly blurs once
    // Windows display scaling stretches it (the same "small raster source
    // shown unscaled at HiDPI" root cause as the old profile icon).
    // =========================================================================

    /**
     * Draws the "SUNRISE / DENTAL CLINIC" navbar wordmark — a teal
     * tooth-badge circle plus bold "SUNRISE" and a smaller "DENTAL CLINIC"
     * subtitle — entirely with vector shapes and real font glyphs instead of
     * a fixed-resolution PNG, so it stays crisp at any display scale.
     * Transparent background, meant to sit directly on the navbar's own
     * black pill (matching {@code logo_scaled.png}'s original colors).
     */
    public static Icon brandLogo(int width, int height) {
        return brandLogo(width, height, new Color(231, 115, 36)); // this app's established brand orange
    }

    /**
     * Same wordmark as {@link #brandLogo(int, int)}, but with the "DENTAL
     * CLINIC" subtitle drawn in white instead of brand-orange — the default
     * orange subtitle is illegible on this app's own orange surfaces (e.g.
     * LoginForm's left panel), so callers placing the logo on a colored
     * background (anything that isn't navBar's black) should use this
     * instead.
     */
    public static Icon brandLogoOnColor(int width, int height) {
        return brandLogo(width, height, Color.WHITE);
    }

    private static Icon brandLogo(int width, int height, Color subtitleColor) {
        return new VectorIcon(width, height, Color.WHITE, (g2, w, h) -> {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Teal badge circle.
            float badgeD = h * 0.82f;
            float badgeX = w * 0.02f;
            float badgeY = (h - badgeD) / 2f;
            g2.setColor(new Color(60, 120, 120));
            Ellipse2D.Float badge = new Ellipse2D.Float(badgeX, badgeY, badgeD, badgeD);
            g2.fill(badge);

            // Simple white tooth silhouette centered in the badge.
            g2.setColor(Color.WHITE);
            float cx = badgeX + badgeD / 2f;
            float cy = badgeY + badgeD / 2f;
            float tw = badgeD * 0.46f;
            float th = badgeD * 0.50f;
            GeneralPath tooth = new GeneralPath();
            tooth.moveTo(cx - tw * 0.55f, cy - th * 0.45f);
            tooth.curveTo(cx - tw * 0.65f, cy - th * 0.05f, cx - tw * 0.45f, cy + th * 0.15f, cx - tw * 0.32f, cy + th * 0.50f);
            tooth.curveTo(cx - tw * 0.22f, cy + th * 0.68f, cx - tw * 0.08f, cy + th * 0.30f, cx, cy + th * 0.30f);
            tooth.curveTo(cx + tw * 0.08f, cy + th * 0.30f, cx + tw * 0.22f, cy + th * 0.68f, cx + tw * 0.32f, cy + th * 0.50f);
            tooth.curveTo(cx + tw * 0.45f, cy + th * 0.15f, cx + tw * 0.65f, cy - th * 0.05f, cx + tw * 0.55f, cy - th * 0.45f);
            tooth.curveTo(cx + tw * 0.35f, cy - th * 0.65f, cx - tw * 0.35f, cy - th * 0.65f, cx - tw * 0.55f, cy - th * 0.45f);
            tooth.closePath();
            g2.fill(tooth);

            // Wordmark text — real font glyphs, so it renders crisp at any
            // resolution instead of baking blur-prone small text into a PNG.
            float textX = badgeX + badgeD + w * 0.05f;

            g2.setFont(new Font("Segoe UI", Font.BOLD, Math.round(h * 0.44f)));
            g2.setColor(Color.WHITE);
            float titleBaseline = h * 0.56f;
            g2.drawString("SUNRISE", textX, titleBaseline);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, Math.round(h * 0.24f)));
            g2.setColor(subtitleColor);
            float subtitleBaseline = h * 0.88f;
            g2.drawString("DENTAL CLINIC", textX, subtitleBaseline);
        });
    }

    // =========================================================================
    // "Dim the parent" backdrop — used behind Login/Register style popups
    // =========================================================================

    private static final java.util.Map<javax.swing.JFrame, JPanel> DIM_PANES = new java.util.WeakHashMap<>();

    /**
     * Dims (or un-dims) a frame's whole content behind a translucent black
     * overlay — e.g. Public_Dashboard visibly receding behind the Login
     * popup, instead of staying fully bright while it's blocked. Installs a
     * custom glass pane the first time it's called for a given frame, then
     * just toggles its visibility on later calls.
     */
    public static void setDimmed(javax.swing.JFrame frame, boolean dimmed) {
        JPanel dimPane = DIM_PANES.computeIfAbsent(frame, f -> {
            JPanel p = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(new Color(0, 0, 0, 90));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            p.setOpaque(false);
            f.setGlassPane(p);
            return p;
        });
        dimPane.setVisible(dimmed);
    }

    // =========================================================================
    // Rounded corners for a plain JPanel (e.g. dashboard tile cards)
    // =========================================================================

    /**
     * Gives a plain {@link JPanel} rounded corners by turning off its normal
     * opaque square fill and instead painting a rounded-rect background as a
     * custom border. The border reads the panel's background color live at
     * paint time (not a fixed snapshot), so existing hover effects that call
     * {@code panel.setBackground(...)} keep working unchanged.
     *
     * Call this from outside any NetBeans GEN-BEGIN/GEN-END block (e.g. right
     * after initComponents()) — it only touches opacity/border, never the
     * panel's declared type, so it's safe from GUI Builder regeneration.
     */
    public static void roundCorners(JPanel panel, int radius) {
        panel.setOpaque(false);
        panel.setBorder(new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fill(new RoundRectangle2D.Float(x, y, w - 1, h - 1, radius, radius));
                g2.dispose();
            }
        });
    }

    /**
     * Shows the small custom-styled dropdown used for a dashboard's navbar
     * profile menu — a flat gray card with two bold, centered rows
     * ("Edit Profile" / "Logout") separated by a thin divider — anchored just
     * below {@code invoker}.
     *
     * This is a plain undecorated {@link javax.swing.JWindow}, not a
     * {@link javax.swing.JPopupMenu}. JPopupMenu delegates its painting to
     * the current look-and-feel's PopupMenuUI, which can silently reinstall
     * its own layout/background whenever the UI is (re)installed — in
     * practice that stomped our styling and left a plain unstyled box.
     * A raw JWindow has no such UI delegate, so what we paint is exactly
     * what renders — the same "build our own popup" approach already used
     * elsewhere in this app (e.g. the Help Desk dialog).
     */
    public static void showProfileMenu(java.awt.Window owner, JComponent invoker,
                                        Runnable onEditProfile, Runnable onLogout) {
        showOptionsMenu(owner, invoker,
                new String[]{"Edit Profile", "Logout"},
                new Runnable[]{onEditProfile, onLogout});
    }

    /**
     * General-purpose version of {@link #showProfileMenu} — the same flat
     * gray card, but with however many labeled rows you pass in
     * (not fixed at two), each wired to its own action. Used e.g. for
     * "change status" menus (Completed / Pending / Rejected) as well as the
     * navbar profile menu.
     */
    public static void showOptionsMenu(java.awt.Window owner, JComponent invoker,
                                        String[] labels, Runnable[] actions) {
        java.awt.Point loc = invoker.getLocationOnScreen();
        java.awt.Point topLeft = new java.awt.Point(
                loc.x + invoker.getWidth() - OPTIONS_MENU_WIDTH, loc.y + invoker.getHeight() + 6);
        showOptionsMenuAt(owner, topLeft, labels, actions);
    }

    private static final int OPTIONS_MENU_WIDTH = 180;

    /**
     * Same popup as {@link #showOptionsMenu(java.awt.Window, JComponent, String[], Runnable[])},
     * but anchored at an explicit screen point instead of derived from a
     * persistent component — for callers like a table-cell click, where
     * there's no standing component to measure (only the click location).
     *
     * @param topLeft where the popup's top-left corner should land, in
     *                screen coordinates (e.g. from {@code MouseEvent.getLocationOnScreen()})
     */
    public static void showOptionsMenuAt(java.awt.Window owner, java.awt.Point topLeft,
                                          String[] labels, Runnable[] actions) {
        if (labels.length == 0 || labels.length != actions.length) {
            return;
        }
        final Color rowBg = new Color(240, 240, 240);
        final Color rowHoverBg = new Color(224, 224, 224);
        final Color dividerColor = new Color(210, 210, 210);
        final int cardWidth = OPTIONS_MENU_WIDTH;
        final int rowHeight = 42;
        final int n = labels.length;
        final int cardHeight = rowHeight * n + (n - 1); // + 1px divider between each row

        javax.swing.JWindow popup = new javax.swing.JWindow(owner);
        popup.setLayout(null);
        popup.setSize(cardWidth, cardHeight);
        popup.setBackground(Color.WHITE);
        popup.getContentPane().setBackground(Color.WHITE);

        JPanel card = new JPanel(null);
        card.setOpaque(false);
        card.setBounds(0, 0, cardWidth, cardHeight);

        int y = 0;
        for (int i = 0; i < n; i++) {
            final int index = i;
            JPanel row = profileMenuRow(labels[i], cardWidth, rowHeight, rowBg, rowHoverBg);
            row.setBounds(0, y, cardWidth, rowHeight);
            // Plain flat corners on every row — no rounding.

            MouseAdapter click = new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    popup.dispose();
                    actions[index].run();
                }
            };
            row.addMouseListener(click);
            row.getComponent(0).addMouseListener(click);
            card.add(row);
            y += rowHeight;

            if (i < n - 1) {
                javax.swing.JSeparator sep = new javax.swing.JSeparator();
                sep.setForeground(dividerColor);
                sep.setBackground(rowBg);
                sep.setOpaque(true);
                sep.setBounds(0, y, cardWidth, 1);
                card.add(sep);
                y += 1;
            }
        }

        popup.add(card);

        // Keep the popup fully inside the owner window's bounds — anchoring
        // it exactly at a raw click point (e.g. a table cell near the right
        // edge) can otherwise push part of it outside the visible UI.
        java.awt.Point placement = topLeft;
        if (owner != null) {
            java.awt.Rectangle ownerBounds = owner.getBounds();
            int margin = 8;
            int maxX = ownerBounds.x + ownerBounds.width - cardWidth - margin;
            int maxY = ownerBounds.y + ownerBounds.height - cardHeight - margin;
            placement = new java.awt.Point(
                    Math.max(ownerBounds.x + margin, Math.min(topLeft.x, maxX)),
                    Math.max(ownerBounds.y + margin, Math.min(topLeft.y, maxY)));
        }
        popup.setLocation(placement.x, placement.y);

        // Dismiss the dropdown as soon as it loses focus (click elsewhere,
        // switch windows, etc.) — the same "click away to close" behavior a
        // real popup menu gives you for free.
        popup.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override public void windowLostFocus(java.awt.event.WindowEvent e) {
                popup.dispose();
            }
        });

        popup.setVisible(true);
        popup.requestFocus();
    }

    /**
     * One row of {@link #showProfileMenu}: a plain JPanel whose background is
     * painted by the caller's round-corner border, with the row's text as a
     * genuine CHILD JLabel on top of it.
     *
     * This two-component structure matters: Swing paints a component's own
     * border AFTER its own content but BEFORE its children. A JLabel's text
     * counts as "its own content", so painting a solid rounded-rect border
     * fill directly on a JLabel paints right over its text, hiding it
     * completely — which is exactly what happened when this used to be a
     * single styled JLabel. Wrapping the label as a child of a plain JPanel
     * fixes it: the panel's border (background fill) paints first, then the
     * child label's text paints on top, same as every dashboard tile card.
     */
    private static JPanel profileMenuRow(String text, int w, int h, Color bg, Color hoverBg) {
        JPanel row = new JPanel(null);
        row.setBackground(bg);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(25, 25, 25));
        label.setBounds(0, 0, w, h);
        row.add(label);

        // Same listener instance on both the row and its label child, so
        // hover highlighting stays consistent no matter which sub-area of
        // the row the mouse is actually over (the label fills the row, so
        // by default only IT would receive mouse events otherwise).
        MouseAdapter hover = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { row.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e)  { row.setBackground(bg); }
        };
        row.addMouseListener(hover);
        label.addMouseListener(hover);
        return row;
    }

    /**
     * Shows a custom-styled "success" confirmation — a rounded white card
     * with a big green vector checkmark, a message, and a rounded gray
     * "Close" pill button — in place of the OS's default
     * {@link javax.swing.JOptionPane} dialog chrome (title bar, system icon,
     * square rectangular OK button).
     *
     * @param owner   the window to center on and block (pass the calling
     *                screen's {@code this})
     * @param message the confirmation text, e.g. "Login Successful!"
     * @param onClose runs after the dialog is dismissed (may be {@code null})
     */
    public static void showSuccessDialog(java.awt.Window owner, String message, Runnable onClose) {
        final int dialogWidth = 420;
        final int circleSize = 90;
        final int messageWidth = dialogWidth - 40;
        final int messageTop = 130;
        final int gapAfterMessage = 20;
        final int buttonHeight = 40;
        final int bottomMargin = 30;

        // Dim whatever's behind this popup, same as the Login form's own
        // backdrop — the owner window is a JFrame for every caller of this
        // dialog, but guard the cast since the parameter type is the wider
        // java.awt.Window.
        final javax.swing.JFrame dimTarget = (owner instanceof javax.swing.JFrame) ? (javax.swing.JFrame) owner : null;
        if (dimTarget != null) {
            setDimmed(dimTarget, true);
        }

        javax.swing.JDialog dialog = new javax.swing.JDialog(owner, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(Color.WHITE); // in case the rounded card's own corners peek through

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        roundCorners(card, 20);

        JLabel lblCheck = new JLabel(check(Color.WHITE, 34), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 200, 83));
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblCheck.setBounds((dialogWidth - circleSize) / 2, 28, circleSize, circleSize);
        card.add(lblCheck);

        // Wrapped in HTML so long messages flow onto as many lines as they
        // need instead of getting clipped by a fixed-line-count guess — a
        // plain JLabel never wraps its text on its own. A literal "\n" in
        // the caller's message becomes an explicit <br>, for callers that
        // want to control exactly where a line breaks.
        String htmlMessage = escapeHtml(message).replace("\n", "<br>");
        JLabel lblMessage = new JLabel(
                "<html><div style='text-align:center;'>" + htmlMessage + "</div></html>",
                SwingConstants.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblMessage.setForeground(new Color(30, 30, 30));
        lblMessage.setVerticalAlignment(SwingConstants.TOP);

        // JLabel.getPreferredSize() ignores wrapping entirely for HTML
        // content and just returns the natural (unwrapped, single-line-ish)
        // size — no good for figuring out how tall a wrapped paragraph will
        // actually be. The reliable way is to ask the HTML View Swing
        // installs on the label to lay itself out at a fixed width and
        // report the span that takes.
        int messageHeight = 50; // fallback if the HTML view somehow isn't installed
        Object htmlView = lblMessage.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
        if (htmlView instanceof javax.swing.text.View) {
            javax.swing.text.View view = (javax.swing.text.View) htmlView;
            view.setSize(messageWidth, 0);
            messageHeight = (int) Math.ceil(view.getPreferredSpan(javax.swing.text.View.Y_AXIS));
        }
        lblMessage.setBounds(20, messageTop, messageWidth, messageHeight);
        card.add(lblMessage);

        int buttonY = messageTop + messageHeight + gapAfterMessage;
        JButton btnClose = actionButton(null, new Color(231, 115, 36), null); // this app's established brand orange
        btnClose.setText("OK");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBounds((dialogWidth - 130) / 2, buttonY, 130, buttonHeight);
        btnClose.addActionListener(e -> {
            dialog.dispose();
            if (dimTarget != null) {
                setDimmed(dimTarget, false);
            }
            if (onClose != null) onClose.run();
        });
        card.add(btnClose);

        int dialogHeight = buttonY + buttonHeight + bottomMargin;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(owner);

        dialog.setContentPane(card);
        dialog.setVisible(true);
    }

    /**
     * Shows a custom-styled "error" popup — a rounded white card with a big
     * red vector cross, a message, and a rounded "OK" pill button — in place
     * of the OS's default {@link javax.swing.JOptionPane} error dialog
     * chrome. Same shape as {@link #showSuccessDialog}, just the red/cross
     * variant, for the "this genuinely failed" case (a Save that didn't
     * persist, a Send that didn't go through, ...).
     *
     * @param owner   the window to center on and block (pass the calling
     *                screen's {@code this})
     * @param message the failure text, e.g. "Couldn't save this appointment."
     * @param onClose runs after the dialog is dismissed (may be {@code null})
     */
    public static void showErrorDialog(java.awt.Window owner, String message, Runnable onClose) {
        final int dialogWidth = 420;
        final int circleSize = 90;
        final int messageWidth = dialogWidth - 40;
        final int messageTop = 130;
        final int gapAfterMessage = 20;
        final int buttonHeight = 40;
        final int bottomMargin = 30;

        final javax.swing.JFrame dimTarget = (owner instanceof javax.swing.JFrame) ? (javax.swing.JFrame) owner : null;
        if (dimTarget != null) {
            setDimmed(dimTarget, true);
        }

        javax.swing.JDialog dialog = new javax.swing.JDialog(owner, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(Color.WHITE);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        roundCorners(card, 20);

        JLabel lblCross = new JLabel(cross(Color.WHITE, 34), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(198, 40, 40)); // this app's established error red (see EDIT_PROFILE_MISMATCH)
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblCross.setBounds((dialogWidth - circleSize) / 2, 28, circleSize, circleSize);
        card.add(lblCross);

        String htmlMessage = escapeHtml(message).replace("\n", "<br>");
        JLabel lblMessage = new JLabel(
                "<html><div style='text-align:center;'>" + htmlMessage + "</div></html>",
                SwingConstants.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMessage.setForeground(new Color(30, 30, 30));
        lblMessage.setVerticalAlignment(SwingConstants.TOP);

        int messageHeight = 50;
        Object htmlView = lblMessage.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
        if (htmlView instanceof javax.swing.text.View) {
            javax.swing.text.View view = (javax.swing.text.View) htmlView;
            view.setSize(messageWidth, 0);
            messageHeight = (int) Math.ceil(view.getPreferredSpan(javax.swing.text.View.Y_AXIS));
        }
        lblMessage.setBounds(20, messageTop, messageWidth, messageHeight);
        card.add(lblMessage);

        int buttonY = messageTop + messageHeight + gapAfterMessage;
        JButton btnClose = actionButton(null, new Color(231, 115, 36), null); // this app's established brand orange
        btnClose.setText("OK");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBounds((dialogWidth - 130) / 2, buttonY, 130, buttonHeight);
        btnClose.addActionListener(e -> {
            dialog.dispose();
            if (dimTarget != null) {
                setDimmed(dimTarget, false);
            }
            if (onClose != null) onClose.run();
        });
        card.add(btnClose);

        int dialogHeight = buttonY + buttonHeight + bottomMargin;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(owner);

        dialog.setContentPane(card);
        dialog.setVisible(true);
    }

    /**
     * Shows a custom-styled Yes/No confirmation popup — a rounded white card
     * with an amber warning badge, a message, and Cancel/{@code confirmLabel}
     * pill buttons — in place of the OS's default
     * {@link javax.swing.JOptionPane#showConfirmDialog}. Same "big icon +
     * message + button(s)" card shape as {@link #showSuccessDialog}/
     * {@link #showErrorDialog}, but with two buttons since the caller needs
     * to know which one was picked. Used for anything destructive/hard to
     * undo — deleting a row, rejecting a request — where a plain "OK" isn't
     * appropriate.
     *
     * @param owner       the window to center on and block (pass the calling screen's {@code this})
     * @param message     the confirmation text, e.g. "Delete patient: Oveen Fernando?"
     * @param confirmLabel text for the destructive button, e.g. "Delete" (defaults to "Confirm" if null/blank)
     * @param onConfirm   runs only if the destructive button was clicked — never for Cancel/X
     */
    public static void showConfirmDialog(java.awt.Window owner, String message, String confirmLabel, Runnable onConfirm) {
        final int dialogWidth = 420;
        final int circleSize = 90;
        final int messageWidth = dialogWidth - 40;
        final int messageTop = 130;
        final int gapAfterMessage = 20;
        final int buttonHeight = 40;
        final int bottomMargin = 30;

        final javax.swing.JFrame dimTarget = (owner instanceof javax.swing.JFrame) ? (javax.swing.JFrame) owner : null;
        if (dimTarget != null) {
            setDimmed(dimTarget, true);
        }

        javax.swing.JDialog dialog = new javax.swing.JDialog(owner, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(Color.WHITE);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        roundCorners(card, 20);

        final Color warnColor = new Color(255, 152, 0); // this app's established amber (see statusPill's "Pending")
        JLabel lblWarn = new JLabel("!", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(warnColor);
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblWarn.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblWarn.setForeground(Color.WHITE);
        lblWarn.setBounds((dialogWidth - circleSize) / 2, 28, circleSize, circleSize);
        card.add(lblWarn);

        String htmlMessage = escapeHtml(message).replace("\n", "<br>");
        JLabel lblMessage = new JLabel(
                "<html><div style='text-align:center;'>" + htmlMessage + "</div></html>",
                SwingConstants.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMessage.setForeground(new Color(30, 30, 30));
        lblMessage.setVerticalAlignment(SwingConstants.TOP);

        int messageHeight = 50;
        Object htmlView = lblMessage.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
        if (htmlView instanceof javax.swing.text.View) {
            javax.swing.text.View view = (javax.swing.text.View) htmlView;
            view.setSize(messageWidth, 0);
            messageHeight = (int) Math.ceil(view.getPreferredSpan(javax.swing.text.View.Y_AXIS));
        }
        lblMessage.setBounds(20, messageTop, messageWidth, messageHeight);
        card.add(lblMessage);

        int buttonY = messageTop + messageHeight + gapAfterMessage;
        int buttonWidth = 140;
        int gapBetweenButtons = 16;
        int buttonsStartX = (dialogWidth - (buttonWidth * 2 + gapBetweenButtons)) / 2;

        Runnable close = () -> {
            dialog.dispose();
            if (dimTarget != null) {
                setDimmed(dimTarget, false);
            }
        };

        JButton btnCancel = actionButton(null, new Color(220, 220, 220), null);
        btnCancel.setText("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setForeground(new Color(60, 60, 60));
        btnCancel.setBounds(buttonsStartX, buttonY, buttonWidth, buttonHeight);
        btnCancel.addActionListener(e -> close.run());
        card.add(btnCancel);

        JButton btnConfirm = actionButton(null, new Color(220, 53, 69), null); // this app's established danger red
        btnConfirm.setText(confirmLabel != null && !confirmLabel.trim().isEmpty() ? confirmLabel : "Confirm");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setBounds(buttonsStartX + buttonWidth + gapBetweenButtons, buttonY, buttonWidth, buttonHeight);
        btnConfirm.addActionListener(e -> {
            close.run();
            if (onConfirm != null) onConfirm.run();
        });
        card.add(btnConfirm);

        int dialogHeight = buttonY + buttonHeight + bottomMargin;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(owner);

        dialog.setContentPane(card);
        dialog.setVisible(true);
    }

    /**
     * Shows a custom-styled "confirm an email address" prompt — a rounded
     * white card with a blue envelope icon, a message, and an editable
     * field pre-filled with a best-guess address — in place of the OS's
     * default {@link javax.swing.JOptionPane} input dialog chrome. Used
     * anywhere a real email is about to be sent and office staff should see
     * (and be able to correct) exactly who it's going to first.
     *
     * @param owner      the window to center on and dim behind this popup
     * @param message    the confirmation text, e.g. "Send bill B104 (Oveen Fernando) to:"
     * @param prefill    starting value for the editable field (may be "")
     * @param onConfirm  runs with the (possibly edited) email once "Send" is
     *                   clicked with non-blank text; never called on Cancel/X
     */
    public static void showEmailConfirmDialog(java.awt.Window owner, String message, String prefill,
            java.util.function.Consumer<String> onConfirm) {
        final int dialogWidth = 440;
        final int circleSize = 64;
        final int fieldX = 30;
        final int fieldWidth = dialogWidth - 2 * fieldX;
        final int fieldHeight = 40;
        final int messageWidth = dialogWidth - 40;

        final javax.swing.JFrame dimTarget = (owner instanceof javax.swing.JFrame) ? (javax.swing.JFrame) owner : null;
        if (dimTarget != null) {
            setDimmed(dimTarget, true);
        }

        javax.swing.JDialog dialog = new javax.swing.JDialog(owner, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(Color.WHITE);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        roundCorners(card, 20);

        JButton btnX = new JButton(cross(new Color(140, 140, 140), 12));
        btnX.setOpaque(false);
        btnX.setContentAreaFilled(false);
        btnX.setBorderPainted(false);
        btnX.setFocusPainted(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.setBounds(dialogWidth - 46, 14, 28, 28);
        card.add(btnX);

        JLabel lblIcon = new JLabel(mail(Color.WHITE, 26), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 122, 255));
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcon.setBounds((dialogWidth - circleSize) / 2, 26, circleSize, circleSize);
        card.add(lblIcon);

        int y = 26 + circleSize + 18;

        String htmlMessage = escapeHtml(message).replace("\n", "<br>");
        JLabel lblMessage = new JLabel(
                "<html><div style='text-align:center;'>" + htmlMessage + "</div></html>", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMessage.setForeground(new Color(30, 30, 30));
        lblMessage.setVerticalAlignment(SwingConstants.TOP);

        int messageHeight = 40;
        Object htmlView = lblMessage.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
        if (htmlView instanceof javax.swing.text.View) {
            javax.swing.text.View view = (javax.swing.text.View) htmlView;
            view.setSize(messageWidth, 0);
            messageHeight = (int) Math.ceil(view.getPreferredSpan(javax.swing.text.View.Y_AXIS));
        }
        lblMessage.setBounds(20, y, messageWidth, messageHeight);
        card.add(lblMessage);
        y += messageHeight + 18;

        JTextField txtEmail = new JTextField(prefill != null ? prefill : "");
        styleField(txtEmail, fieldX, y, fieldWidth, fieldHeight);
        txtEmail.setForeground(new Color(30, 30, 30));
        card.add(txtEmail);
        y += fieldHeight + 24;

        int sendWidth = 160, sendHeight = 42;
        JButton btnSend = pillButton("Send", new Color(0, 122, 255));
        btnSend.setBounds((dialogWidth - sendWidth) / 2, y, sendWidth, sendHeight);
        Runnable closeDialog = () -> {
            dialog.dispose();
            if (dimTarget != null) {
                setDimmed(dimTarget, false);
            }
        };
        btnSend.addActionListener(e -> {
            String value = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
            if (value.isEmpty()) {
                return;
            }
            closeDialog.run();
            if (onConfirm != null) {
                onConfirm.accept(value);
            }
        });
        card.add(btnSend);
        y += sendHeight;

        btnX.addActionListener(e -> closeDialog.run());
        txtEmail.addActionListener(e -> btnSend.doClick());

        int dialogHeight = y + 26;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(owner);
        dialog.setContentPane(card);
        javax.swing.SwingUtilities.invokeLater(() -> {
            txtEmail.requestFocusInWindow();
            txtEmail.selectAll();
        });
        dialog.setVisible(true);
    }

    // =========================================================================
    // "Edit Profile" popup — shared by every dashboard's profile menu
    // =========================================================================

    // Package-visible so every Office Staff grid's scrollable Edit popup
    // (showScrollableFormDialog) matches this same palette exactly.
    static final Color EDIT_PROFILE_BG = new Color(253, 250, 240);
    static final Color EDIT_PROFILE_FIELD_BG = new Color(238, 238, 238);
    static final Color EDIT_PROFILE_MISMATCH = new Color(198, 40, 40);
    private static final Color EYE_ICON_COLOR = new Color(120, 120, 120);

    /**
     * Shows the shared "Edit Profile" popup every dashboard's profile menu
     * opens — a rounded card with an editable username field and a
     * change-password form (current / new / confirm, each with its own
     * show/hide eye toggle). This dialog only owns the UI and the local
     * validation (blank username, Create/Confirm match); actually persisting
     * either change is up to {@code onSave}.
     *
     * @param owner           the window to center on and dim behind this popup
     * @param currentUsername pre-fills the now-editable username field
     * @param onSave          runs with (newUsername, newPassword) once
     *                        validation passes — newUsername is never blank,
     *                        but may be unchanged from currentUsername;
     *                        newPassword may be "" if the password fields
     *                        were left blank. The dialog closes right after.
     */
    public static void showEditProfileDialog(java.awt.Window owner, String currentUsername, java.util.function.BiConsumer<String, String> onSave, boolean mandatory) {
        final int dialogWidth = 440;
        final int fieldX = 30;
        final int fieldWidth = dialogWidth - 2 * fieldX;
        final int fieldHeight = 38;

        final javax.swing.JFrame dimTarget = (owner instanceof javax.swing.JFrame) ? (javax.swing.JFrame) owner : null;
        if (dimTarget != null) {
            setDimmed(dimTarget, true);
        }

        javax.swing.JDialog dialog = new javax.swing.JDialog(owner, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(Color.WHITE);
        if (mandatory) {
            // Forced first-login password change — no window-manager close
            // gesture gets to bypass it either, on top of there being no X button.
            dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        }

        JPanel card = new JPanel(null);
        card.setBackground(EDIT_PROFILE_BG);
        roundCorners(card, 22);

        JButton btnClose = new JButton(cross(new Color(140, 140, 140), 12));
        btnClose.setOpaque(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setBounds(dialogWidth - 46, 14, 28, 28);
        if (!mandatory) {
            // The mandatory case has no way out except actually setting a
            // password — no X button to slip past it.
            card.add(btnClose);
        }

        JLabel lblTitle = new JLabel(mandatory ? "Set Your Password" : "Edit Profile", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(30, 30, 30));
        lblTitle.setBounds(0, 24, dialogWidth, 30);
        card.add(lblTitle);

        int y = 78;
        JLabel lblUsernameCaption = fieldCaption("Username:", fieldX, y);
        card.add(lblUsernameCaption);
        y += 20;
        JTextField txtUsername = new JTextField(currentUsername != null ? currentUsername : "");
        styleField(txtUsername, fieldX, y, fieldWidth, fieldHeight);
        card.add(txtUsername);
        y += fieldHeight + 20;

        JLabel lblCurrentCaption = fieldCaption("Current Password:", fieldX, y);
        card.add(lblCurrentCaption);
        y += 20;
        javax.swing.JPasswordField txtCurrentPassword = new javax.swing.JPasswordField();
        styleField(txtCurrentPassword, fieldX, y, fieldWidth, fieldHeight);
        attachPasswordToggle(card, txtCurrentPassword, fieldX, y, fieldWidth, fieldHeight);
        card.add(txtCurrentPassword);
        y += fieldHeight + 20;

        JLabel lblCreateCaption = fieldCaption("Create Password:", fieldX, y);
        card.add(lblCreateCaption);
        y += 20;
        javax.swing.JPasswordField txtCreatePassword = new javax.swing.JPasswordField();
        styleField(txtCreatePassword, fieldX, y, fieldWidth, fieldHeight);
        attachPasswordToggle(card, txtCreatePassword, fieldX, y, fieldWidth, fieldHeight);
        card.add(txtCreatePassword);
        y += fieldHeight + 20;

        JLabel lblConfirmCaption = fieldCaption("Confirm Password:", fieldX, y);
        card.add(lblConfirmCaption);
        y += 20;
        javax.swing.JPasswordField txtConfirmPassword = new javax.swing.JPasswordField();
        styleField(txtConfirmPassword, fieldX, y, fieldWidth, fieldHeight);
        attachPasswordToggle(card, txtConfirmPassword, fieldX, y, fieldWidth, fieldHeight);
        card.add(txtConfirmPassword);
        y += fieldHeight + 14;

        JLabel lblMismatch = new JLabel(" ", SwingConstants.CENTER);
        lblMismatch.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMismatch.setForeground(EDIT_PROFILE_MISMATCH);
        lblMismatch.setBounds(0, y, dialogWidth, 18);
        card.add(lblMismatch);
        y += 26;

        int saveWidth = 200, saveHeight = 44;
        JButton btnSave = pillButton("Save Details!", new Color(231, 115, 36));
        btnSave.setBounds((dialogWidth - saveWidth) / 2, y, saveWidth, saveHeight);
        btnSave.addActionListener(e -> {
            String newUsername = txtUsername.getText().trim();
            String create = new String(txtCreatePassword.getPassword());
            String confirm = new String(txtConfirmPassword.getPassword());
            if (newUsername.isEmpty()) {
                lblMismatch.setText("Username can't be blank!");
                return;
            }
            if (mandatory && create.isEmpty()) {
                lblMismatch.setText("You must set a new password to continue.");
                return;
            }
            if (!create.equals(confirm)) {
                lblMismatch.setText("Password Missmatch!!!!");
                return;
            }
            dialog.dispose();
            if (dimTarget != null) {
                setDimmed(dimTarget, false);
            }
            if (onSave != null) {
                onSave.accept(newUsername, create);
            }
        });
        card.add(btnSave);
        y += saveHeight;

        if (!mandatory) {
            Runnable closeWithoutSaving = () -> {
                dialog.dispose();
                if (dimTarget != null) {
                    setDimmed(dimTarget, false);
                }
            };
            btnClose.addActionListener(e -> closeWithoutSaving.run());
        }

        int dialogHeight = y + 28;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(owner);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }

    /**
     * A dentist added directly through Dentist Management (not self-
     * registered via Register.java) has a "dentists" row but no "users"
     * login yet — this collects one. Same visual family as
     * {@link #showEditProfileDialog}, minus the "Current Password" field
     * (there's no existing password to confirm — this dentist has never
     * logged in before).
     */
    public static void showCreateLoginDialog(java.awt.Window owner, String dentistName,
            java.util.function.Consumer<String> onSave) {
        final int dialogWidth = 440;
        final int fieldX = 30;
        final int fieldWidth = dialogWidth - 2 * fieldX;
        final int fieldHeight = 38;

        final javax.swing.JFrame dimTarget = (owner instanceof javax.swing.JFrame) ? (javax.swing.JFrame) owner : null;
        if (dimTarget != null) {
            setDimmed(dimTarget, true);
        }

        javax.swing.JDialog dialog = new javax.swing.JDialog(owner, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(Color.WHITE);

        JPanel card = new JPanel(null);
        card.setBackground(EDIT_PROFILE_BG);
        roundCorners(card, 22);

        JButton btnClose = new JButton(cross(new Color(140, 140, 140), 12));
        btnClose.setOpaque(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setBounds(dialogWidth - 46, 14, 28, 28);
        card.add(btnClose);

        JLabel lblTitle = new JLabel("Create Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(30, 30, 30));
        lblTitle.setBounds(0, 24, dialogWidth, 30);
        card.add(lblTitle);

        JLabel lblSubtitle = new JLabel(escapeHtml(dentistName != null ? dentistName : "this dentist"), SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(110, 110, 110));
        lblSubtitle.setBounds(0, 54, dialogWidth, 20);
        card.add(lblSubtitle);

        int y = 90;
        JLabel lblUsernameCaption = fieldCaption("Username:", fieldX, y);
        card.add(lblUsernameCaption);
        y += 20;
        JTextField txtUsername = new JTextField();
        styleField(txtUsername, fieldX, y, fieldWidth, fieldHeight);
        card.add(txtUsername);
        y += fieldHeight + 14;

        // No password field here on purpose — a random temporary password is
        // generated and emailed once this is saved (never typed by Office
        // Staff, never shown on screen); the dentist sets their own the
        // first time they log in.
        JLabel lblHint = new JLabel(
                "<html><div style='text-align:center;'>A temporary password will be emailed to this dentist automatically.</div></html>",
                SwingConstants.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(new Color(110, 110, 110));
        lblHint.setBounds(fieldX, y, fieldWidth, 34);
        card.add(lblHint);
        y += 34 + 6;

        JLabel lblMismatch = new JLabel(" ", SwingConstants.CENTER);
        lblMismatch.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMismatch.setForeground(EDIT_PROFILE_MISMATCH);
        lblMismatch.setBounds(0, y, dialogWidth, 18);
        card.add(lblMismatch);
        y += 26;

        int saveWidth = 200, saveHeight = 44;
        JButton btnSave = pillButton("Save Login", new Color(231, 115, 36));
        btnSave.setBounds((dialogWidth - saveWidth) / 2, y, saveWidth, saveHeight);
        btnSave.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            if (username.isEmpty()) {
                lblMismatch.setText("Username can't be blank!");
                return;
            }
            dialog.dispose();
            if (dimTarget != null) {
                setDimmed(dimTarget, false);
            }
            if (onSave != null) {
                onSave.accept(username);
            }
        });
        card.add(btnSave);
        y += saveHeight;

        Runnable closeWithoutSaving = () -> {
            dialog.dispose();
            if (dimTarget != null) {
                setDimmed(dimTarget, false);
            }
        };
        btnClose.addActionListener(e -> closeWithoutSaving.run());

        int dialogHeight = y + 28;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(owner);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }

    /** Fixed content width every scrollable Edit popup's fields are laid out against — see {@link #showScrollableFormDialog}. */
    static final int FORM_DIALOG_CONTENT_WIDTH = 460 - 2 * 30 - 16; // dialog width minus side margins minus scrollbar gutter

    /**
     * A generic, vertically-scrollable "Edit ___" popup shell — a rounded
     * white card with a title, X close button, a scrollable content area
     * (whatever the caller already built with {@link #addFormSectionHeader}/
     * {@link #addFormField}/{@link #addFormCheckboxRow}), and a single
     * orange "Save Details!" pill button pinned below the scroll area.
     *
     * Every Office Staff grid's "Edit" action uses this same shell so none
     * of them have to rebuild the card/scroll/button chrome by hand — they
     * just build one {@link JPanel} of fields and hand it here.
     *
     * @param owner         window to center on and dim behind
     * @param title         dialog title, e.g. "Edit Patient"
     * @param contentPanel  a {@code JPanel(null)} the caller already filled
     *                      with its own field rows (via the add* helpers below)
     * @param contentHeight the y-cursor {@code addForm*} left off at — i.e.
     *                      contentPanel's real full height
     * @param onSave        runs when "Save Details!" is clicked; return
     *                      {@code null} to close the dialog, or an error
     *                      message to show inline and keep it open
     */
    public static void showScrollableFormDialog(java.awt.Window owner, String title, JPanel contentPanel,
            int contentHeight, java.util.function.Supplier<String> onSave) {
        final int dialogWidth = 460;
        final int contentTop = 66;
        final int maxScrollHeight = 420;
        final int scrollHeight = Math.min(Math.max(contentHeight, 40), maxScrollHeight);
        final int saveWidth = 200, saveHeight = 44;

        final javax.swing.JFrame dimTarget = (owner instanceof javax.swing.JFrame) ? (javax.swing.JFrame) owner : null;
        if (dimTarget != null) {
            setDimmed(dimTarget, true);
        }

        javax.swing.JDialog dialog = new javax.swing.JDialog(owner, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(Color.WHITE);

        JPanel card = new JPanel(null);
        card.setBackground(EDIT_PROFILE_BG);
        roundCorners(card, 22);

        JButton btnClose = new JButton(cross(new Color(140, 140, 140), 12));
        btnClose.setOpaque(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setBounds(dialogWidth - 46, 14, 28, 28);
        card.add(btnClose);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(30, 30, 30));
        lblTitle.setBounds(0, 20, dialogWidth, 30);
        card.add(lblTitle);

        contentPanel.setBackground(EDIT_PROFILE_BG);
        contentPanel.setPreferredSize(new Dimension(dialogWidth - 20, Math.max(contentHeight, scrollHeight)));

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(contentPanel,
                javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(EDIT_PROFILE_BG);
        scrollPane.setBounds(10, contentTop, dialogWidth - 20, scrollHeight);
        styleScrollBar(scrollPane);
        card.add(scrollPane);

        JLabel lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblError.setForeground(EDIT_PROFILE_MISMATCH);
        int errorY = contentTop + scrollHeight + 8;
        lblError.setBounds(0, errorY, dialogWidth, 18);
        card.add(lblError);

        int saveY = errorY + 24;
        JButton btnSave = pillButton("Save Details!", new Color(231, 115, 36));
        btnSave.setBounds((dialogWidth - saveWidth) / 2, saveY, saveWidth, saveHeight);
        btnSave.addActionListener(e -> {
            String error = onSave != null ? onSave.get() : null;
            if (error == null) {
                dialog.dispose();
                if (dimTarget != null) {
                    setDimmed(dimTarget, false);
                }
            } else {
                lblError.setText(error);
            }
        });
        card.add(btnSave);

        Runnable closeWithoutSaving = () -> {
            dialog.dispose();
            if (dimTarget != null) {
                setDimmed(dimTarget, false);
            }
        };
        btnClose.addActionListener(e -> closeWithoutSaving.run());

        int dialogHeight = saveY + saveHeight + 26;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(owner);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }

    /** Adds a bold orange section header (e.g. "Personal Information") into a {@code JPanel(null)} form, returning the y-cursor for the next row. */
    public static int addFormSectionHeader(JPanel panel, String text, int x, int y, int width) {
        JLabel header = new JLabel(text);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setForeground(new Color(231, 115, 36));
        header.setBounds(x, y, width, 22);
        panel.add(header);
        return y + 22 + 8;
    }

    /** Adds a labeled text field row into a {@code JPanel(null)} form, returning the y-cursor for the next row. {@code field} must already exist (caller keeps the reference to read it back on Save). */
    public static int addFormField(JPanel panel, String label, JTextField field, int x, int y, int width) {
        final int fieldHeight = 34;
        JLabel cap = fieldCaption(label, x, y);
        panel.add(cap);
        y += 19;
        styleField(field, x, y, width, fieldHeight);
        panel.add(field);
        return y + fieldHeight + 14;
    }

    /**
     * Adds a row of same-choice checkboxes (e.g. "Title:" Mr / Mrs / Dr) into
     * a {@code JPanel(null)} form — same non-exclusive-at-the-widget-level
     * convention the wizards themselves already use (only one is expected
     * checked; the caller resolves "which one" the same way those wizards
     * do, first-checked-wins). Returns the y-cursor for the next row.
     */
    public static int addFormCheckboxRow(JPanel panel, String label, String[] optionLabels, javax.swing.JCheckBox[] outBoxes,
            String selected, int x, int y, int width) {
        JLabel cap = fieldCaption(label, x, y);
        panel.add(cap);
        int boxX = x + 110;
        int boxWidth = Math.max(55, (width - 110) / Math.max(1, optionLabels.length));
        for (int i = 0; i < optionLabels.length; i++) {
            javax.swing.JCheckBox box = new javax.swing.JCheckBox(optionLabels[i]);
            box.setOpaque(false);
            box.setFont(box.getFont().deriveFont(13f));
            box.setSelected(selected != null && optionLabels[i].equalsIgnoreCase(selected.trim()));
            box.setBounds(boxX, y - 2, boxWidth, 22);
            panel.add(box);
            outBoxes[i] = box;
            boxX += boxWidth;
        }
        return y + 30;
    }

    /**
     * Same as {@link #addFormCheckboxRow(JPanel, String, String[], javax.swing.JCheckBox[], String, int, int, int)}
     * but for independent multi-select booleans (any combination can be
     * checked, unlike the first-checked-wins Title/Gender/Oral Hygiene
     * rows). Returns the y-cursor for the next row.
     */
    public static int addFormCheckboxRow(JPanel panel, String label, String[] optionLabels, javax.swing.JCheckBox[] outBoxes,
            boolean[] selectedFlags, int x, int y, int width) {
        JLabel cap = fieldCaption(label, x, y);
        panel.add(cap);
        int boxX = x + 110;
        int boxWidth = Math.max(55, (width - 110) / Math.max(1, optionLabels.length));
        for (int i = 0; i < optionLabels.length; i++) {
            javax.swing.JCheckBox box = new javax.swing.JCheckBox(optionLabels[i]);
            box.setOpaque(false);
            box.setFont(box.getFont().deriveFont(13f));
            box.setSelected(selectedFlags != null && i < selectedFlags.length && selectedFlags[i]);
            box.setBounds(boxX, y - 2, boxWidth, 22);
            panel.add(box);
            outBoxes[i] = box;
            boxX += boxWidth;
        }
        return y + 30;
    }

    // Package-visible (not private) — reused directly by every Office Staff
    // grid's scrollable Edit popup (see showScrollableFormDialog/addFormField
    // below) so they match this exact field style without re-deriving it.
    static JLabel fieldCaption(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(60, 60, 60));
        lbl.setBounds(x, y, 300, 18);
        return lbl;
    }

    static void styleField(JTextField field, int x, int y, int w, int h) {
        field.setBounds(x, y, w, h);
        field.setBackground(EDIT_PROFILE_FIELD_BG);
        // Right padding is generous (leftover from password fields docking an
        // eye-icon toggle there — see attachPasswordToggle) but harmless for
        // a plain field; kept as-is so existing password-field callers don't regress.
        field.setBorder(new EmptyBorder(0, 12, 0, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    /**
     * Docks an eye/eye-off toggle on the right edge of a password field —
     * same idea as {@link #dockIconInField}, but built by hand here because
     * this toggle also has to swap its own icon (show vs. hide) and flip the
     * field's echo character, not just fire a one-shot callback.
     */
    private static void attachPasswordToggle(JPanel parent, javax.swing.JPasswordField field, int fieldX, int fieldY, int fieldW, int fieldH) {
        JLabel icon = new JLabel(eye(EYE_ICON_COLOR, 16), SwingConstants.CENTER);
        icon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        icon.setBounds(fieldX + fieldW - 34, fieldY, 28, fieldH);
        field.setEchoChar('•');
        icon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                boolean masked = field.getEchoChar() != 0;
                if (masked) {
                    field.setEchoChar((char) 0);
                    icon.setIcon(eyeOff(EYE_ICON_COLOR, 16));
                } else {
                    field.setEchoChar('•');
                    icon.setIcon(eye(EYE_ICON_COLOR, 16));
                }
            }
        });
        parent.add(icon);
        parent.setComponentZOrder(icon, 0);
    }

    /** A fully-rounded pill button (corner radius = half its height), matching this app's brand-orange CTA look. */
    static JButton pillButton(String text, Color bgColor) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                boolean pressed = getModel().isPressed();
                boolean hover = getModel().isRollover();
                Color fill = pressed ? bgColor.darker() : (hover ? brighten(bgColor, 18) : bgColor);
                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, h, h));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Escapes text dropped into a JLabel's HTML body, so a message
     *  containing {@code &}, {@code <}, or {@code >} (e.g. a name) can't
     *  break the markup or be misread as a tag. */
    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // =========================================================================
    // Flat scrollbar (no arrow buttons, thin rounded thumb) for scrollable
    // dialogs like the Help Desk guide.
    // =========================================================================

    /**
     * Replaces a {@link JScrollPane}'s vertical scrollbar with a thin, flat,
     * rounded-thumb style — no up/down arrow buttons, no beveled Windows/
     * Metal look-and-feel chrome — matching the rest of this app's flat
     * design instead of the OS default. Also hides the horizontal scrollbar,
     * since every scroll pane in this app scrolls vertically only.
     */
    public static void styleScrollBar(JScrollPane scrollPane) {
        javax.swing.JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setUI(new FlatScrollBarUI());
        vBar.setPreferredSize(new Dimension(10, 0));
        vBar.setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private static final class FlatScrollBarUI extends BasicScrollBarUI {
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return zeroSizeButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return zeroSizeButton();
        }

        private JButton zeroSizeButton() {
            JButton b = new JButton();
            Dimension zero = new Dimension(0, 0);
            b.setPreferredSize(zero);
            b.setMinimumSize(zero);
            b.setMaximumSize(zero);
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(246, 246, 246));
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !c.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int pad = 3;
            Color fill = isThumbRollover() ? new Color(180, 180, 180) : new Color(206, 206, 206);
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(
                    thumbBounds.x + pad, thumbBounds.y,
                    thumbBounds.width - pad * 2, thumbBounds.height,
                    8, 8));
            g2.dispose();
        }
    }

    // =========================================================================
    // Placeholder ("hint") text for plain JTextFields
    // =========================================================================

    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 150);
    private static final String PLACEHOLDER_PROP = "iconFactory.placeholderActive";

    /**
     * Installs simple gray hint text (e.g. "Search here...") on a plain
     * {@link JTextField} — shown whenever the field is empty and unfocused,
     * cleared automatically as soon as the user starts typing.
     *
     * This works directly on the field's real text/foreground (no custom
     * component or subclass required), which is deliberate: fields declared
     * inside a NetBeans GEN-BEGIN/GEN-END block must stay plain
     * {@code javax.swing.JTextField} or the GUI Builder will overwrite them
     * the next time the paired .form file is regenerated. Call this from
     * outside the GEN block (e.g. after initComponents()) instead.
     *
     * Code that reads the field for real input (e.g. live-search filtering)
     * must call {@link #isPlaceholderShowing(JTextField)} first and treat a
     * "showing" field as empty, so the hint itself is never read as a query.
     */
    public static void installPlaceholder(JTextField field, String placeholder) {
        Color typedColor = field.getForeground();
        if (field.getText().isEmpty()) {
            showPlaceholder(field, placeholder);
        }
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderShowing(field)) {
                    field.setText("");
                    field.setForeground(typedColor);
                    field.putClientProperty(PLACEHOLDER_PROP, Boolean.FALSE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    showPlaceholder(field, placeholder);
                }
            }
        });
    }

    private static void showPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(PLACEHOLDER_COLOR);
        field.putClientProperty(PLACEHOLDER_PROP, Boolean.TRUE);
    }

    /** True if the field is currently showing its placeholder hint rather than real user input. */
    public static boolean isPlaceholderShowing(JTextField field) {
        return Boolean.TRUE.equals(field.getClientProperty(PLACEHOLDER_PROP));
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static Color brighten(Color c, int amt) {
        return new Color(clamp(c.getRed() + amt), clamp(c.getGreen() + amt), clamp(c.getBlue() + amt));
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    /** Blend a color toward white by the given fraction (0 = original, 1 = white). */
    private static Color tint(Color c, float fraction) {
        int r = (int) (c.getRed() + (255 - c.getRed()) * fraction);
        int g = (int) (c.getGreen() + (255 - c.getGreen()) * fraction);
        int b = (int) (c.getBlue() + (255 - c.getBlue()) * fraction);
        return new Color(clamp(r), clamp(g), clamp(b));
    }

    @FunctionalInterface
    private interface Painter {
        void paint(Graphics2D g2, int w, int h);
    }

    /** Simple resolution-independent {@link Icon} backed by a {@link Painter}. */
    private static final class VectorIcon implements Icon {
        private final int w, h;
        private final Color color;
        private final Painter painter;

        VectorIcon(int w, int h, Color color, Painter painter) {
            this.w = w;
            this.h = h;
            this.color = color;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(color);
            painter.paint(g2, w, h);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return w;
        }

        @Override
        public int getIconHeight() {
            return h;
        }
    }
}
