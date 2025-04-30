//multiline

// Represents an array literal expression (e.g., [1, 2, 3]) in the AST

// ArrayLiteral "constructor"
def makeArrayLiteral(elements, position) {
    let literal = {};
    literal["type"] = "ArrayLiteral";
    // Always store a shallow-unique array (never null: default to empty)
    if (elements == null) {
        literal["elements"] = [];
    } else {
        literal["elements"] = elements;
    }
    literal["position"] = position;
    // Attach evaluate and toJson "methods" (as map fields)
    literal["evaluate"] = evaluateArrayLiteral;
    literal["toJson"] = arrayLiteralToJson;
    return literal;
}

// Evaluates the array literal node to a runtime array of values
def evaluateArrayLiteral(self, context) {
    // trackEvaluationStep(context); // FIXME implemented later

    // Build new array for evaluated element values
    let values = [];
    let i = 0;
    let elems = self["elements"];
    let n = len(elems);

    while (i < n) {
        let element = elems[i];
        // "Node" is assumed to be represented as a map with ["evaluate"]
        let result = element["evaluate"](element, context);
        push(values, result);
        i = i + 1;
    }
    return values;
}

// Converts ArrayLiteral node to JSON string representation
def arrayLiteralToJson(self) {
    let elems = self["elements"];
    let n = len(elems);

    // Build JSON for elements
    let i = 0;
    let elementsParts = [];

    while (i < n) {
        let element = elems[i];
        let part = "null";
        if (element != null) {
            part = element["toJson"](element);
        }
        push(elementsParts, part);
        i = i + 1;
    }

    // Join elements with comma + space
    let elementsJson = joinWithCommaSpace(elementsParts);

    return '{ "type": "ArrayLiteral", "position": "' + self["position"] + '", "elements": [ ' + elementsJson + ' ] }';
}

// Helper: join array of strings with ", "
def joinWithCommaSpace(strs) {
    let n = len(strs);
    if (n == 0) {
        return "";
    }
    let result = strs[0];
    let i = 1;
    while (i < n) {
        result = result + ", " + strs[i];
        i = i + 1;
    }
    return result;
}



def isTruthy(value) {
    if (value == null) {
        return false;
    }
    if (typeof(value) == "boolean") {
        return value;
    }
    if (typeof(value) == "number") {
        if (value != 0) {
            return true;
        } else {
            return false;
        }
    }
    if (typeof(value) == "string") {
        if (len(value) > 0) {
            return true;
        } else {
            return false;
        }
    }
    // All other objects are truthy
    return true;
}

def EvaluatorIsTruthy(val) { // INTEGRATION
    return isTruthy(val);
}

def applyPrefixOperator(operator, value) {
    if (operator == "-") {
        if (typeof(value) == "number") {
            return 0 - value;
        }
        return null;
    }
    if (operator == "!") {
        if (isTruthy(value)) {
            return false;
        } else {
            return true;
        }
    }
    return null;
}

def Evaluator_applyPrefixOperator(operator, rightValue) { //INTEGRATION
    return applyPrefixOperator(operator, rightValue);
}

def checkStringLength(left, right, resourceQuota) { // FIXME implement later
    /*
    let leftLength = 0;
    let rightLength = 0;

    if (left != null) {
        leftLength = len(left);
    }
    if (right != null) {
        rightLength = len(right);
    }
    let maxLength = resourceQuota["getMaxStringLength"]();

    if (leftLength + rightLength > maxLength) {
        // throw ResourceExhaustionError(ResourceLimitType.VARIABLE_COUNT, 0, 0)
        // Since throwing exceptions is not described in InterpreterJ, assume calling an error raise procedure:
        resourceQuota["raiseResourceExhaustion"]("VARIABLE_COUNT", 0, 0);
    }
    */
}

def applyInfixOperator(left, operator, right, resourceQuota) {
    if (operator == "+") {
        if (typeof(left) == "array" && typeof(right) == "array") {
            // Clone left list to result list
            let resultList = [];
            let i = 0;
            while (i < len(left)) {
                push(resultList, left[i]);
                i = i + 1;
            }
            let j = 0;
            while (j < len(right)) {
                push(resultList, right[j]);
                j = j + 1;
            }
            return resultList;
        }
        if (typeof(left) == "string" || typeof(right) == "string") {
            let leftStr = "";
            let rightStr = "";
            if (left == null) { leftStr = "null"; } else { leftStr = stringValue(left); }
            if (right == null) { rightStr = "null"; } else { rightStr = stringValue(right); }

            checkStringLength(leftStr, rightStr, resourceQuota);
            
            return leftStr + rightStr;
        }
    }

    if (typeof(left) == "number" && typeof(right) == "number") {
        let leftVal = left;
        let rightVal = right;
        if (operator == "+") {
            return leftVal + rightVal;
        }
        if (operator == "-") {
            return leftVal - rightVal;
        }
        if (operator == "*") {
            return leftVal * rightVal;
        }
        if (operator == "/") {
            return leftVal / rightVal;
        }
        if (operator == "%") {
            return leftVal % rightVal;
        }
        if (operator == "<") {
            return leftVal < rightVal;
        }
        if (operator == ">") {
            return leftVal > rightVal;
        }
        if (operator == "<=") {
            return leftVal <= rightVal;
        }
        if (operator == ">=") {
            return leftVal >= rightVal;
        }
        if (operator == "==") {
            return leftVal == rightVal;
        }
        if (operator == "!=") {
            return leftVal != rightVal;
        }
    }
    
    if (operator == "&&") {
        if (isTruthy(left) && isTruthy(right)) {
            return true;
        } else {
            return false;
        }
    }
    if (operator == "||") {
        if (isTruthy(left) || isTruthy(right)) {
            return true;
        } else {
            return false;
        }
    }
    if (operator == "==") {
        if (left == null) {
            if (right == null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (leftEquals(left, right)) {
                return true;
            } else {
                return false;
            }
        }
    }
    if (operator == "!=") {
        if (left == null) {
            if (right != null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (!leftEquals(left, right)) {
                return true;
            } else {
                return false;
            }
        }
    }
    return null;
}

def stringValue(value) {
    if (typeof(value) == "string") {
        return value;
    }
    if (typeof(value) == "number") {
        return numberToString(value);
    }
    if (typeof(value) == "boolean") {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }
    if (value == null) {
        return "null";
    }
    // For arrays and maps, simplified representation:
    if (typeof(value) == "array") {
        let result = "[";
        let i = 0;
        while (i < len(value)) {
            if (i != 0) {
                result = result + ",";
            }
            result = result + stringValue(value[i]);
            i = i + 1;
        }
        return result + "]";
    }
    if (typeof(value) == "map") {
        let result = "{";
        let k = keys(value);
        let i = 0;
        while (i < len(k)) {
            if (i != 0) {
                result = result + ",";
            }
            let keyStr = "";
            if (typeof(k[i]) == "string") {
                keyStr = k[i];
            } else {
                keyStr = numberToString(k[i]);
            }
            result = result + keyStr + ":" + stringValue(value[k[i]]);
            i = i + 1;
        }
        return result + "}";
    }
    // Unknown types fallback
    return "";
}

def numberToString(num) {
    return string(num);
}

def leftEquals(left, right) {
    // We must manually compare primitive types only
    if (typeof(left) != typeof(right)) {
        return false;
    }
    if (typeof(left) == "string" || typeof(left) == "number" || typeof(left) == "boolean") {
        return left == right;
    }
    // For null
    if (left == null && right == null) {
        return true;
    }
    // For arrays and maps, we consider unequal.
    return false;
}

// ---- Setup for resource quota ----

// Named functions
def getMaxStringLength() {
    return 1000;
}

def raiseResourceExhaustion(reason, a, b) {
    puts("ResourceExhausted: " + reason);
}

// Dummy resource quota map
let dummyQuota = {
    "getMaxStringLength": getMaxStringLength,
    "raiseResourceExhaustion": raiseResourceExhaustion
};



// Create an IndexExpression AST node as a map with methods attached
def makeIndexExpression(collectionNode, indexNode, position) {
    let node = {
        "type": "IndexExpression",
        "collection": collectionNode,
        "index": indexNode,
        "position": position
    }

    def evaluate(self,context) {
        // Optionally: If you need tracking, call trackEvaluationStep(context)
        // Evaluate collection
        let collectionObject = node["collection"]["evaluate"](node["collection"], context)
        // Evaluate index
        let indexValue = node["index"]["evaluate"](node["index"],context)

        if (isArray(collectionObject)) {
            return node["evaluateArrayIndex"](collectionObject, indexValue)
        } else {
            let mapType = false
            if (collectionObject != null) {
                // Check if all keys are not consecutive integers from 0...N-1 with "_isArray"==true
                let k = 0
                let keyList = keys(collectionObject)
                // Naive detection: treat any non-array non-null map as a map
                if (len(keyList) > 0) {
                    mapType = true
                    // But if only "_isArray" as key, treat as array (empty array)
                    if (len(keyList) == 1) {
                        if (keyList[0] == "_isArray") {
                            mapType = false
                        }
                    }
                } else {
                    mapType = true
                }
            }
            if (mapType) {
                return node["evaluateMapIndex"](collectionObject, indexValue)
            }
        }
        throwRuntimeError(
            "Cannot use index operator on non-collection value, got: " + collectionObject,
            node["position"]["line"],
            node["position"]["column"] // FIXME throw, catch, halt, etc not supported
        )
        return null
    }
    node["evaluate"] = evaluate;

    def evaluateArrayIndex(array, indexValue) {
        //puts("DEBUG: evaluateArrayIndex(" + array + "," + indexValue + ")"); // FIXME remove DEBUG code
        // Only numbers allowed for array index
        /*let numeric = false
        if (indexValue != null) {
            if ((indexValue + 0) == indexValue) {
                numeric = true
            }
        }
        if (!(numeric)) {
            throwRuntimeError(
                "Array index must be a number, got: " + indexValue,
                node["position"]["line"],
                node["position"]["column"]
            )
        }*/

        let idx = indexValue /* + 0 */
        let arrayLength = len(array)
        //if ("_isArray" in array) { // ???
        //    arrayLength = array["length"]
        //}
        // Bounds check
        if (idx < 0 || idx >= arrayLength) {
            throwRuntimeError(
                "Array index out of bounds: " + idx + ", array size: " + arrayLength,
                node["position"]["line"],
                node["position"]["column"]
            )
        }

        // Return array element by index
        return array[idx]
    }
    node["evaluateArrayIndex"] = evaluateArrayIndex;

    def evaluateMapIndex(map, key) {
        //puts("DEBUG: evaluateMapIndex(" + map + "," + key + ")"); // FIXME remove DEBUG code
        let isString = false
        let isNumber = false
        if (key != null) {
            if ((key + "") == key) {
                isString = true
            }
            if ((key + 0) == key) {
                isNumber = true
            }
        }
        if (!(isString || isNumber)) {
            throwRuntimeError(
                "Map key must be a string or number, got: " + key,
                node["position"]["line"],
                node["position"]["column"]
            )
        }

        // If key not present, return null
        let keyList = keys(map)
        let found = false
        let k = 0
        while (k < len(keyList)) {
            if (keyList[k] == key) {
                found = true
            }
            k = k + 1
        }
        if (!(found)) {
            return null
        }
        return map[key]
    }
    node["evaluateMapIndex"]=evaluateMapIndex;

    // Manual toJson attached
    def toJson(self) {
        let collectionJson = "null"
        let indexJson = "null"
        if (node["collection"] != null) {
            collectionJson = node["collection"]["toJson"](node["collection"])
        }
        if (node["index"] != null) {
            indexJson = node["index"]["toJson"](node["index"])
        }
        // position should be stringified, assuming position already a string
        return '{ "type": "IndexExpression", "position": "' + node["position"] + '", "collection": ' + collectionJson + ', "index": ' + indexJson + ' }'
    }
    node["toJson"] = toJson;

    return node
}



// FYI: Node is an abstract class and cannot be represented properly, but nested class Position can

// Position "class" as a map constructor and related functions

def makePosition(line, column) {
    let pos = {
        "line": line,
        "column": column
    };
    pos["getLine"] = positionGetLine;
    pos["getColumn"] = positionGetColumn;
    pos["toString"] = positionToString;
    return pos;
}

def positionGetLine(self) {
    return self["line"];
}

def positionGetColumn(self) {
    return self["column"];
}

def positionToString(self) {
    return "" + self["line"] + ":" + self["column"];
}


// ReturnStatement node representation in InterpreterJ

let ReturnStatement = {};

// Constructor: def ReturnStatement_create(value, position)
def ReturnStatement_create(value, position) {
    let node = {};
    node["type"] = "ReturnStatement";
    node["value"] = value;
    node["position"] = position;

    // Attach evaluate function
    node["evaluate"] = ReturnStatement_evaluate;

    // Attach toJson function
    node["toJson"] = ReturnStatement_toJson;

    return node;
}

let returnValueIndicatorMagicValue = "isReturnValue" + random(); //DIRTY HACK

// def ReturnStatement_evaluate(self, context)
def ReturnStatement_evaluate(self, context) {

    //puts("DEBUG: return eval!"); //FIXME remove DEBUG code

    let valueResult = null;
    if (self["value"] != null) {
        valueResult = self["value"]["evaluate"](self["value"], context);
    } else {
        valueResult = null;
    }

    // ReturnValue wrapper
    let returnValue = {};
    returnValue["value"] = valueResult;
    returnValue[returnValueIndicatorMagicValue]  = true;
    return returnValue;
}

// def ReturnStatement_toJson(self)
def ReturnStatement_toJson(self) {
    let valueJson = "null";
    if (self["value"] != null) {
        valueJson = self["value"]["toJson"](self["value"]);
    }
    return '{ "type": "ReturnStatement", "position": "' + self["position"] + '", "value": ' + valueJson + ' }';
}



// WhileStatement node as procedural map

  def makeWhileStatement(condition, body, position) {
    let node = {
      "type": "WhileStatement",
      "position": position,
      "condition": condition,
      "body": body
    };
    
    def evaluate(self,context) {
      let result = null;
      // Loop
      while (EvaluatorIsTruthy(node["condition"]["evaluate"](node["condition"],context))) {
        // Track each iteration
        //context["trackLoopIteration"](node["position"]); // FIXME implemented later
        result = node["body"]["evaluate"](node["body"],context);
        // If result is a ReturnValue, stop loop and propagate
        if (isReturnValue(result)) {
          //return result["value"];
          return result;
        } else {
          // do nothing
        }
      }
      return result;
    }
    node["evaluate"] = evaluate;

    def toJson(self) {
      let conditionJson = "null";
      if (node["condition"] != null) {
        conditionJson = node["condition"]["toJson"](node["condition"]);
      } else {
        // do nothing
      }
      let bodyJson = "null";
      if (node["body"] != null) {
        bodyJson = node["body"]["toJson"](node["body"]);
      } else {
        // do nothing
      }
      return '{ "type": "WhileStatement", "position": "' + node["position"] + '", "condition": ' + conditionJson + ', "body": ' + bodyJson + ' }';
    }
    node["toJson"] = toJson;

    return node;
  }
  

// Helper to check ReturnValue duplicate
/* def isReturnValue(result) {
    if (result == null) {
        return false;
    } else {
        if (isMap(result)) { // FIXME result handling to support return
            let keysArr = keys(result);
            let i = 0;
            while (i < len(keysArr)) {
                if (keysArr[i] == "isReturnValue") {
                    return result["isReturnValue"] == true;
                }
                i = i + 1;
            }
        }
        return false;
    }
} */



// AssignmentStatement representation as a map with helper functions attached manually.
// Object-Oriented structure and methods replaced with procedural style.

def makeAssignmentStatement(name, value, position) {
    let node = {
        "type": "AssignmentStatement",
        "name": name,
        "value": value,
        "position": position
    };

    // Attach toJson function explicitly
    node["toJson"] = assignmentStatementToJson;
    // Attach evaluate function explicitly
    node["evaluate"] = assignmentStatementEvaluate;
    return node;
}

// assignmentStatementToJson function: returns JSON representation
def assignmentStatementToJson(self) {
    let valueJson = "null";
    if (self["value"] != null) {
        // Assumes that value has "toJson" function attached if it's not null
        valueJson = self["value"]["toJson"](self["value"]);
    } else {
        valueJson = "null";
    }
    return '{ "type": "AssignmentStatement", "position": "' + self["position"] +
        '", "name": "' + self["name"] +
        '", "value": ' + valueJson + ' }';
}

// assignmentStatementEvaluate function: calls value's evaluate and assigns to context
def assignmentStatementEvaluate(self, context) {
    let valueResult = null;
    if (self["value"] != null) {
        // Assumes value has "evaluate" function with similar signature
        valueResult = self["value"]["evaluate"](self["value"], context);
    } else {
        valueResult = null;
    }
    // Assumes context has "assign" function as a map field
    // Assignment result is returned
    return context["assign"](context, self["name"], valueResult, self["position"]);
}



// ExpressionStatement representation in InterpreterJ

// Create a new ExpressionStatement node as a map
def makeExpressionStatement(expression, position) {
    let node = { 
        "type": "ExpressionStatement", 
        "position": position, 
        "expression": expression
    };
    node["evaluate"] = evaluateExpressionStatement;
    node["toJson"] = toJsonExpressionStatement;
    return node;
}

// Evaluates the expression (procedurally, null if missing)
def evaluateExpressionStatement(self, context) {
    if (self["expression"] == null) {
        return null;
    } else {
        // Assumes expression node has "evaluate" as a field
        return self["expression"]["evaluate"](self["expression"], context);
    }
}

// Serializes the node as JSON-like string (strings are NOT escaped)
def toJsonExpressionStatement(self) {
    let expr = "null";
    if (self["expression"] != null) {
        // Assumes expression node has "toJson" as a field
        expr = self["expression"]["toJson"](self["expression"]);
    } else {
        expr = "null";
    }
    return '{ "type": "ExpressionStatement", "position": "' + self["position"] + '", "expression": ' + expr + ' }';
}



// InfixExpression node constructor and functions in InterpreterJ

def makeInfixExpression(left, operator, right, position) {
    let node = {
        "type": "InfixExpression",
        "left": left,
        "operator": operator,
        "right": right,
        "position": position
    };
    // Attach functions manually as fields
    node["evaluate"] = evaluateInfixExpression;
    node["toJson"] = infixExpressionToJson;
    return node;
}

// Evaluate the infix expression: wraps tracking, evaluation, and operator application
def evaluateInfixExpression(self, context) {
    // Track evaluation step
    //trackEvaluationStep(context); // FIXME implemented later
    let leftValue = null;
    let rightValue = null;
    let result = null;
    if (self["left"] != null) {
        leftValue = self["left"]["evaluate"](self["left"], context);
    } else {
        leftValue = null;
    }
    if (self["right"] != null) {
        rightValue = self["right"]["evaluate"](self["right"], context);
    } else {
        rightValue = null;
    }
    result = applyInfixOperator(leftValue, self["operator"], rightValue, context["getResourceQuota"](context));
    return result;
}

// Export to JSON string (no newlines or escapes in string literals)
def infixExpressionToJson(self) {
    let leftJson = "null";
    let rightJson = "null";
    let operatorString = "";
    if (self["left"] != null) {
        leftJson = self["left"]["toJson"](self["left"]);
    } else {
        leftJson = "null";
    }
    if (self["right"] != null) {
        rightJson = self["right"]["toJson"](self["right"]);
    } else {
        rightJson = "null";
    }
    operatorString = self["operator"];
    let positionString = self["position"];
    let json = '{ "type": "InfixExpression", "position": "' + positionString + '", "left": ' + leftJson + ', "operator": "' + operatorString + '", "right": ' + rightJson + ' }';
    return json;
}



// NullLiteral node representation for InterpreterJ

// Creates a NullLiteral node: { "type": "NullLiteral", "position": ..., "evaluate": ..., "toJson": ... }
def makeNullLiteral(position) {
    let node = {
        "type": "NullLiteral",
        "position": position
    };
    node["evaluate"] = nullLiteralEvaluate;
    node["toJson"] = nullLiteralToJson;
    return node;
}

// Evaluates the NullLiteral node (always returns null)
def nullLiteralEvaluate(self, context) {
    return null;
}

// Produces JSON for the NullLiteral node, strictly no string escaping
def nullLiteralToJson(self) {
    let json = '{ "type": "NullLiteral", "position": "' + self["position"] + '", "value": null }';
    return json;
}



def makeReturnValue(value, position) { // FIXME called?
  let rv = {};
  rv["value"] = value;

  def getValue() {
    return rv["value"];
  }

  def toString() {
    let result = "Return(";
    if (rv["value"] == null) {
      result = result + "null";
    } else {
      result = result + toStringValue(rv["value"]);
    }
    result = result + ")";
    return result;
  }

  rv["getValue"] = getValue;
  rv["toString"] = toString;
  return rv;
}

// Helper function to convert a value to string representation
def toStringValue(val) {
  if (val == null) {
    return "null";
  }

  //if (val["toString"] != null) { // FIXME
  //  return val["toString"]();
  //}

  if (typeof(val) == "string") {
    return val;
  }
  
  if (typeof(val) == "number") {
    return "" + val;
  }
  
  if (typeof(val) == "boolean") {
    if (val == true) {
      return "true";
    } else {
      return "false";
    }
  }
  
  // Fallback: just convert to string as best effort
  return "" + val;
}



// BlockStatement "constructor"
def makeBlockStatement(statements, position) {
    let node = {
        "type": "BlockStatement",
        "position": position,
        "statements": []
    };
    if (statements != null) {
        let idx = 0;
        let stmtsLen = len(statements);
        while (idx < stmtsLen) {
            push(node["statements"], statements[idx]);
            idx = idx + 1;
        }
    }
    // Attach behavior functions
    node["addStatement"] = blockStatementAddStatement;
    node["evaluate"] = blockStatementEvaluate;
    node["toJson"] = blockStatementToJson;
    return node;
}

// Add a statement to block (null-guard)
def blockStatementAddStatement(self, statement) {
    if (statement != null) {
        push(self["statements"], statement);
    }
}

// Evaluation logic
def blockStatementEvaluate(self, context) {
    // trackEvaluationStep(context);
    //if (context["trackEvaluationStep"] != null) { // FIXME implemented later
    //    context["trackEvaluationStep"](context);
    //}
    // block scope
    let blockContext = null;
    if (context["extend"] != null) {
        blockContext = context["extend"](context);
    } else {
        blockContext = context; // fallback, non-scoped
    }
    let result = null;
    let idx = 0;
    let stmts = self["statements"];
    let stmtsLen = len(stmts);
    while (idx < stmtsLen) {
        let statement = stmts[idx];
        result = statement["evaluate"](statement, blockContext);
        if (result != null) {
            // Early return for ReturnValue
            if (isReturnValue(result)) {
                //puts("xx ret"); //DEBUG
                //return result["value"];
                return result;
            }
            else {
                //puts("xx no ret"); //DEBUG
            }
        }
        idx = idx + 1;
    }
    return result;
}

// Helper: test for ReturnValue (very minimal, expects you store type tags) - duplicate
/* def isReturnValue(obj) {
    if (obj == null) {
        return false;
    }
    if (obj["type"] == "ReturnValue") {
        return true;
    }
    return false;
} */

// toJson, no escaping or .join: manual string assembly
def blockStatementToJson(self) {
    let stmts = self["statements"];
    let stmtsLen = len(stmts);
    let arr = [];
    let idx = 0;
    while (idx < stmtsLen) {
        let statement = stmts[idx];
        if (statement["toJson"] != null) {
            let jsonVal = statement["toJson"](statement);
            push(arr, jsonVal);
        }
        idx = idx + 1;
    }
    // Manual join with comma and newline. (No .join. No escapes. You may only use real line breaks in code.)
    let elementsJson = "";
    idx = 0;
    let arrLen = len(arr);
    while (idx < arrLen) {
        elementsJson = elementsJson + arr[idx];
        if (idx < arrLen - 1) {
            elementsJson = elementsJson + ",\n";
        }
        idx = idx + 1;
    }
    return '{ "type": "BlockStatement", "position": "' + self["position"] + '", "statements": [ ' + elementsJson + ' ] }';
}



// InterpreterJ: FunctionDeclaration node representation

// Create a FunctionDeclaration node map
def makeFunctionDeclaration(name, parameters, body, position) {
    let node = {
        "type": "FunctionDeclaration",
        "name": name,
        "parameters": parameters,
        "body": body,
        "position": position
    };
    node["evaluate"] = evaluateFunctionDeclaration;
    node["toJson"] = functionDeclarationToJson;
    return node;
}

// Evaluate function for the FunctionDeclaration node
def evaluateFunctionDeclaration(node, context) { // FIXME really?
    //trackEvaluationStep(context); // FIXME implemented later

    // Create the function definition as a map
    def functionValue(args) {
        // Create a new context extended from the parent
        let functionContext = extendContext(context);

        // Bind each parameter to its argument (or null if absent)
        let i = 0;
        while (i < len(node["parameters"])) {
            let param = node["parameters"][i];
            let arg = null;
            if (i < len(args)) {
                arg = args[i];
            }
            functionContext["define"](functionContext, param, arg);
            i = i + 1;
        }

        // Evaluate the body in the new function context
        let result = node["body"]["evaluate"](node["body"], functionContext);

        // Unwrap ReturnValue if present (assuming ReturnValue is a map with "value" field)
        if (isReturnValue(result)) {
            return result["value"];
        } else {
            return result;
        }
    }

    // Place the function definition as a callable in context, under the function's name
    context["define"](context, node["name"], functionValue);

    // Return nothing or null as this is a declaration statement
    return null;
}

// Helper: isReturnValue(result)
// Checks if result is a map with key "isReturnValue" set to true
def isReturnValue(result) {
    if (result == null) {
        //puts("No ret value: " + result); // FIXME //DEBUG
        return false;
    } else {
        if (isMap(result)) { // FIXME result handling to support return
            let keysArr = keys(result);
            let i = 0;
            while (i < len(keysArr)) {
                if (keysArr[i] == returnValueIndicatorMagicValue) {
                    //puts("Ret value: " + result); // FIXME //DEBUG
                    return result[returnValueIndicatorMagicValue] == true;
                }
                i = i + 1;
            }
        }
        //puts("No ret value2: " + result); // FIXME //DEBUG
        return false;
    }
}

// toJson for FunctionDeclaration
def functionDeclarationToJson(node) {
    // Build parameters as JSON array of quoted strings
    let elementsArr = [];
    let i = 0;
    while (i < len(node["parameters"])) {
        let quoted = '"' + node["parameters"][i] + '"';
        push(elementsArr, quoted);
        i = i + 1;
    }
    let parametersJson = "";
    if (len(elementsArr) > 0) {
        parametersJson = elementsArr[0];
        i = 1;
        while (i < len(elementsArr)) {
            parametersJson = parametersJson + ", " + elementsArr[i];
            i = i + 1;
        }
    }

    // Handle body toJson
    let bodyJson = "null";
    if (node["body"] != null) {
        // Assume body is a map with a "toJson" function
        bodyJson = node["body"]["toJson"](node["body"]);
    }

    // Build and return the JSON string according to specification
    let s = '{ "type": "FunctionDeclaration", "position": "' + node["position"] + '", "name": "' + node["name"] + '", "parameters": [ ' + parametersJson + ' ], "body": ' + bodyJson + ' }';
    return s;
}



// NumberLiteral node constructor
def makeNumberLiteral(value, position) {
    let node = {
        "type": "NumberLiteral",
        "position": position,
        "value": value
    };
    // Attach evaluate function
    node["evaluate"] = numberLiteralEvaluate;
    // Attach toJson function
    node["toJson"] = numberLiteralToJson;
    return node;
}

// Evaluate function for NumberLiteral node
def numberLiteralEvaluate(node, context) {
    return node["value"];
}

// toJson function for NumberLiteral node
def numberLiteralToJson(node) {
    let typePart = '{ "type": "NumberLiteral", "position": "';
    let posPart = node["position"];
    let valuePart = '", "value": ';
    let valVal = node["value"];
    let endPart = " }";
    return typePart + posPart + valuePart + valVal + endPart;
}



// StringLiteral: a literal string AST node

// Construct a StringLiteral node
def makeStringLiteral(value, position) {
    // Create a map (object) to represent the node
    let node = { 
        "type": "StringLiteral",
        "value": value,
        "position": position
    };
    // Manually attach functions
    node["getValue"] = getStringLiteralValue;
    node["evaluate"] = evaluateStringLiteral;
    node["toJson"] = stringLiteralToJson;
    return node;
}

// Get value field
def getStringLiteralValue(thisNode) {
    return thisNode["value"];
}

// Evaluate the node (returns the literal value; assumes trackEvaluationStep is defined elsewhere)
def evaluateStringLiteral(thisNode, context) {
    // Track this evaluation step (function must exist elsewhere)
    //trackEvaluationStep(context); // FIXME implemented later
    return thisNode["value"];
}

// Generate JSON representation of this StringLiteral node
def stringLiteralToJson(thisNode) {
    let typeString = 'StringLiteral';
    let positionString = thisNode["position"];
    let valueString = thisNode["value"];
    // No string escaping allowed, so produce only correct non-escaped JSON
    // Only single-line, simple, explicit building
    return '{ "type": "' + typeString + '", "position": "' + positionString + '", "value": "' + valueString + '" }';
}



// ===============================
// BooleanLiteral Node (InterpreterJ)
// ===============================

// Constructor for BooleanLiteral Node
def makeBooleanLiteral(value, position) {
    let node = {
        "type": "BooleanLiteral",
        "value": value,
        "position": position
    };
    node["getValue"] = getBooleanLiteralValue;
    node["evaluate"] = evaluateBooleanLiteral;
    node["toJson"] = toJsonBooleanLiteral;
    return node;
}

// Accessor for value
def getBooleanLiteralValue(self) {
    return self["value"];
}

// Evaluator - returns the value
def evaluateBooleanLiteral(self, context) {
    return self["value"];
}

// toJson - returns JSON string
def toJsonBooleanLiteral(self) {
    if (self["value"]) {
        return '{ "type": "BooleanLiteral", "position": "' + self["position"] + '", "value": true }';
    } else {
        return '{ "type": "BooleanLiteral", "position": "' + self["position"] + '", "value": false }';
    }
}



def makeIdentifier(name, position) {
    let obj = {
        "type": "Identifier",
        "name": name,
        "position": position
    };
    // Attach evaluator function
    obj["evaluate"] = identifierEvaluate;
    obj["toJson"] = identifierToJson;
    return obj;
}

// Evaluator function for Identifier nodes
def identifierEvaluate(self, context) {
    // Try reading from context; simulate try/catch by explicit check
    let name = self["name"];
    let position = self["position"];
    // (Assume context["get"](varName, position) returns null or throws a map error object on failure)
    let value = context["get"](context, name, position);
    // It is up to context["get"] to throw an error map if variable is undefined
    return value;
}

// toJson function for Identifier nodes
def identifierToJson(self) {
    let position = self["position"];
    let name = self["name"];
    return '{ "type": "Identifier", "position": "' + position + '", "name": "' + name + '" }';
}



// --- TokenType constants ---
let DEF = "DEF";
let LET = "LET";
let IF = "IF";
let ELSE = "ELSE";
let WHILE = "WHILE";
let RETURN = "RETURN";
let TRUE = "TRUE";
let FALSE = "FALSE";
let NULL = "NULL";
let IDENTIFIER = "IDENTIFIER";

// --- Keywords map ---
let keywords = {};
keywords["def"] = DEF;
keywords["let"] = LET;
keywords["if"] = IF;
keywords["else"] = ELSE;
keywords["while"] = WHILE;
keywords["return"] = RETURN;
keywords["true"] = TRUE;
keywords["false"] = FALSE;
keywords["null"] = NULL;

// --- Keyword lookup function ---
def lookupKeyword(identifier) {
  if (keywords[identifier] != null) {
    return keywords[identifier];
  } else {
    return IDENTIFIER;
  }
}



// Parser.s: LL(1) Predictive Recursive Descent Parser for InterpreterJ

// TokenType constants expected available:
// LET, DEF, IF, ELSE, WHILE, RETURN,
// TRUE, FALSE, NULL, IDENTIFIER,
// NUMBER, STRING,
// PLUS, MINUS, ASTERISK, SLASH, PERCENT,
// EQ, NOT_EQ, LT, GT, LT_EQ, GT_EQ,
// AND, OR, NOT,
// ASSIGN,
// COMMA, SEMICOLON, LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET, COLON, EOF

// Assumes lexer object with method nextToken() returning Token map with keys:
// type, literal, line, column

// Parser state variables global inside this file:

let lexer = null;          // Lexer instance (map)
let tokens = [];           // Array of tokens buffered (map with keys as above)
let currentPosition = 0;   // Index in tokens array

let currentToken = null;   // Current token map
let peekToken = null;      // Next token map

// Errors array stores parsing errors as maps with keys: message, line, column
let errors = [];

// prefixParseFns: map from tokenType -> parser function returning Node (map)
let prefixParseFns = {};

// infixParseFns: map from tokenType -> function taking (Node) returning Node
let infixParseFns = {};

// Precedence numeric values for operators:
// a map from tokenType to precedence integer
let precedences = {};

// Precedence constants for clarity (use integers)
let PREC_LOWEST = 1;
let PREC_OR = 2;
let PREC_AND = 3;
let PREC_EQUALS = 4;
let PREC_COMPARE = 5;
let PREC_SUM = 6;
let PREC_PRODUCT = 7;
let PREC_PREFIX = 8;
let PREC_CALL = 9;

// --- Functions ---

def initPrecedences() {
    precedences["OR"] = PREC_OR;
    precedences["AND"] = PREC_AND;
    precedences["EQ"] = PREC_EQUALS;
    precedences["NOT_EQ"] = PREC_EQUALS;
    precedences["LT"] = PREC_COMPARE;
    precedences["GT"] = PREC_COMPARE;
    precedences["LT_EQ"] = PREC_COMPARE;
    precedences["GT_EQ"] = PREC_COMPARE;
    precedences["PLUS"] = PREC_SUM;
    precedences["MINUS"] = PREC_SUM;
    precedences["ASTERISK"] = PREC_PRODUCT;
    precedences["SLASH"] = PREC_PRODUCT;
    precedences["PERCENT"] = PREC_PRODUCT;
    precedences["LPAREN"] = PREC_CALL;
    precedences["LBRACKET"] = PREC_CALL;
}

// Advance tokens: move currentToken and peekToken forward
def nextToken() {
    currentToken = peekToken;
    if (currentPosition < len(tokens)) {
        peekToken = tokens[currentPosition];
        currentPosition = currentPosition + 1;
    } else {
        let t = lexer["nextToken"](lexer);
        push(tokens, t);
        peekToken = t;
        currentPosition = currentPosition + 1;
    }
}

// Check if current token is of given type string
def currentTokenIs(tokenType) {
    if (currentToken == null) {
        return false;
    }
    if (currentToken["type"] == tokenType) {
        return true;
    }
    return false;
}

// Check if peek token is of given type string
def peekTokenIs(tokenType) {
    if (peekToken == null) {
        return false;
    }
    if (peekToken["type"] == tokenType) {
        return true;
    }
    return false;
}

// Expect peek token to be tokenType, if so advance, else record error and return false
def expectPeek(tokenType) {
    if (peekTokenIs(tokenType)) {
        nextToken();
        return true;
    } else {
        peekError(tokenType);
        return false;
    }
}

// Add error for expected peek token type mismatch
def peekError(tokenType) {
    let message = "Expected next token to be " + tokenType + ", got ";
    if (peekToken != null) {
        message = message + peekToken["type"];
    } else {
        message = message + "null";
    }
    let line = 0;
    let column = 0;
    if (peekToken != null) {
        line = peekToken["line"];
        column = peekToken["column"];
    }
    let err = { "message": message, "line": line, "column": column };
    push(errors, err);
}

// Get the precedence integer of current token's type or default
def currentPrecedence() {
    if (currentToken == null) {
        return PREC_LOWEST;
    }
    let p = precedences[currentToken["type"]];
    if (p == null) {
        return PREC_LOWEST;
    }
    return p;
}

// Get the precedence integer of peek token's type or default
def peekPrecedence() {
    if (peekToken == null) {
        return PREC_LOWEST;
    }
    let p = precedences[peekToken["type"]];
    if (p == null) {
        return PREC_LOWEST;
    }
    return p;
}

// Register prefix parse function for token type string tokType
def registerPrefix(tokType, fn) {
    prefixParseFns[tokType] = fn;
}

// Register infix parse function for token type string tokType
def registerInfix(tokType, fn) {
    infixParseFns[tokType] = fn;
}

// Parse entire program producing a Program node map
def parseProgram() {
    let program = makeProgram();

    while (!currentTokenIs("EOF")) {
        let stmt = parseStatement();
        if (stmt != null) {
            program["addStatement"](program, stmt);
        }
        nextToken();
    }
    return program;
}

// Parse one statement node
def parseStatement() {
    if (currentToken == null) {
        return null;
    }
    let typ = currentToken["type"];

    if (typ == "LET") {
        return parseVariableDeclaration();
    }
    if (typ == "DEF") {
        return parseFunctionDeclaration();
    }
    if (typ == "IF") {
        return parseIfStatement();
    }
    if (typ == "WHILE") {
        return parseWhileStatement();
    }
    if (typ == "RETURN") {
        return parseReturnStatement();
    }
    if (typ == "LBRACE") {
        return parseBlockStatement();
    }
    if (typ == "IDENTIFIER") {
        // Look ahead for `[`, or `=` after identifier to decide special statement
        if (peekToken != null) {
            if (peekToken["type"] == "LBRACKET") {
                let savedPosition = currentPosition;
                // We try parse index assignment statement
                // Move tokens ahead to check
                nextToken(); // move to '['
                if (!currentTokenIs("LBRACKET")) {
                    // Not `[`, rollback and parse expression statement
                    currentPosition = savedPosition;
                    peekToken = tokens[currentPosition-1];
                    currentToken = tokens[currentPosition-2];
                    return parseExpressionStatement();
                }
                nextToken(); // inside '[' expect index expression
                // parse expression (not stored now)
                let idxExpr = parseExpression(PREC_LOWEST);
                if (!expectPeek("RBRACKET")) {
                    currentPosition = savedPosition;
                    peekToken = tokens[currentPosition-1];
                    currentToken = tokens[currentPosition-2];
                    return parseExpressionStatement();
                }
                // Check if next is ASSIGN for index assignment
                if (peekTokenIs("ASSIGN")) {
                    // Reset tokens position and call parseIndexAssignmentStatement
                    currentPosition = savedPosition;
                    peekToken = tokens[currentPosition-1];
                    currentToken = tokens[currentPosition-2];
                    return parseIndexAssignmentStatement();
                } else {
                    // Reset tokens & parse expression statement
                    currentPosition = savedPosition;
                    peekToken = tokens[currentPosition-1];
                    currentToken = tokens[currentPosition-2];
                    return parseExpressionStatement();
                }
            }
            if (peekToken["type"] == "ASSIGN") {
                // assignment statement
                return parseAssignmentStatement();
            }
        }
        return parseExpressionStatement();
    }
    // Default fallback parse expression statement
    return parseExpressionStatement();
}

// Parse variable declaration: "let IDENTIFIER = expression;"
def parseVariableDeclaration() {
    let tok = currentToken;

    if (!expectPeek("IDENTIFIER")) {
        return null;
    }
    let name = currentToken["literal"];

    if (!expectPeek("ASSIGN")) {
        return null;
    }

    nextToken(); // move to expression start

    let initializer = parseExpression(PREC_LOWEST);

    if (peekTokenIs("SEMICOLON")) {
        nextToken();
    }

    let pos = tok["line"] + ":" + tok["column"];

    let node = makeVariableDeclaration(name, initializer, pos);
    return node;
}

// Parse function declaration: "def IDENTIFIER (params) { body }"
def parseFunctionDeclaration() {
    let tok = currentToken;

    if (!expectPeek("IDENTIFIER")) {
        return null;
    }
    let name = currentToken["literal"];

    if (!expectPeek("LPAREN")) {
        return null;
    }

    let parameters = parseFunctionParameters();

    if (!expectPeek("LBRACE")) {
        return null;
    }

    let body = parseBlockStatement();

    let pos = tok["line"] + ":" + tok["column"];

    let node = makeFunctionDeclaration(name, parameters, body, pos);

    return node;
}

// Parse function formal parameters inside parentheses
def parseFunctionParameters() {
    let params = [];

    if (peekTokenIs("RPAREN")) {
        nextToken();
        return params;
    }

    nextToken();
    push(params, currentToken["literal"]);

    while (peekTokenIs("COMMA")) {
        nextToken(); // consume comma
        nextToken(); // advance to next param
        push(params, currentToken["literal"]);
    }
    if (!expectPeek("RPAREN")) {
        return null;
    }
    return params;
}

// Parse if statement: if (condition) { consequence } [else { alternative }]
def parseIfStatement() {
    let tok = currentToken;

    if (!expectPeek("LPAREN")) {
        return null;
    }

    nextToken();
    let condition = parseExpression(PREC_LOWEST);

    if (!expectPeek("RPAREN")) {
        return null;
    }

    if (!expectPeek("LBRACE")) {
        return null;
    }

    let consequence = parseBlockStatement();

    let alternative = null;
    if (peekTokenIs("ELSE")) {
        nextToken();
        if (!expectPeek("LBRACE")) {
            return null;
        }
        alternative = parseBlockStatement();
    }
    let pos = tok["line"] + ":" + tok["column"];

    let node = makeIfStatement(condition, consequence, alternative, pos);
    return node;
}

// Parse return statement: return expression?;
def parseReturnStatement() {
    let tok = currentToken;

    nextToken(); // move after return

    let val = null;
    if (!currentTokenIs("SEMICOLON")) {
        val = parseExpression(PREC_LOWEST);
    }

    if (peekTokenIs("SEMICOLON")) {
        nextToken();
    }

    let pos = tok["line"] + ":" + tok["column"];

    let node = ReturnStatement_create(val, pos);
    return node;
}

// parse while statement: while (condition) { body }
def parseWhileStatement() {
    let tok = currentToken;

    if (!expectPeek("LPAREN")) {
        return null;
    }

    nextToken();
    let condition = parseExpression(PREC_LOWEST);

    if (!expectPeek("RPAREN")) {
        return null;
    }

    if (!expectPeek("LBRACE")) {
        return null;
    }

    let body = parseBlockStatement();

    let pos = tok["line"] + ":" + tok["column"];

    let node = makeWhileStatement(condition, body, pos);
    return node;
}

// parse block statement: { statements* }
def parseBlockStatement() {
    let tok = currentToken;

    let block = makeBlockStatement([], tok["line"] + ":" + tok["column"]);

    nextToken();

    while (!currentTokenIs("RBRACE") && !currentTokenIs("EOF")) {
        let stmt = parseStatement();
        if (stmt != null) {
            block["addStatement"](block, stmt);
        }
        nextToken();
    }

    if (!currentTokenIs("RBRACE")) {
        let err = { "message": "Expected '}' at end of block statement", "line": currentToken["line"], "column": currentToken["column"] };
        push(errors, err);
        return null;
    }

    return block;
}

// parse expression statement: expression;
def parseExpressionStatement() {
    let tok = currentToken;

    let expr = parseExpression(PREC_LOWEST);

    if (peekTokenIs("SEMICOLON")) {
        nextToken();
    }

    let pos = tok["line"] + ":" + tok["column"];

    let node = makeExpressionStatement(expr, pos);

    return node;
}

// parse expression with given precedence
def parseExpression(precedence) {
    if (currentToken == null) {
        return null;
    }
    let prefix = prefixParseFns[currentToken["type"]];
    if (prefix == null) {
        let message = "No prefix parse function for " + currentToken["type"] + " (" + currentToken["literal"] + ")";
        let err = { "message": message, "line": currentToken["line"], "column": currentToken["column"] };
        push(errors, err);
        return null;
    }

    let leftExp = prefix();

    while (!peekTokenIs("SEMICOLON") && precedence < peekPrecedence()) {
        let infix = infixParseFns[peekToken["type"]];
        if (infix == null) {
            return leftExp;
        }
        nextToken();
        leftExp = infix(leftExp);
    }
    return leftExp;
}

// parse identifier token
def parseIdentifier() {
    let pos = currentToken["line"] + ":" + currentToken["column"];
    return makeIdentifier(currentToken["literal"], pos);
}

// parse number literal token
def parseNumberLiteral() {
    let pos = currentToken["line"] + ":" + currentToken["column"];
    let value = 0.0;
    // Convert string literal to number
    // No exception support, so check numeric manually or assume conversion works
    // In InterpreterJ, use dummy parseNumber function or assume parseDouble exists
    let strVal = currentToken["literal"];
    // Could parse via built-in method or assume a function parseDouble
    value = parseDouble(strVal);
    return makeNumberLiteral(value, pos);
}

// dummy parseDouble returns default 0 for now (you may implement)
def parseDouble(str) {
    // Simulate parse double by relying on numeric string
    // In InterpreterJ, must be implemented in runtime; for now fallback 0
    // Alternatively, trust that the numeric string parses as number via implicit conversion
    // The spec shows NumberLiteral value field is number parse
    // Using: value = +str; implicit conversion
    // We do: 
    // Pretend InterpreterJ has built-in parse numeric:
    return int(str); //FIXME doule(x) // +str; // Allowed in InterpreterJ? Assume yes
}

// parse string literal token
def parseStringLiteral() {
    let pos = currentToken["line"] + ":" + currentToken["column"];
    return makeStringLiteral(currentToken["literal"], pos);
}

// parse boolean literal token: true or false
def parseBooleanLiteral() {
    let pos = currentToken["line"] + ":" + currentToken["column"];
    let val = false;
    if (currentToken["type"] == "TRUE") {
        val = true;
    } else {
        val = false;
    }
    return makeBooleanLiteral(val, pos);
}

// parse null literal token
def parseNullLiteral() {
    let pos = currentToken["line"] + ":" + currentToken["column"];
    return makeNullLiteral(pos);
}

// parse grouped expression: ( expression )
def parseGroupedExpression() {
    nextToken();
    let expr = parseExpression(PREC_LOWEST);
    if (!expectPeek("RPAREN")) {
        return null;
    }
    return expr;
}

// parse prefix expressions: -expr or !expr
def parsePrefixExpression() {
    let tok = currentToken;
    let operator = currentToken["literal"];
    nextToken();
    let right = parseExpression(PREC_PREFIX);
    let pos = tok["line"] + ":" + tok["column"];
    return makePrefixExpression(operator, right, pos);
}

// parse infix expressions with left Node given
def parseInfixExpression(left) {
    let tok = currentToken;
    let operator = currentToken["literal"];
    let precedence = currentPrecedence();
    nextToken();
    let right = parseExpression(precedence);
    let pos = tok["line"] + ":" + tok["column"];
    return makeInfixExpression(left, operator, right, pos);
}

// parse call expressions: fn(args...)
def parseCallExpression(functionNode) {
    let tok = currentToken;
    let args = parseCallArguments();
    let pos = tok["line"] + ":" + tok["column"];
    return CallExpression_create(functionNode, args, pos);
}

// parse call arguments list inside parentheses
def parseCallArguments() {
    let args = [];
    if (peekTokenIs("RPAREN")) {
        nextToken();
        return args;
    }
    nextToken();
    push(args, parseExpression(PREC_LOWEST));
    while (peekTokenIs("COMMA")) {
        nextToken(); // consume comma
        nextToken();
        push(args,parseExpression(PREC_LOWEST));
    }
    if (!expectPeek("RPAREN")) {
        return null;
    }
    return args;
}

// parse assignment statement: IDENTIFIER = expression;
def parseAssignmentStatement() {
    let tok = currentToken;
    let name = currentToken["literal"];

    if (!expectPeek("ASSIGN")) {
        return null;
    }
    nextToken();

    let value = parseExpression(PREC_LOWEST);

    if (peekTokenIs("SEMICOLON")) {
        nextToken();
    }

    let pos = tok["line"] + ":" + tok["column"];

    let node = makeAssignmentStatement(name, value, pos);

    return node;
}

// parse array literal: [elements]
def parseArrayLiteral() {
    let tok = currentToken;
    let elements = parseArrayElements();
    let pos = tok["line"] + ":" + tok["column"];
    return makeArrayLiteral(elements, pos);
}

// parse array elements (comma-separated expressions)
def parseArrayElements() {
    let elements = [];
    if (peekTokenIs("RBRACKET")) {
        nextToken();
        return elements;
    }
    nextToken();
    push(elements,parseExpression(PREC_LOWEST));
    while (peekTokenIs("COMMA")) {
        nextToken(); // consume comma
        nextToken();
        push(elements,parseExpression(PREC_LOWEST));
    }
    if (!expectPeek("RBRACKET")) {
        return null;
    }
    return elements;
}

// parse index expression: collection[index]
def parseIndexExpression(collectionNode) {
    let tok = currentToken;
    nextToken(); // skip '['
    let index = parseExpression(PREC_LOWEST);
    if (!expectPeek("RBRACKET")) {
        return null;
    }
    let pos = tok["line"] + ":" + tok["column"];
    return makeIndexExpression(collectionNode, index, pos);
}

// parse index assignment statement: collection[index] = value;
def parseIndexAssignmentStatement() {
    let tok = currentToken;
    let identifier = currentToken["literal"];
    let pos = tok["line"] + ":" + tok["column"];
    let collection = makeIdentifier(identifier, pos);
    nextToken();

    if (!currentTokenIs("LBRACKET")) {
        let err = { "message": "Expected '[' in index expression", "line": currentToken["line"], "column": currentToken["column"] };
        push(errors, err);
        return null;
    }
    nextToken(); // skip '['
    let index = parseExpression(PREC_LOWEST);

    if (!expectPeek("RBRACKET")) {
        return null;
    }

    if (!expectPeek("ASSIGN")) {
        return null;
    }
    nextToken();

    let value = parseExpression(PREC_LOWEST);

    if (peekTokenIs("SEMICOLON")) {
        nextToken();
    }
    return makeIndexAssignmentStatement(collection, index, value, pos);
}

// parse map literal: { pairs }
def parseMapLiteral() {
    let tok = currentToken;

    //puts("parseMapListeral: tok=" + tok["toString"](tok)); //DEBUG

    let pairs = parseMapPairs();
    let pos = tok["line"] + ":" + tok["column"];

    //puts("parseMapListeral: pairs=" + pairs); //DEBUG

    return makeMapLiteral(pairs, pos);
}

// parse map key-value pairs
def parseMapPairs() {
    let pairs = [];
    if (peekTokenIs("RBRACE")) {
        nextToken();

        //puts("parseMapPairs: No pairs!"); //DEBUG

        return pairs;
    }
    nextToken();

    //puts("parseMapPairs: currentToken=" + currentToken["toString"](currentToken)); //DEBUG

    let key = parseExpression(PREC_LOWEST);

    //puts("Key=" + key); //DEBUG

    if (!expectPeek("COLON")) {
        return null;
    }
    nextToken();
    let value = parseExpression(PREC_LOWEST);
    push(pairs, { "key": key, "value": value });

    while (peekTokenIs("COMMA")) {
        nextToken();
        nextToken();
        key = parseExpression(PREC_LOWEST);
        if (!expectPeek("COLON")) {
            return null;
        }
        nextToken();
        value = parseExpression(PREC_LOWEST);
        push(pairs, { "key": key, "value": value });
    }
    if (!expectPeek("RBRACE")) {
        return null;
    }
    return pairs;
}

// --- Initialization ---

def initParser(givenLexer) {
    lexer = givenLexer;
    tokens = [];
    currentPosition = 0;
    errors = [];

    prefixParseFns = {};
    infixParseFns = {};
    precedences = {};

    initPrecedences();

    nextToken();
    nextToken();

    registerPrefix("IDENTIFIER", parseIdentifier);
    registerPrefix("NUMBER", parseNumberLiteral);
    registerPrefix("STRING", parseStringLiteral);
    registerPrefix("TRUE", parseBooleanLiteral);
    registerPrefix("FALSE", parseBooleanLiteral);
    registerPrefix("NULL", parseNullLiteral);
    registerPrefix("LPAREN", parseGroupedExpression);
    registerPrefix("MINUS", parsePrefixExpression);
    registerPrefix("NOT", parsePrefixExpression);
    registerPrefix("LBRACKET", parseArrayLiteral);
    registerPrefix("LBRACE", parseMapLiteral);

    registerInfix("PLUS", parseInfixExpression);
    registerInfix("MINUS", parseInfixExpression);
    registerInfix("ASTERISK", parseInfixExpression);
    registerInfix("SLASH", parseInfixExpression);
    registerInfix("PERCENT", parseInfixExpression);
    registerInfix("EQ", parseInfixExpression);
    registerInfix("NOT_EQ", parseInfixExpression);
    registerInfix("LT", parseInfixExpression);
    registerInfix("GT", parseInfixExpression);
    registerInfix("LT_EQ", parseInfixExpression);
    registerInfix("GT_EQ", parseInfixExpression);
    registerInfix("AND", parseInfixExpression);
    registerInfix("OR", parseInfixExpression);
    registerInfix("LPAREN", parseCallExpression);
    registerInfix("LBRACKET", parseIndexExpression);
}

// --- Provide exported functions ---

// parse given Lexer instance to Program node
def parse(lexerInstance) {
    initParser(lexerInstance);
    return parseProgram();
}

// Errors getter
def getErrors() {
    return errors;
}

// --- End of Parser.s ---

// The parser uses externally defined node constructors and auxiliary functions:
// makeProgram, makeVariableDeclaration, makeFunctionDeclaration, makeIfStatement,
// makeWhileStatement, makeBlockStatement, makeExpressionStatement,
// ReturnStatement_create, makeAssignmentStatement, makeIndexAssignmentStatement,
// makeArrayLiteral, makeMapLiteral, makeIdentifier, makeNumberLiteral, makeStringLiteral,
// makeBooleanLiteral, makeNullLiteral,
// makePrefixExpression, makeInfixExpression, CallExpression_create, makeIndexExpression.

// All these must be defined as in given references.

// --- end ---



// Token "struct" factory and helpers for InterpreterJ

// Factory function to create a Token map
def createToken(type, literal, line, column) {
  let token = {}; // new map
  token["type"] = type;
  token["literal"] = literal;
  token["line"] = line;
  token["column"] = column;
  token["toString"] = tokenToString; // attach method manually
  return token;
}

// Accessor: get token type
def getTokenType(token) {
  return token["type"];
}

// Accessor: get token literal
def getTokenLiteral(token) {
  return token["literal"];
}

// Accessor: get token line
def getTokenLine(token) {
  return token["line"];
}

// Accessor: get token column
def getTokenColumn(token) {
  return token["column"];
}

// toString function for token (returns as string)
def tokenToString(token) {
  return "Token(" + token["type"] + ", '" + token["literal"] + "', " + token["line"] + ":" + token["column"] + ")";
}



// FIXME not sure CallExpression and FunctionDeclaration are properly designed to work together

// CallExpression "class" as a map (NOT a class!)
let CallExpression = {};

// Constructor: def CallExpression_create(callee, arguments, position)
def CallExpression_create(callee, arguments, position) {
    let node = {};
    node["type"] = "CallExpression";
    node["callee"] = callee;
    if (arguments == null) {
        node["arguments"] = [];
    } else {
        node["arguments"] = arguments;
    }
    node["position"] = position;

    // Attach functions to the node map
    node["evaluate"] = CallExpression_evaluate;
    node["toJson"] = CallExpression_toJson;

    return node;
}

// Evaluate (call expression) function. Called as node["evaluate"](node, context).
def CallExpression_evaluate(self, context) {
    // Self = this CallExpression node instance (map)

    // trackEvaluationStep(context); // FIXME implemented later

    // context.trackEvaluationDepth(position)
    context["trackEvaluationDepth"](context, self["position"]);

    // Use try-finally pattern to ensure exitEvaluationDepth is called
    let result = null;
    let errorCaught = false;
    let errorObj = null;

    // Try block simulation
    {
        // Evaluate the function (callee)
        let functionValue = null;
        if (self["callee"] != null) {
            functionValue = self["callee"]["evaluate"](self["callee"], context);
            //puts("DEBUG: functionValue=" + functionValue);
        }
        if (functionValue == null) {
            errorCaught = true;
            errorObj = RuntimeError_create(
                "Cannot call null as a function",
                //self["position"]["line"],
                //self["position"]["column"]
                self["position"]
            );
        }

        // Only continue if no error so far
        if (!errorCaught) {
            //puts("DEBUG: Preparing arguments...");
            // Evaluate arguments
            let args = [];
            let idx = 0;
            let argLen = len(self["arguments"]);
            while (idx < argLen) {
                let argNode = self["arguments"][idx];
                let argValue = argNode["evaluate"](argNode, context);
                push(args, argValue);
                idx = idx + 1;
            }
            //puts("DEBUG: arguments=" + args);
            //puts("DEBUGHARDCODE: result=" + functionValue(args)); // FIXME FIXME
            result = functionValue(args);

            /* FIXME review required ;-)
            // Call if it's CallableFunction:  Assume our CallableFunction is identified by checking map field "apply"
            if (functionValue != null && functionValue["apply"] != null) {
                puts("DEBUG: Function has apply...");

                // Try/catch function application
                let success = false;
                let caughtErr = null;
                let applyResult = null;
                {
                    // Try applying
                    let didThrow = false;
                    let thrown = null;
                    // Simulating try-catch for apply
                    let applyRet = null;
                    // The "apply" field of functionValue must be a function taking (self, args)
                    let caughtApplyError = false;
                    let caughtApplyObj = null;
                    {
                        // Try block
                        applyRet = functionValue["apply"](functionValue, args);
                    }
                    applyResult = applyRet;
                    // success
                    success = true;
                }
                // If apply succeeded
                if (success) {
                    result = applyResult;
                }
            } else {
                // Not a callable function
                errorCaught = true;
                errorObj = RuntimeError_create(
                    "Not a function: " + valueToString(functionValue),
                    //self["position"]["line"],
                    //self["position"]["column"]
                    self["position"]
                );
            }
            */

        }
    }

    // Finally: always call context.exitEvaluationDepth()
    context["exitEvaluationDepth"](context);

    // Rethrow error if there was one
    if (errorCaught) {
        // Throwing in InterpreterJ: call the throwRuntimeError function
        throwRuntimeError(errorObj);
        // To please the static analyzer
        return null;
    }

    return result;
}

// toJson function for CallExpression. Called as node["toJson"](node)
def CallExpression_toJson(self) {
    let argsJson = "";
    let argsLen = len(self["arguments"]);
    let i = 0;
    while (i < argsLen) {
        let argNode = self["arguments"][i];
        let itemJson = "null";
        if (argNode != null && argNode["toJson"] != null) {
            itemJson = argNode["toJson"](argNode);
        }
        if (i > 0) {
            argsJson = argsJson + ", ";
        }
        argsJson = argsJson + itemJson;
        i = i + 1;
    }

    let calleeJson = "null";
    if (self["callee"] != null && self["callee"]["toJson"] != null) {
        calleeJson = self["callee"]["toJson"](self["callee"]);
    }

    // No string escaping or newlines!
    return '{ "type": "CallExpression", "position": "' +
        self["position"] + '", "callee": ' + calleeJson +
        ', "arguments": [' + argsJson + '] }';
}

// Helper: value to string for non-function error message
def valueToString(val) {
    // Only handle primitive values and arrays/maps simply, for debugging
    if (val == null) {
        return "null";
    }
    if (val == true) {
        return "true";
    }
    if (val == false) {
        return "false";
    }
    // If it's a number or string
    // InterpreterJ cannot distinguish types easily; fallback to string concat
    return "" + val;
}

// RuntimeError "constructor"
def RuntimeError_create(msg, pos /* line, column */) {
    let err = {};
    err["message"] = msg;
    //err["line"] = line;
    //err["column"] = column;
    err["pos"] = pos;
    return err;
}

// Simulate "throw new RuntimeError" by calling throwRuntimeError
def throwRuntimeError(error) {
    // No real throw, just call the system error function or stop execution.
    // In InterpreterJ, you'll need to either call your interpreter's panic function,
    // or, if not possible, simply cause an invalid operation:
    panic(error); // FIXME not supported, and dummy implementation below is a bad idea
}

// Dummy panic handler for demo (replace in your engine)
def panic(error) {
    // This will forcefully stop the interpreter if used.
    // For demo purposes, print to output (remove this if not allowed):
    //puts("PANIC: " + error["message"] + " at " + error["line"] + ":" + error["column"]);
    assert(false, "PANIC: " + error["message"] + " at " + error["pos"]);
    // Infinite loop to simulate halt (remove if your engine provides built-in error/throw)
    //FIXME bad idea: while (true) {}
    
}



// IfStatement "class" - represented as a map with functions/properties manually set

// Create an IfStatement node as a map
def makeIfStatement(condition, consequence, alternative, position) {
    let node = {
        "type": "IfStatement",
        "condition": condition,
        "consequence": consequence,
        "alternative": alternative,
        "position": position
    };

    // Attach evaluate function
    node["evaluate"] = ifStatementEvaluate;
    // Attach toJson function
    node["toJson"] = ifStatementToJson;

    return node;
}

// Evaluate the IfStatement: procedural, explicit, no OO
def ifStatementEvaluate(self, context) {
    // Track this evaluation step
    //trackEvaluationStep(context); // FIXME implemented later

    let conditionResult = self["condition"]["evaluate"](self["condition"], context);

    if (EvaluatorIsTruthy(conditionResult)) {
        return self["consequence"]["evaluate"](self["consequence"], context);
    } else {
        if (self["alternative"] != null) {
            return self["alternative"]["evaluate"](self["alternative"], context);
        } else {
            return null;
        }
    }
}

// Serialize the IfStatement to json (NO escaping, strict format! No newlines in strings!)
def ifStatementToJson(self) {
    let condPart = null;
    if (self["condition"] != null) {
        condPart = self["condition"]["toJson"](self["condition"]);
    } else {
        condPart = "null";
    }

    let consPart = null;
    if (self["consequence"] != null) {
        consPart = self["consequence"]["toJson"](self["consequence"]);
    } else {
        consPart = "null";
    }

    let altPart = null;
    if (self["alternative"] != null) {
        altPart = self["alternative"]["toJson"](self["alternative"]);
    } else {
        altPart = "null";
    }

    return '{ "type": "IfStatement", "position": "' + self["position"] + '", "condition": ' + condPart + ', "consequence": ' + consPart + ', "alternative": ' + altPart + ' }';
}



// === Lexer for InterpreterJ ===
// Usage: let lexer = createLexer(inputString); ... functions below

// Factory function: returns a new lexer "struct" (map)
def createLexer(input) {
  let lexer = {};
  lexer["input"] = input;
  lexer["position"] = 0;
  lexer["readPosition"] = 0;
  lexer["ch"] = "";
  lexer["line"] = 1;
  lexer["column"] = 0;
  lexer["readChar"] = readChar;
  lexer["peekChar"] = peekChar;
  lexer["skipWhitespace"] = skipWhitespace;
  lexer["isLetter"] = isLetter;
  lexer["isDigit"] = isDigit;
  lexer["readIdentifier"] = readIdentifier;
  lexer["readNumber"] = readNumber;
  lexer["readStringLiteral"] = readStringLiteral;
  lexer["skipComments"] = skipComments;
  lexer["nextToken"] = scnnnerNextToken;
  lexer["tokenize"] = tokenize;
  // Initialize first character
  readChar(lexer);
  return lexer;
}

// Reads next character and updates position, line, column
def readChar(lexer) {
  let input = lexer["input"];
  let readPosition = lexer["readPosition"];
  if (readPosition >= len(input)) {
    lexer["ch"] = ""; // Empty string means EOF
  } else {
    lexer["ch"] = char(input, readPosition);
  }
  lexer["position"] = lexer["readPosition"];
  lexer["readPosition"] = lexer["readPosition"] + 1;
  lexer["column"] = lexer["column"] + 1;
  // Handle newlines to track line numbers & column resets
  if (lexer["ch"] == chr(10)) { // chr(10) == "\n"
    lexer["line"] = lexer["line"] + 1;
    lexer["column"] = 0;
  }
}

// Looks at next char, does NOT move position
def peekChar(lexer) {
  let input = lexer["input"];
  let readPosition = lexer["readPosition"];
  if (readPosition >= len(input)) {
    return "";
  } else {
    return char(input, readPosition);
  }
}

// Skips whitespace (space, tab, newline, carriage return)
def skipWhitespace(lexer) {
  while (
    lexer["ch"] == " " ||
    lexer["ch"] == chr(9) ||             // "\t"
    lexer["ch"] == chr(10) ||            // "\n"
    lexer["ch"] == chr(13)               // "\r"
  ) {
    readChar(lexer);
  }
}

// Skips comments (//, /* */, or #) at the lexer position
def skipComments(lexer) {
  if (lexer["ch"] == "/") {
    if (peekChar(lexer) == "/") {
      // single-line comment: skip until end of line/EOF
      while (lexer["ch"] != "" && lexer["ch"] != chr(10)) {
        readChar(lexer);
      }
      if (lexer["ch"] != "") {
        readChar(lexer);
      }
    } else {
      if (peekChar(lexer) == "*") {
        // multi-line comment: skip until */
        readChar(lexer); // skip /
        readChar(lexer); // skip *
        let ended = false;
        while (!ended && lexer["ch"] != "") {
          if (lexer["ch"] == "*" && peekChar(lexer) == "/") {
            ended = true;
            readChar(lexer); // skip *
            readChar(lexer); // skip /
          } else {
            readChar(lexer);
          }
        }
      }
    }
  } else {
    if (lexer["ch"] == "#") {
      // python-style: skip to end of line or EOF
      while (lexer["ch"] != "" && lexer["ch"] != chr(10)) {
        readChar(lexer);
      }
      if (lexer["ch"] != "") {
        readChar(lexer);
      }
    }
  }
}

// Returns true if ch is a letter or underscore
def isLetter(ch) {
  let code = ord(ch);
  if (code >= ord("a") && code <= ord("z")) {
    return true;
  }
  if (code >= ord("A") && code <= ord("Z")) {
    return true;
  }
  if (ch == "_") {
    return true;
  }
  return false;
}

// Returns true if ch is a digit
def isDigit(ch) {
  let code = ord(ch);
  if (code >= ord("0") && code <= ord("9")) {
    return true;
  }
  return false;
}

// Reads a full identifier from the current position, returns as string
def readIdentifier(lexer) {
  let input = lexer["input"];
  let start = lexer["position"];
  while (isLetter(lexer["ch"]) || isDigit(lexer["ch"])) {
    readChar(lexer);
  }
  let end = lexer["position"];
  return substr(input, start, end - start);
}

// Reads a full number (integer or float) from the current position, returns as string
def readNumber(lexer) {
  let input = lexer["input"];
  let start = lexer["position"];
  let hasDot = false;
  while (
     isDigit(lexer["ch"]) ||
     (lexer["ch"] == "." && !hasDot)
  ) {
    if (lexer["ch"] == ".") {
      hasDot = true;
    }
    readChar(lexer);
  }
  let end = lexer["position"];
  return substr(input, start, end - start);
}

// Reads a quoted string (handles '' and "") including empty string, NO ESCAPES
def readStringLiteral(lexer, quote) {
  readChar(lexer); // skip opening
  let input = lexer["input"];
  let start = lexer["position"];
  while (lexer["ch"] != "" && lexer["ch"] != quote) {
    readChar(lexer);
  }
  let end = lexer["position"];
  let strVal = substr(input, start, end - start);
  // Unterminated strings: just return up to now
  if (lexer["ch"] != "") {
    readChar(lexer); // skip closing
  }
  return strVal;
}

// Returns the next token ("consumes") and advances/updates lexer
def scnnnerNextToken(lexer) {
  let token = null;

  // Skip whitespace/comments in loop (keep retrying if something changed)
  let skipped = true;
  while (skipped) {
    let posBefore = lexer["position"];
    skipWhitespace(lexer);
    skipComments(lexer);
    if (lexer["position"] > posBefore) {
      skipped = true;
    } else {
      skipped = false;
    }
  }

  // Scan single/double-char tokens
  let ch = lexer["ch"];
  if (ch == "=") {
    if (peekChar(lexer) == "=") {
      let startColumn = lexer["column"];
      let left = ch;
      readChar(lexer);
      token = createToken(TOKEN_EQ, left + lexer["ch"], lexer["line"], startColumn);
    } else {
      token = createToken(TOKEN_ASSIGN, ch, lexer["line"], lexer["column"]);
    }
  } else {
    if (ch == "+") {
      token = createToken(TOKEN_PLUS, ch, lexer["line"], lexer["column"]);
    } else {
      if (ch == "-") {
        token = createToken(TOKEN_MINUS, ch, lexer["line"], lexer["column"]);
      } else {
        if (ch == "*") {
          token = createToken(TOKEN_ASTERISK, ch, lexer["line"], lexer["column"]);
        } else {
          if (ch == "#") {
            skipComments(lexer);
            return scnnnerNextToken(lexer);
          } else {
            if (ch == "/") {
              if (peekChar(lexer) == "/" || peekChar(lexer) == "*") {
                skipComments(lexer);
                return scnnnerNextToken(lexer);
              } else {
                token = createToken(TOKEN_SLASH, ch, lexer["line"], lexer["column"]);
              }
            } else {
              if (ch == "%") {
                token = createToken(TOKEN_PERCENT, ch, lexer["line"], lexer["column"]);
              } else {
                if (ch == "!") {
                  if (peekChar(lexer) == "=") {
                    let startColumn = lexer["column"];
                    let left = ch;
                    readChar(lexer);
                    token = createToken(TOKEN_NOT_EQ, left + lexer["ch"], lexer["line"], startColumn);
                  } else {
                    token = createToken(TOKEN_NOT, ch, lexer["line"], lexer["column"]);
                  }
                } else {
                  if (ch == "<") {
                    if (peekChar(lexer) == "=") {
                      let startColumn = lexer["column"];
                      let left = ch;
                      readChar(lexer);
                      token = createToken(TOKEN_LT_EQ, left + lexer["ch"], lexer["line"], startColumn);
                    } else {
                      token = createToken(TOKEN_LT, ch, lexer["line"], lexer["column"]);
                    }
                  } else {
                    if (ch == ">") {
                      if (peekChar(lexer) == "=") {
                        let startColumn = lexer["column"];
                        let left = ch;
                        readChar(lexer);
                        token = createToken(TOKEN_GT_EQ, left + lexer["ch"], lexer["line"], startColumn);
                      } else {
                        token = createToken(TOKEN_GT, ch, lexer["line"], lexer["column"]);
                      }
                    } else {
                      if (ch == "&") {
                        if (peekChar(lexer) == "&") {
                          let startColumn = lexer["column"];
                          let left = ch;
                          readChar(lexer);
                          token = createToken(TOKEN_AND, left + lexer["ch"], lexer["line"], startColumn);
                        } else {
                          token = createToken(TOKEN_ILLEGAL, ch, lexer["line"], lexer["column"]);
                        }
                      } else {
                        if (ch == "|") {
                          if (peekChar(lexer) == "|") {
                            let startColumn = lexer["column"];
                            let left = ch;
                            readChar(lexer);
                            token = createToken(TOKEN_OR, left + lexer["ch"], lexer["line"], startColumn);
                          } else {
                            token = createToken(TOKEN_ILLEGAL, ch, lexer["line"], lexer["column"]);
                          }
                        } else {
                          if (ch == ",") {
                            token = createToken(TOKEN_COMMA, ch, lexer["line"], lexer["column"]);
                          } else {
                            if (ch == ";") {
                              token = createToken(TOKEN_SEMICOLON, ch, lexer["line"], lexer["column"]);
                            } else {
                              if (ch == "(") {
                                token = createToken(TOKEN_LPAREN, ch, lexer["line"], lexer["column"]);
                              } else {
                                if (ch == ")") {
                                  token = createToken(TOKEN_RPAREN, ch, lexer["line"], lexer["column"]);
                                } else {
                                  if (ch == "{") {
                                    token = createToken(TOKEN_LBRACE, ch, lexer["line"], lexer["column"]);
                                  } else {
                                    if (ch == "}") {
                                      token = createToken(TOKEN_RBRACE, ch, lexer["line"], lexer["column"]);
                                    } else {
                                      if (ch == "[") {
                                        token = createToken(TOKEN_LBRACKET, ch, lexer["line"], lexer["column"]);
                                      } else {
                                        if (ch == "]") {
                                          token = createToken(TOKEN_RBRACKET, ch, lexer["line"], lexer["column"]);
                                        } else {
                                          if (ch == ":") {
                                            token = createToken(TOKEN_COLON, ch, lexer["line"], lexer["column"]);
                                          } else {
                                            // FIXME if (ch == "\"" || ch == "'") {
                                            if (ch == chr(34) || ch == "'") {  
                                              let quote = ch;
                                              let startColumn = lexer["column"];
                                              let stringVal = readStringLiteral(lexer, quote);
                                              return createToken(TOKEN_STRING, stringVal, lexer["line"], startColumn);
                                            } else {
                                              if (ch == "") {
                                                token = createToken(TOKEN_EOF, "", lexer["line"], lexer["column"]);
                                              } else {
                                                if (isLetter(ch)) {
                                                  let startColumn = lexer["column"];
                                                  let ident = readIdentifier(lexer);
                                                  let typ = lookupKeyword(ident);
                                                  return createToken(typ, ident, lexer["line"], startColumn);
                                                } else {
                                                  if (isDigit(ch)) {
                                                    let startColumn = lexer["column"];
                                                    let num = readNumber(lexer);
                                                    return createToken(TOKEN_NUMBER, num, lexer["line"], startColumn);
                                                  } else {
                                                    token = createToken(TOKEN_ILLEGAL, ch, lexer["line"], lexer["column"]);
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Always move forward if not EOF
  readChar(lexer);
  return token;
}

// Tokenize the entire input and return an array of tokens (may be empty)
def tokenize(lexer) {
  let tokens = [];
  let t = scnnnerNextToken(lexer);
  push(tokens, t);
  while (getTokenType(t) != TOKEN_EOF) {
    t = scnnnerNextToken(lexer);
    push(tokens, t);
  }
  return tokens;
}

// === END Lexer ===



// ---------- PrefixExpression "constructor" ----------
def makePrefixExpression(operator, right, position) {
    let node = {
        "type": "PrefixExpression",
        "operator": operator,
        "right": right,
        "position": position
    };
    // Attach functions explicitly
    node["evaluate"] = PrefixExpression_evaluate;
    node["toJson"] = PrefixExpression_toJson;
    return node;
}

// ---------- Evaluate Function ----------
def PrefixExpression_evaluate(self, context) {
    // "self" is the PrefixExpression node/map
    // Example of step tracking (replace with actual function if needed)
    //trackEvaluationStep(context); // FIXME implemented later

    let rightNode = self["right"];
    let rightValue = null;
    if (rightNode != null) {
        rightValue = rightNode["evaluate"](rightNode, context);
    }
    let result = Evaluator_applyPrefixOperator(self["operator"], rightValue);
    return result;
}

// ---------- toJson Function ----------
def PrefixExpression_toJson(self) {
    // Stringify child node
    let rightNode = self["right"];
    let rightJson = "null";
    if (rightNode != null) {
        rightJson = rightNode["toJson"](rightNode);
    }

    return '{ "type": "PrefixExpression", "position": "' +
        self["position"] + '", "operator": "' +
        self["operator"] + '", "right": ' +
        rightJson + ' }';
}



// Token type names as string constants

let TOKEN_EOF = "EOF";
let TOKEN_ILLEGAL = "ILLEGAL";

let TOKEN_IDENTIFIER = "IDENTIFIER";
let TOKEN_NUMBER = "NUMBER";
let TOKEN_STRING = "STRING";

let TOKEN_DEF = "DEF";
let TOKEN_LET = "LET";
let TOKEN_IF = "IF";
let TOKEN_ELSE = "ELSE";
let TOKEN_WHILE = "WHILE";
let TOKEN_RETURN = "RETURN";
let TOKEN_TRUE = "TRUE";
let TOKEN_FALSE = "FALSE";
let TOKEN_NULL = "NULL";

let TOKEN_PLUS = "PLUS";
let TOKEN_MINUS = "MINUS";
let TOKEN_ASTERISK = "ASTERISK";
let TOKEN_SLASH = "SLASH";
let TOKEN_PERCENT = "PERCENT";

let TOKEN_EQ = "EQ";
let TOKEN_NOT_EQ = "NOT_EQ";
let TOKEN_LT = "LT";
let TOKEN_GT = "GT";
let TOKEN_LT_EQ = "LT_EQ";
let TOKEN_GT_EQ = "GT_EQ";

let TOKEN_AND = "AND";
let TOKEN_OR = "OR";
let TOKEN_NOT = "NOT";

let TOKEN_ASSIGN = "ASSIGN";

let TOKEN_COMMA = "COMMA";
let TOKEN_SEMICOLON = "SEMICOLON";
let TOKEN_LPAREN = "LPAREN";
let TOKEN_RPAREN = "RPAREN";
let TOKEN_LBRACE = "LBRACE";
let TOKEN_RBRACE = "RBRACE";
let TOKEN_LBRACKET = "LBRACKET";
let TOKEN_RBRACKET = "RBRACKET";
let TOKEN_COLON = "COLON";

// Map token type -> literal (string shown in source)
let TOKEN_LITERALS = {
  "EOF": "EOF",
  "ILLEGAL": "ILLEGAL",

  "IDENTIFIER": "IDENTIFIER",
  "NUMBER": "NUMBER",
  "STRING": "STRING",

  "DEF": "DEF",
  "LET": "LET",
  "IF": "IF",
  "ELSE": "ELSE",
  "WHILE": "WHILE",
  "RETURN": "RETURN",
  "TRUE": "TRUE",
  "FALSE": "FALSE",
  "NULL": "NULL",

  "PLUS": "+",
  "MINUS": "-",
  "ASTERISK": "*",
  "SLASH": "/",
  "PERCENT": "%",

  "EQ": "==",
  "NOT_EQ": "!=",
  "LT": "<",
  "GT": ">",
  "LT_EQ": "<=",
  "GT_EQ": ">=",

  "AND": "&&",
  "OR": "||",
  "NOT": "!",

  "ASSIGN": "=",

  "COMMA": ",",
  "SEMICOLON": ";",
  "LPAREN": "(",
  "RPAREN": ")",
  "LBRACE": "{",
  "RBRACE": "}",
  "LBRACKET": "[",
  "RBRACKET": "]",
  "COLON": ":"
};

// Function: get the literal for a given token type string
def getTokenLiteral(tokenType) {
  if (TOKEN_LITERALS[tokenType] != null) {
    return TOKEN_LITERALS[tokenType];
  } else {
    return tokenType;
  }
}



// InterpreterJ port of EvaluationContext Java class
// Procedural style, manual explicit map access only, no classes, no dot notation.

// ResourceLimitType constants
let RESOURCE_LIMIT_EVALUATION_DEPTH = "EVALUATION_DEPTH";
let RESOURCE_LIMIT_LOOP_ITERATIONS = "LOOP_ITERATIONS";
let RESOURCE_LIMIT_VARIABLE_COUNT = "VARIABLE_COUNT";
let RESOURCE_LIMIT_EVALUATION_STEPS = "EVALUATION_STEPS";

// Factory to create ResourceQuota map with default values
def makeDefaultResourceQuota() {
    let rq = {};
    rq["maxEvaluationDepth"] =  655350;
    rq["maxLoopIterations"] =   655350;
    rq["maxVariableCount"] =    655350;
    rq["maxEvaluationSteps"] =  655350;
    rq["maxStringLength"] =     655350;
    return rq;
}

// Factory to create ResourceUsage map initialized to zero counts
def makeResourceUsage() {
    let ru = {};
    ru["evaluationDepth"] = 0;
    ru["loopIterations"] = 0;
    ru["variableCount"] = 0;
    ru["evaluationSteps"] = 0;
    return ru;
}

// RuntimeError generator - prints message and aborts execution via assert(false)
def raiseRuntimeError(message, line, column) {
    let fullMessage = "RuntimeError: " + message + " at " + line + ":" + column;
    puts(fullMessage);
    assert(false, fullMessage);
}

// Raise ResourceExhaustion error with reason and position info
def raiseResourceExhaustion(ctx, reason, line, column) {
    let msg = "Resource exhaustion: " + reason; // FIXME implemented later
    raiseRuntimeError(msg, line, column);
}

// Helper to get line and column number from position map or default 0,0
def getLineCol(position) { // FIXME position mess (map vs. string)
    let line = -1;
    let col = -1;
    if (position != null) {
        if (isArray(position)) {
            // position can be map with getLine() and getColumn() functions or properties
            if (position["getLine"] != null) {
                line = position["getLine"](position);
            } else {
                if (position["line"] != null) {
                    line = position["line"];
                }
            }
            if (position["getColumn"] != null) {
                col = position["getColumn"](position);
            } else {
                if (position["column"] != null) {
                    col = position["column"];
                }
            }
        }
    }
    return [line, col];
}

// Check functions to verify resource limits; throw ResourceExhaustionError if exceeded
def checkEvaluationDepth(ctx, position) {
    if (ctx["resourceUsage"]["evaluationDepth"] > ctx["resourceQuota"]["maxEvaluationDepth"]) {
        let arr = getLineCol(position);
        raiseResourceExhaustion(ctx, RESOURCE_LIMIT_EVALUATION_DEPTH, arr[0], arr[1]);
    }
}
def checkLoopIterations(ctx, position) {
    if (ctx["resourceUsage"]["loopIterations"] > ctx["resourceQuota"]["maxLoopIterations"]) {
        let arr = getLineCol(position);
        raiseResourceExhaustion(ctx, RESOURCE_LIMIT_LOOP_ITERATIONS, arr[0], arr[1]);
    }
}
def checkVariableCount(ctx, position) {
    if (ctx["resourceUsage"]["variableCount"] > ctx["resourceQuota"]["maxVariableCount"]) {
        let arr = getLineCol(position);
        raiseResourceExhaustion(ctx, RESOURCE_LIMIT_VARIABLE_COUNT, arr[0], arr[1]);
    }
}
def checkEvaluationSteps(ctx, position) {
    if (ctx["resourceUsage"]["evaluationSteps"] > ctx["resourceQuota"]["maxEvaluationSteps"]) {
        let arr = getLineCol(position);
        //FIXME raiseResourceExhaustion(ctx, RESOURCE_LIMIT_EVALUATION_STEPS, arr[0], arr[1]);
    }
}

// Helper function to check if map has key (no 'in' operator, no direct containsKey)
def mapHasKey(mapObj, key) {
    let ks = keys(mapObj);
    let i = 0;
    while (i < len(ks)) {
        if (ks[i] == key) {
            return true;
        }
        i = i + 1;
    }
    return false;
}

// Creates a new EvaluationContext map with initial values and attached functions
def makeEvaluationContext() {
    let ctx = {};

    ctx["parent"] = null;

    ctx["values"] = {};
    ctx["functions"] = {};

    ctx["resourceQuota"] = makeDefaultResourceQuota();
    ctx["resourceUsage"] = makeResourceUsage();

    // Attach methods explicitly

    ctx["define"] = ctxDefine;
    ctx["get"] = ctxGet;
    ctx["assign"] = ctxAssign;
    ctx["registerFunction"] = ctxRegisterFunction;
    ctx["extend"] = ctxExtend;

    ctx["getResourceQuota"] = ctxGetResourceQuota;
    ctx["getResourceUsage"] = ctxGetResourceUsage;
    ctx["getEvaluationDepth"] = ctxGetEvaluationDepth;

    ctx["trackLoopIteration"] = ctxTrackLoopIteration;
    ctx["trackEvaluationStep"] = ctxTrackEvaluationStep;
    ctx["trackEvaluationDepth"] = ctxTrackEvaluationDepth;
    ctx["exitEvaluationDepth"] = ctxExitEvaluationDepth;

    return ctx;
}

// Define variable in current scope with resource checks
def ctxDefine(ctx, name, value) {
    // Increase variableCount usage
    // Workaround no double array access: use temp var
    let usageMap = ctx["resourceUsage"];
    usageMap["variableCount"] = usageMap["variableCount"] + 1;
    checkVariableCount(ctx, null);

    // Check large string values to avoid memory exhaustion
    let isStringValue = false;
    if (value != null) {
        if (typeof(value) == "string") {
            isStringValue = true;
        }
    }
    if (isStringValue) {
        let strLen = len(value);
        if (strLen > ctx["resourceQuota"]["maxStringLength"]) {
            raiseResourceExhaustion(ctx, RESOURCE_LIMIT_VARIABLE_COUNT, 0, 0);
        }
    }

    // Assign the value. No double array access, so workaround:
    // Instead of ctx["values"][name] = value;
    // We'll do: temp = ctx["values"]; temp[name] = value; ctx["values"] = temp; but assignment of map is by reference.
    // But map insertion must be direct assignment, allowed:
    // BAD ctx["values"][name] = value;
    let vls = ctx["values"];
    vls[name] = value;
    return value;
}

// Get variable or function from current or parent scopes, with resource tracking and errors on not found
def ctxGet(ctx, name, position) {
    // Increment evaluation steps usage
    let usageMap = ctx["resourceUsage"];
    usageMap["evaluationSteps"] = usageMap["evaluationSteps"] + 1;
    checkEvaluationSteps(ctx, position);

    // Check current scope values
    if (mapHasKey(ctx["values"], name)) {
        return ctx["values"][name];
    }

    // Check functions map
    if (mapHasKey(ctx["functions"], name)) {
        return ctx["functions"][name];
    }

    // Recurse to parent scope if any
    if (ctx["parent"] != null) {
        return ctx["parent"]["get"](ctx["parent"], name, position);
    }

    // Not found, raise runtime error
    let arr = getLineCol(position);
    raiseRuntimeError("Undefined variable '" + name + "'", arr[0], arr[1]);
    return null; // unreachable
}

// Assign a value to a variable in current or parent scopes, with checks and errors on undefined
def ctxAssign(ctx, name, value, position) {
    // Increment evaluation steps usage
    let usageMap = ctx["resourceUsage"];
    usageMap["evaluationSteps"] = usageMap["evaluationSteps"] + 1;
    checkEvaluationSteps(ctx, position);

    // Assign in current scope if variable exists
    if (mapHasKey(ctx["values"], name)) {
        // BAD ctx["values"][name] = value;
        let vls = ctx["values"];
        vls[name] = value;
        return value;
    }

    // Otherwise recurse into parent if present
    if (ctx["parent"] != null) {
        return ctx["parent"]["assign"](ctx["parent"], name, value, position);
    }

    // Variable not found; raise error
    let arr = getLineCol(position);
    raiseRuntimeError("Cannot assign to undefined variable '" + name + "'", arr[0], arr[1]);
    return null; // unreachable
}

// Register a library function by name in current scope
def ctxRegisterFunction(ctx, name, functionObject) {
    // BAD ctx["functions"][name] = functionObject;
    let fns = ctx["functions"];
    fns[name] = functionObject;
    return functionObject;
}

// Extend current context creating child context with shared resource usage and quota and new local scopes
def ctxExtend(ctx) {
    // Check evaluation depth limit before creating new context
    let newDepth = ctx["resourceUsage"]["evaluationDepth"] + 1;
    if (newDepth > ctx["resourceQuota"]["maxEvaluationDepth"]) {
        raiseResourceExhaustion(ctx, RESOURCE_LIMIT_EVALUATION_DEPTH, 0, 0);
    }

    let child = {};

    child["parent"] = ctx;
    child["values"] = {};
    child["functions"] = {};

    // Share resource quota and usage (same references)
    child["resourceQuota"] = ctx["resourceQuota"];
    child["resourceUsage"] = ctx["resourceUsage"];

    // Increment evaluation depth for recursion protection immediately
    let usageMap = child["resourceUsage"];
    usageMap["evaluationDepth"] = usageMap["evaluationDepth"] + 1;
    checkEvaluationDepth(child, null);

    // Reattach all methods to child context same as parent
    child["define"] = ctxDefine;
    child["get"] = ctxGet;
    child["assign"] = ctxAssign;
    child["registerFunction"] = ctxRegisterFunction;
    child["extend"] = ctxExtend;

    child["getResourceQuota"] = ctxGetResourceQuota;
    child["getResourceUsage"] = ctxGetResourceUsage;
    child["getEvaluationDepth"] = ctxGetEvaluationDepth;

    child["trackLoopIteration"] = ctxTrackLoopIteration;
    child["trackEvaluationStep"] = ctxTrackEvaluationStep;
    child["trackEvaluationDepth"] = ctxTrackEvaluationDepth;
    child["exitEvaluationDepth"] = ctxExitEvaluationDepth;

    return child;
}

def extendContext(context) { //INTEGRATION
    ctxExtend(context);
}

// Return resourceQuota map for this context
def ctxGetResourceQuota(ctx) { // FIXME implemented later
    return 0; //ctx["resourceQuota"];
}

// Return resourceUsage map for this context
def ctxGetResourceUsage(ctx) { // FIXME implemented later
    return 0; //ctx["resourceUsage"];
}

// Return current evaluationDepth count from resourceUsage
def ctxGetEvaluationDepth(ctx) { // FIXME implemented later
    return 0; //ctx["resourceUsage"]["evaluationDepth"];
}

// Track and check loop iteration increments: increments loopIterations and evaluationSteps
def ctxTrackLoopIteration(ctx, position) { // FIXME implemented later
    //let usageMap = ctx["resourceUsage"];
    //usageMap["loopIterations"] = usageMap["loopIterations"] + 1;
    //checkLoopIterations(ctx, position);

    //usageMap["evaluationSteps"] = usageMap["evaluationSteps"] + 1;
    //checkEvaluationSteps(ctx, position);
}

// Track evaluation step increments
def ctxTrackEvaluationStep(ctx, position) { // FIXME implemented later
    //let usageMap = ctx["resourceUsage"];
    //usageMap["evaluationSteps"] = usageMap["evaluationSteps"] + 1;
    //checkEvaluationSteps(ctx, position);
}

// Track evaluation depth increments, with check
def ctxTrackEvaluationDepth(ctx, position) { // FIXME implemented later
    //let usageMap = ctx["resourceUsage"];
    //usageMap["evaluationDepth"] = usageMap["evaluationDepth"] + 1;
    //checkEvaluationDepth(ctx, position);
}

// Exit/decrement evaluation depth, disallow negative counts
def ctxExitEvaluationDepth(ctx) { // FIXME implemented later
    //let usageMap = ctx["resourceUsage"];
    //usageMap["evaluationDepth"] = usageMap["evaluationDepth"] - 1;
    //if (usageMap["evaluationDepth"] < 0) {
    //    usageMap["evaluationDepth"] = 0;
    //}
}

// -- TEST --



// Represents an assignment to array or map via [ ] = 
// Fields: "collection", "index", "value", "position"
// All functions are attached to node map by string key.

def makeIndexAssignmentStatement(collection, index, value, position) {
    let node = {
        "type": "IndexAssignmentStatement",
        "collection": collection,
        "index": index,
        "value": value,
        "position": position
    };
    node["evaluate"] = indexAssignmentStatement_evaluate;
    node["toJson"] = indexAssignmentStatement_toJson;
    return node;
}

// Evaluate (executes assignment) for IndexAssignmentStatement node
def indexAssignmentStatement_evaluate(self, context) {
    // Optional: track evaluation step for resource limiting
    //trackEvaluationStep(context); // FIXME implemented later

    let collectionObject = self["collection"]["evaluate"](self["collection"], context);
    let indexValue = self["index"]["evaluate"](self["index"], context);
    let valueToAssign = self["value"]["evaluate"](self["value"], context);

    // Check array
    if (isArray(collectionObject)) {
        return assignToArray(collectionObject, indexValue, valueToAssign, self["position"]);
    } else {
        if (isMap(collectionObject)) {
            return assignToMap(collectionObject, indexValue, valueToAssign, self["position"]);
        } else {
            // Not an array or map: runtime error
            throwRuntimeError("Cannot use index operator on non-collection value", self["position"]);
            return null;
        }
    }
}

// Assigns value to array at given index
def assignToArray(array, indexValue, valueToAssign, position) {
    if (!isNumber(indexValue)) {
        throwRuntimeError("Array index must be a number", position);
        return null;
    }
    let idx = int(indexValue);
    let length = len(array);
    if (idx < 0 || idx >= length) {
        throwRuntimeError("Array index out of bounds: " + idx + "", position);
        return null;
    }
    array[idx] = valueToAssign;
    return valueToAssign;
}

// Assigns value to map at given key
def assignToMap(mapObj, key, valueToAssign, position) {
    if (!(isString(key) || isNumber(key))) {
        throwRuntimeError("Map key must be a string or number", position);
        return null;
    }
    mapObj[key] = valueToAssign;
    return valueToAssign;
}

// JSON serialization for the node
def indexAssignmentStatement_toJson(self) {
    let collectionJson = "null";
    if (self["collection"] != null) {
        collectionJson = self["collection"]["toJson"](self["collection"]);
    }
    let indexJson = "null";
    if (self["index"] != null) {
        indexJson = self["index"]["toJson"](self["index"]);
    }
    let valueJson = "null";
    if (self["value"] != null) {
        valueJson = self["value"]["toJson"](self["value"]);
    }
    return '{ "type": "IndexAssignmentStatement", "position": "' + self["position"] + '", "collection": ' + collectionJson + ', "index": ' + indexJson + ', "value": ' + valueJson + ' }';
}



// InterpreterJ translation of MapLiteral Node from Java

// Assumed context: 
// - "let" always needs initializer. 
// - No OOP: everything passes around explicit maps and arrays, never 'this' or dot notation.
// - "position" is assumed to be a field inside the node maps (e.g., node["position"])
// - No function expressions, only def name() { ... }
// - attach functions to node maps with explicit assignment: node["evaluate"] = ...; etc.

// Create a MapLiteral node with explicit "pairs" and "position"
def makeMapLiteral(pairs, position) {
  let node = {};
  node["type"] = "MapLiteral";
  node["pairs"] = pairs;      // pairs: array of {"key": Node, "value": Node} pairs, see below
  node["position"] = position;

  // Attach evaluate function
  def evaluate(self,context) {
    // Track evaluation step to avoid CPU runaway
    //trackEvaluationStep(context); // FIXME implemented later

    // mapValues will be built as a map with string/number keys only
    let mapValues = {};

    let pairsArr = node["pairs"];
    let i = 0;
    while (i < len(pairsArr)) {
      let pair = pairsArr[i];
      // pair must have {"key": Node, "value": Node}
      let keyNode = pair["key"];
      let valueNode = pair["value"];

      // Evaluate the key
      let key = keyNode["evaluate"](keyNode, context);

      // Validate key type: only string or number allowed
      let keyIsString = false;
      let keyIsNumber = false;
      // Basic runtime type check; assuming typeofString() and typeofNumber() provided by stdlib, or use custom logic
      if (typeof(key) == "string") {
        keyIsString = true;
      } else {
        if (typeof(key) == "number") {
          keyIsNumber = true;
        }
      }
      if (!(keyIsString || keyIsNumber)) {
        // Error: Map keys must be string or number
        // Raise a runtime error (assume RuntimeError constructor: def RuntimeError(msg, line, col))
        let msg = "Map keys must be strings or numbers, got: ";
        if (key == null) {
          msg = msg + "null";
        } else {
          msg = msg + typeof(key);
        }
        // Error expects source position; use node["position"]["line"], node["position"]["column"]
        throw(RuntimeError(msg, node["position"]["line"], node["position"]["column"]));
      }

      // Evaluate the value
      let value = valueNode["evaluate"](valueNode,context);

      mapValues[key] = value;

      i = i + 1;
    }

    return mapValues;
  }
  node["evaluate"] = evaluate;

  // Attach toJson function
  def toJson(self) {
    // Produce a string representation of the map literal including all pairs as JSON-like output
    let pairsArr = node["pairs"];
    
    //puts("MapLiteral.toJson: pairsArr=" + pairsArr); //DEBUG
    
    let pairsJsonArray = [];
    let i = 0;
    while (i < len(pairsArr)) {
      let pair = pairsArr[i];
      let keyNode = pair["key"];
      let valueNode = pair["value"];

      // Convert key/value nodes to JSON
      let keyJson = "null";
      if (keyNode != null) {
        keyJson = keyNode["toJson"](keyNode);
      }
      let valueJson = "null";
      if (valueNode != null) {
        valueJson = valueNode["toJson"](valueNode);
      }

      // Append as string: { "key": <keyJson>, "value": <valueJson> }
      let pairJson = '{ "key": ' + keyJson + ', "value": ' + valueJson + ' }';
      push(pairsJsonArray, pairJson);
      i = i + 1;
    }

    // Join comma-separated
    let pairsJson = "";
    i = 0;
    while (i < len(pairsJsonArray)) {
      if (i > 0) {
        pairsJson = pairsJson + ", ";
      }
      pairsJson = pairsJson + pairsJsonArray[i];
      i = i + 1;
    }

    // Build main object
    let result = '{ "type": "MapLiteral", "position": "' + node["position"] + '", "pairs": [ ' + pairsJson + ' ] }';
    return result;
  }
  node["toJson"] = toJson;

  return node;
}



// Program node - the root of every AST

// Create Program node as a map literal with methods attached manually

def makeProgram() {
    let program = {
        "type": "Program",
        "statements": []
    };

    def addStatement(self, statement) {
        if (statement != null) {
            push(program["statements"], statement);
        }
    }
    program["addStatement"] = addStatement;

    //FIXME not supported: program["getStatements"] = def() {
    def getStatements() {
        return program["statements"];
    }
    program["getStatements"] = getStatements;

    // context must be a map holding runtime state, and trackEvaluationStep must be passed in or globally accessible
    def evaluate(self, context) {
        // Track this evaluation step to prevent CPU exhaustion
        //trackEvaluationStep(context, {}); // FIXME position? // FIXME implemented later

        let result = null;
        let stmts = program["statements"];
        let n = len(stmts);
        let i = 0;
        while (i < n) {
            let statement = stmts[i];
            result = statement["evaluate"](statement,context);
            
            // Early return if we hit a ReturnValue (represented as a map with type "ReturnValue")
            //if (result != null) {
            //    //if (isArray(result)) { if (result["type"] == "ReturnValue") { // FIXME we need to agree whether eval returns a map with a key value or directly a value
            //    //    return result["value"]; // FIXME return support
            //    //} }
            //}

            if (isReturnValue(result)) {
                return result["value"];
            }

            i = i + 1;
        }
        return result;
    }
    program["evaluate"] = evaluate;

    // toJson method
    def toJson(self) {
        let stmts = program["statements"];
        let parts = [];
        let n = len(stmts);
        let i = 0;
        while (i < n) {
            push(parts, stmts[i]["toJson"](stmts[i]));
            i = i + 1;
        }
        // Join with ",\n" (not a real newline inside the string, just comma and backslash-n as two characters)
        let sep = ","; // FIXME ",\n";
        let out = "";
        let j = 0;
        while (j < len(parts)) {
            if (j == 0) {
                out = parts[j];
            } else {
                out = out + sep + parts[j];
            }
            j = j + 1;
        }
        return '{ "type": "Program", "statements": [ ' + out + ' ] }';
    }
    program["toJson"] = toJson;

    return program;
}



// Utility: create VariableDeclaration AST node as a map
def makeVariableDeclaration(name, initializer, position) {
    let node = {
        "type": "VariableDeclaration",
        "position": position,
        "name": name,
        "initializer": initializer
    }
    node["evaluate"] = evaluateVariableDeclaration
    node["toJson"] = variableDeclarationToJson
    return node
}

// Actual evaluation logic
def evaluateVariableDeclaration(self, context) {
    // Check if initializer exists, otherwise use null
    let init = self["initializer"]
    let value = null
    if (init != null) {
        value = init["evaluate"](init, context)
    } else {
        value = null
    }
    // context["define"](name, value)
    return context["define"](context, self["name"], value)
}

// JSON representation
def variableDeclarationToJson(self) {
    let result = '{ "type": "VariableDeclaration", "position": "' + self["position"] + '", "name": "' + self["name"] + '", "initializer": '
    let init = self["initializer"]
    if (init != null) {
        result = result + init["toJson"](init)
    } else {
        result = result + 'null'
    }
    result = result + ' }'
    return result
}



// Interpreter.s - InterpreterJ port of the Interpreter Java class

//def trackEvaluationStep(context, position) { // FIXME implemented later
//    context["trackEvaluationStep"](context["trackEvaluationStep"], position);
//}

// Interpreter map holding state and library initializers
def makeInterpreter() {
    let interpreter = {};
    interpreter["ast"] = null;
    interpreter["libraryFunctionInitializers"] = [];
    interpreter["resourceQuota"] = makeDefaultResourceQuota();

    // Constructor with all default library function initializers
    def initWithDefaultLibraries(self) {
        self["libraryFunctionInitializers"] = [
            DefaultLibraryFunctionsInitializer,
            StdIOLibraryFunctionsInitializer,
            MapLibraryFunctionsInitializer,
            ArrayLibraryFunctionsInitializer,
            StringLibraryFunctionsInitializer,
            RegexLibraryFunctionsInitializer,
            TypeLibraryFunctionsInitializer
        ];
        self["resourceQuota"] = makeDefaultResourceQuota();
    }

    // Init with ResourceQuota and library initializers
    def initWithQuotaAndLibraries(self, resourceQuota, initializers) {
        if (resourceQuota != null) {
            self["resourceQuota"] = resourceQuota;
        } else {
            self["resourceQuota"] = makeDefaultResourceQuota();
        }
        if (initializers != null) {
            self["libraryFunctionInitializers"] = initializers;
        } else {
            self["libraryFunctionInitializers"] = [];
        }
    }

    // Register built-in library functions into the context
    def registerBuiltInFunctions(self, context) {
        let i = 0;
        while (i < len(self["libraryFunctionInitializers"])) {
            let initFn = self["libraryFunctionInitializers"][i];
            // Call the initializer function with context argument
            initFn(context);
            i = i + 1;
        }
    }
    interpreter["registerBuiltInFunctions"] = registerBuiltInFunctions;

    // Parse source code string and produce ParseResult map
    def parse(self, sourceCode) {
        // Error list for catching errors
        let errors = [];

        let resultAst = null;
        let hasError = false;

        let lexer = null;
        let parser = null;

        {
            // Simulated try block
            lexer = createLexer(sourceCode);
            parser = initParser(lexer);

            resultAst = parseProgram();

            // Collect errors from parser
            let parseErrors = getErrors();
            let i = 0;
            while (i < len(parseErrors)) {
                let err = parseErrors[i];
                // Create Error map: { message, line, column }
                let errMap = {};
                errMap["message"] = err["message"];
                errMap["line"] = err["line"];
                errMap["column"] = err["column"];
                push(errors, errMap);
                i = i + 1;
            }
        }

        // If parseAst is null or errors present, mark fail
        if (resultAst == null || len(errors) > 0) {
            hasError = true;
        }

        // Update interpreter AST state
        self["ast"] = resultAst;

        // Prepare ParseResult map
        let parseResult = {};
        parseResult["success"] = !hasError;
        parseResult["ast"] = resultAst;
        parseResult["errors"] = errors;
        return parseResult;
    }
    interpreter["parse"] = parse;

    // Evaluate stored AST and produce EvaluationResult map
    def evaluate(self) {
        let errors = [];

        // Check if ast is null
        if (self["ast"] == null) {
            // Create error: No AST to evaluate
            let err = {};
            err["message"] = "No AST to evaluate. Parse code first.";
            err["line"] = 0;
            err["column"] = 0;
            push(errors, err);
            let evalResultMap = {};
            evalResultMap["success"] = false;
            evalResultMap["result"] = null;
            evalResultMap["errors"] = errors;
            return evalResultMap;
        }

        // Step 1: Prepare context with resource quota
        let context = makeEvaluationContext();

        context["resourceQuota"] = self["resourceQuota"];

        // Prepare fresh resource usage map (to avoid keeping old usage accidentally)
        let usageMap = makeResourceUsage();
        context["resourceUsage"] = usageMap;

        // Step 2: Register built-in functions
        self["registerBuiltInFunctions"](self, context);

        // Step 3: Evaluate ast
        let success = true;
        let result = null;

        // Manual simulated try-catch not supported, assume evaluation either succeeds or aborts program
        {
            result = self["ast"]["evaluate"](self["ast"], context);
        }

        // Prepare evaluation result map
        let evalResultMap = {};
        evalResultMap["success"] = true;
        evalResultMap["result"] = result;
        evalResultMap["errors"] = [];

        return evalResultMap;
    }
    interpreter["evaluate"] = evaluate;

    // Return AST JSON string or null if no AST parsed
    def getAstJson(self) {
        if (self["ast"] == null) {
            return null;
        }
        return self["ast"]["toJson"](self["ast"]);
    }
    interpreter["getAstJson"] = getAstJson;

    // Format errors array as multiline string
    def formatErrors(errorsList) {
        if (len(errorsList) == 0) {
            return "No errors";
        }

        let result = "";
        let i = 0;
        while (i < len(errorsList)) {
            let err = errorsList[i];
            // Format: "Error at line:column: message"
            let s = "Error at " + err["line"] + ":" + err["column"] + ": " + err["message"];
            if (i == 0) {
                result = s;
            } else {
                result = result + "\n" + s;
            }
            i = i + 1;
        }
        return result;
    }

    // Get resource quota map
    def getResourceQuota(self) {
        return self["resourceQuota"];
    }

    // Set resource quota map
    def setResourceQuota(self, resourceQuota) {
        self["resourceQuota"] = resourceQuota;
    }

    // Initialize interpreter with default library initializers on creation
    initWithDefaultLibraries(interpreter);

    return interpreter;
}

// DefaultLibraryFunctionsInitializer 

def zeroWrapper(f) {
    def wrapped(args) {
        f();
    }
    wrapped;
}

def oneWrapper(f) {
    def wrapped(args) {
        f(args[0]);
    }
    wrapped;
}

def twoWrapper(f) {
    def wrapped(args) {
        f(args[0],args[1]);
    }
    wrapped;
}

def threeWrapper(f) {
    def wrapped(args) {
        f(args[0],args[1],args[2]);
    }
    wrapped;
}

def DefaultLibraryFunctionsInitializer(context) {
    context["registerFunction"](context, "random", zeroWrapper(random));
    context["registerFunction"](context, "assert", twoWrapper(assert));
    context["registerFunction"](context, "echo", oneWrapper(echo));
    context["registerFunction"](context, "int", oneWrapper(int));
    context["registerFunction"](context, "double", oneWrapper(double));
    context["registerFunction"](context, "string", oneWrapper(string));
}

// StdIOLibraryFunctionsInitializer implementation with puts and gets simulation

def StdIOLibraryFunctionsInitializer(context) {
    context["registerFunction"](context, "gets", zeroWrapper(gets));
    context["registerFunction"](context, "puts", oneWrapper(puts));
}

// MapLibraryFunctionsInitializer stub

def MapLibraryFunctionsInitializer(context) {
    context["registerFunction"](context, "keys", oneWrapper(keys));
    context["registerFunction"](context, "values", oneWrapper(values));
}

// ArrayLibraryFunctionsInitializer stub

def ArrayLibraryFunctionsInitializer(context) {
    context["registerFunction"](context, "push", twoWrapper(push));
    context["registerFunction"](context, "pop", oneWrapper(pop));
    context["registerFunction"](context, "len", oneWrapper(len));
    context["registerFunction"](context, "delete", twoWrapper(delete));
}

// StringLibraryFunctionsInitializer stub

def StringLibraryFunctionsInitializer(context) {
    context["registerFunction"](context, "char", twoWrapper(char));
    context["registerFunction"](context, "ord", oneWrapper(ord));
    context["registerFunction"](context, "chr", oneWrapper(chr));
    context["registerFunction"](context, "substr", threeWrapper(substr));
    context["registerFunction"](context, "startsWith", twoWrapper(startsWith));
    context["registerFunction"](context, "endsWith", twoWrapper(endsWith));
    context["registerFunction"](context, "trim", oneWrapper(trim));
    context["registerFunction"](context, "join", twoWrapper(join));
}

// RegexLibraryFunctionsInitializer stub

def RegexLibraryFunctionsInitializer(context) {
    context["registerFunction"](context, "match", twoWrapper(match));
    context["registerFunction"](context, "findAll", twoWrapper(findAll));
    context["registerFunction"](context, "replace", threeWrapper(replace));
    context["registerFunction"](context, "split", twoWrapper(split));
}

// TypeLibraryFunctionsInitializer stub

def TypeLibraryFunctionsInitializer(context) {
    // Implementation of (), (), (), (), (), (), (), () can be added here if desired

    context["registerFunction"](context, "typeof", oneWrapper(typeof));
    context["registerFunction"](context, "isNumber", oneWrapper(isNumber));
    context["registerFunction"](context, "isString", oneWrapper(isString));
    context["registerFunction"](context, "isBoolean", oneWrapper(isBoolean));
    context["registerFunction"](context, "isArray", oneWrapper(isArray));
    context["registerFunction"](context, "isMap", oneWrapper(isMap));
    context["registerFunction"](context, "isFunction", oneWrapper(isFunction));
    context["registerFunction"](context, "isNull", oneWrapper(isNull));
}

//TEST

// Create an Interpreter instance, parse some code, evaluate, and output results

let interpreter = makeInterpreter();

def newlinehack(line) {
    let lines = split(line,"<NEWLINE/>"); // FIXME SUPERHACK ;-)
    //puts("Split:" + line + " = " +lines);
    let i = 0;
    line = "";
    while (i < len(lines)) {
        if (i > 0) {
            line = line + chr(10);
        }
        line = line + lines[i];
        i = i + 1;
    }
    return line;
}

//let source = "let x = 1 + 2; let y = 10; x * y;";
def readSources() {
    let source = null;
    let line = gets();
    if (line != null) {
        source = newlinehack(line);
    }
    else {
        source = "puts('Hello World ' + 3 / (1 + 2));";
    }
    if (source == "//multiline") {
        let line = gets();
        while (line != null) {
            if (line == "//<EOF>") {
                return source;
            }
            line = newlinehack(line);
            source = source + chr(10) + line;
            line = gets();
        }
    }
    //puts("Hack:" + source); //DEBUG
    source;
}

//puts("DEBUG: interpreter is ready"); //DEBUG

let source = readSources();

let parseResult = interpreter["parse"](interpreter, source);

if (!(parseResult["success"])) {
    puts("Parse failed with errors: " + interpreter["formatErrors"](interpreter, parseResult["errors"]));
} else {
    //puts("Parse succeeded.");
    let evalResult = interpreter["evaluate"](interpreter);
    if (evalResult["success"]) {
        //puts("Evaluate succeeded. Result: " + "" + evalResult["result"]);
        let r = evalResult["result"];
        if (r != null) {
            puts("" + r);
        }
    } else {
        puts("Evaluation failed with errors: " + interpreter["formatErrors"](interpreter, evalResult["errors"]));
    }
}

let astJson = interpreter["getAstJson"](interpreter);
if (astJson != null) {
    //puts("AST JSON: " + astJson);
} else {
    //puts("No AST to show.");
}

assert(interpreter != null, "Interpreter instance should not be null");
//<EOF>
