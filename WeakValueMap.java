import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WeakValueMap<K, V> implements Map<K, V> {

    private final Map<K, WeakReference<V>> map = new HashMap<>();

    @Override
    synchronized public int size() {
        map.entrySet().removeIf(it -> it.getValue().get() == null);
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public V get(Object key) {
        WeakReference<V> weak = map.get(key);
        return weak.get();
    }

    @Override
    public V put(K key, V value) {
        WeakReference<V> weak = new WeakReference<>(value);
        map.put(key, weak);
        return null; // fix it
    }

    @Override
    public V remove(Object key) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public Collection<V> values() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new RuntimeException("not implemented yet");
    }

    public static void main(String[] args) throws InterruptedException {
        var map = new WeakValueMap<String, Object>();
        map.put("A", Arrays.asList("AA"));
        map.put("B", Arrays.asList("BB"));
        System.out.println("map: " + map.size());

        System.out.println("gc");
        System.gc();
        TimeUnit.MILLISECONDS.sleep(900);

        System.out.println("map: " + map.size());
    }
}
