package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;
import interpreter.util.Evaluator;

/**
 * PrefixExpression - an expression with a prefix operator (e.g., -x, !x)
 */
public class PrefixExpression extends Node {
    private final String operator;
    private final Node right;
    
    public PrefixExpression(String operator, Node right) {
        super();
        this.operator = operator;
        this.right = right;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public Node getRight() {
        return right;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        Object rightValue = right.evaluate(context);
        return Evaluator.applyPrefixOperator(operator, rightValue);
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"PrefixExpression\", \"position\": \"%s\", " +
                "\"operator\": \"%s\", \"right\": %s }",
                position,
                operator,
                right != null ? right.toJson() : "null"
        );
    }
} 