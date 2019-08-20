import java.lang.ref.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Strength {
    private static class Data {
        protected void finalize() throws InterruptedException {
            //System.out.println("finalize 1");
            //TimeUnit.MILLISECONDS.sleep(1000);
            //System.out.println("finalize 2");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("adding weak, soft and phantom references per a single object");
        System.out.println("awaiting a reference from each queue");

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
        forceOOM();

        t1.join();
        t2.join();
        t3.join();

        boolean weakOk = weakQueued.get();
        boolean softOk = softQueued.get();
        boolean phantomOk = phantomQueued.get();
        boolean passed = weakOk && softOk && phantomOk;
        System.out.println("weakOk: " + weakOk);
        System.out.println("softOk: " + softOk);
        System.out.println("phantomOk: " + phantomOk);
        System.out.println(passed ? "PASSED" : "FAIL");
    }

    private static Thread watchQue(ReferenceQueue<Data> que, AtomicBoolean flag) {
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

    private static void forceOOM() throws InterruptedException {
        try {
            System.out.println("forcing OOM");
            //noinspection MismatchedQueryAndUpdateOfCollection
            final var leaks = new ArrayList();
            //noinspection InfiniteLoopStatement
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
            TimeUnit.MILLISECONDS.sleep(200);
            System.gc();
            TimeUnit.MILLISECONDS.sleep(200);
            System.gc();
        }
    }
}
