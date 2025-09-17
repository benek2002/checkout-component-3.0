package com.example.checkout.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
public class Receipt {
    private final List<ReceiptLine> items;
    private final List<DiscountLine> discounts;
    private final BigDecimal subtotal;  // suma przed rabatami
    private final BigDecimal total;     // suma po rabatach

    public Receipt(List<ReceiptLine> items, List<DiscountLine> discounts, BigDecimal subtotal, BigDecimal total) {
        this.items = items;
        this.discounts = discounts;
        this.subtotal = subtotal;
        this.total = total;
    }

    @Getter
    public static class ReceiptLine {
        private final String name;
        private final int quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal lineTotal;

        public ReceiptLine(String name, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.lineTotal = lineTotal;
        }
    }

    @Getter
    public static class DiscountLine {
        private final String description;
        private final BigDecimal amount; // np. -5.00

        public DiscountLine(String description, BigDecimal amount) {
            this.description = description;
            this.amount = amount;
        }
    }
}
