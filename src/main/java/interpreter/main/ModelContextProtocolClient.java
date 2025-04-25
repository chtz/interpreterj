package interpreter.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import interpreter.main.ModelContextProtocolServer.EvaluationRequest;
import interpreter.main.ModelContextProtocolServer.EvaluationResponse;
import interpreter.runtime.ResourceQuota;

/**
 * Command-line client for the Model Context Protocol.
 * Demonstrates direct usage of the ModelContextProtocolServer from Java code.
 */
public class ModelContextProtocolClient {
    
    private final ModelContextProtocolServer server;
    
    public ModelContextProtocolClient() {
        this.server = new ModelContextProtocolServer();
    }
    
    /**
     * Run the client in interactive mode.
     * Allows the user to enter scripts and input interactively.
     */
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("InterpreterJ Model Context Protocol Client");
        System.out.println("Type 'exit' to quit");
        System.out.println();
        
        while (true) {
            try {
                // Prompt for script
                System.out.println("Enter your script (end with a line containing only 'END'):");
                StringBuilder scriptBuilder = new StringBuilder();
                String line;
                while (!(line = reader.readLine()).equals("END")) {
                    scriptBuilder.append(line).append("\n");
                }
                
                String script = scriptBuilder.toString();
                if (script.trim().equals("exit")) {
                    break;
                }
                
                // Prompt for input
                System.out.println("Enter input for the script (end with a line containing only 'END'):");
                StringBuilder inputBuilder = new StringBuilder();
                while (!(line = reader.readLine()).equals("END")) {
                    inputBuilder.append(line).append("\n");
                }
                
                // Execute the script
                EvaluationResponse response = server.evaluate(new EvaluationRequest(script, inputBuilder.toString()));
                
                // Display the results
                System.out.println("\nExecution Result:");
                System.out.println("Success: " + response.isSuccess());
                
                if (response.getOutput() != null && !response.getOutput().isEmpty()) {
                    System.out.println("\nOutput:");
                    System.out.println(response.getOutput());
                }
                
                if (response.isSuccess()) {
                    System.out.println("\nResult:");
                    System.out.println(response.getResult());
                } else {
                    System.out.println("\nErrors:");
                    for (Interpreter.Error error : response.getErrors()) {
                        System.out.println(error.toString());
                    }
                }
                
                System.out.println("\n-----------------------------------\n");
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
            }
        }
        
        System.out.println("Goodbye!");
    }
    
    /**
     * Evaluate a script with the given input.
     * 
     * @param script The script to evaluate.
     * @param input The input for the script.
     * @return The evaluation response.
     */
    public EvaluationResponse evaluate(String script, String input) {
        return server.evaluate(new EvaluationRequest(script, input));
    }
    
    /**
     * Evaluate a script with the given input and resource quota.
     * 
     * @param script The script to evaluate.
     * @param input The input for the script.
     * @param resourceQuota The resource quota to use.
     * @return The evaluation response.
     */
    public EvaluationResponse evaluate(String script, String input, ResourceQuota resourceQuota) {
        return server.evaluate(new EvaluationRequest(script, input, resourceQuota));
    }
    
    /**
     * Main entry point for the client.
     * 
     * @param args Command line arguments. If provided, the first argument is the script file path.
     */
    public static void main(String[] args) {
        ModelContextProtocolClient client = new ModelContextProtocolClient();
        
        if (args.length > 0) {
            // Non-interactive mode: evaluate the script from the command line
            String script = args[0];
            String input = args.length > 1 ? args[1] : "";
            
            EvaluationResponse response = client.evaluate(script, input);
            
            // Display the results
            if (response.getOutput() != null && !response.getOutput().isEmpty()) {
                System.out.println(response.getOutput());
            }
            
            if (!response.isSuccess()) {
                for (Interpreter.Error error : response.getErrors()) {
                    System.err.println(error.toString());
                }
                System.exit(1);
            }
            
            if (response.getResult() != null) {
                System.out.println("Result: " + response.getResult());
            }
        } else {
            // Interactive mode
            client.run();
        }
    }
} 