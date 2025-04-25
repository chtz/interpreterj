package interpreter.ast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * MapLiteral - represents a map/dictionary literal expression like {"key": value, 1: 2}
 */
public class MapLiteral extends Node {
    private final Map<Node, Node> pairs;
    
    public MapLiteral(Map<Node, Node> pairs) {
        super();
        this.pairs = pairs != null ? pairs : new LinkedHashMap<>();
    }
    
    public Map<Node, Node> getPairs() {
        return pairs;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step to prevent CPU exhaustion
        trackEvaluationStep(context);
        
        // Evaluate each key-value pair in the map
        Map<Object, Object> mapValues = new HashMap<>();
        for (Map.Entry<Node, Node> entry : pairs.entrySet()) {
            Object key = entry.getKey().evaluate(context);
            
            // Validate key type (only strings and numbers are valid keys)
            if (!(key instanceof String || key instanceof Number)) {
                throw new RuntimeError(
                    "Map keys must be strings or numbers, got: " + (key == null ? "null" : key.getClass().getName()),
                    position.getLine(),
                    position.getColumn()
                );
            }
            
            Object value = entry.getValue().evaluate(context);
            mapValues.put(key, value);
        }
        
        return mapValues;
    }
    
    @Override
    public String toJson() {
        String pairsJson = pairs.entrySet().stream()
                .map(entry -> {
                    String keyJson = entry.getKey() != null ? entry.getKey().toJson() : "null";
                    String valueJson = entry.getValue() != null ? entry.getValue().toJson() : "null";
                    return "{ \"key\": " + keyJson + ", \"value\": " + valueJson + " }";
                })
                .collect(Collectors.joining(", "));
        
        return String.format(
                "{ \"type\": \"MapLiteral\", \"position\": \"%s\", \"pairs\": [ %s ] }",
                position,
                pairsJson
        );
    }
} 