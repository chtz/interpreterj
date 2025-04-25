package interpreter.ast;

import java.util.List;
import java.util.Map;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * IndexExpression - represents an array or map access expression like array[index] or map[key]
 */
public class IndexExpression extends Node {
    private final Node collection;
    private final Node index;
    
    public IndexExpression(Node collection, Node index) {
        super();
        this.collection = collection;
        this.index = index;
    }
    
    public Node getCollection() {
        return collection;
    }
    
    public Node getIndex() {
        return index;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step to prevent CPU exhaustion
        trackEvaluationStep(context);
        
        // Evaluate the collection expression
        Object collectionObject = collection.evaluate(context);
        
        // Evaluate the index expression
        Object indexValue = index.evaluate(context);
        
        // Handle array indexing
        if (collectionObject instanceof List) {
            return evaluateArrayIndex((List<Object>) collectionObject, indexValue);
        }
        
        // Handle map indexing
        if (collectionObject instanceof Map) {
            return evaluateMapIndex((Map<Object, Object>) collectionObject, indexValue);
        }
        
        throw new RuntimeError(
            "Cannot use index operator on non-collection value, got: " + 
            (collectionObject == null ? "null" : collectionObject.getClass().getName()),
            position.getLine(),
            position.getColumn()
        );
    }
    
    private Object evaluateArrayIndex(List<Object> array, Object indexValue) throws RuntimeError {
        if (!(indexValue instanceof Number)) {
            throw new RuntimeError(
                "Array index must be a number, got: " + (indexValue == null ? "null" : indexValue.getClass().getName()),
                position.getLine(),
                position.getColumn()
            );
        }
        
        int idx = ((Number) indexValue).intValue();
        
        if (idx < 0 || idx >= array.size()) {
            throw new RuntimeError(
                "Array index out of bounds: " + idx + ", array size: " + array.size(),
                position.getLine(),
                position.getColumn()
            );
        }
        
        // Get the element at the specified index
        return array.get(idx);
    }
    
    private Object evaluateMapIndex(Map<Object, Object> map, Object key) throws RuntimeError {
        if (!(key instanceof String || key instanceof Number)) {
            throw new RuntimeError(
                "Map key must be a string or number, got: " + (key == null ? "null" : key.getClass().getName()),
                position.getLine(),
                position.getColumn()
            );
        }
        
        if (!map.containsKey(key)) {
            return null; // Return null for non-existent keys
        }
        
        return map.get(key);
    }
    
    @Override
    public String toJson() {
        return String.format(
                "{ \"type\": \"IndexExpression\", \"position\": \"%s\", \"collection\": %s, \"index\": %s }",
                position,
                collection != null ? collection.toJson() : "null",
                index != null ? index.toJson() : "null"
        );
    }
} 