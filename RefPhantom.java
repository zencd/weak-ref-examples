import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

public class RefPhantom {
    public static void main(String[] args) {
        List<byte[]> memLeak = new ArrayList<>();

        ReferenceQueue<StringBuffer> phantomQue = new ReferenceQueue<>();
        PhantomReference<StringBuffer> phantom = new PhantomReference<>(new StringBuffer(), phantomQue);
    }
}
