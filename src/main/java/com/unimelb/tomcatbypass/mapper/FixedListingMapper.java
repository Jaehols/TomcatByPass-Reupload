package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.enums.Condition;
import com.unimelb.tomcatbypass.model.DomainObject;
import com.unimelb.tomcatbypass.model.FixedListing;
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

public class FixedListingMapper extends DataMapper implements ListingMapper<FixedListing> {
    private static final Logger log = Logger.getLogger(FixedListingMapper.class.getName());

    private final ModelSetter<FixedListing> eagerMS = rs -> {
        UUID sellerGroupId = rs.getObject("sg_id", UUID.class);
        SellerGroup sellerGroup = new SellerGroupMapper().findBySgId(sellerGroupId);
        return new FixedListing(
                rs.getObject("listing_id", UUID.class),
                sellerGroup,
                rs.getTimestamp("create_timestamp"),
                rs.getString("description"),
                Condition.valueOf(rs.getString("condition")),
                rs.getBigDecimal("price"),
                rs.getInt("quantity"));
    };

    @Override
    public List<FixedListing> findInLimitOffset(Integer limit, Integer offset) {
        String query = "SELECT * FROM fixed_listing LIMIT ? OFFSET ?;";
        List<Object> params = Arrays.asList(limit, offset);
        return new QueryExecutor<FixedListing>().findAllObjectsBy(query, params, eagerMS);
    }

    @Override
    public List<FixedListing> findByUsernameInLimitOffset(String username, Integer limit, Integer offset) {
        String query = ""
                + "SELECT fl.listing_id, fl.sg_id, fl.create_timestamp, fl.price, fl.description, fl.condition, fl.quantity "
                + "FROM fixed_listing fl JOIN seller_group sg ON sg.sg_id = fl.sg_id "
                + "JOIN user_sg_mapping usm ON sg.sg_id = usm.sg_id "
                + "WHERE usm.username = ? "
                + "LIMIT ? OFFSET ?; ";
        List<Object> params = Arrays.asList(username, limit, offset);
        return new QueryExecutor<FixedListing>().findAllObjectsBy(query, params, eagerMS);
    }

    @Override
    public List<FixedListing> findByDescriptionInLimitOffset(String description, Integer limit, Integer offset) {
        String query = "SELECT * from fixed_listing " + "WHERE lower(description) LIKE ? " + "LIMIT ? OFFSET ?; ";
        List<Object> params = Arrays.asList("%" + description.toLowerCase() + "%", limit, offset);
        return new QueryExecutor<FixedListing>().findAllObjectsBy(query, params, eagerMS);
    }

    @Override
    public List<FixedListing> findAllLazy() {
        String query = "SELECT listing_id FROM fixed_listing;";
        ModelSetter<FixedListing> lazyMS = rs -> new FixedListing(rs.getObject("listing_id", UUID.class));
        return new QueryExecutor<FixedListing>().findAllObjects(query, lazyMS);
    }

    @Override
    public List<FixedListing> findAllEager() {
        String query = "SELECT * FROM fixed_listing;";
        return new QueryExecutor<FixedListing>().findAllObjects(query, eagerMS);
    }

    @Override
    public FixedListing findByListingId(UUID id) {
        String query = "SELECT * FROM fixed_listing WHERE listing_id = ?;";
        List<Object> params = Collections.singletonList(id);
        return new QueryExecutor<FixedListing>().findSingleObjectByParams(query, params, eagerMS);
    }

    public List<FixedListing> findAllActive() {
        String query = "SELECT * FROM fixed_listing WHERE quantity > 0;";
        return new QueryExecutor<FixedListing>().findAllObjects(query, eagerMS);
    }

    public List<FixedListing> findAllActiveByText(String text) {
        String query = "SELECT * FROM fixed_listing WHERE quantity > 0 AND lower(description) LIKE ?;";
        List<Object> params = Collections.singletonList("%" + text.toLowerCase() + "%");
        return new QueryExecutor<FixedListing>().findAllObjectsBy(query, params, eagerMS);
    }

    public List<FixedListing> findByDescription(String description) {
        String query = "SELECT * FROM fixed_listing WHERE description = ?;";
        List<Object> params = Collections.singletonList(description);
        return new QueryExecutor<FixedListing>().findAllObjectsBy(query, params, eagerMS);
    }

    @Override
    protected void insert(DomainObject obj, Connection conn) throws SQLException {
        FixedListing listing = (FixedListing) obj;
        String query = "INSERT INTO fixed_listing VALUES (?, ?, ?, ?, ?, ?, ?);";
        QuerySetter qs = preparedStatement -> {
            int nthPlaceholder = 1;
            preparedStatement.setObject(nthPlaceholder++, listing.getListingId());
            preparedStatement.setObject(
                    nthPlaceholder++, listing.getSellerGroup().getSgId());
            preparedStatement.setTimestamp(nthPlaceholder++, listing.getCreateTimestamp());
            preparedStatement.setBigDecimal(nthPlaceholder++, listing.getPrice());
            preparedStatement.setString(nthPlaceholder++, listing.getDescription());
            preparedStatement.setString(nthPlaceholder++, listing.getCondition().name());
            preparedStatement.setInt(nthPlaceholder, listing.getQuantity());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void update(DomainObject obj, Connection conn) throws SQLException {
        FixedListing listing = (FixedListing) obj;
        String query = "UPDATE fixed_listing SET " + "sg_id=?,"
                + "create_timestamp=?,"
                + "price=?,"
                + "description=?,"
                + "condition=?,"
                + "quantity=? "
                + "WHERE listing_id = ? AND ?>=0;";
        QuerySetter qs = statement -> {
            int nthPlaceholder = 1;
            statement.setObject(nthPlaceholder++, listing.getSellerGroup().getSgId());
            statement.setTimestamp(nthPlaceholder++, listing.getCreateTimestamp());
            statement.setBigDecimal(nthPlaceholder++, listing.getPrice());
            statement.setString(nthPlaceholder++, listing.getDescription());
            statement.setString(nthPlaceholder++, listing.getCondition().name());
            statement.setInt(nthPlaceholder++, listing.getQuantity());
            statement.setObject(nthPlaceholder++, listing.getListingId());
            statement.setObject(nthPlaceholder, listing.getQuantity());
        };
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }

    @Override
    protected void delete(DomainObject obj, Connection conn) throws SQLException {
        FixedListing listing = (FixedListing) obj;
        String query = "DELETE FROM fixed_listing WHERE listing_id=?;";
        QuerySetter qs = statement -> statement.setObject(1, listing.getListingId());
        new QueryExecutor<>().manipulateData(query, qs, conn);
    }
}
