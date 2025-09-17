package com.example.checkout.dto;

import java.math.BigDecimal;

public record DiscountDto(String description,
                          BigDecimal amount) {
}
