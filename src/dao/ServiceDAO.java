package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data access for the {@code services} table — Billing's master service
 * price list (OS_BM_2's "Clinical Charges" dropdown, editable live from
 * OS_BM_Service's "Add Services" screen).
 *
 * @author oveen
 */
public class ServiceDAO {

    /** name -> price, ordered alphabetically for a stable dropdown order. */
    public Map<String, Double> findAll() {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = "SELECT service_name, price FROM services ORDER BY service_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("service_name"), rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.err.println("[ServiceDAO] findAll failed: " + e.getMessage());
        }
        return map;
    }

    /** Adds a new service, or updates the price of an existing one. */
    public void upsert(String name, double price) {
        String sql = "INSERT INTO services (service_name, price) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE price = VALUES(price)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ServiceDAO] upsert failed for " + name + ": " + e.getMessage());
        }
    }

    public void delete(String name) {
        String sql = "DELETE FROM services WHERE service_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ServiceDAO] delete failed for " + name + ": " + e.getMessage());
        }
    }
}
