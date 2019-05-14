import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class iCartaoCidadaoTest {

    @Before
    public void setUp() {
    }

    @Test
    public void simpleDataSignVerifyTest() {
        try {
            String data = "data";
            byte[] signature = iCartaoCidadao.sign(data);
            Assert.assertNotNull(signature);
            Assert.assertTrue(iCartaoCidadao.verify(data, signature));
            Assert.assertFalse(iCartaoCidadao.verify("datas", signature));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void requestSignVerifyTest() {
        try {
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

            byte[] signature = iCartaoCidadao.sign(jsonToString);
            Assert.assertTrue(iCartaoCidadao.verify(jsonToString, signature));
            Assert.assertFalse(iCartaoCidadao.verify(jsonToString2, signature));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @After
    public void tearDown() {
    }
}
