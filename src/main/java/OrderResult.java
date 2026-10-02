/** The final numbers for an order, all rounded to 2 decimal places. */
public record OrderResult(double subtotal,
                          double discount,
                          double deliveryFee,
                          double tax,
                          double total) {
}
