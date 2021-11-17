import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VariableElimintation extends BayesBall {
    private List <Factor> factors;
    private List <NodeN> hidden;
    public VariableElimintation(String name) throws Exception {
        super(name);


    }

    public static void main(String[] args) {
        try{
          VariableElimintation VE = new VariableElimintation("src/main/resources/input.txt");
          VE.init();
            VE.getVariable_elimination_equations()
                    .stream()
                    .forEach(System.out::println);
      }catch (Exception e){
          e.printStackTrace();
      }
    }
}
