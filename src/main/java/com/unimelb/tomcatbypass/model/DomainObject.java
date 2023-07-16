package com.unimelb.tomcatbypass.model;

import com.unimelb.tomcatbypass.mapper.UnitOfWork;

public interface DomainObject {
    default void create() {
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerNew(this);
        }
    }

    default void delete() {
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDeleted(this);
        }
    }
}
