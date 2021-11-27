import java.util.ArrayList;

public class NodeN implements Cloneable {
    private String Name;
    private final ArrayList <NodeN> parents;
  //  private double [][] cpt_table;
    private boolean arrived_from_son;
    private boolean evidence;
    private CPT cpt; // will point to the cpt table of the node.
    private final ArrayList<NodeN> children;

    public NodeN(String name){
        this.Name = name;
        parents = new ArrayList<>();
        this.arrived_from_son = false;
        this.evidence = false;
        this.children = new ArrayList<>();
        //this.cpt = new CPT();
    }

    public NodeN(){
        this.parents = new ArrayList<>();
        this.arrived_from_son = false;

        this.evidence = false;
        this.children = new ArrayList<>();
    }
    // Deep Copy constructor
    public NodeN(NodeN other) {
        this.Name = other.Name;
        this.parents = new ArrayList<>();
        this.arrived_from_son = other.arrived_from_son;
        this.evidence = other.evidence;
        this.children = new ArrayList<>();
        this.parents.addAll(other.parents);
        this.children.addAll(other.children);
        this.cpt = other.cpt;
    }

    public String getName() {
        return Name;
    }


    public ArrayList<NodeN> getParents() {
        return parents;
    }

//    public double[][] getCpt_table() {
//        return cpt_table;
//    }

//    public void setCpt_table(double[][] cpt_table) {
//        this.cpt_table = cpt_table;
//    }

    public void add_sibling(NodeN sib){
        this.parents.add(sib);
    }

    public void add_children(NodeN sib){

        this.children.add(sib);
    }

    public boolean isArrived_from_son() {
        return arrived_from_son;
    }

    public void setArrived_from_son(boolean arrived_from_son) {
        this.arrived_from_son = arrived_from_son;
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


    public void setCpt (CPT cpt){
        this.cpt  = cpt;
    }

    public CPT getCpt() {
        return cpt;
    }
    public NodeN clone() throws CloneNotSupportedException {
        return (NodeN) super.clone();
    }
}
