package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * StringLiteral - a literal string value
 */
public class StringLiteral extends Node {
    private final String value;
    
    public StringLiteral(String value) {
        super();
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step
        trackEvaluationStep(context);
        
        return value;
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"StringLiteral\", \"position\": \"%s\", \"value\": \"%s\" }",
                position,
                escapeJsonString(value)
        );
    }
    
    /**
     * Escape special characters in a string for JSON
     */
    private String escapeJsonString(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
} 