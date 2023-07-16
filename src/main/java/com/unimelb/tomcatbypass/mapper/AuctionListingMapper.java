package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.enums.Condition;
import com.unimelb.tomcatbypass.model.AuctionListing;
import com.unimelb.tomcatbypass.model.DomainObject;
import com.unimelb.tomcatbypass.model.SellerGroup;
import com.unimelb.tomcatbypass.utils.ModelSetter;
import com.unimelb.tomcatbypass.utils.QueryExecutor;
import com.unimelb.tomcatbypass.utils.QuerySetter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class AuctionListingMapper extends DataMapper implements ListingMapper<AuctionListing> {
    private static final Logger log = Logger.getLogger(AuctionListingMapper.class.getName());

    private final ModelSetter<AuctionListing> eagerMS = rs -> {
        UUID sellerGroupId = rs.getObject("sg_id", UUID.class);
        SellerGroup sellerGroup = new SellerGroupMapper().findBySgId(sellerGroupId);
        return new AuctionListing(
                rs.getObject("listing_id", UUID.class),
                sellerGroup,
                rs.getTimestamp("create_timestamp"),
                rs.getString("description"),
                Condition.valueOf(rs.getString("condition")),
                rs.getBigDecimal("start_price"),
                rs.getTimestamp("end_timestamp"));
    };

    @Override
    public List<AuctionListing> findInLimitOffset(Integer limit, Integer offset) {
        String query = "SELECT * FROM auction_listing LIMIT ? OFFSET ?;";
        List<Object> params = Arrays.asList(limit, offset);
        return new QueryExecutor<AuctionListing>().findAllObjectsBy(query, params, eagerMS);
    }

    @Override
    public List<AuctionListing> findByUsernameInLimitOffset(String username, Integer limit, Integer offset) {
        String query = ""
                + "SELECT al.listing_id, al.sg_id, al.create_timestamp, al.start_price, al.description, al.end_timestamp, al.condition "
                + "FROM auction_listing al JOIN seller_group sg ON sg.sg_id = al.sg_id "
                + "JOIN user_sg_mapping usm ON sg.sg_id = usm.sg_id "
                + "WHERE usm.username = ? "
                + "LIMIT ? OFFSET ?; ";
        List<Object> params = Arrays.asList(username, limit, offset);
        return new QueryExecutor<AuctionListing>().findAllObjectsBy(query, params, eagerMS);
    }

    @Override
    public List<AuctionListing> findByDescriptionInLimitOffset(String description, Integer limit, Integer offset) {
        String query = "SELECT * from auction_listing " + "WHERE lower(description) LIKE ? " + "LIMIT ? OFFSET ?; ";
        List<Object> params = Arrays.asList("%" + description.toLowerCase() + "%", limit, offset);
        return new QueryExecutor<AuctionListing>().findAllObjectsBy(query, params, eagerMS);
    }

    @Override
    public List<AuctionListing> findAllLazy() {
        String query = "SELECT listing_id FROM auction_listing;";
        ModelSetter<AuctionListing> lazyMS = rs -> new AuctionListing(rs.getObject("listing_id", UUID.class));
        return new QueryExecutor<AuctionListing>().findAllObjects(query, lazyMS);
    }

    @Override
    public List<AuctionListing> findAllEager() {
        String query = "SELECT * FROM auction_listing;";
        return new QueryExecutor<AuctionListing>().findAllObjects(query, eagerMS);
    }

    @Override
    public AuctionListing findByListingId(UUID id) {
        String query = "SELECT * FROM auction_listing WHERE listing_id = ?;";
        List<Object> params = Collections.singletonList(id);
        return new QueryExecutor<AuctionListing>().findSingleObjectByParams(query, params, eagerMS);
    }

    public List<AuctionListing> findByBidder(String username) {
        String query = "SELECT * FROM auction_listing WHERE listing_id IN "
                + "(SELECT auction_listing.listing_id as listing_id "
                + "FROM auction_listing JOIN bid b on auction_listing.listing_id = b.listing_id "
                + "WHERE b.username = ?);";
        List<Object> params = Collections.singletonList(username);
        return new QueryExecutor<AuctionListing>().findAllObjectsBy(query, params, eagerMS);
    }

    public List<AuctionListing> findAllActive() {
        String query = "SELECT * FROM auction_listing WHERE end_timestamp > NOW();";
        return new QueryExecutor<AuctionListing>().findAllObjects(query, eagerMS);
    }

    public List<AuctionListing> findAllActiveByText(String text) {
        String query = "SELECT * FROM auction_listing WHERE end_timestamp > NOW() AND lower(description) LIKE ?;";
        List<Object> params = Collections.singletonList("%" + text.toLowerCase() + "%");
        return new QueryExecutor<AuctionListing>().findAllObjectsBy(query, params, eagerMS);
    }

    public List<AuctionListing> findByDescription(String description) {
        String query = "SELECT * FROM auction_listing WHERE description = ?;";
        List<Object> params = Collections.singletonList(description);
        return new QueryExecutor<AuctionListing>().findAllObjectsBy(query, params, eagerMS);
    }

    @Override
    protected void insert(DomainObject obj, Connection conn) throws SQLException {
        AuctionListing listing = (AuctionListing) obj;
        String query = "INSERT INTO auction_listing VALUES (?, ?, ?, ?, ?, ?, ?);";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, listing.getListingId());
            statement.setObject(nthPlaceholder++, listing.getSellerGroup().getSgId());
            statement.setTimestamp(nthPlaceholder++, listing.getCreateTimestamp());
            statement.setBigDecimal(nthPlaceholder++, listing.getStartPrice());
            statement.setString(nthPlaceholder++, listing.getDescription());
            statement.setTimestamp(nthPlaceholder++, listing.getEndTimestamp());
            statement.setString(nthPlaceholder, listing.getCondition().name());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void update(DomainObject obj, Connection conn) throws SQLException {
        AuctionListing listing = (AuctionListing) obj;
        String query = "UPDATE auction_listing SET " + "sg_id=?,"
                + "create_timestamp=?,"
                + "start_price=?,"
                + "description=?,"
                + "end_timestamp=?,"
                + "condition=? "
                + "WHERE listing_id = ?;";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, listing.getSellerGroup().getSgId());
            statement.setTimestamp(nthPlaceholder++, listing.getCreateTimestamp());
            statement.setBigDecimal(nthPlaceholder++, listing.getStartPrice());
            statement.setString(nthPlaceholder++, listing.getDescription());
            statement.setTimestamp(nthPlaceholder++, listing.getEndTimestamp());
            statement.setString(nthPlaceholder++, listing.getCondition().name());
            statement.setObject(nthPlaceholder, listing.getListingId());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void delete(DomainObject obj, Connection conn) throws SQLException {
        AuctionListing listing = (AuctionListing) obj;
        String query = "DELETE FROM auction_listing WHERE listing_id=?;";
        QuerySetter qs = statement -> statement.setObject(1, listing.getListingId());
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }
}
