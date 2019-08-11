import java.util.concurrent.TimeUnit;

public class WeakBeansExample {
    static class BeanManager {
        final WeakValueMap<String, Bean> beans = new WeakValueMap<>();

        Bean getBean(String name) {
            synchronized (beans) {
                Bean bean = beans.get(name);
                if (bean == null) {
                    bean = new Bean(name);
                    beans.put(name, bean);
                }
                return bean;
            }
        }
    }

    static class Bean {
        final String name;
        Bean(String name) { this.name = name; }
        public String toString() { return "bean:" + name; }
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
        TimeUnit.MILLISECONDS.sleep(500);
        while (beanManager.beans.size() > 0) {
            System.out.println("beans alive: " + beanManager.beans.size());
            System.gc();
            TimeUnit.MILLISECONDS.sleep(700);
        }
        System.out.println("SUCCESS: all created beans has been auto-cleaned");
    }
}
