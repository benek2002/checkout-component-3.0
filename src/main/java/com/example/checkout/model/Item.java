package com.example.checkout.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
@Getter
@AllArgsConstructor
@ToString
public class Item {
    private String name;
    private BigDecimal price;
}
