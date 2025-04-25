package interpreter.runtime;

/**
 * Defines resource limits for interpreter execution to prevent malicious scripts
 * from consuming excessive resources through recursion, loops, or creating too many variables.
 * 
 * <p>This class provides configurable thresholds for:</p>
 * <ul>
 *   <li>Max evaluation depth - prevents stack overflow through deep recursion</li>
 *   <li>Max loop iterations - prevents infinite loops</li>
 *   <li>Max variable count - prevents memory exhaustion</li>
 *   <li>Max evaluation steps - provides a CPU usage proxy</li>
 *   <li>Max string length - prevents memory exhaustion from huge strings</li>
 * </ul>
 * 
 * <p>Example usage:</p>
 * <pre>
 * // Create a custom quota with restrictive limits
 * ResourceQuota quota = new ResourceQuota(50, 1000, 100, 10000, 100000);
 * 
 * // Create an interpreter with this quota
 * Interpreter interpreter = new Interpreter(quota);
 * </pre>
 */
public class ResourceQuota {
    // Default values for resource limits
    private static final int DEFAULT_MAX_EVALUATION_DEPTH = 500;
    private static final int DEFAULT_MAX_LOOP_ITERATIONS = 10000;
    private static final int DEFAULT_MAX_VARIABLE_COUNT = 1000;
    private static final int DEFAULT_MAX_EVALUATION_STEPS = 100000;
    private static final int DEFAULT_MAX_STRING_LENGTH = 1000000;
    
    // Configurable limits
    private final int maxEvaluationDepth;     // Limits recursion depth
    private final int maxLoopIterations;      // Limits iterations in loops
    private final int maxVariableCount;       // Limits number of variables
    private final int maxEvaluationSteps;     // Limits total evaluation steps
    private final int maxStringLength;        // Limits string length to prevent memory exhaustion
    
    /**
     * Creates a ResourceQuota with default limits.
     * <p>
     * Default values are:
     * <ul>
     *   <li>Max evaluation depth: 500</li>
     *   <li>Max loop iterations: 10,000</li>
     *   <li>Max variable count: 1,000</li>
     *   <li>Max evaluation steps: 100,000</li>
     *   <li>Max string length: 1,000,000</li>
     * </ul>
     */
    public ResourceQuota() {
        this(DEFAULT_MAX_EVALUATION_DEPTH, DEFAULT_MAX_LOOP_ITERATIONS, 
             DEFAULT_MAX_VARIABLE_COUNT, DEFAULT_MAX_EVALUATION_STEPS,
             DEFAULT_MAX_STRING_LENGTH);
    }
    
    /**
     * Creates a ResourceQuota with custom limits
     * 
     * @param maxEvaluationDepth Maximum recursion/function call depth
     * @param maxLoopIterations Maximum iterations across all loops
     * @param maxVariableCount Maximum number of variables that can be created
     * @param maxEvaluationSteps Maximum total evaluation steps (CPU proxy)
     * @param maxStringLength Maximum string length to prevent memory exhaustion
     */
    public ResourceQuota(int maxEvaluationDepth, int maxLoopIterations, 
                         int maxVariableCount, int maxEvaluationSteps,
                         int maxStringLength) {
        this.maxEvaluationDepth = maxEvaluationDepth;
        this.maxLoopIterations = maxLoopIterations;
        this.maxVariableCount = maxVariableCount;
        this.maxEvaluationSteps = maxEvaluationSteps;
        this.maxStringLength = maxStringLength;
    }
    
    /**
     * Creates a ResourceQuota with custom limits, using the default string length limit
     * 
     * @param maxEvaluationDepth Maximum recursion/function call depth
     * @param maxLoopIterations Maximum iterations across all loops
     * @param maxVariableCount Maximum number of variables that can be created
     * @param maxEvaluationSteps Maximum total evaluation steps (CPU proxy)
     */
    public ResourceQuota(int maxEvaluationDepth, int maxLoopIterations, 
                         int maxVariableCount, int maxEvaluationSteps) {
        this(maxEvaluationDepth, maxLoopIterations, maxVariableCount, 
             maxEvaluationSteps, DEFAULT_MAX_STRING_LENGTH);
    }
    
    /**
     * @return the maximum allowed evaluation/call stack depth
     */
    public int getMaxEvaluationDepth() {
        return maxEvaluationDepth;
    }
    
    /**
     * @return the maximum allowed loop iterations
     */
    public int getMaxLoopIterations() {
        return maxLoopIterations;
    }
    
    /**
     * @return the maximum allowed variable count
     */
    public int getMaxVariableCount() {
        return maxVariableCount;
    }
    
    /**
     * @return the maximum allowed evaluation steps
     */
    public int getMaxEvaluationSteps() {
        return maxEvaluationSteps;
    }
    
    /**
     * @return the maximum allowed string length
     */
    public int getMaxStringLength() {
        return maxStringLength;
    }
} 