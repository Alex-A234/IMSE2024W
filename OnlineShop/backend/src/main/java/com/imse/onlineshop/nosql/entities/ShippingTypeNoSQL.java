package com.imse.onlineshop.nosql.entities;

import com.imse.onlineshop.vo.ShipmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("shipping_types")
public class ShippingTypeNoSQL {
    @MongoId
    private Long shippingTypeId;
    private ShipmentType type;
    private String description;
}
