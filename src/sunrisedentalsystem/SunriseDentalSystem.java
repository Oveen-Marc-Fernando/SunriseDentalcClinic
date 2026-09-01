/*
 * Sunrise Dental System — Main Entry Point (MVC Architecture)
 *
 * Flow:
 *   main() → AppController.launch()
 *            └─ Creates Public_Dashboard (View)
 *               └─ User clicks Login → LoginForm (View)
 *                  └─ LogInController validates via LoginModel
 *                     └─ On success → Role-specific Dashboard (View + Controller + Model)
 *
 * @author oveen
 */
package sunrisedentalsystem;

import controller.AppController;

/**
 * Application bootstrap — delegates all startup logic to AppController.
 */
public class SunriseDentalSystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Must be set before any AWT/Swing class loads (hence first thing in
        // main, before AppController.launch() touches a single view class).
        // Forces software rendering instead of the Direct3D/DirectDraw
        // pipeline — Windows' GPU-accelerated Java2D pipeline is a known
        // source of transient window-content "ghosting" (a stale, blended
        // frame briefly visible after a repaint), which is exactly the
        // Public Dashboard's navbar-smearing symptom around the cookie
        // banner. This trades a little rendering performance for pixel-
        // correct repaints on every frame.
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.noddraw", "true");

        AppController.launch();
    }
}
