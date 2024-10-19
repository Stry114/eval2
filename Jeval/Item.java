package Jeval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item {
    public int type;
    public double value;
    public String name;
    public char symbol;

    public static String version = "Jeval v1.5 2024.07.13 (Synchronize with Peval v1.4)";
    private static char[] symbolList = { '^', '*', '/', '+', '-' };
    private static char[] openingParenthesesList = { '(', '[' };
    private static char[] closingParenthesesList = { ')', ']' };
    private static char[][] symbolGroupList = {
            { '^', },
            { '*', '/' },
            { '+', '-' },
    };
    private static HashMap<String, Double> localVariableDic = new HashMap<String, Double>() {
        {
            put("pi", Math.PI);
            put("e", Math.E);
            put("A", 0.);
            put("B", 1.);
            put("C", 2.);
            put("D", 3.);
            put("E", 4.);
            put("F", 5.);
            put("M", 6.);
            put("X", 0.);
            put("Y", 1.);
        }
    };

    public static boolean contains(char[] arr, char targetValue) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == targetValue) {
                return true;
            }
        }
        return false;
    }

    public static boolean isdigit(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAlphabetic(String s) {
        Pattern p = Pattern.compile("[a-zA-Z]{1,}");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public void getType(String value) {
        if (isdigit(value)) {
            this.type = 1;
        } else if ((value.length() == 1) && (contains(symbolList, value.charAt(0)))) {
            this.type = 2;
        } else if (isAlphabetic(value)) {
            this.type = 3;
        } else {
            this.type = 5;
        }
    }

    Item(String value) {
        this.getType(value);
        if (this.type == 1) {
            this.value = Double.valueOf(value);
        } else if (this.type == 5) {
            this.value = jeval(value);
        } else if (this.type == 3) {
            this.value = localVariableDic.get(value);
            this.name = value;
        } else if (this.type == 4) {
            this.name = value;
        } else {
            this.symbol = value.charAt(0);
        }
    }

    Item(String value, int type) {
        this.type = type;
        if (this.type == 1) {
            this.value = Double.valueOf(value);
        } else if (this.type == 5) {
            this.value = jeval(value);
        } else if (this.type == 3) {
            this.value = localVariableDic.get(value);
            this.name = value;
        } else if (this.type == 4) {
            this.name = value;
        } else {
            this.symbol = value.charAt(0);
        }
    }

    public String toString() {
        if (this.type == 1) {
            return "[num]" + this.value;
        } else if (this.type == 2) {
            return "[sym]" + this.symbol;
        } else if (this.type == 3) {
            return "[var]" + this.name;
        } else if (this.type == 4) {
            return "[fnc]" + this.name;
        } else if (this.type == 5) {
            return "[expr]" + this.value;
        } else {
            return "[unk]";
        }
    }

    public static double jeval(String expr) {
        ArrayList<Item> itemList = new ArrayList<>();
        int inParenthses = 0;
        int startPoint = 0;

        // split expr into items
        // process negative number
        if (expr.charAt(0) == '-') {
            expr = '0' + expr;
        }

        for (int i = 0; i < expr.length(); i++) {
            if (expr.charAt(i) == '-' && inParenthses == 0) {
                char lastObject = expr.charAt(i - 1);
                if (contains(symbolList, lastObject)) {
                    continue;
                }
            }
            if (i > 0 && inParenthses == 0) {
                if ((Item.isdigit(String.valueOf(expr.charAt(i - 1)))
                        || contains(closingParenthesesList, expr.charAt(i - 1)))
                        && (Character.isLetter(expr.charAt(i))
                                || contains(openingParenthesesList, expr.charAt(i)))) {
                    if (!(startPoint == i)) {
                        itemList.add(new Item(expr.substring(startPoint, i), 1));
                    }
                    itemList.add(new Item("*", 2));
                    startPoint = i;
                }
            }

            if (contains(symbolList, expr.charAt(i)) && inParenthses == 0) {
                if (!(startPoint == i)) {
                    itemList.add(new Item(expr.substring(startPoint, i)));
                }
                itemList.add(new Item(String.valueOf(expr.charAt(i))));
                startPoint = i + 1;
            } else if (contains(openingParenthesesList, expr.charAt(i)) && inParenthses == 0) {
                inParenthses = 1;
                if (!(startPoint == i)) {
                    itemList.add(new Item(expr.substring(startPoint, i), 4));
                }
                startPoint = i;
            } else if (contains(openingParenthesesList, expr.charAt(i))) {
                inParenthses++;
            } else if (contains(closingParenthesesList, expr.charAt(i)) && inParenthses == 1) {
                inParenthses = 0;
                itemList.add(new Item(expr.substring(startPoint + 1, i), 5));
                startPoint = i + 1;
            } else if (contains(closingParenthesesList, expr.charAt(i))) {
                inParenthses--;
            } else if (i == expr.length() - 1) {
                if (startPoint == 0) {
                    return (new Item(expr)).value;
                }
                itemList.add(new Item(expr.substring(startPoint)));
            }
        }
        // print item list
        // for (Item item : itemList) {
        // System.out.print(item.toString() + " ");
        // }
        // System.out.println();

        // main eval step
        // process function
        int i = 0;
        while (i < itemList.size()) {
            Item item = itemList.get(i);
            double tmp;
            if (item.type == 4) {
                if (item.name.equals("sin")) {
                    tmp = Math.sin(itemList.get(i + 1).value);
                } else if (item.name.equals("cos")) {
                    tmp = Math.cos(itemList.get(i + 1).value);
                } else if (item.name.equals("tan")) {
                    tmp = Math.tan(itemList.get(i + 1).value);
                } else if (item.name.equals("log")) {
                    tmp = Math.log10(itemList.get(i + 1).value);
                } else if (item.name.equals("sqrt")) {
                    tmp = Math.sqrt(itemList.get(i + 1).value);
                } else if (item.name.equals("abs")) {
                    tmp = Math.abs(itemList.get(i + 1).value);
                } else if (item.name.equals("round")) {
                    tmp = Math.round(itemList.get(i + 1).value);
                } else if (item.name.equals("ln")) {
                    tmp = Math.log10(itemList.get(i + 1).value) / Math.log10(Math.E);
                } else if (item.name.equals("ceil")) {
                    tmp = Math.ceil(itemList.get(i + 1).value);
                } else if (item.name.equals("floor")) {
                    tmp = Math.floor(itemList.get(i + 1).value);
                } else {
                    throw new UnknownError();
                }
                itemList.set(i, new Item(String.valueOf(tmp)));
                itemList.remove(i + 1);
                i = i + 1;
            } else {
                i = i + 1;
            }
        }
        // process basic calculate
        for (int j = 0; j < symbolGroupList.length; j++) {
            char[] symbolGroup = symbolGroupList[j];
            i = 0;
            while (i < itemList.size()) {
                if (!(itemList.get(i).type == 2)) {
                    i++;
                    continue;
                }
                if (!(contains(symbolGroup, itemList.get(i).symbol))) {
                    i++;
                    continue;
                }
                char symbol = itemList.get(i).symbol;
                double tmp;
                if (symbol == '^') {
                    tmp = Math.pow(itemList.get(i - 1).value, itemList.get(i + 1).value);
                } else if (symbol == '*') {
                    tmp = itemList.get(i - 1).value * itemList.get(i + 1).value;
                } else if (symbol == '/') {
                    tmp = itemList.get(i - 1).value / itemList.get(i + 1).value;
                } else if (symbol == '+') {
                    tmp = itemList.get(i - 1).value + itemList.get(i + 1).value;
                } else if (symbol == '-') {
                    tmp = itemList.get(i - 1).value - itemList.get(i + 1).value;
                } else {
                    throw (new UnknownError());
                }
                itemList.set(i, new Item(String.valueOf(tmp), 1));
                itemList.remove(i - 1);
                itemList.remove(i);
            }
        }

        localVariableDic.put("ans", itemList.get(0).value);
        return itemList.get(0).value;
    }

    public static String toAutoString(double value) {
        StringBuilder output;
        if (value % 1 == 0) {
            return String.valueOf((int) value);
        }

        output = new StringBuilder(String.valueOf(value));
        for (int i = output.length() - 1; i >= 0; i--) {
            if (i > 13) {
                output.delete(i, i + 1);
            } else if (output.charAt(i) == '0') {
                output.delete(i, i + 1);
            } else {
                break;
            }
        }
        return output.toString();
    }

    public static void main(String[] args) {
        double a = -0.5;
        double b = -0.1;
        String c = toAutoString(a + b);
        System.out.println(c);
    }
}
