package com.example.checkout.model.rules;

import com.example.checkout.model.Item;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
public class BundleDiscountRule implements PricingRule {
    private final Item itemA;
    private final Item itemB;
    private final BigDecimal discount;

    @Override
    public BigDecimal apply(Map<Item, Integer> items) {
        int countA = items.getOrDefault(itemA, 0);
        int countB = items.getOrDefault(itemB, 0);
        int pairs = Math.min(countA, countB);

        return discount.negate().multiply(BigDecimal.valueOf(pairs));
    }
}
