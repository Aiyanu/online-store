# Online Store Order Pricing Calculator

Console-based Java app that calculates an order's subtotal, discount, delivery fee, tax and final amount.

## Setup
- JDK 21+, Maven 3.9+
- `mvn test` runs the JUnit 5 tests
- Run the app:
  ```
  mvn compile
  java -cp target/classes store.ConsoleApp
  ```

## Business logic


| Rule | Detail                                                  |
|---|---------------------------------------------------------|
| Discount tier 1 | Subtotal >= 100,000.00 -> 10%                           |
| Discount tier 2 | Subtotal >= 250,000.00 -> 15% (replaces tier 1)         |
| Bulk discount | Any line with quantity >= 10 -> extra 5% (applied once) |
| Delivery fee | Otherwise Local 15,000.00, International 50,000.00      |
| Tax | 5% of the discounted subtotal (delivery is not taxed)   |