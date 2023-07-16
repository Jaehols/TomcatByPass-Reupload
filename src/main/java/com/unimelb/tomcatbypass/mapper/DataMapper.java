package com.unimelb.tomcatbypass.mapper;

import com.unimelb.tomcatbypass.model.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public abstract class DataMapper {
    private static final Logger log = Logger.getLogger(DataMapper.class.getName());

    protected abstract void insert(DomainObject obj, Connection conn) throws SQLException;

    protected abstract void update(DomainObject obj, Connection conn) throws SQLException;

    protected abstract void delete(DomainObject obj, Connection conn) throws SQLException;

    /**
     * Returns the relevant registered DataMapper instance for a given type.
     * @param type type for which a DataMapper instance should be returned.
     * @return
     */
    protected static DataMapper getMapper(Class<?> type) {
        if (AppUser.class.equals(type)) {
            return new AppUserMapper();
        } else if (AuctionListing.class.equals(type)) {
            return new AuctionListingMapper();
        } else if (AuctionOrder.class.equals(type)) {
            return new AuctionOrderMapper();
        } else if (Bid.class.equals(type)) {
            return new BidMapper();
        } else if (FixedListing.class.equals(type)) {
            return new FixedListingMapper();
        } else if (FixedPriceOrder.class.equals(type)) {
            return new FixedPriceOrderMapper();
        } else if (SellerGroup.class.equals(type)) {
            return new SellerGroupMapper();
        } else if (UserSgMapping.class.equals(type)) {
            return new UserSgMappingMapper();
        }

        log.severe("No mapper registered for type: " + type.getName() + "!");
        return null;
    }
}
