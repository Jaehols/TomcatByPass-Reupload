package com.unimelb.tomcatbypass.service;

import com.unimelb.tomcatbypass.mapper.*;
import com.unimelb.tomcatbypass.model.*;
import com.unimelb.tomcatbypass.utils.ManagedTransaction;
import com.unimelb.tomcatbypass.utils.OperationException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;

public class OrderService {
    private static final Logger log = Logger.getLogger(OrderService.class.getName());
    private static final int DEFAULT_RETRIES = 5;

    @AllArgsConstructor
    public static class ValidatedFixedPriceOrderParams {
        String address;
        Integer quantity;
    }

    @AllArgsConstructor
    public static class ValidatedAuctionOrderParams {
        String address;
    }

    public static FixedPriceOrder getFixedOrderById(UUID orderId) {
        return new FixedPriceOrderMapper().findFixedPriceOrderById(orderId);
    }

    public static List<FixedPriceOrder> getAllFixed() {
        return new FixedPriceOrderMapper().getAll();
    }

    public static List<FixedPriceOrder> getFixedOrderByUser(String username) {
        return new FixedPriceOrderMapper().getByUser(username);
    }

    public static List<FixedPriceOrder> getFixedBySgId(UUID sgId) {
        return new FixedPriceOrderMapper().getBySgId(sgId);
    }

    public static AuctionOrder getAuctionOrderById(UUID orderID) {
        return new AuctionOrderMapper().getByOrderId(orderID);
    }

    public static List<AuctionOrder> getAllAuction() {
        return new AuctionOrderMapper().getAll();
    }

    public static List<AuctionOrder> getAuctionByUser(String username) {
        return new AuctionOrderMapper().getByUser(username);
    }

    public static List<AuctionOrder> getAuctionBySgId(UUID sgId) {
        return new AuctionOrderMapper().getBySgId(sgId);
    }

    public static boolean listingHasAuctionOrder(UUID listingId) {
        return new AuctionOrderMapper().listingHasAuctionOrder(listingId);
    }

    public static boolean canUserEditFixedOrder(UUID orderId, String username) {
        if (new AppUserMapper().findByUsername(username) == null) {
            return false;
        }
        FixedPriceOrder order = new FixedPriceOrderMapper().findFixedPriceOrderById(orderId);
        if (order == null) {
            return false;
        }
        boolean userMadeOrder = order.getUser().getUsername().equals(username);
        boolean userHasEditPermission = AuthService.checkUserSellerGroupPermission(
                username, order.getListing().getSellerGroup().getSgId());

        return userMadeOrder || userHasEditPermission;
    }

    public static boolean canUserEditAuctionOrder(UUID orderId, String username) {
        if (new AppUserMapper().findByUsername(username) == null) {
            return false;
        }
        AuctionOrder order = new AuctionOrderMapper().getByOrderId(orderId);
        if (order == null) {
            return false;
        }
        boolean userMadeOrder = order.getUser().getUsername().equals(username);
        boolean userHasEditPermission = AuthService.checkUserSellerGroupPermission(
                username, order.getListing().getSellerGroup().getSgId());

        return userMadeOrder || userHasEditPermission;
    }

    public static boolean createFixedPriceOrder(ValidatedFixedPriceOrderParams params, UUID listingId, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {

                // ensure positive quantity
                // TODO add this validation to Validated Fixed Order Params
                if (params.quantity < 1) {
                    throw new OperationException(("User '" + username
                            + "' attempted to create an order for '" + params.quantity
                            + "' items from FixedListing '" + listingId
                            + "' but this is non positive!'"));
                }

                // load relevant info
                FixedListing listing = new FixedListingMapper().findByListingId(listingId);
                AppUser user = AppUserService.findByUsername(username);

                // Check that there's enough stock
                if (params.quantity > listing.getQuantity()) {
                    throw new OperationException("User '" + username
                            + "' attempted to create an order for '" + params.quantity
                            + "' items from FixedListing '" + listingId
                            + "' but there were only '" + listing.getQuantity()
                            + "' available! This should not be happening!");
                }

                // create the order
                BigDecimal total = BigDecimal.valueOf(params.quantity).multiply(listing.getPrice());
                FixedPriceOrder order = FixedPriceOrder.builder()
                        .orderId(UUID.randomUUID())
                        .user(user)
                        .listing(listing)
                        .createTimestamp(Timestamp.from(Instant.now()))
                        .address(params.address)
                        .quantity(params.quantity)
                        .total(total)
                        .build();

                log.info("Trying FixedPriceOrder to save to db: " + order);
                order.create();

                // update the listing stock level
                listing.setQuantity(listing.getQuantity() - order.getQuantity());
            }
        }.executeTransaction(Connection.TRANSACTION_SERIALIZABLE, 5);
    }

    public static boolean deleteFixedPriceOrder(UUID orderId, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {

                // Check that user is allowed to delete
                if (!(canUserEditFixedOrder(orderId, username))) {
                    throw new OperationException("User '" + username
                            + "' attempted to delete Fixed Order '" + orderId
                            + "' but they are not authorised This should never happen!");
                }

                FixedPriceOrder order = new FixedPriceOrderMapper().findFixedPriceOrderById(orderId);
                FixedListing listing = (FixedListing) order.getListing();

                log.info("Trying FixedPriceOrder to delete from db: " + order);
                listing.setQuantity(listing.getQuantity() + order.getQuantity());
                order.delete();

            }
        }.executeTransaction(Connection.TRANSACTION_SERIALIZABLE, 5);
    }

    public static boolean updateFixedPriceOrder(
            UUID orderId, Integer updatedQuantity, String updatedAddress, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                // Check that updatedQuantity is not 0 or less
                if (updatedQuantity < 1) {
                    throw new OperationException("Cannot update order to have non positive quantity");
                }

                // Check that user is allowed to edit
                if (!(canUserEditFixedOrder(orderId, username))) {
                    throw new OperationException("User '" + username
                            + "' attempted to edit Fixed Order '" + orderId
                            + "' but they are not authorised This should never happen!");
                }

                FixedPriceOrder order = new FixedPriceOrderMapper().findFixedPriceOrderById(orderId);

                Integer difference = updatedQuantity - order.getQuantity();
                FixedListing listing = (FixedListing) order.getListing();

                // Check that listing is not going to have a negative amount
                if (listing.getQuantity() - difference < 0) {
                    throw new OperationException("Quantity adjusted too high");
                }

                //TODO update the total of the order to reflect the new quantity
                log.info("Trying FixedPriceOrder to update in db: " + order);
                listing.setQuantity(listing.getQuantity() - difference);
                order.setQuantity(updatedQuantity);
                order.setAddress(updatedAddress);
            }
        }.executeTransaction(Connection.TRANSACTION_SERIALIZABLE, 5);
    }

    public static boolean deleteAuctionOrder(UUID orderId, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                // Check that user is allowed to delete
                if (!(canUserEditAuctionOrder(orderId, username))) {
                    throw new OperationException("User '" + username
                            + "' attempted to delete Auction Order '" + orderId
                            + "' but they are not authorised This should never happen!");
                }

                AuctionOrder order = new AuctionOrderMapper().getByOrderId(orderId);

                log.info("Trying AuctionOrder to delete from db: " + order);
                order.delete();
            }
        }.executeTransaction(Connection.TRANSACTION_REPEATABLE_READ, 2);
    }

    public static boolean updateAuctionOrder(UUID orderId, String updatedAddress, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                // Check that user is allowed to edit
                if (!(canUserEditAuctionOrder(orderId, username))) {
                    throw new OperationException("User '" + username
                            + "' attempted to edit Auction Order '" + orderId
                            + "' but they are not authorised This should never happen!");
                }

                AuctionOrder order = new AuctionOrderMapper().getByOrderId(orderId);

                log.info("Trying AuctionOrder to update in db: " + order);
                order.setAddress(updatedAddress);
            }
        }.executeTransaction(Connection.TRANSACTION_REPEATABLE_READ, 2);
    }

    /**
     * TODO: This method hasn't really actually been tested to see if it works, it's currently just a
     *        proof of concept.
     */
    public static boolean createAuctionOrder(ValidatedAuctionOrderParams params, UUID listingId, String username) {
        return new ManagedTransaction() {
            @Override
            protected void doTransactionOperations() throws OperationException {
                // Check that there isn't already an order for this auction
                if (new AuctionOrderMapper().listingHasAuctionOrder(listingId)) {
                    throw new OperationException("User '" + username
                            + "' attempted to create an order for AuctionListing '" + listingId
                            + "' but an order has already been made! This should not be happening!");
                }

                // Check that the auction is over
                AuctionListing listing = new AuctionListingMapper().findByListingId(listingId);
                if (!listing.getEndTimestamp().before(Timestamp.from(Instant.now()))) {
                    throw new OperationException("User '" + username
                            + "' attempted to create an order for AuctionListing '" + listingId
                            + "' but the auction hasn't finished! This should not be happening!");
                }

                // Check that the user has the highest bid
                AppUser user = AppUserService.findByUsername(username);
                Bid bid = new BidMapper().findHighestBidForAuctionListing(listingId);
                if (!user.getUsername().equals(bid.getUser().getUsername())) {
                    throw new OperationException("User '" + username
                            + "' attempted to create an order for AuctionListing '" + listingId
                            + "' but did not have the highest bid! This should not be happening!");
                }

                AuctionOrder order = AuctionOrder.builder()
                        .orderId(UUID.randomUUID())
                        .user(user)
                        .listing(listing)
                        .createTimestamp(Timestamp.from(Instant.now()))
                        .address(params.address)
                        .bid(bid)
                        .build();

                log.info("Trying to save AuctionOrder to db: " + order);
                order.create();
            }
        }.executeTransaction(Connection.TRANSACTION_SERIALIZABLE, DEFAULT_RETRIES);
    }
}
