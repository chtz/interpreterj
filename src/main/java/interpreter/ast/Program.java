package interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.ReturnValue;
import interpreter.runtime.RuntimeError;

/**
 * Program node - the root of every AST
 */
public class Program extends Node {
    private final List<Node> statements;
    
    public Program() {
        super();
        this.statements = new ArrayList<>();
    }
    
    /**
     * Add a statement to the program
     */
    public void addStatement(Node statement) {
        if (statement != null) {
            statements.add(statement);
        }
    }
    
    /**
     * Get all statements in the program
     */
    public List<Node> getStatements() {
        return statements;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        // Track this evaluation step to prevent CPU exhaustion
        trackEvaluationStep(context);
        
        Object result = null;
        
        for (Node statement : statements) {
            result = statement.evaluate(context);
            
            // Early return if we hit a return statement
            if (result instanceof ReturnValue) {
                return ((ReturnValue) result).getValue();
            }
        }
        
        return result;
    }
    
    @Override
    public String toJson() {
        String statementsJson = statements.stream()
                .map(Node::toJson)
                .collect(Collectors.joining(",\n"));
        
        return String.format(
                "{ \"type\": \"Program\", \"statements\": [ %s ] }",
                statementsJson
        );
    }
} 