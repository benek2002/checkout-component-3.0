package com.example.checkout.dto;

import java.math.BigDecimal;

public record ReceiptLineDto(String name,
                             int quantity,
                             BigDecimal unitPrice,
                             BigDecimal lineTotal) {
}
