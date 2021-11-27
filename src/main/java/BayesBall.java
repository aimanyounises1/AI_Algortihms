
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class BayesBall {
    private NodeN n1;
    private NodeN n2;
    private ArrayList<NodeN> net;
    private HashMap<String , ArrayList<NodeN>> net2;
    private  Parser p;
    private Map <String , NodeN> map_net;
    private String xml_file_name;
    private ArrayList<String> st;
    private String file_name;
    private ArrayList <String> bayes_ball_equations;
    private ArrayList <String> variable_elimination_equations;
    private ArrayList <NodeN> evidences;

    public Map<String, NodeN> getMap_net() {
        return map_net;
    }

    public void setMap_net(Map<String, NodeN> map_net) {
        this.map_net = map_net;
    }

    public BayesBall(String name) throws Exception {
        this.file_name = name;
        this.net = new ArrayList<>();
        this.st = new ArrayList<>();
        this.bayes_ball_equations = new ArrayList<>();
        this.variable_elimination_equations = new ArrayList<>();
        read_file();
        p = new Parser(this.xml_file_name);
        this.evidences = new ArrayList<>();
        // now we got the net of our bayesian network
        this.net = p.getNet();
        this.map_net = new HashMap<>();
        this.map_net = this.net.stream()
                .collect(Collectors
                        .toMap(NodeN::getName,NodeN -> NodeN));
//
        this.net2 = new HashMap<>();
        make_graph();
    }
     public void init() {
        this.bayes_ball_equations.forEach((e) -> {
            String node_first = Character.toString(e.charAt(0));
            String node_second = Character.toString(e.charAt(2));
            this.evidences.clear();
                this.net.forEach((n -> {
                    if (node_first.equals(n.getName())) {
                        this.n1 = new NodeN(n);

                    }
                    if (node_second.equals(n.getName())) {
                        this.n2 = new NodeN(n);
                    }

                }));
            if (e.length() > 4){
                String init = e.substring(4);
                init_evidence(init);
            }
          this.check_dependency_with_evidence(n1 , n2);
        });

        this.bayes_ball_equations.forEach(System.out::println);
    }

    private void init_evidence(String e) {
        String [] st = e.split("[,=]");
        st = Arrays.stream(st).distinct().toArray(String[]::new);
        List<String > l = new ArrayList<>();
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
    public boolean check_dependency_with_evidence(NodeN n1 , NodeN n2){
        // in this algorithm we will implement the bayes ball.
        // algorithm if evidence is given.
        ArrayList<NodeN> list = new ArrayList<>();
        list.add(n1);
        this.net2.forEach((s, nodeNS) -> nodeNS.forEach(n -> this.evidences.forEach(n3 -> n.setEvidence(n.getName().equals(n3.getName())))));
       while(!list.isEmpty()){
           NodeN n = list.remove(0);
           if (n.getName().equals(n2.getName())){
               System.out.println("NO");
               return false;
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
                      // because he arrived from son
                      System.out.println("NO");
                      return false;
                  }else{
                      node.setArrived_from_son(true);
                      list.add(node);
                  }
              }
           }
       }
        System.out.println("YES");
        return true;
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
                (s2) -> System.out.println(s2));


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
        for (NodeN nodeN : this.net) {
            for (int j = 0; j < nodeN.getParents().size(); j++) {
                // for loop for all parents
                NodeN n = new NodeN(nodeN.getParents().get(j));

                this.net2.computeIfAbsent(n.getName(), k -> new ArrayList<>());
                this.net2.get(n.getName()).add(nodeN);
            }
        }

    }

    public static void main(String[] args) {
      try{
          BayesBall b = new BayesBall("src/main/resources/input.txt");
          b.init();
      }catch (Exception e){
          e.printStackTrace();
      }

    }
}
