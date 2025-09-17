package com.example.checkout.mapper;

import com.example.checkout.dto.DiscountDto;
import com.example.checkout.dto.ReceiptDto;
import com.example.checkout.dto.ReceiptLineDto;
import com.example.checkout.model.Receipt;

import java.util.stream.Collectors;

public class ReceiptMapper {
    public static ReceiptDto toDto(Receipt receipt) {
        return new ReceiptDto(
                receipt.getItems().stream()
                        .map(line -> new ReceiptLineDto(
                                line.getName(),
                                line.getQuantity(),
                                line.getUnitPrice(),
                                line.getLineTotal()
                        ))
                        .collect(Collectors.toList()),
                receipt.getDiscounts().stream()
                        .map(discount -> new DiscountDto(
                                discount.getDescription(),
                                discount.getAmount()
                        ))
                        .collect(Collectors.toList()),
                receipt.getSubtotal(),
                receipt.getTotal()
        );
    }
}
