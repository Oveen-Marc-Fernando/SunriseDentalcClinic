package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.CookieConsentModel;

/**
 * Data access for the {@code cookie_consents} table — backs
 * {@link controller.CookieConsentController}, which in turn is called from
 * {@code Public_Dashboard}'s cookie banner (recording an acceptance) and
 * {@code AD_OP_Cookies} (Administration's read-only view of them).
 *
 * @author oveen
 */
public class CookieConsentDAO {

    private static final String SELECT_ALL_SQL =
            "SELECT consent_id, device_id, ip_address, user_agent, first_seen "
            + "FROM cookie_consents ORDER BY first_seen DESC";

    /** Every recorded consent, most recent first. */
    public List<CookieConsentModel> findAll() {
        List<CookieConsentModel> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CookieConsentDAO] findAll failed: " + e.getMessage());
        }
        return rows;
    }

    /** Number of consents on record — AD_OP_Cookies' own badge dot. */
    public int count() {
        String sql = "SELECT COUNT(*) FROM cookie_consents";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("[CookieConsentDAO] count failed: " + e.getMessage());
            return 0;
        }
    }

    /** Records a new "Accept All" click. firstSeen is set to the current moment server-side, not passed in. */
    public boolean insert(String deviceId, String ipAddress, String userAgent) {
        String sql = "INSERT INTO cookie_consents (device_id, ip_address, user_agent, first_seen) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deviceId);
            ps.setString(2, ipAddress);
            ps.setString(3, userAgent);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CookieConsentDAO] insert failed for device " + deviceId + ": " + e.getMessage());
            return false;
        }
    }

    private static CookieConsentModel mapRow(ResultSet rs) throws SQLException {
        CookieConsentModel m = new CookieConsentModel();
        m.setConsentId(rs.getInt("consent_id"));
        m.setDeviceId(rs.getString("device_id"));
        m.setIpAddress(rs.getString("ip_address"));
        m.setUserAgent(rs.getString("user_agent"));
        Timestamp ts = rs.getTimestamp("first_seen");
        m.setFirstSeen(ts != null ? ts.toString().substring(0, 19) : "");
        return m;
    }
}
