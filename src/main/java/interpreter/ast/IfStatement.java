package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;
import interpreter.util.Evaluator;

/**
 * IfStatement - a conditional statement
 */
public class IfStatement extends Node {
    private final Node condition;
    private final Node consequence;
    private final Node alternative;
    
    public IfStatement(Node condition, Node consequence, Node alternative) {
        super();
        this.condition = condition;
        this.consequence = consequence;
        this.alternative = alternative;
    }
    
    public Node getCondition() {
        return condition;
    }
    
    public Node getConsequence() {
        return consequence;
    }
    
    public Node getAlternative() {
        return alternative;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        Object conditionResult = condition.evaluate(context);
        
        if (Evaluator.isTruthy(conditionResult)) {
            return consequence.evaluate(context);
        } else if (alternative != null) {
            return alternative.evaluate(context);
        }
        
        return null;
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"IfStatement\", \"position\": \"%s\", " +
                "\"condition\": %s, \"consequence\": %s, \"alternative\": %s }",
                position,
                condition != null ? condition.toJson() : "null",
                consequence != null ? consequence.toJson() : "null",
                alternative != null ? alternative.toJson() : "null"
        );
    }
} 