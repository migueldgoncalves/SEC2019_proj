public class AnotherThread extends Thread {
    private Thread t;
    private String name;

    public AnotherThread(String name) {
        this.name = name;
        System.out.println("I am thread " + name);
        System.out.println(name + " ready to start");
    }

    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println(name + i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println(name + " interrupted");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(name + " exiting");
    }

    public void start() {
        System.out.println("Starting thread");
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }
}
