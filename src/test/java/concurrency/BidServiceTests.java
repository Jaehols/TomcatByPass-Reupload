package concurrency;

import com.unimelb.tomcatbypass.service.BidService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.unimelb.tomcatbypass.service.OrderService;
import org.junit.jupiter.api.Test;

import static concurrency.Utils.*;
import static concurrency.Utils.waitForThreads;

public class BidServiceTests {
    private static final UUID rice = UUID.fromString("e3f81946-05d4-4d64-a935-88c80e005d49");
    private static final String APP_USER = "AppUser";
    private static final String JIMMY = "Jimmy";
    private static final int nThreads = 50;

    // There are no concurrency requirements for bids, so we're just gonna sanity check it by running a
    // bunch of bids.
    // EXPECT: with nThreads = 50, we should have AppUser have the highest bid at 54 at the end. Some bids may not
    // be added, others may be, it doesn't really matter
    @Test
    public void runManyBids() {
        int initBid = 5;
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            BidService.ValidatedBidParams params = new BidService.ValidatedBidParams(new BigDecimal(initBid+i));
            AtomicInteger i_atomic = new AtomicInteger(i);
            threads.add(new Thread(() -> {
                waitForLatch(latch);

                String user = APP_USER; // app user gets even bids
                if (i_atomic.get() % 2 == 0) {
                    user = JIMMY; // jimmy gets odd bids
                }
                BidService.createBid(params, rice, user);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }
}
