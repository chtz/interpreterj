package interpreter.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for string manipulation functions in the interpreter
 */
public class StringFunctionsTest {

    @Test
    @DisplayName("Test char function")
    public void testCharFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let s = \"hello\";\n" +
            "let c1 = char(s, 0);\n" +
            "let c2 = char(s, 4);\n" +
            "[c1, c2];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals("h", list.get(0), "First character should be 'h'");
        assertEquals("o", list.get(1), "Last character should be 'o'");
    }
    
    @Test
    @DisplayName("Test ord function")
    public void testOrdFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let c1 = ord(\"A\");\n" +
            "let c2 = ord(\"z\");\n" +
            "[c1, c2];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals(65.0, list.get(0), "ASCII code for 'A' should be 65");
        assertEquals(122.0, list.get(1), "ASCII code for 'z' should be 122");
    }
    
    @Test
    @DisplayName("Test chr function")
    public void testChrFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let s1 = chr(65);\n" +
            "let s2 = chr(97);\n" +
            "[s1, s2];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals("A", list.get(0), "Character for code 65 should be 'A'");
        assertEquals("a", list.get(1), "Character for code 97 should be 'a'");
    }
    
    @Test
    @DisplayName("Test substr function")
    public void testSubstrFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let s = \"hello world\";\n" +
            "let sub1 = substr(s, 0, 5);\n" +
            "let sub2 = substr(s, 6, 5);\n" +
            "[sub1, sub2];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals("hello", list.get(0), "First substring should be 'hello'");
        assertEquals("world", list.get(1), "Second substring should be 'world'");
    }
    
    @Test
    @DisplayName("Test startsWith function")
    public void testStartsWithFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let s = \"hello world\";\n" +
            "let result1 = startsWith(s, \"hello\");\n" +
            "let result2 = startsWith(s, \"world\");\n" +
            "[result1, result2];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals(true, list.get(0), "String should start with 'hello'");
        assertEquals(false, list.get(1), "String should not start with 'world'");
    }
    
    @Test
    @DisplayName("Test endsWith function")
    public void testEndsWithFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let s = \"hello world\";\n" +
            "let result1 = endsWith(s, \"world\");\n" +
            "let result2 = endsWith(s, \"hello\");\n" +
            "[result1, result2];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals(true, list.get(0), "String should end with 'world'");
        assertEquals(false, list.get(1), "String should not end with 'hello'");
    }
    
    @Test
    @DisplayName("Test trim function")
    public void testTrimFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let s = \"  hello world  \";\n" +
            "trim(s);"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof String, "Result should be a String");
        
        assertEquals("hello world", result, "Trimmed string should be 'hello world'");
    }
    
    @Test
    @DisplayName("Test join function")
    public void testJoinFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let arr = [\"hello\", \"world\", \"test\"];\n" +
            "let result1 = join(arr, \", \");\n" +
            "let result2 = join(arr, \"\");\n" +
            "[result1, result2];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof java.util.List, "Result should be a List");
        
        java.util.List<?> list = (java.util.List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals("hello, world, test", list.get(0), "Joined string with comma should be 'hello, world, test'");
        assertEquals("helloworldtest", list.get(1), "Joined string without delimiter should be 'helloworldtest'");
    }
    
    @Test
    @DisplayName("Test error handling in string functions")
    public void testErrorHandling() {
        // Test char with invalid index
        Interpreter interpreter1 = new Interpreter();
        interpreter1.parse("char(\"hello\", 10);");
        Interpreter.EvaluationResult evalResult1 = interpreter1.evaluate();
        assertFalse(evalResult1.isSuccess(), "Should fail with out of bounds error");
        assertTrue(Interpreter.formatErrors(evalResult1.getErrors()).contains("String index out of bounds"), 
                "Error message should contain 'String index out of bounds'");
        
        // Test ord with multi-character string
        Interpreter interpreter2 = new Interpreter();
        interpreter2.parse("ord(\"abc\");");
        Interpreter.EvaluationResult evalResult2 = interpreter2.evaluate();
        assertFalse(evalResult2.isSuccess(), "Should fail with single character requirement");
        assertTrue(Interpreter.formatErrors(evalResult2.getErrors()).contains("single character"), 
                "Error message should mention single character requirement");
        
        // Test substr with negative start
        Interpreter interpreter3 = new Interpreter();
        interpreter3.parse("substr(\"hello\", -1, 2);");
        Interpreter.EvaluationResult evalResult3 = interpreter3.evaluate();
        assertFalse(evalResult3.isSuccess(), "Should fail with negative start index");
        assertTrue(Interpreter.formatErrors(evalResult3.getErrors()).contains("Start index cannot be negative"), 
                "Error message should mention negative start index");
    }
} 