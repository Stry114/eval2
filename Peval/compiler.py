from enum import Enum
from Peval.runtime import *


class Compiler:

    class ItemType(Enum):
        SYM = "sym"
        CST = "cst"
        VAR = "var"
        EXP = "exp"
        FNC = "fnc"

    class Item:
        register = None
        string = None
        name = None
        type = None

        def __repr__(self):
            return f"[{self.type.name}]{self.name}"

    class Const(Item):
        def __init__(self, string):
            self.type = Compiler.ItemType.CST
            self.value = float(string)
            self.string = string
            self.name = string

    class Symbol(Item):
        def __init__(self, string):
            self.type = Compiler.ItemType.SYM
            self.string = string
            self.name = string

    class Variable(Item):
        def __init__(self, string):
            self.type = Compiler.ItemType.VAR
            self.string = string
            self.name = string

            for i in Compiler.functionList:
                if i == self.string:
                    self.type = Compiler.ItemType.FNC

    class Expression(Item):
        def __init__(self, string):
            self.type = Compiler.ItemType.EXP
            self.string = string
            self.name = string

    symbolList = list("+-*/^")
    numberList = list("0123456789.")
    alphabeta = list("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
    functionList = [
        "sin", "cos", "tan",
        "log", "ln", "sqrt"
    ]

    commandList = []
    mandatoryConstants = False
    maxRegisterNumber = 0
    autoInitRegister = 16
    registerList = [False for i in range(autoInitRegister)]

    def getFreeRegister(self):
        for i in range(len(self.registerList)):
            if not self.registerList[i]:
                self.registerList[i] = True
                return i
        self.registerList.append(True)
        self.maxRegisterNumber = max(self.maxRegisterNumber, len(self.registerList))
        return len(self.registerList) - 1

    def freeRegister(self, index:int):
        self.registerList[index] = False

    def o(self, command):
        self.commandList.append(command)

    def isNumber(self, value:str):
        return value in self.numberList

    def isAlphabet(self, value:str):
        return value in self.alphabeta

    def isSymbol(self, value:str):
        return value in self.symbolList

    def split(self, expr:str):

        startPoint = None
        braStartPoint = None
        inBracket = 0
        inNumber = False
        inAlpha = False
        results = []
        i = 0

        # process negative symbol
        # process end of expr
        expr = expr + " "
        if expr[0] == "-":
            expr = "0"+expr

        while i < len(expr):
            key = expr[i]
            if key==")":
                inBracket -= 1
                if inBracket == 0:
                    item = Compiler.Expression(expr[braStartPoint+1:i])
                    results.append(item)
            elif key=="(":
                inBracket += 1
                if inBracket == 1:
                    braStartPoint = i
            elif inBracket != 0:
                i = i+1
                continue

            if inNumber and not self.isNumber(key):
                inNumber = False
                item = Compiler.Const(expr[startPoint:i])
                results.append(item)
            elif not inNumber and not inAlpha and self.isNumber(key):
                inNumber = True
                startPoint = i
            if inAlpha and not (self.isAlphabet(key) or self.isNumber(key)):
                inAlpha = False
                item = Compiler.Variable(expr[startPoint:i])
                results.append(item)
            elif not inAlpha and self.isAlphabet(key):
                inAlpha = True
                startPoint = i
            if self.isSymbol(key):
                item = Compiler.Symbol(key)
                results.append(item)

            i = i+1
        return results

    def addMulSymbol(self, itemList:list):
        i = 0
        while i < len(itemList)-1:
            i += 1
            thisObj = itemList[i]
            lastObj = itemList[i-1]
            if thisObj.type == Compiler.ItemType.VAR or thisObj.type == Compiler.ItemType.FNC or thisObj.type == Compiler.ItemType.EXP:
                if lastObj.type == Compiler.ItemType.VAR or lastObj.type == Compiler.ItemType.CST or lastObj.type == Compiler.ItemType.EXP:
                    itemList.insert(i, Compiler.Symbol("*"))
                    i += 1
        return itemList

    def calculate(self, itemList:list):
        # process all the vars
        varList = []
        regList = []
        for i in range(len(itemList)):
            item = itemList[i]
            if item.type == Compiler.ItemType.VAR:
                register = self.getFreeRegister()
                item.register = register
                self.o(InitVar(item.name, register))

        i = 0
        while i < len(itemList)-1:
            item = itemList[i]
            nextItem = itemList[i+1]
            if not item.type == Compiler.ItemType.FNC:
                i += 1
                continue

            if item.name == "sin":
                commandClass = Sin
            elif item.name == "cos":
                commandClass = Cos
            elif item.name == "tan":
                commandClass = Tan
            elif item.name == "log":
                commandClass = Log
            elif item.name == "ln":
                commandClass = Ln
            elif item.name == "sqrt":
                commandClass = Sqrt
            else:
                raise NameError("Unsupported function:", item.name)

            if nextItem.register is None:
                value = commandClass(-1, -1).func(nextItem.value)
                item = Compiler.Const(str(value))
            else:
                item = Compiler.Variable("temp")
                item.register = nextItem.register
                self.o(commandClass(nextItem.register, nextItem.register))
            itemList[i] = item
            itemList.pop(i+1)
            i += 1

        i = 0
        while i < len(itemList):
            item = itemList[i]
            if item.type == Compiler.ItemType.SYM:
                if i == 0 or i == len(itemList)-1:
                    raise Exception("Binocular operators cannot be at the beginning/end")
                if item.name == "*":
                    symbolType = 1
                elif item.name == "/":
                    symbolType = 2
                else:
                    i += 1
                    continue

                last_item = itemList[i - 1]
                next_item = itemList[i + 1]
                if last_item.register is None and next_item.register is None:
                    if symbolType == 1:
                        item = Compiler.Const(last_item.value * next_item.value)
                    else:
                        item = Compiler.Const(last_item.value / next_item.value)
                elif last_item.register is not None and next_item.register is None and self.mandatoryConstants:
                    r1 = self.getFreeRegister()
                    r2 = last_item.register
                    self.o(Const(next_item.value, r1))
                    if symbolType == 1:
                        self.o(Mul(r1, r2, r2))
                    else:
                        self.o(Div(r1, r2, r2))
                    self.freeRegister(r1)
                    item = Compiler.Variable("temp")
                    item.register = r2
                elif last_item.register is not None and next_item.register is None:
                    ro = last_item.register
                    if symbolType == 1:
                        self.o(Time(next_item.value, ro))
                    else:
                        self.o(Time(1/next_item.value, ro))
                    item = Compiler.Variable("temp")
                    item.register = ro
                elif last_item.register is None and next_item.register is not None and self.mandatoryConstants:
                    r1 = self.getFreeRegister()
                    r2 = next_item.register
                    self.o(Const(last_item.value, r1))
                    if symbolType == 1:
                        self.o(Mul(r1, r2, r2))
                    else:
                        self.o(Div(r1, r2, r2))
                    self.freeRegister(r1)
                    item = Compiler.Variable("temp")
                    item.register = r2
                elif last_item.register is None and next_item.register is not None:
                    ro = next_item.register
                    if symbolType == 1:
                        self.o(Time(last_item.value, ro))
                    else:
                        self.o(Time(1/last_item.value, ro))
                    item = Compiler.Variable("temp")
                    item.register = ro
                else:
                    r1 = last_item.register
                    r2 = next_item.register
                    if symbolType == 1:
                        self.o(Mul(r1, r2, r2))
                    else:
                        self.o(Div(r1, r2, r2))
                    self.freeRegister(r1)
                    item = Compiler.Variable("temp")
                    item.register = r2
                itemList[i] = item
                itemList.pop(i-1)
                itemList.pop(i)
            else:
                i = i+1



        i = 0
        while i < len(itemList):
            item = itemList[i]
            if item.type == Compiler.ItemType.SYM:
                if i == 0 or i == len(itemList)-1:
                    raise Exception("Binocular operators cannot be at the beginning/end")
                if item.name == "+":
                    symbolType = 1
                elif item.name == "-":
                    symbolType = 2
                else:
                    i += 1
                    continue

                last_item = itemList[i - 1]
                next_item = itemList[i + 1]
                if last_item.register is None and next_item.register is None:
                    if symbolType == 1:
                        item = Compiler.Const(last_item.value + next_item.value)
                    else:
                        item = Compiler.Const(last_item.value - next_item.value)
                elif last_item.register is not None and next_item.register is None and self.mandatoryConstants:
                    r1 = self.getFreeRegister()
                    r2 = last_item.register
                    self.o(Const(next_item.value, r1))
                    if symbolType == 1:
                        self.o(Add(r1, r2, r2))
                    else:
                        self.o(Min(r1, r2, r2))
                    self.freeRegister(r1)
                    item = Compiler.Variable("temp")
                    item.register = r2
                elif last_item.register is not None and next_item.register is None:
                    ro = last_item.register
                    if symbolType == 1:
                        self.o(Accumulate(next_item.value, ro))
                    else:
                        self.o(Accumulate(0-next_item.value, ro))
                    item = Compiler.Variable("temp")
                    item.register = ro
                elif last_item.register is None and next_item.register is not None and self.mandatoryConstants:
                    r1 = self.getFreeRegister()
                    r2 = next_item.register
                    self.o(Const(last_item.value, r1))
                    if symbolType == 1:
                        self.o(Add(r1, r2, r2))
                    else:
                        self.o(Min(r1, r2, r2))
                    self.freeRegister(r1)
                    item = Compiler.Variable("temp")
                    item.register = r2
                elif last_item.register is None and next_item.register is not None:
                    ro = next_item.register
                    if symbolType == 1:
                        self.o(Accumulate(last_item.value, ro))
                    else:
                        self.o(Accumulate(0-last_item.value, ro))
                    item = Compiler.Variable("temp")
                    item.register = ro
                else:
                    r1 = last_item.register
                    r2 = next_item.register
                    if symbolType == 1:
                        self.o(Add(r1, r2, r2))
                    else:
                        self.o(Min(r1, r2, r2))
                    self.freeRegister(r1)
                    item = Compiler.Variable("temp")
                    item.register = r2
                itemList[i] = item
                itemList.pop(i-1)
                itemList.pop(i)
            else:
                i = i+1

        return itemList

    def _compile(self, expr:str):
        itemList = self.split(expr)
        itemList = self.addMulSymbol(itemList)

        for i in range(len(itemList)):
            item = itemList[i]
            if item.type == Compiler.ItemType.EXP:
                j = self._compile(item.string)
                if j[0] == Compiler.ItemType.EXP:
                    item.register = j[1]
                else:
                    itemList[i] = Compiler.Const(str(j[1]))

        itemList = self.calculate(itemList)

        if not len(itemList) == 1:
            raise Exception("Two separate expressions were entered.")
        if itemList[0].type == Compiler.ItemType.CST:
            return (Compiler.ItemType.CST, itemList[0].value)
        else:
            return (Compiler.ItemType.EXP, itemList[0].register)


    def compiler(self, expr:str):
        self.commandList = []
        self.maxRegisterNumber = 0
        self.registerList = [False for i in range(self.autoInitRegister)]

        i = self._compile(expr)
        if i[0] == Compiler.ItemType.EXP:
            self.o(Return(i[1]))
        else:
            self.o(ReturnNumber(i[1]))

        if self.maxRegisterNumber > self.autoInitRegister:
            i = InitRegister(self.maxRegisterNumber)
            self.commandList.insert(0, i)

        return self.commandList

