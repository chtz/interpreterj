#!/usr/bin/env -S java -jar target/interpreterj-1.0.0.jar

// Variable declaration and assignment
let x = 10;
x = x + 5;  // Reassignment
puts(x);  // Output: 15.0

// Block scoping demonstration
let a = 5;
{
  let a = 10;  // Different variable that shadows outer 'a'
  let b = 15;  // Only accessible within this block
  puts(a);     // Output: 10.0
}
puts(a);       // Output: 5.0 (outer 'a' is unchanged)
// puts(b);    // Error: Undefined variable 'b'
