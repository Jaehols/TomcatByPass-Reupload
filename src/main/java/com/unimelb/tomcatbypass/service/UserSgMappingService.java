package com.unimelb.tomcatbypass.service;

import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.model.SellerGroup;
import com.unimelb.tomcatbypass.model.UserSgMapping;
import com.unimelb.tomcatbypass.utils.ManagedTransaction;
import com.unimelb.tomcatbypass.utils.OperationException;
import java.sql.Connection;
import java.util.UUID;
import java.util.logging.Logger;

public class UserSgMappingService {
    private static final Logger log = Logger.getLogger(UserSgMappingService.class.getName());
    private static final int NO_RETRIES = 0;

    public static void insertMapping(String username, UUID sgId, String loggedInUser) {
        new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                if (loggedInUser == null
                        || sgId == null
                        || username == null
                        || (!AuthService.checkUserSellerGroupPermission(loggedInUser, sgId))) {
                    throw new OperationException(
                            "username=" + loggedInUser + " does not have permission to add user with username="
                                    + username + " from seller group with ID="
                                    + sgId);
                }
                AppUser appUser = AppUserService.findByUsername(username);
                SellerGroup sellerGroup = SellerGroupService.findBySgId(sgId);

                UserSgMapping userSgMapping = UserSgMapping.builder()
                        .appUser(appUser)
                        .sellerGroup(sellerGroup)
                        .build();
                log.info("UserSgMapping to save to db: " + userSgMapping);
                userSgMapping.create();
            }
            // TODO Double check if this serialization level is correct
        }.executeTransaction(Connection.TRANSACTION_READ_COMMITTED, NO_RETRIES);
    }

    public static Boolean deleteMapping(String username, UUID sgId, String loggedInUser) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                if (loggedInUser == null
                        || sgId == null
                        || username == null
                        || (!AuthService.checkUserSellerGroupPermission(loggedInUser, sgId))) {
                    throw new OperationException(
                            "username=" + loggedInUser + " does not have permission to remove user with username="
                                    + username + " from seller group with ID="
                                    + sgId);
                }
                AppUser appUser = AppUserService.findByUsername(username);
                SellerGroup sellerGroup = SellerGroupService.findBySgId(sgId);
                UserSgMapping userSgMapping = UserSgMapping.builder()
                        .appUser(appUser)
                        .sellerGroup(sellerGroup)
                        .build();
                log.info("UserSgMapping to delete from db: " + userSgMapping);
                userSgMapping.delete();
            }
        }.executeTransaction(Connection.TRANSACTION_READ_COMMITTED, NO_RETRIES);
    }
}
