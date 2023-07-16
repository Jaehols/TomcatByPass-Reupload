package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.model.DomainObject;
import com.unimelb.tomcatbypass.utils.ModelSetter;
import com.unimelb.tomcatbypass.utils.QueryExecutor;
import com.unimelb.tomcatbypass.utils.QuerySetter;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class AppUserMapper extends DataMapper {
    private static final Logger log = Logger.getLogger(AppUserMapper.class.getName());

    public AppUser findByUsername(String name) {
        String query = "SELECT * FROM app_user WHERE username = ?";
        List<Object> params = Collections.singletonList(name);
        return new QueryExecutor<AppUser>().findSingleObjectByParams(query, params, ms);
    }

    public List<AppUser> findAllUsers() {
        String query = "SELECT * FROM app_user";
        return new QueryExecutor<AppUser>().findAllObjects(query, ms);
    }

    public List<AppUser> findBySellerGroupId(UUID sgId) {
        String query = "SELECT app_user.username, create_timestamp, "
                + "email, pwd, role, address FROM app_user "
                + "JOIN user_sg_mapping usm on app_user.username = usm.username "
                + "JOIN seller_group sg on usm.sg_id = sg.sg_id "
                + "WHERE sg.sg_id=?;";
        List<Object> params = Collections.singletonList(sgId);
        return new QueryExecutor<AppUser>().findAllObjectsBy(query, params, ms);
    }

    public List<AppUser> findAllUsersByText(String text) {
        String query = "SELECT * FROM app_user WHERE lower(username) LIKE ?;";
        List<Object> params = Collections.singletonList("%" + text.toLowerCase() + "%");
        return new QueryExecutor<AppUser>().findAllObjectsBy(query, params, ms);
    }

    private final ModelSetter<AppUser> ms = rs -> {
        String username = rs.getObject("username", String.class);
        Timestamp createTimestamp = rs.getObject("create_timestamp", Timestamp.class);
        String email = rs.getObject("email", String.class);
        String pwd = rs.getObject("pwd", String.class);
        String ROLE = rs.getObject("role", String.class);
        String address = rs.getObject("address", String.class);
        return new AppUser(username, createTimestamp, email, pwd, ROLE, address);
    };

    @Override
    protected void insert(DomainObject obj, Connection conn) throws SQLException {
        AppUser user = (AppUser) obj;
        String query = "INSERT INTO app_user (username, create_timestamp, email, pwd, role, address) "
                + "VALUES (?, ?, ?, ?, ?, ?);";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setString(nthPlaceholder++, user.getUsername());
            statement.setTimestamp(nthPlaceholder++, user.getCreateTimestamp());
            statement.setString(nthPlaceholder++, user.getEmail());
            statement.setString(nthPlaceholder++, user.getPwd());
            statement.setString(nthPlaceholder++, user.getROLE());
            statement.setString(nthPlaceholder, user.getAddress());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void update(DomainObject obj, Connection conn) throws SQLException {
        AppUser user = (AppUser) obj;
        String query = "UPDATE app_user SET "
                + "create_timestamp=?,"
                + "email=?,"
                + "pwd=?,"
                + "role=?,"
                + "address=? "
                + "WHERE username = ?;";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setTimestamp(nthPlaceholder++, user.getCreateTimestamp());
            statement.setString(nthPlaceholder++, user.getEmail());
            statement.setString(nthPlaceholder++, user.getPwd());
            statement.setString(nthPlaceholder++, user.getROLE());
            statement.setString(nthPlaceholder++, user.getAddress());
            statement.setString(nthPlaceholder, user.getUsername());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void delete(DomainObject obj, Connection conn) throws SQLException {
        AppUser user = (AppUser) obj;
        String query = "DELETE FROM app_user WHERE username=?;";
        QuerySetter qs = statement -> statement.setObject(1, user.getUsername());
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }
}
