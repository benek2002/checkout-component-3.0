package com.example.checkout.model.rules;

import com.example.checkout.model.Item;

import java.math.BigDecimal;
import java.util.Map;

public interface PricingRule {
    BigDecimal apply(Map<Item, Integer> items);
}
