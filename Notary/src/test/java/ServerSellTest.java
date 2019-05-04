import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

public class ServerSellTest {

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
    public void methodSellSuccess() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());

            pedido.setUserId(9);
            pedido.setGoodId(9);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User9.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellInvalidUser() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(-1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            pedido.setUserId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            pedido.setUserId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            pedido.setUserId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            // Ensure server is ok after the attack
            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellTwiceGood() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item was Already On Sale", temp.getAnswer());

            // Ensure server is ok after the attack
            pedido.setUserId(2);
            pedido.setGoodId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
            Assert.fail();
        }
    }

    @Test
    public void methodSellInvalidGoodId() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(-1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", temp.getAnswer());

            pedido.setGoodId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", temp.getAnswer());

            pedido.setGoodId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", temp.getAnswer());

            pedido.setGoodId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", temp.getAnswer());

            // Ensure server is ok after the attack
            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
            Assert.fail();
        }
    }

    @Test
    public void methodSellReplayAttack() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());

            // Same request
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("This message has already been processed by The Server!", temp.getAnswer());

            // Ensure server is ok after the attack
            pedido.setUserId(2);
            pedido.setGoodId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellTamperingAttack() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            // Ensure server is ok after the attack
            pedido.setUserId(2);
            pedido.setGoodId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null); //This line is needed before setting signature
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", temp.getAnswer());
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
