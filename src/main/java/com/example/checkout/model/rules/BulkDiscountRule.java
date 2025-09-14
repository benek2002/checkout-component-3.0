package com.example.checkout.model.rules;

import com.example.checkout.model.Item;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
public class BulkDiscountRule implements PricingRule {
    private final Item item;
    private final int requiredQty;
    private final BigDecimal discountedPrice;

    @Override
    public BigDecimal apply(Map<Item, Integer> items) {
        int qty = items.getOrDefault(item, 0);
        if (qty < requiredQty) return BigDecimal.ZERO;

        BigDecimal normalTotal = item.getPrice().multiply(BigDecimal.valueOf(qty));
        BigDecimal promoTotal = discountedPrice.multiply(BigDecimal.valueOf(qty));

        return promoTotal.subtract(normalTotal);
    }
}
