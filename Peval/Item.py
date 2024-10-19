import math


version = "Peval v1.6 2024.07.15"
openingParenthesesList = ["(", "["]
closingParenthesesList = [")", "]"]
symbolList = ["^", "*", "/", "+", "-"]
symbolGroupList = [
    ("^",),
    ("*", "/"),
    ("+", "-")
]

functionNameDic = {
    "abs": abs,
    "round": round,
    "sin": math.sin,
    "cos": math.cos,
    "tan": math.tan,
    "log": math.log10,
    "sqrt": math.sqrt,
    "ceil": math.ceil,
    "floor": math.floor,
    "ln": lambda x: math.log(x)/math.log(math.e),
}

localVariableDic = {
    "pi": math.pi,
    "e": math.e,
    "ans": 0,
    "A": 0,
    "B": 1,
    "C": 2,
    "D": 3,
    "E": 4,
    "F": 5,
    "M": 6,
    "X": 0,
    "Y": 1,
}


def isdigit(value):
    try:
        float(value)
        return True
    except ValueError:
        return False
    


class Item:
    type = 0
    
    # new
    tag = None
    '''
    item type_:
    0 ~ unknown
    1 ~ number
    2 ~ symbol
    3 ~ variable 
    4 ~ function name
    '''

    def get_type(self, value: str):
        if isdigit(value):
            self.type = 1
        elif value in symbolList:
            self.type = 2
        elif value.isalnum():
            if value in functionNameDic:
                self.type = 4
            else:
                self.type = 3
        else:
            self.type = 5

    def __init__(self, value, type_=0):
        # pre-process args
        if isinstance(value, int) or isinstance(value, float):
            value = str(value)
        if type_ == 0:
            self.get_type(value)
        else:
            self.type = type_

        self.name = None
        self.value = None
        self.symbol = None
        if self.type == 1:
            self.value = eval(value)
        elif self.type == 5:
            value = peval(value)
            self.value = value
        elif self.type == 3:
            self.value = localVariableDic[value]
            self.name = value
        elif self.type == 4:
            self.value = None
            self.name = value
        else:
            self.value = None
            self.symbol = value

    def __repr__(self):
        
        #new
        if self.tag != None:
            tag = "<"+self.tag+">"
        else:
            tag = ""
        
        if self.type == 1:
            return "[num]" + str(self.value) + tag
        elif self.type == 2:
            return "[sym]" + self.symbol + tag
        elif self.type == 3:
            return "[var]" + str(self.name) + tag
        elif self.type == 4:
            return "[fnc]" + str(self.name) + tag
        elif self.type == 5:
            return "[expr]" + str(self.value) + tag


def peval(expr):
    item_list = []
    start_point = 0
    in_parentheses = 0
    
    # new
    in_tag = False

    # split expression into items
    # process negative number
    if expr[0] == "-":
        expr = "0"+expr


    # new
    i = -1
    while i < len(expr)-1:
        i += 1
        
        if expr[i] == "-" and not in_parentheses and not in_tag:
            last_object = expr[i - 1]
            if last_object in symbolList:
                continue
        if i > 0 and not in_parentheses and not in_tag:
            if ((isdigit(expr[i-1])) or (expr[i-1] in closingParenthesesList)) and ((expr[i].isalpha()) or (expr[i] in openingParenthesesList)):
                if not start_point == i:
                    item_list.append(Item(expr[start_point:i], 1))
                item_list.append(Item("*", 2))
                start_point = i
                
        if expr[i] in symbolList and not in_parentheses and not in_tag:
            if not start_point == i:
                item_list.append(Item(expr[start_point:i]))
            item_list.append(Item(expr[i]))
            start_point = i + 1
        elif expr[i] in openingParenthesesList and not in_parentheses and not in_tag:
            in_parentheses = 1
            if not start_point == i:
                item_list.append(Item(expr[start_point:i], 4))
            start_point = i
        elif expr[i] in openingParenthesesList and not in_tag:
            in_parentheses += 1
        elif expr[i] in closingParenthesesList and in_parentheses == 1 and not in_tag:
            in_parentheses = 0
            item_list.append(Item(expr[start_point + 1:i], 5))
            start_point = i + 1
        elif expr[i] in closingParenthesesList and not in_tag:
            in_parentheses -= 1
            
        #new
        elif expr[i] == "<":
            in_tag = True
            if not start_point == i:
                item_list.append(Item(expr[start_point:i]))
            start_point = i
        elif expr[i] == ">":
            tag = expr[start_point+1: i]
            item_list[-1].tag = tag
            in_tag = False
            expr = expr[:start_point] + expr[i+1:]
            i = i - len(tag) - 2
            start_point = i + 1
            
        elif i == len(expr) - 1:
            if start_point == 0:
                return Item(expr).value
            item_list.append(Item(expr[start_point:]))


    i = 0
    while i < len(item_list):
        if item_list[i].type == 4:
            func = functionNameDic[item_list[i].name]
            item = func(item_list[i + 1].value)
            item_list[i] = Item(item)
            item_list.pop(i + 1)
            i = i + 1
        else:
            i = i + 1
    # process basic calculate
    for symbolGroup in symbolGroupList:
        i = 0
        while i < len(item_list):
            if not item_list[i].type == 2:
                i = i + 1
                continue
            if not item_list[i].symbol in symbolGroup:
                i = i + 1
                continue
            symbol = item_list[i].symbol
            if symbol == "^":
                item = item_list[i - 1].value ** item_list[i + 1].value
            elif symbol == "*":
                item = item_list[i - 1].value * item_list[i + 1].value
            elif symbol == "/":
                item = item_list[i - 1].value / item_list[i + 1].value
            elif symbol == "+":
                item = item_list[i - 1].value + item_list[i + 1].value
            elif symbol == "-":
                item = item_list[i - 1].value - item_list[i + 1].value
            else:
                raise SyntaxError
            item_list[i] = Item(item, 1)
            item_list.pop(i - 1)
            item_list.pop(i)

    localVariableDic["ans"] = item_list[0].value
    return item_list[0].value


def toAutoString(value):
    return str(round(value, 12))


