from Peval.runtime import VM
from Peval.compiler import Compiler
from Peval.Peval2 import encode
import math
import timeit

def m1():
    vm = VM()
    compiler = Compiler()

    sum_val = 0
    codes = compiler.compiler("sqrt((x+1)(x+2))")
    print(encode(codes))
    for i in range(1, 10001):
        vm.setVar("x", i)
        sum_val += vm.run(codes)
    print(sum_val)

def m2():
    sum_val = 0
    for x in range(1, 10001):
        sum_val += eval("math.sqrt((x+1)*(x+2))")
    print(sum_val)


def m3():
    sum_val = 0
    for x in range(1, 10001):
        sum_val += math.sqrt((x+1)*(x+2))
    print(sum_val)

t1 = timeit.timeit(m1, number=1)
t2 = timeit.timeit(m2, number=1)
t3 = timeit.timeit(m3, number=1)
print(f"{t1=}\n{t2=}\n{t3=}")

