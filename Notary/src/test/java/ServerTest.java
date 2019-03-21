import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {

    private Server servidor;

    @Before
    public void setUp() {
        try {
            servidor = new Server("src\\main\\resources\\GoodsFile1.xml");
        } catch (Exception e) {
            Assert.fail();
            System.out.println("Something Went Wrong In The System");
        }
    }

    @Test
    public void methodSellSuccess() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            String temp = servidor.sell(gson.toJson(pedido));
            Assert.assertEquals("The Item is Now on Sale", temp);
            temp = servidor.sell(gson.toJson(pedido));
            Assert.assertEquals("The Item was Already On Sale", temp);
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
            Assert.fail();
        }
    }

    @Test
    public void methodSellUnsuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(0);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            String temp = servidor.sell(gson.toJson(pedido));
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", temp);
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodSuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            String temp = servidor.getStateOfGood(gson.toJson(pedido));
            Assert.assertEquals("<1, Not-On-Sale>", temp);
            pedido.setUserId(1);
            servidor.sell(gson.toJson(pedido));
            temp = servidor.getStateOfGood(gson.toJson(pedido));
            Assert.assertEquals("<1, On-Sale>", temp);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodUnsuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setGoodId(10);
            pedido.setUserId(1);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            String temp = servidor.getStateOfGood(gson.toJson(pedido));
            Assert.assertEquals("The GoodId 10 Is Not Present In The Server!", temp);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodSuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            String state = servidor.getStateOfGood(gson.toJson(pedido));
            Assert.assertEquals("<1, Not-On-Sale>", state);
            state = servidor.sell(gson.toJson(pedido));
            Assert.assertEquals("The Item is Now on Sale", state);
            pedido.setUserId(1);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            String temp = servidor.transferGood(gson.toJson(pedido));
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp);
            Assert.assertEquals("<2, Not-On-Sale>", servidor.getStateOfGood(gson.toJson(pedido)));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void methodWriteStateAtomically() {
        servidor.saveServerState("Backups/");
        servidor.getSystemState();
    }

    @After
    public void tearDown() {

    }
}
