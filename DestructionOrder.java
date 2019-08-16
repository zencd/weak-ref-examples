import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class DestructionOrder {

    static class Data {
        protected void finalize() {
            System.out.println("order: " + (ref.get() == null ? "clear -> finalize" : "finalize -> clear"));
        }
    }

    static Reference ref;

    public static void main(String[] args) throws InterruptedException {
        //ref = new WeakReference<>(new Data());
        ref = new PhantomReference(new Data(), null);

        System.out.println("gc");
        System.gc();
        TimeUnit.MILLISECONDS.sleep(1000);
    }
}
