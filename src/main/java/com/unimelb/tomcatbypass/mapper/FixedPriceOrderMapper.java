package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.model.*;
import com.unimelb.tomcatbypass.utils.ModelSetter;
import com.unimelb.tomcatbypass.utils.QueryExecutor;
import com.unimelb.tomcatbypass.utils.QuerySetter;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class FixedPriceOrderMapper extends DataMapper {
    private static final Logger log = Logger.getLogger(FixedPriceOrderMapper.class.getName());

    private final ModelSetter<FixedPriceOrder> ms = rs -> {
        UUID order_id = rs.getObject("order_id", UUID.class);
        String username = rs.getObject("username", String.class);
        UUID listing_id = rs.getObject("listing_id", UUID.class);
        Timestamp create_timestamp = rs.getObject("create_timestamp", Timestamp.class);
        String address = rs.getObject("address", String.class);
        Integer quantity = rs.getObject("quantity", Integer.class);
        BigDecimal value = rs.getObject("total", BigDecimal.class);

        AppUser user = new AppUserMapper().findByUsername(username);
        FixedListing listing = new FixedListingMapper().findByListingId(listing_id);

        return new FixedPriceOrder(order_id, user, listing, create_timestamp, address, quantity, value);
    };

    public List<FixedPriceOrder> getAll() {
        String query = "SELECT * FROM fixed_order;";
        return new QueryExecutor<FixedPriceOrder>().findAllObjects(query, ms);
    }

    public List<FixedPriceOrder> getByUser(String username) {
        String query = "SELECT * FROM fixed_order WHERE username=?";
        List<Object> params = Collections.singletonList(username);
        return new QueryExecutor<FixedPriceOrder>().findAllObjectsBy(query, params, ms);
    }


    public List<FixedPriceOrder> getBySgId(UUID sg_id) {
        // TODO need to remap sgId shouldnt be in the object
        String query = "SELECT fo.* FROM seller_group "
                + "JOIN fixed_listing al on seller_group.sg_id = al.sg_id "
                + "JOIN fixed_order fo on al.listing_id = fo.listing_id "
                + "WHERE seller_group.sg_id=?;";
        List<Object> params = Collections.singletonList(sg_id);
        return new QueryExecutor<FixedPriceOrder>().findAllObjectsBy(query, params, ms);
    }

    public FixedPriceOrder findFixedPriceOrderById(UUID order_id) {
        String query = "SELECT * FROM fixed_order WHERE order_id = ?";
        List<Object> params = Collections.singletonList(order_id);
        return new QueryExecutor<FixedPriceOrder>().findSingleObjectByParams(query, params, ms);
    }

    @Override
    protected void insert(DomainObject obj, Connection conn) throws SQLException {
        FixedPriceOrder order = (FixedPriceOrder) obj;
        String query = "INSERT INTO fixed_order VALUES (?, ?, ?, ?, ?, ?, ?);";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, order.getOrderId());
            statement.setObject(nthPlaceholder++, order.getUser().getUsername());
            statement.setObject(nthPlaceholder++, order.getListing().getListingId());
            statement.setTimestamp(nthPlaceholder++, order.getCreateTimestamp());
            statement.setInt(nthPlaceholder++, order.getQuantity());
            statement.setBigDecimal(nthPlaceholder++, order.getTotal());
            statement.setString(nthPlaceholder, order.getAddress());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void update(DomainObject obj, Connection conn) throws SQLException {
        FixedPriceOrder order = (FixedPriceOrder) obj;
        String query = "UPDATE fixed_order SET " + "username=?,"
                + "listing_id=?,"
                + "create_timestamp=?,"
                + "quantity=?,"
                + "total=?,"
                + "address=? "
                + "WHERE order_id = ? AND ?>=1;";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, order.getUser().getUsername());
            statement.setObject(nthPlaceholder++, order.getListing().getListingId());
            statement.setTimestamp(nthPlaceholder++, order.getCreateTimestamp());
            statement.setInt(nthPlaceholder++, order.getQuantity());
            statement.setBigDecimal(nthPlaceholder++, order.getTotal());
            statement.setString(nthPlaceholder++, order.getAddress());
            statement.setObject(nthPlaceholder++, order.getOrderId());
            statement.setObject(nthPlaceholder, order.getQuantity());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void delete(DomainObject obj, Connection conn) throws SQLException {
        FixedPriceOrder order = (FixedPriceOrder) obj;
        String query = "DELETE FROM fixed_order WHERE order_id=?;";
        QuerySetter qs = statement -> statement.setObject(1, order.getOrderId());
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }
}
