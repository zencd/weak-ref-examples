import org.junit.Assert;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.TimeUnit;

public class PhantomDeadAndResurrectedAndDeadAgain {

    static volatile Data resurrect;
    static volatile Reference<Data> ref;

    static class Data {
        final String name;
        Data(String name) { this.name = name; }
        public String toString() { return "Data:" + name; }
        protected void finalize() {
            System.out.println("finalize(): resurrecting " + toString());
            resurrect = this;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var que = new ReferenceQueue<Data>();
        ref = new PhantomReference<>(new Data("phantomic!"), que);
        //ref = new WeakReference<>(new Data("weaky!"), que);

        Thread t1 = new Thread(() -> {waitForQueue(que);}); t1.start();

        //Thread t2 = new Thread(() -> {useResurrect();}); t2.start();

        System.out.println("gc");
        System.gc();
        TimeUnit.MILLISECONDS.sleep(2000);
        Assert.assertNotNull(resurrect);
        System.out.println("clearing resurrect: " + resurrect);
        resurrect = null;

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("gc");
        System.gc();

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("gc");
        System.gc();

        //System.out.println("joining the threads");
        t1.join();
        //t2.join();
    }

    private static void waitForQueue(ReferenceQueue<Data> que) {
        long started = System.currentTimeMillis();
        while (System.currentTimeMillis() - started < 10_000) {
            try {
                Reference removed = null;
                removed = que.remove(300);
                if (removed != null) {
                    System.out.println("    got a ref from queue: " + removed);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("    stopped watching the queue");
    }

    private static void useResurrect() {
        Data resurrectCopy = null;
        long started = System.currentTimeMillis();
        while (System.currentTimeMillis() - started < 10_000) {
            try {
                final Data resurrect = PhantomDeadAndResurrectedAndDeadAgain.resurrect;
                if (resurrectCopy == null) {
                    if (resurrect != null) {
                        System.out.println("        found resurrect: " + resurrect);
                        resurrectCopy = resurrect;
                    }
                } else {
                    System.out.println("        using resurrect: " + resurrectCopy + " / " + ref.get());
                }
                TimeUnit.MILLISECONDS.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("        stopped using resurrect");
    }
}
