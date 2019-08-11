import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class RefWeakAndSoft {
    public static void main(String[] args) throws InterruptedException {
        final int WEAK_SIZE = 20_000_000;
        final int LEAK_INCREMENT = 2_000_000;

        WeakHashMap xxx;

        WeakReference<byte[]> weak = new WeakReference<>(new byte[WEAK_SIZE]);
        SoftReference<byte[]> soft = new SoftReference<>(new byte[WEAK_SIZE]);
        List<byte[]> memLeak = new ArrayList<>();

        boolean weakReported = false;
        boolean softReported = false;
        int i = 0;
        try {
            for (; ; i++) {
                if (!weakReported && null == weak.get()) {
                    System.out.println("WEAK FREED at " + i);
                    weakReported = true;
                }
                if (!softReported && null == soft.get()) {
                    System.out.println("SOFT FREED at " + i);
                    softReported = true;
                }
                memLeak.add(new byte[LEAK_INCREMENT]);

                if (i > 0 && i % 500 == 0) {
                    System.out.println("gc() at " + i);
                    System.gc();
                }
                TimeUnit.MILLISECONDS.sleep(3);
            }
        } catch (OutOfMemoryError e) {
            System.out.println("OOM at " + i);
        }
    }
}
