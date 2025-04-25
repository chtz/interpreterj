#!/usr/bin/env -S java -jar target/interpreterj-1.0.0.jar

// Array creation and access
let numbers = [10, 20, 30, 40, 50];
puts(numbers[0]);  // Output: 10.0
puts(numbers[4]);  // Output: 50.0

// Mixed type arrays
let mixed = [1, "two", true, null];
puts(mixed[1]);  // Output: two

// Array length
puts(len(numbers));  // Output: 5.0

// Modifying array elements
numbers[2] = 99;
puts(numbers[2]);  // Output: 99.0

// Array operations
push(numbers, 60);            // Add element to end
puts(numbers[5]);             // Output: 60.0
puts(len(numbers));           // Output: 6.0

let poppedValue = pop(numbers);  // Remove last element
puts(poppedValue);           // Output: 60.0
puts(len(numbers));          // Output: 5.0

delete(numbers, 0);          // Remove element at index 0
puts(numbers[0]);            // Output: 20.0
puts(len(numbers));          // Output: 4.0

// Array concatenation
let moreNumbers = [60, 70];
let combined = numbers + moreNumbers;
puts(len(combined));         // Output: 6.0

// Array iteration
let sum = 0;
let i = 0;
while (i < len(numbers)) {
  sum = sum + numbers[i];
  i = i + 1;
}
puts("Sum: " + sum);         // Output value depends on previous operations
