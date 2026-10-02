import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Order> cart = new ArrayList<>();

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

        if (cart.isEmpty()) {
            System.out.println("Your cart is empty, so there is nothing to price.");
            return;
        }
    }

    private static String readName() {
        System.out.print("Item name: ");
        return sc.nextLine();
    }

    private static double readPrice() {
        System.out.print("Unit price: ");
        try {
            return Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private static int readQuantity() {
        System.out.print("Quantity: ");
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
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
}
