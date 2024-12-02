#pragma once
#include <iostream>
#include <math.h>
#include <vector>
#include <string>
#include "Runtime.h"
#include <unordered_map>

// template <typename T>
// T max(T num1, T num2)
// {
// 	if (num1 > num2)
// 		return num1;
// 	else
// 		return num2;
// }

class Compiler
{
public:
	enum ItemType
	{
		SYM,
		CST,
		VAR,
		EXP,
		FNC
	};

	class Item
	{
	public:
		std::string name;
		std::string string;
		ItemType type;
		double value;
		int _register = -1;

		std::string toString()
		{
			if (type == CST)
			{
				return "[CST]" + name + "<" + std::to_string(value) + ">";
			}
			return "[" + ItemTypeName[type] + "]" + name;
		}
	};

	class Const : public Item
	{
	public:
		Const(std::string string)
		{
			this->type = CST;
			this->value = std::stod(string);
			this->string = string;
			this->name = string;
		}

		Const(double value)
		{
			this->type = CST;
			this->value = value;
			this->string = std::to_string(value);
			this->name = this->string;
		}
	};

	class Symbol : public Item
	{
	public:
		Symbol(std::string string)
		{
			this->type = SYM;
			this->string = string;
			this->name = string;
		}

		Symbol(char string)
		{
			this->type = SYM;
			this->string = string;
			this->name = this->string;
		}
	};

	class Variable : public Item
	{
	public:
		Variable(std::string string)
		{
			this->type = VAR;
			this->string = string;
			this->name = string;

			for (int i = 0; i < 6; i++)
			{
				if (FunctionList[i] == string)
					type = FNC;
			}
		}
	};

	class Expression : public Item
	{
	public:
		Expression(std::string string)
		{
			this->type = EXP;
			this->string = string;
			this->name = string;
		}
	};

	static char symbolList[6];
	static char numberList[12];
	static char alphabeta[53];
	static const int symbolCount = 5;
	static const int numberCount = 11;
	static const int alphabetaCount = 52;
	static std::string FunctionList[6];
	static std::unordered_map<ItemType, std::string> ItemTypeName;

	bool mandatoryConstants = false;
	int maxRegisterNumber = 0;
	int autoInitRegister = 16;
	std::vector<Command *> commandList = {};
	std::vector<bool> registerList = {};

	int getFreeRegister()
	{
		for (int i = 0; i < registerList.size(); i++)
		{
			if (!registerList[i])
			{
				registerList[i] = true;
				return i;
			}
		}
		registerList.push_back(true);
		maxRegisterNumber = std::max<int>(maxRegisterNumber, registerList.size());
		return registerList.size() - 1;
	}

	void freeRegister(int index)
	{
		registerList[index] = false;
	}

	void o(Command *cmd)
	{
		commandList.push_back(cmd);
	}

	static bool isNumber(char value)
	{
		for (int i = 0; i < numberCount; i++)
		{
			if (numberList[i] == value)
				return true;
		}
		return false;
	}

	static bool isAlphabet(char value)
	{
		for (int i = 0; i < alphabetaCount; i++)
		{
			if (alphabeta[i] == value)
				return true;
		}
		return false;
	}

	static bool isSymbol(char value)
	{
		for (int i = 0; i < symbolCount; i++)
		{
			if (symbolList[i] == value)
				return true;
		}
		return false;
	}

	static void printItemList(std::vector<Item> ItemList)
	{
		std::cout << "{";
		for (int i = 0; i < ItemList.size(); i++)
		{
			std::cout << ItemList[i].toString() << ", ";
		}
		std::cout << "}" << std::endl;
	}

	static double ln(double x)
	{
		return log10(x) / log10(M_E);
	}

	static std::vector<Item> split(std::string expr)
	{
		int startPoint = 0;
		int braStartPoint = 0;
		int inBracket = 0;
		bool inNumber = false;
		bool inAlpha = false;
		std::vector<Item> results = {};
		char key;
		int i = 0;

		expr = expr + " ";
		if (expr[0] == '-')
			expr = '0' + expr;

		while (i < expr.size())
		{
			key = expr[i];
			if (key == ')')
			{
				inBracket--;
				if (inBracket == 0)
				{
					Item item = Expression(expr.substr(braStartPoint + 1, i - braStartPoint - 1));
					results.push_back(item);
				}
			}
			else if (key == '(')
			{
				inBracket++;
				if (inBracket == 1)
					braStartPoint = i;
			}
			else if (inBracket != 0)
			{
				i++;
				continue;
			}

			if (inNumber && !isNumber(key))
			{
				inNumber = false;
				Const item = Const(expr.substr(startPoint, i - startPoint));
				results.push_back(item);
			}
			else if (!inNumber && !inAlpha && isNumber(key))
			{
				inNumber = true;
				startPoint = i;
			}
			if (inAlpha && !(isAlphabet(key) || isNumber(key)))
			{
				inAlpha = false;
				Item item = Variable(expr.substr(startPoint, i - startPoint));
				results.push_back(item);
			}
			else if (!inAlpha && isAlphabet(key))
			{
				inAlpha = true;
				startPoint = i;
			}
			if (isSymbol(key))
			{
				Item item = Symbol(key);
				results.push_back(item);
			}
			i++;
		}
		return results;
	}

	static std::vector<Item> addMulSymbol(std::vector<Item> itemList)
	{
		int i = 0;
		Item thisObj;
		Item lastObj;
		while (i < itemList.size() - 1)
		{
			i++;
			thisObj = itemList[i];
			lastObj = itemList[i - 1];
			if (thisObj.type == VAR || thisObj.type == EXP || thisObj.type == FNC)
			{
				if (lastObj.type == VAR || lastObj.type == CST || lastObj.type == EXP)
				{
					itemList.insert(itemList.begin() + i, Symbol("*"));
					i++;
				}
			}
		}
		return itemList;
	}

private:
	std::vector<Item> calculate(std::vector<Item> itemList)
	{
		// process all the vars
		for (int i = 0; i < itemList.size(); i++)
		{
			Item item = itemList[i];
			if (item.type == VAR)
			{
				int _register = getFreeRegister();
				itemList[i]._register = _register;
				o(new InitVar(item.name, _register));
			}
		}

		int i = 0;
		while (i < itemList.size() - 1)
		{
			Item item = itemList[i];
			Item next = itemList[i + 1];
			if (item.type == FNC && next.type == CST)
			{
				Item itemfill;
				if (item.name == "sin")
				{
					itemfill = Const(sin(next.value));
				}
				else if (item.name == "cos")
				{
					itemfill = Const(cos(next.value));
				}
				else if (item.name == "tan")
				{
					itemfill = Const(tan(next.value));
				}
				else if (item.name == "log")
				{
					itemfill = Const(log(next.value));
				}
				else if (item.name == "ln")
				{
					itemfill = Const(ln(next.value));
				}
				else if (item.name == "sqrt")
				{
					itemfill = Const(sqrt(next.value));
				}
				else
				{
					throw "Unknown function name: " + item.name;
				}

				itemList[i] = itemfill;
				itemList.erase(itemList.begin() + i + 1);
				i++;
			}
			else if (item.type == FNC)
			{
				if (item.name == "sin")
				{
					o(new Sin(next._register, next._register));
				}
				else if (item.name == "cos")
				{
					o(new Cos(next._register, next._register));
				}
				else if (item.name == "tan")
				{
					o(new Tan(next._register, next._register));
				}
				else if (item.name == "log")
				{
					o(new Log(next._register, next._register));
				}
				else if (item.name == "ln")
				{
					o(new Ln(next._register, next._register));
				}
				else if (item.name == "sqrt")
				{
					o(new Sqrt(next._register, next._register));
				}
				else
				{
					throw "Unknown function name: " + item.name;
				}

				Item itemfill = Variable("temp");
				itemfill._register = next._register;
				itemList[i] = itemfill;
				itemList.erase(itemList.begin() + i + 1);
				i++;
			}
			i++;
		}

		i = 0;
		while (i < itemList.size())
		{
			Item item = itemList[i];
			int symbolType;
			if (item.type == SYM)
			{
				if (i == 0 || i == itemList.size() - 1)
					throw "Binocular operators cannot be at the beginning/end";
				if (item.name == "*")
				{
					symbolType = 1;
				}
				else if (item.name == "/")
				{
					symbolType = 2;
				}
				else
				{
					i++;
					continue;
				}

				Item lastItem = itemList[i - 1];
				Item nextItem = itemList[i + 1];
				Item *itemfill = nullptr;

				if (lastItem._register == -1 && nextItem._register == -1)
				{
					printf("2 const");
					if (symbolType == 1)
					{
						itemfill = new Const(lastItem.value * nextItem.value);
					}
					else
					{
						itemfill = new Const(lastItem.value / nextItem.value);
					}
				}
				else if (lastItem._register != -1 && nextItem._register == -1)
				{
					int ro = lastItem._register;
					if (symbolType == 1)
					{
						o(new Time(nextItem.value, ro));
					}
					else
					{
						o(new Time(1 / nextItem.value, ro));
					}
					itemfill = new Variable("temp");
					itemfill->_register = ro;
				}
				else if (lastItem._register == -1 && nextItem._register != -1)
				{
					int ro = nextItem._register;
					if (symbolType == 1)
					{
						o(new Time(lastItem.value, ro));
					}
					else
					{
						o(new Time(1 / lastItem.value, ro));
					}
					itemfill = new Variable("temp");
					itemfill->_register = ro;
				}
				else
				{
					int r1 = lastItem._register;
					int r2 = nextItem._register;
					if (symbolType == 1)
						o(new Mul(r1, r2, r2));
					else
						o(new Div(r1, r2, r2));
					freeRegister(r1);
					itemfill = new Variable("temp");
					itemfill->_register = r2;
				}
				itemList[i] = *itemfill;
				itemList.erase(itemList.begin() + i - 1);
				itemList.erase(itemList.begin() + i);
			}
			else
			{
				i++;
			}
		}

		i = 0;
		while (i < itemList.size())
		{
			Item item = itemList[i];
			int symbolType;
			if (item.type == SYM)
			{
				if (i == 0 || i == itemList.size() - 1)
					throw "Binocular operators cannot be at the beginning/end";
				if (item.name == "+")
				{
					symbolType = 1;
				}
				else if (item.name == "-")
				{
					symbolType = 2;
				}
				else
				{
					i++;
					continue;
				}

				Item lastItem = itemList[i - 1];
				Item nextItem = itemList[i + 1];
				Item *itemfill = nullptr;
				if (lastItem._register == -1 && nextItem._register == -1)
				{
					if (symbolType == 1)
					{
						itemfill = new Const(lastItem.value + nextItem.value);
					}
					else
					{
						itemfill = new Const(lastItem.value - nextItem.value);
					}
				}
				else if (lastItem._register != -1 && nextItem._register == -1)
				{
					int ro = lastItem._register;
					if (symbolType == 1)
					{
						o(new Accumulate(nextItem.value, ro));
					}
					else
					{
						o(new Accumulate(0 - nextItem.value, ro));
					}
					itemfill = new Variable("temp");
					itemfill->_register = ro;
				}
				else if (lastItem._register == -1 && nextItem._register != -1)
				{
					int ro = nextItem._register;
					if (symbolType == 1)
					{
						o(new Accumulate(lastItem.value, ro));
					}
					else
					{
						o(new Accumulate(0 - lastItem.value, ro));
					}
					itemfill = new Variable("temp");
					itemfill->_register = ro;
				}
				else
				{
					int r1 = lastItem._register;
					int r2 = nextItem._register;
					if (symbolType == 1)
					{
						o(new Add(r1, r2, r2));
					}
					else
					{
						o(new Min(r1, r2, r2));
					}
					freeRegister(r1);
					itemfill = new Variable("temp");
					itemfill->_register = r2;
				}
				itemList[i] = *itemfill;
				itemList.erase(itemList.begin() + i - 1);
				itemList.erase(itemList.begin() + i);
			}
			else
			{
				i++;
			}
		}
		return itemList;
	}

	Item autoCompileLauncher(std::string expr)
	{
		// std::cout << "autoCompileLauncher: " << expr << std::endl;

		std::vector<Item> itemList = split(expr);
		// printItemList(itemList);
		itemList = addMulSymbol(itemList);
		// printItemList(itemList);

		for (int i = 0; i < itemList.size(); i++)
		{
			Item item = itemList[i];
			if (item.type == EXP)
			{
				Item j = autoCompileLauncher(item.string);
				if (j.type == CST)
					itemList[i] = Const(j.value);
				else
				{
					itemList[i]._register = j._register;
					// std::cout << "return register: " << item.toString() << "   " << item._register << std::endl;
				}
			}
		}

		// printItemList(itemList);
		itemList = calculate(itemList);
		// printItemList(itemList);
		if (!(itemList.size() == 1))
			throw "Two separate expressions were entered.";
		else
			return itemList[0];
	}

public:
	std::vector<Command *> compiler(std::string expr)
	{
		this->commandList = std::vector<Command *>();
		this->registerList = std::vector<bool>();
		maxRegisterNumber = 0;

		Item i = autoCompileLauncher(expr);
		if (i.type == CST)
			o(new ReturnNumber(i.value));
		else
			o(new Return(i._register));

		if (maxRegisterNumber > autoInitRegister)
		{
			Command *command = new InitRegister(maxRegisterNumber);
			commandList.insert(commandList.begin(), command);
		}

		return commandList;
	}
};

std::string Compiler::FunctionList[6] = {
	"sin", "cos", "tan",
	"log", "ln", "sqrt"};
std::unordered_map<Compiler::ItemType, std::string> Compiler::ItemTypeName = {
	{SYM, "SYM"},
	{CST, "CST"},
	{VAR, "VAR"},
	{EXP, "EXP"},
	{FNC, "FNC"},
};
char Compiler::symbolList[6] = "+-*/^";
char Compiler::numberList[12] = "0123456789.";
char Compiler::alphabeta[53] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
