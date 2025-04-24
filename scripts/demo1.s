#!/usr/bin/env -S java -jar target/interpreterj-1.0.0.jar

puts('Hi');

def foo() {
  let i = 0;
  def bar() {
    i = i + 1;
  }
  bar();
  bar();
  return i;
}

puts ('Foo result=' + foo());

let i = 0;
{
  let i = 1;
  puts ('i=' + i);
}
puts ('i=' + i);
