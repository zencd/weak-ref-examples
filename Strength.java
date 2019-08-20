import java.lang.ref.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Strength {
    private static class Data {}

    public static void main(String[] args) throws InterruptedException {
        System.out.println("adding weak, soft and phantom references per one object");
        System.out.println("waiting for a reference from each queue");

        var data = new Data();

        var weakQue = new ReferenceQueue<Data>();
        var weakQueued = new AtomicBoolean(false);
        var weak = new WeakReference<Data>(data, weakQue);

        var softQue = new ReferenceQueue<Data>();
        var softQueued = new AtomicBoolean(false);
        var soft = new SoftReference<Data>(data, softQue);

        var phantomQue = new ReferenceQueue<Data>();
        var phantomQueued = new AtomicBoolean(false);
        var phantom = new PhantomReference<Data>(data, phantomQue);

        data = null; // GC ready

        Thread t1 = watchQue(weakQue, weakQueued);
        Thread t2 = watchQue(softQue, softQueued);
        Thread t3 = watchQue(phantomQue, phantomQueued);

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("forcing OOM");

        try {
            final var leaks = new ArrayList();
            for (int i = 0;; i++) {
                if (i % 50 == 0) {
                    System.out.println("gc");
                }
                System.gc();
                TimeUnit.MILLISECONDS.sleep(50);
                leaks.add(new byte[20_000_000]);
            }
        } catch (OutOfMemoryError e) {
            System.out.println("OOM caught");
        }

        t1.join();
        t2.join();
        t3.join();

        boolean passed = weakQueued.get() && softQueued.get() && phantomQueued.get();
        System.out.println(passed ? "PASSED" : "FAIL");
    }

    static Thread watchQue(ReferenceQueue<Data> que, AtomicBoolean flag) {
        Thread t = new Thread(() -> {
            try {
                Reference<? extends Data> ref = que.remove(30_000);
                if (ref != null) {
                    flag.set(true);
                    System.out.println("  got reference: " + ref);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        return t;
    }
}
