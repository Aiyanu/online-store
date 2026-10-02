import java.util.List;

/**
 * All the business rules live here. No Scanner and no System.out in this class,
 * so it can be tested without typing anything into the console.
 *
 * Order of calculation: subtotal -> discount -> delivery -> tax -> total.
 */
public class OrderPricingService {

    // Discount rules
    static final double TIER_1_MIN = 100_000.00;
    static final double TIER_2_MIN = 200_000.00;
    static final double TIER_1_PERCENT = 10;
    static final double TIER_2_PERCENT = 15;
    static final int BULK_QUANTITY = 10;
    static final double BULK_PERCENT = 5;

    // Delivery rules
    static final double LOCAL_FEE = 15_000.00;
    static final double INTERNATIONAL_FEE = 50_000.00;

    // Tax rule (applied to the discounted subtotal, delivery is not taxed)
    static final double TAX_PERCENT = 5;

    // ---------- Input checks ----------

    public boolean isValidName(String name) {
        return name != null && !name.isBlank();
    }

    public boolean isValidPrice(double price) {
        return price > 0 && !Double.isInfinite(price); // NaN > 0 is false, so NaN is rejected too
    }

    public boolean isValidQuantity(int quantity) {
        return quantity >= 1;
    }

    // ---------- Pricing ----------

    /** Runs every step in order and returns all the amounts. */
    public OrderResult price(List<Order> items, DeliveryZone zone) {
        double subtotal = calculateSubtotal(items);
        double discount = calculateDiscount(items, subtotal);
        double discounted = round2(subtotal - discount);
        double delivery = calculateDeliveryFee(zone);
        double tax = calculateTax(discounted);
        double total = round2(discounted + delivery + tax);

        return new OrderResult(subtotal, discount, delivery, tax, total);
    }

    public double calculateSubtotal(List<Order> items) {
        double sum = 0;
        for (Order item : items) {
            sum += item.getPrice() * item.getQuantity();
        }
        return round2(sum);
    }

    /** Tier percent (based on subtotal) plus 5% extra if any item has 10 or more units. */
    public double calculateDiscount(List<Order> items, double subtotal) {
        double percent = 0;

        if (subtotal >= TIER_2_MIN) {
            percent = TIER_2_PERCENT;
        } else if (subtotal >= TIER_1_MIN) {
            percent = TIER_1_PERCENT;
        }

        for (Order item : items) {
            if (item.getQuantity() >= BULK_QUANTITY) {
                percent += BULK_PERCENT;
                break; // the bulk bonus is only added once
            }
        }
        return percentOf(subtotal, percent);
    }

    public double calculateDeliveryFee( DeliveryZone zone) {
        if (zone == DeliveryZone.LOCAL) {
            return LOCAL_FEE;
        }
        return INTERNATIONAL_FEE;
    }

    public double calculateTax(double discountedSubtotal) {
        return percentOf(discountedSubtotal, TAX_PERCENT);
    }

    /** amount * percent / 100, rounded to 2 decimal places. */
    private static double percentOf(double amount, double percent) {
        return Math.round(amount * percent) / 100.0;
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}