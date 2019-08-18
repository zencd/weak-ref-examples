import java.lang.ref.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Strength {
    static class Data {
        protected void finalize() throws InterruptedException {
            //System.out.println("finalize " + this);
            //TimeUnit.MILLISECONDS.sleep(900);
            //System.out.println("finalize completed " + this);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        weak_vs_phantom_on_the_same_referent();
        weak_vs_soft_on_the_same_referent();
    }

    static void weak_vs_phantom_on_the_same_referent() throws InterruptedException {
        System.out.println("===== weak_vs_phantom ====");

        var data = new Data();

        var weakQue = new ReferenceQueue<Data>();
        var weak = new WeakReference<Data>(data, weakQue);

        var phantomQue = new ReferenceQueue<Data>();
        var phantom = new PhantomReference<Data>(data, phantomQue);

        data = null;

        Thread t1 = watchQue(weakQue, 10_000);
        Thread t2 = watchQue(phantomQue, 10_000);

        TimeUnit.MILLISECONDS.sleep(500);

        for (int i = 0; i < 4; i++) {
            System.out.println("gc");
            System.gc();
            TimeUnit.MILLISECONDS.sleep(500);
        }

    }

    static void weak_vs_soft_on_the_same_referent() throws InterruptedException {
        System.out.println("===== weak_vs_soft ====");

        var data = new Data();

        var weakQue = new ReferenceQueue<Data>();
        var weak = new WeakReference<Data>(data, weakQue);

        var softQue = new ReferenceQueue<Data>();
        var soft = new SoftReference<Data>(data, softQue);

        data = null;

        Thread t1 = watchQue(weakQue, 1000_000);
        Thread t2 = watchQue(softQue, 1000_000);

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
            System.out.println("OOM: ok");
        }
    }

    static Thread watchQue(ReferenceQueue<Data> que, long timeout) {
        Thread t = new Thread(() -> {
            try {
                Reference<? extends Data> ref = null;
                ref = que.remove(timeout);
                System.out.println("  got reference: " + ref);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        return t;
    }
}
