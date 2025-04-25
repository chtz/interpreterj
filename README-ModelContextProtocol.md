# InterpreterJ Model Context Protocol

This document describes the Model Context Protocol implementation for InterpreterJ, which allows for script evaluation with custom input handling.

## Overview

The Model Context Protocol server for InterpreterJ provides a way to evaluate InterpreterJ scripts with custom input and output handling. It supports:

- Script evaluation with input strings
- Capturing output from `puts()` function calls
- Custom resource quotas for script execution
- Error handling for both parsing and execution errors

## Components

The implementation consists of three main components:

1. `ModelContextProtocolServer` - Core functionality for evaluating scripts
2. `ModelContextProtocolHttpServer` - HTTP API for the protocol
3. `ModelContextProtocolClient` - Command-line client for direct usage

## Using the Model Context Protocol Server

### Direct Java API Usage

```java
// Create a server instance
ModelContextProtocolServer server = new ModelContextProtocolServer();

// Create an evaluation request
EvaluationRequest request = new EvaluationRequest(
    "puts(\"Hello, World!\"); let x = gets(); puts(\"You entered: \" + x);",
    "Some input text"
);

// Evaluate the script
EvaluationResponse response = server.evaluate(request);

// Check the result
if (response.isSuccess()) {
    System.out.println("Output: " + response.getOutput());
    System.out.println("Result: " + response.getResult());
} else {
    System.out.println("Error: " + response.getErrors());
}
```

### Using the HTTP Server

1. Start the HTTP server:

```bash
java -cp target/interpreterj-1.0.0.jar interpreter.main.ModelContextProtocolHttpServer 1234
```

2. Send a POST request to the `/evaluate` endpoint:

```bash
curl -X POST -d '{"script":"let x=gets(); puts(x); puts(x); 12+1;","input":"Some input text"}' http://localhost:1234/evaluate
```

3. The server will respond with a JSON object:

```json
{
  "success": true,
  "output": "Some input text\nSome input text\n",
  "result": "13.0"
}
```

### Using the Command-line Client

1. Start the client in interactive mode:

```bash
java -cp target/interpreterj-1.0.0.jar interpreter.main.ModelContextProtocolClient
```

2. Follow the prompts to enter your script and input.

3. Or run a script directly:

```bash
java -cp target/interpreterj-1.0.0.jar interpreter.main.ModelContextProtocolClient "script.js" "input.txt"
```

## API Reference

### ModelContextProtocolServer

#### EvaluationRequest

| Field         | Type          | Description                                  |
|---------------|---------------|----------------------------------------------|
| script        | String        | The InterpreterJ script to evaluate          |
| input         | String        | Input text for the script (for gets() calls) |
| resourceQuota | ResourceQuota | (Optional) Custom resource limits            |

#### EvaluationResponse

| Field         | Type                   | Description                                  |
|---------------|------------------------|----------------------------------------------|
| success       | boolean                | Whether the evaluation was successful        |
| output        | String                 | Output from puts() calls                     |
| result        | Object                 | The result of the script execution           |
| errors        | List<Interpreter.Error>| Errors that occurred during execution        |

### HTTP API

#### POST /evaluate

Request body:

```json
{
  "script": "puts(\"Hello, World!\");",
  "input": "Optional input",
  "maxEvaluationSteps": 10000
}
```

Response:

```json
{
  "success": true,
  "output": "Hello, World!\n",
  "result": null
}
```

Or in case of errors:

```json
{
  "success": false,
  "output": "",
  "errors": [
    {
      "message": "Undefined variable 'foo'",
      "line": 1,
      "column": 5
    }
  ]
}
```

## Examples

### Example 1: Basic Script

Script:
```javascript
puts("Enter your name:");
let name = gets();
puts("Hello, " + name + "!");
```

Input:
```
John
```

Output:
```
Enter your name:
Hello, John!
```

### Example 2: Input Processing

Script:
```javascript
puts("Enter numbers (one per line):");
let sum = 0;
let num = gets();

while (num != null) {
  let n = int(num);
  if (n != null) {
    sum = sum + n;
  }
  num = gets();
}

puts("Sum: " + sum);
sum;
```

Input:
```
10
20
30
```

Output:
```
Enter numbers (one per line):
Sum: 60
```

Result:
```
60.0
```

## Build and Run

Make sure you have Maven installed, then:

```bash
# Build the project
mvn package

# Run the HTTP server
java -cp target/interpreterj-1.0.0.jar interpreter.main.ModelContextProtocolHttpServer

# Run the command-line client
java -cp target/interpreterj-1.0.0.jar interpreter.main.ModelContextProtocolClient
``` 