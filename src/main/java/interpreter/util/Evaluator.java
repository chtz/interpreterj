package interpreter.util;

import interpreter.runtime.ResourceExhaustionError;
import interpreter.runtime.ResourceExhaustionError.ResourceLimitType;
import interpreter.runtime.ResourceQuota;

/**
 * Utility class with common evaluation methods
 */
public class Evaluator {
    /**
     * Check if a value is truthy in the context of conditionals
     * 
     * @param value The value to check
     * @return true if the value is truthy, false otherwise
     */
    public static boolean isTruthy(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        if (value instanceof String) {
            return !((String) value).isEmpty();
        }
        return true;  // All other objects are truthy
    }
    
    /**
     * Apply prefix operator to a value
     * 
     * @param operator The prefix operator (-, !)
     * @param value The operand value
     * @return The result of the operation
     */
    public static Object applyPrefixOperator(String operator, Object value) {
        switch (operator) {
            case "-":
                if (value instanceof Number) {
                    if (value instanceof Integer) {
                        return -((Integer) value);
                    } else if (value instanceof Double) {
                        return -((Double) value);
                    }
                }
                return null;
            case "!":
                return !isTruthy(value);
            default:
                return null;
        }
    }
    
    /**
     * Apply infix operator to two values
     * 
     * @param left The left operand
     * @param operator The infix operator (+, -, *, /, etc.)
     * @param right The right operand
     * @param resourceQuota The resource quota containing the max string length limit
     * @return The result of the operation
     * @throws ResourceExhaustionError if string concatenation would result in a string 
     *         that exceeds the maximum allowed length
     */
    public static Object applyInfixOperator(Object left, String operator, Object right, ResourceQuota resourceQuota) throws ResourceExhaustionError {
        // Handle special case for string concatenation
        if (operator.equals("+") && (left instanceof String || right instanceof String)) {
            String leftStr = String.valueOf(left);
            String rightStr = String.valueOf(right);
            
            // Check for potential string size violation
            checkStringLength(leftStr, rightStr, resourceQuota);
            
            return leftStr + rightStr;
        }
        
        // Handle number operations
        if (left instanceof Number && right instanceof Number) {
            double leftVal = ((Number) left).doubleValue();
            double rightVal = ((Number) right).doubleValue();
            
            switch (operator) {
                case "+": return leftVal + rightVal;
                case "-": return leftVal - rightVal;
                case "*": return leftVal * rightVal;
                case "/": return leftVal / rightVal;
                case "%": return leftVal % rightVal;
                case "<": return leftVal < rightVal;
                case ">": return leftVal > rightVal;
                case "<=": return leftVal <= rightVal;
                case ">=": return leftVal >= rightVal;
                case "==": return leftVal == rightVal;
                case "!=": return leftVal != rightVal;
            }
        }
        
        // Handle boolean operations
        switch (operator) {
            case "&&": return isTruthy(left) && isTruthy(right);
            case "||": return isTruthy(left) || isTruthy(right);
            case "==": return left == null ? right == null : left.equals(right);
            case "!=": return left == null ? right != null : !left.equals(right);
        }
        
        return null;
    }
    
    /**
     * Check if concatenating two strings would exceed the maximum string length
     * 
     * @param left First string in the concatenation
     * @param right Second string in the concatenation
     * @param resourceQuota The resource quota containing the max string length limit
     * @throws ResourceExhaustionError if the resulting string would be too long
     */
    private static void checkStringLength(String left, String right, ResourceQuota resourceQuota) throws ResourceExhaustionError {
        int leftLength = left != null ? left.length() : 0;
        int rightLength = right != null ? right.length() : 0;
        
        if (leftLength + rightLength > resourceQuota.getMaxStringLength()) {
            throw new ResourceExhaustionError(
                ResourceLimitType.VARIABLE_COUNT, // Using VARIABLE_COUNT as it's closest to memory exhaustion
                0, 0
            );
        }
    }
} 