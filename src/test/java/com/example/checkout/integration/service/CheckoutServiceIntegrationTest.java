package com.example.checkout.integration.service;

import com.example.checkout.exception.UnknownProductException;
import com.example.checkout.model.Receipt;
import com.example.checkout.repository.ItemRepository;
import com.example.checkout.repository.PricingRuleRepository;
import com.example.checkout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckoutServiceIntegrationTest {
    private CheckoutService service;
    private ItemRepository itemRepo;
    private PricingRuleRepository ruleRepo;

    @BeforeEach
    void setup() {
        itemRepo = new ItemRepository();
        ruleRepo = new PricingRuleRepository(itemRepo);
        service = new CheckoutService(itemRepo, ruleRepo);
    }

    @Test
    void calculatesTotalWithBulkDiscount() {
        UUID session = service.startSession();
        service.scan(session, "A");
        service.scan(session, "A");
        service.scan(session, "A");

        Receipt receipt = service.checkout(session);
        assertEquals(new BigDecimal("90"), receipt.getTotal());
    }

    @Test
    void calculatesTotalWithBundleDiscount() {
        UUID session = service.startSession();
        service.scan(session, "A");
        service.scan(session, "C");

        Receipt receipt = service.checkout(session);
        assertEquals(new BigDecimal("65"), receipt.getTotal());
    }

    @Test
    void calculatesTotalWithoutDiscount() {
        UUID session = service.startSession();
        service.scan(session, "B");

        Receipt receipt = service.checkout(session);
        assertEquals(new BigDecimal("10"), receipt.getTotal());
    }

    @Test
    void calculatesTotalWithMultipleDiscounts() {
        UUID session = service.startSession();
        service.scan(session, "A");
        service.scan(session, "A");
        service.scan(session, "A");
        service.scan(session, "C");
        service.scan(session, "C");
        service.scan(session, "C");
        service.scan(session, "C");
        service.scan(session, "A");

        Receipt receipt = service.checkout(session);
        BigDecimal expectedTotal = new BigDecimal("190");
        assertEquals(expectedTotal, receipt.getTotal());
    }

    @Test
    void calculatesTotalEmptyCart() {
        UUID session = service.startSession();

        Receipt receipt = service.checkout(session);
        assertEquals(BigDecimal.ZERO, receipt.getTotal());
    }

    @Test
    void calculatesTotalMultipleItemsNoPromotions() {
        UUID session = service.startSession();
        service.scan(session, "B");
        service.scan(session, "D");

        Receipt receipt = service.checkout(session);
        assertEquals(new BigDecimal("35"), receipt.getTotal());
    }

    @Test
    void calculatesTotalWithMultipleSameBundles() {
        UUID session = service.startSession();
        service.scan(session, "A");
        service.scan(session, "C");
        service.scan(session, "A");
        service.scan(session, "C");

        Receipt receipt = service.checkout(session);

        assertEquals(new BigDecimal("130"), receipt.getTotal());
    }

    @Test
    void calculatesTotalWithBulkAndBundleTogether() {
        UUID session = service.startSession();
        service.scan(session, "A");
        service.scan(session, "A");
        service.scan(session, "A");
        service.scan(session, "C");

        Receipt receipt = service.checkout(session);

        assertEquals(new BigDecimal("115"), receipt.getTotal());
    }

    @Test
    void checkoutDoesNotMutateSessionState() {
        UUID session = service.startSession();
        service.scan(session, "B");
        service.scan(session, "B");

        Receipt receipt1 = service.checkout(session);
        Receipt receipt2 = service.checkout(session);

        assertEquals(receipt1.getTotal(), receipt2.getTotal());
    }

    @Test
    void multipleSessionsAreIndependent() {
        UUID session1 = service.startSession();
        UUID session2 = service.startSession();

        service.scan(session1, "A");
        service.scan(session2, "B");

        Receipt receipt1 = service.checkout(session1);
        Receipt receipt2 = service.checkout(session2);

        assertEquals(new BigDecimal("40"), receipt1.getTotal());
        assertEquals(new BigDecimal("10"), receipt2.getTotal());
    }

    @Test
    void throwsExceptionWhenItemNotFound() {
        UUID session = service.startSession();

        try {
            service.scan(session, "Z");
        } catch (UnknownProductException e) {
            assertEquals("Unknown product: Z", e.getMessage());
        }
    }

    @Test
    void receiptContainsCorrectLineItems() {
        UUID session = service.startSession();
        service.scan(session, "A");
        service.scan(session, "B");

        Receipt receipt = service.checkout(session);

        assertEquals(2, receipt.getItems().size());

        Receipt.ReceiptLine lineA = receipt.getItems().stream()
                .filter(line -> line.getName().equals("A"))
                .findFirst().orElseThrow();
        assertEquals(1, lineA.getQuantity());
        assertEquals(new BigDecimal("40"), lineA.getUnitPrice());
        assertEquals(new BigDecimal("40"), lineA.getLineTotal());

        Receipt.ReceiptLine lineB = receipt.getItems().stream()
                .filter(line -> line.getName().equals("B"))
                .findFirst().orElseThrow();
        assertEquals(1, lineB.getQuantity());
        assertEquals(new BigDecimal("10"), lineB.getUnitPrice());
        assertEquals(new BigDecimal("10"), lineB.getLineTotal());
    }

    @Test
    void receiptContainsDiscountsWhenApplicable() {
        UUID session = service.startSession();
        service.scan(session, "A");
        service.scan(session, "C");

        Receipt receipt = service.checkout(session);

        assertEquals(new BigDecimal("70"), receipt.getSubtotal());
        assertEquals(new BigDecimal("65"), receipt.getTotal());

        assertEquals(1, receipt.getDiscounts().size());
        Receipt.DiscountLine discount = receipt.getDiscounts().get(0);
        assertEquals(new BigDecimal("-5"), discount.getAmount());
    }
}
