#!/bin/bash
# pre-req: macos
# pre-req: go installed and go command in path
cat interpreter.ss|(sed '$d' && echo '//<GO2>')|./program_input_feeder.rb interpreter.ss|./interpreter_mac_arm64|bash
mv app interpreter_mac_arm64
rm app.go
