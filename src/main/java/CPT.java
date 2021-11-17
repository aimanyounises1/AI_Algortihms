import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class CPT {
    private NodeN n; // this node will have this table (a pointer)
    private char [][] CPT_val;
    private double [] cpt_prob;
    private int line;
    private HashSet <Character> variables;
    public CPT(){

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
        System.out.println("parent " + this.n.getName());
        this.line = 0;
        init_table();

    }

    private void init_table() {
        this.CPT_val = new char[this.cpt_prob.length ][this.n.getParents().size()];
        List <Character > names = this.n
                 .getParents()
                                .stream()
                                        .map(n1 -> n1.getName().charAt(0))
                                                 .collect(Collectors.toList());

        Character [] name  = names
                               .toArray(new Character[0]);
        Collections.addAll(this.variables , name);

        if(name.length > 0) {
            for (int i = 0; i < this.CPT_val[0].length; i++) {
                this.CPT_val[0][i] = name[i];
            }
        }

        permute("" , this.n.getParents().size() + 1);
        for (int  i = 0 ;i < this.CPT_val.length ;  i++){
            System.out.println(Arrays.toString(this.CPT_val[i]) +" " +  this.cpt_prob[i]);
        }

    }
    private void permute(String to_split , int iterations){
    if (iterations == 0){
        this.CPT_val[this.line++] = to_split.toCharArray();

    }else{
        permute(to_split + "T" , iterations - 1);
        permute(to_split + "F" , iterations - 1 );
    }
    }

    public NodeN getN() {
        return n;
    }

    public void setN(NodeN n) {
        this.n = n;
    }

    public char[][] getCPT_val() {
        return CPT_val;
    }

    public void setCPT_val(char[][] CPT_val) {
        this.CPT_val = CPT_val;
    }

    public double[] getCpt_prob() {
        return cpt_prob;
    }

    public void setCpt_prob(double[] cpt_prob) {
        this.cpt_prob = cpt_prob;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
