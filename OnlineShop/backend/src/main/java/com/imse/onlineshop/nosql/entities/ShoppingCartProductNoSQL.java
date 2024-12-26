package com.imse.onlineshop.nosql.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartProductNoSQL {
  // embedded inside Customer
  private Long product;
  private Double pricePerUnit;
  private String productName;
}
