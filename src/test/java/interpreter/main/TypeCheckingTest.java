package interpreter.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for type checking functions in the interpreter
 */
public class TypeCheckingTest {

    @Test
    @DisplayName("Test typeof function")
    public void testTypeofFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let numberType = typeof(42);\n" +
            "let stringType = typeof(\"hello\");\n" +
            "let booleanType = typeof(true);\n" +
            "let nullType = typeof(null);\n" +
            "let arrayType = typeof([1, 2, 3]);\n" +
            "let mapType = typeof({\"a\": 1, \"b\": 2});\n" +
            "let funcType = typeof(echo);\n" +
            "[numberType, stringType, booleanType, nullType, arrayType, mapType, funcType];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> types = (List<?>) result;
        assertEquals(7, types.size(), "Types list should have 7 elements");
        assertEquals("number", types.get(0), "Type of 42 should be 'number'");
        assertEquals("string", types.get(1), "Type of \"hello\" should be 'string'");
        assertEquals("boolean", types.get(2), "Type of true should be 'boolean'");
        assertEquals("null", types.get(3), "Type of null should be 'null'");
        assertEquals("array", types.get(4), "Type of [1, 2, 3] should be 'array'");
        assertEquals("map", types.get(5), "Type of {\"a\": 1, \"b\": 2} should be 'map'");
        assertEquals("function", types.get(6), "Type of echo should be 'function'");
    }
    
    @Test
    @DisplayName("Test isNumber function")
    public void testIsNumberFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let result1 = isNumber(42);\n" +
            "let result2 = isNumber(\"42\");\n" +
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
        assertEquals(true, list.get(0), "42 should be a number");
        assertEquals(false, list.get(1), "\"42\" should not be a number");
    }
    
    @Test
    @DisplayName("Test isString function")
    public void testIsStringFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let result1 = isString(\"hello\");\n" +
            "let result2 = isString(42);\n" +
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
        assertEquals(true, list.get(0), "\"hello\" should be a string");
        assertEquals(false, list.get(1), "42 should not be a string");
    }
    
    @Test
    @DisplayName("Test isBoolean function")
    public void testIsBooleanFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let result1 = isBoolean(true);\n" +
            "let result2 = isBoolean(false);\n" +
            "let result3 = isBoolean(1);\n" +
            "[result1, result2, result3];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> list = (List<?>) result;
        assertEquals(3, list.size(), "Result array should have 3 elements");
        assertEquals(true, list.get(0), "true should be a boolean");
        assertEquals(true, list.get(1), "false should be a boolean");
        assertEquals(false, list.get(2), "1 should not be a boolean");
    }
    
    @Test
    @DisplayName("Test isArray function")
    public void testIsArrayFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let result1 = isArray([1, 2, 3]);\n" +
            "let result2 = isArray(\"hello\");\n" +
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
        assertEquals(true, list.get(0), "[1, 2, 3] should be an array");
        assertEquals(false, list.get(1), "\"hello\" should not be an array");
    }
    
    @Test
    @DisplayName("Test isMap function")
    public void testIsMapFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let result1 = isMap({\"a\": 1, \"b\": 2});\n" +
            "let result2 = isMap([1, 2, 3]);\n" +
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
        assertEquals(true, list.get(0), "{\"a\": 1, \"b\": 2} should be a map");
        assertEquals(false, list.get(1), "[1, 2, 3] should not be a map");
    }
    
    @Test
    @DisplayName("Test isFunction function")
    public void testIsFunctionFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let result1 = isFunction(echo);\n" +
            "let result2 = isFunction(\"echo\");\n" +
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
        assertEquals(true, list.get(0), "echo should be a function");
        assertEquals(false, list.get(1), "\"echo\" should not be a function");
    }
    
    @Test
    @DisplayName("Test isNull function")
    public void testIsNullFunction() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let result1 = isNull(null);\n" +
            "let result2 = isNull(0);\n" +
            "let result3 = isNull(\"\");\n" +
            "[result1, result2, result3];"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof List, "Result should be a List");
        
        List<?> list = (List<?>) result;
        assertEquals(3, list.size(), "Result array should have 3 elements");
        assertEquals(true, list.get(0), "null should be null");
        assertEquals(false, list.get(1), "0 should not be null");
        assertEquals(false, list.get(2), "\"\" should not be null");
    }
    
    @Test
    @DisplayName("Test type checking in practice")
    public void testTypeCheckingInPractice() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "def getType(value) {\n" +
            "  return typeof(value);\n" +
            "}\n" +
            "\n" +
            "def safeToString(value) {\n" +
            "  if (isNull(value)) {\n" +
            "    return \"null\";\n" +
            "  } else { if (isArray(value)) {\n" +
            "    let result = \"[\";\n" +
            "    let i = 0;\n" +
            "    while (i < len(value)) {\n" +
            "      result = result + safeToString(value[i]);\n" +
            "      if (i < len(value) - 1) {\n" +
            "        result = result + \", \";\n" +
            "      }\n" +
            "      i = i + 1;\n" +
            "    }\n" +
            "    result = result + \"]\";\n" +
            "    return result;\n" +
            "  } else { if (isMap(value)) {\n" +
            "    return \"object\";\n" +
            "  } else {\n" +
            "    return value;\n" +
            "  } } }\n" +
            "}\n" +
            "\n" +
            "let result = safeToString([1, null, \"hello\", [true, false]]);\n" +
            "result;"
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof String, "Result should be a String");
        
        assertEquals("[1.0, null, hello, [true, false]]", result, 
                "Safe string representation should handle different types");
    }
} 