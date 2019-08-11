import org.junit.Assert;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class WeakAndPhantomDifference {

    static volatile Data resurrected;
    static volatile int resurrectedInt;

    static class Data {
        final String name;
        Data(String name) { this.name = name; }
        public String toString() { return "Data:" + name; }
        protected void finalize() throws Throwable {
            System.out.println("finalize started for " + name);
            //TimeUnit.MILLISECONDS.sleep(500);
            //System.out.println("finalize completed for " + name);
            //resurrected = this;
            //resurrectedInt = 500;
        }
    }
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Java: " + System.getProperty("java.vm.version"));
        var phantomQue = new ReferenceQueue<Data>(); var phantom = new PhantomReference<>(new Data("phantom-data"), phantomQue); onePass("phantom", phantomQue);
        var weakQue = new ReferenceQueue<Data>(); var weak = new WeakReference<>(new Data("weak-data"), weakQue); onePass("weak", weakQue);
    }

    static void onePass(String name, ReferenceQueue que) throws InterruptedException {
        System.out.println("===== Pass " + name + " =====");

        Thread t = new Thread(() -> {
            try {
                System.out.println("waiting for " + que);
                Reference removed = null;
                removed = que.remove(5000);
                System.out.println("got a ref from queue: " + removed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();

        for (int i = 0; i < 4; i++) {
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println("gc #" + i);
            System.gc();
        }

        System.out.println("joining the thread");
        t.join();
    }
}
