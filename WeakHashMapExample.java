import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

class WeakHashMapExample {
    static class Person {}

    static class PersonExt {}

    public static void main(String[] args) throws InterruptedException {
        Person person = new Person();
        PersonExt ext = new PersonExt();
        WeakHashMap<Person, PersonExt> map = new WeakHashMap<>();
        map.put(person, ext);
        System.out.println("map size: " + map.size());
        person = null;
        ext = null;

        for (int i = 0; i < 100; i++) {
            System.out.println("map size: " + map.size());
            System.gc();
            if (map.size() == 0) {
                System.out.println("SUCCESS");
                break;
            }
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }
}