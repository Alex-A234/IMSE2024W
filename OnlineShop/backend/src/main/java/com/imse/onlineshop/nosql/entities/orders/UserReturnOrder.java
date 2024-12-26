package com.imse.onlineshop.nosql.entities.orders;

import com.imse.onlineshop.controller.sql.order.response.dto.ReturnOrderProductsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReturnOrder {
    private ReturnOrderKey returnOrderKey;
    private String reasonDescription;
    private Date date;
    private UserOrder order;
    private Set<ReturnOrderProductsDTO> returnOrderProducts;
}
