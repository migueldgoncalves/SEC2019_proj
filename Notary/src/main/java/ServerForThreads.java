import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerForThreads extends UnicastRemoteObject implements iServerThread {

    public ServerForThreads() throws RemoteException {
    }

    public static void main(String[] args) {
        try {
            //Create an interface object from the implementation class
            iServerThread server = new ServerForThreads();

            LocateRegistry.createRegistry(9090);

            Naming.rebind("rmi://localhost:9090/Notary", server);

            System.out.println("Server is Running on port 9090 with the name of Notary");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Press Any Key To exit");
            reader.readLine();
            System.exit(1);

            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public String wait(int time, String name) {
        try {
            Thread.sleep(time);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            return ("I'm back, client " + name + " " + dateFormat.format(date));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public String wait2(int time, String name) {
        try {
            Thread.sleep(time);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            return ("I'm back with new functions, client " + name + " " + dateFormat.format(date));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}