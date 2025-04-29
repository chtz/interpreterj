package interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import interpreter.runtime.CallableFunction;
import interpreter.runtime.EvaluationContext;
import interpreter.runtime.ReturnValue;
import interpreter.runtime.RuntimeError;

/**
 * FunctionDeclaration - a function declaration statement
 */
public class FunctionDeclaration extends Node {
    private final String name;
    private final List<String> parameters;
    private final Node body;
    
    public FunctionDeclaration(String name, List<String> parameters, Node body) {
        super();
        this.name = name;
        this.parameters = parameters != null ? parameters : new ArrayList<>();
        this.body = body;
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getParameters() {
        return parameters;
    }
    
    public Node getBody() {
        return body;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step
        trackEvaluationStep(context);
        
        // Create a function wrapper that will execute the function body
        CallableFunction function = (List<Object> args) -> {
            try {
            	if (args.size() != parameters.size()) {
            		throw new RuntimeException("Function " + name + parameters + " called with " + args.size() + " arguments"); //FIXME
            	}
            	
                // Create a new environment with the parent as the current environment
                EvaluationContext functionContext = context.extend();
                
                // Bind arguments to parameters
                for (int i = 0; i < parameters.size(); i++) {
                    String param = parameters.get(i);
                    Object arg = i < args.size() ? args.get(i) : null;
                    
                    functionContext.define(param, arg);
                }
                
                // Execute the function body
                Object result = body.evaluate(functionContext);
                
                // Unwrap ReturnValue if present
                if (result instanceof ReturnValue) {
                    return ((ReturnValue) result).getValue();
                }
                
                return result;
            } catch (RuntimeError e) {
                // Preserve the original RuntimeError as the cause to allow for proper unwrapping
                throw new RuntimeException("Error in function '" + name + "': " + e.getMessage(), e);
            }
        };
        
        // Define the function in the environment
        return context.define(name, function);
    }
    
    @Override
    public String toJson() {
        String parametersJson = parameters.stream()
                .map(param -> "\"" + param + "\"")
                .collect(Collectors.joining(", "));
        
        return String.format(
                "{ \"type\": \"FunctionDeclaration\", \"position\": \"%s\", " +
                "\"name\": \"%s\", \"parameters\": [%s], \"body\": %s }",
                position,
                name,
                parametersJson,
                body != null ? body.toJson() : "null"
        );
    }
} 