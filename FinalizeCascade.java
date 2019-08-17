import java.util.concurrent.TimeUnit;

public class FinalizeCascade {

    static class A {
        protected void finalize() throws Throwable {
            System.out.println("A::finalize");
        }
    }

    static class B extends A {
        protected void finalize() throws Throwable {
            System.out.println("B::finalize");
        }
    }

    static class C extends B {
    }

    public static void main(String[] args) throws InterruptedException {
        C c = new C();
        c = null;
        System.gc();
        TimeUnit.SECONDS.sleep(1);
    }
}
