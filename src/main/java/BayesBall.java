
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class BayesBall {
    private NodeN n1;
    private NodeN n2;
    private ArrayList<NodeN> net;
    private HashMap<String , ArrayList<NodeN>> net2;
    private  Parser p;
    private String xml_file_name;
    private ArrayList<String> st;
    private String file_name;
    private ArrayList <String> bayes_ball_equations;
    private ArrayList <String> variable_elimination_equations;
    private ArrayList <NodeN> evidences;

    public BayesBall(String name) throws Exception {
        this.file_name = name;
        this.net = new ArrayList<NodeN>();
        this.st = new ArrayList<>();
        this.bayes_ball_equations = new ArrayList<String>();
        this.variable_elimination_equations = new ArrayList<String>();
        read_file();
        p = new Parser(this.xml_file_name);
        this.evidences = new ArrayList<NodeN>();
        // now we got the net of our bayesian network
        this.net = p.getNet();
        this.net2 = new HashMap<String , ArrayList<NodeN>>();
        make_graph();
    }
     public void init() {
        this.bayes_ball_equations.forEach((e) -> {
            String node_first = Character.toString(e.charAt(0));
            String node_second = Character.toString(e.charAt(2));

                this.net.forEach((n -> {
                    if (node_first.equals(n.getName())) {
                        this.n1 = n;

                    }
                    if (node_second.equals(n.getName())) {
                        this.n2 = n;
                    }

                }));
            if (e.length() > 4){
                e = e.substring(4 , e.length());
                init_evidence(e);
            }
        });

        System.out.println("check with evidence = " + this.check_dependency_with_evidence(n1 , n2));
    }

    private void init_evidence(String e) {
        String [] st = e.split("[,\\=]");
        st = Arrays.stream(st).distinct().toArray(String[]::new);
        List<String > l = new ArrayList<String>();
        Collections.addAll(l , st);
        l.remove("T");
        st = l.stream().toArray(String[]::new);
        for(String str : st){
            this.net.forEach(n -> {
                n.setEvidence(true);
                if (n.getName().equals(str)){
                    NodeN add = new NodeN(n);
                    this.evidences.add(add);
                }
            });
        }

    }

    /*
    We will use Breadth First search to check the if the two nodes are independent
    The idea to use stimulation and finding a path between two nodes.
    This method will return false if they are independent.

     */
    public boolean check_dependency_without_evidence(NodeN n1 , NodeN n2){
        ArrayList<NodeN> list = new ArrayList<NodeN>();
        list.add(n1);
        while(!list.isEmpty()){
            NodeN n = new NodeN(list.remove(0));
            for (int i = 0 ; i < n.getParents().size() ; i++){
              if (n.getParents().get(i).getName().equals(n2.getName())){
                  System.out.println("Yes");
                  return false;
              }
              if (!n.getParents().get(i).isVisited()){
                  int j  = i;
                  this.net.forEach(n3 -> {
                      if (n3.getName().equals(n.getParents().get(j).getName())){
                          NodeN temp = new NodeN(n3);
                          list.add(n3);
                      }
                  });
              }
            }
            n.setVisited(true);
        }
        System.out.println("No");
        return true;
    }

    public boolean check_dependency_with_evidence(NodeN n1 , NodeN n2){
        // in this algorithm we will implement the bayes ball.
        // algorithm if evidence is given.
        ArrayList<NodeN> list = new ArrayList<>();
        list.add(n1);
        this.net2.forEach((s, nodeNS) -> {
            nodeNS.forEach(n -> {
               this.evidences.forEach(n3 -> {
                   if (n.getName().equals(n3.getName())){
                       n.setEvidence(true);
                   }else{
                       n.setEvidence(false);
                   }
               });
            });
        });
       while(!list.isEmpty()){
           NodeN n = list.remove(0);

           System.out.println(n.getName());
           if (n.getName().equals(n2.getName())){
               return true;
           }
           if (!n.isArrived_from_son()) {
               ArrayList<NodeN> l = this.net2.get(n.getName());
               if (l != null)
               for (NodeN nn : l) {
                   if (nn.isEvidence()) {
                       n.setArrived_from_son(true);
                       list.addAll(nn.getParents());
                       for (NodeN parent : nn.getParents()) {
                           parent.setArrived_from_son(true);
                       }
                   }
                   if (!nn.isEvidence()) {
                       list.add(nn);
                   }

                   }
               }
           if (n.isArrived_from_son()){
              for (NodeN node : n.getParents()){
                  if (node.getName().equals(n2.getName())){
                      return true;
                  }else{
                      node.setArrived_from_son(true);
                      list.add(node);
                  }
              }
           }
       }
        return false;
    }
    public void read_file() throws  Exception{
        File file = new File(this.file_name);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String read_line;

        // now we have the all string in the file.
        while((read_line = br.readLine())!= null){
            st.add(read_line);
        }
        build_input(this.st);
    }

    private void build_input(ArrayList<String> st) {
        if (!st.get(0).contains(".xml")){
            throw new RuntimeException("Your input file isn't valid");
        }
        for (String s : st){
            if (s.contains(".xml")){
                this.xml_file_name = s;
                System.out.println("xml file name = " + this.xml_file_name);
            }
            else if (s.contains("|") && !s.contains("(")){
                this.bayes_ball_equations.add(s);

            }
            else if (s.contains("|") && s.contains("(") && s.contains(")")) {
                this.variable_elimination_equations.add(s);
            }else{
                throw new RuntimeException("Please check your file.");
            }
        }
        System.out.println("bayes_ball_equations =");
        this.bayes_ball_equations.forEach(
                (s2) -> { System.out.println(s2 + "\n");});


    }

    public NodeN getN1() {
        return n1;
    }

    public NodeN getN2() {
        return n2;
    }

    public void setN2(NodeN n2) {
        this.n2 = n2;
    }

    public ArrayList<NodeN> getNet() {
        return net;
    }

    public void setNet(ArrayList<NodeN> net) {
        this.net = net;
    }

    public HashMap<String, ArrayList<NodeN>> getNet2() {
        return net2;
    }

    public void setNet2(HashMap<String, ArrayList<NodeN>> net2) {
        this.net2 = net2;
    }

    public Parser getP() {
        return p;
    }

    public void setP(Parser p) {
        this.p = p;
    }

    public String getXml_file_name() {
        return xml_file_name;
    }

    public void setXml_file_name(String xml_file_name) {
        this.xml_file_name = xml_file_name;
    }

    public ArrayList<String> getSt() {
        return st;
    }

    public void setSt(ArrayList<String> st) {
        this.st = st;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public ArrayList<String> getBayes_ball_equations() {
        return bayes_ball_equations;
    }

    public void setBayes_ball_equations(ArrayList<String> bayes_ball_equations) {
        this.bayes_ball_equations = bayes_ball_equations;
    }

    public ArrayList<String> getVariable_elimination_equations() {
        return variable_elimination_equations;
    }

    public void setVariable_elimination_equations(ArrayList<String> variable_elimination_equations) {
        this.variable_elimination_equations = variable_elimination_equations;
    }

    public ArrayList<NodeN> getEvidences() {
        return evidences;
    }

    public void setEvidences(ArrayList<NodeN> evidences) {
        this.evidences = evidences;
    }

    public void setN1(NodeN n1) {
        this.n1 = n1;
    }

    public void make_graph(){
        // for loop for all nodes
        for (int i = 0 ; i < this.net.size() ; i++){
            for (int j = 0 ; j < this.net.get(i).getParents().size();j++){
                // for loop for all parents
                NodeN n = new NodeN(this.net.get(i).getParents().get(j));

                if (this.net2.get(n.getName()) == null){
                    this.net2.put(n.getName() , new ArrayList<NodeN>());
                }
                this.net2.get(n.getName()).add(this.net.get(i));
            }
        }

    }

//    public static void main(String[] args) {
//      try{
//          BayesBall b = new BayesBall("src/main/resources/input.txt");
//          b.init();
//      }catch (Exception e){
//          e.printStackTrace();
//      }
//
//    }
}
