package com.imse.onlineshop.nosql.entities;

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
@Document("delivery_companies")
public class DeliveryCompanyNoSQL {
    @MongoId
    private Long deliveryCompanyId;
    private String name;
    private String city;
}
