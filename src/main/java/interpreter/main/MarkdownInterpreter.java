package interpreter.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import interpreter.main.Interpreter.EvaluationResult;

/**
 * <pre>
 * mvn install 2>&1 > /dev/null && cat README.md | java -cp target/interpreterj-1.0.0.jar interpreter.main.MarkdownInterpreter | tee README2.md
 * </pre>
 */
public class MarkdownInterpreter {
	private DataInputStream in = new DataInputStream(System.in);
	private DataOutputStream out = new DataOutputStream(System.out);

	@SuppressWarnings("unchecked")
	private void run() {
		for (String s = gets(); s != null; s = gets()) {
			puts(s);
			
			if (s.startsWith("```script")) {
				StringBuilder script = new StringBuilder(); 
				while (!(s = gets()).startsWith("```")) {
					puts(s);
					if (script.length() > 0) {
						script.append("\n");
					}
					script.append(s);
				}
				puts("```");
				
				StringBuilder scriptOut = new StringBuilder();
				Interpreter i = new Interpreter(ec -> {
//					ec.registerFunction("gets", args -> {
//						return null;
//			        });
			    	
			    	ec.registerFunction("puts", args -> {
			    		if (scriptOut.length() > 0) {
			    			scriptOut.append("\n");
			    		}
			    		scriptOut.append(args.get(0));
			    		return null;
			        });
				});
				i.parse(script.toString());
				EvaluationResult r = i.evaluate();
				
				if (scriptOut.length() > 0) {
					puts("\n<sup><sub>Script Output (generated)</sub></sup>\n```output\n" + scriptOut.toString() + "\n```");
				}
				
				if (r.getResult() != null) {
					puts("\n<sup><sub>Script Result (generated)</sub></sup>\n```result\n" + r.getResult().toString() + "\n```");
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private String gets() {
		try {
			return in.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void puts(String s) {
		try {
			out.writeBytes(s);
			out.writeBytes("\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		new MarkdownInterpreter().run();
	}
}
