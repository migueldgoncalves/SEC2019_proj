import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Main {

    public static void main(String[] args) {
        try {
            Server servidor = new Server(0);
            servidor.getSystemState();
            iProxy proxy = servidor;

            LocateRegistry.createRegistry(servidor.PORT);

            Naming.rebind("rmi://localhost:" + servidor.PORT + "/Notary", proxy);

            System.out.println("Server is Running on port " + servidor.PORT + " with the name of Notary");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Press Any Key To exit");
            reader.readLine();
            System.exit(1);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
