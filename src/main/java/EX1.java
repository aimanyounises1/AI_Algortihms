public class EX1 {
    public static void main(String[] args) {
        try{
            VariableElimination VE = new VariableElimination("src/main/resources/input.txt");
            VE.init_factors();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
