package interpreter.ast;

import java.util.List;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * IndexAssignmentStatement - represents an assignment to an array element like array[index] = value
 */
public class IndexAssignmentStatement extends Node {
    private final Node array;
    private final Node index;
    private final Node value;
    
    public IndexAssignmentStatement(Node array, Node index, Node value) {
        super();
        this.array = array;
        this.index = index;
        this.value = value;
    }
    
    public Node getArray() {
        return array;
    }
    
    public Node getIndex() {
        return index;
    }
    
    public Node getValue() {
        return value;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step to prevent CPU exhaustion
        trackEvaluationStep(context);
        
        Object arrayObject = array.evaluate(context);
        Object indexValue = index.evaluate(context);
        Object valueToAssign = value.evaluate(context);
        
        if (!(arrayObject instanceof List)) {
            throw new RuntimeError(
                "Cannot use index operator on non-array value",
                position.getLine(),
                position.getColumn()
            );
        }
        
        @SuppressWarnings("unchecked")
        List<Object> arrayList = (List<Object>) arrayObject;
        
        if (!(indexValue instanceof Number)) {
            throw new RuntimeError(
                "Array index must be a number",
                position.getLine(),
                position.getColumn()
            );
        }
        
        int idx = ((Number) indexValue).intValue();
        
        if (idx < 0 || idx >= arrayList.size()) {
            throw new RuntimeError(
                "Array index out of bounds: " + idx,
                position.getLine(),
                position.getColumn()
            );
        }
        
        arrayList.set(idx, valueToAssign);
        return valueToAssign;
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"IndexAssignmentStatement\", \"position\": \"%s\", " +
                "\"array\": %s, \"index\": %s, \"value\": %s }",
                position,
                array != null ? array.toJson() : "null",
                index != null ? index.toJson() : "null",
                value != null ? value.toJson() : "null"
        );
    }
} 