import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class CPT {
    private final NodeN n; // this node will have this table (a pointer)
    private char [][] CPT_val;
    private final double [] cpt_prob;
    private int line;
    private Map<String , NodeN> vars;
    private final HashMap<String , Integer> indexes;
    private final HashSet<Character> variables;

    public Map<String, NodeN> getVars() {
        return vars;
    }





    public CPT(String val , NodeN nodeN){
        String [] values = val.split(" ");
        DoubleStream doubleStream = Arrays
                .stream(values)
                        .flatMapToDouble(n ->
                                      DoubleStream
                                                  .of(Double
                                                            .parseDouble(n)));

        this.cpt_prob = doubleStream.toArray();
        this.n = nodeN;
        this.variables = new HashSet<>();
        this.indexes = new HashMap<>();
        this.vars = new HashMap<>();
        this.line = 0;
        init_table();

    }

    private void init_table() {
        this.CPT_val = new char[this.cpt_prob.length ][this.n.getParents().size()];

        Character [] name  = this.n
                .getParents()
                .stream()
                .map(n1 -> n1.getName().charAt(0)).toArray(Character[]::new);
        Collections.addAll(this.variables , name);
        this.vars = this.n
                .getParents()
                .stream()
                .collect(Collectors
                        .toMap(NodeN::getName , NodeN -> NodeN));
        if(name.length > 0) {
            for (int i = 0; i < this.CPT_val[0].length; i++) {
                this.CPT_val[0][i] = name[i];
            }
        }

        permute("" , this.n.getParents().size() + 1);
    }
    private void permute(String to_split , int iterations){
    if (iterations == 0){
        this.CPT_val[this.line++] = to_split.toCharArray();
        this.indexes.put(to_split , this.line);

    }else{
        permute(to_split + "T" , iterations - 1);
        permute(to_split + "F" , iterations - 1 );
    }
    }

    public NodeN getN() {
        return n;
    }



    public char[][] getCPT_val() {
        return CPT_val;
    }

    public double[] getCpt_prob() {
        return cpt_prob;
    }



    public HashSet<Character> getVariables() {
        return variables;
    }




}
