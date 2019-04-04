import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.PrintWriter;

public class CartaoCidadaoTest {

    @Before
    public void setUp() {
    }

    @Test
    public void simpleDataSignVerifyTest() {
        try {
            String data = "data";
            byte[] signature = CartaoCidadao.sign(data);
            Assert.assertNotNull(signature);
            Assert.assertTrue(CartaoCidadao.verify(data, signature));
            Assert.assertFalse(CartaoCidadao.verify("datas", signature));
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

            byte[] signature = CartaoCidadao.sign(jsonToString);
            Assert.assertTrue(CartaoCidadao.verify(jsonToString, signature));
            Assert.assertFalse(CartaoCidadao.verify(jsonToString2, signature));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void writeCCPublicKeyToFileTest() {
        try {
            String baseDir = System.getProperty("user.dir").replace("\\Notary", "");

            new File(baseDir + "\\Client\\src\\main\\resources\\Notary_CC.pub");
            new File(baseDir + "\\Notary\\src\\main\\resources\\Notary_CC.pub");
            PrintWriter writer = new PrintWriter(baseDir + "\\Client\\src\\main\\resources\\Notary_CC.pub");
            PrintWriter writer2 = new PrintWriter(baseDir + "\\Notary\\src\\main\\resources\\Notary_CC.pub");
            writer.println("Empty key file");
            writer2.println("Empty key file");
            writer.close();
            writer2.close();

            CartaoCidadao.writeCCPublicKeyToFile();

            String jsonString = FileUtils.readFileToString(new File(baseDir + "\\Client\\src\\main\\resources\\Notary_CC.pub"), "UTF-8");
            jsonString = jsonString.replace("\n", "").replace("\r", "");
            String jsonString2 = FileUtils.readFileToString(new File(baseDir + "\\Notary\\src\\main\\resources\\Notary_CC.pub"), "UTF-8");
            jsonString = jsonString.replace("\n", "").replace("\r", "");

            Assert.assertTrue(jsonString.contains("-----BEGIN RSA PUBLIC KEY-----"));
            Assert.assertTrue(jsonString.length() > 100); //Slightly more than the combined length of these strings
            Assert.assertTrue(jsonString.length() < 500); //Expected file size is in range 200-500
            Assert.assertTrue(jsonString.contains("-----END RSA PUBLIC KEY-----"));
            Assert.assertTrue(jsonString2.contains("-----BEGIN RSA PUBLIC KEY-----"));
            Assert.assertTrue(jsonString2.length() > 100); //Slightly more than the combined length of these strings
            Assert.assertTrue(jsonString2.length() < 500); //Expected file size is in range 200-500
            Assert.assertTrue(jsonString2.contains("-----END RSA PUBLIC KEY-----"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @After
    public void tearDown() {
    }
}
