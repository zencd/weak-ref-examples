import org.junit.Assert;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class PhantomDeadAndResurrectedAndDeadAgain {

    static Data resurrect;

    static class Data {
        final String name;
        Data(String name) { this.name = name; }
        public String toString() { return "Data:" + name; }
        protected void finalize() throws Throwable {
            System.out.println("finalize started for " + toString());
            resurrect = this;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var que = new ReferenceQueue<Data>();
        //var phantom = new PhantomReference<>(new Data("phantomic!"), que);
        var phantom = new WeakReference<>(new Data("weaky!"), que);

        Thread t = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println("waiting for que");
                    Reference removed = null;
                    removed = que.remove(2000);
                    System.out.println("got a ref from queue: " + removed);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();

        System.out.println("gc");
        System.gc();
        TimeUnit.MILLISECONDS.sleep(3000);
        System.out.println("clearing resurrection if any: " + resurrect);
        resurrect = null;

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("gc");
        System.gc();

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("gc");
        System.gc();

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("gc");
        System.gc();

        System.out.println("joining the thread");
        t.join();
    }
}
