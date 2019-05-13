import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Objects;

public class ServerSellTest {

    private Server servidor;

    private Request state = null;

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
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item was Already On Sale", Objects.requireNonNull(state).getAnswer());

            transferGoodRequestGenerator(1, 1, 2);

            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", Objects.requireNonNull(state).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellInvalidGood() {
        try {
            state = sellGoodRequestGenerator(-1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(0, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(2, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(9, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(10, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
            Assert.fail();
        }
    }

    @Test
    public void methodSellInvalidUser() {
        try {
            state = sellGoodRequestGenerator(1, -1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 0, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 2, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 10, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellWithSellerId() {
        try {
            state = sellGoodRequestGenerator(1, 1, -1, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 1, 1, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item was Already On Sale", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellWithBuyerId() {
        try {
            state = sellGoodRequestGenerator(1, 1, 0, -1, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 1, 0, 1, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item was Already On Sale", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellWithBuyerNonce() {
        try {
            state = sellGoodRequestGenerator(1, 1, 0, 0, -1, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 1, 0, 0, 1, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item was Already On Sale", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellWithBuyerKey() {
        try {
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, -1, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 1, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The Item was Already On Sale", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellInvalidNonce() {
        try {
            long nonce = new Date().getTime();
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, nonce, 1, 0, null);
            Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, nonce, 1, 0, null);
            Assert.assertEquals("This message has already been processed by The Server!", Objects.requireNonNull(state).getAnswer());

            nonce++;
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, nonce, 1, 0, null);
            Assert.assertEquals("The Item was Already On Sale", Objects.requireNonNull(state).getAnswer());

            nonce--;
            nonce--;
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, nonce, 1, 0, null);
            Assert.assertEquals("This message has already been processed by The Server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellInvalidKey() {
        try {
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 2, 0, null);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 3, 0, null);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellWithNotaryId() {
        try {
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, -1, null);
            Assert.assertEquals("As a Notary, you cannot invoke this method!", Objects.requireNonNull(state).getAnswer());

            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 1, null);
            Assert.assertEquals("As a Notary, you cannot invoke this method!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellWithAnswer() {
        try {
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, "answer");
            Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodSellReplayAttack() {
        try {
            long nounce = new Date().getTime();
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, nounce, 1, 0, null);
            Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(state).getAnswer());

            // Same request
            state = sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, nounce, 1, 0, null);
            Assert.assertEquals("This message has already been processed by The Server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(true);
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

            ensureServerIsOkAfterAttack(true);
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

    private Request sellGoodRequestGenerator(int goodId, int userId, int sellerId, int buyerId, long buyerNonce, long buyerKeyId, long nounce, long keyId, int notaryId, String answer) {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setGoodId(goodId);
            pedido.setUserId(userId);
            pedido.setSellerId(sellerId);
            pedido.setBuyerId(buyerId);
            pedido.setBuyerNounce(buyerNonce);
            if(buyerKeyId >= 1 && buyerKeyId <= 9)
                pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + buyerKeyId + ".key"), gson.toJson(pedido)));
            else
                pedido.setBuyerSignature(null);
            pedido.setNounce(nounce);
            pedido.setNotaryId(notaryId);
            pedido.setAnswer(answer);
            if(keyId >= 1 && keyId <= 9)
                pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + keyId + ".key"), gson.toJson(pedido)));
            else
                pedido.setSignature(null);

            return gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Request getGoodStateRequestGenerator(int userId, int goodId) {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(userId);
            pedido.setGoodId(goodId);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + userId + ".key"), gson.toJson(pedido)));

            return gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Request transferGoodRequestGenerator(int goodId, int sellerId, int buyerId) {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            long buyerNonce = new Date().getTime();

            pedido.setGoodId(goodId);
            pedido.setUserId(buyerId);
            pedido.setSellerId(sellerId);
            pedido.setBuyerId(buyerId);
            pedido.setNounce(buyerNonce);
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + buyerId + ".key"), gson.toJson(pedido)));
            pedido.setUserId(sellerId);
            pedido.setNounce(new Date().getTime());
            pedido.setBuyerNounce(buyerNonce);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + sellerId + ".key"), gson.toJson(pedido)));

            return gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ensureServerIsOkAfterAttack(boolean goodOnSale) {
        try {
            if(!goodOnSale) {
                Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null)).getAnswer());
            } else {
                Assert.assertEquals("The Item was Already On Sale", Objects.requireNonNull(sellGoodRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null)).getAnswer());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
