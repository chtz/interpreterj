package interpreter.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import interpreter.main.ModelContextProtocolServer.EvaluationRequest;
import interpreter.main.ModelContextProtocolServer.EvaluationResponse;
import interpreter.runtime.ResourceQuota;

/**
 * HTTP server implementation of the Model Context Protocol for InterpreterJ.
 * Provides an HTTP API for script evaluation.
 */
public class ModelContextProtocolHttpServer {
    private final int port;
    private final HttpServer server;
    private final ModelContextProtocolServer protocolServer;
    
    /**
     * Creates a new HTTP server for the Model Context Protocol.
     * 
     * @param port The port to listen on.
     * @throws IOException If the server cannot be created.
     */
    public ModelContextProtocolHttpServer(int port) throws IOException {
        this.port = port;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.protocolServer = new ModelContextProtocolServer();
        
        // Register the evaluation endpoint
        server.createContext("/evaluate", new EvaluationHandler());
    }
    
    /**
     * Starts the HTTP server.
     */
    public void start() {
        server.start();
        System.out.println("Model Context Protocol HTTP server started on port " + port);
    }
    
    /**
     * Stops the HTTP server.
     */
    public void stop() {
        server.stop(0);
        System.out.println("Model Context Protocol HTTP server stopped");
    }
    
    /**
     * HTTP handler for script evaluation.
     */
    private class EvaluationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!exchange.getRequestMethod().equals("POST")) {
                    sendResponse(exchange, 405, "Method Not Allowed");
                    return;
                }
                
                // Read the request body
                String requestBody = readInputStream(exchange.getRequestBody());
                
                // Parse the request parameters
                Map<String, String> params = parseFormData(requestBody);
                
                // Extract the script and input
                String script = params.get("script");
                String input = params.get("input");
                
                if (script == null || script.isEmpty()) {
                    sendResponse(exchange, 400, "Missing required parameter: script");
                    return;
                }
                
                // Create the evaluation request
                EvaluationRequest request = new EvaluationRequest(script, input != null ? input : "");
                
                // Set custom resource quota if requested
                if (params.containsKey("maxEvaluationSteps")) {
                    try {
                        int maxEvaluationSteps = Integer.parseInt(params.get("maxEvaluationSteps"));
                        // Create a new ResourceQuota with the custom evaluation steps
                        // Using default values from ResourceQuota documentation
                        ResourceQuota quota = new ResourceQuota(
                            500,     // Default max evaluation depth
                            10000,   // Default max loop iterations
                            1000,    // Default max variable count
                            maxEvaluationSteps
                        );
                        request.setResourceQuota(quota);
                    } catch (NumberFormatException e) {
                        // Ignore invalid value, use default
                    }
                }
                
                // Evaluate the script
                EvaluationResponse response = protocolServer.evaluate(request);
                
                // Prepare the response
                StringBuilder responseBody = new StringBuilder();
                responseBody.append("{ ");
                responseBody.append("\"success\": ").append(response.isSuccess()).append(", ");
                
                if (response.isSuccess()) {
                    responseBody.append("\"output\": \"")
                               .append(escapeJson(response.getOutput())).append("\", ");
                    
                    Object result = response.getResult();
                    if (result != null) {
                        responseBody.append("\"result\": \"")
                                   .append(escapeJson(result.toString())).append("\"");
                    } else {
                        responseBody.append("\"result\": null");
                    }
                } else {
                    responseBody.append("\"output\": \"")
                               .append(escapeJson(response.getOutput())).append("\", ");
                    
                    responseBody.append("\"errors\": [");
                    if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                        boolean first = true;
                        for (Interpreter.Error error : response.getErrors()) {
                            if (!first) {
                                responseBody.append(", ");
                            }
                            first = false;
                            
                            responseBody.append("{ ")
                                       .append("\"message\": \"").append(escapeJson(error.getMessage())).append("\", ")
                                       .append("\"line\": ").append(error.getLine()).append(", ")
                                       .append("\"column\": ").append(error.getColumn())
                                       .append(" }");
                        }
                    }
                    responseBody.append("]");
                }
                
                responseBody.append(" }");
                
                // Send the response
                sendResponse(exchange, 200, responseBody.toString(), "application/json");
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
            }
        }
        
        /**
         * Escapes special characters in a string for JSON.
         * 
         * @param s The string to escape.
         * @return The escaped string.
         */
        private String escapeJson(String s) {
            if (s == null) {
                return "";
            }
            
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }
        
        /**
         * Parses form data from the request body.
         * 
         * @param formData The form data to parse.
         * @return A map of parameter names to values.
         */
        private Map<String, String> parseFormData(String formData) {
            Map<String, String> result = new HashMap<>();
            
            if (formData == null || formData.isEmpty()) {
                return result;
            }
            
            // Check if it's JSON format
            if (formData.trim().startsWith("{")) {
                // Very simple JSON parsing - just for demonstration
                // In a real application, use a proper JSON parser
                String trimmed = formData.trim();
                trimmed = trimmed.substring(1, trimmed.length() - 1); // Remove { }
                
                for (String pair : trimmed.split(",")) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().replace("\"", "");
                        String value = keyValue[1].trim();
                        
                        // Remove quotes if present
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        
                        result.put(key, value);
                    }
                }
                
                return result;
            }
            
            // Form URL encoded format
            for (String pair : formData.split("&")) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    result.put(keyValue[0], keyValue[1]);
                }
            }
            
            return result;
        }
        
        /**
         * Reads the contents of an input stream into a string.
         * 
         * @param is The input stream to read from.
         * @return The contents of the input stream as a string.
         * @throws IOException If an I/O error occurs.
         */
        private String readInputStream(InputStream is) throws IOException {
            try (java.util.Scanner scanner = new java.util.Scanner(is, StandardCharsets.UTF_8.name())) {
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "";
            }
        }
        
        /**
         * Sends an HTTP response.
         * 
         * @param exchange The HTTP exchange to respond to.
         * @param statusCode The HTTP status code.
         * @param response The response body.
         * @throws IOException If an I/O error occurs.
         */
        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            sendResponse(exchange, statusCode, response, "text/plain");
        }
        
        /**
         * Sends an HTTP response with the specified content type.
         * 
         * @param exchange The HTTP exchange to respond to.
         * @param statusCode The HTTP status code.
         * @param response The response body.
         * @param contentType The content type of the response.
         * @throws IOException If an I/O error occurs.
         */
        private void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
    
    /**
     * Main entry point for the HTTP server.
     * 
     * @param args Command line arguments. The first argument is the port number (default: 8080).
     */
    public static void main(String[] args) {
        try {
            int port = 8080;
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number: " + args[0]);
                    System.exit(1);
                }
            }
            
            ModelContextProtocolHttpServer server = new ModelContextProtocolHttpServer(port);
            server.start();
            
            // Shutdown hook to stop the server when the application is terminated
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop();
            }));
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 