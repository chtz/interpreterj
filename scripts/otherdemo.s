#!/usr/bin/env -S java -jar target/interpreterj-1.0.0.jar

let x = [
  "hello",
  {
    1 : "hello",
    "welt" : 66
  },
  3
];

puts(x);
puts(x[0]);
puts(x[1]);
puts(x[1][1]);
puts(x[1]["welt"]);

let y = [x, x];
puts(len(y));
puts(len(y[0]));