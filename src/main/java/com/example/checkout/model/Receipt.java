package com.example.checkout.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@ToString
@RequiredArgsConstructor
public class Receipt {
    private final Map<Item, Integer> items;
    private final BigDecimal total;
}
