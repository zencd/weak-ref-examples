import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Random;

public class WeakReferenceAsPhantom {
    static class CustomObject {
        final int externalResourceId = new Random().nextInt(); // any resource descriptor, etc
    }

    static class CustomFinalizer extends WeakReference<CustomObject> {
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
        ReferenceQueue<CustomObject> referenceQueue = new ReferenceQueue<CustomObject>();
        CustomObject referent = new CustomObject();
        CustomFinalizer weak = new CustomFinalizer(referent, referenceQueue);
        referent = null; // making it GC-ready

        System.gc();

        // you probably need a separate thread for real application
        Reference aRef = referenceQueue.remove(); // XXX it's blocking
        ((CustomFinalizer) aRef).finalizeResources();
        aRef.clear();
    }
}
