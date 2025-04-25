package interpreter.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import interpreter.main.Interpreter.ArrayLibraryFunctionsInitializer;
import interpreter.main.Interpreter.DefaultLibraryFunctionsInitializer;
import interpreter.main.Interpreter.EvaluationResult;
import interpreter.main.Interpreter.MapLibraryFunctionsInitializer;
import interpreter.main.Interpreter.ParseResult;

/**
 * <pre>
 * mvn install 2>&1 > /dev/null && cat README.md | java -cp target/interpreterj-1.0.0.jar interpreter.main.MarkdownInterpreter | tee README2.md
 * </pre>
 */
public class MarkdownInterpreter {
	final BufferedReader in;
	final PrintWriter out;

	public MarkdownInterpreter(InputStream in, OutputStream out) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new PrintWriter(new OutputStreamWriter(out));
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		boolean firstEmptyLine = false;
		for (String s = gets(); s != null; s = gets()) {
			if (s.isEmpty()) {
				if (!firstEmptyLine) {
					firstEmptyLine = true;
					puts("");
				}
			}
			else {
				firstEmptyLine = false;
			
				if (s.startsWith("<sup><sub>Script Output (generated)</sub></sup>") 
						|| s.startsWith("<sup><sub>Script Result (generated)</sub></sup>"))
				{
					while (!(s = gets()).trim().equals("```")) {
						//consume
					}
				}
				else if (s.trim().startsWith("```script")) {
					puts(s.trim());
					final StringBuilder script = new StringBuilder(); 
					while (!(s = gets()).trim().startsWith("```")) {
						puts(s);
						if (script.length() > 0) {
							script.append("\n");
						}
						script.append(s.trim());
					}
					puts("```");
					
					final StringBuilder scriptOut = new StringBuilder();
					Interpreter i = new Interpreter(ec -> {
						new ArrayLibraryFunctionsInitializer().accept(ec); // FIXME better way to replace parts of std lib
						new MapLibraryFunctionsInitializer().accept(ec);
						new DefaultLibraryFunctionsInitializer().accept(ec);
				    	ec.registerFunction("puts", args -> {
				    		if (scriptOut.length() > 0) {
				    			scriptOut.append("\n");
				    		}
				    		scriptOut.append(args.get(0).toString());
				    		return null;
				        });
					});
					ParseResult pr = i.parse(script.toString());
					
					if (!pr.isSuccess()) {
						puts("\n<sup><sub>Script Output (generated)</sub></sup>\n```output\nParse error: " + Interpreter.formatErrors(pr.getErrors()) + "\n```");
					}
					else {
						EvaluationResult r = i.evaluate();
						
						if (!r.isSuccess()) {
							puts("\n<sup><sub>Script Output (generated)</sub></sup>\n```output\nEval error: " + Interpreter.formatErrors(r.getErrors()) + "\n```");	
						}
						else {
							if (scriptOut.length() > 0) {
								puts("\n<sup><sub>Script Output (generated)</sub></sup>\n```output\n" + scriptOut.toString() + "\n```");
							}
							
							if (r.getResult() != null) {
								puts("\n<sup><sub>Script Result (generated)</sub></sup>\n```result\n" + r.getResult().toString() + "\n```");
							}
						}
					}
				}
				else {
					puts(s);
				}
			}
		}
		out.flush();
	}
	
	private String gets() {
		try {
			return in.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void puts(String s) {
		out.write(s);
		out.write('\n');
	}
	
	public static void main(String[] args) {
		new MarkdownInterpreter(System.in, System.out).run();
	}
}
