package com.imse.onlineshop.nosql.entities.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrder {
    private Long order;
    private Double purchaseAmount;
    private Date date;
    private String cardNumber;
    private Set<OrderProducts> orderProducts;
}
