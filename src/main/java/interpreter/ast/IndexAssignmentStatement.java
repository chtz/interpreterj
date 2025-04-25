package interpreter.ast;

import java.util.List;
import java.util.Map;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * IndexAssignmentStatement - represents an assignment to an array element or map entry like array[index] = value or map[key] = value
 */
public class IndexAssignmentStatement extends Node {
    private final Node collection;
    private final Node index;
    private final Node value;
    
    public IndexAssignmentStatement(Node collection, Node index, Node value) {
        super();
        this.collection = collection;
        this.index = index;
        this.value = value;
    }
    
    public Node getCollection() {
        return collection;
    }
    
    public Node getIndex() {
        return index;
    }
    
    public Node getValue() {
        return value;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step to prevent CPU exhaustion
        trackEvaluationStep(context);
        
        Object collectionObject = collection.evaluate(context);
        Object indexValue = index.evaluate(context);
        Object valueToAssign = value.evaluate(context);
        
        // Handle array assignment
        if (collectionObject instanceof List) {
            return assignToArray((List<Object>) collectionObject, indexValue, valueToAssign);
        }
        
        // Handle map assignment
        if (collectionObject instanceof Map) {
            return assignToMap((Map<Object, Object>) collectionObject, indexValue, valueToAssign);
        }
        
        throw new RuntimeError(
            "Cannot use index operator on non-collection value",
            position.getLine(),
            position.getColumn()
        );
    }
    
    private Object assignToArray(List<Object> array, Object indexValue, Object valueToAssign) throws RuntimeError {
        if (!(indexValue instanceof Number)) {
            throw new RuntimeError(
                "Array index must be a number",
                position.getLine(),
                position.getColumn()
            );
        }
        
        int idx = ((Number) indexValue).intValue();
        
        if (idx < 0 || idx >= array.size()) {
            throw new RuntimeError(
                "Array index out of bounds: " + idx,
                position.getLine(),
                position.getColumn()
            );
        }
        
        array.set(idx, valueToAssign);
        return valueToAssign;
    }
    
    private Object assignToMap(Map<Object, Object> map, Object key, Object valueToAssign) throws RuntimeError {
        if (!(key instanceof String || key instanceof Number)) {
            throw new RuntimeError(
                "Map key must be a string or number",
                position.getLine(),
                position.getColumn()
            );
        }
        
        map.put(key, valueToAssign);
        return valueToAssign;
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"IndexAssignmentStatement\", \"position\": \"%s\", " +
                "\"collection\": %s, \"index\": %s, \"value\": %s }",
                position,
                collection != null ? collection.toJson() : "null",
                index != null ? index.toJson() : "null",
                value != null ? value.toJson() : "null"
        );
    }
} 