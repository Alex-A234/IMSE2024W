package com.imse.onlineshop.controller.sql.order.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnOrderProductsDTO {
    private Long uuid;
    private ProductDTO product;
    private Integer amount;
}
