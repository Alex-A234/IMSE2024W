package com.imse.onlineshop.controller.sql.order.response;

import com.imse.onlineshop.controller.sql.order.response.dto.OrderProductsDTO;
import com.imse.onlineshop.controller.sql.order.response.dto.ProducerDTO;
import com.imse.onlineshop.controller.sql.order.response.dto.ProductDTO;
import com.imse.onlineshop.nosql.entities.orders.UserOrder;
import com.imse.onlineshop.sql.entities.Order;
import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
public class ListOrderResponse {
    private Long orderId;
    private Double purchaseAmount;
    private Date date;
    private String cardNumber;
    private Set<OrderProductsDTO> orderProducts;

    public static ListOrderResponse from(Order order) {
        return new ListOrderResponse(
                order.getOrderId(),
                order.getPurchaseAmount(),
                order.getDate(),
                order.getPaymentInformation().getCardNumber(),
                order.getOrderProducts().stream()
                        .map(p -> new OrderProductsDTO(
                                p.getUuid(),
                                new ProductDTO(
                                        p.getProduct().getProductNumber(),
                                        p.getProduct().getPricePerUnit(),
                                        p.getProduct().getProductName(),
                                        new ProducerDTO(
                                                p.getProduct().getProducer().getProducerName()
                                        )
                                ),
                                p.getAmount()
                        )).collect(Collectors.toSet())
        );
    }

    public static ListOrderResponse fromNoSQL(UserOrder order) {
        var orderProducts = new ArrayList<OrderProductsDTO>();

        AtomicInteger i = new AtomicInteger();
        order.getOrderProducts().forEach(p -> {
            orderProducts.add(new OrderProductsDTO(
                    (long) i.incrementAndGet(),
                    new ProductDTO(
                            p.getProduct().getProductNumber(),
                            p.getProduct().getPricePerUnit(),
                            p.getProduct().getProductName(),
                            new ProducerDTO(
                                    p.getProduct().getProducer().getProducerName()
                            )
                    ),
                    p.getAmount()
            ));
        });

        return new ListOrderResponse(
                order.getOrder(),
                order.getPurchaseAmount(),
                new Date(order.getDate().getTime()),
                order.getCardNumber(),
                new HashSet<>(orderProducts)
        );
    }

    private ListOrderResponse(Long orderId, Double purchaseAmount, Date date, String cardNumber, Set<OrderProductsDTO> orderProducts) {
        this.orderId = orderId;
        this.purchaseAmount = purchaseAmount;
        this.date = date;
        this.orderProducts = orderProducts;

        this.cardNumber = "*".repeat(Math.max(0, cardNumber.length() - 4)) +
                cardNumber.charAt(cardNumber.length() - 4) +
                cardNumber.charAt(cardNumber.length() - 3) +
                cardNumber.charAt(cardNumber.length() - 2) +
                cardNumber.charAt(cardNumber.length() - 1);
    }
}
