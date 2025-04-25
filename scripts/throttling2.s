#!/usr/bin/env -S java -jar target/interpreterj-1.0.0.jar

def foo(x) {
  puts("Hi " + x);
  //x = x - 1;
  if (x > 0) {
    foo(x);
  }
}

foo(10);
