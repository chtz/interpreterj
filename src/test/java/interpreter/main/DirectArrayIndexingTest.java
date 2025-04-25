package interpreter.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DirectArrayIndexingTest {
    @Test
    @DisplayName("Test direct array indexing as statement")
    public void testDirectArrayIndexingAsStatement() {
        Interpreter interpreter = new Interpreter();
        Interpreter.ParseResult parseResult = interpreter.parse(
            "let arr = [10, 20, 30, 40, 50];\n" +
            "arr[4];"  // Direct array indexing as the last statement
        );
        
        assertTrue(parseResult.isSuccess(), "Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
        
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        assertTrue(evalResult.isSuccess(), "Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
        
        Object result = evalResult.getResult();
        assertEquals(50.0, result, "Element at index 4 should be 50.0");
    }
} 