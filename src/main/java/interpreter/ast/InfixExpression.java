package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;
import interpreter.util.Evaluator;

/**
 * InfixExpression - an expression with an infix operator (e.g., a + b, a < b)
 */
public class InfixExpression extends Node {
    private final Node left;
    private final String operator;
    private final Node right;
    
    public InfixExpression(Node left, String operator, Node right) {
        super();
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    public Node getLeft() {
        return left;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public Node getRight() {
        return right;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step to prevent CPU exhaustion
        trackEvaluationStep(context);
        
        Object leftValue = left.evaluate(context);
        Object rightValue = right.evaluate(context);
        
        return Evaluator.applyInfixOperator(leftValue, operator, rightValue, context.getResourceQuota());
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"InfixExpression\", \"position\": \"%s\", " +
                "\"left\": %s, \"operator\": \"%s\", \"right\": %s }",
                position,
                left != null ? left.toJson() : "null",
                operator,
                right != null ? right.toJson() : "null"
        );
    }
} 