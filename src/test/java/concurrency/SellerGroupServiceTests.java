package concurrency;

import com.unimelb.tomcatbypass.service.SellerGroupService;
import com.unimelb.tomcatbypass.service.UserSgMappingService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static concurrency.Utils.waitForLatch;
import static concurrency.Utils.waitForThreads;

public class SellerGroupServiceTests {
    private static final int NUM_THREADS = 50;
    private static final String APP_USER = "AppUser";
    private static final String JIMMY = "Jimmy";
    private static final UUID THE_CARTEL = UUID.fromString("def8570f-358d-4d56-85da-b0f9d3440fc6");

    private static final SellerGroupService.ValidatedSellerGroupParams params =
            new SellerGroupService.ValidatedSellerGroupParams("Best Seller Group");

    @Test
    public void testCreateSellerGroup() {
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                SellerGroupService.saveNewSellerGroup(params, APP_USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }

    @Test
    public void testDeleteSellerGroup() {
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                SellerGroupService.deleteSellerGroup(THE_CARTEL, APP_USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }

    @Test
    public void testAddUserToSellerGroup() {
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                UserSgMappingService.insertMapping(JIMMY, THE_CARTEL, APP_USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }

    @Test
    public void testRemoveUserFromSellerGroup() {
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                UserSgMappingService.deleteMapping(JIMMY, THE_CARTEL, APP_USER);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }
}
