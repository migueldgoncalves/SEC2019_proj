import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Objects;

public class ServerTransferTest {

    private Server servidor;

    private Request state = null;

    @Before
    public void setUp() {
        try {
            //Cleans server backup file
            PrintWriter writer = new PrintWriter(new File(System.getProperty("user.dir") + "\\Backups\\ServerState.old"));
            writer.println("");
            writer.close();

            servidor = new Server("src\\main\\resources\\GoodsFile1.xml");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong In The System");
        }
    }

    @Test
    public void methodTransferGoodSuccessful() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            sellGoodRequestGenerator(1, 1);

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good with AnswerClasses.Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidGood() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(-1, 2, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(0, 2, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(2, 2, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(9, 2, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(10, 2, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidBuyerUser() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, -1, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 0, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 1, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 9, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 10, 1, 2, buyerNonce,2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidSellerGoodNotAtSale() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, -1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 0, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 2, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 9, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 10, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidSellerGoodAtSale() {
        try {
            sellGoodRequestGenerator(1, 1);

            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, -1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 0, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 2, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 9, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 10, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidBuyerGoodNotAtSale() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, -1, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 0, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 1, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 9, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 10, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidBuyerGoodAtSale() {
        try {
            sellGoodRequestGenerator(1, 1);

            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, -1, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 0, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 1, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 9, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 10, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidBuyerNonce() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce++;
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("This message from Buyer has already been processed", Objects.requireNonNull(state).getAnswer());

            buyerNonce--;
            buyerNonce--;
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("This message from Buyer has already been processed", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidBuyerKey() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 1, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 3, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidSellerUser() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, -1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 0, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 2, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 9, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 10, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidSellerNonce() {
        try {
            long buyerNonce = new Date().getTime();
            long sellerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, sellerNonce, buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            sellerNonce++;
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, sellerNonce, buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, sellerNonce, buyerNonce, 1, 0, null);
            Assert.assertEquals("This message from Seller has already been processed", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            sellerNonce--;
            sellerNonce--;
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, sellerNonce, buyerNonce, 1, 0, null);
            Assert.assertEquals("This message from Seller has already been processed", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodBuyerNonceChangedInServer() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce + 1, 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidSellerKey() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 2, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 3, 0, null);
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodNotaryIdInRequest() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, -1, null);
            Assert.assertEquals("As a Notary, you cannot invoke this method!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 1, null);
            Assert.assertEquals("As a Notary, you cannot invoke this method!", Objects.requireNonNull(state).getAnswer());

            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 2, null);
            Assert.assertEquals("As a Notary, you cannot invoke this method!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodAnswerInRequest() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, "answer");
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodReplayAttackBetweenBuyerAndSeller() {
        try {
            long buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            //Same request from Buyer
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("This message from Buyer has already been processed", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodReplayAttackBetweenSellerAndNotary() {
        try {
            long buyerNonce = new Date().getTime();
            long sellerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, sellerNonce, buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good Id, Owner Id or New Owner ID is not present in the server!", Objects.requireNonNull(state).getAnswer());

            // Same request from Seller
            buyerNonce = new Date().getTime();
            state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, sellerNonce, buyerNonce, 1, 0, null);
            Assert.assertEquals("This message from Seller has already been processed", Objects.requireNonNull(state).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodTamperingAttackBetweenBuyerAndSeller() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            long buyerNonce = new Date().getTime();

            pedido.setGoodId(1);
            pedido.setUserId(2);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(buyerNonce);
            pedido.setNotaryId(0);
            pedido.setAnswer(null);
            pedido.setBuyerSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 2), gson.toJson(pedido)));
            pedido.setAnswer("answer"); //Tampering
            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setBuyerNounce(buyerNonce);
            pedido.setSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 1), gson.toJson(pedido)));
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class).getAnswer());

            pedido = new Request();

            pedido.setGoodId(1);
            pedido.setUserId(2);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(buyerNonce);
            pedido.setNotaryId(0);
            pedido.setAnswer(null);
            pedido.setBuyerSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 2), gson.toJson(pedido)));
            pedido.setBuyerSignature(null); //Tampering
            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setBuyerNounce(buyerNonce);
            pedido.setSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 1), gson.toJson(pedido)));
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class).getAnswer());

            pedido = new Request();

            pedido.setGoodId(1);
            pedido.setUserId(2);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(buyerNonce);
            pedido.setNotaryId(0);
            pedido.setAnswer(null);
            pedido.setBuyerSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 2), gson.toJson(pedido)));
            pedido.setNounce(0); //Tampering
            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setBuyerNounce(0); //The value received in the tampered message
            pedido.setSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 1), gson.toJson(pedido)));
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good! Buyer Did Not Request To Purchase This Item", gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodTamperingAttackBetweenSellerAndServer() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            long buyerNonce = new Date().getTime();

            pedido.setGoodId(1);
            pedido.setUserId(2);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(buyerNonce);
            pedido.setNotaryId(0);
            pedido.setAnswer(null);
            pedido.setBuyerSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 2), gson.toJson(pedido)));
            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setBuyerNounce(buyerNonce);
            pedido.setSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 1), gson.toJson(pedido)));
            pedido.setAnswer("answer"); // Tampering
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class).getAnswer());

            pedido = new Request();

            pedido.setGoodId(1);
            pedido.setUserId(2);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(buyerNonce);
            pedido.setNotaryId(0);
            pedido.setAnswer(null);
            pedido.setBuyerSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 2), gson.toJson(pedido)));
            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setBuyerNounce(buyerNonce);
            pedido.setSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 1), gson.toJson(pedido)));
            pedido.setSignature(null); //Tampering
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class).getAnswer());

            pedido = new Request();

            pedido.setGoodId(1);
            pedido.setUserId(2);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(buyerNonce);
            pedido.setNotaryId(0);
            pedido.setAnswer(null);
            pedido.setBuyerSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 2), gson.toJson(pedido)));
            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setBuyerNounce(0); //The value received in the tampered message
            pedido.setSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 1), gson.toJson(pedido)));
            pedido.setNounce(0); //Tampering
            Assert.assertEquals("Invalid Authorization to Transfer AnswerClasses.Good!", gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class).getAnswer());

            ensureServerIsOkAfterAttack(false);
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

            KeyStoreInterface.deleteKeystore();
        } catch (Exception e) {
            e.printStackTrace();
        }
        state = null;
    }

    private Request getGoodStateRequestGenerator(int userId, int goodId) {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(userId);
            pedido.setGoodId(goodId);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, userId), gson.toJson(pedido)));

            return gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Request sellGoodRequestGenerator(int userId, int goodId) {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(userId);
            pedido.setGoodId(goodId);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, userId), gson.toJson(pedido)));

            return gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Request transferGoodRequestGenerator(int goodId, int buyerUserId, int sellerId, int buyerId, long buyerNonce, int buyerKeyId, int sellerUserId, long sellerNonce,
                                                 long buyerNonceFromSeller, int sellerKeyId, int notaryId, String answer) {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setGoodId(goodId);
            pedido.setUserId(buyerUserId);
            pedido.setSellerId(sellerId);
            pedido.setBuyerId(buyerId);
            pedido.setNounce(buyerNonce);
            pedido.setNotaryId(notaryId);
            pedido.setAnswer(answer);
            pedido.setBuyerSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, buyerKeyId), gson.toJson(pedido)));
            pedido.setUserId(sellerUserId);
            pedido.setNounce(sellerNonce);
            pedido.setBuyerNounce(buyerNonceFromSeller);
            pedido.setSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, sellerKeyId), gson.toJson(pedido)));

            return gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ensureServerIsOkAfterAttack(boolean goodOnSale) {
        try {
            if(!goodOnSale) {
                Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(getGoodStateRequestGenerator(1, 1)).getAnswer());
                Assert.assertEquals("The Item is Now on Sale", Objects.requireNonNull(sellGoodRequestGenerator(1, 1)).getAnswer());
            } else {
                Assert.assertEquals("<1, On-Sale>", Objects.requireNonNull(getGoodStateRequestGenerator(1, 1)).getAnswer());
            }
            long buyerNonce = new Date().getTime();
            Request state = transferGoodRequestGenerator(1, 2, 1, 2, buyerNonce, 2, 1, new Date().getTime(), buyerNonce, 1, 0, null);
            Assert.assertEquals("The AnswerClasses.Good with AnswerClasses.Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", Objects.requireNonNull(state).getAnswer());
            Assert.assertEquals("<2, Not-On-Sale>", Objects.requireNonNull(getGoodStateRequestGenerator(1, 1)).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
