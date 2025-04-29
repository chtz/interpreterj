package interpreter.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import interpreter.ast.Program;
import interpreter.lexer.Lexer;
import interpreter.parser.Parser;
import interpreter.runtime.CallableFunction;
import interpreter.runtime.EvaluationContext;
import interpreter.runtime.ResourceExhaustionError;
import interpreter.runtime.ResourceQuota;
import interpreter.runtime.RuntimeError;

/**
 * Main Interpreter class that orchestrates lexing, parsing, and evaluation of code.
 * This is the primary entry point for using the interpreter.
 */
public class Interpreter {
    private Program ast;
    private Consumer<EvaluationContext>[] libraryFunctionInitializers;
    private ResourceQuota resourceQuota;
    
	/**
     * Error class to represent parser or runtime errors
     */
    public static class Error {
        private final String message;
        private final int line;
        private final int column;
        
        public Error(String message, int line, int column) {
            this.message = message;
            this.line = line;
            this.column = column;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        @Override
        public String toString() {
            return String.format("Error at %d:%d: %s", line, column, message);
        }
    }
    
    /**
     * Parse result class to represent the result of parsing code
     */
    public static class ParseResult {
        private final boolean success;
        private final Program ast;
        private final List<Error> errors;
        
        public ParseResult(boolean success, Program ast, List<Error> errors) {
            this.success = success;
            this.ast = ast;
            this.errors = errors;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public Program getAst() {
            return ast;
        }
        
        public List<Error> getErrors() {
            return errors;
        }
    }
    
    /**
     * Evaluation result class to represent the result of evaluating code
     */
    public static class EvaluationResult {
        private final boolean success;
        private final Object result;
        private final List<Error> errors;
        
        public EvaluationResult(boolean success, Object result, List<Error> errors) {
            this.success = success;
            this.result = result;
            this.errors = errors;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public Object getResult() {
            return result;
        }
        
        public List<Error> getErrors() {
            return errors;
        }
    }
    
    /**
     * Creates a new Interpreter instance with fresh context.
     */
    @SuppressWarnings("unchecked")
	public Interpreter() {
    	this(new ResourceQuota(), new DefaultLibraryFunctionsInitializer(), 
             new StdIOLibraryFunctionsInitializer(), new MapLibraryFunctionsInitializer(),
             new ArrayLibraryFunctionsInitializer(), new StringLibraryFunctionsInitializer(),
             new RegexLibraryFunctionsInitializer(), new TypeLibraryFunctionsInitializer());
    }
    
    /**
     * Creates a new Interpreter instance with custom resource quotas
     */
    @SuppressWarnings("unchecked")
    public Interpreter(ResourceQuota resourceQuota) {
        this(resourceQuota, new DefaultLibraryFunctionsInitializer(), 
             new StdIOLibraryFunctionsInitializer(), new MapLibraryFunctionsInitializer(),
             new ArrayLibraryFunctionsInitializer(), new StringLibraryFunctionsInitializer(),
             new RegexLibraryFunctionsInitializer(), new TypeLibraryFunctionsInitializer());
    }
    
    @SuppressWarnings("unchecked")
	public Interpreter(Consumer<EvaluationContext>... libraryFunctionInitializers) {
    	this(new ResourceQuota(), libraryFunctionInitializers);
    }
    
    @SuppressWarnings("unchecked")
    public Interpreter(ResourceQuota resourceQuota, Consumer<EvaluationContext>... libraryFunctionInitializers) {
    	this.ast = null;
    	this.libraryFunctionInitializers = libraryFunctionInitializers;
        this.resourceQuota = resourceQuota;
    }
    
    /**
     * Register built-in functions that should be available by default
     */
	private void registerBuiltInFunctions(EvaluationContext context) { 
		for (Consumer<EvaluationContext> libraryFunctionInitializer : libraryFunctionInitializers) { 
			libraryFunctionInitializer.accept(context);
		}
    }
    
    /**
     * Parse the source code and generate an AST
     */
    public ParseResult parse(String sourceCode) {
        try {
            // Create lexer and parser
            Lexer lexer = new Lexer(sourceCode);
            Parser parser = new Parser(lexer);
            
            // Parse the program to generate AST
            this.ast = parser.parseProgram();
            
            // Collect any errors from the parser
            List<Error> errors = new ArrayList<>();
            for (Parser.Error error : parser.getErrors()) {
                errors.add(new Error(error.getMessage(), error.getLine(), error.getColumn()));
            }
            
            return new ParseResult(errors.isEmpty(), this.ast, errors);
        } catch (Exception e) {
        	List<Error> errors = new ArrayList<>();
            errors.add(new Error("Unexpected error: " + e.getMessage(), 0, 0));
            
            return new ParseResult(false, null, errors);
        }
    }
    
    /**
     * Evaluate the AST and return the result
     */
    public EvaluationResult evaluate() {
    	List<Error> errors = new ArrayList<>();
        
        try {
        	EvaluationContext context = new EvaluationContext(resourceQuota);
            
            // Re-register built-in functions
            registerBuiltInFunctions(context); 
            
            // Check if we have a valid AST
            if (this.ast == null) {
                errors.add(new Error("No AST to evaluate. Parse code first.", 0, 0));
                
                return new EvaluationResult(false, null, errors);
            }
            
            Object result = this.ast.evaluate(context);
            
            return new EvaluationResult(true, result, new ArrayList<>());
        } catch (ResourceExhaustionError e) {
            // Handle resource exhaustion errors
            errors.add(new Error(e.getMessage(), e.getLine(), e.getColumn()));
            
            return new EvaluationResult(false, null, errors);
        } catch (RuntimeError e) {
            // Handle runtime errors
            errors.add(new Error(e.getMessage(), e.getLine(), e.getColumn()));
            
            return new EvaluationResult(false, null, errors);
        } catch (Exception e) {
            // Handle unexpected errors
            errors.add(new Error("Unexpected error: " + e.getMessage(), 0, 0));
            
            return new EvaluationResult(false, null, errors);
        }
    }
    
    /**
     * Return a JSON representation of the AST for visualization
     */
    public String getAstJson() {
        if (this.ast == null) {
            return null;
        }
        
        return this.ast.toJson();
    }
    
    /**
     * Format errors as a string
     */
    public static String formatErrors(List<Error> errors) { // FIXME refactoring move
        if (errors.isEmpty()) {
            return "No errors";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Error error : errors) {
            sb.append(error.toString()).append("\n");
        }
        
        return sb.toString();
    }
    
    public final static class DefaultLibraryFunctionsInitializer implements Consumer<EvaluationContext> {
		@Override
		public void accept(EvaluationContext ec) {
			ec.registerFunction("assert", args -> {
	            try {
	            	boolean test = (Boolean) args.get(0);
	            	String message = (String) args.get(1);
	            	if (!test) throw new AssertionError(message);
	            	return null;
	            }
	            catch (NumberFormatException e) {
	            	return null;
	            }
	        });
			
			ec.registerFunction("echo", args -> {
	            return args.get(0);
	        });
	    	
			ec.registerFunction("int", args -> {
	            try {
	            	return Integer.parseInt(args.get(0).toString());
	            }
	            catch (NumberFormatException e) {
	            	return null;
	            }
	        });
			
			ec.registerFunction("double", args -> {
	            try {
	            	return Double.parseDouble(args.get(0).toString());
	            }
	            catch (NumberFormatException e) {
	            	return null;
	            }
	        });
			
			ec.registerFunction("string", args -> {
	            try {
	            	return args.get(0).toString();
	            }
	            catch (NumberFormatException e) {
	            	return null;
	            }
	        });
		}
	}
    
    public final static class StdIOLibraryFunctionsInitializer implements Consumer<EvaluationContext> {
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		final PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));

		@Override
		public void accept(EvaluationContext ec) {
	    	ec.registerFunction("gets", args -> {
	            try {
					return in.readLine();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
	        });
	    	
	    	ec.registerFunction("puts", args -> {
	    		out.write(args.get(0).toString());
	    		out.write('\n');
	    		out.flush();
	    		return null;
	        });
		}
	}
    
    public final static class ArrayLibraryFunctionsInitializer implements Consumer<EvaluationContext> {
        @Override
        public void accept(EvaluationContext ec) {
            // len(array) - Get the length of an array
            ec.registerFunction("len", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("len() requires 1 argument");
                }
                
                Object arg = args.get(0);
                if (arg instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> array = (List<Object>) arg;
                    return (double) array.size();
                }
                
                if (arg instanceof String) {
                    return (double) ((String) arg).length();
                }
                
                if (arg instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> map = (Map<Object, Object>) arg;
                    return (double) map.size();
                }
                
                throw new RuntimeException("len() argument must be an array, string, or map");
            });
            
            // push(array, value) - Add a value to the end of an array
            ec.registerFunction("push", args -> {
                if (args.size() < 2) {
                    throw new RuntimeException("push() requires 2 arguments");
                }
                
                Object arrayArg = args.get(0);
                Object value = args.get(1);
                
                if (!(arrayArg instanceof List)) {
                    throw new RuntimeException("First argument to push() must be an array");
                }
                
                @SuppressWarnings("unchecked")
                List<Object> array = (List<Object>) arrayArg;
                array.add(value);
                
                return value;
            });
            
            // pop(array) - Remove and return the last element from an array
            ec.registerFunction("pop", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("pop() requires 1 argument");
                }
                
                Object arrayArg = args.get(0);
                
                if (!(arrayArg instanceof List)) {
                    throw new RuntimeException("Argument to pop() must be an array");
                }
                
                @SuppressWarnings("unchecked")
                List<Object> array = (List<Object>) arrayArg;
                
                if (array.isEmpty()) {
                    throw new RuntimeException("Cannot pop from an empty array");
                }
                
                return array.remove(array.size() - 1);
            });
            
            // delete(array, index) - Remove an element at a specific index
            ec.registerFunction("delete", args -> {
                if (args.size() < 2) {
                    throw new RuntimeException("delete() requires 2 arguments");
                }
                
                Object collectionArg = args.get(0);
                Object keyOrIndexArg = args.get(1);
                
                if (collectionArg instanceof List) {
                    if (!(keyOrIndexArg instanceof Number)) {
                        throw new RuntimeException("Second argument to delete() must be a number for arrays");
                    }
                    
                    @SuppressWarnings("unchecked")
                    List<Object> array = (List<Object>) collectionArg;
                    int index = ((Number) keyOrIndexArg).intValue();
                    
                    if (index < 0 || index >= array.size()) {
                        throw new RuntimeException("Array index out of bounds: " + index);
                    }
                    
                    return array.remove(index);
                } else if (collectionArg instanceof Map) {
                    if (!(keyOrIndexArg instanceof String || keyOrIndexArg instanceof Number)) {
                        throw new RuntimeException("Second argument to delete() for maps must be a string or number");
                    }
                    
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> map = (Map<Object, Object>) collectionArg;
                    
                    if (!map.containsKey(keyOrIndexArg)) {
                        return null; // Key doesn't exist
                    }
                    
                    return map.remove(keyOrIndexArg);
                }
                
                throw new RuntimeException("First argument to delete() must be an array or map");
            });
        }
    }
    
    /**
     * Library initializer for map-related functions
     */
    public final static class MapLibraryFunctionsInitializer implements Consumer<EvaluationContext> {
        @Override
        public void accept(EvaluationContext ec) {
            // keys(map) - Return array of all keys in the map
            ec.registerFunction("keys", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("keys() requires 1 argument");
                }
                
                Object arg = args.get(0);
                if (!(arg instanceof Map)) {
                    throw new RuntimeException("Argument to keys() must be a map");
                }
                
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) arg;
                
                return new ArrayList<>(map.keySet());
            });
            
            // values(map) - Return array of all values in the map
            ec.registerFunction("values", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("values() requires 1 argument");
                }
                
                Object arg = args.get(0);
                if (!(arg instanceof Map)) {
                    throw new RuntimeException("Argument to values() must be a map");
                }
                
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) arg;
                
                return new ArrayList<>(map.values());
            });
        }
    }
    
    /**
     * Library initializer for string manipulation functions
     */
    public final static class StringLibraryFunctionsInitializer implements Consumer<EvaluationContext> {
        @Override
        public void accept(EvaluationContext ec) {
            // char(string, index) - Get character at specific index
            ec.registerFunction("char", args -> {
                if (args.size() < 2) {
                    throw new RuntimeException("char() requires 2 arguments");
                }
                
                Object strArg = args.get(0);
                Object indexArg = args.get(1);
                
                if (!(strArg instanceof String)) {
                    throw new RuntimeException("First argument to char() must be a string");
                }
                
                if (!(indexArg instanceof Number)) {
                    throw new RuntimeException("Second argument to char() must be a number");
                }
                
                String str = (String) strArg;
                int index = ((Number) indexArg).intValue();
                
                if (index < 0 || index >= str.length()) {
                    throw new RuntimeException("String index out of bounds: " + index);
                }
                
                return String.valueOf(str.charAt(index));
            });
            
            // ord(char) - Get ASCII/Unicode code point of character
            ec.registerFunction("ord", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("ord() requires 1 argument");
                }
                
                Object charArg = args.get(0);
                
                if (!(charArg instanceof String)) {
                    throw new RuntimeException("Argument to ord() must be a string");
                }
                
                String str = (String) charArg;
                
                if (str.length() != 1) {
                    throw new RuntimeException("Argument to ord() must be a single character");
                }
                
                return (double) str.charAt(0);
            });
            
            // chr(code) - Convert code point to character
            ec.registerFunction("chr", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("chr() requires 1 argument");
                }
                
                Object codeArg = args.get(0);
                
                if (!(codeArg instanceof Number)) {
                    throw new RuntimeException("Argument to chr() must be a number");
                }
                
                int code = ((Number) codeArg).intValue();
                
                return String.valueOf((char) code);
            });
            
            // substr(string, start, length) - Get substring
            ec.registerFunction("substr", args -> {
                if (args.size() < 3) {
                    throw new RuntimeException("substr() requires 3 arguments");
                }
                
                Object strArg = args.get(0);
                Object startArg = args.get(1);
                Object lengthArg = args.get(2);
                
                if (!(strArg instanceof String)) {
                    throw new RuntimeException("First argument to substr() must be a string");
                }
                
                if (!(startArg instanceof Number)) {
                    throw new RuntimeException("Second argument to substr() must be a number");
                }
                
                if (!(lengthArg instanceof Number)) {
                    throw new RuntimeException("Third argument to substr() must be a number");
                }
                
                String str = (String) strArg;
                int start = ((Number) startArg).intValue();
                int length = ((Number) lengthArg).intValue();
                
                if (start < 0) {
                    throw new RuntimeException("Start index cannot be negative");
                }
                
                if (length < 0) {
                    throw new RuntimeException("Length cannot be negative");
                }
                
                if (start >= str.length()) {
                    return "";
                }
                
                int end = Math.min(start + length, str.length());
                
                return str.substring(start, end);
            });
            
            // startsWith(string, prefix) - Check if string starts with prefix
            ec.registerFunction("startsWith", args -> {
                if (args.size() < 2) {
                    throw new RuntimeException("startsWith() requires 2 arguments");
                }
                
                Object strArg = args.get(0);
                Object prefixArg = args.get(1);
                
                if (!(strArg instanceof String)) {
                    throw new RuntimeException("First argument to startsWith() must be a string");
                }
                
                if (!(prefixArg instanceof String)) {
                    throw new RuntimeException("Second argument to startsWith() must be a string");
                }
                
                String str = (String) strArg;
                String prefix = (String) prefixArg;
                
                return str.startsWith(prefix);
            });
            
            // endsWith(string, suffix) - Check if string ends with suffix
            ec.registerFunction("endsWith", args -> {
                if (args.size() < 2) {
                    throw new RuntimeException("endsWith() requires 2 arguments");
                }
                
                Object strArg = args.get(0);
                Object suffixArg = args.get(1);
                
                if (!(strArg instanceof String)) {
                    throw new RuntimeException("First argument to endsWith() must be a string");
                }
                
                if (!(suffixArg instanceof String)) {
                    throw new RuntimeException("Second argument to endsWith() must be a string");
                }
                
                String str = (String) strArg;
                String suffix = (String) suffixArg;
                
                return str.endsWith(suffix);
            });
            
            // trim(string) - Trim whitespace
            ec.registerFunction("trim", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("trim() requires 1 argument");
                }
                
                Object strArg = args.get(0);
                
                if (!(strArg instanceof String)) {
                    throw new RuntimeException("Argument to trim() must be a string");
                }
                
                String str = (String) strArg;
                
                return str.trim();
            });
            
            // join(array, delimiter) - Join array elements with delimiter
            ec.registerFunction("join", args -> {
                if (args.size() < 2) {
                    throw new RuntimeException("join() requires 2 arguments");
                }
                
                Object arrayArg = args.get(0);
                Object delimiterArg = args.get(1);
                
                if (!(arrayArg instanceof List)) {
                    throw new RuntimeException("First argument to join() must be an array");
                }
                
                if (!(delimiterArg instanceof String)) {
                    throw new RuntimeException("Second argument to join() must be a string");
                }
                
                @SuppressWarnings("unchecked")
                List<Object> array = (List<Object>) arrayArg;
                String delimiter = (String) delimiterArg;
                
                StringBuilder result = new StringBuilder();
                
                for (int i = 0; i < array.size(); i++) {
                    result.append(array.get(i));
                    
                    if (i < array.size() - 1) {
                        result.append(delimiter);
                    }
                }
                
                return result.toString();
            });
        }
    }
    
    /**
     * Library initializer for regular expression functions
     */
    public final static class RegexLibraryFunctionsInitializer implements Consumer<EvaluationContext> {
        @Override
        public void accept(EvaluationContext ec) {
            // match(string, pattern) - Match pattern against string
            ec.registerFunction("match", args -> {
                if (args.size() < 2) {
                    throw new RuntimeException("match() requires 2 arguments");
                }
                
                Object strArg = args.get(0);
                Object patternArg = args.get(1);
                
                if (!(strArg instanceof String)) {
                    throw new RuntimeException("First argument to match() must be a string");
                }
                
                if (!(patternArg instanceof String)) {
                    throw new RuntimeException("Second argument to match() must be a string");
                }
                
                try {
                    String str = (String) strArg;
                    String pattern = (String) patternArg;
                    // Unescape backslashes in the pattern
                    pattern = pattern.replace("\\\\", "\\");
                    
                    return str.matches(pattern);
                } catch (java.util.regex.PatternSyntaxException e) {
                    throw new RuntimeException("Invalid regex pattern: " + e.getMessage());
                }
            });
            
            // findAll(string, pattern) - Find all matches
            ec.registerFunction("findAll", args -> {
                if (args.size() < 2) {
                    throw new RuntimeException("findAll() requires 2 arguments");
                }
                
                Object strArg = args.get(0);
                Object patternArg = args.get(1);
                
                if (!(strArg instanceof String)) {
                    throw new RuntimeException("First argument to findAll() must be a string");
                }
                
                if (!(patternArg instanceof String)) {
                    throw new RuntimeException("Second argument to findAll() must be a string");
                }
                
                try {
                    String str = (String) strArg;
                    String patternStr = (String) patternArg;
                    // Unescape backslashes in the pattern
                    patternStr = patternStr.replace("\\\\", "\\");
                    
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternStr);
                    java.util.regex.Matcher matcher = pattern.matcher(str);
                    
                    List<String> matches = new ArrayList<>();
                    while (matcher.find()) {
                        matches.add(matcher.group());
                    }
                    
                    return matches;
                } catch (java.util.regex.PatternSyntaxException e) {
                    throw new RuntimeException("Invalid regex pattern: " + e.getMessage());
                }
            });
            
            // replace(string, pattern, replacement) - Replace pattern with replacement
            ec.registerFunction("replace", args -> {
                if (args.size() < 3) {
                    throw new RuntimeException("replace() requires 3 arguments");
                }
                
                Object strArg = args.get(0);
                Object patternArg = args.get(1);
                Object replacementArg = args.get(2);
                
                if (!(strArg instanceof String)) {
                    throw new RuntimeException("First argument to replace() must be a string");
                }
                
                if (!(patternArg instanceof String)) {
                    throw new RuntimeException("Second argument to replace() must be a string");
                }
                
                if (!(replacementArg instanceof String)) {
                    throw new RuntimeException("Third argument to replace() must be a string");
                }
                
                try {
                    String str = (String) strArg;
                    String patternStr = (String) patternArg;
                    String replacement = (String) replacementArg;
                    // Unescape backslashes in the pattern
                    patternStr = patternStr.replace("\\\\", "\\");
                    
                    return str.replaceAll(patternStr, replacement);
                } catch (java.util.regex.PatternSyntaxException e) {
                    throw new RuntimeException("Invalid regex pattern: " + e.getMessage());
                }
            });
            
            // split(string, pattern) - Split string by pattern
            ec.registerFunction("split", args -> {
                if (args.size() < 2) {
                    throw new RuntimeException("split() requires 2 arguments");
                }
                
                Object strArg = args.get(0);
                Object patternArg = args.get(1);
                
                if (!(strArg instanceof String)) {
                    throw new RuntimeException("First argument to split() must be a string");
                }
                
                if (!(patternArg instanceof String)) {
                    throw new RuntimeException("Second argument to split() must be a string");
                }
                
                try {
                    String str = (String) strArg;
                    String patternStr = (String) patternArg;
                    // Unescape backslashes in the pattern
                    patternStr = patternStr.replace("\\\\", "\\");
                    
                    // Use -1 as the limit to preserve trailing empty strings
                    String[] parts = str.split(patternStr, -1);
                    
                    List<String> result = new ArrayList<>();
                    for (String part : parts) {
                        result.add(part);
                    }
                    
                    return result;
                } catch (java.util.regex.PatternSyntaxException e) {
                    throw new RuntimeException("Invalid regex pattern: " + e.getMessage());
                }
            });
        }
    }
    
    /**
     * Library initializer for type checking functions
     */
    public final static class TypeLibraryFunctionsInitializer implements Consumer<EvaluationContext> {
        @Override
        public void accept(EvaluationContext ec) {
            // typeof(value) - Get type of value
            ec.registerFunction("typeof", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("typeof() requires 1 argument");
                }
                
                Object arg = args.get(0);
                
                if (arg == null) {
                    return "null";
                } else if (arg instanceof Number) {
                    return "number";
                } else if (arg instanceof String) {
                    return "string";
                } else if (arg instanceof Boolean) {
                    return "boolean";
                } else if (arg instanceof List) {
                    return "array";
                } else if (arg instanceof Map) {
                    return "map";
                } else if (arg instanceof CallableFunction) {
                    return "function";
                } else {
                    return "object";
                }
            });
            
            // isNumber(value) - Check if value is a number
            ec.registerFunction("isNumber", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("isNumber() requires 1 argument");
                }
                
                return args.get(0) instanceof Number;
            });
            
            // isString(value) - Check if value is a string
            ec.registerFunction("isString", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("isString() requires 1 argument");
                }
                
                return args.get(0) instanceof String;
            });
            
            // isBoolean(value) - Check if value is a boolean
            ec.registerFunction("isBoolean", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("isBoolean() requires 1 argument");
                }
                
                return args.get(0) instanceof Boolean;
            });
            
            // isArray(value) - Check if value is an array
            ec.registerFunction("isArray", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("isArray() requires 1 argument");
                }
                
                return args.get(0) instanceof List;
            });
            
            // isMap(value) - Check if value is a map
            ec.registerFunction("isMap", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("isMap() requires 1 argument");
                }
                
                return args.get(0) instanceof Map;
            });
            
            // isFunction(value) - Check if value is a function
            ec.registerFunction("isFunction", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("isFunction() requires 1 argument");
                }
                
                return args.get(0) instanceof CallableFunction;
            });
            
            // isNull(value) - Check if value is null
            ec.registerFunction("isNull", args -> {
                if (args.isEmpty()) {
                    throw new RuntimeException("isNull() requires 1 argument");
                }
                
                return args.get(0) == null;
            });
        }
    }
    
    /**
     * Get the current resource quota
     */
    public ResourceQuota getResourceQuota() {
        return resourceQuota;
    }
    
    /**
     * Set a new resource quota
     */
    public void setResourceQuota(ResourceQuota resourceQuota) {
        this.resourceQuota = resourceQuota;
    }
}
