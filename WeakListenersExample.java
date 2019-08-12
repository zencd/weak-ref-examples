import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class WeakListenersExample {

    interface Listener {
        void process();
    }

    // WeakHashSet не завезли, так что newSetFromMap()
    final Set<Listener> listeners = Collections.newSetFromMap(new WeakHashMap<>());

    private void addEventListener(Listener listener) {
        listeners.add(listener);
    }

    static void client(WeakListenersExample eventManager) {
        Listener listener = new Listener() {
            // XXX do not "optimize" it to lambda!
            public void process() {
                System.out.println("a listener reacts on event");
            }
        };
        eventManager.addEventListener(listener);
    }

    public static void main(String[] args) throws InterruptedException {
        var eventManager = new WeakListenersExample();

        for (int i = 0; i < 3; i++) {
            client(eventManager);
        }

        System.out.println("notifying listeners...");
        eventManager.listeners.forEach(Listener::process);

        while (true) {
            int size = eventManager.listeners.size();
            System.out.println("listeners: " + size);
            if (size == 0) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(500);
            System.gc();
        }

        System.out.println("notifying listeners...");
        eventManager.listeners.forEach(Listener::process);
    }

}
