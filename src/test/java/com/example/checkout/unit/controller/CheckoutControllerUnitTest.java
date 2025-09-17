package com.example.checkout.unit.controller;

import com.example.checkout.controller.CheckoutController;
import com.example.checkout.dto.ReceiptDto;
import com.example.checkout.model.Receipt;
import com.example.checkout.service.CheckoutService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutControllerUnitTest {

    @Test
    void startSessionReturnsUUID() {
        CheckoutService service = Mockito.mock(CheckoutService.class);
        UUID uuid = UUID.randomUUID();
        Mockito.when(service.startSession()).thenReturn(uuid);

        CheckoutController controller = new CheckoutController(service);
        UUID result = controller.startSession();

        assertNotNull(result);
        assertEquals(uuid, result);
    }

    @Test
    void scanItemCallsService() {
        CheckoutService service = Mockito.mock(CheckoutService.class);
        CheckoutController controller = new CheckoutController(service);

        UUID sessionId = UUID.randomUUID();
        String name = "A";

        controller.scanItem(sessionId, name);

        Mockito.verify(service, Mockito.times(1)).scan(sessionId, name);
    }

    @Test
    void checkoutReturnsReceiptDto() {
        CheckoutService service = Mockito.mock(CheckoutService.class);

        UUID sessionId = UUID.randomUUID();
        Receipt.ReceiptLine line = new Receipt.ReceiptLine("A", 2, new BigDecimal("40"), new BigDecimal("80"));
        Receipt mockReceipt = new Receipt(
                List.of(line),
                List.of(),
                new BigDecimal("80"),
                new BigDecimal("80")
        );

        Mockito.when(service.checkout(sessionId)).thenReturn(mockReceipt);

        CheckoutController controller = new CheckoutController(service);
        ReceiptDto receiptDto = controller.checkout(sessionId);

        assertNotNull(receiptDto);
        assertEquals(new BigDecimal("80"), receiptDto.total());
        assertEquals(1, receiptDto.items().size());
        assertEquals("A", receiptDto.items().get(0).name());
        assertEquals(2, receiptDto.items().get(0).quantity());
    }

    @Test
    void scanAndCheckoutFlow() {
        CheckoutService service = Mockito.mock(CheckoutService.class);
        CheckoutController controller = new CheckoutController(service);

        UUID sessionId = UUID.randomUUID();
        String name1 = "A";
        String name2 = "B";

        controller.scanItem(sessionId, name1);
        controller.scanItem(sessionId, name2);

        Mockito.verify(service, Mockito.times(1)).scan(sessionId, name1);
        Mockito.verify(service, Mockito.times(1)).scan(sessionId, name2);

        Receipt.ReceiptLine lineA = new Receipt.ReceiptLine("A", 1, new BigDecimal("40"), new BigDecimal("40"));
        Receipt.ReceiptLine lineB = new Receipt.ReceiptLine("B", 1, new BigDecimal("10"), new BigDecimal("10"));
        Receipt mockReceipt = new Receipt(
                List.of(lineA, lineB),
                List.of(),
                new BigDecimal("50"),
                new BigDecimal("50")
        );

        Mockito.when(service.checkout(sessionId)).thenReturn(mockReceipt);

        ReceiptDto receiptDto = controller.checkout(sessionId);
        assertEquals(new BigDecimal("50"), receiptDto.total());
        assertEquals(2, receiptDto.items().size());
    }

    @Test
    void checkoutWithDiscounts() {
        CheckoutService service = Mockito.mock(CheckoutService.class);
        CheckoutController controller = new CheckoutController(service);

        UUID sessionId = UUID.randomUUID();

        Receipt.ReceiptLine lineA = new Receipt.ReceiptLine("A", 3, new BigDecimal("40"), new BigDecimal("120"));

        Receipt.DiscountLine discount = new Receipt.DiscountLine("3 for 90 promo on A", new BigDecimal("-30"));

        Receipt mockReceipt = new Receipt(
                List.of(lineA),
                List.of(discount),
                new BigDecimal("120"),
                new BigDecimal("90")
        );

        Mockito.when(service.checkout(sessionId)).thenReturn(mockReceipt);

        ReceiptDto receiptDto = controller.checkout(sessionId);

        assertEquals(new BigDecimal("120"), receiptDto.subtotal());
        assertEquals(1, receiptDto.discounts().size());
        assertEquals("-30", receiptDto.discounts().get(0).amount().toPlainString());
        assertEquals(new BigDecimal("90"), receiptDto.total());
    }
}

