package interpreter.runtime;

/**
 * Error thrown when a script exceeds configured resource limits.
 * 
 * <p>This error is thrown when a script attempts to use more resources than allowed by the
 * configured {@link ResourceQuota}. The specific type of limit that was exceeded is indicated
 * by the {@link ResourceLimitType}.</p>
 * 
 * <p>Resource exhaustion errors help prevent malicious scripts from:</p>
 * <ul>
 *   <li>Creating infinite recursion (stack overflow)</li>
 *   <li>Running infinite loops</li>
 *   <li>Allocating excessive memory through variable creation</li>
 *   <li>Consuming excessive CPU time</li>
 * </ul>
 * 
 * <p>These errors are typically caught by the {@link interpreter.main.Interpreter} class
 * and reported to the user as part of the evaluation result.</p>
 */
public class ResourceExhaustionError extends RuntimeError {
    private static final long serialVersionUID = 1L;
    
    private final ResourceLimitType limitType;
    
    /**
     * Types of resource limits that can be exceeded
     */
    public enum ResourceLimitType {
        /** Indicates that the maximum call stack depth has been exceeded (prevents stack overflow) */
        EVALUATION_DEPTH("Maximum call stack depth exceeded"),
        
        /** Indicates that the maximum number of loop iterations has been exceeded (prevents infinite loops) */
        LOOP_ITERATIONS("Maximum loop iterations exceeded"),
        
        /** Indicates that the maximum number of variables has been exceeded (prevents memory exhaustion) */
        VARIABLE_COUNT("Maximum variable count exceeded"),
        
        /** Indicates that the maximum number of evaluation steps has been exceeded (prevents CPU exhaustion) */
        EVALUATION_STEPS("Maximum execution steps exceeded");
        
        private final String description;
        
        ResourceLimitType(String description) {
            this.description = description;
        }
        
        /**
         * @return A human-readable description of the resource limit
         */
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Creates a new ResourceExhaustionError with the specified limit type
     * 
     * @param limitType The type of resource limit exceeded
     * @param line Line number where the limit was exceeded
     * @param column Column where the limit was exceeded
     */
    public ResourceExhaustionError(ResourceLimitType limitType, int line, int column) {
        super(limitType.getDescription(), line, column);
        this.limitType = limitType;
    }
    
    /**
     * @return the type of resource limit that was exceeded
     */
    public ResourceLimitType getLimitType() {
        return limitType;
    }
} 