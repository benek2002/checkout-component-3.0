package com.example.checkout.unit.rules;

import com.example.checkout.model.Item;
import com.example.checkout.model.rules.UnitPriceRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitPriceRuleTest {

    private UnitPriceRule rule;

    @BeforeEach
    void setup() {
        rule = new UnitPriceRule();
    }

    @Test
    void calculatesTotalForSingleItem() {
        Item item = new Item("A", new BigDecimal("40"));
        Map<Item, Integer> basket = new HashMap<>();
        basket.put(item, 1);

        BigDecimal total = rule.apply(basket);
        assertEquals(new BigDecimal("40"), total);
    }

    @Test
    void calculatesTotalForMultipleItems() {
        Item itemA = new Item("A", new BigDecimal("40"));
        Item itemB = new Item("B", new BigDecimal("10"));

        Map<Item, Integer> basket = new HashMap<>();
        basket.put(itemA, 2);
        basket.put(itemB, 3);

        BigDecimal total = rule.apply(basket);
        assertEquals(new BigDecimal("110"), total);
    }

    @Test
    void calculatesTotalForEmptyBasket() {
        Map<Item, Integer> basket = new HashMap<>();
        BigDecimal total = rule.apply(basket);
        assertEquals(BigDecimal.ZERO, total);
    }
}
