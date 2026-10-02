import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Order> cart = new ArrayList<>();
    private static final OrderPricingService pricing = new OrderPricingService();

    public static void main(String[] args) {
        System.out.println("#### Welcome to our online store ####");
        System.out.println("#### Please place an order ####");

        boolean adding = true;
        while (adding) {
            String name = readName();
            double price = readPrice();
            int quantity = readQuantity();

            cart.add(new Order(name, price, quantity));
            System.out.println("Added: " + name + " x" + quantity);

            System.out.print("Add another item? (y/n): ");
            adding = sc.nextLine().trim().equalsIgnoreCase("y");
        }

        DeliveryZone zone = readDeliveryZone();
        OrderResult result = pricing.price(cart, zone);
        printSummary(result);
    }

    private static String readName() {
        while (true) {
            System.out.print("Item name: ");
            String name = sc.nextLine().trim();

            if (!name.isEmpty()) {
                return name;
            }
            System.out.println("Name must not be blank.");
        }
    }

    private static double readPrice() {
        while (true) {
            System.out.print("Unit price: ");
            try {
                double price = Double.parseDouble(sc.nextLine().trim());

                if (price > 0 && !Double.isInfinite(price)) {
                    return price;
                }
                System.out.println("Price must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Price must be a number, for example 2500.50.");
            }
        }
    }

    private static int readQuantity() {
        while (true) {
            System.out.print("Quantity: ");
            try {
                int quantity = Integer.parseInt(sc.nextLine().trim());

                if (quantity >= 1) {
                    return quantity;
                }
                System.out.println("Quantity must be at least 1.");
            } catch (NumberFormatException e) {
                System.out.println("Quantity must be a whole number, for example 3.");
            }
        }
    }

    private static DeliveryZone readDeliveryZone() {
        System.out.printf("""
                Select Delivery Zone (0/1)
                0: Local (NGN %,.2f)
                1: International (NGN %,.2f)
                """, OrderPricingService.LOCAL_FEE,
                OrderPricingService.INTERNATIONAL_FEE);

        while (true) {
            System.out.print("Your choice: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) {
                return DeliveryZone.LOCAL;
            }
            if (choice.equals("1")) {
                return DeliveryZone.INTERNATIONAL;
            }
            System.out.println("Please type 0 or 1.");
        }
    }

    private static void printSummary(OrderResult result) {
        System.out.println();
        System.out.println("--- Order Summary ---");
        System.out.printf("Subtotal:  %,15.2f%n", result.subtotal());
        System.out.printf("Discount: -%,15.2f%n", result.discount());
        System.out.printf("Delivery:  %,15.2f%n", result.deliveryFee());
        System.out.printf("Tax:       %,15.2f%n", result.tax());
        System.out.printf("TOTAL:     %,15.2f%n", result.total());
    }
}