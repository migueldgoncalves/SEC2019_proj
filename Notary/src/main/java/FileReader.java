import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class FileReader {

    private static final String ELEMENT_TAG_NAME = "good";
    private static final String ID_ATTRIBUTE = "id";
    private static final String OWNER_ID_TAG = "ownerId";
    private static final String GOOD_NAME_TAG = "goodName";
    private static final String ON_SALE_STATE = "onsaleState";

    public FileReader() {

    }

    public ArrayList<ArrayList<Good>> goodsListConstructor(String path) {

        try {
            ArrayList<ArrayList<Good>> parsedGoods = new ArrayList<>();
            File input = new File(path);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(input);

            String name = document.getDocumentElement().getNodeName();
            System.out.println(name);

            NodeList nodeList = document.getElementsByTagName(ELEMENT_TAG_NAME);
            System.out.println(nodeList.getLength());

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node listNode = nodeList.item(i);
                System.out.println(listNode.getNodeName());

                if (listNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) listNode;
                    String goodId = nodeElement.getAttribute(ID_ATTRIBUTE);
                    System.out.println(nodeElement.getAttribute(ID_ATTRIBUTE));

                    String ownerId = nodeElement.getElementsByTagName(OWNER_ID_TAG).item(0).getTextContent();
                    System.out.println(ownerId);

                    String goodName = nodeElement.getElementsByTagName(GOOD_NAME_TAG).item(0).getTextContent();
                    System.out.println(goodName);

                    String onSaleState = nodeElement.getElementsByTagName(ON_SALE_STATE).item(0).getTextContent();
                    System.out.println(onSaleState);
                    System.out.println("\n");

                    parsedGoods.add(Integer.parseInt(ownerId), );

                    parsedGoods.add(Integer.parseInt(ownerId), new ArrayList<Good>().add(new Good(Integer.parseInt(ownerId), Integer.parseInt(goodId), goodName, false)));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private

}
