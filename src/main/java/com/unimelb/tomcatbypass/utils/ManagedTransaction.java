package com.unimelb.tomcatbypass.utils;

import com.unimelb.tomcatbypass.mapper.UnitOfWork;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ManagedTransaction {
    private static final Logger log = Logger.getLogger(ManagedTransaction.class.getName());

    /**
     * @param isolationLevel is the DB isolation level, read here:
     *  <a href="https://www.postgresql.org/docs/current/transaction-iso.html">Postgres Isolation Levels</a>
     * @param retries if you only want to execute this once, with no retries, pass in zero to this paramater.
     * @return boolean value telling you whether the transaction was successful.
     */
    public Boolean executeTransaction(int isolationLevel, int retries) {
        // We are going to do reads, so we have to start a Session before the UoW.
        Session.startSession();
        boolean success = false;
        try {
            Session.getSession().setAutoCommit(false);
            // you need to figure out what isolation level you need, read here:
            // https://www.postgresql.org/docs/current/transaction-iso.html
            Session.getSession().setTransactionIsolation(isolationLevel);

            // Here, we start the UoW transaction as we are starting to make changes.
            UnitOfWork.startTransaction();

            doTransactionOperations();

            // and here we actually push the changes. This should use the existing session and then close it.
            success = UnitOfWork.getCurrent().commit();

        } catch (SQLException e) {
            log.log(Level.WARNING, "Attempted to set isolation level and failed. " + e.getMessage());
        } catch (OperationException e) {
            log.log(Level.WARNING, "OperationException raised during transaction - this transaction will not be retried. " + e.getMessage());
            UnitOfWork.getCurrent().abort();
            // the transaction safely fails
            retries = 0;

        }
        Session.closeSession();

        // if we errored before committing or tried to commit and failed due to serialisation, retry
        // Note that if the preconditions are no longer met this will return without any effect - should be safe
        if (!success && retries > 0) {
            log.warning("Failed to execute transaction. Retrying with " + (retries - 1) + " retries left");
            success = executeTransaction(isolationLevel, retries - 1);
        }
        return success;
    }

    /**
     * You will need to use the following methods to keep the UOW informed of changes to Domain Objects:
     *     - create()
     *     - delete()
     */
    protected void doTransactionOperations() throws OperationException {
        throw new UnsupportedOperationException("You need to override this method!!!");
    }
}
