import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Main {

    public static void main(String[] args) {
        try {
            //Create an interface object from the implementation class
            iProxy proxy = new Server();

            LocateRegistry.createRegistry(8086);

            Naming.rebind("rmi://localhost:8086/Notary", proxy);

            System.out.println("Server is Running on port 8086 with the name of Notary");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Press Any Key To exit");
            reader.readLine();
            return;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
