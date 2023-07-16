package com.unimelb.tomcatbypass.service;

import com.unimelb.tomcatbypass.mapper.*;
import com.unimelb.tomcatbypass.model.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

import com.unimelb.tomcatbypass.utils.ManagedTransaction;
import com.unimelb.tomcatbypass.utils.OperationException;
import lombok.AllArgsConstructor;

public class BidService {
    private static final Logger log = Logger.getLogger(BidService.class.getName());

    @AllArgsConstructor
    public static class ValidatedBidParams {
        BigDecimal value;
    }

    public static boolean createBid(ValidatedBidParams params, UUID listingId, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                // VALIDATE BID CREATION!!
                // fetch the listing to create a bid on
                AuctionListing listing = new AuctionListingMapper().findByListingId(listingId);
                if (listing == null) {
                    throw new OperationException("Attempted to create a bid for AuctionListing '" + listingId
                            + "' but the listing could not be retrieved from DB.");
                }

                // fetch the user
                AppUser user = AppUserService.findByUsername(username);
                if (user == null) {
                    throw new OperationException("Attempted to create a bid for AuctionListing '" + listingId
                            + "' but the user '" + username
                            + "' could not be retrieved from DB.");
                }

                // Check that the auction isn't over
                Timestamp bidTime = Timestamp.from(Instant.now()); // need to use this time for the actual bid
                if (listing.getEndTimestamp().before(bidTime)) {
                    throw new OperationException("User '" + username
                            + "' attempted to create a bid for AuctionListing '" + listingId
                            + "' but the auction has finished!");
                }

                // Check that the new bid is the highest bid
                Bid highestBid = new BidMapper().findHighestBidForAuctionListing(listingId);
                if (highestBid != null && highestBid.getValue().compareTo(params.value) >= 0) {
                    // "compare >= 0" means "bid.value >= value"
                    throw new OperationException("User '" + username
                            + "' attempted to place a bid of '" + params.value
                            + "' on AuctionListing '" + listingId
                            + "', but the highest bid was for '" + highestBid.getValue()
                            + "'!");
                }

                // check that first bid meets the reserve
                if (highestBid == null && listing.getStartPrice().compareTo(params.value) > 0) {
                    // "a.compare(b) >= 0" means "a > b"
                    throw new OperationException("User '" + username
                            + "' attempted to place a bid of '" + params.value
                            + "' on AuctionListing '" + listingId
                            + "', but it did not meet the reserve of '" + listing.getStartPrice()
                            + "'!");
                }

                Bid bid = Bid.builder()
                        .bidId(UUID.randomUUID())
                        .user(user)
                        .listing(listing)
                        .createTimestamp(bidTime)
                        .value(params.value)
                        .build();

                log.info("Bid to save to db: " + bid);
                bid.create();
            }
        }.executeTransaction(Connection.TRANSACTION_READ_COMMITTED, 0);
    }

    public static Bid findHighestBid(UUID listingId) {
        return new BidMapper().findHighestBidForAuctionListing(listingId);
    }
}
