import org.junit.Assert;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Random;

public class PhantomReferenceExample {

    static class CustomObject {
        final int externalResourceId = new Random().nextInt(); // any resource descriptor, etc
    }

    static class CustomFinalizer extends PhantomReference<CustomObject> {
        private final int externalResourceId;

        public CustomFinalizer(CustomObject referent, ReferenceQueue<CustomObject> q) {
            super(referent, q);
            // XXX do not store `referent` reference to this!
            this.externalResourceId = referent.externalResourceId;
        }

        public void finalizeResources() {
            System.out.println("PASSED: finalizeResources() invoked for " + externalResourceId);
        }
    }

    public static void main(String[] args) throws Exception {
        ReferenceQueue que = new ReferenceQueue<CustomObject>();
        CustomObject referent = new CustomObject();
        CustomFinalizer phantom = new CustomFinalizer(referent, que);
        referent = null; // GC-ready

        System.gc();

        // you probably need a separate thread for real application
        CustomFinalizer aRef = (CustomFinalizer)que.remove(); // XXX it's blocking
        Assert.assertNull(aRef.get());
        aRef.finalizeResources();
        aRef.clear(); // not required since Java 9
    }
}
