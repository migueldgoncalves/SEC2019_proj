import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientForThreads extends UnicastRemoteObject implements Callable<String>, iClientThread {

    public iServerThread server;

    public int time = 8000;
    public String name = "first";
    public int functionToCall = 0;

    public ClientForThreads() throws RemoteException {
    }

    public static void main(String[] args) {
        try {
            //Prompt User For Input of Port To Register
            iClientThread client = new ClientForThreads();

            LocateRegistry.createRegistry(9091);

            Naming.rebind("rmi://localhost:" + 9091 + "/" + 1, client);

            //End Of Client Registration in RMI

            ((ClientForThreads) client).server = (iServerThread) Naming.lookup("rmi://localhost:9090/Notary");

            System.out.println("Waiting");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            reader.readLine();

            //System.out.println(server.wait(8000, "first"));

            //System.out.println(((ClientForThreads) client).server.wait(8000, "first"));
            //System.out.println(((ClientForThreads) client).server.wait(0, "second"));

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            ExecutorService executorService2 = Executors.newSingleThreadExecutor();
            Future<String> future = executorService.submit((Callable<String>) client);
            Future<String> future2 = executorService2.submit((Callable<String>) client);
            ((ClientForThreads) client).functionToCall = 1;
            executorService.shutdown();
            executorService2.shutdown();
            System.out.println(future.get());
            System.out.println(future2.get());

        } catch (ConnectException e) {
            System.out.println("Could not connect to server. The server may be offline or unavailable due to network reasons.");
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public String call() {
        if (functionToCall == 1) {
            functionToCall = 2;
            return doSomething1(8000, "first");
        }
        if (functionToCall == 2)
            return doSomething2(0, "second");
        return "Error";
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
