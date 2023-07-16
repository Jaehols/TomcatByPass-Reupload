package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.model.*;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.service.SellerGroupService;
import com.unimelb.tomcatbypass.utils.ModelSetter;
import com.unimelb.tomcatbypass.utils.QueryExecutor;
import com.unimelb.tomcatbypass.utils.QuerySetter;
import org.springframework.security.core.userdetails.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class UserSgMappingMapper extends DataMapper {
    Logger log = Logger.getLogger(UserSgMappingMapper.class.getName());

    public UserSgMapping findUserSgMapping(String username, UUID sgId) {
        String query = "SELECT * FROM user_sg_mapping WHERE username=? AND sg_id=?;";
        List<Object> params = Arrays.asList(username, sgId);
        return new QueryExecutor<UserSgMapping>().findSingleObjectByParams(query, params, ms);
    }

    private final ModelSetter<UserSgMapping> ms = rs -> {
        String username = rs.getString("username");
        UUID sgId = rs.getObject("sg_id", UUID.class);
        AppUser appUser = AppUserService.findByUsername(username);
        SellerGroup sellerGroup = SellerGroupService.findBySgId(sgId);
        return new UserSgMapping(appUser, sellerGroup);
    };

    @Override
    protected void insert(DomainObject obj, Connection conn) throws SQLException {
        UserSgMapping mapping = (UserSgMapping) obj;
        String query = "INSERT INTO user_sg_mapping (username, sg_id) VALUES (?, ?);";

        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, mapping.getAppUser().getUsername());
            statement.setObject(nthPlaceholder, mapping.getSellerGroup().getSgId());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void update(DomainObject obj, Connection conn) throws SQLException {
        log.severe("UserSgMappingMapper update method should never be called");
    }

    @Override
    protected void delete(DomainObject obj, Connection conn) throws SQLException {
        UserSgMapping mapping = (UserSgMapping) obj;
        String query = "DELETE FROM user_sg_mapping WHERE sg_id=? AND username=?;";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, mapping.getSellerGroup().getSgId());
            statement.setObject(nthPlaceholder, mapping.getAppUser().getUsername());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }
}
