import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class FileInterface {

    private static final String ELEMENT_TAG_NAME = "client";
    private static final String ID_ATTRIBUTE = "id";
    private static final String WRITE_TIMESTAMP_TAG = "write";
    private static final String READ_TIMESTAMP_TAG = "read";

    private static final int MODE_READ = 0;
    private static final int MODE_WRITE = 1;

    public static int[] readTimestamps(int clientId) {
        return fileAccesser(clientId, 0, 0, MODE_READ);
    }

    public static int[] writeTimestamps(int clientId, int writeTimestamp, int readTimestamp) {
        return fileAccesser(clientId, writeTimestamp, readTimestamp, MODE_WRITE);
    }

    // Only funcion to access file, therefore ensuring synchronized access
    private synchronized static int[] fileAccesser(int clientId, int writeTimestamp, int readTimestamp, int operationMode) {
        if(operationMode==MODE_READ) {
            try {
                if(clientId <1 || clientId >9)
                    return null;

                int writeTimestampToRead = 0;
                int readTimestampToRead = 0;
                int[] response = new int[2];

                File input = new File(baseDirGenerator() + "\\src\\main\\resources\\timestamps.xml");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(input);

                NodeList nodeList = document.getElementsByTagName(ELEMENT_TAG_NAME);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node listNode = nodeList.item(i);
                    if (listNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element nodeElement = (Element) listNode;
                        if(nodeElement.getAttribute(ID_ATTRIBUTE).equals(String.valueOf(clientId))) {
                            writeTimestampToRead = Integer.valueOf(nodeElement.getElementsByTagName(WRITE_TIMESTAMP_TAG).item(0).getTextContent());
                            readTimestampToRead = Integer.valueOf(nodeElement.getElementsByTagName(READ_TIMESTAMP_TAG).item(0).getTextContent());
                        }
                    }
                }
                response[0] = writeTimestampToRead;
                response[1] = readTimestampToRead;
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else if (operationMode==MODE_WRITE) {
            try {
                File input = new File(baseDirGenerator() + "\\src\\main\\resources\\timestamps.xml");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(input);

                NodeList nodeList = document.getElementsByTagName(ELEMENT_TAG_NAME);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node listNode = nodeList.item(i);
                    if (listNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element nodeElement = (Element) listNode;
                        if (nodeElement.getAttribute(ID_ATTRIBUTE).equals(String.valueOf(clientId))) {
                            Node node = nodeElement.getElementsByTagName(WRITE_TIMESTAMP_TAG).item(0);
                            node.replaceChild(document.createTextNode(String.valueOf(writeTimestamp)), node.getFirstChild());
                            node = nodeElement.getElementsByTagName(READ_TIMESTAMP_TAG).item(0);
                            node.replaceChild(document.createTextNode(String.valueOf(readTimestamp)), node.getFirstChild());
                        }
                    }
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.transform(new DOMSource(document), new StreamResult(input));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String baseDirGenerator(){
        String basePath = System.getProperty("user.dir");
        if(!basePath.contains("\\Client"))
            basePath+="\\Client";
        return basePath;
    }
}