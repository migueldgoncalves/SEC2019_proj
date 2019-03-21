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

        TwoThreads firstThread = new TwoThreads("first", print);
        firstThread.start();
        TwoThreads secondThread = new TwoThreads("second", print);
        secondThread.start();
    }
}
