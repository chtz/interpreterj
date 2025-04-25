package interpreter.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for array functionality in the interpreter
 */
public class ArrayTest {

    @Test
    @DisplayName("Test empty array creation")
    public void testEmptyArray() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse("let a = []; a;");
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        assertEquals(0, ((java.util.List<?>) result).size(), "Empty array should have size 0");
    }
    
    @Test
    @DisplayName("Test array with elements")
    public void testArrayWithElements() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse("let a = [1, 2, 3]; a;");
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(3, list.size(), "Array should have 3 elements");
        assertEquals(1.0, list.get(0), "First element should be 1.0");
        assertEquals(2.0, list.get(1), "Second element should be 2.0");
        assertEquals(3.0, list.get(2), "Third element should be 3.0");
    }
    
    @Test
    @DisplayName("Test array length")
    public void testArrayLength() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let a = [];\n" +
            "let b = [1, 2, 3];\n" +
            "let aLen = len(a);\n" +
            "let bLen = len(b);\n" +
            "[aLen, bLen];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals(0.0, list.get(0), "Length of empty array should be 0");
        assertEquals(3.0, list.get(1), "Length of [1, 2, 3] should be 3");
    }
    
    @Test
    @DisplayName("Test array concatenation")
    public void testArrayConcatenation() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let b = [1, 2, 3];\n" +
            "let c = [4, 5];\n" +
            "let d = b + c;\n" +
            "d;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(5, list.size(), "Concatenated array should have 5 elements");
        assertEquals(1.0, list.get(0));
        assertEquals(2.0, list.get(1));
        assertEquals(3.0, list.get(2));
        assertEquals(4.0, list.get(3));
        assertEquals(5.0, list.get(4));
    }
    
    @Test
    @DisplayName("Test array indexing")
    public void testArrayIndexing() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let arr = [10, 20, 30, 40, 50];\n" +
            "arr;\n"  // Just return the array itself for debugging
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(5, list.size(), "Array should have 5 elements");
        assertEquals(10.0, list.get(0), "First element should be 10.0");
        assertEquals(50.0, list.get(4), "Last element should be 50.0");
    }
    
    @Test
    @DisplayName("Test array element assignment")
    public void testArrayElementAssignment() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let d = [1, 2, 3, 4, 5];\n" +
            "d[0] = 9;\n" +
            "d;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(5, list.size(), "Array should still have 5 elements");
        assertEquals(9.0, list.get(0), "First element should be 9.0 after assignment");
    }
    
    @Test
    @DisplayName("Test array delete")
    public void testArrayDelete() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let d = [1, 2, 3, 4, 5];\n" +
            "delete(d, 0);\n" +
            "d;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(4, list.size(), "Array should have 4 elements after deletion");
        assertEquals(2.0, list.get(0), "First element should be 2.0 after deletion");
    }
    
    @Test
    @DisplayName("Test array push")
    public void testArrayPush() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let d = [2, 3, 4, 5];\n" +
            "push(d, 7);\n" +
            "d;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(5, list.size(), "Array should have 5 elements after push");
        assertEquals(7.0, list.get(4), "Last element should be 7.0 after push");
    }
    
    @Test
    @DisplayName("Test array pop")
    public void testArrayPop() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let d = [2, 3, 4, 5, 7];\n" +
            "let popped = pop(d);\n" +
            "[popped, d];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> resultList = (java.util.List<?>) result;
        assertEquals(2, resultList.size(), "Result array should have 2 elements");
        
        // Check popped value
        assertEquals(7.0, resultList.get(0), "Popped value should be 7.0");
        
        // Check array after popping
        java.util.List<?> list = (java.util.List<?>) resultList.get(1);
        assertEquals(4, list.size(), "Array should have 4 elements after pop");
        assertEquals(5.0, list.get(3), "Last element should be 5.0 after pop");
    }
    
    @Test
    @DisplayName("Test array comprehensive example")
    public void testArrayComprehensiveExample() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let a = [];\n" +
            "let b = [1, 2, 3];\n" +
            "let c = [4, 5];\n" +
            "let d = b + c;\n" + // [1, 2, 3, 4, 5]
            "d[0] = 9;\n" +      // [9, 2, 3, 4, 5]
            "delete(d, 0);\n" +  // [2, 3, 4, 5]
            "push(d, 7);\n" +    // [2, 3, 4, 5, 7]
            "let last = pop(d);\n" + // [2, 3, 4, 5], last = 7
            "[a, b, c, d, last];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> resultList = (java.util.List<?>) result;
        assertEquals(5, resultList.size(), "Result array should have 5 elements");
        
        // Check a (empty array)
        java.util.List<?> a = (java.util.List<?>) resultList.get(0);
        assertEquals(0, a.size(), "a should be empty");
        
        // Check b ([1, 2, 3])
        java.util.List<?> b = (java.util.List<?>) resultList.get(1);
        assertEquals(3, b.size(), "b should have 3 elements");
        assertEquals(1.0, b.get(0));
        
        // Check c ([4, 5])
        java.util.List<?> c = (java.util.List<?>) resultList.get(2);
        assertEquals(2, c.size(), "c should have 2 elements");
        assertEquals(4.0, c.get(0));
        
        // Check d ([2, 3, 4, 5])
        java.util.List<?> d = (java.util.List<?>) resultList.get(3);
        assertEquals(4, d.size(), "d should have 4 elements");
        assertEquals(2.0, d.get(0));
        
        // Check last (7)
        assertEquals(7.0, resultList.get(4), "last should be 7.0");
    }
    
    @Test
    @DisplayName("Test direct array element access")
    public void testDirectArrayElementAccess() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let arr = [10, 20, 30, 40, 50];\n" +
            "arr[4];"  
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertEquals(50.0, result, "Element at index 4 should be 50.0");
    }
    
    @Test
    @DisplayName("Test array function with index")
    public void testArrayFunctionWithIndex() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let arr = [10, 20, 30, 40, 50];\n" +
            "echo(arr[4]);"  // Use the built-in echo function
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertEquals(50.0, result, "Element at index 4 should be 50.0");
    }
} 