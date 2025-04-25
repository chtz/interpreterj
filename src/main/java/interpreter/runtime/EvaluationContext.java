package interpreter.runtime;

import java.util.HashMap;
import java.util.Map;

import interpreter.ast.Node;
import interpreter.runtime.ResourceExhaustionError.ResourceLimitType;

/**
 * Environment to store variables and functions in the current scope
 */
public class EvaluationContext {
    private final EvaluationContext parent;
    private final Map<String, Object> values;
    private final Map<String, CallableFunction> functions;
    
    // Resource tracking fields - shared across all context instances
    private final ResourceUsage resourceUsage;
    private final ResourceQuota resourceQuota;
    
    public EvaluationContext() throws RuntimeError {
        this(null, new ResourceQuota(), new ResourceUsage());
    }
    
    public EvaluationContext(EvaluationContext parent) throws RuntimeError {
        this(parent, 
            parent != null ? parent.getResourceQuota() : new ResourceQuota(), 
            parent != null ? parent.getResourceUsage() : new ResourceUsage());
    }
    
    public EvaluationContext(ResourceQuota customQuota) throws RuntimeError {
        this(null, customQuota, new ResourceUsage());
    }
    
    private EvaluationContext(EvaluationContext parent, ResourceQuota resourceQuota, ResourceUsage resourceUsage) throws RuntimeError {
        this.parent = parent;
        this.values = new HashMap<>();
        this.functions = new HashMap<>();
        this.resourceQuota = resourceQuota;
        this.resourceUsage = resourceUsage;
        
        // Track context depth for recursion protection
        if (parent != null) {
            this.resourceUsage.incrementEvaluationDepth();
            // Check depth immediately after incrementing
            try {
                checkEvaluationDepth(null);
            } catch (ResourceExhaustionError e) {
                // If depth is exceeded during construction, decrement it back and rethrow
                this.resourceUsage.decrementEvaluationDepth();
                throw new RuntimeError(e.getMessage(), 0, 0);
            }
        }
    }
    
    /**
     * Create a new nested scope
     */
    public EvaluationContext extend() throws RuntimeError {
        // Check depth limit before creating a new context
        if (resourceUsage.getEvaluationDepth() + 1 > resourceQuota.getMaxEvaluationDepth()) {
            throw new ResourceExhaustionError(
                ResourceLimitType.EVALUATION_DEPTH,
                0, 0
            );
        }
        
        return new EvaluationContext(this, this.resourceQuota, this.resourceUsage);
    }
    
    /**
     * Define a variable in the current scope
     */
    public Object define(String name, Object value) throws RuntimeError {
        // Track variable count for memory protection
        resourceUsage.incrementVariableCount();
        checkVariableCount(null);
        
        // Check for oversized string values that could exhaust memory
        if (value instanceof String && ((String) value).length() > resourceQuota.getMaxStringLength()) {
            throw new ResourceExhaustionError(
                ResourceLimitType.VARIABLE_COUNT,
                0, 0
            );
        }
        
        values.put(name, value);
        return value;
    }
    
    /**
     * Get a variable from the current or parent scopes
     */
    public Object get(String name, Node.Position position) throws RuntimeError {
        // Increment evaluation steps
        resourceUsage.incrementEvaluationSteps();
        checkEvaluationSteps(position);
        
        // Check current scope
        if (values.containsKey(name)) {
            return values.get(name);
        }
        
        // Check library functions
        if (functions.containsKey(name)) {
            return functions.get(name);
        }
        
        // Look in parent scope
        if (parent != null) {
            return parent.get(name, position);
        }
        
        // Not found
        throw new RuntimeError(
                "Undefined variable '" + name + "'", 
                position != null ? position.getLine() : 0,
                position != null ? position.getColumn() : 0
        );
    }
    
    /**
     * Assign a value to a variable in the current or parent scopes
     */
    public Object assign(String name, Object value, Node.Position position) throws RuntimeError {
        // Increment evaluation steps
        resourceUsage.incrementEvaluationSteps();
        checkEvaluationSteps(position);
        
        // Check current scope
        if (values.containsKey(name)) {
            values.put(name, value);
            return value;
        }
        
        // Try to assign in parent scope
        if (parent != null) {
            return parent.assign(name, value, position);
        }
        
        // Not found
        throw new RuntimeError(
                "Cannot assign to undefined variable '" + name + "'", 
                position != null ? position.getLine() : 0,
                position != null ? position.getColumn() : 0
        );
    }
    
    /**
     * Register a library function
     */
    public CallableFunction registerFunction(String name, CallableFunction function) {
        functions.put(name, function);
        return function;
    }
    
    /**
     * @return The resource quota configured for this context
     */
    public ResourceQuota getResourceQuota() {
        return resourceQuota;
    }
    
    /**
     * @return The current resource usage tracking object
     */
    public ResourceUsage getResourceUsage() {
        return resourceUsage;
    }
    
    /**
     * Get the current evaluation depth
     */
    public int getEvaluationDepth() {
        return resourceUsage.getEvaluationDepth();
    }
    
    /**
     * Increment and check loop iterations counter
     * 
     * @param position Source position for error reporting
     * @throws RuntimeError if loop iterations limit is exceeded
     */
    public void trackLoopIteration(Node.Position position) throws RuntimeError {
        resourceUsage.incrementLoopIterations();
        checkLoopIterations(position);
        
        // Also track general evaluation steps
        resourceUsage.incrementEvaluationSteps();
        checkEvaluationSteps(position);
    }
    
    /**
     * Track evaluation step and check against limit
     * 
     * @param position Source position for error reporting
     * @throws RuntimeError if evaluation steps limit is exceeded
     */
    public void trackEvaluationStep(Node.Position position) throws RuntimeError {
        resourceUsage.incrementEvaluationSteps();
        checkEvaluationSteps(position);
    }
    
    /**
     * Track evaluation depth and check against limit
     * Used when entering a function call to prevent too deep recursion
     * 
     * @param position Source position for error reporting
     * @throws RuntimeError if evaluation depth limit is exceeded
     */
    public void trackEvaluationDepth(Node.Position position) throws RuntimeError {
        resourceUsage.incrementEvaluationDepth();
        checkEvaluationDepth(position);
    }
    
    /**
     * Decrement the evaluation depth
     * Used when exiting a function call
     */
    public void exitEvaluationDepth() {
        resourceUsage.decrementEvaluationDepth();
    }
    
    // Helper methods to check resource limits
    
    private void checkEvaluationDepth(Node.Position position) throws ResourceExhaustionError {
        if (resourceUsage.getEvaluationDepth() > resourceQuota.getMaxEvaluationDepth()) {
            throw new ResourceExhaustionError(
                ResourceLimitType.EVALUATION_DEPTH,
                position != null ? position.getLine() : 0,
                position != null ? position.getColumn() : 0
            );
        }
    }
    
    private void checkLoopIterations(Node.Position position) throws ResourceExhaustionError {
        if (resourceUsage.getLoopIterations() > resourceQuota.getMaxLoopIterations()) {
            throw new ResourceExhaustionError(
                ResourceLimitType.LOOP_ITERATIONS,
                position != null ? position.getLine() : 0,
                position != null ? position.getColumn() : 0
            );
        }
    }
    
    private void checkVariableCount(Node.Position position) throws ResourceExhaustionError {
        if (resourceUsage.getVariableCount() > resourceQuota.getMaxVariableCount()) {
            throw new ResourceExhaustionError(
                ResourceLimitType.VARIABLE_COUNT,
                position != null ? position.getLine() : 0,
                position != null ? position.getColumn() : 0
            );
        }
    }
    
    private void checkEvaluationSteps(Node.Position position) throws ResourceExhaustionError {
        if (resourceUsage.getEvaluationSteps() > resourceQuota.getMaxEvaluationSteps()) {
            throw new ResourceExhaustionError(
                ResourceLimitType.EVALUATION_STEPS,
                position != null ? position.getLine() : 0,
                position != null ? position.getColumn() : 0
            );
        }
    }
    
    /**
     * Helper class to track resource usage statistics
     */
    public static class ResourceUsage {
        private int evaluationDepth = 0;
        private int loopIterations = 0;
        private int variableCount = 0;
        private int evaluationSteps = 0;
        
        public void incrementEvaluationDepth() {
            evaluationDepth++;
        }
        
        public void incrementLoopIterations() {
            loopIterations++;
        }
        
        public void incrementVariableCount() {
            variableCount++;
        }
        
        public void incrementEvaluationSteps() {
            evaluationSteps++;
        }
        
        public int getEvaluationDepth() {
            return evaluationDepth;
        }
        
        public int getLoopIterations() {
            return loopIterations;
        }
        
        public int getVariableCount() {
            return variableCount;
        }
        
        public int getEvaluationSteps() {
            return evaluationSteps;
        }
        
        public void decrementEvaluationDepth() {
            evaluationDepth--;
        }
    }
} 