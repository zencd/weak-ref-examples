import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

class WeakHashMapExample {
    public static void main(String[] args) throws InterruptedException {
        Map<Object, Object> map = new WeakHashMap<>();
        map.put(new Object(), "dummy");

        System.out.println("map size: " + map.size());
        System.out.println("gc");
        System.gc();
        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("map size: " + map.size());
        System.out.println(map.size() == 0 ? "SUCCESS" : "FAIL");
    }
}