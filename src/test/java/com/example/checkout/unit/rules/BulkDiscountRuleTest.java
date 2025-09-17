package com.example.checkout.unit.rules;

import com.example.checkout.model.Item;
import com.example.checkout.model.rules.BulkDiscountRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BulkDiscountRuleTest {

    @Test
    void appliesDiscountWhenQtyMeetsRequirement() {
        Item itemA = new Item("A", new BigDecimal("40"));
        BulkDiscountRule rule = new BulkDiscountRule(itemA, 3, new BigDecimal("30"));
        Map<Item, Integer> basket = Map.of(itemA, 3);

        BigDecimal discount = rule.apply(basket);

        assertEquals(new BigDecimal("-30"), discount);
    }

    @Test
    void noDiscountIfQtyBelowRequirement() {
        Item itemA = new Item("A", new BigDecimal("40"));
        BulkDiscountRule rule = new BulkDiscountRule(itemA, 3, new BigDecimal("30"));
        Map<Item, Integer> basket = Map.of(itemA, 2);

        BigDecimal discount = rule.apply(basket);

        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void appliesDiscountWhenQtyExceedsRequirement() {
        Item itemA = new Item("A", new BigDecimal("40"));
        BulkDiscountRule rule = new BulkDiscountRule(itemA, 3, new BigDecimal("30"));

        Map<Item, Integer> basket = Map.of(itemA, 5);
        BigDecimal discount = rule.apply(basket);

        assertEquals(new BigDecimal("-30"), discount);
    }

    @Test
    void appliesDiscountForMultipleItemsEachMeetingRequirement() {
        Item itemA = new Item("A", new BigDecimal("40"));
        BulkDiscountRule rule = new BulkDiscountRule(itemA, 3, new BigDecimal("30"));

        Map<Item, Integer> basket = Map.of(itemA, 6);
        BigDecimal discount = rule.apply(basket);

        assertEquals(new BigDecimal("-60"), discount);
    }

    @Test
    void appliesDiscountWithEmptyBasket() {
        Item itemA = new Item("A", new BigDecimal("40"));
        BulkDiscountRule rule = new BulkDiscountRule(itemA, 3, new BigDecimal("30"));

        Map<Item, Integer> basket = Map.of();
        BigDecimal discount = rule.apply(basket);

        assertEquals(BigDecimal.ZERO, discount);
    }
}
