package model;

/**
 * One recorded "Accept All" click on the Public Dashboard's cookie-consent
 * banner (see view.Public_Dashboard#showCookieConsentBanner). Declining
 * isn't recorded here — this is specifically a log of accepted consents,
 * for Administration's read-only "Cookies" screen (AD_OP_Cookies).
 *
 * Since this is a desktop application rather than a website, {@code deviceId}
 * and {@code userAgent} are desktop-appropriate stand-ins for the browser
 * concepts they're named after — see util.DeviceIdentity for exactly how
 * each one is produced.
 *
 * @author oveen
 */
public class CookieConsentModel {

    private int    consentId;
    private String deviceId;
    private String ipAddress;
    private String userAgent;
    private String firstSeen; // "yyyy-MM-dd HH:mm:ss"

    public CookieConsentModel() {
        // default empty constructor
    }

    public int    getConsentId()          { return consentId; }
    public void   setConsentId(int v)     { this.consentId = v; }

    public String getDeviceId()           { return deviceId; }
    public void   setDeviceId(String v)   { this.deviceId = v; }

    public String getIpAddress()          { return ipAddress; }
    public void   setIpAddress(String v)  { this.ipAddress = v; }

    public String getUserAgent()          { return userAgent; }
    public void   setUserAgent(String v)  { this.userAgent = v; }

    public String getFirstSeen()          { return firstSeen; }
    public void   setFirstSeen(String v)  { this.firstSeen = v; }

    @Override
    public String toString() {
        return "CookieConsentModel{"
                + "deviceId='" + deviceId + '\''
                + ", ipAddress='" + ipAddress + '\''
                + ", userAgent='" + userAgent + '\''
                + ", firstSeen='" + firstSeen + '\''
                + '}';
    }
}
