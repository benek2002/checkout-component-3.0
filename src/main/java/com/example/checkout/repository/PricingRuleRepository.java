package com.example.checkout.repository;

import com.example.checkout.model.rules.BulkDiscountRule;
import com.example.checkout.model.rules.BundleDiscountRule;
import com.example.checkout.model.rules.PricingRule;
import com.example.checkout.model.rules.UnitPriceRule;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PricingRuleRepository {
    private final List<PricingRule> rules = new ArrayList<>();

    public PricingRuleRepository(ItemRepository itemRepository) {
        rules.add(new UnitPriceRule());
        rules.add(new BulkDiscountRule(itemRepository.findByName("A"), 3, new BigDecimal("30")));
        rules.add(new BulkDiscountRule(itemRepository.findByName("B"), 2, new BigDecimal("7.5")));
        rules.add(new BulkDiscountRule(itemRepository.findByName("C"), 4, new BigDecimal("20")));
        rules.add(new BulkDiscountRule(itemRepository.findByName("D"), 2, new BigDecimal("23.5")));
        rules.add(new BundleDiscountRule(itemRepository.findByName("A"), itemRepository.findByName("C"), new BigDecimal("5")));
    }

    public List<PricingRule> getAllRules() {
        return rules;
    }
}
