package interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * ArrayLiteral - represents an array literal expression like [1, 2, 3]
 */
public class ArrayLiteral extends Node {
    private final List<Node> elements;
    
    public ArrayLiteral(List<Node> elements) {
        super();
        this.elements = elements != null ? elements : new ArrayList<>();
    }
    
    public List<Node> getElements() {
        return elements;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step to prevent CPU exhaustion
        trackEvaluationStep(context);
        
        // Evaluate each element in the array
        List<Object> arrayValues = new ArrayList<>();
        for (Node element : elements) {
            arrayValues.add(element.evaluate(context));
        }
        
        return arrayValues;
    }
    
    @Override
    public String toJson() {
        String elementsJson = elements.stream()
                .map(element -> element != null ? element.toJson() : "null")
                .collect(Collectors.joining(", "));
        
        return String.format(
                "{ \"type\": \"ArrayLiteral\", \"position\": \"%s\", \"elements\": [ %s ] }",
                position,
                elementsJson
        );
    }
} 