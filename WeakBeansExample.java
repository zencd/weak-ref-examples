import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class WeakBeansExample {
    static class BeanManager {
        WeakHashMap<Object, String> beans = new WeakHashMap<>();

        Object getBean(String name) {
            List<String> bean = Arrays.asList("bean:" + name);
            beans.put(bean, null);
            return bean;
        }
    }

    static void beanUser(BeanManager beanManager, String beanName, long pause) {
        try {
            var bean = beanManager.getBean(beanName);
            System.out.println("someone started using bean " + bean.toString());
            Thread.sleep(pause);
            System.out.println("finished using bean " + bean.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BeanManager beanManager = new BeanManager();
        new Thread(() -> beanUser(beanManager, "A", 2000)).start();
        new Thread(() -> beanUser(beanManager, "B", 4000)).start();
        TimeUnit.MILLISECONDS.sleep(500);
        while (beanManager.beans.size() > 0) {
            System.out.println("beans alive: " + beanManager.beans.size());
            System.gc();
            TimeUnit.MILLISECONDS.sleep(700);
        }
        System.out.println("SUCCESS: all created beans has been auto-cleaned");
    }
}
