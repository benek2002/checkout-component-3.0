package com.example.checkout.acceptance;

import com.example.checkout.dto.ReceiptDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckoutAcceptanceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/checkout";
    }

    @Test
    void bulkDiscountForC() {
        UUID sessionId = restTemplate.postForObject(baseUrl() + "/start", null, UUID.class);

        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/C", null, Void.class);
        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/C", null, Void.class);
        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/C", null, Void.class);
        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/C", null, Void.class);

        ReceiptDto receipt = restTemplate.getForObject(baseUrl() + "/" + sessionId + "/total", ReceiptDto.class);

        assertThat(receipt.items()).hasSize(1);
        assertThat(receipt.discounts()).hasSize(1);
        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("80"));
    }

    @Test
    void bundleDiscountAandC() {
        UUID sessionId = restTemplate.postForObject(baseUrl() + "/start", null, UUID.class);

        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/A", null, Void.class);
        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/C", null, Void.class);

        ReceiptDto receipt = restTemplate.getForObject(baseUrl() + "/" + sessionId + "/total", ReceiptDto.class);

        assertThat(receipt.items()).hasSize(2);
        assertThat(receipt.discounts()).hasSize(1);
        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("65"));
    }

    @Test
    void checkoutWithoutItemsReturnsZeroTotal() {
        UUID sessionId = restTemplate.postForObject(baseUrl() + "/start", null, UUID.class);

        ReceiptDto receipt = restTemplate.getForObject(baseUrl() + "/" + sessionId + "/total", ReceiptDto.class);

        assertThat(receipt.items()).isEmpty();
        assertThat(receipt.discounts()).isEmpty();
        assertThat(receipt.total()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void scanningUnknownItemReturnsBadRequest() {
        UUID sessionId = restTemplate.postForObject(baseUrl() + "/start", null, UUID.class);

        var response = restTemplate.postForEntity(baseUrl() + "/" + sessionId + "/scan/Z", null, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).contains("Unknown product: Z");
    }

    @Test
    void fullCheckoutFlowWithDiscounts() {
        UUID sessionId = restTemplate.postForObject(baseUrl() + "/start", null, UUID.class);

        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/A", null, Void.class);
        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/A", null, Void.class);
        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/A", null, Void.class);
        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/B", null, Void.class);
        restTemplate.postForObject(baseUrl() + "/" + sessionId + "/scan/B", null, Void.class);

        ReceiptDto receipt = restTemplate.getForObject(baseUrl() + "/" + sessionId + "/total", ReceiptDto.class);

        assertThat(receipt.items()).hasSize(2);
        assertThat(receipt.discounts()).hasSize(2);
        assertThat(receipt.total()).isEqualByComparingTo(new BigDecimal("105"));
    }
}


