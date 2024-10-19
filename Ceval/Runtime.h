#include <iostream>
#include <cmath>
#include <vector>
#include <string>
#include <unordered_map>

#define M_PI 3.14159265358979323846
#define M_E 2.71828182845904523536

class VM;
class Command;

class Command
{
public:
    virtual void run(VM *vm) {}

    virtual std::string toCode()
    {
        return ".pass";
    }
};

class VM
{
public:
    std::unordered_map<std::string, double> varDic;
    std::vector<double> registers;
    double result;

    const char *version = "Ceval-VM P1-debug";
    const int vercode = -1;
    const int CorrespondingPevalVersionCode = -1;

    VM()
    {
        this->varDic = {
            {"A", 0.},
            {"B", 1.},
            {"C", 2.},
            {"D", 3.},
            {"E", 4.},
            {"F", 5.},
            {"M", 6.},
            {"X", 0.},
            {"Y", 1.},
            {"ans", 0.},
            {"e", M_E},
            {"pi", M_PI},
        };
        this->registers =
            {
                0., 0., 0., 0.,
                0., 0., 0., 0.,
                0., 0., 0., 0.,
                0., 0., 0., 0.};
        this->result = 0.;
    }

    void setVar(std::string varName, double value)
    {
        varDic[varName] = value;
    }

    double run(Command *commandList[], int lenth)
    {
        result = 0.;
        for (int i = 0; i < lenth; i++)
        {
            commandList[i]->run(this);
        }
        return result;
    }
};

class Const : public Command
{
private:
    double value;
    int ro;

public:
    Const(double value, int ro)
    {
        this->value = value;
        this->ro = ro;
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = value;
    }

    std::string toCode() override
    {
        return ".const " + std::to_string(value) + " -> " + std::to_string(ro);
    }
};

class Add : public Command
{
private:
    int r1, r2, ro;

public:
    Add(int r1, int r2, int ro)
    {
        this->r1 = r1;
        this->r2 = r2;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".add r" + std::to_string(r1) + " r" + std::to_string(r2) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = vm->registers[r1] + vm->registers[r2];
    }
};

class Min : public Command
{
private:
    int r1, r2, ro;

public:
    Min(int r1, int r2, int ro)
    {
        this->r1 = r1;
        this->r2 = r2;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".min r" + std::to_string(r1) + " r" + std::to_string(r2) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = vm->registers[r1] - vm->registers[r2];
    }
};

class Mul : public Command
{
private:
    int r1, r2, ro;

public:
    Mul(int r1, int r2, int ro)
    {
        this->r1 = r1;
        this->r2 = r2;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".mul r" + std::to_string(r1) + " r" + std::to_string(r2) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = vm->registers[r1] * vm->registers[r2];
    }
};

class Div : public Command
{
private:
    int r1, r2, ro;

public:
    Div(int r1, int r2, int ro)
    {
        this->r1 = r1;
        this->r2 = r2;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".div r" + std::to_string(r1) + " r" + std::to_string(r2) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = vm->registers[r1] / vm->registers[r2];
    }
};

class Accumulate : public Command
{
private:
    double number;
    int ro;

public:
    Accumulate(double number, int ro)
    {
        this->number = number;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".acc " + std::to_string(number) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] += number;
    }
};

class Time : public Command
{
private:
    double number;
    int ro;

public:
    Time(double number, int ro)
    {
        this->number = number;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".time " + std::to_string(number) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] *= number;
    }
};

class Return : public Command
{
private:
    int ro;

public:
    Return(int ro)
    {
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".return r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->result = vm->registers[ro];
    }
};

class ReturnNumber : public Command
{
private:
    double number;

public:
    ReturnNumber(double number)
    {
        this->number = number;
    }

    std::string toCode() override
    {
        return ".retNum " + std::to_string(number);
    }

    void run(VM *vm) override
    {
        vm->result = number;
    }
};

class InitVar : public Command
{

private:
    std::string varName;
    int ro;

public:
    InitVar(std::string varName, int ro)
    {
        this->varName = varName;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".initVar " + varName + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = vm->varDic[varName];
    }
};

class InitRegister : public Command
{
private:
    int number;

public:
    InitRegister(int number)
    {
        this->number = number;
    }

    std::string toCode() override
    {
        return ".initReg " + std::to_string(number);
    }

    void run(VM *vm) override
    {
        if (vm->registers.size() < number)
            vm->registers = std::vector<double>(number);
    }
};

class Sin : public Command
{
private:
    int r1, ro;

public:
    Sin(int r1, int ro)
    {
        this->r1 = r1;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".sin r" + std::to_string(r1) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = sin(vm->registers[r1]);
    }
};

class Cos : public Command
{
private:
    int r1, ro;

public:
    Cos(int r1, int ro)
    {
        this->r1 = r1;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".cos r" + std::to_string(r1) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = cos(vm->registers[r1]);
    }
};

class Tan : public Command
{
private:
    int r1, ro;

public:
    Tan(int r1, int ro)
    {
        this->r1 = r1;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".tan r" + std::to_string(r1) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = tan(vm->registers[r1]);
    }
};

class Log : public Command
{
private:
    int r1, ro;

public:
    Log(int r1, int ro)
    {
        this->r1 = r1;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".log r" + std::to_string(r1) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = log10(vm->registers[r1]);
    }
};

class Ln : public Command
{
private:
    int r1, ro;

public:
    Ln(int r1, int ro)
    {
        this->r1 = r1;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".ln r" + std::to_string(r1) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = log10(vm->registers[r1]) / log10(M_E);
    }
};

class Sqrt : public Command
{
private:
    int r1, ro;

public:
    Sqrt(int r1, int ro)
    {
        this->r1 = r1;
        this->ro = ro;
    }

    std::string toCode() override
    {
        return ".sqrt r" + std::to_string(r1) + " -> r" + std::to_string(ro);
    }

    void run(VM *vm) override
    {
        vm->registers[ro] = sqrt(vm->registers[r1]);
    }
};