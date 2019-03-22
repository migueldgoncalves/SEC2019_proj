import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Client2ForThreads extends UnicastRemoteObject implements iClientThread {

    public iServerThread server;

    public Client2ForThreads() throws RemoteException {
    }

    public static void main(String[] args) {
        try {
            //Prompt User For Input of Port To Register
            iClientThread client = new ClientForThreads();

            LocateRegistry.createRegistry(9092);

            Naming.rebind("rmi://localhost:" + 9092 + "/" + 2, client);

            //End Of Client Registration in RMI

            ((ClientForThreads) client).server = (iServerThread) Naming.lookup("rmi://localhost:9090/Notary");
            iClientThread client2 = (iClientThread) Naming.lookup("rmi://localhost:9091/1");

            System.out.println("Waiting");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            reader.readLine();

            //System.out.println(server.wait(0, "second"));

            //System.out.println(((ClientForThreads) client).server.wait(8000, "second"));

            Runnable r = new Runnable() {
                public void run() {
                    try {
                        System.out.println(client.doSomething1(8000, "first"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            new Thread(r).start();

            System.out.println(client.doSomething2(0, "second"));

        } catch (ConnectException e) {
            System.out.println("Could not connect to server. The server may be offline or unavailable due to network reasons.");
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public String doSomething1(int time, String name) {
        try {
            System.out.println("I have reached " + name + " to run function 1");
            return server.wait(8000, "first");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public String doSomething2(int time, String name) {
        try {
            System.out.println("I have reached " + name + " to run function 2");
            return server.wait2(time, name);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error2";
        }
    }
}