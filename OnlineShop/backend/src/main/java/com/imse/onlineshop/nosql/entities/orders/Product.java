package com.imse.onlineshop.nosql.entities.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long productNumber;
    private Double pricePerUnit;
    private String productName;
    private Producer producer;
}
