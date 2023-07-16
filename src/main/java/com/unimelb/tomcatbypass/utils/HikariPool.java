package com.unimelb.tomcatbypass.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides connections to the database.
 * It uses a connection pool:
 * <a href="https://github.com/brettwooldridge/HikariCP">HikariCP</a>
 */
public class HikariPool extends Pool {
    private static final Logger log = Logger.getLogger(HikariPool.class.getName());
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource = null;

    private HikariPool() {}

    static {
        if (hikariEnabled) {
            log.info("Configuring new database connection pool.");
            Instant startTime = Instant.now();

            if (!registerDriverWithSuccess()) {
                System.exit(1);
            }

            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);

            config.setAutoCommit(false);
            //        config.setConnectionTimeout(5000);

            dataSource = new HikariDataSource(config);

            long timeElapsed = Duration.between(startTime, Instant.now()).getSeconds();
            log.info("--------------------------------------------------------------");
            log.info("Finished configuring " + HikariPool.class.getName() + " in " + timeElapsed + " seconds.");
            log.info("--------------------------------------------------------------");
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            log.info("New connection to db.");
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return conn;
    }

    public static void testConnection() {
        try (Connection test = dataSource.getConnection()) {
            log.info("Got first testConnection successfully.");
        } catch (SQLException e) {
            log.info("Failed to get first testConnection.");
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static void shutdownHook() {
        dataSource.close();
        try {
            DriverManager.deregisterDriver(driver);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
