package com.imse.onlineshop.controller.sql.order.response.dto;

import com.imse.onlineshop.vo.ShipmentType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseShippingTypeDTO {
    private Long shippingTypeId;
    private ShipmentType type;
}
