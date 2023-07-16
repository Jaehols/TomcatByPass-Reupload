package concurrency;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Utils {
    public static final int NUMBER_OF_THREADS = 100;
    public static final String JIMMY = "Jimmy";
    public static final UUID zingerBoxId = UUID.fromString("8770a207-6c72-465e-a1ef-46a128c6ad71");

    public static void waitForLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void waitForThreads(List<Thread> threads) {
        try {
            for (Thread thread: threads) {
                thread.join();
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
}
