package com.imse.onlineshop.nosql.entities.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderProducts {
    private Long uuid;
    private Product product;
    private Integer amount;
}
