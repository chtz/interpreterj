package interpreter.util;

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
     * @return The result of the operation
     */
    public static Object applyInfixOperator(Object left, String operator, Object right) {
        // Handle special case for string concatenation
        if (operator.equals("+") && (left instanceof String || right instanceof String)) {
            return String.valueOf(left) + String.valueOf(right);
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
} 