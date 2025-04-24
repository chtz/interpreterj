package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.ReturnValue;
import interpreter.runtime.RuntimeError;
import interpreter.util.Evaluator;

/**
 * WhileStatement - a loop statement
 */
public class WhileStatement extends Node {
    private final Node condition;
    private final Node body;
    
    public WhileStatement(Node condition, Node body) {
        super();
        this.condition = condition;
        this.body = body;
    }
    
    public Node getCondition() {
        return condition;
    }
    
    public Node getBody() {
        return body;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        Object result = null;
        
        while (Evaluator.isTruthy(condition.evaluate(context))) {
            result = body.evaluate(context);
            
            // Handle return statements inside the loop
            if (result instanceof ReturnValue) {
                return result;
            }
        }
        
        return result;
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"WhileStatement\", \"position\": \"%s\", " +
                "\"condition\": %s, \"body\": %s }",
                position,
                condition != null ? condition.toJson() : "null",
                body != null ? body.toJson() : "null"
        );
    }
} 