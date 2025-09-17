package com.example.checkout.controller;

import com.example.checkout.dto.ReceiptDto;
import com.example.checkout.mapper.ReceiptMapper;
import com.example.checkout.model.Receipt;
import com.example.checkout.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/start")
    public UUID startSession() {
        UUID sessionId = checkoutService.startSession();
        log.info("API: Started session {}", sessionId);
        return sessionId;
    }

    @PostMapping("/{sessionId}/scan/{name}")
    public void scanItem(@PathVariable UUID sessionId, @PathVariable String name) {
        log.info("API: Scanning item '{}' for session {}", name, sessionId);
        checkoutService.scan(sessionId, name);
    }

    @GetMapping("/{sessionId}/total")
    public ReceiptDto checkout(@PathVariable UUID sessionId) {
        log.info("API: Checkout requested for session {}", sessionId);
        Receipt receipt = checkoutService.checkout(sessionId);
        return ReceiptMapper.toDto(receipt);
    }
}
