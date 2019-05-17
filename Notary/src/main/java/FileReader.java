import AnswerClasses.Good;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

class FileReader {

    private static final String ELEMENT_TAG_NAME = "good";
    private static final String ID_ATTRIBUTE = "id";
    private static final String OWNER_ID_TAG = "ownerId";
    private static final String GOOD_NAME_TAG = "goodName";
    private static final String ON_SALE_STATE = "onsaleState";

    private Dictionary<Integer, ArrayList<Good>> parsedGoods = new Hashtable<>();


    FileReader() {

    }

    Dictionary<Integer, ArrayList<Good>> goodsListConstructor(String path) {

        try {
            File input = new File(path);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(input);

            String name = document.getDocumentElement().getNodeName();

            NodeList nodeList = document.getElementsByTagName(ELEMENT_TAG_NAME);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node listNode = nodeList.item(i);

                if (listNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) listNode;
                    String goodId = nodeElement.getAttribute(ID_ATTRIBUTE);

                    String ownerId = nodeElement.getElementsByTagName(OWNER_ID_TAG).item(0).getTextContent();

                    String goodName = nodeElement.getElementsByTagName(GOOD_NAME_TAG).item(0).getTextContent();

                    String onSaleState = nodeElement.getElementsByTagName(ON_SALE_STATE).item(0).getTextContent();

                    addGoodsToArrayList(new Good(Integer.parseInt(ownerId), Integer.parseInt(goodId), goodName, onSaleState.equals("true")));
                }
            }

            return parsedGoods;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addGoodsToArrayList(Good goodToAdd) {
        try {
            if (parsedGoods.get(goodToAdd.getOwnerId()) == null) {
                ArrayList<Good> temp = new ArrayList<>();
                temp.add(goodToAdd);
                parsedGoods.put(goodToAdd.getOwnerId(), temp);
            } else {
                ArrayList<Good> good = parsedGoods.get(goodToAdd.getOwnerId());
                good.add(goodToAdd);
                parsedGoods.remove(goodToAdd.getOwnerId());
                parsedGoods.put(goodToAdd.getOwnerId(), good);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong During AnswerClasses.Good Adition to Dictionary");
        }
    }

    Dictionary<Integer, ArrayList<Good>> getParsedGoods() {
        return parsedGoods;
    }

}
