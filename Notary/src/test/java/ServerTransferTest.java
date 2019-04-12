import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

public class ServerTransferTest {

    private Server servidor;

    @Before
    public void setUp() {
        try {
            servidor = new Server("src\\main\\resources\\GoodsFile1.xml");

            //Cleans server backup file
            PrintWriter writer = new PrintWriter(new File(System.getProperty("user.dir") + "\\Backups\\ServerState.old"));
            writer.println("");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong In The System");
        }
    }

    @Test
    public void methodTransferGoodSuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());
            pedido.setUserId(1);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setSignature(null);
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @After
    public void tearDown() {
        try {
            //Cleans server backup file
            PrintWriter writer = new PrintWriter(new File(System.getProperty("user.dir") + "\\Backups\\ServerState.old"));
            writer.println("");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
