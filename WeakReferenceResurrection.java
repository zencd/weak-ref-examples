import org.junit.Assert;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WeakReferenceResurrection {
    public static void main(String[] args) {
        ReferenceQueue<Object> que = new ReferenceQueue<Object>();
        WeakReference weak = new WeakReference<>(new Object(), que);

        System.out.println("gc...");
        System.gc();

        Reference polled;
        while ((polled = que.poll()) == null) {
            System.out.println("not GC'ed yet");
        }
        System.out.println("ref appeared in queue: " + polled);
        System.out.println("its referent: " + polled.get());
        polled.clear();
    }
}
