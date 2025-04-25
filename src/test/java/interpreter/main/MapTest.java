package interpreter.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Tests for map/dictionary functionality in the interpreter
 */
public class MapTest {

    @Test
    @DisplayName("Test empty map creation")
    public void testEmptyMap() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse("let m = {}; m;");
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof Map, "Result should be a Map");
        assertEquals(0, ((Map<?, ?>) result).size(), "Empty map should have size 0");
    }
    
    @Test
    @DisplayName("Test map with elements")
    public void testMapWithElements() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let m = {\"a\": 1, \"b\": 2, \"c\": 3}; m;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof Map, "Result should be a Map");
        
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals(3, map.size(), "Map should have 3 elements");
        assertEquals(1.0, map.get("a"), "Value for key 'a' should be 1.0");
        assertEquals(2.0, map.get("b"), "Value for key 'b' should be 2.0");
        assertEquals(3.0, map.get("c"), "Value for key 'c' should be 3.0");
    }
    
    @Test
    @DisplayName("Test mixed key types")
    public void testMixedKeyTypes() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let m = {\"a\": 1, 2: \"b\", 3: true}; m;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof Map, "Result should be a Map");
        
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals(3, map.size(), "Map should have 3 elements");
        assertEquals(1.0, map.get("a"), "Value for key 'a' should be 1.0");
        assertEquals("b", map.get(2.0), "Value for key 2.0 should be 'b'");
        assertEquals(true, map.get(3.0), "Value for key 3.0 should be true");
    }
    
    @Test
    @DisplayName("Test map length")
    public void testMapLength() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let m1 = {};\n" +
            "let m2 = {\"a\": 1, \"b\": 2, \"c\": 3};\n" +
            "let len1 = len(m1);\n" +
            "let len2 = len(m2);\n" +
            "[len1, len2];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> list = (List<?>) result;
        assertEquals(2, list.size(), "Result array should have 2 elements");
        assertEquals(0.0, list.get(0), "Length of empty map should be 0");
        assertEquals(3.0, list.get(1), "Length of {a:1, b:2, c:3} should be 3");
    }
    
    @Test
    @DisplayName("Test map element access")
    public void testMapElementAccess() {
        Interpreter interpreter = new Interpreter();
        
        // Test accessing an element directly
        Interpreter.ParseResult parseResult1 = interpreter.parse(
            "let m = {\"a\": 10, \"b\": 20, \"c\": 30};\n" +
            "m[\"b\"];"
        );
        
        assertTrue(parseResult1.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult1.getErrors()));
        
        Interpreter.EvaluationResult evalResult1 = interpreter.evaluate();
        assertTrue(evalResult1.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult1.getErrors()));
        
        Object result1 = evalResult1.getResult();
        assertEquals(20.0, result1, "Value for key 'b' should be 20.0");
        
        // Test accessing numeric key
        Interpreter interpreter2 = new Interpreter();
        Interpreter.ParseResult parseResult2 = interpreter2.parse(
            "let m = {1: \"one\", 2: \"two\", 3: \"three\"};\n" +
            "m[2];"
        );
        
        assertTrue(parseResult2.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult2.getErrors()));
        
        Interpreter.EvaluationResult evalResult2 = interpreter2.evaluate();
        assertTrue(evalResult2.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult2.getErrors()));
        
        Object result2 = evalResult2.getResult();
        assertEquals("two", result2, "Value for key 2 should be 'two'");
        
        // Test accessing non-existent key
        Interpreter interpreter3 = new Interpreter();
        Interpreter.ParseResult parseResult3 = interpreter3.parse(
            "let m = {\"a\": 1, \"b\": 2};\n" +
            "m[\"c\"];"  // Non-existent key
        );
        
        assertTrue(parseResult3.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult3.getErrors()));
        
        Interpreter.EvaluationResult evalResult3 = interpreter3.evaluate();
        assertTrue(evalResult3.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult3.getErrors()));
        
        Object result3 = evalResult3.getResult();
        assertNull(result3, "Value for non-existent key should be null");
    }
    
    @Test
    @DisplayName("Test map element assignment")
    public void testMapElementAssignment() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let m = {\"a\": 1, \"b\": 2};\n" +
            "m[\"c\"] = 3;\n" + // Add new key
            "m[\"a\"] = 10;\n" + // Update existing key
            "m;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof Map, "Result should be a Map");
        
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals(3, map.size(), "Map should have 3 elements after assignments");
        assertEquals(10.0, map.get("a"), "Value for key 'a' should be 10.0 after update");
        assertEquals(2.0, map.get("b"), "Value for key 'b' should remain 2.0");
        assertEquals(3.0, map.get("c"), "Value for new key 'c' should be 3.0");
    }
    
    @Test
    @DisplayName("Test keys and values functions")
    public void testKeysAndValues() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let m = {\"a\": 1, \"b\": 2, \"c\": 3};\n" +
            "let k = keys(m);\n" +
            "let v = values(m);\n" +
            "[k, v];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> resultList = (List<?>) result;
        assertEquals(2, resultList.size(), "Result array should have 2 elements");
        
        // Check keys
        List<?> keys = (List<?>) resultList.get(0);
        assertEquals(3, keys.size(), "Keys array should have 3 elements");
        assertTrue(keys.contains("a"), "Keys should contain 'a'");
        assertTrue(keys.contains("b"), "Keys should contain 'b'");
        assertTrue(keys.contains("c"), "Keys should contain 'c'");
        
        // Check values
        List<?> values = (List<?>) resultList.get(1);
        assertEquals(3, values.size(), "Values array should have 3 elements");
        assertTrue(values.contains(1.0), "Values should contain 1.0");
        assertTrue(values.contains(2.0), "Values should contain 2.0");
        assertTrue(values.contains(3.0), "Values should contain 3.0");
    }
    
    @Test
    @DisplayName("Test map delete function")
    public void testMapDelete() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let m = {\"a\": 1, \"b\": 2, \"c\": 3};\n" +
            "let removed = delete(m, \"b\");\n" +
            "[m, removed];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> resultList = (List<?>) result;
        assertEquals(2, resultList.size(), "Result array should have 2 elements");
        
        // Check map after deletion
        Map<?, ?> map = (Map<?, ?>) resultList.get(0);
        assertEquals(2, map.size(), "Map should have 2 elements after deletion");
        assertTrue(map.containsKey("a"), "Map should still contain key 'a'");
        assertFalse(map.containsKey("b"), "Map should no longer contain key 'b'");
        assertTrue(map.containsKey("c"), "Map should still contain key 'c'");
        
        // Check removed value
        assertEquals(2.0, resultList.get(1), "Removed value should be 2.0");
    }
    
    @Test
    @DisplayName("Test nested structures with maps and arrays")
    public void testNestedStructures() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let nested = {\n" +
            "  \"a\": [1, 2, 3],\n" +
            "  \"b\": {\"x\": 10, \"y\": 20}\n" +
            "};\n" +
            "let arrayValue = nested[\"a\"][1];\n" +
            "let mapValue = nested[\"b\"][\"y\"];\n" +
            "[arrayValue, mapValue];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> resultList = (List<?>) result;
        assertEquals(2, resultList.size(), "Result array should have 2 elements");
        assertEquals(2.0, resultList.get(0), "Value from nested array should be 2.0");
        assertEquals(20.0, resultList.get(1), "Value from nested map should be 20.0");
    }
} 