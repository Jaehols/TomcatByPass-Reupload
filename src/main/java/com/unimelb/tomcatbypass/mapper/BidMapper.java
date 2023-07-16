package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.model.AuctionListing;
import com.unimelb.tomcatbypass.model.Bid;
import com.unimelb.tomcatbypass.model.DomainObject;
import com.unimelb.tomcatbypass.utils.ModelSetter;
import com.unimelb.tomcatbypass.utils.QueryExecutor;
import com.unimelb.tomcatbypass.utils.QuerySetter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

public class BidMapper extends DataMapper {
    private static final Logger log = Logger.getLogger(BidMapper.class.getName());

    private final ModelSetter<Bid> ms = rs -> {
        UUID bid_id = rs.getObject("bid_id", UUID.class);
        String username = rs.getObject("username", String.class);
        UUID listing_id = rs.getObject("listing_id", UUID.class);
        Timestamp create_timestamp = rs.getObject("create_timestamp", Timestamp.class);
        BigDecimal value = rs.getObject("value", BigDecimal.class);

        AppUser user = new AppUserMapper().findByUsername(username);
        AuctionListing listing = new AuctionListingMapper().findByListingId(listing_id);

        return new Bid(bid_id, user, listing, create_timestamp, value);
    };

    public List<Bid> findAllForAuctionListing(UUID listing_id) {
        String query = "SELECT * FROM bid WHERE listing_id =?;";
        List<Object> params = Collections.singletonList(listing_id);
        return new QueryExecutor<Bid>().findAllObjectsBy(query, params, ms);
    }

    public Bid findHighestBidForAuctionListing(UUID listing_id) {
        String query = "SELECT * FROM bid WHERE listing_id = ? ORDER BY value DESC, create_timestamp ASC LIMIT 1;";
        List<Object> params = Collections.singletonList(listing_id);
        return new QueryExecutor<Bid>().findSingleObjectByParams(query, params, ms);
    }

    public Bid findById(UUID bidId) {
        String query = "SELECT * FROM bid WHERE bid_id = ?;";
        List<Object> params = Collections.singletonList(bidId);
        return new QueryExecutor<Bid>().findSingleObjectByParams(query, params, ms);
    }

    @Override
    protected void insert(DomainObject obj, Connection conn) throws SQLException {
        Bid bid = (Bid) obj;
        String query = "INSERT INTO bid VALUES (?, ?, ?, ?, ?);";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, bid.getBidId());
            statement.setObject(nthPlaceholder++, bid.getUser().getUsername());
            statement.setObject(nthPlaceholder++, bid.getListing().getListingId());
            statement.setTimestamp(nthPlaceholder++, bid.getCreateTimestamp());
            statement.setBigDecimal(nthPlaceholder, bid.getValue());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void update(DomainObject obj, Connection conn) throws SQLException {
        Bid bid = (Bid) obj;
        String query = "UPDATE bid SET " + "username=?,"
                + "listing_id=?,"
                + "create_timestamp=?,"
                + "value=? "
                + "WHERE bid_id = ?;";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, bid.getUser().getUsername());
            statement.setObject(nthPlaceholder++, bid.getListing().getListingId());
            statement.setTimestamp(nthPlaceholder++, bid.getCreateTimestamp());
            statement.setBigDecimal(nthPlaceholder++, bid.getValue());
            statement.setObject(nthPlaceholder, bid.getBidId());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void delete(DomainObject obj, Connection conn) throws SQLException {
        Bid bid = (Bid) obj;
        String query = "DELETE FROM bid WHERE bid_id=?;";
        QuerySetter qs = statement -> statement.setObject(1, bid.getBidId());
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }
}
