import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class WeakWithBadFinalize {

    static List<Custom> leaks = new ArrayList<>();

    static class Custom {
        byte[] large = new byte[200_000_000];
        @Override
        protected void finalize() throws Throwable {
            System.out.println("finalize " + this);
            leaks.add(this); // XXX leads to OOM
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var map = new WeakHashMap<Custom, String>();
        for (int i = 0;; i++) {
            System.out.println("allocation #" + i);
            var referent = new Custom();
            //var ref = new WeakReference<>(referent);
            map.put(referent, "dummy");
            if (i % 1000 == 0) {
                System.gc();
                TimeUnit.MILLISECONDS.sleep(500);
            }
        }
    }
}
