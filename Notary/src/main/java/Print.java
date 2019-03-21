public class Print {

    public void printCount() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                Thread.sleep(100);
            }
        } catch (Exception e) {
            System.out.println("Interrupted");
        }
    }
}
