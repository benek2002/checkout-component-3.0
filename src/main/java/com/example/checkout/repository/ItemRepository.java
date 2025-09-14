package com.example.checkout.repository;

import com.example.checkout.model.Item;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRepository {

    private final Map<String, Item> items = new HashMap<>();

    public ItemRepository() {
        items.put("A", new Item("A", new BigDecimal("40")));
        items.put("B", new Item("B", new BigDecimal("10")));
        items.put("C", new Item("C", new BigDecimal("30")));
        items.put("D", new Item("D", new BigDecimal("25")));
    }

    public Item findByName(String name) {
        return items.get(name);
    }
}
