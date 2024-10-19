package Jeval;

import java.util.ArrayList;

public class Compiler {

    public enum ItemType {
        SYM, CST, VAR, EXP, FNC
    }

    public static class Item {
        public String name;
        public String string;
        public int register = -1;
        public double value;
        public ItemType type;

        public String toString() {
            if (this.type == ItemType.CST) {
                return "[CST]" + name + "<" + value + ">";
            }
            return "[" + type + "]" + name;
        }
    }

    public static class Const extends Item {

        Const(String string) {
            this.type = ItemType.CST;
            this.value = Double.valueOf(string);
            this.string = string;
            this.name = string;
        }

        Const(double value) {
        }
    }

    public static class Symbol extends Item {
        Symbol(String string) {
            this.type = ItemType.SYM;
            this.string = string;
            this.name = string;
        }

        Symbol(char string) {
            this.type = ItemType.SYM;
            this.string = String.valueOf(string);
            this.name = this.string;
        }
    }

    public static class Variable extends Item {
        Variable(String string) {
            this.type = ItemType.VAR;
            this.string = string;
            this.name = string;

            for (String func : functionList) {
                if (func.equals(string)) {
                    type = ItemType.FNC;
                }
            }
        }
    }

    public static class Expression extends Item {
        Expression(String string) {
            this.type = ItemType.EXP;
            this.string = string;
            this.name = string;
        }
    }

    public static char[] symbolList = "+-*/^".toCharArray();
    public static char[] numberList = "0123456789.".toCharArray();
    public static char[] alphabate = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static String[] functionList = new String[] {
            "sin", "cos", "tan",
            "log", "ln", "sqrt"
    };

    public boolean mandatoryConstants = false;
    public int maxRegisterNumber = 0;
    public int autoInitRegister = 16;
    public ArrayList<Command> commandList = new ArrayList<>();
    public ArrayList<Boolean> registerList = new ArrayList<>();

    public int getFreeRegister() {
        for (int i = 0; i < registerList.size(); i++) {
            if (!registerList.get(i)) {
                registerList.set(i, true);
                return i;
            }
        }
        registerList.add(true);
        maxRegisterNumber = Math.max(maxRegisterNumber, registerList.size());
        return registerList.size() - 1;
    }

    public void freeRegister(int index) {
        registerList.set(index, false);
    }

    public void o(Command cmd) {
        commandList.add(cmd);
    }

    public static boolean isNumber(char value) {
        for (char key : numberList) {
            if (key == value)
                return true;
        }
        return false;
    }

    public static boolean isAlphabet(char value) {
        for (char key : alphabate) {
            if (key == value)
                return true;
        }
        return false;
    }

    public static boolean isSymbol(char value) {
        for (char key : symbolList) {
            if (key == value)
                return true;
        }
        return false;
    }

    public static void printItemList(ArrayList<Item> ItemList) {
        System.out.print("{");
        for (Item item : ItemList) {
            System.out.print(item.toString());
            System.out.print(", ");
        }
        System.out.println("}");
    }

    public static double ln(double x) {
        return Math.log(x) / Math.log(Math.E);
    }

    public static Command[] toCommandArray(ArrayList<Command> arrayList) {
        Command[] array = new Command[arrayList.size()];
        for (int i = 1; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }
        return array;
    }

    public static ArrayList<Item> split(String expr) {
        int startPoint = 0;
        int braStartPoint = 0;
        int inBracket = 0;
        boolean inNumber = false;
        boolean inAlpha = false;
        ArrayList<Item> results = new ArrayList<>();
        char key;
        int i = 0;

        expr = expr + " ";
        if (expr.charAt(0) == '-')
            expr = "0" + expr;

        while (i < expr.length()) {
            key = expr.charAt(i);
            if (key == ')') {
                inBracket--;
                if (inBracket == 0) {
                    Item item = new Compiler.Expression(expr.substring(braStartPoint + 1, i));
                    results.add(item);
                }
            } else if (key == '(') {
                inBracket++;
                if (inBracket == 1)
                    braStartPoint = i;
            } else if (inBracket != 0) {
                i++;
                continue;
            }

            if (inNumber && !isNumber(key)) {
                inNumber = false;
                Const item = new Const(expr.substring(startPoint, i));
                results.add(item);
            } else if (!inNumber && !inAlpha && isNumber(key)) {
                inNumber = true;
                startPoint = i;
            }
            if (inAlpha && !(isAlphabet(key) || isNumber(key))) {
                inAlpha = false;
                Item item = new Variable(expr.substring(startPoint, i));
                results.add(item);
            } else if (!inAlpha && isAlphabet(key)) {
                inAlpha = true;
                startPoint = i;
            }
            if (isSymbol(key)) {
                Item item = new Symbol(key);
                results.add(item);
            }
            i++;
        }
        return results;
    }

    public static ArrayList<Item> addMulSymbol(ArrayList<Item> itemList) {
        int i = 0;
        Item thisObj;
        Item lastObj;
        while (i < itemList.size() - 1) {
            i++;
            thisObj = itemList.get(i);
            lastObj = itemList.get(i - 1);
            if (thisObj.type == ItemType.VAR || thisObj.type == ItemType.EXP || thisObj.type == ItemType.FNC) {
                if (lastObj.type == ItemType.VAR || lastObj.type == ItemType.CST || lastObj.type == ItemType.EXP) {
                    itemList.add(i, new Symbol("*"));
                    i++;
                }
            }
        }
        return itemList;
    }

    private ArrayList<Item> calculate(ArrayList<Item> itemList) throws Exception {

        // process all the vars
        for (Item item : itemList) {
            if (item.type == ItemType.VAR) {
                int register = getFreeRegister();
                item.register = register;
                o(new Command.InitVar(item.name, register));
            }
        }

        int i = 0;
        while (i < itemList.size() - 1) {
            Item item = itemList.get(i);
            Item next = itemList.get(i + 1);
            if (item.type == ItemType.FNC && next.type == ItemType.CST) {
                Item itemfill;
                if (item.name.equals("sin")) {
                    itemfill = new Const(Math.sin(next.value));
                } else if (item.name.equals("cos")) {
                    itemfill = new Const(Math.cos(next.value));
                } else if (item.name.equals("tan")) {
                    itemfill = new Const(Math.tan(next.value));
                } else if (item.name.equals("log")) {
                    itemfill = new Const(Math.log(next.value));
                } else if (item.name.equals("ln")) {
                    itemfill = new Const(ln(next.value));
                } else if (item.name.equals("sqrt")) {
                    itemfill = new Const(Math.sqrt(next.value));
                } else {
                    throw new Exception("Unknown function name: " + item.name);
                }

                itemList.set(i, itemfill);
                itemList.remove(i + 1);
                i++;
            } else if (item.type == ItemType.FNC) {
                if (item.name.equals("sin")) {
                    o(new Command.Sin(next.register, next.register));
                } else if (item.name.equals("cos")) {
                    o(new Command.Cos(next.register, next.register));
                } else if (item.name.equals("tan")) {
                    o(new Command.Tan(next.register, next.register));
                } else if (item.name.equals("log")) {
                    o(new Command.Log(next.register, next.register));
                } else if (item.name.equals("ln")) {
                    o(new Command.Ln(next.register, next.register));
                } else if (item.name.equals("sqrt")) {
                    o(new Command.Sqrt(next.register, next.register));
                } else {
                    throw new Exception("Unknown function name: " + item.name);
                }

                Item itemfill = new Variable("temp");
                itemfill.register = next.register;
                itemList.set(i, itemfill);
                itemList.remove(i + 1);
                i++;
            }
            i++;
        }

        i = 0;
        while (i < itemList.size()) {
            Item item = itemList.get(i);
            int symbolType;
            if (item.type == ItemType.SYM) {

                if (i == 0 || i == itemList.size() - 1)
                    throw new Exception("Binocular operators cannot be at the beginning/end");
                if (item.name.equals("*")) {
                    symbolType = 1;
                } else if (item.name.equals("/")) {
                    symbolType = 2;
                } else {
                    i++;
                    continue;
                }

                Item lastItem = itemList.get(i - 1);
                Item nextItem = itemList.get(i + 1);
                Item itemfill = null;
                if (lastItem.register == -1 && nextItem.register == -1) {
                    if (symbolType == 1) {
                        itemfill = new Const(lastItem.value * nextItem.value);
                    } else {
                        itemfill = new Const(lastItem.value / nextItem.value);
                    }
                } else if (lastItem.register != -1 && nextItem.register == -1) {
                    int ro = lastItem.register;
                    if (symbolType == 1) {
                        o(new Command.Time(nextItem.value, ro));
                    } else {
                        o(new Command.Time(1 / nextItem.value, ro));
                    }
                    itemfill = new Variable("temp");
                    itemfill.register = ro;
                } else if (lastItem.register == -1 && nextItem.register != -1) {
                    int ro = nextItem.register;
                    if (symbolType == 1) {
                        o(new Command.Time(lastItem.value, ro));
                    } else {
                        o(new Command.Time(1 / lastItem.value, ro));
                    }
                    itemfill = new Variable("temp");
                    itemfill.register = ro;
                } else {
                    int r1 = lastItem.register;
                    int r2 = nextItem.register;
                    if (symbolType == 1) {
                        o(new Command.Mul(r1, r2, r2));
                    } else {
                        o(new Command.Div(r1, r2, r2));
                    }
                    freeRegister(r1);
                    itemfill = new Variable("temp");
                    itemfill.register = r2;
                }
                itemList.set(i, itemfill);
                itemList.remove(i - 1);
                itemList.remove(i);
            } else {
                i++;
            }
        }

        i = 0;
        while (i < itemList.size()) {
            Item item = itemList.get(i);
            int symbolType;
            if (item.type == ItemType.SYM) {

                if (i == 0 || i == itemList.size() - 1)
                    throw new Exception("Binocular operators cannot be at the beginning/end");
                if (item.name.equals("+")) {
                    symbolType = 1;
                } else if (item.name.equals("-")) {
                    symbolType = 2;
                } else {
                    i++;
                    continue;
                }

                Item lastItem = itemList.get(i - 1);
                Item nextItem = itemList.get(i + 1);
                Item itemfill = null;
                if (lastItem.register == -1 && nextItem.register == -1) {
                    if (symbolType == 1) {
                        itemfill = new Const(lastItem.value + nextItem.value);
                    } else {
                        itemfill = new Const(lastItem.value - nextItem.value);
                    }
                } else if (lastItem.register != -1 && nextItem.register == -1) {
                    int ro = lastItem.register;
                    if (symbolType == 1) {
                        o(new Command.Accumulate(nextItem.value, ro));
                    } else {
                        o(new Command.Accumulate(0 - nextItem.value, ro));
                    }
                    itemfill = new Variable("temp");
                    itemfill.register = ro;
                } else if (lastItem.register == -1 && nextItem.register != -1) {
                    int ro = nextItem.register;
                    if (symbolType == 1) {
                        o(new Command.Accumulate(lastItem.value, ro));
                    } else {
                        o(new Command.Accumulate(1 / lastItem.value, ro));
                    }
                    itemfill = new Variable("temp");
                    itemfill.register = ro;
                } else {
                    int r1 = lastItem.register;
                    int r2 = nextItem.register;
                    if (symbolType == 1) {
                        o(new Command.Add(r1, r2, r2));
                    } else {
                        o(new Command.Min(r1, r2, r2));
                    }
                    freeRegister(r1);
                    itemfill = new Variable("temp");
                    itemfill.register = r2;
                }
                itemList.set(i, itemfill);
                itemList.remove(i - 1);
                itemList.remove(i);
            } else {
                i++;
            }
        }

        return itemList;
    }

    private Item autoCompileLauncher(String expr) throws Exception {
        ArrayList<Item> itemList = split(expr);
        itemList = addMulSymbol(itemList);

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (item.type == ItemType.EXP) {
                Item j = autoCompileLauncher(item.string);
                if (j.type == ItemType.CST) {
                    itemList.set(i, new Const(j.value));
                } else {
                    item.register = j.register;
                }
            }
        }

        itemList = calculate(itemList);
        if (!(itemList.size() == 1)) {
            throw new Exception("Two separate expressions were entered.");
        } else {
            return itemList.get(0);
        }
    }

    public Command[] compiler(String expr) throws Exception {
        commandList = new ArrayList<Command>();
        registerList = new ArrayList<>();
        maxRegisterNumber = 0;

        try {
            Item i = autoCompileLauncher(expr);
            if (i.type == ItemType.CST) {
                o(new Command.ReturnNumber(i.value));
            } else {
                o(new Command.Return(i.register));
            }

            if (maxRegisterNumber > autoInitRegister) {
                Command command = new Command.InitRegister(maxRegisterNumber);
                commandList.add(0, command);
            }
        } catch (Exception e) {
            throw e;
        }

        return commandList.toArray(new Command[commandList.size()]);
    }
}
