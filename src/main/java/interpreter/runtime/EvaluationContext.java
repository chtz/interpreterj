package interpreter.runtime;

import java.util.HashMap;
import java.util.Map;

import interpreter.ast.Node;

/**
 * Environment to store variables and functions in the current scope
 */
public class EvaluationContext {
    private final EvaluationContext parent;
    private final Map<String, Object> values;
    private final Map<String, CallableFunction> functions;
    
    public EvaluationContext() {
        this(null);
    }
    
    public EvaluationContext(EvaluationContext parent) {
        this.parent = parent;
        this.values = new HashMap<>();
        this.functions = new HashMap<>();
    }
    
    /**
     * Create a new nested scope
     */
    public EvaluationContext extend() {
        return new EvaluationContext(this);
    }
    
    /**
     * Define a variable in the current scope
     */
    public Object define(String name, Object value) {
        values.put(name, value);
        return value;
    }
    
    /**
     * Get a variable from the current or parent scopes
     */
    public Object get(String name, Node.Position position) throws RuntimeError {
        // Check current scope
        if (values.containsKey(name)) {
            return values.get(name);
        }
        
        // Check library functions
        if (functions.containsKey(name)) {
            return functions.get(name);
        }
        
        // Look in parent scope
        if (parent != null) {
            return parent.get(name, position);
        }
        
        // Not found
        throw new RuntimeError(
                "Undefined variable '" + name + "'", 
                position != null ? position.getLine() : 0,
                position != null ? position.getColumn() : 0
        );
    }
    
    /**
     * Assign a value to a variable in the current or parent scopes
     */
    public Object assign(String name, Object value, Node.Position position) throws RuntimeError {
        // Check current scope
        if (values.containsKey(name)) {
            values.put(name, value);
            return value;
        }
        
        // Try to assign in parent scope
        if (parent != null) {
            return parent.assign(name, value, position);
        }
        
        // Not found
        throw new RuntimeError(
                "Cannot assign to undefined variable '" + name + "'", 
                position != null ? position.getLine() : 0,
                position != null ? position.getColumn() : 0
        );
    }
    
    /**
     * Register a library function
     */
    public CallableFunction registerFunction(String name, CallableFunction function) {
        functions.put(name, function);
        return function;
    }
} 