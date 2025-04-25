package interpreter.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for regular expression functions in the interpreter
 */
public class RegexFunctionsTest {

    @Test
    @DisplayName("Test match function")
    public void testMatchFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let result1 = match(\"hello123\", \"^[a-z]+\\\\d+$\");\n" +
            "let result2 = match(\"123hello\", \"^[a-z]+\\\\d+$\");\n" +
            "[result1, result2];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> list = (List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals(true, list.get(0), "String 'hello123' should match the pattern");
        assertEquals(false, list.get(1), "String '123hello' should not match the pattern");
    }
    
    @Test
    @DisplayName("Test findAll function")
    public void testFindAllFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let result = findAll(\"hello123world456\", \"\\\\d+\");\n" +
            "result;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> list = (List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals("123", list.get(0), "First match should be '123'");
        assertEquals("456", list.get(1), "Second match should be '456'");
    }
    
    @Test
    @DisplayName("Test replace function")
    public void testReplaceFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let s = \"hello123world456\";\n" +
            "let result = replace(s, \"\\\\d+\", \"X\");\n" +
            "result;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof String, "Result should be a String");
        
        assertEquals("helloXworldX", result, "Digits should be replaced with 'X'");
    }
    
    @Test
    @DisplayName("Test split function")
    public void testSplitFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let s = \"hello,world,test\";\n" +
            "let result = split(s, \",\");\n" +
            "result;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> list = (List<?>) result;
        assertEquals(3, list.size(), "Result array should have 3 elements");
        assertEquals("hello", list.get(0), "First part should be 'hello'");
        assertEquals("world", list.get(1), "Second part should be 'world'");
        assertEquals("test", list.get(2), "Third part should be 'test'");
    }
    
    @Test
    @DisplayName("Test split with empty parts")
    public void testSplitWithEmptyParts() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let s = \"a,,b,c,\";\n" +
            "let result = split(s, \",\");\n" +
            "result;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> list = (List<?>) result;
        assertEquals(5, list.size(), "Result array should have 5 elements (including empty parts)");
        assertEquals("a", list.get(0), "First part should be 'a'");
        assertEquals("", list.get(1), "Second part should be empty");
        assertEquals("b", list.get(2), "Third part should be 'b'");
        assertEquals("c", list.get(3), "Fourth part should be 'c'");
        assertEquals("", list.get(4), "Fifth part should be empty");
    }
    
    @Test
    @DisplayName("Test complex regex patterns")
    public void testComplexRegexPatterns() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let email = \"user@example.com\";\n" +
            "let isValidEmail = match(email, \"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$\");\n" +
            "isValidEmail;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertEquals(true, result, "Email should match the pattern");
    }
    
    @Test
    @DisplayName("Test error handling in regex functions")
    public void testErrorHandling() {
        // Test with invalid regex pattern
        Interpreter interpreter = new Interpreter();
        interpreter.parse("match(\"hello\", \"[\");");
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertFalse(evalResult.isSuccess(), "Should fail with invalid regex pattern");
        assertTrue(Interpreter.formatErrors(evalResult.getErrors()).contains("Invalid regex pattern"), 
                "Error message should mention invalid pattern");
    }
} 