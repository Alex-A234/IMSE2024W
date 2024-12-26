package com.imse.onlineshop.controller.sql.order.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderResponse {
    private Date created;
}
