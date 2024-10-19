import Jeval.VM;
import Jeval.Command;
import Jeval.Compiler;

public class Demo {
    public static void main(String[] args) throws Exception {
        VM vm = new VM();
        Compiler compiler = new Compiler();

        double sum_val = 0;
        Command[] codes = compiler.compiler("sqrt((x+1)(x+2))");

        for (int i = 1; i < 10001; i++) {
            vm.setVar("x", i);
            sum_val += vm.run(codes);
        }
        System.out.println(sum_val);
    }
}