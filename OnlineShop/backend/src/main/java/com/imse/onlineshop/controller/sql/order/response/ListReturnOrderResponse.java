package com.imse.onlineshop.controller.sql.order.response;

import com.imse.onlineshop.controller.sql.order.response.dto.ProducerDTO;
import com.imse.onlineshop.controller.sql.order.response.dto.ProductDTO;
import com.imse.onlineshop.controller.sql.order.response.dto.ReturnOrderKeyDTO;
import com.imse.onlineshop.controller.sql.order.response.dto.ReturnOrderProductsDTO;
import com.imse.onlineshop.nosql.entities.orders.UserReturnOrder;
import com.imse.onlineshop.sql.entities.ReturnOrder;
import lombok.Data;

import java.sql.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
public class ListReturnOrderResponse {
    private ReturnOrderKeyDTO returnOrderKey;
    private String reasonDescription;
    private Date date;
    private ListOrderResponse order;
    private Set<ReturnOrderProductsDTO> returnOrderProducts;

    public static ListReturnOrderResponse from(ReturnOrder returnOrder) {
        return new ListReturnOrderResponse(
                new ReturnOrderKeyDTO(
                        returnOrder.getReturnOrderKey().getCustomerSsn(),
                        returnOrder.getReturnOrderKey().getReturnOrderId()
                ),
                returnOrder.getReasonDescription(),
                returnOrder.getDate(),
                ListOrderResponse.from(returnOrder.getOrder()),
                returnOrder.getReturnOrderProducts().stream()
                        .map(rp -> new ReturnOrderProductsDTO(
                                rp.getUuid(),
                                new ProductDTO(
                                        rp.getProduct().getProductNumber(),
                                        rp.getProduct().getPricePerUnit(),
                                        rp.getProduct().getProductName(),
                                        new ProducerDTO(
                                                rp.getProduct().getProducer().getProducerName()
                                        )
                                ),
                                rp.getAmount()
                        )).collect(Collectors.toSet())
        );
    }

    public static ListReturnOrderResponse fromNoSQL(UserReturnOrder returnOrder) {
        AtomicInteger i = new AtomicInteger();
       return new ListReturnOrderResponse(
               new ReturnOrderKeyDTO(
                       returnOrder.getReturnOrderKey().getCustomerSsn(),
                       returnOrder.getReturnOrderKey().getReturnOrderId()
               ),
               returnOrder.getReasonDescription(),
               new Date(returnOrder.getDate().getTime()),
               ListOrderResponse.fromNoSQL(returnOrder.getOrder()),
               returnOrder.getReturnOrderProducts()
                       .stream()
                       .map(p -> new ReturnOrderProductsDTO(
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
                       )).collect(Collectors.toSet())
       );
    }

    private ListReturnOrderResponse(ReturnOrderKeyDTO returnOrderKey, String reasonDescription, Date date, ListOrderResponse order, Set<ReturnOrderProductsDTO> returnOrderProducts) {
        this.returnOrderKey = returnOrderKey;
        this.reasonDescription = reasonDescription;
        this.date = date;
        this.order = order;
        this.returnOrderProducts = returnOrderProducts;
    }
}
