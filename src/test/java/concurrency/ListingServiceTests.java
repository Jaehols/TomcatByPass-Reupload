package concurrency;

import static concurrency.Utils.waitForLatch;
import static concurrency.Utils.waitForThreads;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unimelb.tomcatbypass.enums.Condition;
import com.unimelb.tomcatbypass.mapper.AuctionListingMapper;
import com.unimelb.tomcatbypass.mapper.FixedListingMapper;
import com.unimelb.tomcatbypass.model.AuctionListing;
import com.unimelb.tomcatbypass.model.FixedListing;
import com.unimelb.tomcatbypass.model.Listing;
import com.unimelb.tomcatbypass.service.ListingService;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import com.unimelb.tomcatbypass.service.SellerGroupService;
import com.unimelb.tomcatbypass.service.UserSgMappingService;
import com.unimelb.tomcatbypass.utils.Session;
import org.junit.jupiter.api.Test;

/**
 * NOTE: Every single test in this class assumes that it has a 'clean' version of the database beforehand.
 * <br>
 * THIS MEANS YOU NEED TO RESET THE DATABASE BEFORE EVERY TEST.
 */
public class ListingServiceTests {
    ListingService.ValidatedAuctionListingParams auctionParams = ListingService.ValidatedAuctionListingParams.builder()
            .startPrice(BigDecimal.TEN)
            .description("Fireproof Car")
            .endTimestamp(Timestamp.from(Instant.now().plus(1L, ChronoUnit.DAYS)))
            .condition(Condition.NEW)
            .build();

    ListingService.ValidatedFixedListingParams fixedParams = ListingService.ValidatedFixedListingParams.builder()
            .price(BigDecimal.TEN)
            .description("Fixed Price Sandwich")
            .condition(Condition.NEW)
            .quantity(1)
            .build();

    UUID CARTEL_SG_ID = UUID.fromString("def8570f-358d-4d56-85da-b0f9d3440fc6");
    String APP_USER_USERNAME = "AppUser";
    String ADMIN_USER_USERNAME = "Admin";
    int NUMBER_OF_THREADS = 50;

    /**
     * NOTE: Editing a seller group name is not a use case :)
     */
    public void unusedTest1() {}

    /**
     * NOTE: Deleting a user is not a use case :)
     */
    public void unusedTest2() {}

    /**
     * This test is kinda cool. It creates a bunch of listings as setup, then it deletes all of them at the same time,
     *  and it also removes the user from the associated seller group at the same time.
     * <br>
     * The end assertion is that the number of created listings minus the number of deleted listings should equal
     *  the number of leftover listings in the db at the end.
     */
    @Test
    public void deleteFixedListingButUserRemovedFromSellerGroup() {

        // Create a bunch of identical (except for their IDs of course) listings.
        List<Boolean> createSuccesses = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            createSuccesses.add(ListingService.saveNewFixedListing(fixedParams, CARTEL_SG_ID, APP_USER_USERNAME));
        }

        // Get all the listings back out of the db, so we can get their IDs.
        Session.startSession();
        List<FixedListing> createdListings = new FixedListingMapper().findByDescription(fixedParams.getDescription());
        Session.closeSession();
        List<UUID> listingIds = createdListings.stream().map(Listing::getListingId).collect(Collectors.toList());

        // Now we create a bunch of threads to delete every single listing we created at once.
        ConcurrentLinkedQueue<Boolean> listingDeleteSuccesses = new ConcurrentLinkedQueue<>();
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (UUID listingId : listingIds) {
            Thread newThread = new Thread(() -> {
                waitForLatch(latch);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                listingDeleteSuccesses.add(ListingService.deleteListing(listingId, APP_USER_USERNAME));
            });
            newThread.start();
            threads.add(newThread);
        }

        ConcurrentLinkedQueue<Boolean> removeUserSgMappingSuccesses = new ConcurrentLinkedQueue<>();
        // We also create a single thread to remove the user from the seller group once, to spice things up.
        threads.add(new Thread(() -> {
            waitForLatch(latch);
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
            removeUserSgMappingSuccesses.add(UserSgMappingService.deleteMapping(APP_USER_USERNAME, CARTEL_SG_ID, ADMIN_USER_USERNAME));
        }));
        threads.get(threads.size() - 1).start();

        latch.countDown();
        waitForThreads(threads);

        // Sanity check that all the creates were successful
        assertTrue(createSuccesses.stream().allMatch(b -> b.equals(true)));
        assertEquals(NUMBER_OF_THREADS, createSuccesses.size());
        int numCreatedSuccessfully = createSuccesses.size();

        // Sanity check that the removal from the seller group was successful.
        assertTrue(removeUserSgMappingSuccesses.peek());

        Session.startSession();
        List<FixedListing> remainingListings = new FixedListingMapper().findByDescription(fixedParams.getDescription());
        Session.closeSession();

        // Now we do the important part:
        // Check that the number of created listings minus the number of deleted equals the number of remaining.
        int numDeletedSuccessfully = (int) listingDeleteSuccesses.stream().filter(b -> b.equals(true)).count();
        int numRemainingListings = remainingListings.size();
        System.out.println("numCreatedSuccessfully=" + numCreatedSuccessfully + " numDeletedSuccessfully=" + numDeletedSuccessfully + " numRemainingListings=" + numRemainingListings);
        assertEquals(numCreatedSuccessfully - numDeletedSuccessfully, numRemainingListings);
    }

    /**
     * This test is kinda cool. It creates a bunch of listings as setup, then it deletes all of them at the same time,
     *  and it also removes the user from the associated seller group at the same time.
     * <br>
     * The end assertion is that the number of created listings minus the number of deleted listings should equal
     *  the number of leftover listings in the db at the end.
     */
    @Test
    public void deleteAuctionListingButUserRemovedFromSellerGroup() {

        // Create a bunch of identical (except for their IDs of course) listings.
        List<Boolean> createSuccesses = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            createSuccesses.add(ListingService.saveNewAuctionListing(auctionParams, CARTEL_SG_ID, APP_USER_USERNAME));
        }

        // Get all the listings back out of the db, so we can get their IDs.
        Session.startSession();
        List<AuctionListing> createdListings = new AuctionListingMapper().findByDescription(auctionParams.getDescription());
        Session.closeSession();
        List<UUID> listingIds = createdListings.stream().map(Listing::getListingId).collect(Collectors.toList());

        // Now we create a bunch of threads to delete every single listing we created at once.
        ConcurrentLinkedQueue<Boolean> listingDeleteSuccesses = new ConcurrentLinkedQueue<>();
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (UUID listingId : listingIds) {
            Thread newThread = new Thread(() -> {
                waitForLatch(latch);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                listingDeleteSuccesses.add(ListingService.deleteListing(listingId, APP_USER_USERNAME));
            });
            newThread.start();
            threads.add(newThread);
        }

        ConcurrentLinkedQueue<Boolean> removeUserSgMappingSuccesses = new ConcurrentLinkedQueue<>();
        // We also create a single thread to remove the user from the seller group once, to spice things up.
        threads.add(new Thread(() -> {
            waitForLatch(latch);
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
            removeUserSgMappingSuccesses.add(UserSgMappingService.deleteMapping(APP_USER_USERNAME, CARTEL_SG_ID, ADMIN_USER_USERNAME));
        }));
        threads.get(threads.size() - 1).start();

        latch.countDown();
        waitForThreads(threads);

        // Sanity check that all the creates were successful
        assertTrue(createSuccesses.stream().allMatch(b -> b.equals(true)));
        assertEquals(NUMBER_OF_THREADS, createSuccesses.size());
        int numCreatedSuccessfully = createSuccesses.size();

        // Sanity check that the removal from the seller group was successful.
        assertTrue(removeUserSgMappingSuccesses.peek());

        Session.startSession();
        List<AuctionListing> remainingListings = new AuctionListingMapper().findByDescription(auctionParams.getDescription());
        Session.closeSession();

        // Now we do the important part:
        // Check that the number of created listings minus the number of deleted equals the number of remaining.
        int numDeletedSuccessfully = (int) listingDeleteSuccesses.stream().filter(b -> b.equals(true)).count();
        int numRemainingListings = remainingListings.size();
        System.out.println("numCreatedSuccessfully=" + numCreatedSuccessfully + " numDeletedSuccessfully=" + numDeletedSuccessfully + " numRemainingListings=" + numRemainingListings);
        assertEquals(numCreatedSuccessfully - numDeletedSuccessfully, numRemainingListings);
    }

    /**
     * Tests interleaving of auction listing creation with removal of user from linked seller group.
     * <br>
     * NOTE: This test repeatedly tries to create a listing with almost exactly the same details. This is ok, we
     *  only enforce that the primary keys are unique.
     * <br>
     * This idea here is to just remove the user from the group once, and check that the number of listings created is
     *  equal to the number of times our saveNewAuctionListing method succeeded.
     */
    @Test
    public void createAuctionListingButUserRemovedFromSellerGroup() {
        ConcurrentLinkedQueue<Boolean> createSuccesses = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Boolean> removeSuccesses = new ConcurrentLinkedQueue<>();

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                createSuccesses.add(ListingService.saveNewAuctionListing(auctionParams, CARTEL_SG_ID, APP_USER_USERNAME));
            }));
            threads.get(i).start();
        }

        // Just remove from seller group once.
        threads.add(new Thread(() -> {
            waitForLatch(latch);
            removeSuccesses.add(UserSgMappingService.deleteMapping(APP_USER_USERNAME, CARTEL_SG_ID, ADMIN_USER_USERNAME));
        }));
        threads.get(threads.size() - 1).start();

        latch.countDown();
        waitForThreads(threads);

        Session.startSession();
        List<AuctionListing> listings = new AuctionListingMapper().findByDescription(auctionParams.getDescription());
        Session.closeSession();

        assertEquals(createSuccesses.size(), NUMBER_OF_THREADS);  // This is just a sanity check.

        // We should have been able to remove the chosen user from the chosen seller group.
        assertEquals(removeSuccesses.peek(), true);

        // We want the number of new listings in the db to be the same as the number of successes we recorded.
        assertEquals(createSuccesses.stream().filter(pred -> pred.equals(true)).count(), listings.size());
    }

    /**
     * Tests interleaving of fixed listing creation with removal of user from linked seller group.
     * <br>
     * NOTE: This test repeatedly tries to create a listing with almost exactly the same details. This is ok, we
     *  only enforce that the primary keys are unique.
     * <br>
     * This idea here is to just remove the user from the group once, and check that the number of listings created is
     *  equal to the number of times our saveNewFixedListing method succeeded.
     */
    @Test
    public void createFixedListingButUserRemovedFromSellerGroup() {
        ConcurrentLinkedQueue<Boolean> createSuccesses = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Boolean> removeSuccesses = new ConcurrentLinkedQueue<>();

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                createSuccesses.add(ListingService.saveNewFixedListing(fixedParams, CARTEL_SG_ID, APP_USER_USERNAME));
            }));
            threads.get(i).start();
        }

        // Just remove from seller group once.
        threads.add(new Thread(() -> {
            waitForLatch(latch);
            removeSuccesses.add(UserSgMappingService.deleteMapping(APP_USER_USERNAME, CARTEL_SG_ID, ADMIN_USER_USERNAME));
        }));
        threads.get(threads.size() - 1).start();

        latch.countDown();
        waitForThreads(threads);

        Session.startSession();
        List<FixedListing> listings = new FixedListingMapper().findByDescription(fixedParams.getDescription());
        Session.closeSession();

        assertEquals(createSuccesses.size(), NUMBER_OF_THREADS);  // This is just a sanity check.

        // We should have been able to remove the chosen user from the chosen seller group.
        assertEquals(removeSuccesses.peek(), true);

        // We want the number of new listings in the db to be the same as the number of successes we recorded.
        assertEquals(createSuccesses.stream().filter(pred -> pred.equals(true)).count(), listings.size());
    }


    /**
     * Tests interleaving of fixed listing creation with deletion of the linked seller group.
     * <br>
     * NOTE: This test repeatedly tries to create a listing with almost exactly the same details. This is ok, we
     *  only enforce that the primary keys are unique.
     * <br>
     * This idea here is to just delete the seller group once, and check that the number of listings created is
     *  equal to the number of times our saveNewFixedListing method succeeded.
     */
    @Test
    public void createFixedListingButDeleteSellerGroup() {
        ConcurrentLinkedQueue<Boolean> createSuccesses = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Boolean> deleteSuccesses = new ConcurrentLinkedQueue<>();

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                createSuccesses.add(ListingService.saveNewFixedListing(fixedParams, CARTEL_SG_ID, APP_USER_USERNAME));
            }));
            threads.get(i).start();
        }

        // Just delete the seller group once.
        threads.add(new Thread(() -> {
            waitForLatch(latch);
            deleteSuccesses.add(SellerGroupService.deleteSellerGroup(CARTEL_SG_ID, ADMIN_USER_USERNAME));
        }));
        threads.get(threads.size() - 1).start();

        latch.countDown();
        waitForThreads(threads);

        Session.startSession();
        List<FixedListing> listings = new FixedListingMapper().findByDescription(fixedParams.getDescription());
        Session.closeSession();

        assertEquals(createSuccesses.size(), NUMBER_OF_THREADS);  // This is just a sanity check.

        // We should have been able to delete the chosen seller group.
        assertEquals(deleteSuccesses.peek(), true);

        assertEquals(0, listings.size());
    }

    /**
     * Tests interleaving of auction listing creation with deletion of the linked seller group.
     * <br>
     * NOTE: This test repeatedly tries to create a listing with almost exactly the same details. This is ok, we
     *  only enforce that the primary keys are unique.
     * <br>
     * This idea here is to just delete the seller group once, and check that the number of listings created is
     *  equal to the number of times our saveNewAuctionListing method succeeded.
     */
    @Test
    public void createAuctionListingButDeleteSellerGroup() {
        ConcurrentLinkedQueue<Boolean> createSuccesses = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Boolean> deleteSuccesses = new ConcurrentLinkedQueue<>();

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                createSuccesses.add(ListingService.saveNewAuctionListing(auctionParams, CARTEL_SG_ID, APP_USER_USERNAME));
            }));
            threads.get(i).start();
        }

        // Just delete the seller group once.
        threads.add(new Thread(() -> {
            waitForLatch(latch);
            deleteSuccesses.add(SellerGroupService.deleteSellerGroup(CARTEL_SG_ID, ADMIN_USER_USERNAME));
        }));
        threads.get(threads.size() - 1).start();

        latch.countDown();
        waitForThreads(threads);

        Session.startSession();
        List<AuctionListing> listings = new AuctionListingMapper().findByDescription(auctionParams.getDescription());
        Session.closeSession();

        assertEquals(createSuccesses.size(), NUMBER_OF_THREADS);  // This is just a sanity check.

        // We should have been able to delete the chosen seller group.
        assertEquals(deleteSuccesses.peek(), true);

        assertEquals(0, listings.size());
    }

    /**
     * Just creates a bunch of new fixed listings with the same details except their ID.
     */
    @Test
    public void createFixedListings() {
        ConcurrentLinkedQueue<Boolean> successes = new ConcurrentLinkedQueue<>();

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                successes.add(ListingService.saveNewFixedListing(fixedParams, CARTEL_SG_ID, APP_USER_USERNAME));
            }));
            threads.get(i).start();
        }
        latch.countDown();
        waitForThreads(threads);

        Session.startSession();
        List<FixedListing> listings = new FixedListingMapper().findByDescription(fixedParams.getDescription());
        Session.closeSession();

        assertEquals(successes.size(), NUMBER_OF_THREADS);  // This is just a sanity check.

        // All listings should have been created successfully.
        assertEquals(NUMBER_OF_THREADS, successes.stream().filter(pred -> pred.equals(true)).count());

        // We want the number of new listings in the db to be the same as the number of successes we recorded.
        assertEquals(successes.stream().filter(pred -> pred.equals(true)).count(), listings.size());
    }

    /**
     * Just creates a bunch of new auction listings with the same details except their ID.
     */
    @Test
    public void createAuctionListings() {
        ConcurrentLinkedQueue<Boolean> successes = new ConcurrentLinkedQueue<>();

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                successes.add(ListingService.saveNewAuctionListing(auctionParams, CARTEL_SG_ID, APP_USER_USERNAME));
            }));
            threads.get(i).start();
        }
        latch.countDown();
        waitForThreads(threads);

        Session.startSession();
        List<AuctionListing> listings = new AuctionListingMapper().findByDescription(auctionParams.getDescription());
        Session.closeSession();

        assertEquals(successes.size(), NUMBER_OF_THREADS);  // This is just a sanity check.

        // All listings should have been created successfully.
        assertEquals(NUMBER_OF_THREADS, successes.stream().filter(pred -> pred.equals(true)).count());

        // We want the number of new listings in the db to be the same as the number of successes we recorded.
        assertEquals(successes.stream().filter(pred -> pred.equals(true)).count(), listings.size());
    }

    /**
     * Simple unit test of database persistence.
     */
    @Test
    public void createAuctionUnitTest() {
        Boolean success = ListingService.saveNewAuctionListing(auctionParams, CARTEL_SG_ID, APP_USER_USERNAME);
        Session.startSession();
        List<AuctionListing> results = new AuctionListingMapper().findByDescription(auctionParams.getDescription());
        Session.closeSession();

        assertEquals(success, true);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0).getDescription(), auctionParams.getDescription());
    }

    /**
     * Simple unit test of database persistence.
     */
    @Test
    public void createFixedUnitTest() {
        Boolean success = ListingService.saveNewFixedListing(fixedParams, CARTEL_SG_ID, APP_USER_USERNAME);
        Session.startSession();
        List<FixedListing> results = new FixedListingMapper().findByDescription(fixedParams.getDescription());
        Session.closeSession();

        assertEquals(success, true);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0).getDescription(), fixedParams.getDescription());
    }
}
