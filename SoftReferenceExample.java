import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SoftReferenceExample {

    static volatile boolean finalized = false;

    static class LargeObject {
        final byte[] data = new byte[20_000_000];

        @Override
        protected void finalize() throws Throwable {
            finalized = true;
            System.out.println("finalize()... finalized set to " + finalized);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var soft1 = new SoftReference<>(new LargeObject());

        System.out.println("forcing OOM... wait");
        int i = 0;
        try {
            List<byte[]> memLeak = new ArrayList<>();
            for (; ; i++) {
                if (soft1.get() == null) {
                    System.out.println();
                    System.out.println("SUCCESS: softRef: " + soft1.get() + " at " + i);
                }
                memLeak.add(new byte[10_000_000]);
                if (i > 0 && i % 10 == 0) {
                    System.out.print(".");
                    System.gc();
                }
                TimeUnit.MILLISECONDS.sleep(3);
            }
        } catch (OutOfMemoryError e) {
            System.out.println();
            System.out.println("OOM at " + i + ", finalized? " + finalized);
            System.out.println(finalized ? "PASSED" : "FAIL");
        }
    }
}
