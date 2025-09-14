package com.example.checkout.service;

import com.example.checkout.model.Item;
import com.example.checkout.model.Receipt;
import com.example.checkout.repository.ItemRepository;
import com.example.checkout.repository.PricingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final ItemRepository itemRepository;
    private final PricingRuleRepository ruleRepository;
    private final Map<UUID, Map<Item, Integer>> sessions = new HashMap<>();

    public UUID startSession() {
        UUID id = UUID.randomUUID();
        sessions.put(id, new HashMap<>());
        return id;
    }

    public void scan(UUID sessionId, String name) {
        Item item = itemRepository.findByName(name);
        if (item == null) throw new IllegalArgumentException("Unknown Name: " + name);

        sessions.computeIfAbsent(sessionId, k -> new HashMap<>())
                .merge(item, 1, Integer::sum);
    }

    public Receipt checkout(UUID sessionId) {
        Map<Item, Integer> basket = sessions.getOrDefault(sessionId, Map.of());

        BigDecimal total = ruleRepository.getAllRules().stream()
                .map(rule -> rule.apply(basket))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Receipt(basket, total);
    }
}
