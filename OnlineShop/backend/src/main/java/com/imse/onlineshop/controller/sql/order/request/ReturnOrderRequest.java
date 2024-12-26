package com.imse.onlineshop.controller.sql.order.request;

import com.imse.onlineshop.vo.ReturnProducts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderRequest {
    private String description;
    private String customer;
    private Set<ReturnProducts> products;
}
