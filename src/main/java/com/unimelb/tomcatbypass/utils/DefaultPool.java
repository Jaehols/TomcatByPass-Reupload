package com.unimelb.tomcatbypass.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultPool extends Pool {
    private static final Logger log = Logger.getLogger(DefaultPool.class.getName());

    private DefaultPool() {}

    static {
        if (!hikariEnabled) {
            log.info("Configuring new database connection pool.");
            Instant startTime = Instant.now();

            if (!registerDriverWithSuccess()) {
                System.exit(1);
            }

            long timeElapsed = Duration.between(startTime, Instant.now()).getSeconds();
            log.info("--------------------------------------------------------------");
            log.info("Finished configuring " + DefaultPool.class.getName() + " in " + timeElapsed + " seconds.");
            log.info("--------------------------------------------------------------");
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            log.info("New connection to db.");
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return conn;
    }

    public static void testConnection() {
        try (Connection test = DriverManager.getConnection(url, user, password)) {
            log.info("Got first testConnection successfully.");
        } catch (SQLException e) {
            log.info("Failed to get first testConnection.");
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
