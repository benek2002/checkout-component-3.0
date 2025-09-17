package com.example.checkout.unit.service;

import com.example.checkout.model.Item;
import com.example.checkout.model.Receipt;
import com.example.checkout.model.rules.PricingRule;
import com.example.checkout.repository.ItemRepository;
import com.example.checkout.repository.PricingRuleRepository;
import com.example.checkout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CheckoutServiceUnitTest {
    private ItemRepository itemRepo;
    private PricingRuleRepository ruleRepo;
    private CheckoutService service;

    private Item itemA;

    @BeforeEach
    void setup() {
        itemRepo = Mockito.mock(ItemRepository.class);
        ruleRepo = Mockito.mock(PricingRuleRepository.class);
        service = new CheckoutService(itemRepo, ruleRepo);

        itemA = new Item("A", new BigDecimal("40"));
    }

    @Test
    void startSessionCreatesUniqueSession() {
        UUID session1 = service.startSession();
        UUID session2 = service.startSession();

        assertNotEquals(session1, session2);
    }

    @Test
    void scanDelegatesToRepository() {
        UUID session = service.startSession();

        when(itemRepo.findByName("A")).thenReturn(itemA);
        service.scan(session, "A");

        verify(itemRepo, times(1)).findByName("A");
    }

    @Test
    void checkoutAppliesRules() {
        UUID session = service.startSession();

        when(itemRepo.findByName("A")).thenReturn(itemA);
        service.scan(session, "A");

        PricingRule unitRule = mock(PricingRule.class);
        when(unitRule.apply(anyMap())).thenReturn(new BigDecimal("40"));

        PricingRule discountRule = mock(PricingRule.class);
        when(discountRule.apply(anyMap())).thenReturn(new BigDecimal("-10"));

        when(ruleRepo.getAllRules()).thenReturn(List.of(unitRule, discountRule));

        Receipt receipt = service.checkout(session);

        assertEquals(new BigDecimal("30"), receipt.getTotal());
        assertEquals(new BigDecimal("40"), receipt.getSubtotal());
        assertEquals(1, receipt.getDiscounts().size());
        assertEquals(new BigDecimal("-10"), receipt.getDiscounts().get(0).getAmount());
    }

    @Test
    void checkoutCreatesReceiptLines() {
        UUID session = service.startSession();

        when(itemRepo.findByName("A")).thenReturn(itemA);
        service.scan(session, "A");
        service.scan(session, "A");

        PricingRule unitRule = mock(PricingRule.class);
        when(unitRule.apply(anyMap())).thenReturn(new BigDecimal("80"));

        when(ruleRepo.getAllRules()).thenReturn(List.of(unitRule));

        Receipt receipt = service.checkout(session);

        assertEquals(1, receipt.getItems().size());
        Receipt.ReceiptLine line = receipt.getItems().get(0);
        assertEquals("A", line.getName());
        assertEquals(2, line.getQuantity());
        assertEquals(new BigDecimal("40"), line.getUnitPrice());
        assertEquals(new BigDecimal("80"), line.getLineTotal());
    }

    @Test
    void checkoutWithMultipleDiscounts() {
        UUID session = service.startSession();

        when(itemRepo.findByName("A")).thenReturn(itemA);
        service.scan(session, "A");
        service.scan(session, "A");
        service.scan(session, "A"); // razem 3 szt.

        PricingRule unitRule = mock(PricingRule.class);
        when(unitRule.apply(anyMap())).thenReturn(new BigDecimal("120"));

        PricingRule discount1 = mock(PricingRule.class);
        when(discount1.apply(anyMap())).thenReturn(new BigDecimal("-15"));

        PricingRule discount2 = mock(PricingRule.class);
        when(discount2.apply(anyMap())).thenReturn(new BigDecimal("-5"));

        when(ruleRepo.getAllRules()).thenReturn(List.of(unitRule, discount1, discount2));

        Receipt receipt = service.checkout(session);

        assertEquals(new BigDecimal("100"), receipt.getTotal());
        assertEquals(new BigDecimal("120"), receipt.getSubtotal());
        assertEquals(2, receipt.getDiscounts().size());
        assertTrue(receipt.getDiscounts().stream()
                .anyMatch(d -> d.getAmount().equals(new BigDecimal("-15"))));
        assertTrue(receipt.getDiscounts().stream()
                .anyMatch(d -> d.getAmount().equals(new BigDecimal("-5"))));
    }

}
