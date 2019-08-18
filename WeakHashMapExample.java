import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

class WeakHashMapExample {
    static class Data {}

    public static void main(String[] args) throws InterruptedException {
        Map<Data, String> map = new WeakHashMap<>();
        //Map<Data, String> map = new HashMap<>();
        map.put(new Data(), "dummy");

        for (int i = 0; i < 3; i++) {
            System.out.println("map size: " + map.size() + " ... gc");
            System.gc();
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }
}