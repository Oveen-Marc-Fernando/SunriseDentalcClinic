package view;

import controller.AppController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Timer;

/**
 * Public_Dashboard View — Home screen for unauthenticated visitors.
 *
 * A single scrolling page, like a real marketing website: the black nav bar
 * stays fixed at the top (outside the scroll area). Home and About Us each
 * smooth-scroll the page down to their own section; Services opens a
 * dropdown of every service by name — picking one smooth-scrolls straight
 * to that exact service's card, not just the top of the section — same as
 * a website's "jump to anchor" nav menu.
 *
 * The nav bar itself is still declared in Public_Dashboard.form (NetBeans
 * GUI Builder compatible); everything below it — hero, services grid, about
 * section, footer — is built by hand in {@link #buildScrollableContent()}
 * since it's generated from data (the service list), not a fixed layout a
 * form file can describe.
 *
 * @author oveen
 */
public class Public_Dashboard extends javax.swing.JFrame {

    private static final String[][] SERVICES = {
        {"Aligners", "Straighten your smile discreetly with clear, custom-fit aligners — no metal, no hassle, just confident results."},
        {"Teeth Whitening", "Turn back the stains in one visit — professional-grade whitening for a brighter, more radiant smile."},
        {"Cleaning & Polishing", "Deep-clean scaling and polishing that leaves your teeth smooth, fresh, and noticeably brighter."},
        {"Dental Implants", "A permanent, natural-feeling replacement for missing teeth — built to look, feel, and function like your own."},
        {"Smile Design", "Your dream smile, engineered — a tailored blend of treatments designed around your unique features."},
        {"Root Canal Treatment", "Pain-free, precision root canal care that saves your natural tooth and gets you smiling again."},
        {"Fillings", "Seamless, tooth-colored fillings that repair damage invisibly — strong, durable, and virtually undetectable."},
        {"Dental Bridges", "Close the gap with a bridge that restores your bite, your smile, and your confidence in one solution."},
        {"Crowns", "Custom-crafted crowns that rebuild strength and beauty into a damaged tooth, built to last for years."},
        {"Dentures", "Comfortable, natural-looking dentures crafted to fit perfectly and restore your smile with confidence."},
    };

    /**
     * Longer "What Are X?" intro paragraph shown in each service's detail
     * popup (see {@link #showServiceDetailDialog}) — one entry per
     * {@link #SERVICES} index, in the same order.
     */
    private static final String[] SERVICE_INTRO = {
        "Aligners are clear, removable trays that gradually shift your teeth into place — comfortable, "
        + "nearly invisible, and easy to manage. At Sunrise Dental Clinic, your entire treatment is planned "
        + "and monitored by our own experienced dentists, giving you greater control, accuracy, and confidence "
        + "throughout your smile journey.",

        "Teeth whitening lifts years of stains from coffee, tea, and everyday life in a single professional-grade "
        + "session — far faster and more even than any over-the-counter kit. Our dentists tailor the strength of "
        + "each treatment to your teeth's sensitivity, so you get a brighter smile without the guesswork.",

        "Routine scaling and polishing removes the plaque and tartar buildup a toothbrush alone can't reach, "
        + "leaving your teeth smooth, fresh, and visibly brighter. It's the simplest, most affordable way to "
        + "protect your gums and catch small problems before they become big ones.",

        "Dental implants replace a missing tooth root with a titanium post that fuses naturally with your "
        + "jawbone, topped with a crown built to match your smile. Unlike a bridge or denture, an implant "
        + "stands on its own — no healthy neighboring teeth need to be touched.",

        "Smile Design blends whichever treatments your smile actually needs — whitening, veneers, alignment, "
        + "or contouring — into one tailored plan built around your face, your goals, and your budget. Instead "
        + "of a single procedure, you get a complete, engineered result.",

        "A root canal removes infected or inflamed tissue from deep inside a tooth, then seals it to stop the "
        + "pain and save the tooth itself — the alternative is usually extraction. Modern technique and "
        + "anesthesia make the procedure far more comfortable than its reputation suggests.",

        "Tooth-colored fillings repair the damage from a cavity while blending invisibly into your smile — no "
        + "dark metal fillings, no obvious patchwork. The affected area is cleaned out and rebuilt in a single "
        + "visit, stopping decay before it spreads further.",

        "A dental bridge closes the gap left by one or more missing teeth by anchoring a replacement tooth to "
        + "the healthy teeth on either side. It restores your bite, stops neighboring teeth from shifting, and "
        + "gives you back a complete, confident smile.",

        "A crown is a custom-crafted cap that rebuilds a damaged, weakened, or heavily decayed tooth from the "
        + "outside in — restoring its strength, shape, and appearance all at once. It's often the difference "
        + "between saving a tooth and losing it.",

        "Dentures replace some or all of your missing teeth with a comfortable, natural-looking, removable "
        + "appliance — custom-fitted to your mouth for a secure fit and a smile that looks like your own. A "
        + "practical, proven solution for extensive tooth loss."
    };

    /**
     * "Advantages of X" bullet list per service, parallel to
     * {@link #SERVICE_INTRO}. Each bullet is encoded as {@code "Term::description"}
     * — {@link #showServiceDetailDialog} splits on "::" to bold just the
     * lead term, the way the reference layout does.
     */
    private static final String[][] SERVICE_ADVANTAGES = {
        { // Aligners
            "Invisible Appearance::Clear aligners are hardly noticeable, offering a discreet way to straighten your teeth.",
            "Comfortable Fit::Made of smooth plastic, aligners don't irritate your gums or cheeks the way metal brackets can.",
            "Removable Design::Take them out to eat, brush, or floss — daily routines stay simple and hygienic.",
            "Fewer Clinic Visits::Treatment plans typically need fewer follow-up appointments, saving you time.",
            "Better Oral Hygiene::Since aligners come out, keeping your mouth clean is far easier than with fixed braces."
        },
        { // Teeth Whitening
            "Visible Results Fast::Noticeably brighter teeth in as little as one visit.",
            "Even, Natural Shade::Professional application whitens uniformly, avoiding the blotchy patches home kits can leave.",
            "Sensitivity-Managed::Strength is adjusted to your teeth, reducing the discomfort store-bought trays cause.",
            "Long-Lasting::Results hold much longer than drugstore strips with basic aftercare.",
            "Dentist-Supervised::Any underlying issues are checked before treatment begins."
        },
        { // Cleaning & Polishing
            "Prevents Gum Disease::Removing tartar at the gumline is one of the biggest defenses against gingivitis.",
            "Fresher Breath::Clears out the bacteria buildup that causes persistent bad breath.",
            "Brighter Smile::Polishing lifts surface stains that brushing alone leaves behind.",
            "Early Problem Detection::Your dentist spots cavities or issues while they're still small and easy to treat.",
            "Quick & Painless::A routine visit, usually done in well under an hour."
        },
        { // Dental Implants
            "Look & Feel Natural::Built and shaded to blend seamlessly with your own teeth.",
            "Built to Last::With proper care, implants can last a lifetime — far longer than bridges or dentures.",
            "Protects Jawbone::Implants stimulate the jaw the way a natural root does, preventing bone loss.",
            "No Impact on Other Teeth::Neighboring healthy teeth are never filed down or altered.",
            "Restores Full Function::Bite and chew with the same confidence as your natural teeth."
        },
        { // Smile Design
            "Fully Personalized::Every plan is built around your unique teeth, gums, and facial features.",
            "One Coordinated Plan::Multiple treatments are sequenced together instead of booked one at a time.",
            "Preview Before You Commit::See a projected outcome before any treatment begins.",
            "Balanced, Natural Results::Designed for harmony with your face, not just isolated tooth-by-tooth fixes.",
            "Long-Term Confidence::A smile built to hold up, not just look good on day one."
        },
        { // Root Canal Treatment
            "Saves Your Natural Tooth::Keeps you from losing a tooth that could otherwise be saved.",
            "Stops the Pain::Removes the source of infection, not just the symptom.",
            "Prevents Spread of Infection::Stops the problem from reaching neighboring teeth or the jawbone.",
            "Restores Normal Biting::A treated tooth, once capped, chews and functions normally again.",
            "Cost-Effective Long-Term::Usually far less costly than extraction plus a bridge or implant later."
        },
        { // Fillings
            "Virtually Invisible::Matched to your natural tooth shade, unlike traditional metal fillings.",
            "Bonds Directly to the Tooth::Creates a stronger structural seal than older filling materials.",
            "Mercury-Free::No amalgam, a preference many patients care about.",
            "Preserves More Tooth Structure::Requires removing less healthy tooth than older filling techniques.",
            "Single-Visit Fix::Most fillings are completed start to finish in one appointment."
        },
        { // Dental Bridges
            "Restores a Complete Smile::Fills visible gaps left by missing teeth.",
            "Prevents Teeth From Shifting::Keeps neighboring teeth from drifting into the empty space over time.",
            "Improves Chewing & Speech::Restores function that a gap can quietly interfere with.",
            "Fixed in Place::Unlike a removable denture, a bridge stays anchored — no daily removal needed.",
            "Faster Than Implants::Typically completed in fewer visits than an implant-based solution."
        },
        { // Crowns
            "Restores Strength::Protects a weakened tooth from further cracking or breaking.",
            "Natural Appearance::Shaped and shaded to match the rest of your smile.",
            "Long-Lasting Protection::Built from durable materials designed to hold up for years.",
            "Versatile Fix::Used after large fillings, root canals, or to cap a dental implant.",
            "Protects Against Further Damage::Shields a vulnerable tooth from everyday wear."
        },
        { // Dentures
            "Restores Your Smile::Fills out your appearance and supports your facial structure.",
            "Improves Eating & Speech::Makes both far easier than living with missing teeth.",
            "Custom Comfortable Fit::Molded to your mouth for a secure, natural feel.",
            "More Affordable::Generally a lower-cost option than a full set of implants.",
            "Removable & Easy to Clean::Simple daily care compared to fixed restorations."
        }
    };

    private static final int CONTENT_WIDTH  = 920;
    private static final int CARD_WIDTH     = 410;
    private static final int CARD_LEFT_X    = 30;
    private static final int CARD_RIGHT_X   = 480;
    private static final int CARD_ROW_START = 705; // room for the HomePagePanel hero content, see buildHeroSection
    private static final Color ACCENT = new Color(231, 115, 36);
    private static final Color BEFORE_BG = new Color(233, 233, 233);
    private static final Color BEFORE_ICON = new Color(150, 150, 150);
    private static final Color AFTER_BG = new Color(255, 246, 237);
    private static final int FOOTER_HEIGHT = 200;
    private static final int SEE_MORE_BUTTON_HEIGHT = 32;
    private static final int FIND_US_CARD_HEIGHT = 60;

    // Static, not instance state — once a visitor Declines/Accepts/closes the
    // cookie banner (any of the three counts as "decided"), it should never
    // come back for the rest of this run, even if a new Public_Dashboard
    // instance is created later (e.g. logging out back to the public page).
    private static boolean cookieConsentDecided = false;

    // Smooth-scroll targets — the y-coordinate (within the scrollable content
    // panel, same units as the vertical scrollbar's value) each nav item
    // scrolls to. A little above each section's own heading so it doesn't
    // land flush against the top edge of the viewport.
    private static final int SCROLL_HOME  = 0;
    private static final int SCROLL_SERVICES = 600; // moved down to make room for the tagline + taller 3 teal highlight boxes under the hero banner

    // Everything below is computed once by computeLayout(), before the page
    // is built, instead of being hardcoded — a card's height (and therefore
    // every section's position below it) depends on how tall the *longest*
    // service description actually renders at this font/width, which isn't
    // knowable as a compile-time constant. Guessing that height wrong is
    // exactly what caused descriptions to overflow their own card box.
    private int cardHeight;
    private int cardRowStep;
    private int aboutHeadingY;
    private int scrollAbout;
    private int aboutRowY;
    private int aboutRowHeight;
    private int findUsCardY;
    private int highlightsRowY;
    private int footerY;
    private int contentHeight;
    private static final int HIGHLIGHT_BOX_HEIGHT = 70;
    private static final int ABOUT_TEXT_CARD_PAD = 20; // inner padding between the paragraph's bordered card and the text itself

    // Shared contact details — used by both the About Us "Find Us" card and the footer's Contact Us column.
    private static final String CLINIC_ADDRESS = "No 15/35, Galle Road, Colombo";
    private static final String CLINIC_PHONE   = "011-2225233";
    private static final String CLINIC_EMAIL   = "sunrisedentalcolombo@gmail.com";

    // About Us section — shared between computeLayout() (which needs to measure the paragraph's
    // real height up front, so the Get Directions card sits right after it instead of after the
    // taller photo) and buildAboutSection() (which actually renders it).
    private static final String ABOUT_TEXT = "Sunrise Dental Clinic has been providing world-class, patient-centered dental "
            + "care in the heart of Colombo since 2026. Our team of experienced, highly qualified dentists "
            + "combines the latest dental technology with a warm, personal touch to make every visit "
            + "comfortable — from routine check-ups and preventive care to advanced cosmetic, restorative, "
            + "and emergency treatments. We believe a healthy, confident smile is the foundation of "
            + "overall wellbeing, and we're committed to walking with you at every stage of your dental "
            + "journey. Every treatment plan is tailored to your individual needs, and our modern, "
            + "welcoming clinic is designed to put even the most anxious patients at ease.";
    // Right margin matches the service card grid's own (CARD_RIGHT_X + CARD_WIDTH), so
    // this row spans out to the same right edge as the rest of the page. The left edge
    // (the photo's own position) is its own independent number, not tied to the card
    // grid's CARD_LEFT_X — moving it doesn't move anything else.
    private static final int ABOUT_IMAGE_X = 10;
    private static final int ABOUT_ROW_RIGHT_X = CARD_RIGHT_X + CARD_WIDTH;
    private static final int ABOUT_IMAGE_W = 460;
    // The paragraph's own left edge — a fixed number, not computed from the photo's
    // width/position, so moving one doesn't move the other. The photo's own right
    // edge is at 530 (ABOUT_IMAGE_X + ABOUT_IMAGE_W); this leaves a 20px gap past
    // that so growing the photo doesn't run into the paragraph — bump this number
    // if ABOUT_IMAGE_W grows again.
    private static final int ABOUT_TEXT_X = 475;
    private static final Font ABOUT_TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // The service-detail popup's Close button often sits, on screen, right
    // on top of a *different* card further down the scrolled content. The
    // same mouse-up that dismisses the modal dialog can "fall through" to
    // whatever is now at that screen position and register as a fresh click
    // there — reopening a new popup for that other card, which then repeats
    // when *that* one is closed. Ignoring any card click within this window
    // of a dialog closing swallows that fall-through click.
    private long lastDetailDialogCloseMillis = 0;
    private static final int DETAIL_DIALOG_CLICK_GUARD_MS = 400;

    /** Hard re-entrancy guard: a second detail popup can never open while one is already showing, full stop — independent of the timing-based guard above. */
    private boolean detailDialogShowing = false;

    public Public_Dashboard() {
        initComponents();
        lblUserIcon.setIcon(IconFactory.userGlyph(java.awt.Color.WHITE, 26)); // crisp vector glyph, no backdrop — sits directly on the black pill
        lblLogo.setIcon(IconFactory.brandLogo(130, 40)); // crisp vector wordmark (fixes blurry 130x40 raster logo at HiDPI)
        IconFactory.roundCorners(navBar, 28); // fully rounded pill — radius = half the bar's height
        computeLayout(); // must run before buildScrollableContent() — every section below the services grid is positioned off its result
        buildScrollableContent();
        setSize(1016, 739);
        setLocationRelativeTo(null);
        setupEventListeners();

        // Wait until the window has actually been shown, then hold another 2s
        // before the banner appears — so visitors see the real dashboard load
        // first, exactly like a real website's cookie banner easing in after
        // the page itself has rendered, instead of popping up instantly.
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                if (cookieConsentDecided) {
                    return; // already Declined/Accepted/closed earlier this run — never ask again
                }
                Timer cookieDelay = new Timer(2000, ev -> showCookieConsentBanner());
                cookieDelay.setRepeats(false);
                cookieDelay.start();
            }
        });
    }

    /**
     * A cookie-consent card floating above the whole page, anchored near the
     * bottom of the window — same idea as any real website's cookie banner.
     * Added straight to the frame's layered pane (not the scrollable content
     * panel), so it stays fixed in place regardless of scroll position and
     * always paints above everything else on the page.
     */
    private void showCookieConsentBanner() {
        final int cardW = 460, cardH = 264, pad = 24;
        final int contentW = 1000, contentH = 700; // mainPanel's own fixed size — see initComponents()
        final int x = (contentW - cardW) / 2;
        final int y = contentH - cardH - 24;

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // Soft "lifted card" shadow — same technique as IconFactory.actionButton.
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new RoundRectangle2D.Float(2, 4, w - 4, h - 6, 22, 22));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 3, h - 6, 22, 22));
                g2.setColor(new Color(230, 230, 235));
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 4, h - 7, 22, 22));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBounds(x, y, cardW, cardH);

        JPanel badge = new JPanel(null);
        badge.setBackground(new Color(255, 239, 222));
        IconFactory.roundCorners(badge, 16);
        badge.setBounds(pad, pad, 56, 56);
        JLabel iconLbl = new JLabel(IconFactory.cookie(ACCENT, 30), javax.swing.SwingConstants.CENTER);
        iconLbl.setBounds(0, 0, 56, 56);
        badge.add(iconLbl);
        card.add(badge);

        JButton btnX = new JButton(IconFactory.cross(new Color(150, 150, 155), 11));
        btnX.setOpaque(false);
        btnX.setContentAreaFilled(false);
        btnX.setBorderPainted(false);
        btnX.setFocusPainted(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.setBounds(cardW - 40, 16, 24, 24);
        card.add(btnX);

        JLabel heading = new JLabel("We Value Your Privacy");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 19));
        heading.setForeground(new Color(28, 28, 36));
        heading.setBounds(pad, pad + 56 + 16, cardW - 2 * pad, 26);
        card.add(heading);

        String desc = "We use cookies to enhance your experience, serve personalised content, and analyse traffic.";
        addWrappedText(card, desc, new Font("Segoe UI", Font.PLAIN, 13), new Color(95, 95, 102),
                cardW - 2 * pad, pad, pad + 56 + 16 + 30);

        // Equal-width buttons, split evenly across the usable card width
        // (rather than Decline being narrower than Accept All).
        final int btnGap = 16;
        final int btnW = (cardW - 2 * pad - btnGap) / 2;
        final int btnY = cardH - pad - 44;

        JButton btnDecline = IconFactory.pillButton("Decline", new Color(238, 240, 244), new Color(60, 65, 75));
        btnDecline.setBounds(pad, btnY, btnW, 44);
        card.add(btnDecline);

        JButton btnAccept = IconFactory.pillButton("Accept All", ACCENT, Color.WHITE);
        btnAccept.setBounds(pad + btnW + btnGap, btnY, btnW, 44);
        card.add(btnAccept);

        // Deferred to the next event-dispatch turn, same reasoning as
        // showServiceDetailDialog's invokeLater(dialog::dispose): mutating
        // the layered pane synchronously inside whatever's currently on the
        // EDT (a Timer firing, or another component's own paint cycle) is
        // what left stale pixels behind — a fresh EDT turn gives Swing's
        // dirty-region tracking a clean slate to repaint from.
        javax.swing.SwingUtilities.invokeLater(() -> {
            getLayeredPane().add(card, javax.swing.JLayeredPane.POPUP_LAYER);
            getLayeredPane().validate();
            getContentPane().repaint();
            Toolkit.getDefaultToolkit().sync();
        });

        // The header X isn't an actual decision — it just dismisses the
        // banner for now, so it comes back after another 2s, same as if it
        // had never been shown. Decline / Accept All are real decisions:
        // either one sets cookieConsentDecided so the banner never returns
        // again this run.
        btnX.addActionListener(e -> {
            removeCookieCard(card);
            Timer reshow = new Timer(2000, ev -> {
                if (!cookieConsentDecided) {
                    showCookieConsentBanner();
                }
            });
            reshow.setRepeats(false);
            reshow.start();
        });
        btnDecline.addActionListener(e -> {
            cookieConsentDecided = true;
            removeCookieCard(card);
        });
        btnAccept.addActionListener(e -> {
            cookieConsentDecided = true;
            // Fire-and-forget on a background thread — recording the
            // acceptance must never make the banner feel slow to dismiss,
            // and a slow/unreachable database must never block the click.
            new Thread(() -> controller.CookieConsentController.recordAcceptance(
                    util.DeviceIdentity.getDeviceId(),
                    util.DeviceIdentity.getLocalIpAddress(),
                    util.DeviceIdentity.getUserAgentLike()), "cookie-consent-log").start();
            removeCookieCard(card);
        });
    }

    /**
     * Removing the banner with a plain {@code remove()} + {@code repaint()}
     * left a stale, ghosted composite of the frame's earlier content briefly
     * visible around where the (translucent, non-opaque) card used to sit —
     * a known Swing double-buffering quirk where a partial repaint after
     * removing a component from a layered pane doesn't fully discard the old
     * backbuffer content. {@code validate()} forces Swing to immediately
     * settle the component tree before the repaint, which is the standard
     * fix for exactly this class of leftover-pixels bug.
     */
    private void removeCookieCard(JPanel card) {
        // Deferred for the same reason as the add side above — removing the
        // card synchronously inside the button's own click dispatch is what
        // left stale pixels behind; a fresh EDT turn fixes that.
        javax.swing.SwingUtilities.invokeLater(() -> {
            getLayeredPane().remove(card);
            getLayeredPane().validate();
            getContentPane().repaint();
            Toolkit.getDefaultToolkit().sync();
        });
    }

    /**
     * Works out every Y-coordinate below the services grid, instead of
     * hardcoding them: a card's height depends on how tall the *longest*
     * service description actually renders at this font/width, which isn't
     * knowable as a compile-time constant, and every section after the grid
     * (About Us, the Find Us card, the footer, the page's total height) is
     * positioned relative to wherever the grid actually ends up. Must run
     * before {@link #buildScrollableContent()}.
     */
    private void computeLayout() {
        final int cardPad = 16;
        final int descWidth = CARD_WIDTH - 2 * cardPad;
        final int imgH = 100;
        final Font descFont = new Font("Segoe UI", Font.PLAIN, 12);

        int maxDescHeight = 0;
        for (String[] service : SERVICES) {
            maxDescHeight = Math.max(maxDescHeight, measureWrappedTextHeight(service[1], descFont, descWidth));
        }

        int descTop = cardPad + imgH + 12 + 30; // mirrors textTop + 30 in buildServiceCard
        cardHeight = descTop + maxDescHeight + 14 + SEE_MORE_BUTTON_HEIGHT + cardPad; // room for the "See More" button below the tallest description
        cardRowStep = cardHeight + 20; // same gap between rows this grid always used

        int rows = (SERVICES.length + 1) / 2;
        int gridBottom = CARD_ROW_START + (rows - 1) * cardRowStep + cardHeight;

        aboutHeadingY = gridBottom + 50;
        scrollAbout = aboutHeadingY - 20; // a little above the heading, same convention as SCROLL_HOME/SCROLL_SERVICES

        final int aboutHeadingHeight = 40;
        aboutRowY = aboutHeadingY + aboutHeadingHeight + 35;

        // aboutRowHeight sizes the photo (with a floor so it stays a decent size
        // regardless of how short the paragraph happens to be) — but the Get
        // Directions button is positioned off the paragraph's own real height,
        // not the photo's, since it sits in the text column next to (not below)
        // the photo and shouldn't wait on the photo just because the photo's
        // taller. footerY then takes whichever of the two actually ends lower,
        // so the footer never overlaps either one.
        final int aboutTextX = ABOUT_TEXT_X;
        final int aboutCardW = ABOUT_ROW_RIGHT_X - aboutTextX;
        final int aboutTextW = aboutCardW - 2 * ABOUT_TEXT_CARD_PAD;
        int aboutTextHeight = measureWrappedTextHeight(ABOUT_TEXT, ABOUT_TEXT_FONT, aboutTextW);
        int aboutTextCardHeight = aboutTextHeight + 2 * ABOUT_TEXT_CARD_PAD;
        aboutRowHeight = Math.max(aboutTextCardHeight, 380);
        findUsCardY = aboutRowY + aboutTextCardHeight + 25;

        int photoBottom = aboutRowY + aboutRowHeight;
        int buttonBottom = findUsCardY + FIND_US_CARD_HEIGHT;
        highlightsRowY = Math.max(photoBottom, buttonBottom) + 20;
        footerY = highlightsRowY + HIGHLIGHT_BOX_HEIGHT + 30;

        contentHeight = footerY + FOOTER_HEIGHT + 30;
    }

    private void setupEventListeners() {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        lblUserIcon.setCursor(hand);
        lblServices.setCursor(hand);
        lblHome.setCursor(hand);
        lblAbout.setCursor(hand);
        lblLogo.setCursor(hand); // clicking the brand logo goes Home, same convention as any real website

        lblUserIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AppController.showLogin(Public_Dashboard.this);
            }
        });

        lblLogo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { smoothScrollTo(SCROLL_HOME); }
        });
        lblHome.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { smoothScrollTo(SCROLL_HOME); }
        });
        lblServices.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { smoothScrollTo(SCROLL_SERVICES); }
        });
        lblAbout.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { smoothScrollTo(scrollAbout); }
        });
    }

    /** Top y-coordinate of the i-th service card within the scrollable content panel. */
    private int cardTop(int index) {
        int row = index / 2;
        return CARD_ROW_START + row * cardRowStep;
    }

    /**
     * Animates the scroll pane's vertical scrollbar from wherever it is now
     * to {@code targetY}, eased out over ~600ms — slow enough to actually see
     * the page scroll down to the section, the same "smooth scroll to
     * section" feel a real website gives its nav links, rather than an
     * instant jump cut.
     */
    private void smoothScrollTo(int targetY) {
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        final int start = vBar.getValue();
        final int max = Math.max(0, vBar.getMaximum() - vBar.getVisibleAmount());
        final int clampedTarget = Math.max(0, Math.min(targetY, max));
        final int distance = clampedTarget - start;
        if (distance == 0) {
            return;
        }
        final int totalSteps = 50; // was 24 (~280ms) — too fast to read as scrolling; this is ~600ms
        final Timer[] timerHolder = new Timer[1];
        final int[] step = {0};
        timerHolder[0] = new Timer(12, e -> {
            step[0]++;
            double progress = Math.min(1.0, (double) step[0] / totalSteps);
            double eased = 1 - Math.pow(1 - progress, 3); // ease-out cubic
            vBar.setValue(start + (int) Math.round(distance * eased));
            if (step[0] >= totalSteps) {
                timerHolder[0].stop();
            }
        });
        timerHolder[0].start();
    }

    /**
     * Builds the entire scrollable page below the fixed nav bar — hero,
     * services grid (data-driven from {@link #SERVICES}), about section,
     * footer — and installs it into {@link #scrollPane}. Hand-built rather
     * than form-declared since the services grid is generated from a list,
     * not a fixed set of components a .form file can describe.
     */
    private void buildScrollableContent() {
        JPanel content = new JPanel(null);
        content.setBackground(Color.WHITE);
        content.setPreferredSize(new Dimension(CONTENT_WIDTH, contentHeight));

        buildHeroSection(content);
        buildServicesSection(content);
        buildAboutSection(content);
        buildFooter(content);

        scrollPane.setViewportView(content);
        IconFactory.styleScrollBar(scrollPane); // thin flat scrollbar instead of the OS-default one
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // Default viewport scrolling uses a low-level copyArea() "blit" to shift
        // pixels instead of a full repaint — fast, but it can smear/ghost stale
        // pixels into the shared window backbuffer now that the cookie banner
        // floats above this scroll pane in the frame's layered pane (a
        // translucent, non-opaque overlay). Forcing SIMPLE_SCROLL_MODE trades
        // that micro-optimization for a full clean repaint on every scroll,
        // which removes the ghosting entirely.
        scrollPane.getViewport().setScrollMode(javax.swing.JViewport.SIMPLE_SCROLL_MODE);

        // Fires on every scroll change — manual dragging/wheel *and* our own
        // smoothScrollTo() animation (which just calls setValue() repeatedly) —
        // so the nav bar's active-section highlight stays correct either way.
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> updateActiveNavLink());
        updateActiveNavLink(); // starting state: page loads at the top, Home is active
    }

    /**
     * Highlights whichever nav item matches the section currently in view —
     * Home, Services, or About Us — in the brand accent color, the same
     * "active page" convention a real website's nav bar uses; every other
     * item stays plain white.
     */
    private void updateActiveNavLink() {
        int scrollY = scrollPane.getVerticalScrollBar().getValue();
        JLabel active;
        if (scrollY >= scrollAbout - 40) {
            active = lblAbout;
        } else if (scrollY >= SCROLL_SERVICES - 40) {
            active = lblServices;
        } else {
            active = lblHome;
        }
        for (JLabel navItem : new JLabel[]{lblHome, lblServices, lblAbout}) {
            navItem.setForeground(navItem == active ? ACCENT : Color.WHITE);
        }
    }

    /**
     * The Home page's hero content — banner, tagline, 3 highlight boxes — is
     * now a real GUI-Builder-built component (see HomePagePanel.form/.java),
     * not hand-coded here. This just instantiates it and places it, the same
     * way any other component on this page is added to the scrollable content.
     */
    private void buildHeroSection(JPanel content) {
        HomePagePanel heroPanel = new HomePagePanel();
        content.add(heroPanel);
        heroPanel.setBounds(0, 0, CONTENT_WIDTH, 545);
    }

    private void buildServicesSection(JPanel content) {
        JLabel heading = new JLabel("Our Dental Services", javax.swing.SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 25));
        content.add(heading);
        // y=620, not 560 — the scrollable viewport is 605px tall (see the .form
        // file's scrollPane height), so this keeps "Our Dental Services" entirely
        // below the fold on initial load: Home shows only the banner/tagline/
        // highlight boxes until the user scrolls or clicks "Services".
        heading.setBounds(0, 620, CONTENT_WIDTH, 40);

        JLabel subtitle = new JLabel("Comprehensive care for every stage of your smile journey.", javax.swing.SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(110, 110, 110));
        content.add(subtitle);
        subtitle.setBounds(0, 665, CONTENT_WIDTH, 25);

        for (int i = 0; i < SERVICES.length; i++) {
            int col = i % 2;
            int x = col == 0 ? CARD_LEFT_X : CARD_RIGHT_X;
            int y = cardTop(i);
            content.add(buildServiceCard(i, SERVICES[i][0], SERVICES[i][1], x, y));
        }
    }

    /**
     * Maps a {@link #SERVICES} index to the basename of its real
     * user-supplied {@code <basename>_before.png} / {@code _after.png}
     * pair in src/resources — see {@link #buildServiceCard}. Any index not
     * listed here still falls back to the original vector tooth-icon cards.
     */
    private static final java.util.Map<Integer, String> SERVICE_PHOTO_BASENAME = java.util.Map.ofEntries(
            java.util.Map.entry(0, "aligner"),    // Aligners
            java.util.Map.entry(1, "whitening"),  // Teeth Whitening
            java.util.Map.entry(2, "cleaning"),   // Cleaning & Polishing
            java.util.Map.entry(3, "implants"),   // Dental Implants
            java.util.Map.entry(4, "design"),     // Smile Design
            java.util.Map.entry(5, "canal"),      // Root Canal Treatment
            java.util.Map.entry(6, "fillings"),   // Fillings
            java.util.Map.entry(7, "bridges"),    // Dental Bridges
            java.util.Map.entry(8, "crown"),      // Crowns
            java.util.Map.entry(9, "dentures")    // Dentures
    );

    /**
     * A "before / after" style service card — same visual story as a real
     * clinic's results gallery (a muted "before" panel next to a brighter
     * "after" panel, each with its own corner badge). Cards not listed in
     * {@link #SERVICE_PHOTO_BASENAME} still draw original vector shapes
     * instead of photography, since there's no way to license or reuse
     * another practice's — quite possibly patients' — actual before/after
     * photos; the listed cards use real photos the user supplied themselves
     * and placed in src/resources.
     */
    private JPanel buildServiceCard(int index, String name, String description, int x, int y) {
        final int pad = 16;
        final int imgH = 100;
        final int imgW = (CARD_WIDTH - 2 * pad - 12) / 2;
        final String photoBasename = SERVICE_PHOTO_BASENAME.get(index);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(new javax.swing.border.LineBorder(new Color(228, 228, 228), 1, true));
        card.setBounds(x, y, CARD_WIDTH, cardHeight);
        JPanel before = photoBasename != null
                ? buildPhotoBox("/resources/" + photoBasename + "_before.png", pad, pad, imgW, imgH)
                : buildResultBox(pad, pad, imgW, imgH, BEFORE_BG, BEFORE_ICON);
        card.add(before);
        JPanel beforeBadge = buildResultBadge(pad, pad, imgW, imgH, "BEFORE", new Color(30, 30, 30));
        card.add(beforeBadge);
        card.setComponentZOrder(beforeBadge, 0); // Container.add() appends to the *back* of paint order (index 0 paints last, i.e. on top) — without this the opaque box paints over the badge and hides it entirely

        JPanel after = photoBasename != null
                ? buildPhotoBox("/resources/" + photoBasename + "_after.png", pad + imgW + 12, pad, imgW, imgH)
                : buildResultBox(pad + imgW + 12, pad, imgW, imgH, AFTER_BG, ACCENT);
        card.add(after);
        JPanel afterBadge = buildResultBadge(pad + imgW + 12, pad, imgW, imgH, "AFTER", ACCENT);
        card.add(afterBadge);
        card.setComponentZOrder(afterBadge, 0);

        int textTop = pad + imgH + 12;
        JLabel title = new JLabel(name);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(35, 35, 35));
        card.add(title);
        title.setBounds(pad, textTop, CARD_WIDTH - 2 * pad, 26);

        // cardHeight was sized in computeLayout() to fit this description's real
        // measured height (the longest one across all services, specifically) —
        // so using that real height here, via the same measuring helper, can
        // never overflow the card the way a guessed/fixed height could.
        addWrappedText(card, description, new Font("Segoe UI", Font.PLAIN, 12), new Color(95, 95, 95),
                CARD_WIDTH - 2 * pad, pad, textTop + 30);

        // Positioned relative to the card's own (uniform) bottom, not the description's
        // own height, so it lines up at the same Y on every card regardless of how long
        // that card's own description happens to be.
        JButton btnSeeMore = new JButton("See More");
        btnSeeMore.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSeeMore.setBackground(new Color(0x3C, 0x78, 0x78));
        btnSeeMore.setForeground(Color.WHITE);
        btnSeeMore.setBorderPainted(false);
        btnSeeMore.setFocusPainted(false);
        btnSeeMore.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSeeMore.addActionListener(e -> {
            if (System.currentTimeMillis() - lastDetailDialogCloseMillis < DETAIL_DIALOG_CLICK_GUARD_MS) {
                return; // swallow the OS click-through from the popup that just closed
            }
            // invokeLater, not a direct call: opening the modal dialog's nested event
            // loop *synchronously inside this very click dispatch* is what left
            // Swing's click/press-release tracking confused once the dialog closed —
            // starting it on a fresh event-loop turn instead avoids that entirely.
            javax.swing.SwingUtilities.invokeLater(() -> showServiceDetailDialog(index));
        });
        card.add(btnSeeMore);
        final int seeMoreWidth = 120;
        btnSeeMore.setBounds((CARD_WIDTH - seeMoreWidth) / 2, cardHeight - SEE_MORE_BUTTON_HEIGHT - pad, seeMoreWidth, SEE_MORE_BUTTON_HEIGHT);

        addCardHoverEffect(card, index);
        return card;
    }

    /**
     * One real "before" or "after" photo panel — draws {@code resourcePath}
     * (a classpath resource under src/resources) cover-fit and cropped to
     * fill the box exactly, clipped to the same rounded corners as
     * {@link #buildResultBox}'s vector version, so a real photo card and a
     * vector-icon card line up visually. No badge of its own; see
     * {@link #buildResultBadge}.
     */
    private JPanel buildPhotoBox(String resourcePath, int x, int y, int w, int h) {
        return buildPhotoBox(resourcePath, x, y, w, h, true);
    }

    /**
     * @param coverFit true fills the box exactly, cropping whatever overflows (the
     *                 before/after service cards, where every box must line up to
     *                 the same size); false shrinks the image to fit entirely inside
     *                 the box instead, letterboxed on white rather than cropped —
     *                 for a photo (like the About Us building shot) that needs to
     *                 stay fully visible with nothing cut off.
     */
    private JPanel buildPhotoBox(String resourcePath, int x, int y, int w, int h, boolean coverFit) {
        final Image photo = new ImageIcon(getClass().getResource(resourcePath)).getImage();

        JPanel box = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int iw = photo.getWidth(this);
                int ih = photo.getHeight(this);
                if (iw <= 0 || ih <= 0) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));
                double scale = coverFit
                        ? Math.max((double) w / iw, (double) h / ih)  // cover-fit: fill the box, crop the overflow
                        : Math.min((double) w / iw, (double) h / ih); // contain-fit: fit fully inside, letterbox the rest
                int dw = (int) Math.ceil(iw * scale);
                int dh = (int) Math.ceil(ih * scale);
                g2.drawImage(photo, (w - dw) / 2, (h - dh) / 2, dw, dh, this);
                g2.dispose();
            }
        };
        box.setOpaque(!coverFit); // contain-fit needs an opaque white backdrop behind the letterbox bars
        box.setBackground(Color.WHITE);
        box.setBounds(x, y, w, h);
        return box;
    }

    /** One "before" or "after" panel: a tinted box with a centered tooth glyph — no badge of its own; see {@link #buildResultBadge}. */
    private JPanel buildResultBox(int x, int y, int w, int h, Color bg, Color iconColor) {
        JPanel box = new JPanel(null);
        box.setBackground(bg);
        box.setBounds(x, y, w, h);
        IconFactory.roundCorners(box, 10);

        JLabel icon = new JLabel(IconFactory.tooth(iconColor, 40), javax.swing.SwingConstants.CENTER);
        box.add(icon);
        icon.setBounds(0, 0, w, h);

        return box;
    }

    /**
     * A pill-shaped "BEFORE"/"AFTER" label anchored near the bottom of its
     * result box, centered horizontally. (Real clinic galleries straddle
     * this badge across the seam of two *stacked* photos — this card's
     * before/after panels sit side by side instead, so straddling the box's
     * bottom edge would just float the pill in the blank card padding below
     * it; keeping it fully inside the box reads cleanly instead.)
     */
    private JPanel buildResultBadge(int boxX, int boxY, int boxW, int boxH, String text, Color color) {
        int badgeW = 64, badgeH = 20;
        int x = boxX + (boxW - badgeW) / 2;
        int y = boxY + boxH - badgeH - 8;

        JPanel badge = new JPanel(null);
        badge.setBackground(color);
        badge.setBounds(x, y, badgeW, badgeH);
        IconFactory.roundCorners(badge, badgeH);

        JLabel label = new JLabel(text, javax.swing.SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(Color.WHITE);
        badge.add(label);
        label.setBounds(0, 0, badgeW, badgeH);

        return badge;
    }

    /**
     * A subtle "lift" on hover — background lightens, border picks up the
     * accent color — purely a visual cue that the card holds more detail;
     * opening the detail popup itself is the "See More" button's job alone
     * now (see {@link #showServiceDetailDialog}), not a click anywhere on
     * the card.
     */
    private void addCardHoverEffect(JPanel card, int index) {
        final Color defaultBg = card.getBackground();
        final Color hoverBg = new Color(255, 247, 240);
        final javax.swing.border.LineBorder defaultBorder = (javax.swing.border.LineBorder) card.getBorder();
        final javax.swing.border.LineBorder hoverBorder = new javax.swing.border.LineBorder(ACCENT, 1, true);

        MouseAdapter hover = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(hoverBg);
                card.setBorder(hoverBorder);
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(defaultBg);
                card.setBorder(defaultBorder);
            }
        };
        addHoverListenerRecursively(card, hover);
    }

    /**
     * Attaches {@code hover} to every component in this subtree, not just
     * direct children — needed because the before/after tooth icons now
     * fill their whole box, so the mouse is always over some nested
     * component, never the card's own background, once it's inside a card.
     */
    private void addHoverListenerRecursively(java.awt.Container parent, MouseAdapter hover) {
        parent.addMouseListener(hover);
        for (java.awt.Component comp : parent.getComponents()) {
            comp.addMouseListener(hover);
            if (comp instanceof java.awt.Container) {
                addHoverListenerRecursively((java.awt.Container) comp, hover);
            }
        }
    }

    /**
     * The full "What Are X? / Advantages of X" article for one service, in
     * a scrollable popup — opened by clicking its card. Cards themselves
     * stay compact in the 2-column grid; this is where the long-form
     * content (from {@link #SERVICE_INTRO} / {@link #SERVICE_ADVANTAGES})
     * actually lives.
     */
    private void showServiceDetailDialog(int index) {
        if (detailDialogShowing) {
            return; // a popup is already open — never let a second one stack on top of it
        }
        detailDialogShowing = true;

        String name = SERVICES[index][0];
        String intro = SERVICE_INTRO[index];
        String[] advantages = SERVICE_ADVANTAGES[index];
        final int contentWidth = 520; // was 560 — text (justified to fill this width) sat almost flush against the scrollbar; this opens up real right-side breathing room

        JPanel body = new JPanel(null);
        body.setBackground(Color.WHITE);

        int y = 40;
        // Plain JLabel, not HTML — a single short line, never wraps.
        JLabel heading = new JLabel("What Are " + name + "?");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(30, 30, 30));
        body.add(heading);
        heading.setBounds(20, y, contentWidth + 40, 30);
        y += 30 + 30;

        // Same manual FontMetrics wrap as the rest of the page (see addWrappedText's
        // doc comment) instead of HTML-in-JLabel — this dialog was still using the
        // HTML renderer and hit the exact same mid-line text-cutting bug that was
        // already fixed everywhere else on this page.
        int introHeight = addWrappedText(body, intro,
                new Font("Segoe UI", Font.PLAIN, 14), new Color(70, 70, 70), contentWidth, 20, y);
        y += introHeight + 40;

        JLabel advHeading = new JLabel("Advantages of " + name);
        advHeading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        advHeading.setForeground(new Color(30, 30, 30));
        body.add(advHeading);
        advHeading.setBounds(20, y, contentWidth, 28);
        y += 40;

        for (String bullet : advantages) {
            String[] parts = bullet.split("::", 2);
            String term = parts[0];
            String desc = parts.length > 1 ? parts[1] : "";
            int bulletHeight = addWrappedBulletText(body, term, desc,
                    new Font("Segoe UI", Font.BOLD, 13), new Font("Segoe UI", Font.PLAIN, 13),
                    new Color(70, 70, 70), contentWidth - 20, 20, y, 18);
            y += bulletHeight + 10;
        }
        y += 20;
        body.setPreferredSize(new Dimension(600, y));

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        IconFactory.styleScrollBar(scroll);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JButton btnClose = new JButton("OK");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(ACCENT);
        btnClose.setForeground(Color.WHITE);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(120, 38));
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JDialog dialog = new JDialog(this, name, true);
        // Undecorated — no native OS title bar/X. We draw our own header below
        // with our own X button instead, so it's a button we actually control
        // (styling, click handling) rather than the OS's.
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // JDialog defaults to HIDE_ON_CLOSE, which would never fire windowClosed below
        // invokeLater, not a direct dispose() call: disposing synchronously inside this
        // same click handler is what lets the OS redeliver that mouse-up to whatever
        // card is now exposed underneath (the actual cause of the "repeat popup" bug) —
        // deferring it to the next event-queue turn decouples the two.
        btnClose.addActionListener(e -> javax.swing.SwingUtilities.invokeLater(dialog::dispose));
        // Covers every dismissal path (Close button, header X, Esc) in one place —
        // see the DETAIL_DIALOG_CLICK_GUARD_MS field comment for why this is needed.
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                lastDetailDialogCloseMillis = System.currentTimeMillis();
                detailDialogShowing = false;
            }
        });

        JPanel header = new JPanel(null);
        header.setBackground(new Color(248, 249, 250));
        header.setPreferredSize(new Dimension(10, 44));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        JLabel headerTitle = new JLabel(name);
        headerTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        headerTitle.setForeground(new Color(60, 60, 60));
        headerTitle.setBounds(16, 0, 300, 44);
        header.add(headerTitle);

        // A close button we draw and control — same actionButton + cross-glyph
        // pattern every other dialog in this app already uses for its own X —
        // instead of relying on the OS native title-bar X (removed along with
        // the rest of the native chrome via setUndecorated above).
        JButton btnCloseX = IconFactory.actionButton(IconFactory.cross(Color.WHITE, 12), new Color(220, 53, 69), "Close");
        btnCloseX.setBounds(620 - 44, 8, 28, 28);
        btnCloseX.addActionListener(e -> javax.swing.SwingUtilities.invokeLater(dialog::dispose));
        header.add(btnCloseX);
        // Undecorated windows lose the native title bar's drag-to-move — dragging
        // this header moves the dialog instead, so it still feels like a window.
        final java.awt.Point[] dragStart = {null};
        header.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
        });
        header.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragStart[0] == null) return;
                java.awt.Point loc = dialog.getLocation();
                dialog.setLocation(loc.x + e.getX() - dragStart[0].x, loc.y + e.getY() - dragStart[0].y);
            }
        });

        JPanel closeBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        closeBar.setBackground(new Color(248, 249, 250));
        closeBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        closeBar.add(btnClose);

        JPanel pnlModal = new JPanel(new BorderLayout());
        pnlModal.setBackground(Color.WHITE);
        pnlModal.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        pnlModal.add(header, BorderLayout.NORTH);
        pnlModal.add(scroll, BorderLayout.CENTER);
        pnlModal.add(closeBar, BorderLayout.SOUTH);

        dialog.setContentPane(pnlModal);
        dialog.setSize(620, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /**
     * Word-wraps {@code text} to fit {@code width} using real
     * {@link FontMetrics}, and adds one plain-text {@link JLabel} per line
     * — deliberately not HTML. Every mid-sentence text-clipping bug hit
     * while building this page traced back to Swing's HTML-in-JLabel
     * renderer, and only ever affected HTML-rendered labels — plain-text
     * labels (like every card's title) never once exhibited it. Rather
     * than keep chasing that renderer's behavior, plain lines measured
     * directly against the real font avoid the whole class of bug.
     * Returns the total height used, so the caller can lay out whatever
     * comes next.
     */
    private int addWrappedText(JPanel parent, String text, Font font, Color color, int width, int x, int y) {
        FontMetrics fm = parent.getFontMetrics(font);
        List<String> lines = wrapText(text, fm, width);
        int lineHeight = fm.getHeight();
        int spaceWidth = fm.charWidth(' ');
        int curY = y;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            boolean lastLine = i == lines.size() - 1;
            // Justified: every line but the last is stretched to the full column
            // width by inserting extra spaces between its words (real justify needs
            // an HTML/text-layout engine we deliberately moved away from — see
            // addWrappedText's own class-level reasoning above — so this approximates
            // it directly). The last line of a paragraph is conventionally left as-is,
            // not stretched, the same way justified text works in print.
            String renderLine = lastLine ? line : justifyLine(line, fm, width, spaceWidth);

            JLabel lineLabel = new JLabel(renderLine);
            lineLabel.setFont(font);
            lineLabel.setForeground(color);
            parent.add(lineLabel);
            // A flat, generous buffer past the wrap column width — not a tight fit
            // around the measured text. FontMetrics.stringWidth() sums per-glyph
            // advances and doesn't account for kerning, which "Segoe UI" uses, so it
            // systematically under-measures the width Swing's own UI delegate computes
            // when actually painting the same string — a label sized to match that
            // under-measurement is *always* a little too narrow, and Swing silently
            // truncates it with "…" rather than overflow. These labels sit at the end
            // of a row with nothing to their right, so a generous width costs nothing.
            lineLabel.setBounds(x, curY, width + 40, lineHeight);
            curY += lineHeight;
        }
        return curY - y;
    }

    /**
     * Same manual FontMetrics wrap as {@link #addWrappedText}, but for a
     * bullet line that mixes a bold {@code term} with a plain {@code desc}
     * on one continuous run (e.g. "<b>Term</b>: rest of the sentence") —
     * the one thing HTML-in-JLabel was still being used for in this file,
     * and the source of the same mid-line text-cutting bug. Wraps word by
     * word across both fonts, then lays each line out as one JLabel per
     * word so bold and plain segments sit flush against each other with no
     * gap or overlap. Returns the total height used.
     */
    private int addWrappedBulletText(JPanel parent, String term, String desc, Font boldFont, Font plainFont,
            Color color, int width, int x, int y, int indent) {
        FontMetrics fmBold = parent.getFontMetrics(boldFont);
        FontMetrics fmPlain = parent.getFontMetrics(plainFont);
        int lineHeight = Math.max(fmBold.getHeight(), fmPlain.getHeight());
        int spaceWidth = fmPlain.charWidth(' ');
        int textWidth = width - indent;

        List<String> words = new ArrayList<>();
        List<Boolean> bolds = new ArrayList<>();
        for (String w : (term + ":").split(" ")) {
            words.add(w);
            bolds.add(Boolean.TRUE);
        }
        for (String w : desc.split(" ")) {
            words.add(w);
            bolds.add(Boolean.FALSE);
        }

        List<List<Integer>> lines = new ArrayList<>();
        List<Integer> currentLine = new ArrayList<>();
        int currentWidth = 0;
        for (int i = 0; i < words.size(); i++) {
            FontMetrics fm = bolds.get(i) ? fmBold : fmPlain;
            int wWidth = fm.stringWidth(words.get(i));
            int addWidth = (currentLine.isEmpty() ? 0 : spaceWidth) + wWidth;
            if (!currentLine.isEmpty() && currentWidth + addWidth > textWidth) {
                lines.add(currentLine);
                currentLine = new ArrayList<>();
                currentWidth = 0;
                addWidth = wWidth;
            }
            currentLine.add(i);
            currentWidth += addWidth;
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }

        int curY = y;
        JLabel bulletLabel = new JLabel("•");
        bulletLabel.setFont(plainFont);
        bulletLabel.setForeground(color);
        parent.add(bulletLabel);
        bulletLabel.setBounds(x, curY, indent, lineHeight);

        for (List<Integer> line : lines) {
            int curX = x + indent;
            for (int idx : line) {
                boolean bold = bolds.get(idx);
                Font f = bold ? boldFont : plainFont;
                FontMetrics fm = bold ? fmBold : fmPlain;
                String word = words.get(idx);
                JLabel wordLabel = new JLabel(word);
                wordLabel.setFont(f);
                wordLabel.setForeground(color);
                parent.add(wordLabel);
                // Generous flat buffer past the measured width, same reasoning as
                // addWrappedText — FontMetrics.stringWidth() under-measures kerned
                // fonts, and the buffer only affects this label's own bounds, not
                // where the next word gets placed (that uses the exact measurement).
                wordLabel.setBounds(curX, curY, fm.stringWidth(word) + 20, lineHeight);
                curX += fm.stringWidth(word) + spaceWidth;
            }
            curY += lineHeight + 5; // a little extra breathing room, like the old line-height:145%
        }
        return curY - y - 5;
    }

    /** Stretches one wrapped line to fill {@code width} by distributing extra spaces evenly across its word gaps. Single-word lines and lines that already reach (or exceed) the width pass through unchanged — nothing to stretch, or already full. */
    private static String justifyLine(String line, FontMetrics fm, int width, int spaceWidth) {
        String[] words = line.split(" ");
        if (words.length <= 1 || spaceWidth <= 0) {
            return line;
        }
        int deficit = width - fm.stringWidth(line);
        int gaps = words.length - 1;
        int extraSpaces = deficit / spaceWidth;
        if (extraSpaces <= 0) {
            return line;
        }
        int baseExtra = extraSpaces / gaps;
        int remainder = extraSpaces % gaps; // the first `remainder` gaps get one extra space each, spreading the leftover evenly rather than dumping it all in the last gap
        StringBuilder sb = new StringBuilder();
        for (int g = 0; g < gaps; g++) {
            sb.append(words[g]);
            int spacesHere = 1 + baseExtra + (g < remainder ? 1 : 0);
            for (int s = 0; s < spacesHere; s++) {
                sb.append(' ');
            }
        }
        sb.append(words[words.length - 1]);
        return sb.toString();
    }

    /** Measures how tall {@link #addWrappedText} would render {@code text} at {@code width}, without adding anything to a container — for sizing a card before it's built (see computeLayout()). */
    private int measureWrappedTextHeight(String text, Font font, int width) {
        FontMetrics fm = new JLabel().getFontMetrics(font);
        return wrapText(text, fm, width).size() * fm.getHeight();
    }

    private static List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (line.length() == 0 || fm.stringWidth(candidate) <= maxWidth) {
                line = new StringBuilder(candidate);
            } else {
                lines.add(line.toString());
                line = new StringBuilder(word);
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private void buildAboutSection(JPanel content) {
        JLabel heading = new JLabel("About Us", javax.swing.SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 25));
        content.add(heading);
        heading.setBounds(0, aboutHeadingY, CONTENT_WIDTH, 40);

        final int rowY = aboutRowY;
        content.add(buildPhotoBox("/resources/about_us.jpg", ABOUT_IMAGE_X, rowY, ABOUT_IMAGE_W, aboutRowHeight, false));

        // No visible border on either the photo or the paragraph now — back to
        // floating on plain white, just keeping the same padded layout dimensions.
        final int textX = ABOUT_TEXT_X;
        final int cardW = ABOUT_ROW_RIGHT_X - textX;
        final int textW = cardW - 2 * ABOUT_TEXT_CARD_PAD;
        int textHeight = measureWrappedTextHeight(ABOUT_TEXT, ABOUT_TEXT_FONT, textW);
        int textCardHeight = textHeight + 2 * ABOUT_TEXT_CARD_PAD;

        JPanel textCard = new JPanel(null);
        textCard.setBackground(Color.WHITE);
        content.add(textCard);
        textCard.setBounds(textX, rowY, cardW, textCardHeight);

        addWrappedText(textCard, ABOUT_TEXT, ABOUT_TEXT_FONT, new Color(60, 60, 60), textW, ABOUT_TEXT_CARD_PAD, ABOUT_TEXT_CARD_PAD);

        buildFindUsCard(content, findUsCardY);
        buildHighlightsRow(content, highlightsRowY);
        // The rest of the contact details also live in the footer below, alongside the logo and quick links — see buildFooter.
    }

    /** Four reassurance highlights, each its own hoverable icon card — the same clickable-card hover treatment as the service cards, just simpler content. */
    private void buildHighlightsRow(JPanel content, int y) {
        final int count = 4, gap = 20;
        final int boxW = (ABOUT_ROW_RIGHT_X - ABOUT_IMAGE_X - (count - 1) * gap) / count;

        buildHighlightBox(content, ABOUT_IMAGE_X, y, boxW,
                IconFactory::clock, "24/7 Emergency Care");
        buildHighlightBox(content, ABOUT_IMAGE_X + (boxW + gap), y, boxW,
                IconFactory::tooth, "Expert Dentists");
        buildHighlightBox(content, ABOUT_IMAGE_X + 2 * (boxW + gap), y, boxW,
                IconFactory::check, "Certified & Trusted");
        buildHighlightBox(content, ABOUT_IMAGE_X + 3 * (boxW + gap), y, boxW,
                IconFactory::headset, "Friendly Support");
    }

    /** One highlight card: an icon over a short label, both recoloring to the brand accent on hover along with the usual card lift (background tint + border). */
    private void buildHighlightBox(JPanel content, int x, int y, int w,
            java.util.function.BiFunction<Color, Integer, javax.swing.Icon> iconFn, String label) {
        final Color normalColor = new Color(110, 110, 110);
        final int iconSize = 20;
        final Color defaultBorderColor = new Color(228, 228, 228);
        final Color[] borderColor = {defaultBorderColor};

        // Genuinely rounded corners, not LineBorder's — its "rounded" flag barely
        // shows at 1px thickness. Painted directly (fill + stroked outline) instead.
        JPanel box = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int bw = getWidth(), bh = getHeight();
                // Inset by 1px on ALL sides (not just right/bottom) so the stroke
                // — centered on its path — has room to render fully on the left
                // and top edges too, instead of getting clipped by the component bounds.
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(1, 1, bw - 2, bh - 2, 14, 14));
                g2.setColor(borderColor[0]);
                g2.draw(new RoundRectangle2D.Float(1, 1, bw - 2, bh - 2, 14, 14));
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setBackground(Color.WHITE);
        content.add(box);
        box.setBounds(x, y, w, HIGHLIGHT_BOX_HEIGHT);

        JLabel iconLabel = new JLabel(iconFn.apply(normalColor, iconSize));
        iconLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        box.add(iconLabel);
        iconLabel.setBounds(0, 8, w, iconSize + 4);

        JLabel textLabel = new JLabel(label, javax.swing.SwingConstants.CENTER);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        textLabel.setForeground(new Color(50, 50, 50));
        box.add(textLabel);
        textLabel.setBounds(4, 8 + iconSize + 8, w - 8, 20);

        final Color defaultBg = box.getBackground();
        final Color hoverBg = new Color(255, 247, 240);

        MouseAdapter hover = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                box.setBackground(hoverBg);
                borderColor[0] = ACCENT;
                box.repaint();
                iconLabel.setIcon(iconFn.apply(ACCENT, iconSize));
            }
            @Override public void mouseExited(MouseEvent e) {
                box.setBackground(defaultBg);
                borderColor[0] = defaultBorderColor;
                box.repaint();
                iconLabel.setIcon(iconFn.apply(normalColor, iconSize));
            }
        };
        addHoverListenerRecursively(box, hover);
    }

    /**
     * Just the "Get Directions" button, no card/border around it — clicking
     * it opens the address directly in Google Maps via {@link #openDirections()},
     * no intermediate popup.
     */
    private void buildFindUsCard(JPanel content, int y) {
        JButton btnDirections = new JButton("Get Directions");
        btnDirections.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDirections.setBackground(ACCENT);
        btnDirections.setForeground(Color.WHITE);
        btnDirections.setBorderPainted(false);
        btnDirections.setFocusPainted(false);
        btnDirections.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDirections.addActionListener(e -> openDirections());
        content.add(btnDirections);
        final int btnW = 180, btnH = 42;
        // Centered under the About paragraph's own column (same x/width math as
        // buildAboutSection's text), not the full page width — so it lines up with
        // the text above it instead of sitting under the gap next to the photo.
        final int textX = ABOUT_TEXT_X;
        final int textW = ABOUT_ROW_RIGHT_X - textX;
        btnDirections.setBounds(textX + (textW - btnW) / 2, y + (FIND_US_CARD_HEIGHT - btnH) / 2, btnW, btnH);
    }

    /** Opens the clinic's address as a Google Maps search in the system's default browser. */
    private void openDirections() {
        try {
            String query = URLEncoder.encode(CLINIC_ADDRESS, StandardCharsets.UTF_8.toString());
            Desktop.getDesktop().browse(new URI("https://www.google.com/maps/search/?api=1&query=" + query));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Couldn't open your browser for directions:\n" + ex.getMessage(),
                    "Get Directions", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Full-width dark footer bar — logo, tagline, and social icons on the
     * left; quick nav links in the middle; contact details on the right;
     * a divider and copyright line across the bottom. Same real-website
     * shape as the fixed nav bar up top (including its rounded corners),
     * just for the bottom of the page.
     */
    private void buildFooter(JPanel content) {
        JPanel footer = new JPanel(null);
        footer.setBackground(Color.BLACK);
        content.add(footer);
        footer.setBounds(0, footerY, CONTENT_WIDTH, FOOTER_HEIGHT);
        IconFactory.roundCorners(footer, 20);

        final Color muted = new Color(190, 190, 190);
        final Color heading = ACCENT;
        final int col1X = 40, col2X = 380, col3X = 630, rightEdge = 880;

        // Column 1 — brand
        JLabel logo = new JLabel(IconFactory.brandLogo(130, 36));
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { smoothScrollTo(SCROLL_HOME); }
        });
        footer.add(logo);
        logo.setBounds(col1X, 22, 130, 36);

        addWrappedText(footer, "Comprehensive, patient-centered dental care in the heart of Colombo.",
                new Font("Segoe UI", Font.PLAIN, 12), muted, 260, col1X, 62);

        int iconX = col1X;
        for (javax.swing.Icon socialIcon : new javax.swing.Icon[]{
            IconFactory.mailBrand(20), IconFactory.instagramBrand(20), IconFactory.tiktokBrand(20), IconFactory.whatsappBrand(20)
        }) {
            JLabel iconLabel = new JLabel(socialIcon);
            footer.add(iconLabel);
            iconLabel.setBounds(iconX, 105, 20, 20);
            iconX += 30;
        }

        // Column 2 — quick links
        JLabel linksHeading = new JLabel("Quick Links");
        linksHeading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        linksHeading.setForeground(heading);
        footer.add(linksHeading);
        linksHeading.setBounds(col2X, 22, 200, 22);

        addFooterLink(footer, "Home", col2X, 54, () -> smoothScrollTo(SCROLL_HOME));
        addFooterLink(footer, "Services", col2X, 79, () -> smoothScrollTo(SCROLL_SERVICES));
        addFooterLink(footer, "About Us", col2X, 104, () -> smoothScrollTo(scrollAbout));

        // Column 3 — contact
        JLabel contactHeading = new JLabel("Contact Us");
        contactHeading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contactHeading.setForeground(heading);
        footer.add(contactHeading);
        contactHeading.setBounds(col3X, 22, 200, 22);

        String[] contactLines = {CLINIC_ADDRESS, CLINIC_PHONE, CLINIC_EMAIL};
        int lineY = 54;
        for (String line : contactLines) {
            JLabel lineLabel = new JLabel(line);
            lineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lineLabel.setForeground(muted);
            footer.add(lineLabel);
            lineLabel.setBounds(col3X, lineY, rightEdge - col3X, 20);
            lineY += 23;
        }

        // Divider + copyright
        JPanel divider = new JPanel();
        divider.setBackground(new Color(60, 60, 60));
        footer.add(divider);
        divider.setBounds(col1X, 150, rightEdge - col1X, 1);

        JLabel copyright = new JLabel("© 2026 Sunrise Dental Clinic | Your Smile, Our Priority", javax.swing.SwingConstants.CENTER);
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyright.setForeground(muted);
        footer.add(copyright);
        copyright.setBounds(0, 164, CONTENT_WIDTH, 24);
    }

    /** One clickable "Home"/"Services"/"About Us" style text link inside the footer. */
    private void addFooterLink(JPanel footer, String text, int x, int y, Runnable onClick) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        link.setForeground(new Color(190, 190, 190));
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
            @Override public void mouseEntered(MouseEvent e) { link.setForeground(ACCENT); }
            @Override public void mouseExited(MouseEvent e) { link.setForeground(new Color(190, 190, 190)); }
        });
        footer.add(link);
        link.setBounds(x, y, 200, 20);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblHome = new javax.swing.JLabel();
        lblServices = new javax.swing.JLabel();
        lblAbout = new javax.swing.JLabel();
        lblUserIcon = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sunrise Dental Clinic");
        setName("Public_Dashboard"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(null);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(null);

        navBar.setBackground(new java.awt.Color(0, 0, 0));
        navBar.setLayout(null);

        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblLogo.setForeground(new java.awt.Color(255, 255, 255));
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo_scaled.png"))); // NOI18N
        navBar.add(lblLogo);
        lblLogo.setBounds(15, 7, 160, 40);

        lblHome.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        lblHome.setForeground(new java.awt.Color(255, 255, 255));
        lblHome.setText("Home");
        navBar.add(lblHome);
        lblHome.setBounds(450, 18, 70, 25);

        lblServices.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        lblServices.setForeground(new java.awt.Color(255, 255, 255));
        lblServices.setText("Services");
        navBar.add(lblServices);
        lblServices.setBounds(565, 18, 90, 25);

        lblAbout.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        lblAbout.setForeground(new java.awt.Color(255, 255, 255));
        lblAbout.setText("About Us");
        navBar.add(lblAbout);
        lblAbout.setBounds(700, 18, 100, 25);

        lblUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/login_scaled.png"))); // NOI18N
        lblUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        navBar.add(lblUserIcon);
        lblUserIcon.setBounds(880, 10, 40, 40);

        mainPanel.add(navBar);
        navBar.setBounds(30, 20, 940, 55);

        scrollPane.setBorder(null);
        mainPanel.add(scrollPane);
        scrollPane.setBounds(30, 85, 940, 605);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(0, 0, 1000, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblAbout;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblServices;
    private javax.swing.JLabel lblUserIcon;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navBar;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
