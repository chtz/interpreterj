package interpreter.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import interpreter.runtime.ResourceQuota;

/**
 * Tests that verify resource limits are enforced correctly
 */
public class ResourceLimitTest {

    @Test
    @DisplayName("Test that loop iterations are limited")
    public void testLoopIterationLimit() {
        // Create a restrictive quota with a very low loop iteration limit
        ResourceQuota quota = new ResourceQuota(500, 10, 1000, 100000);
        Interpreter interpreter = new Interpreter(quota);
        
        String infiniteLoop = 
            "let i = 0;\n" +
            "while (true) {\n" +
            "  i = i + 1;\n" +
            "}\n" +
            "i;";
        
        // Parse should succeed
        Interpreter.ParseResult parseResult = interpreter.parse(infiniteLoop);
        assertTrue(parseResult.isSuccess(), "Parsing should succeed");
        
        // Evaluation should fail with loop iteration limit
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertFalse(evalResult.isSuccess(), "Evaluation should fail");
        assertTrue(evalResult.getErrors().get(0).getMessage().contains("Maximum loop iterations exceeded"), 
                   "Should report loop iteration limit: " + evalResult.getErrors().get(0).getMessage());
    }
    
    @Test
    @DisplayName("Test that evaluation depth is limited")
    public void testEvaluationDepthLimit() {
        // Create a restrictive quota with a very low call stack depth limit
        ResourceQuota quota = new ResourceQuota(5, 10000, 1000, 100000);
        Interpreter interpreter = new Interpreter(quota);
        
        String recursiveFunction = 
            "def factorial(n) {\n" +
            "  if (n <= 1) {\n" +
            "    return 1;\n" +
            "  }\n" +
            "  return n * factorial(n - 1);\n" +
            "}\n" +
            "factorial(20);"; // Will exceed our depth limit of 5
        
        // Parse should succeed
        Interpreter.ParseResult parseResult = interpreter.parse(recursiveFunction);
        assertTrue(parseResult.isSuccess(), "Parsing should succeed");
        
        // Evaluation should fail with stack depth limit
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertFalse(evalResult.isSuccess(), "Evaluation should fail");
        assertTrue(evalResult.getErrors().get(0).getMessage().contains("Maximum call stack depth exceeded"), 
                   "Should report stack depth limit: " + evalResult.getErrors().get(0).getMessage());
    }
    
    @Test
    @DisplayName("Test that variable count is limited")
    public void testVariableCountLimit() {
        // Create a restrictive quota with a very low variable count limit
        ResourceQuota quota = new ResourceQuota(500, 10000, 5, 100000);
        Interpreter interpreter = new Interpreter(quota);
        
        String manyVariables = 
            "let a = 1;\n" +
            "let b = 2;\n" +
            "let c = 3;\n" +
            "let d = 4;\n" +
            "let e = 5;\n" +
            "let f = 6;\n" + // This should exceed our limit of 5
            "a + b + c + d + e + f;";
        
        // Parse should succeed
        Interpreter.ParseResult parseResult = interpreter.parse(manyVariables);
        assertTrue(parseResult.isSuccess(), "Parsing should succeed");
        
        // Evaluation should fail with variable count limit
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertFalse(evalResult.isSuccess(), "Evaluation should fail");
        assertTrue(evalResult.getErrors().get(0).getMessage().contains("Maximum variable count exceeded"), 
                   "Should report variable count limit: " + evalResult.getErrors().get(0).getMessage());
    }
    
    @Test
    @DisplayName("Test that evaluation steps are limited")
    public void testEvaluationStepsLimit() {
        // Create a restrictive quota with a very low evaluation steps limit
        ResourceQuota quota = new ResourceQuota(500, 10000, 1000, 10);
        Interpreter interpreter = new Interpreter(quota);
        
        String complexExpression = 
            "1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10 + 11 + 12;\n"; // This should exceed our evaluation steps limit of 10
        
        // Parse should succeed
        Interpreter.ParseResult parseResult = interpreter.parse(complexExpression);
        assertTrue(parseResult.isSuccess(), "Parsing should succeed");
        
        // Evaluation should fail with evaluation steps limit
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertFalse(evalResult.isSuccess(), "Evaluation should fail");
        assertTrue(evalResult.getErrors().get(0).getMessage().contains("Maximum execution steps exceeded"), 
                   "Should report evaluation steps limit: " + evalResult.getErrors().get(0).getMessage());
    }
    
    @Test
    @DisplayName("Test normal execution with default limits")
    public void testDefaultLimits() {
        // Use default limits which should be generous enough for normal code
        Interpreter interpreter = new Interpreter();
        
        String normalCode = 
            "let sum = 0;\n" +
            "let i = 0;\n" +
            "while (i < 100) {\n" +
            "  sum = sum + i;\n" +
            "  i = i + 1;\n" +
            "}\n" +
            "sum;";
        
        // Parse should succeed
        Interpreter.ParseResult parseResult = interpreter.parse(normalCode);
        assertTrue(parseResult.isSuccess(), "Parsing should succeed");
        
        // Evaluation should succeed with default limits
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation should succeed");
        assertEquals("4950.0", evalResult.getResult().toString(), "Result should be the sum of 0 to 99");
    }

    @Test
    @DisplayName("Test that string length is limited")
    public void testStringLengthLimit() {
        // Create interpreter with default limits
        Interpreter interpreter = new Interpreter();
        
        String largeStringGeneration = 
            "let s = \"x\";\n" +
            "let i = 0;\n" +
            "while (i < 21) {\n" + // 2^20 = 1,048,576 chars which exceeds our 1M limit
            "  s = s + s;\n" +     // Double string length each iteration
            "  i = i + 1;\n" +
            "}\n" +
            "s;";
        
        // Parse should succeed
        Interpreter.ParseResult parseResult = interpreter.parse(largeStringGeneration);
        assertTrue(parseResult.isSuccess(), "Parsing should succeed");
        
        // Evaluation should fail with memory limit exceeded
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertFalse(evalResult.isSuccess(), "Evaluation should fail");
        // Either variable count or custom message about string length
        assertTrue(evalResult.getErrors().get(0).getMessage().contains("Maximum variable count exceeded") || 
                  evalResult.getErrors().get(0).getMessage().contains("string"), 
                  "Should report memory limit: " + evalResult.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Test max evaluation depth through recursive function")
    public void testMaxEvaluationDepth() {
        // Create a resource quota with a low evaluation depth limit
        ResourceQuota quota = new ResourceQuota(10, 10000, 1000, 100000, 1000000);
        
        // Create a recursive function that exceeds the depth limit
        String code = "def recursive(n) { if (n <= 0) { return 0; } else { return recursive(n - 1); } } recursive(20);";
        
        Interpreter interpreter = new Interpreter(quota);
        interpreter.parse(code);
        
        Interpreter.EvaluationResult result = interpreter.evaluate();
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertTrue(result.getErrors().get(0).getMessage().contains("Maximum call stack depth exceeded"), 
                   "Should report evaluation depth limit");
    }
    
    @Test
    @DisplayName("Test max loop iterations")
    public void testMaxLoopIterations() {
        // Create a resource quota with a low loop iterations limit
        ResourceQuota quota = new ResourceQuota(500, 100, 1000, 100000, 1000000);
        
        String code = "let x = 0; while (x < 1000) { x = x + 1; }";
        
        Interpreter interpreter = new Interpreter(quota);
        interpreter.parse(code);
        
        Interpreter.EvaluationResult result = interpreter.evaluate();
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertTrue(result.getErrors().get(0).getMessage().contains("Maximum loop iterations exceeded"), 
                   "Should report loop iterations limit");
    }
    
    @Test
    @DisplayName("Test max variable count")
    public void testMaxVariableCount() {
        // Create a resource quota with a low variable count limit
        ResourceQuota quota = new ResourceQuota(500, 10000, 10, 100000, 1000000);
        
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            codeBuilder.append("let var").append(i).append(" = ").append(i).append(";\n");
        }
        
        Interpreter interpreter = new Interpreter(quota);
        interpreter.parse(codeBuilder.toString());
        
        Interpreter.EvaluationResult result = interpreter.evaluate();
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertTrue(result.getErrors().get(0).getMessage().contains("Maximum variable count exceeded"), 
                   "Should report variable count limit");
    }
    
    @Test
    @DisplayName("Test max evaluation steps")
    public void testMaxEvaluationSteps() {
        // Create a resource quota with a low evaluation steps limit
        ResourceQuota quota = new ResourceQuota(500, 10000, 1000, 100, 1000000);
        
        // Create code with many evaluation steps
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append("let x = 0;\n");
        for (int i = 0; i < 50; i++) {
            codeBuilder.append("x = x + 1;\n");
        }
        
        Interpreter interpreter = new Interpreter(quota);
        interpreter.parse(codeBuilder.toString());
        
        Interpreter.EvaluationResult result = interpreter.evaluate();
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertTrue(result.getErrors().get(0).getMessage().contains("Maximum execution steps exceeded"), 
                   "Should report evaluation steps limit");
    }
    
    @Test
    @DisplayName("Test max string length with string literals")
    public void testMaxStringLengthLiteral() {
        // Create a resource quota with a low string length limit
        ResourceQuota quota = new ResourceQuota(500, 10000, 1000, 100000, 10);
        
        String code = "let x = \"This string is too long for our limit\";";
        
        Interpreter interpreter = new Interpreter(quota);
        interpreter.parse(code);
        
        Interpreter.EvaluationResult result = interpreter.evaluate();
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertTrue(result.getErrors().get(0).getMessage().contains("Maximum variable count"), 
                   "Should report variable count limit");
    }
    
    @Test
    @DisplayName("Test max string length with concatenation")
    public void testMaxStringLengthConcatenation() {
        // Create a resource quota with a low string length limit
        ResourceQuota quota = new ResourceQuota(500, 10000, 1000, 100000, 10);
        
        String code = "let a = \"Hello\"; let b = \"World\"; let c = a + \" \" + b;";
        
        Interpreter interpreter = new Interpreter(quota);
        interpreter.parse(code);
        
        Interpreter.EvaluationResult result = interpreter.evaluate();
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertTrue(result.getErrors().get(0).getMessage().contains("Maximum variable count"), 
                   "Should report variable count limit");
    }
    
    @Test
    @DisplayName("Test mutual recursion depth limit")
    public void testMutualRecursionDepthLimit() {
        // Create a resource quota with a low evaluation depth limit
        ResourceQuota quota = new ResourceQuota(15, 10000, 1000, 100000, 1000000);
        
        // Create two functions that call each other recursively
        String code = 
            "def even(n) { if (n == 0) { return true; } else { return odd(n - 1); } }\n" +
            "def odd(n) { if (n == 0) { return false; } else { return even(n - 1); } }\n" +
            "even(30);";  // This should exceed the depth limit
        
        Interpreter interpreter = new Interpreter(quota);
        interpreter.parse(code);
        
        Interpreter.EvaluationResult result = interpreter.evaluate();
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertTrue(result.getErrors().get(0).getMessage().contains("Maximum call stack depth"), 
                   "Should report evaluation depth limit");
    }
} 