import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class Parser {
    private final ArrayList<NodeN> net;
    private final Map<String , NodeN> map;
    private NodeN n = new NodeN();
     public Parser(String name){
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         this.net = new ArrayList<>();
         this.map = new HashMap<>();
         try (InputStream is = readXmlFileIntoInputStream(name)) {

             // parse XML file
             DocumentBuilder db = dbf.newDocumentBuilder();

             // read from a project's resources folder
             Document doc = db.parse(is);


             if (doc.hasChildNodes()) {
                 printNote(doc.getChildNodes());
             }

         } catch (ParserConfigurationException | SAXException | IOException e) {
             e.printStackTrace();
         }

     }

    public ArrayList<NodeN> getNet() {
        return net;
    }

    private void printNote(NodeList nodeList) {

        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                // get node name and value
                if(tempNode.getNodeName().equals("FOR")){
                     n = new NodeN(tempNode.getTextContent());
                    net.add(n);
                    this.map.put(n.getName() , n);
                }
                if (tempNode.getNodeName().equals("GIVEN")){
                    NodeN sib = new NodeN(tempNode.getTextContent());
                    n.add_sibling(sib);
                    if (this.map.get(sib.getName())!=null){
                        NodeN nodeN = this.map.get(sib.getName());
                        nodeN.add_children(n);
                    }

                }
                if (tempNode.getNodeName().equals("TABLE")){
                    CPT cpt = new CPT(tempNode.getTextContent() , this.n);
                    this.n.setCpt(cpt);

                }

                if (tempNode.hasChildNodes()) {
                    // loop again if has child nodes
                    printNote(tempNode.getChildNodes());
                }

            }
            }

        }

    private InputStream readXmlFileIntoInputStream(String fileName) {
        return Parser.class.getClassLoader().getResourceAsStream(fileName);
    }

}
