package interpreter;

import interpreter.main.Interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleRepl {
    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("InterpreterJ REPL - Type 'exit' to quit");
        
        String line;
        try {
            while (true) {
                System.out.print("> ");
                line = reader.readLine();
                
                if (line == null || line.equals("exit")) {
                    break;
                }
                
                // Parse and evaluate the input
                Interpreter.ParseResult parseResult = interpreter.parse(line);
                
                if (!parseResult.isSuccess()) {
                    System.out.println("Parse error: " + Interpreter.formatErrors(parseResult.getErrors()));
                    continue;
                }
                
                Interpreter.EvaluationResult evalResult = interpreter.evaluate();
                
                if (!evalResult.isSuccess()) {
                    System.out.println("Evaluation error: " + Interpreter.formatErrors(evalResult.getErrors()));
                    continue;
                }
                
                Object result = evalResult.getResult();
                if (result != null) {
                    System.out.println("=> " + result.toString());
                } else {
                    System.out.println("=> null");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
    }
} 