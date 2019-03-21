public class TwoThreads implements Runnable {
    String name;
    Print print;
    private Thread t;

    public TwoThreads(String name) {
        this.name = name;
        System.out.println("I am thread " + name);
        System.out.println(name + " ready to start");
    }

    public TwoThreads(String name, Print print) {
        this.name = name;
        this.print = print;
        System.out.println("I am thread " + name);
        System.out.println(name + " ready to start");
    }

    public void run() {
        /*try {
            for (int i = 0; i < 10; i++) {
                System.out.println(name+i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println(name + " interrupted");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(name + " exiting");*/
        System.out.println(name);
        print.printCount();
    }

    public void start() {
        System.out.println("Starting thread");
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }
}
