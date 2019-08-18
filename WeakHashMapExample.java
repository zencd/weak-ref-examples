import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

class WeakHashMapExample {
    public static void main(String[] args) throws InterruptedException {
        Map<Object, Object> map = new WeakHashMap<>();
        //Map<Data, String> map = new HashMap<>();
        map.put(new Object(), "dummy");

        for (int i = 0; i < 3; i++) {
            System.out.println("map size: " + map.size() + " ... gc");
            System.gc();
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }
}