package com.example.checkout.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReceiptDto(List<ReceiptLineDto> items,
                         List<DiscountDto> discounts,
                         BigDecimal subtotal,
                         BigDecimal total) {
}
