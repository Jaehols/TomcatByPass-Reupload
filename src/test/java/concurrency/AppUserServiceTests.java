package concurrency;

import com.unimelb.tomcatbypass.service.AppUserService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import static concurrency.Utils.*;


public class AppUserServiceTests {
    public static final String USER_NAME = "Fredward";
    public static final String EMAIL = "fredman@gmail.com";
    public static final String PASSWORD = "password";
    public static final String ADDRESS = "77 Louise st, Nedlands, 6009";

    @Test
    public void testCreateNewUser() {
        AppUserService.ValidatedUserParams user = new AppUserService.ValidatedUserParams(USER_NAME, EMAIL, PASSWORD, ADDRESS);

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            threads.add(new Thread(() -> {
                waitForLatch(latch);
                AppUserService.saveNewUser(user);
            }));

            threads.get(i).start();
        }
        latch.countDown();

        waitForThreads(threads);
    }

}
