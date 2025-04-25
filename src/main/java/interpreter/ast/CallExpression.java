package interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import interpreter.runtime.CallableFunction;
import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * CallExpression - a function call expression
 */
public class CallExpression extends Node {
    private final Node callee;
    private final List<Node> arguments;
    
    public CallExpression(Node callee, List<Node> arguments) {
        super();
        this.callee = callee;
        this.arguments = arguments != null ? arguments : new ArrayList<>();
    }
    
    public Node getCallee() {
        return callee;
    }
    
    public List<Node> getArguments() {
        return arguments;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step to prevent CPU exhaustion and recursion depth
        trackEvaluationStep(context);
        
        // Evaluate the function (callee)
        Object function = callee.evaluate(context);
        
        if (function == null) {
            throw new RuntimeError(
                    "Cannot call null as a function",
                    position.getLine(),
                    position.getColumn()
            );
        }
        
        // Evaluate the arguments
        List<Object> args = new ArrayList<>();
        for (Node arg : arguments) {
            args.add(arg.evaluate(context));
        }
        
        // Call the function
        if (function instanceof CallableFunction) {
            try {
                return ((CallableFunction) function).apply(args);
            } catch (RuntimeException e) {
                // Unwrap RuntimeException if it was originally a RuntimeError
                if (e.getCause() instanceof RuntimeError) {
                    throw (RuntimeError) e.getCause();
                }
                throw new RuntimeError(
                        "Error in function call: " + e.getMessage(),
                        position.getLine(),
                        position.getColumn()
                );
            }
        } else {
            throw new RuntimeError(
                    "Not a function: " + function,
                    position.getLine(),
                    position.getColumn()
            );
        }
    }
    
    @Override
    public String toJson() {
        String argumentsJson = arguments.stream()
                .map(Node::toJson)
                .collect(Collectors.joining(", "));
        
        return String.format(
                "{ \"type\": \"CallExpression\", \"position\": \"%s\", " +
                "\"callee\": %s, \"arguments\": [%s] }",
                position,
                callee != null ? callee.toJson() : "null",
                argumentsJson
        );
    }
} 