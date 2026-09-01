package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton connection manager for the application's MySQL database.
 *
 * Exactly one {@code DatabaseConnection} instance ever exists — created
 * lazily, thread-safely, on first use (double-checked locking via
 * {@link #getInstance()}) — and it's the single point that loads
 * {@code db.properties} and knows how to reach the database.
 *
 * What's deliberately <em>not</em> a singleton is the JDBC
 * {@link Connection} itself: {@link #openConnection()} hands back a fresh
 * {@code Connection} on every call, the same as before. A single shared
 * {@code Connection} reused across this app's many concurrent Swing
 * screens/dialogs would not be safe (plain JDBC {@code Connection}s aren't
 * meant for concurrent multi-threaded use) and could silently go stale.
 * Every DAO already opens its {@code Connection} in a try-with-resources
 * block and closes it when done — that pooling-free, connection-per-call
 * pattern is unchanged here; only how the *manager* itself is obtained is
 * now a proper Singleton.
 *
 * The static {@link #getConnection()} method is kept for source
 * compatibility — every DAO in this project already calls
 * {@code DatabaseConnection.getConnection()} directly, so none of them
 * needed to change; it simply routes through {@link #getInstance()} now.
 *
 * @author oveen
 */
public final class DatabaseConnection {

    private static final String CONFIG_FILE = "/db/db.properties";

    /**
     * The single shared instance. {@code volatile} so a partially-constructed
     * instance can never be visible to another thread racing into
     * {@link #getInstance()} at the same time.
     */
    private static volatile DatabaseConnection instance;

    private final Properties config;

    private DatabaseConnection() {
        this.config = loadConfig();
    }

    /**
     * Returns the single shared {@code DatabaseConnection} instance,
     * creating it on the first call. Double-checked locking: the common
     * case (instance already exists) never pays for the {@code synchronized}
     * block at all.
     */
    public static DatabaseConnection getInstance() {
        DatabaseConnection result = instance;
        if (result == null) {
            synchronized (DatabaseConnection.class) {
                result = instance;
                if (result == null) {
                    instance = result = new DatabaseConnection();
                }
            }
        }
        return result;
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

    /** Opens a brand-new {@link Connection} using this singleton's loaded config — see the class javadoc for why this isn't itself a shared/reused Connection. */
    public Connection openConnection() throws SQLException {
        String driver = config.getProperty("db.driver");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC driver \"" + driver + "\" not found on the classpath. "
                    + "Add its .jar under Project Properties → Libraries (e.g. mysql-connector-j).", e);
        }
        return DriverManager.getConnection(
                config.getProperty("db.url"),
                config.getProperty("db.user"),
                config.getProperty("db.password"));
    }

    /** Convenience static entry point, kept so every existing call site (every DAO) needs no changes — delegates to {@link #getInstance()}. */
    public static Connection getConnection() throws SQLException {
        return getInstance().openConnection();
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
