package interpreter.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import interpreter.main.Interpreter.Error;
import interpreter.runtime.CallableFunction;
import interpreter.runtime.EvaluationContext;

/**
 * Test suite for the Interpreter
 */
public class InterpreterTest {
	@Test
    @DisplayName("Test calling script function as java function")
	public void testProgramAsFunction() {
		Interpreter interpreter = new Interpreter();
		interpreter.parse("def quadruple(x) { return 4 * x; }");
		
		CallableFunction quadruple = (CallableFunction) interpreter.evaluate().getResult();
		
		// All numbers in this interpreter are represented as double values, hence the .0 suffix
		assertEquals("88.0", quadruple.apply(List.of(22)).toString());
		assertEquals("44.0", quadruple.apply(List.of(11)).toString()); 
	}
	
	@Test
    @DisplayName("Test parse once run twice")
    public void testParseOnceRunTwice() {
		Interpreter interpreter = new Interpreter();
		interpreter.parse("def createCounter() {\n" +
	            "  let count = 0;\n" +
	            "  def increment() {\n" +
	            "    count = count + 1;\n" +
	            "    return count;\n" +
	            "  }\n" +
	            "  return increment;\n" +
	            "}\n" +
	            "let counter = createCounter();\n" +
	            "let x = counter();\n" +  // 1
	            "let y = counter();\n" +  // 2
	            "let z = counter();\n" +  // 3
	            "x + y + z;");
		
		assertEquals("6.0", interpreter.evaluate().getResult().toString());
		assertEquals("6.0", interpreter.evaluate().getResult().toString());
    }
	
	@Test
    @DisplayName("Test custom library functions")
    public void testCustomLibraryFunctions() {
		assertProgram(
	            "let x = counter();\n" +
	            "let y = counter();\n" +
	            "let z = counter();\n" +
	            "x + y + z;",
	            "6.0"
	        , ctx -> {
	        	final AtomicInteger i = new AtomicInteger(0);
	            ctx.registerFunction("counter", args -> i.incrementAndGet());	
	        });
    }
	
	@Test
    @DisplayName("Test closures")
    public void testClosures() {
        assertProgram(
            "def createCounter() {\n" +
            "  let count = 0;\n" +
            "  def increment() {\n" +
            "    count = count + 1;\n" +
            "    return count;\n" +
            "  }\n" +
            "  return increment;\n" +
            "}\n" +
            "let counter = createCounter();\n" +
            "let x = counter();\n" +  // 1
            "let y = counter();\n" +  // 2
            "let z = counter();\n" +  // 3
            "x + y + z;",
            "6.0"
        );
    }
	
    @Test
    @DisplayName("Test simple expressions")
    public void testSimpleExpressions() {
        assertExpression("5 + 3 * 2", "11.0");
        assertExpression("2 * (5 + 3)", "16.0");
        assertExpression("5 > 3", "true");
        assertExpression("5 < 3", "false");
        assertExpression("5 == 5", "true");
        assertExpression("5 != 5", "false");
        assertExpression("true && false", "false");
        assertExpression("true || false", "true");
        assertExpression("!true", "false");
        assertExpression("-5", "-5.0");
    }
    
    @Test
    @DisplayName("Test variable declarations and assignments")
    public void testVariableDeclarations() {
        assertProgram(
            "let x = 10;\n" +
            "let y = 20;\n" +
            "x + y;",
            "30.0"
        );
    }
    
    @Test
    @DisplayName("Test if statements with true condition")
    public void testIfStatementTrue() {
        assertProgram(
            "let x = 10;\n" +
            "let y = 20;\n" +
            "if (x < y) { x = x + 1; } else { y = y + 1; }\n" +
            "x;",
            "11.0"
        );
    }
    
    @Test
    @DisplayName("Test if statements with false condition")
    public void testIfStatementFalse() {
        assertProgram(
            "let x = 30;\n" +
            "let y = 20;\n" +
            "if (x < y) { x = x + 1; } else { y = y + 1; }\n" +
            "y;",
            "21.0"
        );
    }
    
    @Test
    @DisplayName("Test while loops")
    public void testWhileLoops() {
        assertProgram(
            "let i = 0;\n" +
            "let sum = 0;\n" +
            "while (i < 5) {\n" +
            "  sum = sum + i;\n" +
            "  i = i + 1;\n" +
            "}\n" +
            "sum;",
            "10.0"
        );
    }
    
    @Test
    @DisplayName("Test functions")
    public void testFunctions() {
        assertProgram(
            "def add(a, b) {\n" +
            "  return a + b;\n" +
            "}\n" +
            "add(5, 3);",
            "8.0"
        );
    }
    
    @Test
    @DisplayName("Test recursive functions")
    public void testRecursiveFunctions() {
        assertProgram(
            "def factorial(n) {\n" +
            "  if (n <= 1) {\n" +
            "    return 1;\n" +
            "  }\n" +
            "  return n * factorial(n - 1);\n" +
            "}\n" +
            "echo(factorial(5));",
            "120.0"
        );
    }
    
    @Test
    @DisplayName("Test complex program with functions and variables")
    public void testComplexProgram() {
        assertProgram(
            "let x = 10;\n" +
            "let y = 20;\n" +
            "let sum = x + y;\n" +
            "def max(a, b) {\n" +
            "  if (a > b) {\n" +
            "    return a;\n" +
            "  } else {\n" +
            "    return b;\n" +
            "  }\n" +
            "}\n" +
            "let maximum = max(x, y);\n" +
            "sum + maximum;",
            "50.0"
        );
    }
    
    @Test
    @DisplayName("Test nested scopes and variable shadowing")
    public void testNestedScopesAndShadowing() {
        assertProgram(
            "let x = 10;\n" +
            "let result = 0;\n" +
            "def outer() {\n" +
            "  let x = 20;\n" +
            "  def inner() {\n" +
            "    let x = 30;\n" +
            "    return x;\n" +
            "  }\n" +
            "  return inner() + x;\n" +
            "}\n" +
            "outer() + x;",  // (30 + 20) + 10 = 60
            "60.0"
        );
    }
    
    @Test
    @DisplayName("Test string literals and operations")
    public void testStringOperations() {
        assertExpression("\"Hello, world!\"", "Hello, world!");
        assertExpression("'Single quoted'", "Single quoted");
    }
    
    @Test
    @DisplayName("Test null literal")
    public void testNullLiteral() {
        assertExpression("null", "null");
        assertProgram(
            "let x = null;\n" +
            "x == null;",
            "true"
        );
    }
        
    @Test
    @DisplayName("Test runtime error for undefined variable")
    public void testUndefinedVariableError() {
        assertRuntimeError(
            "let x = 10;\n" +
            "y + 5;",
            "Undefined variable 'y'"
        );
    }
    
    @Test
    @DisplayName("Test parse error for syntax mistake")
    public void testSyntaxError() {
        assertParseError(
            "def foo { return 5; }", // Missing parentheses in function declaration
            "Expected next token to be LPAREN"
        );
    }
    
    @Test
    @DisplayName("Test modulo operation")
    public void testModuloOperation() {
        assertExpression("10 % 3", "1.0");
    }
    
    @Test
    @DisplayName("Test multiple return paths in function")
    public void testMultipleReturnPaths() {
        assertProgram(
            "def testFunc(x) {\n" +
            "  if (x > 0) {\n" +
            "    return x * 10;\n" +
            "  } else {\n" +
            "    if (x < 0) {\n" +
            "      return x * -5;\n" +
            "    } else {\n" +
            "      return 0;\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "testFunc(5) + testFunc(-3) + testFunc(0);",
            "65.0"  // (5*10) + (-3*-5) + 0 = 50 + 15 + 0 = 65
        );
    }
    
    @Test
    @DisplayName("Test complex expressions and operator precedence")
    public void testOperatorPrecedence() {
        assertExpression("5 + 3 * 2", "11.0");  // 5 + (3 * 2) = 5 + 6 = 11
        assertExpression("(5 + 3) * 2", "16.0");  // (5 + 3) * 2 = 8 * 2 = 16
        assertExpression("2 * 3 + 4 * 5", "26.0");  // (2 * 3) + (4 * 5) = 6 + 20 = 26
        assertExpression("2 + 3 > 4 + 5", "false");  // (2 + 3) > (4 + 5) = 5 > 9 = false
        assertExpression("true && false || true", "true");  // (true && false) || true = false || true = true
        assertExpression("true && (false || true)", "true");  // true && (false || true) = true && true = true
        assertExpression("3 + 4 > 5 && 10 % 3 == 1", "true");  // (3 + 4 > 5) && (10 % 3 == 1) = true && true = true
    }
    
    @Test
    @DisplayName("Test comments in code")
    public void testComments() {
        assertProgram(
            "// This is a single-line comment\n\n" +
            "let x = 10; // Comment after statement\n" +
            "/* This is a\n" +
            "   multi-line comment */\n" +
            "let y = 20;\n" +
            "x + y;",
            "30.0"
        );
    }
    
    @Test
    @DisplayName("Test nested control flow")
    public void testNestedControlFlow() {
        assertProgram(
            "let result = 0;\n" +
            "let i = 0;\n" +
            "while (i < 3) {\n" +
            "  let j = 0;\n" +
            "  while (j < 3) {\n" +
            "    if (i == j || i + j == 2) {\n" +
            "      result = result + (i * j);\n" +
            "    }\n" +
            "    j = j + 1;\n" +
            "  }\n" +
            "  i = i + 1;\n" +
            "}\n" +
            "result;",  // (0,0)=0 + (0,2)=0 + (1,1)=1 + (2,0)=0 + (2,2)=4 = 5
            "5.0"
        );
    }
    
    @Test
    @DisplayName("Test empty return statement")
    public void testEmptyReturn() {
        assertProgram(
            "def emptyReturn() {\n" +
            "  return;\n" +
            "}\n" +
            "emptyReturn();",
            "null"  // Empty return should return null
        );
    }
    
    @Test
    @DisplayName("Test lexer with all token types")
    public void testLexerAllTokens() {
        assertProgram(
            "// This is a comment\n" +
            "let x = 10 + 20 - 5 * 2 / 1;\n" +
            "let y = (x > 20) && (x < 50);\n" +
            "let z = (x != 30) || (x == 30);\n" +
            "let str = \"hello\";\n" +
            "let empty = null;\n" +
            "x;",
            "20.0"
        );
    }
    
    @Test
    @DisplayName("Test function with missing closing brace")
    public void testFunctionWithMissingBrace() {
        assertParseError(
            "def add(a, b) {\n" +
            "  return a + b;\n" +
            // Missing closing brace
            "add(1, 2);",
            "Expected '}'"
        );
    }
    
    @Test
    @DisplayName("Test function with too many arguments - ignored extra arguments")
    public void testFunctionWithTooManyArguments() {
        assertProgram(
            "def add(a, b) {\n" +
            "  return a + b;\n" +
            "}\n" +
            "add(1, 2, 3);",  // Too many arguments, but extra ones are ignored
            "3.0"  // 1 + 2 = 3
        );
    }
    
    @Test
    @DisplayName("Test function with too few arguments - missing args as null/undefined")
    public void testFunctionWithTooFewArguments() {
        assertProgram(
            "def add(a, b) {\n" +
            "  if (b == null) {\n" +
            "    return a;\n" +
            "  }\n" +
            "  return a + b;\n" +
            "}\n" +
            "add(5);",  // Too few arguments, missing argument treated as null
            "5.0"  // Expects return a (which is 5)
        );
    }
    
    @Test
    @DisplayName("Test recursive function with deep recursion")
    public void testDeepRecursion() {
        assertProgram(
            "def sum(n) {\n" +
            "  if (n <= 0) {\n" +
            "    return 0;\n" +
            "  }\n" +
            "  return n + sum(n - 1);\n" +
            "}\n" +
            "sum(10);",  // Sum of numbers 1-10
            "55.0"
        );
    }
    
    @Test
    @DisplayName("Test empty program")
    public void testEmptyProgram() {
        assertProgram("", "null");
    }
    
    @Test
    @DisplayName("Test consecutive statements")
    public void testConsecutiveStatements() {
        assertProgram(
            "let x = 10; let y = 20; x + y;",
            "30.0"
        );
    }
    
    @Test
    @DisplayName("Test function as first-class values")
    public void testFunctionAsValues() {
        assertProgram(
            "def createAdder(x) {\n" +
            "  def adder(y) {\n" +
            "    return x + y;\n" +
            "  }\n" +
            "  return adder;\n" +
            "}\n" +
            "let add5 = createAdder(5);\n" +
            "add5(10);",
            "15.0"
        );
    }
    
    @Test
    @DisplayName("Test string concatenation")
    public void testStringConcatenation() {
        assertProgram(
            "let firstName = 'John';\n" +
            "let lastName = 'Doe';\n" +
            "firstName + ' ' + lastName;",
            "John Doe"
        );
    }
    
    @Test
    @DisplayName("Test boolean conversion in conditions")
    public void testBooleanConversions() {
        assertProgram(
            "let result = '';\n" +
            "// These should all be truthy\n" +
            "if (true) { result = result + 'a'; }\n" +
            "if (1) { result = result + 'b'; }\n" +
            // These should all be falsy
            "if (false) { result = result + 'x'; }\n" +
            "if (0) { result = result + 'y'; }\n" +
            "if (null) { result = result + 'z'; }\n" +
            "result;",
            "ab"  // Only truthy conditions execute
        );
    }
    
    @Test
    @DisplayName("Test nested function definitions")
    public void testNestedFunctionDefinitions() {
        assertProgram(
            "def outer(x) {\n" +
            "  def middle(y) {\n" +
            "    def inner(z) {\n" +
            "      return x + y + z;\n" +
            "    }\n" +
            "    return inner;\n" +
            "  }\n" +
            "  return middle;\n" +
            "}\n" +
            "let f1 = outer(1);\n" +
            "let f2 = f1(2);\n" +
            "f2(3);",
            "6.0"  // 1 + 2 + 3 = 6
        );
    }
    
    @Test
    @DisplayName("Test error for reassigning to undefined variable")
    public void testReassignUndefinedVariable() {
        assertRuntimeError(
            "x = 10;",  // x is not defined
            "undefined variable 'x'"
        );
    }
    
    @Test
    @DisplayName("Test error for invalid token")
    public void testInvalidToken() {
        assertParseError(
            "let x = @;", // @ is not a valid token
            "No prefix parse function for ILLEGAL"
        );
    }
    
    @Test
    @DisplayName("Test block-scoped variables")
    public void testBlockScopedVariables() {
        // Test basic block scoping - outer variable should remain unchanged
        assertProgram(
            "let x = 10;\n" +
            "{\n" +
            "  let x = 20;\n" +
            "  let y = 30;\n" +
            "}\n" +
            "// x should still be 10, y should be undefined\n" +
            "x;",
            "10.0"
        );
        
        // Test that variables defined in blocks are not accessible outside
        assertRuntimeError(
            "let x = 10;\n" +
            "{\n" +
            "  let y = 20;\n" +
            "}\n" +
            "y;", // y should not be accessible here
            "Undefined variable 'y'"
        );
        
        // Test nested blocks
        assertProgram(
            "let x = 10;\n" +
            "let result = 0;\n" +
            "{\n" +
            "  let x = 20;\n" +
            "  result = result + x;\n" +
            "  {\n" +
            "    let x = 30;\n" +
            "    result = result + x;\n" +
            "  }\n" +
            "  result = result + x;\n" +
            "}\n" +
            "result + x;", // Should be (0+20+30+20)+10 = 80
            "80.0"
        );
    }
    
    private void assertExpression(String expression, String expected) {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(expression);
        
        assertTrue(parseResult.isSuccess(),
            "Parse error in expression: " + expression + "\n" + formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        
        assertTrue(evalResult.isSuccess(),
            "Evaluation error in expression: " + expression + "\n" + formatErrors(evalResult.getErrors()));
        
        String actual = String.valueOf(evalResult.getResult());
        assertEquals(expected, actual, "Expression: " + expression);
    }
    
    private void assertProgram(String program, String expected) {
    	assertProgram(program, expected, null);
    }
    
    @SuppressWarnings("unchecked")
	private void assertProgram(String program, String expected, Consumer<EvaluationContext> optionalAlternativeLibraryFunctionInitializer) {
    	Interpreter interpreter;
    	if (optionalAlternativeLibraryFunctionInitializer != null) {
    		interpreter = new Interpreter(optionalAlternativeLibraryFunctionInitializer);
    	}
    	else {
    		interpreter = new Interpreter();
    	}
        Interpreter.ParseResult parseResult = interpreter.parse(program);
        
        assertTrue(parseResult.isSuccess(),
            "Parse error in program:\n" + program + "\n" + formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        
        assertTrue(evalResult.isSuccess(),
            "Evaluation error in program:\n" + program + "\n" + formatErrors(evalResult.getErrors()));
        
        String actual = String.valueOf(evalResult.getResult());
        assertEquals(expected, actual, "Program evaluation result mismatch");
    }
    
    private void assertRuntimeError(String program, String errorMessage) {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(program);
        
        assertTrue(parseResult.isSuccess(),
            "Parse error in program:\n" + program + "\n" + formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        
        assertFalse(evalResult.isSuccess(),
            "Expected runtime error but evaluation succeeded: " + program);
        
        String errorString = formatErrors(evalResult.getErrors());
        assertTrue(errorString.contains(errorMessage),
            "Expected error message to contain: " + errorMessage + "\nActual error: " + errorString);
    }
    
    private void assertParseError(String program, String errorMessage) {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(program);
        
        assertFalse(parseResult.isSuccess(),
            "Expected parse error but parsing succeeded: " + program);
        
        String errorString = formatErrors(parseResult.getErrors());
        assertTrue(errorString.contains(errorMessage),
            "Expected error message to contain: " + errorMessage + "\nActual error: " + errorString);
    }
    
    private String formatErrors(List<Error> errors) {
        return Interpreter.formatErrors(errors);
    }
} 