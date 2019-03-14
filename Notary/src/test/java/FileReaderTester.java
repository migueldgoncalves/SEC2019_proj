import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Dictionary;

public class FileReaderTester {

    private static final String PATH_XML_FILE_1 = "src\\main\\resources\\GoodsFile1.xml";
    private static final String PATH_XML_FILE_2 = "src\\main\\resources\\GoodsFile2.xml";

    FileReader fileReader = null;
    Dictionary<Integer, ArrayList<Good>> dictionary = null;

    @Before
    public void setUp() {
        fileReader = new FileReader();
    }

    @Test
    public void getParsedGoodsTest() {
        dictionary = fileReader.getParsedGoods();
        Assert.assertEquals(0, dictionary.size());
    }

    @Test
    public void goodsListConstructor1() {
        dictionary = fileReader.goodsListConstructor(PATH_XML_FILE_1);
        Assert.assertEquals(9, dictionary.size());

        ArrayList<Good> goodsUser1 = dictionary.get(1);
        ArrayList<Good> goodsUser2 = dictionary.get(2);
        ArrayList<Good> goodsUser3 = dictionary.get(3);
        ArrayList<Good> goodsUser4 = dictionary.get(4);
        ArrayList<Good> goodsUser5 = dictionary.get(5);
        ArrayList<Good> goodsUser6 = dictionary.get(6);
        ArrayList<Good> goodsUser7 = dictionary.get(7);
        ArrayList<Good> goodsUser8 = dictionary.get(8);
        ArrayList<Good> goodsUser9 = dictionary.get(9);

        Assert.assertEquals(1, goodsUser1.size());
        Assert.assertEquals(1, goodsUser2.size());
        Assert.assertEquals(1, goodsUser3.size());
        Assert.assertEquals(1, goodsUser4.size());
        Assert.assertEquals(1, goodsUser5.size());
        Assert.assertEquals(1, goodsUser6.size());
        Assert.assertEquals(1, goodsUser7.size());
        Assert.assertEquals(1, goodsUser8.size());
        Assert.assertEquals(1, goodsUser9.size());

        Good good1 = goodsUser1.get(0);
        Good good2 = goodsUser2.get(0);
        Good good3 = goodsUser3.get(0);
        Good good4 = goodsUser4.get(0);
        Good good5 = goodsUser5.get(0);
        Good good6 = goodsUser6.get(0);
        Good good7 = goodsUser7.get(0);
        Good good8 = goodsUser8.get(0);
        Good good9 = goodsUser9.get(0);

        Assert.assertEquals(1, good1.getGoodId());
        Assert.assertEquals(1, good1.getOwnerId());
        Assert.assertEquals("Fiambre", good1.getName());
        Assert.assertFalse(good1.isOnSale());

        Assert.assertEquals(2, good2.getGoodId());
        Assert.assertEquals(2, good2.getOwnerId());
        Assert.assertEquals("Queijo", good2.getName());
        Assert.assertFalse(good2.isOnSale());

        Assert.assertEquals(3, good3.getGoodId());
        Assert.assertEquals(3, good3.getOwnerId());
        Assert.assertEquals("Carne", good3.getName());
        Assert.assertFalse(good3.isOnSale());

        Assert.assertEquals(4, good4.getGoodId());
        Assert.assertEquals(4, good4.getOwnerId());
        Assert.assertEquals("Matilde", good4.getName());
        Assert.assertFalse(good4.isOnSale());

        Assert.assertEquals(5, good5.getGoodId());
        Assert.assertEquals(5, good5.getOwnerId());
        Assert.assertEquals("Cafe", good5.getName());
        Assert.assertFalse(good5.isOnSale());

        Assert.assertEquals(6, good6.getGoodId());
        Assert.assertEquals(6, good6.getOwnerId());
        Assert.assertEquals("carro", good6.getName());
        Assert.assertFalse(good6.isOnSale());

        Assert.assertEquals(7, good7.getGoodId());
        Assert.assertEquals(7, good7.getOwnerId());
        Assert.assertEquals("casa", good7.getName());
        Assert.assertFalse(good7.isOnSale());

        Assert.assertEquals(8, good8.getGoodId());
        Assert.assertEquals(8, good8.getOwnerId());
        Assert.assertEquals("Fiambre", good8.getName());
        Assert.assertFalse(good8.isOnSale());

        Assert.assertEquals(9, good9.getGoodId());
        Assert.assertEquals(9, good9.getOwnerId());
        Assert.assertEquals("Fiambre", good9.getName());
        Assert.assertFalse(good9.isOnSale());
    }

    @Test
    public void goodsListConstructor2() {
        dictionary = fileReader.goodsListConstructor(PATH_XML_FILE_2);
        Assert.assertEquals(2, dictionary.size());

        ArrayList<Good> goodsUser2 = dictionary.get(2);
        ArrayList<Good> goodsUser3 = dictionary.get(3);

        Assert.assertEquals(2, goodsUser2.size());
        Assert.assertEquals(1, goodsUser3.size());

        Good good1 = goodsUser2.get(0);
        Good good2 = goodsUser2.get(1);
        Good good3 = goodsUser3.get(0);

        Assert.assertEquals(1, good1.getGoodId());
        Assert.assertEquals(2, good1.getOwnerId());
        Assert.assertEquals("Fiambre", good1.getName());
        Assert.assertFalse(good1.isOnSale());

        Assert.assertEquals(2, good2.getGoodId());
        Assert.assertEquals(2, good2.getOwnerId());
        Assert.assertEquals("Queijo", good2.getName());
        Assert.assertFalse(good2.isOnSale());

        Assert.assertEquals(3, good3.getGoodId());
        Assert.assertEquals(3, good3.getOwnerId());
        Assert.assertEquals("Carne", good3.getName());
        Assert.assertTrue(good3.isOnSale());
    }

    @After
    public void tearDown() {
        fileReader = null;
        dictionary = null;
    }
}