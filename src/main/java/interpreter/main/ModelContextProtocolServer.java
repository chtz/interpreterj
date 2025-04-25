package interpreter.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import interpreter.runtime.CallableFunction;
import interpreter.runtime.EvaluationContext;
import interpreter.runtime.ResourceQuota;

/**
 * Model Context Protocol server for InterpreterJ.
 * Allows evaluation of scripts with custom input and output handling.
 */
public class ModelContextProtocolServer {
    
    /**
     * Request model for script evaluation.
     */
    public static class EvaluationRequest {
        private String script;
        private String input;
        private ResourceQuota resourceQuota;
        
        public EvaluationRequest() {
            // Default constructor
        }
        
        public EvaluationRequest(String script, String input) {
            this.script = script;
            this.input = input;
            this.resourceQuota = new ResourceQuota();
        }
        
        public EvaluationRequest(String script, String input, ResourceQuota resourceQuota) {
            this.script = script;
            this.input = input;
            this.resourceQuota = resourceQuota;
        }
        
        public String getScript() {
            return script;
        }
        
        public void setScript(String script) {
            this.script = script;
        }
        
        public String getInput() {
            return input;
        }
        
        public void setInput(String input) {
            this.input = input;
        }
        
        public ResourceQuota getResourceQuota() {
            return resourceQuota;
        }
        
        public void setResourceQuota(ResourceQuota resourceQuota) {
            this.resourceQuota = resourceQuota;
        }
    }
    
    /**
     * Response model for script evaluation.
     */
    public static class EvaluationResponse {
        private boolean success;
        private String output;
        private Object result;
        private List<Interpreter.Error> errors;
        
        public EvaluationResponse() {
            // Default constructor
            this.errors = new ArrayList<>();
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getOutput() {
            return output;
        }
        
        public void setOutput(String output) {
            this.output = output;
        }
        
        public Object getResult() {
            return result;
        }
        
        public void setResult(Object result) {
            this.result = result;
        }
        
        public List<Interpreter.Error> getErrors() {
            return errors;
        }
        
        public void setErrors(List<Interpreter.Error> errors) {
            this.errors = errors;
        }
    }
    
    /**
     * Evaluates an InterpreterJ script with the given input.
     * Each line of input will be returned by the gets() function in the script.
     * Each call to puts() in the script will append a line to the output.
     *
     * @param request The evaluation request containing the script and input.
     * @return An evaluation response with the output, result, and any errors.
     */
    public EvaluationResponse evaluate(EvaluationRequest request) {
        EvaluationResponse response = new EvaluationResponse();
        
        // Create a string reader for the input
        final BufferedReader inputReader = new BufferedReader(new StringReader(request.getInput() != null ? request.getInput() : ""));
        
        // Create a string writer for the output
        final StringWriter outputWriter = new StringWriter();
        
        // Create a custom IO library function initializer
        @SuppressWarnings("unchecked")
        Interpreter interpreter = new Interpreter(
            request.getResourceQuota() != null ? request.getResourceQuota() : new ResourceQuota(),
            new Interpreter.DefaultLibraryFunctionsInitializer(),
            createCustomIOLibraryInitializer(inputReader, outputWriter),
            new Interpreter.MapLibraryFunctionsInitializer(),
            new Interpreter.ArrayLibraryFunctionsInitializer(),
            new Interpreter.StringLibraryFunctionsInitializer(),
            new Interpreter.RegexLibraryFunctionsInitializer(),
            new Interpreter.TypeLibraryFunctionsInitializer()
        );
        
        // Parse the script
        Interpreter.ParseResult parseResult = interpreter.parse(request.getScript());
        
        if (!parseResult.isSuccess()) {
            response.setSuccess(false);
            response.setErrors(parseResult.getErrors());
            return response;
        }
        
        // Evaluate the script
        Interpreter.EvaluationResult evalResult = interpreter.evaluate();
        
        // Set the output
        response.setOutput(outputWriter.toString());
        
        // Set the result
        if (evalResult.isSuccess()) {
            response.setSuccess(true);
            response.setResult(evalResult.getResult());
        } else {
            response.setSuccess(false);
            response.setErrors(evalResult.getErrors());
        }
        
        return response;
    }
    
    /**
     * Creates a custom IO library function initializer that redirects gets() and puts()
     * to the provided input reader and output writer.
     *
     * @param inputReader The reader to use for gets().
     * @param outputWriter The writer to use for puts().
     * @return A consumer that initializes the custom IO library functions.
     */
    private Consumer<EvaluationContext> createCustomIOLibraryInitializer(
            final BufferedReader inputReader, 
            final StringWriter outputWriter) {
        
        return new Consumer<EvaluationContext>() {
            @Override
            public void accept(EvaluationContext ec) {
                ec.registerFunction("gets", args -> {
                    try {
                        return inputReader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                
                ec.registerFunction("puts", args -> {
                    String output = args.get(0).toString();
                    outputWriter.write(output);
                    outputWriter.write('\n');
                    return null;
                });
            }
        };
    }
} 