import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class WeakHashMapIllegalUsage {
    public static void main(String[] args) throws InterruptedException {
        Object key = new Object();
        Object value = new Object();
        WeakHashMap<Object, Object> map = new WeakHashMap<>();
        map.put(key, value);
        //key = null;
        value = null;

        for (int i = 0; i < 100; i++) {
            System.out.print(".");
            System.gc();
            if (map.size() == 0) {
                System.out.println();
                System.out.println("SUCCESS");
                System.exit(0);
            }
            TimeUnit.MILLISECONDS.sleep(50);
        }
        System.out.println();
        System.out.println("FAILED - WHM didn't clear");
    }
}
