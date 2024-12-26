package com.imse.onlineshop.controller.nosql.products;

import com.imse.onlineshop.nosql.entities.ShoppingCartProductNoSQL;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponseShoppingCart {

  private Long productNumber;
  private Double pricePerUnit;
  private String productName;

  public static ProductResponseShoppingCart fromNoSQL(
      ShoppingCartProductNoSQL shoppingCartProductNoSQL) {
    return new ProductResponseShoppingCart(shoppingCartProductNoSQL.getProduct(),
        shoppingCartProductNoSQL.getPricePerUnit(), shoppingCartProductNoSQL.getProductName());
  }

}
