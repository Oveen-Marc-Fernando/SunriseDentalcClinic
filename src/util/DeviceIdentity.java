package util;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Desktop-appropriate stand-ins for the browser concepts the cookie-consent
 * audit trail is named after (see model.CookieConsentModel) — there's no
 * real browser involved here, so these three values are produced honestly
 * rather than faked as if they came from one:
 *
 * <ul>
 *   <li>{@link #getDeviceId()} — a random ID generated once per installation
 *       and cached in a local file, the closest desktop-app equivalent to a
 *       persistent browser cookie/device fingerprint.</li>
 *   <li>{@link #getLocalIpAddress()} — this machine's own local network
 *       address (there's no HTTP request to read a client IP from).</li>
 *   <li>{@link #getUserAgentLike()} — a Java/OS version string in the same
 *       spirit as a browser's User-Agent header, not a literal one.</li>
 * </ul>
 *
 * @author oveen
 */
public final class DeviceIdentity {

    private DeviceIdentity() {
    }

    private static final Path ID_FILE =
            Path.of(System.getProperty("user.home"), ".sunrisedental", "device_id.txt");

    private static volatile String cachedDeviceId;

    /** This installation's persisted device ID — generated once, reused on every later run. */
    public static synchronized String getDeviceId() {
        if (cachedDeviceId != null) {
            return cachedDeviceId;
        }
        try {
            if (Files.exists(ID_FILE)) {
                String existing = Files.readString(ID_FILE, StandardCharsets.UTF_8).trim();
                if (!existing.isEmpty()) {
                    cachedDeviceId = existing;
                    return cachedDeviceId;
                }
            }
            String generated = UUID.randomUUID().toString();
            Files.createDirectories(ID_FILE.getParent());
            Files.writeString(ID_FILE, generated, StandardCharsets.UTF_8);
            cachedDeviceId = generated;
        } catch (IOException e) {
            // Falls back to a fresh, unpersisted ID for just this run rather
            // than failing the whole cookie-acceptance flow over a disk issue.
            System.err.println("[DeviceIdentity] Couldn't read/write " + ID_FILE + ": " + e.getMessage());
            cachedDeviceId = UUID.randomUUID().toString();
        }
        return cachedDeviceId;
    }

    /** This machine's own local network address, e.g. "192.168.1.42". */
    public static String getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /** A Java/OS version string, the desktop-app equivalent of a browser's User-Agent header. */
    public static String getUserAgentLike() {
        String java = System.getProperty("java.version", "unknown");
        String os = System.getProperty("os.name", "unknown");
        String osVersion = System.getProperty("os.version", "");
        return "SunriseDentalDesktop/1.0 (Java " + java + "; " + os + " " + osVersion + ")";
    }
}
