package interpreter.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import interpreter.runtime.ResourceQuota;

/**
 * Command-line interface for the Interpreter.
 * Executes scripts from files and handles errors appropriately.
 */
public class InterpreterCLI {

    private static final int SUCCESS_EXIT_CODE = 0;
    private static final int ERROR_EXIT_CODE = 1;

    public static void main(String[] args) {
        // Validate arguments
        if (args.length != 1) {
            System.err.println("Usage: java interpreter.main.InterpreterCLI <script_path>");
            System.exit(ERROR_EXIT_CODE);
        }

        String scriptPath = args[0];
        String sourceCode;
        
        try {
            // Read the script file
            Path path = Paths.get(scriptPath);
            sourceCode = Files.readString(path);
        } catch (IOException e) {
            System.err.println("Error reading script file: " + e.getMessage());
            System.exit(ERROR_EXIT_CODE);
            return; // This line is never reached but prevents compiler warnings
        }

        // Create interpreter instance
        Interpreter interpreter = new Interpreter();
        interpreter.setResourceQuota(new ResourceQuota(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        // Parse the script
        Interpreter.ParseResult parseResult = interpreter.parse(sourceCode);
        
        if (!parseResult.isSuccess()) {
            // Output parse errors to stderr
            for (Interpreter.Error error : parseResult.getErrors()) {
                System.err.println(error.toString());
            }
            System.exit(ERROR_EXIT_CODE);
        }
        
        // Execute the script
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        
        if (!evalResult.isSuccess()) {
            // Output runtime errors to stderr
            for (Interpreter.Error error : evalResult.getErrors()) {
                System.err.println(error.toString());
            }
            System.exit(ERROR_EXIT_CODE);
        }
        
        // If we get here, execution was successful
        System.exit(SUCCESS_EXIT_CODE);
    }
} 