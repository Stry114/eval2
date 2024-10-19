#pragma once
#include <math.h>
#include <vector>
#include <string>
#include "Runtime.h"
#include <unordered_map>

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

	static std::unordered_map<ItemType, std::string> ItemTypeName = {
		{SYM, "SYM"},
		{CST, "CST"},
		{VAR, "VAR"},
		{EXP, "EXP"},
		{FNC, "FNC"},
	};

	static class Item
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

	static class Const : public Item
	{
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

	static class Symbol : public Item
	{
		Symbol(std::string string)
		{
			this->type = SYM;
			this->string = string;
			this->name = string;
		}

		Symbol(char string)
		{
			this->type = SYM;
			this->string = std::to_string(string);
			this->name = this->string;
		}
	};

	static class Variable : public Item
	{
		Variable(std::string string)
		{
			this->type = VAR;
			this->string = string;
			this->name = string;

			for (int i = 0; i < FunctionList->length(); i++)
			{
				if (FunctionList[i] == string)
					type = FNC;
			}
		}
	};

	static class Expression : public Item
	{
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

	bool mandatoryConstants = false;
	int maxRegisterNumber = 0;
	int autoInitRegister = 16;
	std::vector<Command> commandList = {};
	std::vector<bool> registerList = {};

	int get0FreeRegister()
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
		maxRegisterNumber = max<int>(maxRegisterNumber, registerList.size());
		return registerList.size() - 1;
	}

	void freeRegister(int index)
	{
		registerList[index] = false;
	}

	void o(Command cmd)
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
					Item item = Compiler::Expression(expr.substr(braStartPoint + 1, i));
				}
			}
		}
	}
};

std::string Compiler::FunctionList[6] = {
	"sin", "cos", "tan",
	"log", "ln", "sqrt"};
char Compiler::symbolList[6] = "+-*/^";
char Compiler::numberList[12] = "0123456789.";
char Compiler::alphabeta[53] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
