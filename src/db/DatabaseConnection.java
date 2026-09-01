package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 *
 * @author oveen
 */
public final class DatabaseConnection {

    private static final String CONFIG_FILE = "/db/db.properties";
    private static final Properties CONFIG = loadConfig();

    private DatabaseConnection() {
        
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class.getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + CONFIG_FILE + " on the classpath.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + CONFIG_FILE, e);
        }
        return props;
    }

    
    public static Connection getConnection() throws SQLException {
        String driver = CONFIG.getProperty("db.driver");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC driver \"" + driver + "\" not found on the classpath. "
                    + "Add its .jar under Project Properties → Libraries (e.g. mysql-connector-j).", e);
        }
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.password"));
    }

    
    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            boolean ok = c != null && !c.isClosed();
            System.out.println("[DatabaseConnection] Connected: " + ok);
            return ok;
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Connection failed: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        testConnection();
    }
}
