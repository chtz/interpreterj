package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * NumberLiteral - a literal number value
 */
public class NumberLiteral extends Node {
    private final Double value;
    
    public NumberLiteral(Double value) {
        super();
        this.value = value;
    }
    
    public Double getValue() {
        return value;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        return value;
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"NumberLiteral\", \"position\": \"%s\", \"value\": %s }",
                position,
                value
        );
    }
} 