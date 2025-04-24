package interpreter.runtime;

/**
 * Return value object used for function returns and control flow
 */
public class ReturnValue {
    private final Object value;
    
    public ReturnValue(Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "Return(" + (value == null ? "null" : value.toString()) + ")";
    }
} 