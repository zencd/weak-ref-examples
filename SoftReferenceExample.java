import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SoftReferenceExample {

    public static void main(String[] args) throws InterruptedException {
        final int WEAK_SIZE = 20_000_000;
        final int LEAK_INCREMENT = 9_000_000;

        Map<String, SoftReference<byte[]>> imageCache = new HashMap<>();
        byte[] image = new byte[20_000_000];
        imageCache.put("1.jpg", new SoftReference<>(image));
        image = null;

        WeakReference<byte[]> weak = new WeakReference<>(new byte[WEAK_SIZE]);
        SoftReference<byte[]> soft = new SoftReference<>(new byte[WEAK_SIZE]);


        int i = 0;
        try {
            List<byte[]> memLeak = new ArrayList<>();
            for (; ; i++) {
                SoftReference<byte[]> softRef = imageCache.get("1.jpg");
                if (softRef.get() == null) {
                    System.out.println("SUCCESS: softRef: " + softRef.get() + " at " + i);
                }
                memLeak.add(new byte[LEAK_INCREMENT]);
                if (i > 0 && i % 500 == 0) {
                    System.gc();
                }
                TimeUnit.MILLISECONDS.sleep(3);
            }
        } catch (OutOfMemoryError e) {
            System.out.println("OOM at " + i);
        }
    }
}
