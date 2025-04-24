package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * ExpressionStatement - a statement that evaluates an expression
 */
public class ExpressionStatement extends Node {
    private final Node expression;
    
    public ExpressionStatement(Node expression) {
        super();
        this.expression = expression;
    }
    
    public Node getExpression() {
        return expression;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        if (expression == null) {
            return null;
        }
        return expression.evaluate(context);
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"ExpressionStatement\", \"position\": \"%s\", \"expression\": %s }",
                position,
                expression != null ? expression.toJson() : "null"
        );
    }
} 