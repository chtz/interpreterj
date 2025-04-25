package interpreter.ast;

import java.util.List;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * IndexExpression - represents an array access expression like array[index]
 */
public class IndexExpression extends Node {
    private final Node array;
    private final Node index;
    
    public IndexExpression(Node array, Node index) {
        super();
        this.array = array;
        this.index = index;
    }
    
    public Node getArray() {
        return array;
    }
    
    public Node getIndex() {
        return index;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step to prevent CPU exhaustion
        trackEvaluationStep(context);
        
        // Evaluate the array expression
        Object arrayObject = array.evaluate(context);
        
        // Evaluate the index expression
        Object indexValue = index.evaluate(context);
        
        if (!(arrayObject instanceof List)) {
            throw new RuntimeError(
                "Cannot use index operator on non-array value, got: " + (arrayObject == null ? "null" : arrayObject.getClass().getName()),
                position.getLine(),
                position.getColumn()
            );
        }
        
        @SuppressWarnings("unchecked")
        List<Object> arrayList = (List<Object>) arrayObject;
        
        if (!(indexValue instanceof Number)) {
            throw new RuntimeError(
                "Array index must be a number, got: " + (indexValue == null ? "null" : indexValue.getClass().getName()),
                position.getLine(),
                position.getColumn()
            );
        }
        
        int idx = ((Number) indexValue).intValue();
        
        if (idx < 0 || idx >= arrayList.size()) {
            throw new RuntimeError(
                "Array index out of bounds: " + idx + ", array size: " + arrayList.size(),
                position.getLine(),
                position.getColumn()
            );
        }
        
        // Get the element at the specified index
        Object result = arrayList.get(idx);
        return result;
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"IndexExpression\", \"position\": \"%s\", \"array\": %s, \"index\": %s }",
                position,
                array != null ? array.toJson() : "null",
                index != null ? index.toJson() : "null"
        );
    }
} 