package com.unimelb.tomcatbypass.utils;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Pool {
    private static final Logger log = Logger.getLogger(Pool.class.getName());
    protected static final String url = System.getenv("POSTGRES_URL");
    protected static final String user = System.getenv("POSTGRES_USER");
    protected static final String password = System.getenv("POSTGRES_PASSWORD");
    protected static final Boolean hikariEnabled = true;
    protected static Driver driver = null;

    protected static Boolean registerDriverWithSuccess() {
        boolean success = false;

        try {
            log.info("Registering postgresql driver.");

            driver = new org.postgresql.Driver();

            log.info("Driver Properties: " + driver.getClass().getName());

            DriverManager.registerDriver(driver);
            log.info("Successful postgresql driver registration.");
            success = true;

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (NullPointerException e) {
            log.log(Level.SEVERE, e.getMessage(), "No driver found for postgresql.");
        }
        return success;
    }
}
