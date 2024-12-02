import enum
import math
import random


class VM:
    def __init__(self):
        self.VarDic = {
            "A": 0,
            "B": 1,
            "C": 2,
            "D": 3,
            "E": 4,
            "F": 5,
            "M": 6,
            "X": 0,
            "Y": 1,
            "ans": 0,
            "e": math.e,
            "pi": math.pi,
        }
        self.registers = [-1 for i in range(16)]
        self.result = None
        self.version = "Peval2-VM P1"
        self.verCode = 1
        self.run = self.run_basic

    def activeDebugMode(self, versionCode:int):
        if versionCode == 0:
            self.run = self.run_basic
        if versionCode == 240831:
            self.run = self.run_debug_240831

    def setVar(self, varName: str, value: float):
        self.VarDic[varName] = value

    def run_basic(self, CommandList: list):
        self.result = None
        for cmd in CommandList:
            cmd.run(self)
        return self.result
    
    def run_debug_240831(self, CommandList: list):
        self.result = None
        i = 0
        while i < len(CommandList):
            CommandList[i].run(self)
            i += 1


class Command:
    class Type(enum.Enum):
        INIT_VAR = 0
        CONST = 1
        ADD = 2
        MIN = 3
        MUL = 4
        DIV = 5
        RETURN = 6
        ACCUMULATE = 7
        INIT_REGISTER = 8
        RETURN_NUMBER = 9

        SIN = 9
        COS = 10
        TAN = 11
        LOG = 12
        LN = 13
        SQRT = 14
        TIME = 15

        # POWER = 16
        TO_INT = 17
        ABS = 18


class Sin(Command):
    def __init__(self, r1:int, ro:int):
        self.r1 = r1
        self.ro = ro
        self.func = math.sin

    def run(self, vm: VM):
        vm.registers[self.ro] = math.sin(vm.registers[self.r1])

    def toCode(self):
        return f".sin r{self.r1} -> r{self.ro}"


class Cos(Command):
    def __init__(self, r1:int, ro:int):
        self.r1 = r1
        self.ro = ro
        self.func = math.cos

    def run(self, vm: VM):
        vm.registers[self.ro] = math.cos(vm.registers[self.r1])

    def toCode(self):
        return f".cos r{self.r1} -> r{self.ro}"


class Tan(Command):
    def __init__(self, r1:int, ro:int):
        self.r1 = r1
        self.ro = ro
        self.func = math.tan

    def run(self, vm: VM):
        vm.registers[self.ro] = math.tan(vm.registers[self.r1])

    def toCode(self):
        return f".tan r{self.r1} -> r{self.ro}"


class Log(Command):
    def __init__(self, r1:int, ro:int):
        self.r1 = r1
        self.ro = ro
        self.func = math.log10

    def run(self, vm: VM):
        vm.registers[self.ro] = math.log10(vm.registers[self.r1])

    def toCode(self):
        return f".log r{self.r1} -> r{self.ro}"


class Ln(Command):
    def __init__(self, r1:int, ro:int):
        self.r1 = r1
        self.ro = ro
        self.func = lambda x: math.log(x, math.e)

    def run(self, vm: VM):
        vm.registers[self.ro] = math.log(vm.registers[self.r1], math.e)

    def toCode(self):
        return f".ln r{self.r1} -> r{self.ro}"


class Sqrt(Command):
    def __init__(self, r1:int, ro:int):
        self.r1 = r1
        self.ro = ro
        self.func = math.sqrt

    def run(self, vm: VM):
        vm.registers[self.ro] = math.sqrt(vm.registers[self.r1])

    def toCode(self):
        return f".sqrt r{self.r1} -> r{self.ro}"
    
class ToInt(Command):
    def __init__(self, r1:int, ro:int):
        self.r1 = r1
        self.ro = ro
        self.func = math.floor
    
    def run(self, vm:VM):
        vm.registers[self.ro] = math.floor(vm.registers[self.r1])

    def toCode(self):
        return f".toInt r{self.r1} -> r{self.ro}"
    
class Abs(Command):
    def __init__(self, r1:int, r2:int):
        self.r1 = r1
        self.r2 = r2
        self.func = abs

    def run(self, vm:VM):
        vm.registers[self.ro] = abs(vm.registers[self.r1])

    def toCode(self):
        return f".abs r{self.r1} -> {self.ro}"

class InitRegister(Command):
    def __init__(self, number:int):
        self.type = Command.Type.INIT_REGISTER
        self.number = number

    def toCode(self):
        return f".initReg {self.number}"

    def run(self, vm:VM):
        while len(vm.registers) < self.number:
            vm.registers.append(0)


class ReturnNumber(Command):
    def __init__(self, Number:float):
        self.type = Command.Type.RETURN_NUMBER
        self.number = Number

    def toCode(self):
        return f".retNum {self.number}"

    def run(self, vm:VM):
        vm.result = self.number


class Accumulate(Command):
    def __init__(self, number:float, ro):
        self.type = Command.Type.ACCUMULATE
        self.number = number
        self.ro = ro

    def toCode(self):
        return f".acc {self.number} -> r{self.ro}"

    def run(self, vm:VM):
        vm.registers[self.ro] += self.number


class Time(Command):
    def __init__(self, number:float, ro):
        self.type = Command.Type.ACCUMULATE
        self.number = number
        self.ro = ro

    def toCode(self):
        return f".time {self.number} -> r{self.ro}"

    def run(self, vm:VM):
        vm.registers[self.ro] *= self.number


class Add(Command):
    def __init__(self, r1: int, r2: int, ro: int):
        self.type = Command.Type.ADD
        self.r1 = r1
        self.r2 = r2
        self.ro = ro

    def toCode(self):
        return f".add r{self.r1} r{self.r2} -> r{self.ro}"

    def run(self, vm:VM):
        vm.registers[self.ro] = vm.registers[self.r1] + vm.registers[self.r2]


class Min(Command):
    def __init__(self, r1: int, r2: int, ro: int):
        self.type = Command.Type.MIN
        self.r1 = r1
        self.r2 = r2
        self.ro = ro

    def toCode(self):
        return f".min r{self.r1} r{self.r2} -> r{self.ro}"

    def run(self, vm:VM):
        vm.registers[self.ro] = vm.registers[self.r1] - vm.registers[self.r2]


class Mul(Command):
    def __init__(self, r1: int, r2: int, ro: int):
        self.type = Command.Type.MUL
        self.r1 = r1
        self.r2 = r2
        self.ro = ro

    def toCode(self):
        return f".mul r{self.r1} r{self.r2} -> r{self.ro}"

    def run(self, vm:VM):
        vm.registers[self.ro] = vm.registers[self.r1] * vm.registers[self.r2]


class Div(Command):
    def __init__(self, r1: int, r2: int, ro: int):
        self.type = Command.Type.DIV
        self.r1 = r1
        self.r2 = r2
        self.ro = ro

    def toCode(self):
        return f".div r{self.r1} r{self.r2} -> r{self.ro}"

    def run(self, vm:VM):
        vm.registers[self.ro] = vm.registers[self.r1] / vm.registers[self.r2]


class Return(Command):
    def __init__(self, r0: int):
        self.type = Command.Type.RETURN
        self.r0 = r0

    def toCode(self):
        return f".return r{self.r0}"

    def run(self, vm:VM):
        vm.result = vm.registers[self.r0]


class Const(Command):
    def __init__(self, value:float, ro: int):
        self.type = Command.Type.CONST
        self.value = value
        self.ro = ro

    def toCode(self):
        return f".const {self.value} -> r{self.ro}"

    def run(self, vm:VM):
        vm.registers[self.ro] = self.value


class InitVar(Command):
    def __init__(self, varName:str, ro: int):
        self.type = Command.Type.INIT_VAR
        self.var = varName
        self.ro = ro

    def toCode(self):
        return f".initVar {self.var} -> r{self.ro}"

    def run(self, vm:VM):
        vm.registers[self.ro] = vm.VarDic[self.var]


if __name__ == "__main__":
    vm = VM()
    codes = [
        InitVar("e", 0),
        Accumulate(5, 0),
        Return(0)
    ]
    
    import time
    
    timer = time.time()
    for i in range(10000000):
        vm.run(codes)
    timer = time.time() - timer
    print("time=", timer*1000, "ms")

    vm.activeDebugMode(240831)
    timer = time.time()
    for i in range(10000000):
        vm.run(codes)
    timer = time.time() - timer
    print("time=", timer*1000, "ms")