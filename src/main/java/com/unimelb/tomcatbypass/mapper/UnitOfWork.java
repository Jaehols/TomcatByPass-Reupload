package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.model.DomainObject;
import com.unimelb.tomcatbypass.utils.Session;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnitOfWork {
    private static final Logger log = Logger.getLogger(UnitOfWork.class.getName());
    private static final ThreadLocal<UnitOfWork> current = new ThreadLocal<>();

    public static void startTransaction() {
        current.set(new UnitOfWork());
    }

    public static UnitOfWork getCurrent() {
        return current.get();
    }

    private final List<DomainObject> newObjects = new ArrayList<>();
    private final List<DomainObject> dirtyObjects = new ArrayList<>();
    private final List<DomainObject> deletedObjects = new ArrayList<>();

    public void registerNew(DomainObject object) {
        if (newObjects.contains(object)) {
            log.warning("Attempted to register as NEW an object that is already NEW.");
            return;
        }
        if (dirtyObjects.contains(object)) {
            log.warning("Attempted to register as NEW an object that is already DIRTY.");
            return;
        }
        if (deletedObjects.contains(object)) {
            log.warning("Attempted to register as NEW an object that is already DELETED.");
            return;
        }
        newObjects.add(object);
    }

    public void registerDirty(DomainObject object) {
        if (deletedObjects.contains(object)) {
            log.warning("Attempted to register as DIRTY an object that is already DELETED.");
            return;
        }

        if (!newObjects.contains(object) && !dirtyObjects.contains(object)) {
            dirtyObjects.add(object);
        }
    }

    public void registerDeleted(DomainObject object) {
        if (newObjects.remove(object)) return; // if new, we don't need to remove from db
        dirtyObjects.remove(object);
        if (!deletedObjects.contains(object)) {
            deletedObjects.add(object);
        }
    }

    public boolean commit() {
        if (newObjects.isEmpty() && dirtyObjects.isEmpty() && deletedObjects.isEmpty()) {
            log.warning("Attempting to commit empty changes to the database!");
        }

        Connection session = Session.forceGetSession(); // This allows UOW to be used without having to start a session.
        boolean success = false;

        try {
            session.setAutoCommit(false);

            try {
                DataMapper mapper;
                for (DomainObject obj : newObjects) {
                    mapper = DataMapper.getMapper(obj.getClass());
                    if (mapper != null) mapper.insert(obj, session);
                }
                for (DomainObject obj : dirtyObjects) {
                    mapper = DataMapper.getMapper(obj.getClass());
                    if (mapper != null) mapper.update(obj, session);
                }
                for (DomainObject obj : deletedObjects) {
                    mapper = DataMapper.getMapper(obj.getClass());
                    if (mapper != null) mapper.delete(obj, session);
                }

                session.commit();
                success = true;

            } catch (SQLException e) {
                log.log(Level.INFO, e.getMessage(), e);
                session.rollback();
            }

        } catch (SQLException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }

        Session.closeSession();
        flushObjects();
        return success;
    }

    public void abort() {
        if (newObjects.isEmpty() && dirtyObjects.isEmpty() && deletedObjects.isEmpty()) {
            log.info("Aborting empty unit of work, not a big deal.");
        } else {
            log.info("Aborting unit of work with uncommitted changes.");
        }
        flushObjects();
        current.set(null);
    }

    private void flushObjects() {
        newObjects.clear();
        dirtyObjects.clear();
        deletedObjects.clear();
    }
}
