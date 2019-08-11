import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class WeakJobsExample {

    static class Job implements Runnable {
        public void run() {
            try {
                int timeout = new Random().nextInt(5);
                System.out.println(Thread.currentThread().getName() + " sleeping for " + timeout);
                TimeUnit.SECONDS.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static final WeakHashMap<Job, Object> jobs = new WeakHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            Job job = new Job();
            jobs.put(job, "dummy");
            new Thread(job).start();
        }
        while (true) {
            int size = jobs.size();
            System.out.println("jobs: " + size);
            if (size == 0) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(1000);
            System.gc();
        }
    }
}
