import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public class Main {

    public static void main(String args[]){
        try {
            //Create an interface object from the implementation class
            iProxy proxy = new Server();

            LocateRegistry.createRegistry(8086);

            Naming.rebind("localhost:8086/Notary", proxy);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
