package com.imse.onlineshop.controller.sql.order.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryCompanyDTO {
        private Long deliveryCompanyId;
        private String name;
        private String city;
}
