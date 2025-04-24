package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * BooleanLiteral - a literal boolean value
 */
public class BooleanLiteral extends Node {
    private final Boolean value;
    
    public BooleanLiteral(Boolean value) {
        super();
        this.value = value;
    }
    
    public Boolean getValue() {
        return value;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        return value;
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"BooleanLiteral\", \"position\": \"%s\", \"value\": %s }",
                position,
                value
        );
    }
} 