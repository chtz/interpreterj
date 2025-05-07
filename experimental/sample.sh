#!/bin/bash

echo "Example running on Java-based interpreter"
./java_interpreter.sh sample.ss

echo "Example running on Go-based interpreter (transpiled from IJ-based interpreter)"
./program_input_feeder.rb sample.ss|./interpreter_mac_arm64

echo "Example running on IJ-based interpreter running on Go-based interpreter (transpiled from IJ-based interpreter)"
./program_input_feeder.rb sample.ss|./hosted_interpreter.sh

echo Example running on IJ-based interpreter running on IJ-based interpreter running on Java-based interpreter
./program_input_feeder.rb sample.ss|./selfhosted_interpreter.sh

echo "Example transpiled to Go, compiled with go build, and executed"
(cat sample.ss | sed '$d' && echo "//<GO2>") | ./interpreter_mac_arm64 | bash
rm app.go
./app
rm app
