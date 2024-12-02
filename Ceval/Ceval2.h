#include <iostream>
#include "Compiler.h"

Compiler compiler = Compiler();
VM vm = VM();

double ceval2(std::string expr)
{
    std::vector<Command *> commands = compiler.compiler(expr);
    return vm.run(commands);
}

std::string encode(std::vector<Command *> commandList)
{
    std::string out = "";
    for (int i = 0; i < commandList.size(); i++)
    {
        out += commandList[i]->toCode();
        out += "\n";
    }
    return out;
}

std::vector<Command *> compile(std::string expr)
{
    return compiler.compiler(expr);
}

double run(std::vector<Command *> cmds)
{
    return vm.run(cmds);
}