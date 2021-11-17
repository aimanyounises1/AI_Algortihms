import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class Parser {
    private ArrayList<NodeN> net;
     public Parser(String name){
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         this.net = new ArrayList<NodeN>();
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
         for (int i  = 0 ; i < net.size() ; i++){
             for(int j = 0 ; j < net.get(i).getParents().size() ; j++){
              //   System.out.println(net.get(i).getParents().get(j).getName());
             }
         }

     }
   private NodeN n = new NodeN();

    public ArrayList<NodeN> getNet() {
        return net;
    }

    public void setNet(ArrayList<NodeN> net) {
        this.net = net;
    }

    public NodeN getN() {
        return n;
    }

    public void setN(NodeN n) {
        this.n = n;
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
                }
                if (tempNode.getNodeName().equals("GIVEN")){
                    NodeN sib = new NodeN(tempNode.getTextContent());
                    n.add_sibling(sib);

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
    public static void main(String[] args) {
        Parser p = new Parser("alarm_net.xml");
    }
}
