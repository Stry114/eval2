package Jeval;

public class Command {
    public void run(VM vm) {
    };

    public String toCode() {
        return ".pass";
    };

    public static enum Type {
        INIT_VAR,
        CONST,
        ADD, MIN,
        MUL, DIV,
        RETURN,
        ACCUMULATE,
        INIT_REGISTER,
        RETURN_NUMBER,
        SIN, COS, TAN,
        LOG, LN, SQRT,
        TIME,
        TO_INT, ABS,
    }

    public static class Sin extends Command {
        private int r1, ro;

        Sin(int r1, int ro) {
            this.r1 = r1;
            this.ro = ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = Math.sin(vm.registers[r1]);
        }

        @Override
        public String toCode() {
            return ".sin r" + r1 + " -> r" + ro;
        }

        public double func(Double x) {
            return Math.sin(x);
        }
    }

    public static class Cos extends Command {
        private int r1, ro;

        Cos(int r1, int ro) {
            this.r1 = r1;
            this.ro = ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = Math.cos(vm.registers[r1]);
        }

        @Override
        public String toCode() {
            return ".cos r" + r1 + " -> r" + ro;
        }

        public double func(Double x) {
            return Math.cos(x);
        }
    }

    public static class Tan extends Command {
        private int r1, ro;

        Tan(int r1, int ro) {
            this.r1 = r1;
            this.ro = ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = Math.tan(vm.registers[r1]);
        }

        @Override
        public String toCode() {
            return ".tan r" + r1 + " -> r" + ro;
        }

        public double func(Double x) {
            return Math.tan(x);
        }
    }

    public static class Log extends Command {
        private int r1, ro;

        Log(int r1, int ro) {
            this.r1 = r1;
            this.ro = ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = Math.log10(vm.registers[r1]);
        }

        @Override
        public String toCode() {
            return ".log r" + r1 + " -> r" + ro;
        }

        public double func(Double x) {
            return Math.log10(x);
        }
    }

    public static class Ln extends Command {
        private int r1, ro;

        Ln(int r1, int ro) {
            this.r1 = r1;
            this.ro = ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = func(vm.registers[r1]);
        }

        @Override
        public String toCode() {
            return ".ln r" + r1 + " -> r" + ro;
        }

        public double func(Double x) {
            return Math.log10(x) / Math.log10(Math.E);
        }
    }

    public static class Sqrt extends Command {
        private int r1, ro;

        Sqrt(int r1, int ro) {
            this.r1 = r1;
            this.ro = ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = Math.sqrt(vm.registers[r1]);
        }

        @Override
        public String toCode() {
            return ".sqrt r" + r1 + " -> r" + ro;
        }

        public double func(Double x) {
            return Math.sqrt(x);
        }
    }

    public static class ToInt extends Command {
        private int r1, ro;

        ToInt(int r1, int ro) {
            this.r1 = r1;
            this.ro = ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = Math.floor(vm.registers[r1]);
        }

        @Override
        public String toCode() {
            return ".toInt r" + r1 + " -> r" + ro;
        }

        public double func(double x) {
            return Math.floor(x);
        }
    }

    public static class Abs extends Command {
        private int r1, ro;

        Abs(int r1, int ro) {
            this.r1 = r1;
            this.ro = ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = Math.abs(vm.registers[r1]);
        }

        @Override
        public String toCode() {
            return ".abs r" + r1 + " -> r" + ro;
        }

        public double func(double x) {
            return Math.abs(x);
        }
    }

    public static class InitRegister extends Command {
        private int number;

        InitRegister(int number) {
            this.number = number;
        }

        @Override
        public String toCode() {
            return ".initReg " + number;
        }

        @Override
        public void run(VM vm) {
            if (vm.registers.length < number) {
                vm.registers = new double[number];
            }
        }
    }

    public static class ReturnNumber extends Command {
        private double number;

        ReturnNumber(double number) {
            this.number = number;
        }

        @Override
        public String toCode() {
            return ".retNum " + number;
        }

        @Override
        public void run(VM vm) {
            vm.result = number;
        }
    }

    public static class Accumulate extends Command {
        private double number;
        private int ro;

        Accumulate(double number, int ro) {
            this.number = number;
            this.ro = ro;
        }

        @Override
        public String toCode() {
            return ".acc " + number + " -> r" + ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] += number;
        }
    }

    public static class Time extends Command {
        private double number;
        private int ro;

        Time(double number, int ro) {
            this.number = number;
            this.ro = ro;
        }

        @Override
        public String toCode() {
            return ".time " + number + " -> r" + ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] *= number;
        }
    }

    public static class Add extends Command {
        private int r1, r2, ro;

        Add(int r1, int r2, int ro) {
            this.r1 = r1;
            this.r2 = r2;
            this.ro = ro;
        }

        @Override
        public String toCode() {
            return ".add r" + r1 + " r" + r2 + " -> r" + ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = vm.registers[r1] + vm.registers[r2];
        }
    }

    public static class Min extends Command {
        private int r1, r2, ro;

        Min(int r1, int r2, int ro) {
            this.r1 = r1;
            this.r2 = r2;
            this.ro = ro;
        }

        @Override
        public String toCode() {
            return ".min r" + r1 + " r" + r2 + " -> r" + ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = vm.registers[r1] - vm.registers[r2];
        }
    }

    public static class Mul extends Command {
        private int r1, r2, ro;

        Mul(int r1, int r2, int ro) {
            this.r1 = r1;
            this.r2 = r2;
            this.ro = ro;
        }

        @Override
        public String toCode() {
            return ".mul r" + r1 + " r" + r2 + " -> r" + ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = vm.registers[r1] * vm.registers[r2];
        }
    }

    public static class Div extends Command {
        private int r1, r2, ro;

        Div(int r1, int r2, int ro) {
            this.r1 = r1;
            this.r2 = r2;
            this.ro = ro;
        }

        @Override
        public String toCode() {
            return ".div r" + r1 + " r" + r2 + " -> r" + ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = vm.registers[r1] / vm.registers[r2];
        }
    }

    public static class Return extends Command {
        private int r0;

        Return(int r0) {
            this.r0 = r0;
        }

        @Override
        public String toCode() {
            return ".return r" + r0;
        }

        @Override
        public void run(VM vm) {
            vm.result = vm.registers[r0];
        }
    }

    public static class Const extends Command {
        private double value;
        private int ro;

        Const(double value, int ro) {
            this.value = value;
            this.ro = ro;
        }

        @Override
        public String toCode() {
            return ".const " + value + " -> " + ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = value;
        }
    }

    public static class InitVar extends Command {
        private String varName;
        private int ro;

        InitVar(String varName, int ro) {
            this.varName = varName;
            this.ro = ro;
        }

        @Override
        public String toCode() {
            return ".initVar " + varName + " -> r" + ro;
        }

        @Override
        public void run(VM vm) {
            vm.registers[ro] = vm.varDic.get(varName);
        }
    }
}
