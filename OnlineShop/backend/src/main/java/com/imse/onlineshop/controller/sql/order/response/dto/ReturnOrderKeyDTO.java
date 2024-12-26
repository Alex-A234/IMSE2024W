package com.imse.onlineshop.controller.sql.order.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnOrderKeyDTO {
    private String customerSsn;
    private Long returnOrderId;
}
