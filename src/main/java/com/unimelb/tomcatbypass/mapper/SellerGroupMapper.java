package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.model.DomainObject;
import com.unimelb.tomcatbypass.model.SellerGroup;
import com.unimelb.tomcatbypass.service.SellerGroupService;
import com.unimelb.tomcatbypass.utils.ModelSetter;
import com.unimelb.tomcatbypass.utils.QueryExecutor;
import com.unimelb.tomcatbypass.utils.QuerySetter;

import javax.management.Query;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class SellerGroupMapper extends DataMapper {
    Logger log = Logger.getLogger(SellerGroupMapper.class.getName());

    private final ModelSetter<SellerGroup> ms = rs -> {
        UUID sellerGroupId = rs.getObject("sg_id", UUID.class);
        String name = rs.getString("name");
        List<AppUser> users = new AppUserMapper().findBySellerGroupId(sellerGroupId);
        return new SellerGroup(sellerGroupId, name, users);
    };

    public List<SellerGroup> findAllSellerGroups() {
        String query = "SELECT * FROM seller_group;";
        return new QueryExecutor<SellerGroup>().findAllObjects(query, ms);
    }

    public SellerGroup findBySgId(UUID id) {
        String query = "SELECT * FROM seller_group WHERE sg_id = ?;";
        List<Object> params = Collections.singletonList(id);
        return new QueryExecutor<SellerGroup>().findSingleObjectByParams(query, params, ms);
    }

    public List<SellerGroup> findSellerGroupsByAppUsername(String username) {
        String query = "SELECT sg.sg_id AS sg_id, sg.name AS name "
                + "FROM user_sg_mapping usm JOIN seller_group sg ON sg.sg_id = usm.sg_id "
                + "WHERE usm.username = ?;";
        List<Object> params = Collections.singletonList(username);
        return new QueryExecutor<SellerGroup>().findAllObjectsBy(query, params, ms);
    }

    public UUID getFixedOrderSgId(UUID order_id) {
        String query = "SELECT sg.*\n" + "FROM fixed_order\n"
                + "    JOIN fixed_listing fl on fixed_order.listing_id = fl.listing_id\n"
                + "    JoIN seller_group sg on fl.sg_id = sg.sg_id\n"
                + "WHERE fixed_order.order_id = ?;";
        List<Object> params = Collections.singletonList(order_id);
        return new QueryExecutor<SellerGroup>()
                .findSingleObjectByParams(query, params, ms)
                .getSgId();
    }

    public UUID getAuctionOrderSgId(UUID order_id) {
        String query = "SELECT sg.*\n" + "FROM auction_order\n"
                + "    JOIN auction_listing al on auction_order.listing_id = al.listing_id\n"
                + "    JoIN seller_group sg on al.sg_id = sg.sg_id\n"
                + "WHERE auction_order.order_id = ?;";
        List<Object> params = Collections.singletonList(order_id);
        return new QueryExecutor<SellerGroup>()
                .findSingleObjectByParams(query, params, ms)
                .getSgId();
    }

    @Override
    protected void insert(DomainObject obj, Connection conn) throws SQLException {
        SellerGroup sg = (SellerGroup) obj;
        String query = "INSERT INTO seller_group (sg_id, name) VALUES (?, ?);";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, sg.getSgId());
            statement.setObject(nthPlaceholder, sg.getName());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);

        // A mapping must be added so that the creating user is considered part of the seller group
        String query2 = "INSERT INTO user_sg_mapping (username, sg_id) VALUES (?, ?);";
        QuerySetter qs2 = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, sg.getAppUsers().get(0).getUsername());
            statement.setObject(nthPlaceholder, sg.getSgId());
        };
        new QueryExecutor<>().manipulateData(query2, qs2, conn);
    }

    @Override
    protected void update(DomainObject obj, Connection conn) throws SQLException {
        SellerGroup sg = (SellerGroup) obj;
        String query = "UPDATE seller_group SET " + "name=? " + "WHERE sg_id = ?;";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, sg.getName());
            statement.setObject(nthPlaceholder, sg.getSgId());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void delete(DomainObject obj, Connection conn) throws SQLException {
        SellerGroup sg = (SellerGroup) obj;
        String query = "DELETE FROM seller_group WHERE sg_id=?;";
        QuerySetter qs = statement -> statement.setObject(1, sg.getSgId());
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }
}
