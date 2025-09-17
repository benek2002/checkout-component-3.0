package com.example.checkout.unit.rules;

import com.example.checkout.model.Item;
import com.example.checkout.model.rules.BundleDiscountRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BundleDiscountRuleTest {

    @Test
    void appliesDiscountForPairs() {
        Item itemA = new Item("A", new BigDecimal("40"));
        Item itemC = new Item("C", new BigDecimal("30"));
        BundleDiscountRule rule = new BundleDiscountRule(itemA, itemC, new BigDecimal("5"));
        Map<Item, Integer> basket = Map.of(itemA, 2, itemC, 1);

        BigDecimal discount = rule.apply(basket);

        assertEquals(new BigDecimal("-5"), discount);
    }

    @Test
    void noDiscountIfNoPairs() {
        Item itemA = new Item("A", new BigDecimal("40"));
        Item itemC = new Item("C", new BigDecimal("30"));
        BundleDiscountRule rule = new BundleDiscountRule(itemA, itemC, new BigDecimal("5"));
        Map<Item, Integer> basket = Map.of(itemA, 1);

        BigDecimal discount = rule.apply(basket);

        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void appliesDiscountForMultiplePairs() {
        Item itemA = new Item("A", new BigDecimal("40"));
        Item itemC = new Item("C", new BigDecimal("30"));
        BundleDiscountRule rule = new BundleDiscountRule(itemA, itemC, new BigDecimal("5"));

        Map<Item, Integer> basket = Map.of(itemA, 3, itemC, 2);
        BigDecimal discount = rule.apply(basket);

        assertEquals(new BigDecimal("-10"), discount);
    }

    @Test
    void appliesDiscountWithEmptyBasket() {
        Item itemA = new Item("A", new BigDecimal("40"));
        Item itemC = new Item("C", new BigDecimal("30"));
        BundleDiscountRule rule = new BundleDiscountRule(itemA, itemC, new BigDecimal("5"));

        Map<Item, Integer> basket = Map.of();
        BigDecimal discount = rule.apply(basket);

        assertEquals(BigDecimal.ZERO, discount);
    }
}
