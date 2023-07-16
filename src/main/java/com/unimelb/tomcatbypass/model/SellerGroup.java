package com.unimelb.tomcatbypass.model;

import com.unimelb.tomcatbypass.mapper.UnitOfWork;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@ToString
public class SellerGroup implements DomainObject {
    private UUID sgId;
    private String name;
    private List<AppUser> appUsers;

    @Builder
    public SellerGroup(UUID sgId, String name, List<AppUser> appUsers) {
        this.sgId = sgId;
        this.name = name;
        this.appUsers = appUsers;
    }

    public void setName(String name) {
        this.name = name;
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }

    public void addUser(AppUser appUser) {
        appUsers.add(appUser);
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }

    public void removeUser(AppUser appUser) {
        appUsers.remove(appUser);
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow != null) {
            uow.registerDirty(this);
        }
    }
}
