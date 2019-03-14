import java.rmi.Remote;

public interface iClient extends Remote {

    String Buy(int ownerId, int newOwnerId, int goodId);

}
