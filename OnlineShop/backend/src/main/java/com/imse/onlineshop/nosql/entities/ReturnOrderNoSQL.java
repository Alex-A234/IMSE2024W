package com.imse.onlineshop.nosql.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// embedded inside Customer.
public class ReturnOrderNoSQL {
    @Indexed(name = "user_return_order", unique = true)
    private Long returnOrderId;
    private String reasonDescription;

    @Indexed(name = "user_return_order_date", unique = true)
    private Date date;
    private Long orderId;
    private Set<ReturnOrderProductNoSQL> products;
}
