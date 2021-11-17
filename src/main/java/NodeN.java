import java.util.ArrayList;

public class NodeN {
    private String Name;
    private ArrayList <NodeN> parents;
    private double [][] cpt_table;
    private boolean arrived_from_son;
    private boolean evidence;
    private boolean visited;
    private CPT cpt; // will point to the cpt table of the node.
    private ArrayList<NodeN> children;

    public NodeN(String name){
        this.Name = name;
        parents = new ArrayList<NodeN>();
        this.visited = false;
        this.arrived_from_son = false;
        this.evidence = false;
        this.children = new ArrayList<NodeN>();
        this.cpt = new CPT();
    }

    public NodeN(){
        this.parents = new ArrayList<NodeN>();
        this.arrived_from_son = false;
        this.visited = false;
        this.evidence = false;
        this.children = new ArrayList<NodeN>();
    }
    // Deep Copy constructor
    public NodeN(NodeN other) {
        this.Name = other.Name;
        this.parents = new ArrayList<NodeN>();
        this.arrived_from_son = other.arrived_from_son;
        this.visited = other.visited;
        this.evidence = other.evidence;
        this.children = new ArrayList<>();
        for (int i = 0; i < other.parents.size(); i++) {
            this.parents.add(other.parents.get(i));

        }
        for (int i = 0; i < other.children.size(); i++) {
            this.children.add(other.children.get(i));

        }
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<NodeN> getParents() {
        return parents;
    }

    public void setParents(ArrayList<NodeN> parents) {
        this.parents = parents;
    }

    public double[][] getCpt_table() {
        return cpt_table;
    }

    public void setCpt_table(double[][] cpt_table) {
        this.cpt_table = cpt_table;
    }

    public void add_sibling(NodeN sib){
        this.parents.add(sib);
    }

    public void add_children(NodeN sib){
        System.out.println("child added");
        this.children.add(sib);
    }

    public boolean isArrived_from_son() {
        return arrived_from_son;
    }

    public void setArrived_from_son(boolean arrived_from_son) {
        this.arrived_from_son = arrived_from_son;
    }
    public boolean isVisited(){
        return this.visited;
    }
    public void setVisited(boolean b){
        this.visited = b;
    }

    public boolean isEvidence() {
        return evidence;
    }

    public void setEvidence(boolean evidence) {
        this.evidence = evidence;
    }

    public ArrayList<NodeN> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<NodeN> children) {
        this.children = children;
    }
    public void setCpt (CPT cpt){
        this.cpt  = cpt;
    }

    public CPT getCpt() {
        return cpt;
    }
}
