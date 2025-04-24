package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * Identifier - a reference to a variable
 */
public class Identifier extends Node {
    private final String name;
    
    public Identifier(String name) {
        super();
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        try {
            return context.get(name, position);
        } catch (RuntimeError e) {
            throw new RuntimeError(
                    "Undefined variable '" + name + "'",
                    position.getLine(),
                    position.getColumn()
            );
        }
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"Identifier\", \"position\": \"%s\", \"name\": \"%s\" }",
                position,
                name
        );
    }
} 