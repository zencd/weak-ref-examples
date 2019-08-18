import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WeakBeansExample {
    static class BeanManager {
        private final Map<String, WeakReference<Bean>> beans = new HashMap<>();

        Bean getBean(String name) {
            synchronized (beans) {
                Bean bean;
                WeakReference<Bean> weak = beans.get(name);
                if (weak == null) {
                    bean = createBeanFull(name);
                } else {
                    bean = weak.get();
                    if (bean == null) {
                        bean = createBeanFull(name);
                    }
                }
                return bean;
            }
        }

        private Bean createBeanFull(String name) {
            Bean bean = new Bean(name);
            WeakReference<Bean> weak = new WeakReference<>(bean);
            beans.put(name, weak);
            return bean;
        }

        int size() {
            synchronized (beans) {
                beans.entrySet().removeIf(it -> it.getValue().get() == null);
                return beans.size();
            }
        }
    }

    static class Bean {
        final String name;
        Bean(String name) { this.name = name; }
        public String toString() { return "bean:" + name; }
        protected void finalize() { System.out.println("  finalize " + this); }
    }

    static void beanUser(BeanManager beanManager, String beanName, long pause) {
        try {
            var bean = beanManager.getBean(beanName);
            Thread.sleep(pause);
            System.out.println("finished using bean " + bean.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BeanManager beanManager = new BeanManager();
        new Thread(() -> beanUser(beanManager, "A", 1000)).start();
        new Thread(() -> beanUser(beanManager, "B", 2000)).start();
        new Thread(() -> beanUser(beanManager, "B", 3000)).start();
        TimeUnit.MILLISECONDS.sleep(500);
        while (beanManager.size() > 0) {
            System.out.println("beans alive: " + beanManager.size());
            System.gc();
            TimeUnit.MILLISECONDS.sleep(800);
        }
        System.out.println("SUCCESS: all created beans has been auto-cleaned");
    }
}
