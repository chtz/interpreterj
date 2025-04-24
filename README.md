# InterpreterJ

InterpreterJ is a simple yet powerful interpreted scripting language implemented in Java. It supports functions, closures, variable declarations, and control flow structures.

## Language Introduction

InterpreterJ is a dynamically-typed language with syntax similar to JavaScript. Below is an introduction to its main features.

### Basic Syntax and Data Types

```script
// Numbers
let x = 42;        // Integer
let pi = 3.14159;  // Floating point

// Strings (single or double quotes)
let name = "John";
let greeting = 'Hello';

// Booleans
let isActive = true;
let isComplete = false;

// Null
let empty = null;

// Basic operators
let sum = 10 + 5;       // 15
let diff = 10 - 5;      // 5
let product = 10 * 5;   // 50
let quotient = 10 / 5;  // 2
let remainder = 10 % 3; // 1

// String concatenation
let fullGreeting = greeting + ", " + name; // "Hello, John"
```

<sup><sub>Script Result (generated)</sub></sup>
```result
Hello, John
```


### Variable Scoping

Variables are block-scoped, meaning they're only accessible within the block they're defined in:

```script
let x = 10;
{
  let x = 20;  // Different variable, only exists in this block
  let y = 30;  // Only exists in this block
  puts(x);     // Outputs: 20
}
puts(x);       // Outputs: 10
// puts(y);    // Error: Undefined variable 'y'
```

<sup><sub>Script Output (generated)</sub></sup>
```output
20.0
10.0
```


### Control Flow

InterpreterJ supports standard control flow constructs:

```script
// If statements
let num = 42;
if (num > 50) {
  puts("Number is greater than 50");
} else {
  puts("Number is 50 or less");
}

// While loops
let i = 0;
let sum = 0;
while (i < 5) {
  sum = sum + i;
  i = i + 1;
}
puts(sum);  // Outputs: 10 (0+1+2+3+4)
```

<sup><sub>Script Output (generated)</sub></sup>
```output
Number is 50 or less
10.0
```


### Functions

Functions are first-class citizens and support closures:

```script
// Basic function
def add(a, b) {
  return a + b;
}
puts(add(5, 3));  // Outputs: 8

// Functions can be used as values
def createMultiplier(factor) {
  def multiply(n) {
    return n * factor;
  }
  return multiply;
}

let double = createMultiplier(2);
let triple = createMultiplier(3);
puts(double(5));  // Outputs: 10
puts(triple(5));  // Outputs: 15
```

<sup><sub>Script Output (generated)</sub></sup>
```output
8.0
10.0
15.0
```


### Closures and State

Functions can access and modify variables from their enclosing scope:

```script
def createCounter() {
  let count = 0;
  def increment() {
    count = count + 1;
    return count;
  }
  return increment;
}

let counter = createCounter();
puts(counter());  // Outputs: 1
puts(counter());  // Outputs: 2
puts(counter());  // Outputs: 3
```

<sup><sub>Script Output (generated)</sub></sup>
```output
1.0
2.0
3.0
```


### Expressions and Operator Precedence

InterpreterJ follows standard operator precedence rules:

```script
// Arithmetic precedence
puts(5 + 3 * 2);     // Outputs: 11 (5 + (3 * 2))
puts((5 + 3) * 2);   // Outputs: 16 ((5 + 3) * 2)

// Logical operators
puts(true && false || true);  // Outputs: true ((true && false) || true)
puts(true && (false || true)); // Outputs: true (true && (false || true))

// Comparison operators
puts(3 + 4 > 5 && 10 % 3 == 1); // Outputs: true ((3 + 4 > 5) && (10 % 3 == 1))
```

<sup><sub>Script Output (generated)</sub></sup>
```output
11.0
16.0
true
true
true
```


### Nested Functions and Recursion

Functions can be nested and recursive:

```script
// Recursive factorial function
def factorial(n) {
  if (n <= 1) {
    return 1;
  }
  return n * factorial(n - 1);
}

puts(factorial(5));  // Outputs: 120 (5 * 4 * 3 * 2 * 1)

// Nested functions
def outer(x) {
  def middle(y) {
    def inner(z) {
      return x + y + z;
    }
    return inner;
  }
  return middle;
}

let f1 = outer(1);
let f2 = f1(2);
puts(f2(3));  // Outputs: 6 (1 + 2 + 3)
```

<sup><sub>Script Output (generated)</sub></sup>
```output
120.0
6.0
```


### Error Handling

The interpreter detects and reports various errors:

```script
// Undefined variable
// x + 5;  // Error: Undefined variable 'x'

// Reassigning undefined variable
// x = 10;  // Error: Cannot assign to undefined variable 'x'

// Syntax error
// def foo { return 5; }  // Error: Expected '(' after 'def foo'
```

## Language Grammar (EBNF)

This section describes the grammar of the InterpreterJ language in Extended Backus-Naur Form (EBNF).

```ebnf
/* Program Structure */
Program           ::= Statement*

/* Statements */
Statement         ::= VariableDeclaration
                    | FunctionDeclaration
                    | IfStatement
                    | WhileStatement
                    | ReturnStatement
                    | BlockStatement
                    | AssignmentStatement
                    | ExpressionStatement

/* Declarations */
VariableDeclaration ::= "let" Identifier "=" Expression (";" | <newline>)?
FunctionDeclaration ::= "def" Identifier "(" Parameters? ")" BlockStatement
Parameters         ::= Identifier ("," Identifier)*

/* Control Flow */
IfStatement        ::= "if" "(" Expression ")" BlockStatement ("else" BlockStatement)?
WhileStatement     ::= "while" "(" Expression ")" BlockStatement
ReturnStatement    ::= "return" Expression? (";" | <newline>)?

/* Block and Assignment */
BlockStatement     ::= "{" Statement* "}"
AssignmentStatement ::= Identifier "=" Expression (";" | <newline>)?
ExpressionStatement ::= Expression (";" | <newline>)?

/* Expressions */
Expression         ::= OrExpression
OrExpression       ::= AndExpression ("||" AndExpression)*
AndExpression      ::= EqualityExpression ("&&" EqualityExpression)*
EqualityExpression ::= ComparisonExpression (("==" | "!=") ComparisonExpression)*
ComparisonExpression ::= AdditiveExpression (("<" | ">" | "<=" | ">=") AdditiveExpression)*
AdditiveExpression ::= MultiplicativeExpression (("+" | "-") MultiplicativeExpression)*
MultiplicativeExpression ::= PrefixExpression (("*" | "/" | "%") PrefixExpression)*
PrefixExpression   ::= ("-" | "!") PrefixExpression | CallExpression
CallExpression     ::= PrimaryExpression ("(" Arguments? ")")*
Arguments          ::= Expression ("," Expression)*
PrimaryExpression  ::= Identifier 
                    | NumberLiteral 
                    | StringLiteral
                    | BooleanLiteral
                    | NullLiteral
                    | "(" Expression ")"

/* Literals and Identifiers */
Identifier         ::= [a-zA-Z_][a-zA-Z0-9_]*
NumberLiteral      ::= [0-9]+ ("." [0-9]+)?
StringLiteral      ::= '"' [^"]* '"' | "'" [^']* "'"
BooleanLiteral     ::= "true" | "false"
NullLiteral        ::= "null"
```

## Language Features Summary

InterpreterJ includes:

1. **Variables**: Declared with `let` keyword and featuring block-level scoping
2. **Functions**: Defined with `def` keyword, supporting parameters and return values
3. **Closures**: Functions can capture variables from enclosing scopes
4. **Control Flow**: `if`/`else` statements and `while` loops
5. **Data Types**:
   - Numbers (floating point)
   - Strings (with single or double quotes)
   - Booleans (`true` and `false`)
   - `null` value
6. **Operators**:
   - Arithmetic: `+`, `-`, `*`, `/`, `%`
   - Comparison: `==`, `!=`, `<`, `>`, `<=`, `>=`
   - Logical: `&&`, `||`, `!`
7. **Comments**:
   - Single-line: `// comment`
   - Multi-line: `/* comment */`
