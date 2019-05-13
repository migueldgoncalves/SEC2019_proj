import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public interface iProxy extends Remote {

    String sell(String request) throws RemoteException;

    String getStateOfGood(String request) throws RemoteException;

    String transferGood(String request) throws RemoteException;

    ConcurrentHashMap<Integer, Integer> getNetworkOfNotaries() throws RemoteException;

    void joinNetwork(Integer ID, Integer PORT) throws RemoteException;

    String getServerStatus(int userId) throws RemoteException;

}
