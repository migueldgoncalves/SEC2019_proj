import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

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
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item was Already On Sale", temp.getAnswer());
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
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", temp.getAnswer());
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
            Random random = new Random();
            pedido.setNounce(random.nextInt());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", temp.getAnswer());
            //pedido.setUserId(1);
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            servidor.sell(gson.toJson(pedido));
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request response = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, On-Sale>", response.getAnswer());
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
            Request temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The GoodId 10 Is Not Present In The Server!", temp.getAnswer());
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
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());
            pedido.setUserId(1);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setSignature(null);
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());
            pedido.setNounce(new Random().nextInt());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void replayAttackTest() {
        try {
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(1);
            Gson gson = new Gson();
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv("src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            System.out.println(servidor.getStateOfGood(gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("This message has already been processed!", temp.getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodWriteStateAtomically() {
        //servidor.saveServerState();
        //servidor.getSystemState();
    }

    @After
    public void tearDown() {

    }
}
