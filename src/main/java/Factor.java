import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.Arrays;

public class Factor {
    private char [][] fac_values;
    private double [] fac_val;

    public Factor(CPT cpt){
        this.fac_val = Arrays.copyOf(cpt.getCpt_prob() , cpt.getCpt_prob().length);
        this.fac_values = new char[cpt.getCPT_val().length][cpt.getCPT_val()[0].length];
        this.fac_values =  Arrays
               .stream(cpt.getCPT_val()) // stream
                     .map(char [] :: clone) // clone the array
                                    .toArray(char [][]::new); // put it in the array
       Arrays
               .stream(this.fac_values)
                    .forEach(chars -> {
                        System.out.println(Arrays.toString(chars));
                     });

    }
    public void factor_joining(Factor factor){
        // in this function we will multiple two tables of factors to be one
        

    }
}
