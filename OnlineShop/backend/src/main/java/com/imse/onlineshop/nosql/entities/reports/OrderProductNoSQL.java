package com.imse.onlineshop.nosql.entities.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// embedded inside Order
public class OrderProductNoSQL {
    private Long product;
    private Integer amount;
}
