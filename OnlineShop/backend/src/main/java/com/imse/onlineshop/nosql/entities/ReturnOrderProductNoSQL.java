package com.imse.onlineshop.nosql.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// embedded inside ReturnOrder
public class ReturnOrderProductNoSQL {
    private Long product;
    private Integer amount;
}
