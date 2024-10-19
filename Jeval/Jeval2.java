package Jeval;

import java.io.File;
import java.io.FileWriter;

public class Jeval2 {
    private static VM vm = new VM();
    private static double timeRecoder;
    private static Compiler compiler = new Compiler();

    public static double jeval(String expr) {
        return Item.jeval(expr);
    }

    public static double jeval2(String expr) throws Exception {
        Command[] commands = compiler.compiler(expr);
        return vm.run(commands);
    }

    public static String encode(Command[] commands) {
        StringBuilder out = new StringBuilder();
        for (Command cmd : commands) {
            out.append(cmd.toCode());
            out.append("\n");
        }
        return out.toString();
    }

    public static Command[] compile(String expr) throws Exception {
        return compiler.compiler(expr);
    }

    public static void compile_as_file(String expr, String fileName) throws Exception {
        String code = encode(compile(expr));
        File file = new File(fileName);
        if (!file.exists())
            file.createNewFile();
        FileWriter fileWriter = new FileWriter(file.getName());
        fileWriter.write(code);
        fileWriter.close();
    }

    public static double run(Command[] commandArray) {
        return vm.run(commandArray);
    }

    public static double timer() {
        double currentTime = System.currentTimeMillis();
        double deltaTimeMs = currentTime - timeRecoder;
        timeRecoder = currentTime;
        return deltaTimeMs;
    }

    public static void main(String[] args) throws Exception {
        double ans = jeval("-sin(0.5*pi)");
        System.out.println(ans);
    }
}