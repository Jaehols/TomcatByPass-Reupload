package com.unimelb.tomcatbypass.service;

import com.unimelb.tomcatbypass.mapper.SellerGroupMapper;
import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.model.SellerGroup;
import com.unimelb.tomcatbypass.utils.ManagedTransaction;
import com.unimelb.tomcatbypass.utils.OperationException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;

public class SellerGroupService {
    private static final Logger log = Logger.getLogger(SellerGroupService.class.getName());
    private static final int NO_RETRIES = 0;

    @AllArgsConstructor
    public static class ValidatedSellerGroupParams {
        String name;
    }

    public static SellerGroup findBySgId(UUID id) {
        return new SellerGroupMapper().findBySgId(id);
    }

    public static List<SellerGroup> findAllSellerGroups() {
        return new SellerGroupMapper().findAllSellerGroups();
    }

    public static List<SellerGroup> findSellerGroupsByAppUsername(String username) {
        return new SellerGroupMapper().findSellerGroupsByAppUsername(username);
    }

    public static void saveNewSellerGroup(ValidatedSellerGroupParams params, String username) {
        new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                if(params.name == null || username == null) {
                    throw new OperationException("Either username=" + username + " or name=" +
                            params.name + " is null so the seller group can't be created");
                }

                UUID sgId = UUID.randomUUID();
                AppUser appUser = AppUserService.findByUsername(username);
                List<AppUser> appUsers = new ArrayList<>();
                appUsers.add(appUser);
                SellerGroup sellerGroup = SellerGroup.builder()
                        .sgId(sgId)
                        .name(params.name)
                        .appUsers(appUsers)
                        .build();
                log.info("SellerGroup to save to db: " + sellerGroup);
                sellerGroup.create();
            }
        }.executeTransaction(Connection.TRANSACTION_READ_COMMITTED, NO_RETRIES);
    }

    public static Boolean deleteSellerGroup(UUID sgId, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {

                if (sgId == null || username == null ||
                        (!AuthService.checkUserSellerGroupPermission(username, sgId))) {
                    throw new OperationException("username=" + username
                            + " does not have permission to delete seller group with ID=" + sgId);
                }

                SellerGroup sellerGroup = new SellerGroupMapper().findBySgId(sgId);
                sellerGroup.delete();
            }
        }.executeTransaction(Connection.TRANSACTION_READ_COMMITTED, NO_RETRIES);
    }
}
