package com.unimelb.tomcatbypass.service;

import com.unimelb.tomcatbypass.enums.Roles;
import com.unimelb.tomcatbypass.mapper.AppUserMapper;
import com.unimelb.tomcatbypass.model.AppUser;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import com.unimelb.tomcatbypass.utils.ManagedTransaction;
import com.unimelb.tomcatbypass.utils.OperationException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppUserService {
    private static final Logger log = Logger.getLogger(AppUserService.class.getName());

    @AllArgsConstructor
    public static class ValidatedUserParams {
        String username;
        String email;
        String password;
        String address;
    }

    public static AppUser findByUsername(String name) {
        return new AppUserMapper().findByUsername(name);
    }

    public static List<AppUser> getAllUsers() {
        return new AppUserMapper().findAllUsers();
    }

    public static List<AppUser> findBySellerGroupId(UUID sgId) {
        return new AppUserMapper().findBySellerGroupId(sgId);
    }

    public static List<AppUser> findAllUsersByText(String text) {
        return new AppUserMapper().findAllUsersByText(text);
    }

    public static boolean saveNewUser(ValidatedUserParams validatedUserParams) {
        return new ManagedTransaction() {

            @Override
            protected void doTransactionOperations() throws OperationException {

                // Check if username is available
                if (checkUserExists(validatedUserParams.username)) {
                    throw new OperationException("Cannot Create User Username Taken");
                }

                AppUser appUser = AppUser.builder()
                        .username(validatedUserParams.username)
                        .createTimestamp(Timestamp.from(Instant.now()))
                        .email(validatedUserParams.email)
                        .pwd(validatedUserParams.password)
                        .ROLE(Roles.USER.name())
                        .address(validatedUserParams.address)
                        .build();

                log.info("Trying to save AppUser to db: " + appUser);
                appUser.create();
            }
        }.executeTransaction(Connection.TRANSACTION_READ_COMMITTED, 0);
    }

    public static boolean checkUserExists(String username) {
        log.info("Checking if User Exists inside AppUserService");
        if (!(Objects.isNull(AppUserService.findByUsername(username)))) {
            log.info("Username taken");
            return true;
        } else {
            log.info("Username free");
            return false;
        }
    }
}
