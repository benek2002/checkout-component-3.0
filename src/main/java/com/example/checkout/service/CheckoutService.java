package com.example.checkout.service;

import com.example.checkout.exception.UnknownProductException;
import com.example.checkout.model.Item;
import com.example.checkout.model.Receipt;
import com.example.checkout.repository.ItemRepository;
import com.example.checkout.repository.PricingRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {
    private final ItemRepository itemRepository;
    private final PricingRuleRepository ruleRepository;
    private final Map<UUID, Map<Item, Integer>> sessions = new HashMap<>();

    public UUID startSession() {
        UUID id = UUID.randomUUID();
        sessions.put(id, new HashMap<>());
        log.info("Started new session: {}", id);
        return id;
    }

    public void scan(UUID sessionId, String name) {
        Item item = itemRepository.findByName(name);
        if (item == null)  {
            log.warn("Attempted to scan unknown item: {}", name);
            throw new UnknownProductException(name);
        }

        sessions.computeIfAbsent(sessionId, k -> new HashMap<>())
                .merge(item, 1, Integer::sum);

        log.info("Scanned item '{}' in session {}. Quantity now: {}", name, sessionId,
                sessions.get(sessionId).get(item));
    }

    public Receipt checkout(UUID sessionId) {
        Map<Item, Integer> basket = sessions.getOrDefault(sessionId, Map.of());

        List<Receipt.ReceiptLine> lines = basket.entrySet().stream()
                .map(entry -> {
                    Item item = entry.getKey();
                    int qty = entry.getValue();
                    BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(qty));
                    return new Receipt.ReceiptLine(item.getName(), qty, item.getPrice(), lineTotal);
                })
                .toList();

        BigDecimal subtotal = lines.stream()
                .map(Receipt.ReceiptLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Receipt.DiscountLine> discounts = ruleRepository.getAllRules().stream()
                .map(rule -> rule.apply(basket))
                .filter(d -> d.compareTo(BigDecimal.ZERO) < 0)
                .map(d -> new Receipt.DiscountLine("Promotion", d))
                .toList();

        BigDecimal totalDiscounts = discounts.stream()
                .map(Receipt.DiscountLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = subtotal.add(totalDiscounts);

        log.info("Checkout for session {}: subtotal={}, discounts={}, total={}",
                sessionId, subtotal, totalDiscounts, total);

        return new Receipt(lines, discounts, subtotal, total);
    }
}
