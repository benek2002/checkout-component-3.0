package com.example.checkout.model.rules;

import com.example.checkout.model.Item;

import java.math.BigDecimal;
import java.util.Map;

public class UnitPriceRule implements PricingRule {
    @Override
    public BigDecimal apply(Map<Item, Integer> items) {
        return items.entrySet().stream()
                .map(e -> e.getKey().getPrice().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
