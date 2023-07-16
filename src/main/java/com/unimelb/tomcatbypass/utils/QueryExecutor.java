package com.unimelb.tomcatbypass.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryExecutor<T> {
    private static final Logger log = Logger.getLogger(QueryExecutor.class.getName());
    private Connection session = null;
    private boolean closeWhenDone = false;

    private void setupConnection() {
        if (session == null) {
            session = Session.getSession();
            closeWhenDone = false;

            if (session == null) {
                session = Session.forceGetSession();
                closeWhenDone = true;
                log.severe("Ran a query outside of a session - this shouldn't happen; you have a bug.");
            }
        }
    }

    /**
     * Use this for queries with:
     * - SAVE
     * - UPDATE
     * - DELETE
     * @param qs needs to be passed in so that this method and class can remain generic.
     */
    public static void manipulateData(String query, QuerySetter qs, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        // For the parameters given, set all of them in the given query string.
        qs.setQueryParametersForDb(statement);
        statement.executeUpdate();
    }

    /**
     * This finds a single row in the db matching the given query with the given parameters. Returns null if no row
     * matches the query.
     * @param ms needs to be passed in so that this method and class can remain generic.
     */
    public T findSingleObjectByParams(String query, List<Object> params, ModelSetter<T> ms) {
        setupConnection();
        ResultSet rs = null;
        T output = null;

        try (PreparedStatement statement = session.prepareStatement(query)) {
            // For the parameters given, set all of them in the given query string.
            int i = 1;
            for (Object param : params) {
                statement.setObject(i, param);
                i++;
            }

            if (statement.execute()) {
                rs = statement.getResultSet();
                if (rs.next()) {
                    // Use the passed in method definition to set model object attributes from db.
                    output = ms.setModelAttributesFromDb(rs);
                }
            } else {
                log.log(Level.WARNING, "Execution of PreparedStatement returned false");
            }

        } catch (SQLException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        } finally {
            cleanup(rs);
        }
        return output;
    }

    /**
     * This finds all rows in the db matching the given query with parameters.
     * @param ms needs to be passed in so that this method and class can remain generic.
     */
    public List<T> findAllObjectsBy(String query, List<Object> params, ModelSetter<T> ms) {
        setupConnection();
        ResultSet rs = null;
        List<T> output = new ArrayList<>();

        try (PreparedStatement statement = session.prepareStatement(query)) {
            int i = 1;
            for (Object param : params) {
                statement.setObject(i, param);
                i++;
            }

            if (statement.execute()) {
                rs = statement.getResultSet();
                while (rs.next()) {
                    // Use the passed in method definition to set model object attributes from db.
                    output.add(ms.setModelAttributesFromDb(rs));
                }
            } else {
                log.log(Level.WARNING, "Execution of PreparedStatement returned false");
            }

        } catch (SQLException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        } finally {
            cleanup(rs);
        }
        return output;
    }

    /**
     * This finds all rows in the db matching the given query.
     * It assumes the query has no parameters.
     * @param ms needs to be passed in so that this method and class can remain generic.
     */
    public List<T> findAllObjects(String query, ModelSetter<T> ms) {
        setupConnection();
        ResultSet rs = null;
        List<T> output = new ArrayList<>();

        try (Statement statement = session.createStatement()) {
            rs = statement.executeQuery(query);
            while (rs.next()) {
                // Use the passed in method definition to set model object attributes from db.
                output.add(ms.setModelAttributesFromDb(rs));
            }

        } catch (SQLException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        } finally {
            cleanup(rs);
        }
        return output;
    }

    private void cleanup(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (session != null && closeWhenDone) {
                Session.closeSession();
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }

        session = null;
    }
}
