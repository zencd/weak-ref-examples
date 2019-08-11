import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class WeakListenersExample {

    static class Listener {

    }

    static final WeakHashMap<Listener, Object> listeners = new WeakHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
        }
        while (true) {
            int size = listeners.size();
            System.out.println("jobs: " + size);
            if (size == 0) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(1000);
            System.gc();
        }
    }
}
