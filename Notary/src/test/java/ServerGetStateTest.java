import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

public class ServerGetStateTest {

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
    public void methodGetStateOfGoodSuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", temp.getAnswer());

            // Put good 1 to sell
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            servidor.sell(gson.toJson(pedido));

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request response = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, On-Sale>", response.getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodInvalidGood() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setGoodId(-1);
            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The GoodId -1 Is Not Present In The Server!", temp.getAnswer());

            pedido.setGoodId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The GoodId 0 Is Not Present In The Server!", temp.getAnswer());

            pedido.setGoodId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The GoodId 10 Is Not Present In The Server!", temp.getAnswer());

            // Ensure server is ok after attack
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", temp.getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodInvalidUser() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setGoodId(1);
            pedido.setUserId(-1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", temp.getAnswer());

            pedido.setUserId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", temp.getAnswer());

            pedido.setUserId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", temp.getAnswer());

            pedido.setUserId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", temp.getAnswer());

            // Ensure server is ok after attack
            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", temp.getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodReplayAttack() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", temp.getAnswer());

            // Same request
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("This message has already been processed!", temp.getAnswer());

            // Ensure server is ok after the attack
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", temp.getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodTamperingAttack() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            // Ensure server is ok after the attack
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", temp.getAnswer());
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
