package com.imse.onlineshop.nosql.entities.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderKey {
    private String customerSsn;
    private Long returnOrderId;
}
