package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.ReturnValue;
import interpreter.runtime.RuntimeError;

/**
 * ReturnStatement - a statement that returns a value from a function
 */
public class ReturnStatement extends Node {
    private final Node value;
    
    public ReturnStatement(Node value) {
        super();
        this.value = value;
    }
    
    public Node getValue() {
        return value;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        Object valueResult = value != null ? value.evaluate(context) : null;
        return new ReturnValue(valueResult);
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"ReturnStatement\", \"position\": \"%s\", \"value\": %s }",
                position,
                value != null ? value.toJson() : "null"
        );
    }
} 