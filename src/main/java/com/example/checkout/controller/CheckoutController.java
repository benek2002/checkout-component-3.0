package com.example.checkout.controller;

import com.example.checkout.model.Receipt;
import com.example.checkout.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/start")
    public UUID startSession() {
        return checkoutService.startSession();
    }

    @PostMapping("/{sessionId}/scan/{name}")
    public void scanItem(@PathVariable UUID sessionId, @PathVariable String name) {
        checkoutService.scan(sessionId, name);
    }

    @GetMapping("/{sessionId}/total")
    public Receipt checkout(@PathVariable UUID sessionId) {
        return checkoutService.checkout(sessionId);
    }

}
