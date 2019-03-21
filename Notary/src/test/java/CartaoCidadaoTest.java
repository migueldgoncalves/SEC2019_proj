import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CartaoCidadaoTest {

    @Before
    public void setUp() {
    }

    @Test
    public void simpleDataSignTest() {
        String data = "data";
        byte[] signature = CartaoCidadao.sign(data);
        Assert.assertNotNull(signature);
    }

    @Test
    public void simpleDataSignVerifyTest() {
        String data = "data";
        byte[] signature = CartaoCidadao.sign(data);
        Assert.assertTrue(CartaoCidadao.verify(data, signature));
    }

    @Test
    public void requestSignVerifyTest() {
        Request request = new Request();
        request.setBuyerId(1);
        request.setGoodId(2);
        request.setSellerId(3);
        request.setUserId(4);

        Gson json = new Gson();
        String jsonToString = json.toJson(request);
        byte[] signature = CartaoCidadao.sign(jsonToString);

        Assert.assertTrue(CartaoCidadao.verify(jsonToString, signature));
    }

    @Test
    public void simpleDataNotMatchingSignatureTest() {
        try {
            String data = "data";
            byte[] signature = CartaoCidadao.sign(data);
            Assert.assertFalse(CartaoCidadao.verify("datas", signature));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void requestNotMatchingSignatureTest() {
        Request request = new Request();
        request.setBuyerId(1);
        request.setGoodId(2);
        request.setSellerId(3);
        request.setUserId(4);

        Request request2 = new Request();
        request.setBuyerId(5);
        request.setGoodId(6);
        request.setSellerId(7);
        request.setUserId(8);

        Gson json = new Gson();
        String jsonToString = json.toJson(request);
        Gson json2 = new Gson();
        String jsonToString2 = json2.toJson(request2);

        try {
            byte[] signature = CartaoCidadao.sign(jsonToString);
            Assert.assertFalse(CartaoCidadao.verify(jsonToString2, signature));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @After
    public void tearDown() {
    }
}
