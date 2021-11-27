import java.util.*;
import java.util.stream.Collectors;

public class VariableElimination extends BayesBall {
    private List <Factor> factors;
    private final List <NodeN> hidden;
    private final Map<String , NodeN> evidences;
    private NodeN n;
    private boolean isTrue;

    public VariableElimination(String name) throws Exception {
        super(name);
        factors = new ArrayList<>();
        //init_factors();
        this.hidden = new ArrayList<>();
        this.evidences = new HashMap<>();
    }

    public void init_factors(){
        this.getVariable_elimination_equations().forEach(System.out::println);
        String [][] arr = this.getVariable_elimination_equations()
                .stream()
                .map(s -> s.substring(2))
                .map(s -> s.replace("," , ""))
                .map(s -> s.replace(")" ,""))
                .map(s -> s.split(""))
                .toArray(String[][]::new);

        this.modify_VE_vars(arr);


    }
    private void modify_VE_vars(String [][] arr) {
          String [] arr2  = Arrays.stream(arr)
                  .map(Arrays::toString)
                  .map(s -> s.replace("[" ,""))
                  .map(s -> s.replace("]" , ""))
                  .map(s->s.replace("," , ""))
                  .toArray(String[]::new);
        for(String s : arr2){
            this.factors.clear();
            this.hidden.clear();
            this.evidences.clear();
            this.parse(s);
            System.out.println(s);
            this.get_factors();

            this.execute();
        }
    }

    private void execute() {
        // we will join the factors in the given order in the hidden list
        // then we will eliminate using the property of name
        // how we handle if we have table with 3 names?
        // also we have to handle the table after we did join and eliminate
        // so we have to remove the node that we did eliminated
        // plus to that we have to remove the factors that we did join for them
        // and consider if we have one valued factor and the size of the factor list is 1
        // then we can return the result after normalizing with the equation we did in the class
        // then we will scale this values.
         this.hidden.add(this.n);
         while(!this.hidden.isEmpty()){
            NodeN node = this.hidden.remove(0);
            Factor f = null;
            boolean b = true;
            if (this.factors.size() > 1) {
                for (int j = 0; j < this.factors.size() && b; j++) {
                    if (this.factors.get(j).getHidden_var().containsKey(node.getName())) {
                       f = new Factor(this.factors.remove(j));
                        //f = this.factors.remove(j);
                        for (int k = 0; k < this.factors.size(); k++) {
                            if (this.factors.get(k).getHidden_var().containsKey(f.getName())) {
                                // we will join them
                                Factor f2 = new Factor(this.factors.remove(k));
                                f.factor_joining(f2);// join this factor
                                this.factors.add(f);
                                b = false;
                                break;
                            }
                        }
                    }
                }
                this.factors = this.factors.stream().
                        sorted(Comparator
                                .comparing
                                        (factor -> factor
                                                .getFac_val().length))
                        .collect(Collectors.toList());

            }
            if (f != null){
                if (f.getName().equals(node.getName())){
                    this.hidden.add(0 , node);
                }
            }
        }
         this.normalize();
    }

    private void parse(String s) {
        String [] arr = s.split(" {2}");
        for (String value : arr) {
            String[] splatted = value.split(" ");
            List<String> vars = Arrays.asList(splatted);
            if (vars.contains("|")) {
                this.getvars(vars);
                this.get_evidence(vars);
            }
            if (vars.contains("-")) {
                this.getHidden(vars);
            }
        }
    }

    private void get_evidence(List<String> vars) {
        int index = vars.indexOf("|");
        System.out.println(vars);
        List <String> copy = vars.subList(index + 1 , vars.size());
        List <String> evidences =  copy.stream()
                .filter(s -> !s.equals("T") && !s.equals("="))
                .collect(Collectors.toList());
        ArrayList<String> values = new ArrayList<>(copy);
        values.removeAll(evidences);
        values = (ArrayList<String>) values
                .stream()
                .filter(s -> !s.equals("="))
                .collect(Collectors.toList());
        System.out.println(evidences);
        for (int  i = 0 ; i < evidences.size() ; i++){
            System.out.println(evidences.get(i));
            if (values.get(i).equals("T")){
                this.getMap_net().get(evidences.get(i)).setEvidence(true);
            }
            this.evidences.put(evidences.get(i) , this.getMap_net().get(evidences.get(i)));
        }
    }

    private void getHidden(List<String> vars) {
        vars = vars.stream()
                .filter(s -> !s.equals("-"))
                .collect(Collectors.toList());
        for (String var : vars) {
            // add to the hidden variables
            if (this.getMap_net().get(var) != null) {
                this.hidden.add(this.getMap_net().get(var));
            }
        }
    }
    public void get_factors(){

        this.getNet().forEach(n1 -> {
            // check if this node is in evidence , and if it's then eliminate the value of it
            if (n1.isEvidence() || n1.getChildren().size() > 0 || n1.getName().equals(this.n.getName())){
                this.factors.add(new Factor(n1.getCpt()));
            }

        });

        this.evidences.forEach((s, n1) -> { // factor hidden vars evidence -> update factor
            for (int i = 0 ; i < this.factors.size() ; i++){
                Factor f = this.factors.get(i);
                if (f.getName().equals(n1.getName()) || f.getHidden_var().containsKey(n1.getName())) {
                        if(!f.isOneValued()) {
                            f.update(n1, n1.getName());// eliminate the true values from evidences

                        }
                }
              if (f.isOneValued() && f.getName().equals(n1.getName())){
                  this.factors.remove(i);
                  i--;
              }
            }
        });
        for (Factor value : this.factors) {
            // update the hidden
            for (NodeN nodeN : this.hidden) {
                // if there is a variable in the factor vars list is not in hidden list then remove
                if (!value.getHidden_var().containsKey(nodeN.getName())) {
                    value.getHidden_var().remove(nodeN.getName());
                }
            }
        }
        this.factors = this.factors.stream().
                sorted(Comparator
                        .comparing
                                (factor -> factor
                                        .getFac_val().length))
                .collect(Collectors.toList());

    }

    private void getvars(List <String> vars) {
        int index = vars.indexOf("|");
        ArrayList <String> list = (ArrayList<String>) vars
                .stream().filter(s -> !s.equals("="))
                .collect(Collectors.toList());
        for (int i = 0 ; i < index - 1; i++){
            // init the node
            if (!list.get(i).equals("T") && !list.get(i).equals("F")){
                this.n = this.getMap_net()
                        .get(list.get(i));

            }else {
                if (list.get(i).equals("T")){
                    this.isTrue = true;
                }
                if (list.get(i).equals("F")){
                    this.isTrue = false;
                }
            }
        }
    }
    private void normalize(){
        // this function will be executed after the joining factor functions
        Factor a = new Factor(this.factors.remove(0));
       // Factor b = new Factor(this.n.getCpt());
        //a.factor_joining(a);
        double [] arr = a.getFac_val();
        double sum = 0;
        for (double v : arr) {
            sum += v;
        }
        for (int  i = 0 ; i < arr.length ; i++){
            arr[i] = arr[i] / sum;
        }
        if (this.isTrue){
            System.out.println("result is = " + arr[0]);
        }else{
            System.out.println("result is = "+arr[1]);
        }

    }


}
