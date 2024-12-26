package com.imse.onlineshop.controller.sql.order.response;

import com.imse.onlineshop.controller.sql.order.response.dto.*;
import com.imse.onlineshop.sql.entities.Order;
import lombok.Data;

import java.sql.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class GetOrderResponse {
    private Long orderId;
    private Double purchaseAmount;
    private Date date;

    private DeliveryCompanyDTO deliveryCompany;
    private ResponseShippingTypeDTO shippingType;
    private PaymentInformationDTO paymentInformation;
    private Set<OrderProductsDTO> orderProducts;

    public static GetOrderResponse from(Order order) {
        return new GetOrderResponse(
                order.getOrderId(),
                order.getPurchaseAmount(),
                order.getDate(),
                new DeliveryCompanyDTO(
                        order.getDeliveryCompanies().getDeliveryCompanyId(),
                        order.getDeliveryCompanies().getName(),
                        order.getDeliveryCompanies().getCity()
                ),
                new ResponseShippingTypeDTO(
                        order.getShippingType().getShippingTypeId(),
                        order.getShippingType().getType()
                ),
                new PaymentInformationDTO(
                        order.getPaymentInformation().getPaymentID(),
                        order.getPaymentInformation().getCardNumber(),
                        order.getPaymentInformation().getNameOnCard(),
                        order.getPaymentInformation().getExpirationDate()
                ),
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

    private GetOrderResponse(Long orderId, Double purchaseAmount, Date date, DeliveryCompanyDTO deliveryCompany, ResponseShippingTypeDTO shippingType, PaymentInformationDTO paymentInformation, Set<OrderProductsDTO> orderProducts) {
        this.orderId = orderId;
        this.purchaseAmount = purchaseAmount;
        this.date = date;
        this.deliveryCompany = deliveryCompany;
        this.shippingType = shippingType;
        this.paymentInformation = paymentInformation;
        this.orderProducts = orderProducts;
    }
}
