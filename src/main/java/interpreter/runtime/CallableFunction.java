package interpreter.runtime;

import java.util.List;

/**
 * Interface for function implementation
 */
public interface CallableFunction extends java.util.function.Function<List<Object>, Object> {}