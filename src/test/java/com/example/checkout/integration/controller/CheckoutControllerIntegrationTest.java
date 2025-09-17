package com.example.checkout.integration.controller;
import com.example.checkout.dto.DiscountDto;
import com.example.checkout.dto.ReceiptDto;
import com.example.checkout.dto.ReceiptLineDto;
import com.example.checkout.model.Receipt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckoutControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/checkout";
    }

    private UUID startSession() {
        return restTemplate.postForObject(baseUrl() + "/start", null, UUID.class);
    }

    private void scan(UUID sessionId, String itemName) {
        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/" + itemName, null, Void.class);
    }

    private ReceiptDto checkout(UUID sessionId) {
        return restTemplate.getForObject(baseUrl() + "/" + sessionId + "/total", ReceiptDto.class);
    }

    @Test
    void emptyCartReturnsZeroReceipt() {
        UUID session = startSession();
        ReceiptDto receipt = checkout(session);

        assertThat(receipt.subtotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(receipt.total()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(receipt.items()).isEmpty();
        assertThat(receipt.discounts()).isEmpty();
    }

    @Test
    void singleItemNoDiscount() {
        UUID session = startSession();
        scan(session, "B");

        ReceiptDto receipt = checkout(session);

        assertThat(receipt.subtotal()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(receipt.items()).hasSize(1);
        assertThat(receipt.discounts()).isEmpty();
    }

    @Test
    void multipleItemsNoPromotions() {
        UUID session = startSession();
        scan(session, "A");
        scan(session, "B");
        scan(session, "D");

        ReceiptDto receipt = checkout(session);

        assertThat(receipt.subtotal()).isEqualByComparingTo(new BigDecimal("75")); // 40+10+25
        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("75"));
        assertThat(receipt.discounts()).isEmpty();
    }

    @Test
    void bulkDiscountForB() {
        UUID session = startSession();
        scan(session, "B");
        scan(session, "B");

        ReceiptDto receipt = checkout(session);

        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("15.0"));
        assertThat(receipt.discounts()).isNotEmpty();
    }

    @Test
    void bulkDiscountForC() {
        UUID session = startSession();
        scan(session, "C");
        scan(session, "C");
        scan(session, "C");
        scan(session, "C");

        ReceiptDto receipt = checkout(session);

        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("80.0"));
        assertThat(receipt.discounts()).isNotEmpty();
    }

    @Test
    void bulkDiscountForD() {
        UUID session = startSession();
        scan(session, "D");
        scan(session, "D");

        ReceiptDto receipt = checkout(session);

        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("47.0"));
        assertThat(receipt.discounts()).isNotEmpty();
    }

    @Test
    void bundleDiscountAandC() {
        UUID session = startSession();
        scan(session, "A");
        scan(session, "C");

        ReceiptDto receipt = checkout(session);

        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("65.0"));
        assertThat(receipt.discounts()).isNotEmpty();
    }

    @Test
    void multipleSameBulkDiscounts() {
        UUID session = startSession();
        for (int i = 0; i < 6; i++) scan(session, "A");

        ReceiptDto receipt = checkout(session);

        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("180")); // 2x 3x30
        assertThat(receipt.discounts()).hasSize(1);
    }

    @Test
    void mixedDiscounts() {
        UUID session = startSession();
        scan(session, "A");
        scan(session, "A");
        scan(session, "A");
        scan(session, "B");
        scan(session, "B");
        scan(session, "C");

        ReceiptDto receipt = checkout(session);

        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("130.0"));
        assertThat(receipt.discounts()).isNotEmpty();
    }

    @Test
    void multipleSessionsAreIndependent() {
        UUID s1 = startSession();
        UUID s2 = startSession();

        scan(s1, "A");
        scan(s2, "B");

        ReceiptDto r1 = checkout(s1);
        ReceiptDto r2 = checkout(s2);

        assertThat(r1.total()).isEqualByComparingTo(new BigDecimal("40"));
        assertThat(r2.total()).isEqualByComparingTo(new BigDecimal("10"));
    }
}
