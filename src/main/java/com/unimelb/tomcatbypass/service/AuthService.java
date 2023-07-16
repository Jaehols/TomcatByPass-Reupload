package com.unimelb.tomcatbypass.service;

import com.unimelb.tomcatbypass.enums.Roles;
import com.unimelb.tomcatbypass.mapper.AppUserMapper;
import com.unimelb.tomcatbypass.mapper.SellerGroupMapper;
import com.unimelb.tomcatbypass.mapper.UserSgMappingMapper;
import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.model.SellerGroup;
import com.unimelb.tomcatbypass.model.UserSgMapping;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger log = Logger.getLogger(AuthService.class.getName());

    public static boolean checkUserSellerGroupPermission(String username, UUID sgId) {
        return isUserAdmin(username) || isUserInSellerGroup(username, sgId);
    }

    public static boolean isUserAdmin(String username) {
        AppUser user = new AppUserMapper().findByUsername(username);
        return user.getROLE().equals(Roles.ADMIN.name());
    }

    public static boolean isUserInSellerGroup(String username, UUID sgId) {
        UserSgMapping userSgMapping = new UserSgMappingMapper().findUserSgMapping(username, sgId);
        return userSgMapping != null;
    }
}
