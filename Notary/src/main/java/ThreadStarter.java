import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadStarter {

    public static void main(String[] args) {

        Print print = new Print();

        /*TwoThreads firstThread = new TwoThreads("first");
        firstThread.start();

        TwoThreads secondThread = new TwoThreads("second");
        secondThread.start();

        TwoThreads thirdThread = new TwoThreads("third");
        thirdThread.start();

        TwoThreads forthThread = new TwoThreads("forth");
        forthThread.start();*/

        /*TwoThreads firstThread = new TwoThreads("first", print);
        firstThread.start();
        TwoThreads secondThread = new TwoThreads("second", print);
        secondThread.start();*/

        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            ExecutorService executorService2 = Executors.newSingleThreadExecutor();
            Future<String> future = executorService.submit(new TwoThreads("first", new Print()));
            Future<String> future2 = executorService2.submit(new TwoThreads("second", new Print()));
            executorService.shutdown();
            executorService2.shutdown();
            System.out.println(future.get());
            System.out.println(future2.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
