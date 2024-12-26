package com.imse.onlineshop.controller.sql.order.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderProductsDTO {
    private Long uuid;
    private ProductDTO product;
    private Integer amount;
}
