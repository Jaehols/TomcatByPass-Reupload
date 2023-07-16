package concurrency;

import com.unimelb.tomcatbypass.service.BidService;
import com.unimelb.tomcatbypass.service.OrderService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static concurrency.Utils.*;


public class OrderServiceTests {
    public static final String APP_USER = "AppUser";
    public static final String JIMMY = "Jimmy";
    public static final UUID lukeDegreeId = UUID.fromString("01e48a6c-cf8a-49fd-b719-7f81601eb338");
    private static final UUID rice = UUID.fromString("e3f81946-05d4-4d64-a935-88c80e005d49");

    /**
     * This test fires off a bunch of auction orders for a single auction.
     *  EXPECT: only one auction order is created, all others fail.
     */
    @Test
    public void testCreateAuctionOrder() {
        OrderService.ValidatedAuctionOrderParams aop = new OrderService.ValidatedAuctionOrderParams("address");

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                OrderService.createAuctionOrder(aop, lukeDegreeId, APP_USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }

    /**
     * This test fires off a bunch of bids for a single auction at once, and also fires off an auction order for a
     * different auction. The idea is to see if this causes the auction order to retry
     *  EXPECT: only one auction order is created, all others fail.
     */
    @Test
    public void testCreateAuctionOrderVsBids() {
        int nBids = 50;
        OrderService.ValidatedAuctionOrderParams aop = new OrderService.ValidatedAuctionOrderParams("address");

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        AtomicInteger bidSuccesses = new AtomicInteger();
        for (int i = 0; i < nBids; i++) {
            BidService.ValidatedBidParams bidParams = new BidService.ValidatedBidParams(new BigDecimal(5+i));
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                if (BidService.createBid(bidParams, rice, JIMMY)) {
                    bidSuccesses.getAndIncrement();
                }
            }));
            threads.get(i).start();
        }

        AtomicBoolean order_success = new AtomicBoolean(false);
        threads.add(new Thread(() -> {
            waitForLatch(latch);
            order_success.set(OrderService.createAuctionOrder(aop, lukeDegreeId, APP_USER));
        }));
        threads.get(threads.size() - 1).start();

        latch.countDown();

        waitForThreads(threads);

        System.out.println("Finished test - " + bidSuccesses.get() + " out of " + nBids + " bids succeeded, and the auction order returned " + order_success.get());
    }


    // This is an interesting one cause we dont really care about lost updates by our reasoning but it allows us to
    // see the expected behaviour of rollbacks and different threads being the last editor
    @Test
    public void testEditAuctionOrder() {
        final UUID ORDER_ID = UUID.fromString("617fef77-6735-48f8-ad2d-ec8cc373faa3");
        final String USER = "Gus F";


        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i=0; i< 10; i++) {
            int finalI = i;
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                OrderService.updateAuctionOrder(ORDER_ID, Integer.toString(finalI), USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }

    //Likewise with this demonstrates rollbacks and failures because the order doesnt exist
    @Test
    public void deleteAuctionOrder() {
        final UUID ORDER_ID = UUID.fromString("617fef77-6735-48f8-ad2d-ec8cc373faa3");
        final String USER = "Gus F";

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                OrderService.deleteAuctionOrder(ORDER_ID, USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }

    // on fresh DB, listing has 24 quantity, Order has 4 quantity
    // expected result Order has 10 quantity, listing has 18 quantity
    @Test
    public void testManyIdenticalFixedOrderEdits() {
        final UUID ORDER_ID = UUID.fromString("d4c572a5-bcd3-4455-a6f3-3914a0d494a8");
        final Integer NEW_AMOUNT = 10;
        final String UPDATED_ADDRESS = "185 Stanley st";
        final String USER = "Gus F";


        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                OrderService.updateFixedPriceOrder(ORDER_ID, NEW_AMOUNT, UPDATED_ADDRESS, USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }


    // If run on fresh database all orders in this set will be cancelled and the final listing will have 40 quantity
    @Test
    public void testManyFixedOrderCancels() {
        final UUID[] UUID_ARRAY = new UUID[]{
                UUID.fromString("d4c572a5-bcd3-4455-a6f3-3914a0d494a8"),
                UUID.fromString("4bb25168-37b4-40bc-87d5-2be09762ae9b"),
                UUID.fromString("b1bedce2-4fbe-4d6c-be79-5f0cc8e2fd45"),
                UUID.fromString("7889b072-3316-4608-8526-4112b2a0fade"),
                UUID.fromString("a67aec5b-2290-4511-99af-c63b730c5701")
        };
        final String USER = "Gus F";


        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i=0; i< 5; i++) {
            int finalI = i;
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                OrderService.deleteFixedPriceOrder(UUID_ARRAY[finalI], USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }

    // At finish time across listing and order quantity total should be 40
    @Test
    public void testManyRandomQuantityFixedOrderEdits() {
        final UUID ORDER_ID = UUID.fromString("d4c572a5-bcd3-4455-a6f3-3914a0d494a8");
        final String UPDATED_ADDRESS = "185 Stanley st";
        final Integer MIN_INT = -5;
        final Integer MAX_INT = 30;
        final String USER = "Gus F";


        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                final Integer amount = ThreadLocalRandom.current().nextInt(MIN_INT, MAX_INT);
                waitForLatch(latch);
                OrderService.updateFixedPriceOrder(ORDER_ID, amount, UPDATED_ADDRESS, USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }

    @Test
    public void testManyRandomQuantityBuysFixedOrder() {
        final UUID LISTING_ID = UUID.fromString("98b7e5b5-78bf-4807-9af5-f5ca1db51fbe");
        final String UPDATED_ADDRESS = "185 Stanley st";
        final Integer MIN_INT_BUY = -5;
        final Integer MAX_INT_BUY = 10;
        final String USER = "Gus F";

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        //Buying threads
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                final Integer amount = ThreadLocalRandom.current().nextInt(MIN_INT_BUY, MAX_INT_BUY);
                OrderService.ValidatedFixedPriceOrderParams order =
                        new OrderService.ValidatedFixedPriceOrderParams(UPDATED_ADDRESS, amount);
                waitForLatch(latch);
                OrderService.createFixedPriceOrder(order, LISTING_ID, USER);
            }));

            threads.get(i).start();
        }

        latch.countDown();

        waitForThreads(threads);
    }


    // At finish time across listing and order quantity total should be 40
    // SELECT SUM(quantity) FROM fixed_order WHERE listing_id = '98b7e5b5-78bf-4807-9af5-f5ca1db51fbe'; +
    // SELECT quantity FROM fixed_listing WHERE listing_id = '98b7e5b5-78bf-4807-9af5-f5ca1db51fbe';
    // = 40
    @Test
    public void testManyRandomQuantityFixedOrderBuyAndEdits() {
        final UUID ORDER_ID = UUID.fromString("d4c572a5-bcd3-4455-a6f3-3914a0d494a8");
        final UUID LISTING_ID = UUID.fromString("98b7e5b5-78bf-4807-9af5-f5ca1db51fbe");
        final String UPDATED_ADDRESS = "185 Stanley st";
        final Integer MIN_INT_EDIT = -5;
        final Integer MAX_INT_EDIT = 30;
        final Integer MIN_INT_BUY = -5;
        final Integer MAX_INT_BUY = 30;
        final String USER = "Gus F";


        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        //Editing threads
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                final Integer amount = ThreadLocalRandom.current().nextInt(MIN_INT_EDIT, MAX_INT_EDIT);
                waitForLatch(latch);
                OrderService.updateFixedPriceOrder(ORDER_ID, amount, UPDATED_ADDRESS, USER);
            }));

            threads.get(i).start();
        }

        //Buying threads
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                final Integer amount = ThreadLocalRandom.current().nextInt(MIN_INT_BUY, MAX_INT_BUY);
                OrderService.ValidatedFixedPriceOrderParams order =
                        new OrderService.ValidatedFixedPriceOrderParams(UPDATED_ADDRESS, amount);
                waitForLatch(latch);
                OrderService.createFixedPriceOrder(order, LISTING_ID, USER);
            }));

            threads.get(i+10).start();
        }

        latch.countDown();

        waitForThreads(threads);
    }

    // At finish time across listing and order quantity total should be 40
    // SELECT SUM(quantity) FROM fixed_order WHERE listing_id = '98b7e5b5-78bf-4807-9af5-f5ca1db51fbe'; +
    // SELECT quantity FROM fixed_listing WHERE listing_id = '98b7e5b5-78bf-4807-9af5-f5ca1db51fbe';
    // = 40
    @Test
    public void testManyRandomQuantityFixedOrderBuyAndCancel() {
        final UUID LISTING_ID = UUID.fromString("98b7e5b5-78bf-4807-9af5-f5ca1db51fbe");
        final UUID[] UUID_ARRAY = new UUID[]{
                UUID.fromString("d4c572a5-bcd3-4455-a6f3-3914a0d494a8"),
                UUID.fromString("4bb25168-37b4-40bc-87d5-2be09762ae9b"),
                UUID.fromString("b1bedce2-4fbe-4d6c-be79-5f0cc8e2fd45"),
                UUID.fromString("7889b072-3316-4608-8526-4112b2a0fade"),
                UUID.fromString("a67aec5b-2290-4511-99af-c63b730c5701")
        };
        final String UPDATED_ADDRESS = "185 Stanley st";
        final Integer MIN_INT_BUY = -5;
        final Integer MAX_INT_BUY = 30;
        final String USER = "Gus F";


        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        //Buying threads
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                final Integer amount = ThreadLocalRandom.current().nextInt(MIN_INT_BUY, MAX_INT_BUY);
                OrderService.ValidatedFixedPriceOrderParams order =
                        new OrderService.ValidatedFixedPriceOrderParams(UPDATED_ADDRESS, amount);
                waitForLatch(latch);
                OrderService.createFixedPriceOrder(order, LISTING_ID, USER);
            }));

            threads.get(i).start();
        }

        //Cancelling threads
        for (int i=0; i< 5; i++) {
            int finalI = i;
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                OrderService.deleteFixedPriceOrder(UUID_ARRAY[finalI], USER);
            }));

            threads.get(i+10).start();
        }

        latch.countDown();

        waitForThreads(threads);
    }

    // At finish time across listing and order quantity total should be 40
    // SELECT SUM(quantity) FROM fixed_order WHERE listing_id = '98b7e5b5-78bf-4807-9af5-f5ca1db51fbe'; +
    // SELECT quantity FROM fixed_listing WHERE listing_id = '98b7e5b5-78bf-4807-9af5-f5ca1db51fbe';
    // = 40
    // Comment or uncomment the first UUID in the UUID_ARRAY to demonstrate the order being edited being cancelled or not
    @Test
    public void testManyRandomQuantityFixedOrderEditsAndCancel() {
        final UUID ORDER_ID = UUID.fromString("d4c572a5-bcd3-4455-a6f3-3914a0d494a8");
        final UUID[] UUID_ARRAY = new UUID[]{
                UUID.fromString("d4c572a5-bcd3-4455-a6f3-3914a0d494a8"),
                UUID.fromString("4bb25168-37b4-40bc-87d5-2be09762ae9b"),
                UUID.fromString("b1bedce2-4fbe-4d6c-be79-5f0cc8e2fd45"),
                UUID.fromString("7889b072-3316-4608-8526-4112b2a0fade"),
                UUID.fromString("a67aec5b-2290-4511-99af-c63b730c5701")
        };
        final String UPDATED_ADDRESS = "185 Stanley st";
        final Integer MIN_INT_EDIT = -5;
        final Integer MAX_INT_EDIT = 30;
        final String USER = "Gus F";


        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        //Editing threads
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                final Integer amount = ThreadLocalRandom.current().nextInt(MIN_INT_EDIT, MAX_INT_EDIT);
                waitForLatch(latch);
                OrderService.updateFixedPriceOrder(ORDER_ID, amount, UPDATED_ADDRESS, USER);
            }));

            threads.get(i).start();
        }

        //Cancelling threads
        for (int i=0; i< 5; i++) {
            int finalI = i;
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                OrderService.deleteFixedPriceOrder(UUID_ARRAY[finalI], USER);
            }));

            threads.get(i+10).start();
        }

        latch.countDown();

        waitForThreads(threads);
    }

    // At finish time across listing and order quantity total should be 40
    // SELECT SUM(quantity) FROM fixed_order WHERE listing_id = '98b7e5b5-78bf-4807-9af5-f5ca1db51fbe'; +
    // SELECT quantity FROM fixed_listing WHERE listing_id = '98b7e5b5-78bf-4807-9af5-f5ca1db51fbe';
    // = 40
    // Comment or uncomment the first UUID in the UUID_ARRAY to demonstrate the order being edited being cancelled or not
    @Test
    public void testManyRandomQuantityFixedOrderBuysAndEditsAndCancel() {
        final UUID LISTING_ID = UUID.fromString("98b7e5b5-78bf-4807-9af5-f5ca1db51fbe");
        final UUID ORDER_ID = UUID.fromString("d4c572a5-bcd3-4455-a6f3-3914a0d494a8");
        final UUID[] UUID_ARRAY = new UUID[]{
                UUID.fromString("d4c572a5-bcd3-4455-a6f3-3914a0d494a8"),
                UUID.fromString("4bb25168-37b4-40bc-87d5-2be09762ae9b"),
                UUID.fromString("b1bedce2-4fbe-4d6c-be79-5f0cc8e2fd45"),
                UUID.fromString("7889b072-3316-4608-8526-4112b2a0fade"),
                UUID.fromString("a67aec5b-2290-4511-99af-c63b730c5701")
        };
        final String UPDATED_ADDRESS = "185 Stanley st";
        final Integer MIN_INT_EDIT = -5;
        final Integer MAX_INT_EDIT = 30;
        final Integer MIN_INT_BUY = -5;
        final Integer MAX_INT_BUY = 30;
        final String USER = "Gus F";


        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        //Buying threads
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                final Integer amount = ThreadLocalRandom.current().nextInt(MIN_INT_BUY, MAX_INT_BUY);
                OrderService.ValidatedFixedPriceOrderParams order =
                        new OrderService.ValidatedFixedPriceOrderParams(UPDATED_ADDRESS, amount);
                waitForLatch(latch);
                OrderService.createFixedPriceOrder(order, LISTING_ID, USER);
            }));

            threads.get(i).start();
        }

        //Editing threads
        for (int i=0; i< 10; i++) {
            threads.add(new Thread(() -> {
                final Integer amount = ThreadLocalRandom.current().nextInt(MIN_INT_EDIT, MAX_INT_EDIT);
                waitForLatch(latch);
                OrderService.updateFixedPriceOrder(ORDER_ID, amount, UPDATED_ADDRESS, USER);
            }));

            threads.get(i+10).start();
        }

        //Cancelling threads
        for (int i=0; i< 5; i++) {
            int finalI = i;
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                OrderService.deleteFixedPriceOrder(UUID_ARRAY[finalI], USER);
            }));

            threads.get(i+20).start();
        }

        latch.countDown();

        waitForThreads(threads);
    }


}
