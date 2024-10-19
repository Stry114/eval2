# Ceval/Jeval/Peval
在Java/C++中**部分地**实现了类似Python`eval()`的功能：传入一个字符串表达式，然后计算出表达式的值。
另外，在Python中提供了比`eval()`更快的求值函数。

项目包含三个解释器与三个简单的编译器和三个简单的虚拟机（基于上述三种语言），用于求表达式的值，或将表达式编译成简单的机器码并运行。
## 示例
### 计算线性数学表达式
Java
```Java
double ans = Jeval2.jeval("-16+5^(8-5)");
System.out.println(ans);

>>>109.0
```

Python
```Python
ans = Peval2.peval("-16+5^(8-5)")
print(ans)

>>>109
```

### 支持含有变量与函数的表达式
Java
```Java
double ans = Jeval2.jeval("-sin(0.5*pi)");
System.out.println(ans);

>>>-1.0
```

Python
```Python
ans = Peval2.peval("-sin(0.5*pi)")
print(ans)

>>>109
```
# 使用
项目包含三个解释器与三个简单的编译器和三个简单的虚拟机（基于上述三种语言）。
 - 当你需要直接求表达式的值时，可以使用解释器。
 - 当你需要计算大量重复的运算，而每次的运算只有个别参数不同时，可以使用编译器编译后运行。以此方式将极大地提升计算性能。（参考下方“用例”章节）
## Jeval (基于Java)
### 使用解释器
```Java
import Jeval.Jeval2;

public class Demo {
    public static void main(String[] args) {
        double ans = Jeval2.jeval("-16+5^(8-5)");
        System.out.println(ans);
    }
}

>>>109.0
```
### 使用编译器与虚拟机
```Java
import Jeval.Compiler;
import Jeval.Command;
import Jeval.VM;

public class Demo {
    public static void main(String[] args) throws Exception {
        VM vm = new VM();
        Compiler compiler = new Compiler();

        Command[] codes = compiler.compiler("2e*sin(0.5pi)");
        double ans = vm.run(codes);
        System.out.println(ans);
    }
}

>>>5.43656365691809
```
你也可以直接使用`Jeval2.jeval2()`函数来自动完成上述过程（编译与运行）。
```Java
import Jeval.Jeval2;

public class Demo {
    public static void main(String[] args) throws Exception {
        double ans = Jeval2.jeval2("2e*sin(0.5pi)");
        System.out.println(ans);
    }
}
>>>5.43656365691809
```
## Peval (基于Python)
### 使用解释器
```Python
from Peval.Peval2 import peval

ans = peval("-16+5^(8-5)")
print(ans)

>>>109
```

### 使用编译器
```Python
from Peval.compiler import Compiler
from Peval.runtime import VM

compiler = Compiler()
vm = VM()

codes = compiler.compiler("2e*sin(0.5pi)")
ans = vm.run(codes)
print(ans)

>>>5.43656365691809
```
你也可以直接使用`Peval2.peval2()`函数来自动完成上述过程（编译与运行）。
```Python
from Peval.Peval2 import peval2

ans = peval2("2e*sin(0.5pi)")
print(ans)

>>>5.43656365691809
```
## Ceval (基于C++)
目前Ceval只完成了runtime与vm，基于C++的编译器与解释器仍处于调试与开发阶段。
如有必要，可以尝试编写机器码，然后在vm上运行。
# 表达式规范
## 使用解释器时 (ceval/jevel/peval)
### 解释器支持五种基本运算符号：

|符号|名称|优先级|
|---|---|----|
|+|加法|最低|
|-|减法|最低|
|*|乘法|中|
|/|除法|中|
|^|乘方|最高|

### 解释器支持括号，也支持括号嵌套
```
    (1+2*(5-9))*5
    = -35
```
值得注意的是，解释器支持英文圆括号与英文方括号（`( )`与`[ ]`）。二者将被视为完全相同的括号,这意味着两种括号可以混用。
另外，请不要省略函数名后的括号。
### 解释器支持函数运算
|函数名称|说明|
|----|----|
|sin|正弦函数，弧度制|
|cos|余弦函数，弧度制|
|tan|正切函数，弧度制|
|log|以10为底数的对数函数|
|ln|以e为底数的对数函数|
|sqrt|开平方根|
|ceil|向上取整|
|floor|向下取整|
|round|四舍五入取整|
### 解释器支持变量
默认含有以下基本变量
|变量名称|默认值|说明|
|---|---|---|
|pi|3.14159265358979323846|圆周率|
|e|2.71828182845904523536|自然常数|
|ans|0|自动存储上一次计算的结果|
|A|0||
|B|1||
|C|2||
|D|3||
|E|4||
|F|5||
|M|6||
|X|0||
|Y|1||

在不同语言中，此功能的实现方式：
|语言|实现方式|
|---|---|
|Peval|字典|
|Jeval|HashMap<String, Double>|
|Ceval|std::unordered_map<std::string, double>|

### 解释器支持省略乘号
在变量/函数前方加上数字，可以表示二者相乘。
```
2a
= 2*a
```

```
2sin(1)
= 2*sin(1)
```
## 使用编译器与VM时 (ceval2/jevel2/peval2)
### 编译器支持四种基本运算符号：

|符号|名称|优先级|
|---|---|----|
|+|加法|低|
|-|减法|低|
|*|乘法|高|
|/|除法|高|
### 编译器支持括号，也支持括号嵌套
```
    (1+2*(5-9))*5
    = -35
```
值得注意的是，编译器**只支持英文圆括号**。
另外，请不要省略函数名后的括号。
### 编译器支持函数运算
|函数名称|说明|
|----|----|
|sin|正弦函数，弧度制|
|cos|余弦函数，弧度制|
|tan|正切函数，弧度制|
|log|以10为底数的对数函数|
|ln|以e为底数的对数函数|
|sqrt|开平方根|
### 虚拟机支持变量
以下是所有初始变量。

|变量名称|初始值|说明|
|---|---|---|
|pi|3.14159265358979323846|圆周率|
|e|2.71828182845904523536|自然常数|
|ans|0||
|A|0||
|B|1||
|C|2||
|D|3||
|E|4||
|F|5||
|M|6||
|X|0||
|Y|1||

得注意的是，每一个虚拟机实例都存储有自己的变量。虚拟机与解释器之间也不能共享变量。可以使用`VM.setVar(varName, value)`函数来修改变量的值或者创建新的变量。具体用例如下：
 - vm for python
```Python
from Peval.runtime import VM

vm = VM()
vm.setVar("A", 1.14);
vm.setVar("new_var", 5.14);
```

 - vm for Java
```Java
import Jeval.VM;

public class Demo {
    public static void main(String[] args) {
        VM vm = new VM();
        vm.setVar("A", 1.14);
        vm.setVar("new_var", 5.14);
    }
}
```

 - vm for C++
```C++
#include "Ceval/Runtime.h"

int main()
{
    VM vm = VM();
    vm.setVar("A", 1.14);
    vm.setVar("new_var", 5.14);
    return 0;
}
```

在不同版本中，变量都以键值对的方式存储在哈希表中：
|语言|实现方式|
|---|---|
|Peval (Python3)|字典|
|Jeval (Java)|HashMap<String, Double>|
|Ceval (Cpp)|std::unordered_map<std::string, double>|

### 解释器支持省略乘号
在变量/函数前方加上数字，可以表示二者相乘。
```
2a
= 2*a
```

```
2sin(1)
= 2*sin(1)
```

在变量之间添加空格，可以表示二者相乘。
```
2A pi
= 2*A*pi
```
# 虚拟机与编译器
## 编译器
 - 编译器负责将字符串表达式编译成机器码，机器码的本质是由Command类（及其子类）实例组成的数组/列表。
 - 通常，表达式中不含任何变量的部分会被编译器直接计算出来，并直接用计算结果替代，以此来提高vm的运行效率。
## 虚拟机
 - 每个虚拟机会包含16个寄存器（r0~r15）和1个结果寄存器。
 - 若需要使用超过16个寄存器，需要使用`.initReg`命令来初始化更多的寄存器。
 - 所有的寄存器都只能存储浮点数类型的值。具体如下表：

|语言|类型|
|---|---|
|Python (Peval.VM)|float|
|Java (Jeval.VM)|double/Double|
|C++ (Ceval.VM)|double|

因此，所有的运算也都基于浮点数。
## 机器码
|机器码|类名|语法|描述|
|---|--|---|---|
|initVar|InitVar|`.initVar var -> r0`|将变量`var`的值移动到寄存器r0中|
|const|Const|`.const n -> r0`|将寄存器r0的值设置为n|
|add|Add|`.add r1 r2 -> r0`|将寄存器r1与r2的值相加的结果赋值给r0|
|min|Min|`.min r1 r2 -> r0`|将寄存器r1与r2的值相减的结果赋值给r0|
|mul|Mul|`.mul r1 r2 -> r0`|将寄存器r1与r2的值相乘的结果赋值给r0|
|div|Div|`.div r1 r2 -> r0`|将寄存器r1与r2的值相除的结果赋值给r0|
|return|Return|`.return r0`|将寄存器r0的值复制到结果寄存器|
|retNum|ReturnNumber|`.retNum n`|将寄n赋值给结果寄存器|
|acc|Accumulate|`.acc n -> r0`|将寄存器r0的值加上n|
|time|Time|`.time n -> r0`|将寄存器r0的值乘以n|
|initReg|InitRegister|`.initReg n`|在虚拟机中初始化n个寄存器，初值为0|
|sin|Sin|`.sin r1 -> r0`|求寄存器r1中数值的sin值，并赋值给寄存器r0|
|cos|Cos|`.cos r1 -> r0`|求寄存器r1中数值的cos值，并赋值给寄存器r0|
|tan|Tan|`.tan r1 -> r0`|求寄存器r1中数值的tan值，并赋值给寄存器r0|
|log|Log|`.log r1 -> r0`|求寄存器r1中数值的log值，并赋值给寄存器r0|
|ln|Ln|`.ln r1 -> r0`|求寄存器r1中数值的ln值，并赋值给寄存器r0|
|sqrt|Sqrt|`.sqrt r1 -> r0`|求寄存器r1中数值的sqrt值，并赋值给寄存器r0|
## 查看编译器编译产生的机器码
可以使用`encode()`函数查看编译结果。
### Java
```Java
import Jeval.Jeval2;
import Jeval.Command;
import Jeval.Compiler;

public class Demo {
    public static void main(String[] args) throws Exception {
        Compiler compiler = new Compiler();

        Command[] codes = compiler.compiler("5sin(0.5pi)");
        System.out.println(Jeval2.encode(codes));
    }
}
```
```
.initVar pi -> r0
.time 0.5 -> r0
.sin r0 -> r0
.time 5.0 -> r0
.return r0
```
### Python
```Python
from Peval.compiler import Compiler
from Peval.Peval2 import encode

compiler = Compiler()

codes = compiler.compiler("5sin(0.5pi)")
print(encode(codes))
```
```
.initVar pi -> r0
.time 0.5 -> r0
.sin r0 -> r0
.time 5.0 -> r0
.return r0
```
# 用例：计算数列求和
计算数列 $a_n=\sqrt{(n+1)(n+2)} $ 的前10000项和
 - Python
```Python
from Peval.runtime import VM
from Peval.compiler import Compiler

vm = VM()
compiler = Compiler()

sum_val = 0
codes = compiler.compiler("sqrt((x+1)(x+2))")
for i in range(1, 10001):
    vm.setVar("x", i)
    sum_val += vm.run(codes)
print(sum_val)

>>>50019998.93564133
```
 - Java
```Java
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

>>>5.001999893564133E7
```
### 性能对比
|方案|用时(s)|输出结果|
|---|---|---|
|Python build-in `eval()`|0.0651490|50019998.93564133|
|Peval Compiler & VM|0.0066150|50019998.93564133|
|Jeval Compiler & VM|0.0148496|5.001999893564133E7|
|Ceval VM|0.001924|5.002e+07|
 - 测试数据为平均数据。
 - 经测试，Jeval 在编译阶段耗费了大量时间，导致其测试表现远低于 Peval。这是由于其编译器性能不佳。
 - Ceval的编译器未完成，测试数据是手动编写机器码后，使用 Ceval VM 运行的结果。
# 关于文件及版本的说明
## Item
名为`Item`的文件支持了使用解释器计算表达式的值。
此文件依靠bug运行，别问，我也不想维护。
## Runtime/VM/Command
 - 名为`Command`的文件包含了机器码的类，及所有机器码的共同父类`Command`
 - 名为`VM`的文件包含了虚拟机类
 - 名为`Runtime`的文件包含了以上两项。
## Compiler
名为`Compiler`的文件包含编译器的类。
## Peval2/Jeval2/Ceval2
以2结尾的文件包含了很多可以直接使用的函数
## 版本
 - 我将目前的版本号记为1。通过`VM.verCode`来查看版本号。
 - C++版本的编译器仍未完成，将在日后逐渐完善。
 - 关于解释器，它是个早期的作品，稍微改一下就会出现很多bug，现在我也不愿意维护它们了，我也不建议任何人去动它。