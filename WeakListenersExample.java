import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class WeakListenersExample {

    interface Listener {
        void process();
    }

    static class Manager {
        // WeakHashSet не завезли, так что newSetFromMap()
        final Set<Listener> listeners = Collections.newSetFromMap(new WeakHashMap<>());

        void addEventListener(Listener listener) {
            listeners.add(listener);
        }
    }

    private static void clientThread(Manager eventManager) {
        Listener listener = new Listener() {
            // XXX do not "optimize" it to lambda
            public void process() {
                System.out.println("a listener reacts on event");
            }
        };
        eventManager.addEventListener(listener);
    }

    public static void main(String[] args) throws InterruptedException {
        var manager = new Manager();

        for (int i = 0; i < 3; i++) {
            clientThread(manager);
        }

        System.out.println("notifying listeners...");
        manager.listeners.forEach(Listener::process);

        while (true) {
            int size = manager.listeners.size();
            System.out.println("listeners: " + size);
            if (size == 0) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(500);
            System.gc();
        }
        System.out.println("SUCCESS: no listener remains");

        System.out.println("notifying listeners...");
        manager.listeners.forEach(Listener::process);
    }
}
