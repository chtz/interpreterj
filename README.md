# InterpreterJ

InterpreterJ is a simple yet powerful interpreted scripting language implemented in Java. It combines the simplicity of JavaScript-like syntax with powerful features like first-class functions, closures, and block scoping.

## Table of Contents
1. [Overview](#overview)
2. [Language Guide](#language-guide)
   - [Basic Syntax and Data Types](#basic-syntax-and-data-types)
   - [Variables and Assignments](#variables-and-assignments)
   - [Operators](#operators)
   - [Control Flow](#control-flow)
   - [Functions and Closures](#functions-and-closures)
   - [Arrays](#arrays)
   - [Maps/Dictionaries](#maps-dictionaries)
   - [Comments](#comments)
3. [Built-in Functions](#built-in-functions)
4. [Language Grammar (EBNF)](#language-grammar-ebnf)
5. [Resource Quotas and Security](#resource-quotas-and-security)
6. [Java API Usage](#java-api-usage)
7. [Examples](#examples)
   - [Simple Calculator](#simple-calculator)
   - [Array Sorting](#array-sorting)
   - [Functional Programming](#functional-programming)

## Overview

InterpreterJ is a dynamically-typed language designed for simplicity and ease of embedding in Java applications:

- **Simple Syntax**: Easy to learn JavaScript-like syntax
- **First-class Functions**: Functions as values, supporting closures
- **Block Scoping**: Variables are scoped to their containing block
- **Arrays**: Native array support with built-in utility functions
- **Maps/Dictionaries**: Native support for key-value collections
- **Resource Control**: Built-in protection against infinite loops and recursion
- **Easy Embedding**: Simple Java API to embed in your applications

## Language Guide

### Basic Syntax and Data Types

InterpreterJ supports the following primitive data types:

```script
// Numbers (internally stored as double precision floating point)
let integer = 42;
let decimal = 3.14159;
let result = integer + decimal;
puts(result);  // Output: 45.14159

// Strings (with single or double quotes)
let greeting = "Hello";
let name = 'World';
let message = greeting + ", " + name + "!";
puts(message);  // Output: Hello, World!

// Booleans
let isActive = true;
let isComplete = false;
puts(isActive);  // Output: true

// Null
let empty = null;
// puts(empty);  // Output: null // FIXME throws exception
```

<sup><sub>Script Output (generated)</sub></sup>
```output
45.14159
Hello, World!
true
```


### Variables and Assignments

Variables are declared using the `let` keyword and follow block scoping rules:

```script
// Variable declaration and assignment
let x = 10;
x = x + 5;  // Reassignment
puts(x);  // Output: 15.0

// Block scoping demonstration
let a = 5;
{
  let a = 10;  // Different variable that shadows outer 'a'
  let b = 15;  // Only accessible within this block
  puts(a);     // Output: 10.0
}
puts(a);       // Output: 5.0 (outer 'a' is unchanged)
// puts(b);    // Error: Undefined variable 'b'
```

<sup><sub>Script Output (generated)</sub></sup>
```output
15.0
10.0
5.0
```


### Operators

InterpreterJ supports a variety of operators:

```script
// Arithmetic operators
let a = 10;
let b = 3;
puts(a + b);  // Addition: 13.0
puts(a - b);  // Subtraction: 7.0
puts(a * b);  // Multiplication: 30.0
puts(a / b);  // Division: 3.3333333333333335
puts(a % b);  // Modulo: 1.0

// Comparison operators
puts(a == b);  // Equal to: false
puts(a != b);  // Not equal to: true
puts(a > b);   // Greater than: true
puts(a < b);   // Less than: false
puts(a >= b);  // Greater than or equal to: true
puts(a <= b);  // Less than or equal to: false

// Logical operators
let c = true;
let d = false;
puts(c && d);  // Logical AND: false
puts(c || d);  // Logical OR: true
puts(!c);      // Logical NOT: false

// String concatenation
let str1 = "Hello";
let str2 = "World";
puts(str1 + " " + str2);  // Output: Hello World
```

<sup><sub>Script Output (generated)</sub></sup>
```output
13.0
7.0
30.0
3.3333333333333335
1.0
false
true
true
false
true
false
false
true
false
Hello World
```


### Control Flow

InterpreterJ provides standard control flow constructs:

```script
// If statement
let temperature = 75;
if (temperature > 80) {
  puts("It's hot outside!");
} else { if (temperature > 60) { // FIXME else if support
  puts("It's pleasant outside!");  // This will execute
} else {
  puts("It's cold outside!");
} }

// While loop
let counter = 1;
let factorial = 1;
while (counter <= 5) {
  factorial = factorial * counter;
  counter = counter + 1;
}
puts("5! = " + factorial);  // Output: 5! = 120.0

// Early return from loops using if-return
def findItem(arr, target) {
  let i = 0;
  while (i < len(arr)) {
    if (arr[i] == target) {
      return i;  // Early return when item is found
    }
    i = i + 1;
  }
  return -1;  // Not found
}

let items = [10, 20, 30, 40, 50];
let index = findItem(items, 30);
puts("Found at index: " + index);  // Output: Found at index: 2.0
```

<sup><sub>Script Output (generated)</sub></sup>
```output
It's pleasant outside!
5! = 120.0
Found at index: 2.0
```


### Functions and Closures

Functions are first-class citizens and support closures:

```script
// Basic function declaration and call
def greet(name) {
  return "Hello, " + name + "!";
}
puts(greet("Alice"));  // Output: Hello, Alice!

// Functions without return statements
def printSum(a, b) {
  let result = a + b;
  puts(result);
}
printSum(3, 4);  // Output: 7.0

// Functions as arguments
def applyFunction(func, value) {
  return func(value);
}

def double(x) {
  return x * 2;
}

def square(x) {
  return x * x;
}

puts(applyFunction(double, 5));  // Output: 10.0
puts(applyFunction(square, 5));  // Output: 25.0

// Closures - functions that capture their environment
def createCounter() {
  let count = 0;
  
  def counter() {
    count = count + 1;
    return count;
  }
  
  return counter;
}

let counter1 = createCounter();
let counter2 = createCounter();

puts(counter1());  // Output: 1.0
puts(counter1());  // Output: 2.0
puts(counter2());  // Output: 1.0 (separate instance)
```

<sup><sub>Script Output (generated)</sub></sup>
```output
Hello, Alice!
7.0
10.0
25.0
1.0
2.0
1.0
```


### Arrays

Arrays are dynamic collections that can store values of any type:

```script
// Array creation and access
let numbers = [10, 20, 30, 40, 50];
puts(numbers[0]);  // Output: 10.0
puts(numbers[4]);  // Output: 50.0

// Mixed type arrays
let mixed = [1, "two", true, null];
puts(mixed[1]);  // Output: two

// Array length
puts(len(numbers));  // Output: 5.0

// Modifying array elements
numbers[2] = 99;
puts(numbers[2]);  // Output: 99.0

// Array operations
push(numbers, 60);            // Add element to end
puts(numbers[5]);             // Output: 60.0
puts(len(numbers));           // Output: 6.0

let poppedValue = pop(numbers);  // Remove last element
puts(poppedValue);           // Output: 60.0
puts(len(numbers));          // Output: 5.0

delete(numbers, 0);          // Remove element at index 0
puts(numbers[0]);            // Output: 20.0
puts(len(numbers));          // Output: 4.0

// Array concatenation
let moreNumbers = [60, 70];
let combined = numbers + moreNumbers;
puts(len(combined));         // Output: 6.0

// Array iteration
let sum = 0;
let i = 0;
while (i < len(numbers)) {
  sum = sum + numbers[i];
  i = i + 1;
}
puts("Sum: " + sum);         // Output value depends on previous operations
```

<sup><sub>Script Output (generated)</sub></sup>
```output
10.0
50.0
two
5.0
99.0
60.0
6.0
60.0
5.0
20.0
4.0
6.0
Sum: 209.0
```


### Maps/Dictionaries

Maps (also known as dictionaries) are collections of key-value pairs where keys can be strings or numbers:

```script
// Map creation
let emptyMap = {};
let person = {"name": "Alice", "age": 30, "isActive": true};
puts(person["name"]);  // Output: Alice

// Mixed key types
let mixed = {"a": 1, 2: "b", 3: true};
puts(mixed["a"]);  // Output: 1.0
puts(mixed[2]);    // Output: b

// Map length
puts(len(person));  // Output: 3.0

// Modifying map elements
person["age"] = 31;
puts(person["age"]);  // Output: 31.0

// Adding new key-value pairs
person["email"] = "alice@example.com";
puts(len(person));  // Output: 4.0

// Getting keys and values
let keys = keys(person);    // Returns an array of keys
let values = values(person);  // Returns an array of values

puts(len(keys));    // Output: 4.0
puts(values[0]);    // Output depends on order, one of the values

// Deleting entries
delete(person, "email");
puts(len(person));  // Output: 3.0

// Accessing non-existent key returns null
// puts(person["email"]);  // Output: null // FIXME throws exception

// Nested maps and arrays
let complex = {
  "user": {"id": 123, "name": "Bob"},
  "items": [1, 2, 3]
};

puts(complex["user"]["name"]);  // Output: Bob
puts(complex["items"][1]);      // Output: 2.0
```

<sup><sub>Script Output (generated)</sub></sup>
```output
Alice
1.0
b
3.0
31.0
4.0
4.0
Alice
3.0
Bob
2.0
```


### Comments

InterpreterJ supports two types of comments:

```script
// This is a single-line comment

/* This is a 
   multi-line 
   comment */

let x = 10;  // Comments can appear at the end of a line
puts(x);     // Output: 10.0
```

<sup><sub>Script Output (generated)</sub></sup>
```output
10.0
```


## Built-in Functions

InterpreterJ provides several built-in functions for common operations:

```script
// Output function
puts("Hello, World!");  // Prints to standard output with newline

// Input function
// let userInput = gets();  // Reads a line from standard input

// Type conversion
let strNumber = "42";
let number = int(strNumber);  // Converts string to integer
puts(number);  // Output: 42.0

// Echo function (returns its argument unchanged)
let value = echo(123);
puts(value);  // Output: 123.0

// Array functions
let arr = [1, 2, 3];
puts(len(arr));          // Get array length: 3.0

push(arr, 4);            // Add element to end: [1, 2, 3, 4]
puts(len(arr));          // Output: 4.0

let last = pop(arr);     // Remove and return last element
puts(last);              // Output: 4.0
puts(len(arr));          // Output: 3.0

delete(arr, 0);          // Remove element at index 0
puts(arr[0]);            // Output: 2.0
puts(len(arr));          // Output: 2.0

// Map functions
let map = {"a": 1, "b": 2, "c": 3};
puts(len(map));           // Get map size: 3.0

let mapKeys = keys(map);  // Get array of keys
puts(mapKeys[0]);         // Output: a (order may vary)

let mapValues = values(map);  // Get array of values
puts(mapValues[0]);       // Output: 1.0 (order may vary)

delete(map, "b");         // Remove entry with key "b"
puts(len(map));           // Output: 2.0

// String functions
let str = "Hello World";
puts(char(str, 0));       // Get character at index: H
puts(substr(str, 0, 5));  // Get substring: Hello
puts(trim("  text  "));   // Trim whitespace: text

// Character conversion
puts(ord("A"));           // Get ASCII code of a character: 65.0
puts(chr(66));            // Convert ASCII code to character: B

// String checks
puts(startsWith(str, "Hello"));  // Check if string starts with prefix: true
puts(endsWith(str, "World"));    // Check if string ends with suffix: true

// Array to string conversion
let items = ["apple", "banana", "cherry"];
puts(join(items, ", "));  // Join array elements: apple, banana, cherry

// Regular expression functions
let text = "hello123world456";

// Match entire string against pattern
puts(match(text, "^[a-z]+\\d+[a-z]+\\d+$"));  // true

// Find all matches
let matches = findAll(text, "\\d+");  // Returns ["123", "456"]
puts(matches[0]);  // 123

// Replace patterns
puts(replace(text, "\\d+", "X"));  // helloXworldX

// Split string by delimiter
let parts = split("a,b,c", ",");
puts(len(parts));  // 3.0

// Type checking functions
puts(typeof(42));         // number
puts(typeof("hello"));    // string
puts(typeof(true));       // boolean
puts(typeof(null));       // null
puts(typeof([1, 2, 3]));  // array
puts(typeof({"a": 1}));   // map
puts(typeof(echo));       // function

// Type checking predicates
puts(isNumber(42));       // true
puts(isString("hello"));  // true
puts(isBoolean(true));    // true
puts(isArray([1, 2, 3])); // true
puts(isMap({"a": 1}));    // true
puts(isFunction(echo));   // true
puts(isNull(null));       // true
```

<sup><sub>Script Output (generated)</sub></sup>
```output
Hello, World!
42
123.0
3.0
4.0
4.0
3.0
2.0
2.0
3.0
a
1.0
2.0
H
Hello
text
65.0
B
true
true
apple, banana, cherry
true
123
helloXworldX
3.0
number
string
boolean
null
array
map
function
true
true
true
true
true
true
true
```


## Language Grammar (EBNF)

The following EBNF (Extended Backus-Naur Form) grammar defines the syntax of InterpreterJ:

```ebnf
/* Program Structure */
Program             ::= Statement*

/* Statements */
Statement           ::= VariableDeclaration
                      | FunctionDeclaration
                      | IfStatement
                      | WhileStatement
                      | ReturnStatement
                      | BlockStatement
                      | AssignmentStatement
                      | IndexAssignmentStatement
                      | ExpressionStatement

/* Declarations */
VariableDeclaration ::= "let" Identifier "=" Expression (";" | <newline>)?
FunctionDeclaration ::= "def" Identifier "(" Parameters? ")" BlockStatement
Parameters          ::= Identifier ("," Identifier)*

/* Control Flow */
IfStatement         ::= "if" "(" Expression ")" BlockStatement 
                       ("else" (IfStatement | BlockStatement))?
WhileStatement      ::= "while" "(" Expression ")" BlockStatement
ReturnStatement     ::= "return" Expression? (";" | <newline>)?

/* Block and Assignment */
BlockStatement           ::= "{" Statement* "}"
AssignmentStatement      ::= Identifier "=" Expression (";" | <newline>)?
IndexAssignmentStatement ::= Identifier "[" Expression "]" "=" Expression (";" | <newline>)?
ExpressionStatement      ::= Expression (";" | <newline>)?

/* Expressions */
Expression         ::= OrExpression
OrExpression       ::= AndExpression ("||" AndExpression)*
AndExpression      ::= EqualityExpression ("&&" EqualityExpression)*
EqualityExpression ::= ComparisonExpression (("==" | "!=") ComparisonExpression)*
ComparisonExpression ::= AdditiveExpression (("<" | ">" | "<=" | ">=") AdditiveExpression)*
AdditiveExpression ::= MultiplicativeExpression (("+" | "-") MultiplicativeExpression)*
MultiplicativeExpression ::= PrefixExpression (("*" | "/" | "%") PrefixExpression)*
PrefixExpression   ::= ("-" | "!") PrefixExpression | CallExpression
CallExpression     ::= IndexExpression ("(" Arguments? ")")*
IndexExpression    ::= PrimaryExpression ("[" Expression "]")*
Arguments          ::= Expression ("," Expression)*
PrimaryExpression  ::= Identifier 
                      | NumberLiteral 
                      | StringLiteral
                      | BooleanLiteral
                      | NullLiteral
                      | ArrayLiteral
                      | MapLiteral
                      | "(" Expression ")"

/* Literals and Identifiers */
Identifier         ::= [a-zA-Z_][a-zA-Z0-9_]*
NumberLiteral      ::= Digit+ ("." Digit+)?
Digit              ::= [0-9]
StringLiteral      ::= '"' [^"]* '"' | "'" [^']* "'"
BooleanLiteral     ::= "true" | "false"
NullLiteral        ::= "null"
ArrayLiteral       ::= "[" (Expression ("," Expression)*)? "]"
MapLiteral         ::= "{" (MapEntry ("," MapEntry)*)? "}"
MapEntry           ::= Expression ":" Expression

/* Comments */
Comment            ::= SingleLineComment | MultiLineComment
SingleLineComment  ::= "//" [^\n]*
MultiLineComment   ::= "/*" .* "*/"
```

## Resource Quotas and Security

InterpreterJ includes built-in protection against resource abuse:

```java
// Create a custom resource quota
ResourceQuota quota = new ResourceQuota(
    500,    // maxEvaluationDepth - prevents excessive recursion
    10000,  // maxLoopIterations - prevents infinite loops
    1000,   // maxVariableCount - prevents memory exhaustion
    100000  // maxEvaluationSteps - limits CPU usage
);

// Create an interpreter with the custom quota
Interpreter interpreter = new Interpreter(quota);
```

The protection mechanisms include:

1. **Recursion Limit**: Prevents stack overflow from excessive function recursion
```script
   // This would trigger an error if recursion depth exceeds the limit
   def recursiveFunction(n) {
     if (n <= 0) { return 0; }
     return recursiveFunction(n - 1); 
   }
   recursiveFunction(10);
```

<sup><sub>Script Result (generated)</sub></sup>
```result
0.0
```


2. **Loop Iteration Limit**: Prevents infinite loops
```script
   // This would trigger an error if iterations exceed the limit
   let i = 0;
   while (true) {
     i = i + 1;
     if (i > 100) { puts("done"); return; } 
   }
```

<sup><sub>Script Output (generated)</sub></sup>
```output
done
```


3. **Variable Count Limit**: Prevents memory exhaustion
```script
   // Creating too many variables would trigger an error
   let i = 0;
   while (i < 10) {
     let newVar = i;  // Each iteration creates a new variable
     i = i + 1;
   }
```

<sup><sub>Script Result (generated)</sub></sup>
```result
10.0
```


4. **Evaluation Steps Limit**: Prevents CPU exhaustion
```script
   // Complex operations may trigger an error if they take too many steps
   let result = 1;
   let i = 0;
   while (i < 1000) {
     result = result * i;
     i = i + 1;
   }
```

<sup><sub>Script Output (generated)</sub></sup>
```output
Eval error: Error at 0:0: Maximum call stack depth exceeded

```


## Java API Usage

Here's how to use InterpreterJ in a Java application:

```java
import interpreter.main.Interpreter;
import interpreter.runtime.ResourceQuota;

public class Example {
    public static void main(String[] args) {
        // Create a new interpreter instance
        Interpreter interpreter = new Interpreter();
        
        // Create code to execute
        String code = "def factorial(n) {\n" +
                      "  if (n <= 1) { return 1; }\n" +
                      "  return n * factorial(n - 1);\n" +
                      "}\n\n" +
                      "factorial(5);";
        
        // Parse the code into an AST
        Interpreter.ParseResult parseResult = interpreter.parse(code);
        
        if (parseResult.isSuccess()) {
            // Execute the code if parsing succeeded
            Interpreter.EvaluationResult evalResult = interpreter.evaluate();
            
            if (evalResult.isSuccess()) {
                System.out.println("Result: " + evalResult.getResult());
                // Output: Result: 120.0
            } else {
                System.out.println("Runtime error: " + 
                    Interpreter.formatErrors(evalResult.getErrors()));
            }
        } else {
            System.out.println("Parse error: " + 
                Interpreter.formatErrors(parseResult.getErrors()));
        }
        
        // Using custom resource quotas
        ResourceQuota quota = new ResourceQuota(
            100,   // maxEvaluationDepth
            5000,  // maxLoopIterations
            500,   // maxVariableCount
            10000  // maxEvaluationSteps
        );
        
        Interpreter safeInterpreter = new Interpreter(quota);
        // Use safeInterpreter in the same way...
    }
}
```

## Examples

### Simple Calculator

```script
// A simple calculator with basic operations
def calculate(a, b, operation) {
  if (operation == "+") {
    return a + b;
  } else { if (operation == "-") {
    return a - b;
  } else { if (operation == "*") {
    return a * b;
  } else { if (operation == "/") {
    if (b == 0) {
      return "Error: Division by zero";
    }
    return a / b;
  } else {
    return "Error: Unknown operation";
  } } } }
}

// Test different operations
puts(calculate(15, 5, "+"));  // Output: 20.0
puts(calculate(15, 5, "-"));  // Output: 10.0
puts(calculate(15, 5, "*"));  // Output: 75.0
puts(calculate(15, 5, "/"));  // Output: 3.0
puts(calculate(15, 0, "/"));  // Output: Error: Division by zero
```

<sup><sub>Script Output (generated)</sub></sup>
```output
20.0
10.0
75.0
3.0
Error: Division by zero
```


### Array Sorting

```script
// Simple bubble sort implementation
def bubbleSort(arr) {
  let n = len(arr);
  let i = 0;
  
  while (i < n) {
    let j = 0;
    let swapped = false;
    
    while (j < n - i - 1) {
      if (arr[j] > arr[j + 1]) {
        // Swap elements
        let temp = arr[j];
        arr[j] = arr[j + 1];
        arr[j + 1] = temp;
        swapped = true;
      }
      j = j + 1;
    }
    
    // If no swapping occurred in this pass, array is sorted
    if (!swapped) {
      return arr; // FIXME support break;
    }
    
    i = i + 1;
  }
  
  return arr;
}

// Test the bubble sort
let numbers = [64, 34, 25, 12, 22, 11, 90];
bubbleSort(numbers);

// Print sorted array
let i = 0;
let output = "Sorted array: ";
while (i < len(numbers)) {
  output = output + numbers[i];
  if (i < len(numbers) - 1) {
    output = output + ", ";
  }
  i = i + 1;
}
puts(output);  // Output: Sorted array: 11, 12, 22, 25, 34, 64, 90
```

<sup><sub>Script Output (generated)</sub></sup>
```output
Parse error: Error at 15:7: Expected next token to be RBRACKET, got PLUS instead
Error at 15:12: No prefix parse function for ASSIGN (=)

```


### Functional Programming

```script
// Map function: apply a function to each element in an array
def map(arr, fn) {
  let result = [];
  let i = 0;
  while (i < len(arr)) {
    push(result, fn(arr[i]));
    i = i + 1;
  }
  return result;
}

// Filter function: select elements that satisfy a predicate
def filter(arr, predicate) {
  let result = [];
  let i = 0;
  while (i < len(arr)) {
    if (predicate(arr[i])) {
      push(result, arr[i]);
    }
    i = i + 1;
  }
  return result;
}

// Reduce function: combine elements into a single value
def reduce(arr, fn, initial) {
  let result = initial;
  let i = 0;
  while (i < len(arr)) {
    result = fn(result, arr[i]);
    i = i + 1;
  }
  return result;
}

// Test data
let numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

// Double each number with map
def double(x) { return x * 2; }
let doubled = map(numbers, double);
puts("Doubled: " + doubled[0] + ", " + doubled[1] + ", " + doubled[2] + "...");  
// Output: Doubled: 2, 4, 6...

// Get even numbers with filter
def isEven(x) { return x % 2 == 0; }
let evens = filter(numbers, isEven);
puts("Evens: " + evens[0] + ", " + evens[1] + ", " + evens[2] + "...");  
// Output: Evens: 2, 4, 6...

// Sum all numbers with reduce
def add(acc, x) { return acc + x; }
let sum = reduce(numbers, add, 0);
puts("Sum: " + sum);  // Output: Sum: 55.0

// Chain operations: Sum of doubled even numbers
let result = reduce(
  map(
    filter(numbers, isEven),
    double
  ),
  add,
  0
);
puts("Sum of doubled evens: " + result);  // Output: Sum of doubled evens: 60.0
```

<sup><sub>Script Output (generated)</sub></sup>
```output
Doubled: 2.0, 4.0, 6.0...
Evens: 2.0, 4.0, 6.0...
Sum: 55.0
Sum of doubled evens: 60.0
```


