# InterpreterJ Overview

**InterpreterJ** is an experimental scripting language. Its Java-based interpreter was created using a *vibe coding* approach, leveraging **Cursor** and the **Claude 3.7 Sonnet** model.  
(Code for the interpreter can be found in the parent folder.)

The `java_interpreter.sh` wrapper script allows you to run InterpreterJ scripts (stored in the file system) using the Java-based interpreter.

> ⚠️ Both InterpreterJ and its Java-based implementation are highly experimental, incomplete, and **not intended for production use** — or arguably, any use beyond experimentation.

---

## Experimental Self-Hosted Interpreter

The code in the **`experimental`** folder contains a highly experimental implementation of an InterpreterJ interpreter written *in* the InterpreterJ language itself.

The main interpreter file, `interpreter.ss`, is essentially a port of the Java-based InterpreterJ implementation.  
It was generated using a combination of:

- LLM APIs (GPT-4.1 and GPT-4.1-mini),
- custom tooling,
- and extensive manual tweaks.

---

## I/O Limitations

The experimental interpreter has **limited I/O capabilities**:
- `gets()` reads from **STDIN**
- `puts()` writes to **STDOUT**

As a result, both:
1. The script to be interpreted, and  
2. The input that the script consumes  

must be supplied via **STDIN**.

To handle this, use the `program_input_feeder.rb` utility. It:

- Reads a script from a file
- Sends the script content to **STDOUT**
- Then passes all **STDIN** to **STDOUT** as well

The interpreter:
- First reads the script via STDIN
- Waits for a special input marker
- Then parses the received script
- Finally, continues reading additional input for the script from STDIN via `gets()`

---

## Self-Hosting Demo

The `selfhosted_interpreter.sh` script demonstrates the self-hosting capability by:

1. Running the experimental interpreter  
2. Inside another instance of the experimental interpreter  
3. Which is itself running in the **Java-based interpreter**

To see the self-hosted interpreter in action, run the demo using:

```bash
./sample.sh
```

---

## Go Transpilation

The interpreter is capable of compiling IJ scripts to binaries by transpiling the scripts to Go and then using `go build` to create the binary. The binary version of the interpreter (which is even more experimental) is also present in the `examples` directory (see `interpreter_mac_arm64`). You can also use the build script `rebuild_interpreter.sh` to regenerate the interpreter binary. 

Note: Go build tools need to be installed, and the `go` command must be available in your `PATH`.
