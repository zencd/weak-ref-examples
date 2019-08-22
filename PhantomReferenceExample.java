import org.junit.Assert;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PhantomReferenceExample {

    static class CustomObject {
        final long externalResourceId = System.currentTimeMillis(); // any resource descriptor, etc
    }

    static class CustomObjectCleanup extends PhantomReference<CustomObject> {
        private final long externalResourceId;

        CustomObjectCleanup(CustomObject referent, ReferenceQueue<CustomObject> q) {
            super(referent, q);
            // XXX do not store `referent` reference to this!
            this.externalResourceId = referent.externalResourceId;
        }

        void cleanupResources() {
            System.out.println("PASSED: cleanupResources() invoked for " + externalResourceId);
        }
    }

    private final ReferenceQueue<CustomObject> que = new ReferenceQueue<>();
    private final Set<CustomObjectCleanup> phantoms = new HashSet<>();

    private void markForCleanup(CustomObject referent) {
        CustomObjectCleanup phantom = new CustomObjectCleanup(referent, que);
        phantoms.add(phantom);
    }

    private void cleanupQueuedObjects() {
        try {
            while (true) {
                CustomObjectCleanup aRef = (CustomObjectCleanup) que.remove(); // XXX blocking
                Assert.assertEquals(1, phantoms.size());
                aRef.cleanupResources();
                aRef.clear(); // not required since Java 9
                phantoms.remove(aRef);
                Assert.assertEquals(0, phantoms.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        PhantomReferenceExample example = new PhantomReferenceExample();
        CustomObject referent = new CustomObject();
        example.markForCleanup(referent);
        referent = null; // GC ready

        Thread t = new Thread(example::cleanupQueuedObjects);
        t.setDaemon(true);
        t.start();

        System.gc();
        TimeUnit.MILLISECONDS.sleep(1_000);
    }
}
