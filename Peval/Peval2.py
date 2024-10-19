import math
import time
import Peval.Item as Item
import Peval.decoder as decoder
from Peval.compiler import *
from Peval.runtime import *

compiler = Compiler()
vm = VM()


def peval(expr: str):
    return Item.peval(expr)


def peval2(expr: str):
    commands = compiler.compiler(expr)
    return vm.run(commands)


def encode(commandList: list[Command]):
    outList = []
    for i in range(len(commandList)):
        outList.append(commandList[i].toCode())
    return "\n".join(outList)


def compile(expr: str):
    return compiler.compiler(expr)


def compile_as_file(expr, filename: str):
    commands = compiler.compiler(expr)
    with open(filename, "w") as f:
        for command in commands:
            f.write(command.toCode() + "\n")


def run(cmds: list):
    return vm.run(cmds)


def timer(func, *args, times=10000):
    t0 = time.time()
    for i in range(times):
        func(*args)
    print(func(*args))
    return (time.time() - t0) / times * 1000


def decode(code: str):
    return decoder.decode(code)


if __name__ == '__main__':
    code = encode(compile("(-b+sqrt(b*b-4*a*c))/2a"))
    print(code)