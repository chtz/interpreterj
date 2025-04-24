package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * NullLiteral - a literal null value
 */
public class NullLiteral extends Node {
    
    public NullLiteral() {
        super();
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        return null;
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"NullLiteral\", \"position\": \"%s\", \"value\": null }",
                position
        );
    }
} 