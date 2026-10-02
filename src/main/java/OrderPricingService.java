import java.util.List;
public class OrderPricingService {

    static final double TIER_1_MIN = 100_000.00;
    static final double TIER_2_MIN = 200_000.00;
    static final double TIER_1_PERCENT = 10;
    static final double TIER_2_PERCENT = 15;
    static final int BULK_QUANTITY = 10;
    static final double BULK_PERCENT = 5;

    static final double LOCAL_FEE = 15_000.00;
    static final double INTERNATIONAL_FEE = 50_000.00;

    static final double TAX_PERCENT = 5;

    public String checkItem(String name, double price, int quantity) {
        if (name == null || name.isBlank()) {
            return "Name must not be blank.";
        }
        if (Double.isNaN(price) || price <= 0) {
            return "Price must be a number greater than 0.";
        }
        if (quantity < 1) {
            return "Quantity must be a whole number of at least 1.";
        }
        return null;
    }

    public OrderResult price(List<Order> items, DeliveryZone zone) {
        double subtotal = calculateSubtotal(items);
        double discount = calculateDiscount(items, subtotal);
        double discounted = round2(subtotal - discount);
        double delivery = calculateDeliveryFee(discounted, zone);
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
                break;
            }
        }
        return calculateAmountFromPercent(subtotal, percent);
    }

    public double calculateDeliveryFee(double discountedSubtotal, DeliveryZone zone) {
        if (zone == DeliveryZone.LOCAL) {
            return LOCAL_FEE;
        }
        return INTERNATIONAL_FEE;
    }

    public double calculateTax(double discountedSubtotal) {
        return calculateAmountFromPercent(discountedSubtotal, TAX_PERCENT);
    }

    private static double calculateAmountFromPercent(double amount, double percent) {
        return Math.round(amount * percent) / 100.0;
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
