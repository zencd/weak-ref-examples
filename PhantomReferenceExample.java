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

    private void cleanupQueuedObjects() throws InterruptedException {
        // you probably need to do it in a separate thread
        System.out.println("awaiting queue");
        CustomObjectCleanup aRef = (CustomObjectCleanup)que.remove(5_000); // XXX it's blocking
        Assert.assertNotNull("the phantom reference never queued", aRef);
        Assert.assertNull(aRef.get());
        aRef.cleanupResources();
        aRef.clear(); // not required since Java 9
        phantoms.remove(aRef);
        Assert.assertEquals(0, phantoms.size());
    }

    public static void main(String[] args) throws Exception {
        PhantomReferenceExample example = new PhantomReferenceExample();
        CustomObject referent = new CustomObject();
        example.markForCleanup(referent);
        referent = null; // GC ready
        System.gc();
        TimeUnit.MILLISECONDS.sleep(500);
        example.cleanupQueuedObjects(); // use a dedicated thread
    }
}
