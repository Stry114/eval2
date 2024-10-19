from Peval.runtime import *


def rx_to_register(rx: str):
    if not rx.startswith("r"):
        return int(rx)
    else:
        return int(rx[1:])


def decode(code: str):
    code = code.replace("\n", "")
    code = code.split(".")
    code = code[1:]

    results = []
    for line in code:
        if line.replace(" ", "") == "":
            continue

        line = line.replace(" -> ", "->")
        line = line.replace("->", " -> ")
        line = line.replace(" > ", " -> ")

        RO = None
        i = line.split(" ")

        j = 0
        while j < len(i):
            if i[j] == "->":
                RO = rx_to_register(i[j+1])
            if i[j] == "":
                i.pop(j)
            else:
                j += 1

        if RO is None:
            RO = rx_to_register(i[-1])

        if i[0] == "initVar":
            cmd = InitVar(i[1], RO)
        elif i[0] == "const":
            cmd = Const(float(i[1]), RO)
        elif i[0] == "add":
            cmd = Add(rx_to_register(i[1]), rx_to_register(i[2]), RO)
        elif i[0] == "min":
            cmd = Min(rx_to_register(i[1]), rx_to_register(i[2]), RO)
        elif i[0] == "mul":
            cmd = Mul(rx_to_register(i[1]), rx_to_register(i[2]), RO)
        elif i[0] == "div":
            cmd = Div(rx_to_register(i[1]), rx_to_register(i[2]), RO)
        elif i[0] == "return":
            cmd = Return(rx_to_register(i[1]))
        elif i[0] == "acc":
            cmd = Accumulate(float(i[1]), RO)
        elif i[0] == "time":
            cmd = Time(float(i[1]), RO)
        elif i[0] == "initReg":
            cmd = InitRegister(int(i[1]))
        elif i[0] == "retNum":
            cmd = ReturnNumber(float(i[1]))
        elif i[0] == "sin":
            cmd = Sin(rx_to_register(i[1]), RO)
        elif i[0] == "cos":
            cmd = Cos(rx_to_register(i[1]), RO)
        elif i[0] == "tan":
            cmd = Tan(rx_to_register(i[1]), RO)
        elif i[0] == "log":
            cmd = Log(rx_to_register(i[1]), RO)
        elif i[0] == "ln":
            cmd = Ln(rx_to_register(i[1]), RO)
        elif i[0] == "sqrt":
            cmd = Sqrt(rx_to_register(i[1]), RO)
        results.append(cmd)
    return results