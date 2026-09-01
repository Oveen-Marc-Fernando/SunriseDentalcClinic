package controller;

import dao.CookieConsentDAO;
import java.util.List;
import model.CookieConsentModel;

/**
 * Controller for the cookie-consent audit trail — one end is
 * {@code Public_Dashboard}'s cookie banner (calls {@link #recordAcceptance}
 * the moment "Accept All" is clicked), the other is Administration's
 * read-only {@code AD_OP_Cookies} screen (calls {@link #getAll}).
 *
 * Backed by the real {@code cookie_consents} table (see db/schema.sql) via
 * {@link CookieConsentDAO}.
 *
 * @author oveen
 */
public class CookieConsentController {

    private static final CookieConsentDAO COOKIE_DAO = new CookieConsentDAO();

    /** Read-only snapshot of every recorded consent, most recent first. */
    public static List<CookieConsentModel> getAll() {
        return COOKIE_DAO.findAll();
    }

    /** Number of consents on record — AD_OP_Cookies' badge dot, and every other Operations screen's quick-access button. */
    public static int getRecordCount() {
        return COOKIE_DAO.count();
    }

    /**
     * Records a new "Accept All" click. Fire-and-forget from the caller's
     * point of view — a failed insert (e.g. the database is briefly
     * unreachable) is logged by the DAO but never blocks or interrupts the
     * cookie banner itself, since accepting cookies must always work from
     * the visitor's perspective regardless of whether the audit log write
     * succeeds.
     */
    public static void recordAcceptance(String deviceId, String ipAddress, String userAgent) {
        COOKIE_DAO.insert(deviceId, ipAddress, userAgent);
    }
}
