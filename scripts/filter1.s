#!/usr/bin/env -S java -jar target/interpreterj-1.0.0.jar

// Sample: echo "1\nx\n2" | ./scripts/filter1.s

puts("# Input");
let s = gets();
let sum = 0;
while (s != null) {

  let i = int(s);
  if (i != null) {
    puts("{" + s + "}");
    sum = sum + i;
  }
  else {
    puts(">" + s + "<");
  }

  s = gets();
}

puts("# Sum: " + sum);
