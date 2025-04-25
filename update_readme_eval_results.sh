#!/bin/bash
mvn install 2>&1 > /dev/null && cat README.md | java -cp target/interpreterj-1.0.0.jar interpreter.main.MarkdownInterpreter | tee README2.md
mv README2.md README.md
