import java.util.*;
import java.util.stream.Collectors;

public class Factor  {
    private char [][] fac_values;
    private double [] fac_val;
    private ArrayList <Character> vars_names;
    private final Map<String , NodeN> hidden_vars;
    private final HashMap<String ,Integer> indexes; // every var in which col
    private String name; // this will represent the current name of the factor
    private int line;
    private boolean oneValued; // if the factor just have one variable
    private final Map<String , Double> values;
    private final NodeN n;
    private int numOfAdd;
    private int numOfMul;
    //deep copy constructor
    public Factor(Factor f){
        this.oneValued = f.oneValued;
        this.fac_val = Arrays.copyOf(f.fac_val , f.fac_val.length);
        this.fac_values = new char[f.fac_values.length][f.fac_values[0].length];
        this.fac_values = Arrays
                .stream(f.fac_values)
                .map(char[]::clone)
                .toArray(char[][]::new);
        this.line = 0;
        this.indexes = (HashMap<String, Integer>) f.indexes.clone();
        this.name = f.name;
        this.vars_names = new ArrayList<>(f.vars_names);
        this.hidden_vars = new HashMap<>();
        this.values = new HashMap<>();
        this.n = new NodeN(f.n);
        f.hidden_vars.forEach((key, value) -> {
            try {
                this.hidden_vars.put(key, value.clone());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        f.indexes.forEach(this.indexes::put);
        this.numOfMul = f.numOfMul;
        this.numOfAdd = f.numOfAdd;
    }

    public Factor(CPT cpt){
        this.fac_val = Arrays.copyOf(cpt.getCpt_prob() , cpt.getCpt_prob().length);
        this.fac_values = new char[cpt.getCPT_val().length + 1][cpt.getCPT_val()[0].length];
        this.vars_names = new ArrayList<>();
        this.line = 0;
        this.indexes = new HashMap<>(); // store the indexes of the factors
        this.name = cpt.getN().getName();
        this.hidden_vars = new HashMap<>();
        this.hidden_vars.putAll(cpt.getVars());
        this.n = cpt.getN();
        this.hidden_vars.put(this.name , n);
        this.values = new HashMap<>();
        Collections.addAll(this.vars_names, cpt.getVariables()
                .toArray(new Character[0]));
        Collections.reverse(this.vars_names);
        this.vars_names.add(cpt.getN().getName().charAt(0));
        this.update_vars_name();
        this.arrange(cpt.getCPT_val());
        this.numOfAdd = 0;
        this.numOfMul = 0;
        this.oneValued = this.vars_names.size() == 1;
    }
    private void update_vars_name() {
        Character [] arr = this.vars_names.toArray(new Character[0]);
        for ( int i = 0 ; i < arr.length ; i++){
            this.fac_values[0][i] = arr[i];
            this.indexes.put(Character.toString(arr[i]), i);
        }
    }

    public void factor_joining(Factor factor){
        if (factor.isOneValued() && this.oneValued && factor.name.equals(this.name)){
            this.soft_join(factor);
            return;
        }
        Factor a =  new Factor(this);
        this.update_vars_name(factor); // will update the hidden vars and the vars_names
        int rows_length = (int) Math.pow(2 , this.vars_names.size()); // the size of the new factor
        boolean b;
        // new arrays with new size
        this.fac_val = new double[rows_length];
        this.fac_values = new char[rows_length + 1][this.vars_names.size()];
        // init the first row of the fac_values to be the vars names
        this.init_vars(this.fac_values);
        this.indexes.clear();
        Character [] arr = this.vars_names.toArray(new Character[0]);
        for (int  i = 0 ; i < arr.length ; i++){
            this.indexes.put(Character.toString(arr[i]) , i);
        }
        // e of boolean values using permute
        this.line = 1;
        this.permute("", this.vars_names.size());
        // if the this vars size is bigger than factor then swipe them
        // in join
        b = this.fac_values.length > factor.fac_values.length;
        // join the factors without changing the table length
        this.join(a , factor , b);
        // insert the new values to the table using the hashmap values
        this.modify();
        // eliminate the factor from the table
        this.eliminate(a , factor);
        this.oneValued = this.vars_names.size() == 1;
    }

    private void update_vars_name(Factor factor) {
      //  this.hidden_vars.putAll(factor.hidden_vars);// update the hidden var to use it in variable elimination
        // because list.contain doesn't work;
        ArrayList<Character> arr = new ArrayList<>(factor.vars_names);
        if (this.vars_names.size() > factor.vars_names.size()){
            this.vars_names.addAll(factor.vars_names);

        }else{
            arr.addAll(this.vars_names);
            this.vars_names = new ArrayList<>(arr);
        }
        HashSet<Character> set = new HashSet<>();
        this.vars_names= (ArrayList<Character>)
                this.vars_names.stream()
                        .filter(set::add)
                        .collect(Collectors.toList());

    }

    private void soft_join(Factor factor) {
        Factor a =  new Factor(this);
        for (int  i = 0 ; i < this.fac_val.length ; i++){
            this.fac_val[i] = a.fac_val[i] * factor.fac_val[i];
            this.numOfMul++;
        }
    }
    private void modify() {
        for (int  i = 1 ; i < this.fac_values.length ; i++){
            String key = Arrays.toString(this.fac_values[i])
                    .replace("[" , "")
                    .replace("]" , "")
                    .replace(",","")
                    .replace(" " , "");

            if (this.values.get(key) != null){
                this.fac_val[i - 1] = this.values.get(key);
            }
        }
    }

    public void eliminate(Factor a , Factor b){
        if (a.vars_names.size() < b.vars_names.size()){ // a is bigger than b
            // swap them
            Factor temp = a;
            a = b;
            b = temp;
        }
        // update the list of vars to contain only the vars that are not in the intersection
        this.vars_names = this.update_vars(a , b);
        int size =(int)Math.pow(2 , this.vars_names.size());
        double [] fac_val = new double[size];
        char [] [] fac_values = new char[size + 1][this.vars_names.size()];
        int k = 0;
        Character [] arr = this.vars_names.toArray(new Character[0]);
        this.init_vars(fac_values);
        this.line = 1;
        List <String> vars_a  = this.modify_list(a);
        vars_a.add(a.name);
        List <String> vars_b = this.modify_list(b);
        vars_b.add(b.name);
        List <String> retained = new ArrayList<>(vars_a);
        retained.retainAll(vars_b);
        vars_a.addAll(vars_b);
        if (vars_a.size() > 1) vars_a = this.remove_duplicates(vars_a);
        List <String> sub = this.update_lists(a , b , vars_a);
        for (int  i = 1 ; i < this.fac_values.length ; i++){
            String row = replace_str(Arrays.toString(this.fac_values[i]));
            StringBuilder inter = new StringBuilder();
            for (String str : sub){
                int index = this.indexes.get(str);
                inter.append(row.charAt(index));

            }
            for (int j = i + 1;  j < this.fac_values.length; j++){
                String j_row = replace_str(Arrays.toString(this.fac_values[j]));
                StringBuilder intersect = new StringBuilder();
                for (String str  : sub){
                    int index = this.indexes.get(str);
                    intersect.append(j_row.charAt(index));
                }
                if (inter.toString().equals(intersect.toString())){
                    fac_val[k++] =+ this.fac_val[i - 1] + this.fac_val[j - 1];
                    this.numOfAdd++;

                }
            }
        }
        // this function will update our array after elimination

        this.hidden_vars.putAll(a.hidden_vars);
        this.update_after_elimination(arr , retained , size , fac_values, fac_val);
    }

    private ArrayList<Character> update_vars(Factor a, Factor b) {
        ArrayList <Character> list = new ArrayList<>();
        for (int  i = 0 ;i < a.fac_values[0].length ; i++){
            if (!b.hidden_vars.containsKey(Character.toString(a.fac_values[0][i]))){
                list.add(a.fac_values[0][i]);
            }
        }
        for (int  i = 0 ;i < b.fac_values[0].length ; i++){
            if (!a.hidden_vars.containsKey(Character.toString(b.fac_values[0][i]))){
                list.add(b.fac_values[0][i]);
            }
        }
        return list;
    }

    private List<String> update_lists(Factor a, Factor b, List<String> vars_a) {
        List <String> sub = new ArrayList<>();
        for (String str : vars_a){
            if (!a.getHidden_var().containsKey(str) || !b.getHidden_var().containsKey(str)){
                sub.add(str);
            }
        }
        return sub;
    }

    private void init_vars(char[][] fac_values) {
        Character [] arr = this.vars_names.toArray(new Character[0]);
       // this.indexes.clear();
        for ( int i = 0 ; i < arr.length ; i++){
            fac_values[0][i] = arr[i];
        }
    }


    private void update_after_elimination(Character[] arr, List<String> vars_a, int size,char[][]fac_values ,double [] fac_val) {
        this.fac_values = new char[size + 1][this.vars_names.size()];
        this.fac_values[0] = Arrays.copyOf(fac_values[0] , fac_values[0].length);
        this.permute("", this.vars_names.size());
        this.fac_val = Arrays.copyOf(fac_val , fac_val.length);
        for (String str : vars_a){
            this.hidden_vars.remove(str);
        }
        this.indexes.clear();
        for ( int i = 0 ; i < arr.length ; i++){
            fac_values[0][i] = arr[i];
            this.indexes.put(Character.toString(arr[i]), i);
        }
        this.name = Character.toString(this.vars_names.get(0));
    }

    private void join(Factor a , Factor b ,  boolean c){
        if (!c){ // a is bigger than b
            // swap them
            Factor temp = a;
            a = b;
            b = temp;
        }
        //this.values.clear();
        List <String> vars_a  = this.modify_list(a);
        List <String> vars_b = this.modify_list(b);
        vars_a.retainAll(vars_b);
        if (vars_a.size() > 1)
        vars_a = this.remove_duplicates(vars_a);
        for (int  i = 1 ; i < a.fac_values.length; i++){
            String row_name_a = Arrays.toString(a.fac_values[i]);
            row_name_a = replace_str(row_name_a);
            StringBuilder inter = new StringBuilder();
            for (String str : vars_a){
                int index = a.indexes.get(str);
                inter.append(row_name_a.charAt(index));
            }
            for (int j = 1 ; j < b.fac_values.length ; j++){
                String row_name_b = Arrays.toString(b.fac_values[j]);
                row_name_b = replace_str(row_name_b);

                StringBuilder intersect = new StringBuilder();
                for (String str : vars_a){
                    int index = b.indexes.get(str);
                    intersect.append(row_name_b.charAt(index)); // T
                }
                if (inter.toString().equals(intersect.toString())){

                    row_name_b = this.update_row(row_name_b , vars_a , b.indexes);
                   // row_name_a = this.update_row(row_name_a, vars_a , a.indexes);
                    row_name_b = row_name_b + row_name_a;
                    double value = a.fac_val[i - 1] * b.fac_val[j - 1];
                    this.numOfMul++;
                    this.values.put(row_name_b, value);
                }
            }
        }
    }

    private String update_row(String row_name_b, List<String> vars_a, HashMap<String, Integer> indexes) {
        StringBuilder res = new StringBuilder();
        ArrayList<Integer> in = new ArrayList<>();
        for (String a : vars_a){
            int index = indexes.get(a);
            in.add(index);
        }
        for (int i = 0 ; i < row_name_b.length() ; i++){
            if (!in.contains(i)){
                res.append(row_name_b.charAt(i));
            }
        }
        return res.toString();
    }


    private List<String> remove_duplicates(List<String> vars_a) {
         HashSet<String> set = new HashSet<>();
        return vars_a.stream().filter(set::add).collect(Collectors.toList());
    }

    private List<String> modify_list(Factor a) {
        return a.vars_names.stream()
                .map(Object::toString)
                .distinct()
                .collect(Collectors.toList());
    }

    private void permute(String to_split, int iterations){
        if (iterations == 0){
            this.fac_values[line++] = to_split.toCharArray();
        }else{
            permute(to_split + "T" , iterations - 1);
            permute(to_split + "F" , iterations - 1 );
        }
    }

    public double[] getFac_val() {
        return fac_val;
    }

    public String getName() {
        return name;
    }

    public boolean isOneValued() {
        return oneValued;
    }



    public void update(NodeN n1 , String name) {
        if (n1.isEvidence() && (n1.getName().equals(this.name) || this.hidden_vars.containsKey(n1.getName()))){
            this.hidden_vars.remove(name);
            this.vars_names.removeIf(character -> character.equals(name.charAt(0)));
            //this.indexes.clear();
            // update the size
            int rows_length = (int) Math.pow(2 , this.vars_names.size());
            // update the fac values
            ArrayList <Double> arr = new ArrayList<>();
            char [][] fac_values = new char[rows_length + 1][this.vars_names.size()];
            Character [] arr1 = this.vars_names.toArray(new Character[0]);
            for (int i = 0 ; i < arr1.length ; i++){
                fac_values[0][i] = arr1[i];
              //  this.indexes.put(Character.toString(fac_values[0][i]) , i);
            }
            int j = 1 ;
            int index = this.indexes.get(name);
            for (int i = 1 ; i < this.fac_values.length ; i++){
                if (this.fac_values[i][index] == 'T'){

                    arr.add(this.fac_val[i - 1]); // the position would be the
                    fac_values[j] =  this.copyrow(index , this.fac_values[i]);
                    j++;
                }
            }
            this.indexes.clear();
            for (int i = 0 ; i < arr1.length ; i++){
                this.indexes.put(Character.toString(fac_values[0][i]) , i);
            }

           this.fac_values =  Arrays.stream(fac_values)
                    .map(char[]::clone)
                    .toArray(char[][]::new);
           this.fac_val = arr.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
           this.name  = this.vars_names.get(0).toString();
           if (this.vars_names.size() == 1){
               this.oneValued = true;
           }
        }
    }

    private char[] copyrow(int index , char [] arr) {
        char [] new_arr = new char[arr.length - 1];
        int j = 0;
        for (int i = 0 ; i < arr.length ; i++){
            if (i !=  index){
                new_arr[j++] = arr[i];
            }
        }
        return new_arr;
    }

    private void arrange(char [] []cpt){
        char [][] fac_values;
        fac_values =  Arrays
                .stream(cpt) // stream
                .map(char [] :: clone) // clone the array
                .toArray(char [][]::new); // put it in the array

        Character [] arr = this.vars_names.toArray(new Character[0]);
        for (int i = 0 ; i < arr.length ; i++){
            this.fac_values[0][i] = arr[i];
        }
        this.fac_values[0][this.fac_values[0].length - 1] = this.name.charAt(0);
        for (int i = 0 ; i < this.fac_values.length - 1 ; i++){
            this.fac_values[i + 1] = Arrays.copyOf(fac_values[i] , fac_values[i].length);
        }
    }
     public void print(){
         System.out.println(Arrays.toString(this.fac_values[0]));
      for (int i = 0 ; i < this.fac_values.length - 1; i++) {
          System.out.println(Arrays.toString(this.fac_values[i + 1]) + "=" + this.fac_val[i]);
      }
    }
    private String replace_str(String str){
        str = str.replace("[" , "")
                .replace("]" , "")
                .replace(",","")
                .replace(" " , "");
        return str;}
    public Map<String, NodeN> getHidden_var() {
        return hidden_vars;
    }



}
