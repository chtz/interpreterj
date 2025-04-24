package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * VariableDeclaration - declaration of a variable with optional initialization
 */
public class VariableDeclaration extends Node {
    private final String name;
    private final Node initializer;
    
    public VariableDeclaration(String name, Node initializer) {
        super();
        this.name = name;
        this.initializer = initializer;
    }
    
    public String getName() {
        return name;
    }
    
    public Node getInitializer() {
        return initializer;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        Object value = initializer != null ? initializer.evaluate(context) : null;
        return context.define(name, value);
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"VariableDeclaration\", \"position\": \"%s\", \"name\": \"%s\", \"initializer\": %s }",
                position,
                name,
                initializer != null ? initializer.toJson() : "null"
        );
    }
} 