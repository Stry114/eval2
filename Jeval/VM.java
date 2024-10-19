package Jeval;

import java.util.HashMap;
import java.lang.Math;

public class VM {
    public HashMap<String, Double> varDic;
    public double[] registers;
    public double result;
    public final String version = "Jeval-VM P1";
    public final int verCode = 1;
    public final int CorrespondingPevalVersionCode = 1;

    public VM() {
        this.varDic = new HashMap<>() {
            {
                put("A", 0.);
                put("B", 1.);
                put("C", 2.);
                put("D", 3.);
                put("E", 4.);
                put("F", 5.);
                put("M", 6.);
                put("X", 0.);
                put("Y", 1.);
                put("ans", 0.);
                put("e", Math.E);
                put("pi", Math.PI);
            }
        };
        this.registers = new double[] {
                0., 0., 0., 0.,
                0., 0., 0., 0.,
                0., 0., 0., 0.,
                0., 0., 0., 0.
        };
        this.result = 0;
    }

    public void setVar(String varName, double value) {
        varDic.put(varName, value);
    }

    public Double run(Command[] commandList) {
        result = 0;
        for (Command cmd : commandList) {
            cmd.run(this);
        }
        return result;
    }

    public static void main(String[] args) {
        VM vm = new VM();
        Command[] cmds = new Command[] {
                new Command.InitVar("e", 0),
                new Command.Accumulate(5., 0),
                new Command.Return(0)
        };

        double timer = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            vm.run(cmds);
        }
        timer = System.currentTimeMillis() - timer;
        System.out.println("time= " + timer + " ms");
    }
}