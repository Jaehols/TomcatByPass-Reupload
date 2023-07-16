package com.unimelb.tomcatbypass.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Intended as a wrapper around database connections to prevent opening more than one in a given transaction.
 * Allows us to set transaction properties on a db connection without having to pass it around as a method parameter.
 */
public class Session {
    private static final Logger log = Logger.getLogger(Session.class.getName());
    private static final ThreadLocal<Connection> current = new ThreadLocal<>();

    /**
     * This will start a new session. If there is an existing session, it is closed!
     */
    public static void startSession() {
        if (current.get() != null) {
            log.warning("Started session but there was already a session open. This shouldn't be the case.");
            closeSession();
        }
        if (Pool.hikariEnabled) {
            current.set(HikariPool.getConnection());
        } else {
            current.set(DefaultPool.getConnection());
        }
    }

    /**
     * Gets the current session. This will be null if there is no session!
     * @return Connection, or null
     */
    public static Connection getSession() {
        return current.get();
    }

    /**
     * Gets the current session. Creates a new session if there is no session!
     * @return Connection, never null!
     */
    public static Connection forceGetSession() {
        if (current.get() == null) {
            log.info("forceGetSession found no active session for this thread and is about to create one");
            startSession();
        }
        return current.get();
    }

    /**
     * Clean up the current session, and set it to null!
     * This is idempotent; calling it on a null session does nothing.
     */
    public static void closeSession() {
        if (current.get() != null) {
            try {
                current.get().close();
            } catch (SQLException e) {
                log.log(Level.WARNING, "Failed to close connection.");
            }
        }
        current.set(null);
    }
}
