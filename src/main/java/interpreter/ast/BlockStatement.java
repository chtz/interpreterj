package interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.ReturnValue;
import interpreter.runtime.RuntimeError;

/**
 * BlockStatement - a block of statements with its own scope
 */
public class BlockStatement extends Node {
    private final List<Node> statements;
    
    public BlockStatement() {
        super();
        this.statements = new ArrayList<>();
    }
    
    public BlockStatement(List<Node> statements) {
        super();
        this.statements = new ArrayList<>(statements);
    }
    
    public void addStatement(Node statement) {
        if (statement != null) {
            statements.add(statement);
        }
    }
    
    public List<Node> getStatements() {
        return statements;
    }
    
    @Override
    public Object evaluate(EvaluationContext context) throws RuntimeError {
        Object result = null;
        
        for (Node statement : statements) {
            result = statement.evaluate(context);
            
            // Early return from blocks if we hit a return statement
            if (result instanceof ReturnValue) {
                return result;
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
                "{ \"type\": \"BlockStatement\", \"position\": \"%s\", \"statements\": [ %s ] }",
                position,
                statementsJson
        );
    }
} 