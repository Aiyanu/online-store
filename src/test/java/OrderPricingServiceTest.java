import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

public class OrderPricingServiceTest {

    private final OrderPricingService service = new OrderPricingService();

    private static final double DELTA = 0.001;

    private OrderResult priceOf(DeliveryZone zone, Order... items) {
        return service.price(List.of(items), zone);
    }

    // ---------- Invalid input ----------

    @Test
    void validValuesAreAccepted() {
        assertTrue(service.isValidName("Pen"));
        assertTrue(service.isValidPrice(2_500.50));
        assertTrue(service.isValidQuantity(1));
    }

    @Test
    void blankNameIsRejected() {
        assertFalse(service.isValidName(""));
        assertFalse(service.isValidName("   "));
    }

    @Test
    void zeroPriceIsRejected() {
        assertFalse(service.isValidPrice(0));
    }

    @Test
    void negativePriceIsRejected() {
        assertFalse(service.isValidPrice(-500));
    }

    @Test
    void notANumberPriceIsRejected() {
        assertFalse(service.isValidPrice(Double.NaN));
    }

    @Test
    void zeroQuantityIsRejected() {
        assertFalse(service.isValidQuantity(0));
    }

    @Test
    void negativeQuantityIsRejected() {
        assertFalse(service.isValidQuantity(-3));
    }

    // ---------- Normal cases ----------
    @Test
    void smallLocalOrderHasNoDiscount() {
        OrderResult result = priceOf(DeliveryZone.LOCAL, new Order("Pen", 10_000, 2));
        assertEquals(20_000, result.subtotal(), DELTA);
        assertEquals(0, result.discount(), DELTA);
        assertEquals(15_000, result.deliveryFee(), DELTA);
        assertEquals(1_000, result.tax(), DELTA);
        assertEquals(36_000, result.total(), DELTA);
    }

    @Test
    void internationalDeliveryCostsMore() {
        OrderResult result = priceOf(DeliveryZone.INTERNATIONAL, new Order("Pen", 10_000, 2));
        assertEquals(50_000, result.deliveryFee(), DELTA);
        assertEquals(71_000, result.total(), DELTA);
    }

    @Test
    void tier2DiscountIsFifteenPercent() {
        OrderResult result = priceOf(DeliveryZone.LOCAL, new Order("Phone", 100_000, 2));
        assertEquals(30_000, result.discount(), DELTA);
        assertEquals(15_000, result.deliveryFee(), DELTA);
        assertEquals(8_500, result.tax(), DELTA);
        assertEquals(193_500, result.total(), DELTA);
    }

    @Test
    void bulkDiscountAppliesAtQuantityTen() {
        OrderResult result = priceOf(DeliveryZone.LOCAL, new Order("Pen", 5_000, 10));
        assertEquals(2_500, result.discount(), DELTA);
        assertEquals(64_875, result.total(), DELTA);
    }

    @Test
    void tierAndBulkDiscountsAreAdded() {
        OrderResult result = priceOf(DeliveryZone.LOCAL, new Order("Bag", 15_000, 10));
        assertEquals(22_500, result.discount(), DELTA);
        assertEquals(148_875, result.total(), DELTA);
    }

    @Test
    void multipleItemsAreAddedUp() {
        OrderResult result = priceOf(DeliveryZone.LOCAL,
                new Order("Pen", 10_000, 2), new Order("Book", 5_500, 4));
        assertEquals(42_000, result.subtotal(), DELTA);
    }

    // ---------- Boundary cases ----------

    @Test
    void belowTier1GetsNoDiscount() {
        OrderResult result = priceOf(DeliveryZone.LOCAL, new Order("Shirt", 33_333, 3));
        assertEquals(0, result.discount(), DELTA);
        assertEquals(4_999.95, result.tax(), DELTA);
        assertEquals(119_998.95, result.total(), DELTA);
    }

    @Test
    void exactlyTier1GetsTenPercent() {
        // 50,000 x 2 = 100,000 | 10% = 10,000 | 90,000 | delivery 15,000 | tax 4,500 | 109,500
        OrderResult result = priceOf(DeliveryZone.LOCAL, new Order("Shoe", 50_000, 2));
        assertEquals(10_000, result.discount(), DELTA);
        assertEquals(109_500, result.total(), DELTA);
    }
}