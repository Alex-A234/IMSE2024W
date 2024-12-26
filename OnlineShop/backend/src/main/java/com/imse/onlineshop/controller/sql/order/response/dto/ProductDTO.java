package com.imse.onlineshop.controller.sql.order.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
    private Long productNumber;
    private Double pricePerUnit;
    private String productName;
    private ProducerDTO producer;
}
