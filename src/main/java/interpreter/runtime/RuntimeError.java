package interpreter.runtime;

/**
 * Runtime error with position information
 */
public class RuntimeError extends Exception {
    private static final long serialVersionUID = 1L;
    
	private final int line;
    private final int column;
    
    public RuntimeError(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }
    
    public RuntimeError(String message, Throwable cause, int line, int column) {
        super(message, cause);
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
        return String.format("Runtime Error at %d:%d: %s", line, column, getMessage());
    }
} 