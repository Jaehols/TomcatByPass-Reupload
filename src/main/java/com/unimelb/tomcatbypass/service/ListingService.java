package com.unimelb.tomcatbypass.service;

import com.unimelb.tomcatbypass.enums.Condition;
import com.unimelb.tomcatbypass.mapper.*;
import com.unimelb.tomcatbypass.model.*;
import com.unimelb.tomcatbypass.utils.ManagedTransaction;
import com.unimelb.tomcatbypass.utils.OperationException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * NOTE: A lot of the logic for Auction and Fixed listings is almost identical.
 * There's probably a nice way of making this polymorphic.
 */
public class ListingService {
    private static final Logger log = Logger.getLogger(ListingService.class.getName());
    private static final int DEFAULT_RETRIES = 1;

    @Getter // For testing.
    @Builder
    public static class ValidatedFixedListingParams {
        @NonNull
        BigDecimal price;

        @NonNull
        String description;

        @NonNull
        Condition condition;

        @NonNull
        Integer quantity;
    }

    @Getter // For testing.
    @Builder
    public static class ValidatedAuctionListingParams {
        @NonNull
        BigDecimal startPrice;

        @NonNull
        String description;

        @NonNull
        Timestamp endTimestamp;

        @NonNull
        Condition condition;
    }

    /**
     * Gets a listing by the listing_id as a Listing object, returns null if none found.
     */
    public static Listing getById(UUID listingId) {
        Listing listing = new FixedListingMapper().findByListingId(listingId);
        if (listing == null) {
            listing = new AuctionListingMapper().findByListingId(listingId);
        }
        return listing;
    }

    public static List<FixedListing> findFixedListingsInLimitOffset(Integer limit, Integer offset) {
        List<FixedListing> listings = new FixedListingMapper().findInLimitOffset(limit, offset);
        return listings;
    }

    public static List<AuctionListing> findAuctionListingsInLimitOffset(Integer limit, Integer offset) {
        List<AuctionListing> listings = new AuctionListingMapper().findInLimitOffset(limit, offset);
        return listings;
    }

    public static List<Listing> findByUsernameInLimitOffset(String username, Integer limit, Integer offset) {
        List<FixedListing> fixedListings =
                new FixedListingMapper().findByUsernameInLimitOffset(username, limit, offset);
        List<AuctionListing> auctionListings =
                new AuctionListingMapper().findByUsernameInLimitOffset(username, limit, offset);

        List<Listing> allListings = new ArrayList<>();
        allListings.addAll(fixedListings);
        allListings.addAll(auctionListings);

        return allListings;
    }

    public static List<FixedListing> findFixedListingsByDescriptionInLimitOffset(
            String description, Integer limit, Integer offset) {
        List<FixedListing> listings =
                new FixedListingMapper().findByDescriptionInLimitOffset(description, limit, offset);
        return listings;
    }

    public static List<AuctionListing> findAuctionListingsByDescriptionInLimitOffset(
            String description, Integer limit, Integer offset) {
        List<AuctionListing> listings =
                new AuctionListingMapper().findByDescriptionInLimitOffset(description, limit, offset);
        return listings;
    }

    public static List<Listing> findAllActiveListings() {
        List<FixedListing> fixedListings = new FixedListingMapper().findAllActive();
        List<AuctionListing> auctionListings = new AuctionListingMapper().findAllActive();

        List<Listing> allListings = new ArrayList<>();
        allListings.addAll(fixedListings);
        allListings.addAll(auctionListings);
        return allListings;
    }

    public static List<Listing> findAllActiveListingsByText(String text) {
        List<FixedListing> fixedListings = new FixedListingMapper().findAllActiveByText(text);
        List<AuctionListing> auctionListings = new AuctionListingMapper().findAllActiveByText(text);

        List<Listing> allListings = new ArrayList<>();
        allListings.addAll(fixedListings);
        allListings.addAll(auctionListings);

        return allListings;
    }

    public static List<AuctionListing> findByBidder(String username) {
        List<AuctionListing> listings = new AuctionListingMapper().findByBidder(username);
        return listings;
    }

    /**
     * @param params which have already been validated.
     * @param sellerGroupId identifies the seller group to connect this listing to.
     */
    public static Boolean saveNewFixedListing(ValidatedFixedListingParams params, UUID sellerGroupId, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                if (!AuthService.isUserInSellerGroup(username, sellerGroupId)) {
                    throw new OperationException("error verifying username=" + username + " for sgId=" + sellerGroupId);
                }
                SellerGroup actualSg = new SellerGroupMapper().findBySgId(sellerGroupId);
                FixedListing fixedListing = FixedListing.builder()
                        .listingId(UUID.randomUUID())
                        .sellerGroup(actualSg)
                        .createTimestamp(Timestamp.from(Instant.now()))
                        .price(params.price)
                        .description(params.description)
                        .condition(params.condition)
                        .quantity(params.quantity)
                        .build();
                log.info("Trying to save FixedPriceListing to db: " + fixedListing);
                fixedListing.create();
            }
        }.executeTransaction(Connection.TRANSACTION_REPEATABLE_READ, DEFAULT_RETRIES);
    }

    /**
     * @param params which have already been validated.
     * @param sellerGroupId identifies the seller group to connect this listing to.
     */
    public static Boolean saveNewAuctionListing(
            ValidatedAuctionListingParams params, UUID sellerGroupId, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                if (!AuthService.isUserInSellerGroup(username, sellerGroupId)) {
                    throw new OperationException("error verifying username=" + username + " for sgId=" + sellerGroupId);
                }
                SellerGroup actualSg = new SellerGroupMapper().findBySgId(sellerGroupId);
                AuctionListing auctionListing = AuctionListing.builder()
                        .listingId(UUID.randomUUID())
                        .sellerGroup(actualSg)
                        .createTimestamp(Timestamp.from(Instant.now()))
                        .startPrice(params.startPrice)
                        .description(params.description)
                        .endTimestamp(params.endTimestamp)
                        .condition(params.condition)
                        .build();
                log.info("Trying to save AuctionListing to db: " + auctionListing);
                auctionListing.create();
            }
        }.executeTransaction(Connection.TRANSACTION_REPEATABLE_READ, DEFAULT_RETRIES);
    }

    /**
     * Deletes this given listing if the user is admin or in the same seller group as the listing.
     */
    public static Boolean deleteListing(UUID listingId, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                Listing listing = ListingService.getById(listingId);
                if (listing == null
                        || (!AuthService.checkUserSellerGroupPermission(
                                username, listing.getSellerGroup().getSgId()))) {
                    throw new OperationException("username=" + username
                            + " does not have permission to delete listing with listingId=" + listing);
                }
                log.info("Listing to delete from db: " + listing);
                listing.delete();
            }
        }.executeTransaction(Connection.TRANSACTION_REPEATABLE_READ, 5);
    }
}
