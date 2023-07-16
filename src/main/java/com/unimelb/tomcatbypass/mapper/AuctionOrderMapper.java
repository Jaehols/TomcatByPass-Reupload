package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.model.*;
import com.unimelb.tomcatbypass.utils.ModelSetter;
import com.unimelb.tomcatbypass.utils.QueryExecutor;
import com.unimelb.tomcatbypass.utils.QuerySetter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class AuctionOrderMapper extends DataMapper {
    private static final Logger log = Logger.getLogger(AuctionOrderMapper.class.getName());

    private final ModelSetter<AuctionOrder> ms = rs -> {
        UUID order_id = rs.getObject("order_id", UUID.class);
        String username = rs.getObject("username", String.class);
        UUID listing_id = rs.getObject("listing_id", UUID.class);
        Timestamp create_timestamp = rs.getObject("create_timestamp", Timestamp.class);
        String address = rs.getObject("address", String.class);
        UUID bid_id = rs.getObject("bid_id", UUID.class);

        AppUser user = new AppUserMapper().findByUsername(username);
        AuctionListing listing = new AuctionListingMapper().findByListingId(listing_id);
        Bid bid = new BidMapper().findById(bid_id);

        return new AuctionOrder(order_id, user, listing, create_timestamp, address, bid);
    };

    public List<AuctionOrder> getAll() {
        String query = "SELECT * FROM auction_order;";
        return new QueryExecutor<AuctionOrder>().findAllObjects(query, ms);
    }

    public List<AuctionOrder> getByUser(String username) {
        String query = "SELECT * FROM auction_order WHERE username=?";
        List<Object> params = Collections.singletonList(username);
        return new QueryExecutor<AuctionOrder>().findAllObjectsBy(query, params, ms);
    }

    public List<AuctionOrder> getBySgId(UUID sg_id) {
        // TODO need to remap sgId shouldnt be in the object
        String query = "SELECT ao.* FROM seller_group "
                + "JOIN auction_listing al on seller_group.sg_id = al.sg_id "
                + "JOIN auction_order ao on al.listing_id = ao.listing_id "
                + "WHERE seller_group.sg_id=?;";
        List<Object> params = Collections.singletonList(sg_id);
        return new QueryExecutor<AuctionOrder>().findAllObjectsBy(query, params, ms);
    }

    public AuctionOrder getByOrderId(UUID order_id) {
        String query = "SELECT * FROM auction_order WHERE order_id = ?";
        List<Object> params = Collections.singletonList(order_id);
        return new QueryExecutor<AuctionOrder>().findSingleObjectByParams(query, params, ms);
    }

    public boolean listingHasAuctionOrder(UUID listingId) {
        String query = "SELECT * FROM auction_order WHERE listing_id=?;";
        List<Object> params = Collections.singletonList(listingId);
        return !new QueryExecutor<AuctionOrder>().findAllObjectsBy(query, params, ms).isEmpty();
    }

    @Override
    protected void insert(DomainObject obj, Connection conn) throws SQLException {
        AuctionOrder order = (AuctionOrder) obj;
        String query = "INSERT INTO auction_order VALUES (?, ?, ?, ?, ?, ?);";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, order.getOrderId());
            statement.setObject(nthPlaceholder++, order.getUser().getUsername());
            statement.setObject(nthPlaceholder++, order.getListing().getListingId());
            statement.setObject(nthPlaceholder++, order.getBid().getBidId());
            statement.setTimestamp(nthPlaceholder++, order.getCreateTimestamp());
            statement.setString(nthPlaceholder, order.getAddress());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void update(DomainObject obj, Connection conn) throws SQLException {
        AuctionOrder order = (AuctionOrder) obj;
        String query = "UPDATE auction_order SET " + "username=?,"
                + "listing_id=?,"
                + "bid_id=?,"
                + "create_timestamp=?,"
                + "address=? "
                + "WHERE order_id = ?;";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, order.getUser().getUsername());
            statement.setObject(nthPlaceholder++, order.getListing().getListingId());
            statement.setObject(nthPlaceholder++, order.getBid().getBidId());
            statement.setTimestamp(nthPlaceholder++, order.getCreateTimestamp());
            statement.setString(nthPlaceholder++, order.getAddress());
            statement.setObject(nthPlaceholder, order.getOrderId());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void delete(DomainObject obj, Connection conn) throws SQLException {
        AuctionOrder order = (AuctionOrder) obj;
        String query = "DELETE FROM auction_order WHERE order_id=?;";
        QuerySetter qs = statement -> statement.setObject(1, order.getOrderId());
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }
}
