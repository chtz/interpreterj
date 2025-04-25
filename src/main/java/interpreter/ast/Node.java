package interpreter.ast;

import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * Base Node class for all AST nodes
 */
public abstract class Node {
    protected Position position;
    
    public Node() {
        this.position = new Position(0, 0);
    }
    
    /**
     * Position class to store line and column information
     */
    public static class Position {
        private final int line;
        private final int column;
        
        public Position(int line, int column) {
            this.line = line;
            this.column = column;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        @Override
        public String toString() {
            return line + ":" + column;
        }
    }
    
    /**
     * Set the position of this node
     */
    public void setPosition(int line, int column) {
        this.position = new Position(line, column);
    }
    
    /**
     * Get the position of this node
     */
    public Position getPosition() {
        return position;
    }
    
    /**
     * Track an evaluation step to prevent runaway execution
     * 
     * @param context The evaluation context
     * @throws RuntimeError if step limits are exceeded
     */
    protected void trackEvaluationStep(EvaluationContext context) throws RuntimeError {
        context.trackEvaluationStep(position);
    }
    
    /**
     * Evaluate this node in the given context
     * @param context The evaluation context
     * @return The result of evaluating this node
     * @throws RuntimeError If an error occurs during evaluation
     */
    public abstract Object evaluate(EvaluationContext context) throws RuntimeError;
    
    /**
     * Get a string representation of this node for debugging
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
    /**
     * Get a JSON representation of this node for visualization
     */
    public String toJson() {
        return "{ \"type\": \"" + getClass().getSimpleName() + "\" }";
    }
} 