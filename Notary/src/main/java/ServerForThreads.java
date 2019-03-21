public class ServerForThreads {

    public String wait(int time, String name) {
        try {
            Thread.sleep(time);
            return ("I'm back, client " + name);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }

    }
}
