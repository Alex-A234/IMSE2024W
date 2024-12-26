package com.imse.onlineshop.nosql.entities;

import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Embedded in Producer.
public class ProductNoSQL {
  @Indexed(name = "producer_product_number", unique = true)
  private Long productNumber;
  private Double pricePerUnit;
  private Integer amount;
  private String productName;
}
