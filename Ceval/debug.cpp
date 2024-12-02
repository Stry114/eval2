#include "Ceval2.h"
#include <iostream>
#include <chrono>

using namespace std;
using namespace std::chrono;

int main()
{
    VM vm = VM();
    Compiler compiler = Compiler();

    steady_clock::time_point start = steady_clock::now();

    double sum_val = 0;
    std::vector<Command *> codes = compiler.compiler("sqrt((x+1)(x+2))");

    for (double i = 1; i < 10001; i++)
    {
        vm.varDic["x"] = i;
        sum_val += vm.run(codes);
    }

    printf("%f", sum_val);

    steady_clock::time_point end = steady_clock::now();
    duration<double> time_span = duration_cast<duration<double>>(end - start);
    cout << "函数add执行用时： " << time_span.count() << " 秒" << endl;

    return 0;
}