package interpreter.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import interpreter.ast.Program;
import interpreter.lexer.Lexer;
import interpreter.parser.Parser;
import interpreter.runtime.EvaluationContext;
import interpreter.runtime.RuntimeError;

/**
 * Main Interpreter class that orchestrates lexing, parsing, and evaluation of code.
 * This is the primary entry point for using the interpreter.
 */
public class Interpreter {
    private Program ast;
    private Consumer<EvaluationContext>[] libraryFunctionInitializers; 
    
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
    	this(new DefaultLibraryFunctionsInitializer(), new StdIOLibraryFunctionsInitializer());
    }
    
    @SuppressWarnings("unchecked")
	public Interpreter(Consumer<EvaluationContext>... libraryFunctionInitializers) {
    	this.ast = null;
    	this.libraryFunctionInitializers = libraryFunctionInitializers;
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
        	EvaluationContext context = new EvaluationContext();
            
            // Re-register built-in functions
            registerBuiltInFunctions(context); 
            
            // Check if we have a valid AST
            if (this.ast == null) {
                errors.add(new Error("No AST to evaluate. Parse code first.", 0, 0));
                
                return new EvaluationResult(false, null, errors);
            }
            
            Object result = this.ast.evaluate(context);
            
            return new EvaluationResult(true, result, new ArrayList<>());
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
}
