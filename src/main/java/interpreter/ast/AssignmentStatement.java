package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * AssignmentStatement - assign a value to a variable
 */
public class AssignmentStatement extends Node {
    private final String name;
    private final Node value;
    
    public AssignmentStatement(String name, Node value) {
        super();
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public Node getValue() {
        return value;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        Object valueResult = value.evaluate(context);
        return context.assign(name, valueResult, position);
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"AssignmentStatement\", \"position\": \"%s\", " +
                "\"name\": \"%s\", \"value\": %s }",
                position,
                name,
                value != null ? value.toJson() : "null"
        );
    }
} 