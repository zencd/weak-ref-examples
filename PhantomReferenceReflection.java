import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class PhantomReferenceReflection {
    private static final int NUMBER_OF_REFERENCES = 10;

    static class LargeObject {
        private final byte[] space = new byte[1024 * 1024];
        private final int id;

        public LargeObject(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "LargeObject " + id;
        }
    }

    static class GhostReference extends PhantomReference {
        private static final Collection currentRefs = new HashSet();
        private static final Field referent;

        static {
            try {
                referent = Reference.class.getDeclaredField("referent");
                referent.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Field \"referent\" not found");
            }
        }

        public GhostReference(Object referent, ReferenceQueue queue) {
            super(referent, queue);
            currentRefs.add(this);
        }

        public void clear() {
            currentRefs.remove(this);
            super.clear();
        }

        public Object getReferent() {
            try {
                return referent.get(this);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("referent should be accessible!");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final ReferenceQueue queue = new ReferenceQueue();
        new Thread() {
            {
                setDaemon(true);
                start();
            }

            public void run() {
                try {
                    while (true) {
                        System.out.println("waiting for queue");
                        GhostReference ref = (GhostReference) queue.remove();
                        System.out.println("waited for queue");
                        LargeObject obj = (LargeObject) ref.getReferent();
                        System.out.println("GHOST " + obj);
                        ref.clear();
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        };
        for (int i = 0; i < NUMBER_OF_REFERENCES; i++) {
            System.out.println("NEW   " + i);
            // We do not need to keep strong reference to the actual
            // reference anymore, and we also do not need a reverse
            // lookup anymore
            new GhostReference(new LargeObject(i), queue);
        }
        byte[][] buf = new byte[1024][];
        System.out.println("Allocating until OOME...");
        for (int i = 0; i < buf.length; i++) {
            buf[i] = new byte[1024 * 1024];
            TimeUnit.MILLISECONDS.sleep(50);
        }
    }
}
