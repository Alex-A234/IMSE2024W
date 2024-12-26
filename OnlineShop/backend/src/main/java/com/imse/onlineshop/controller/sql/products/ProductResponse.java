package com.imse.onlineshop.controller.sql.products;

import com.imse.onlineshop.nosql.entities.ProductNoSQL;
import com.imse.onlineshop.sql.entities.Product;
import lombok.Data;

@Data
public class ProductResponse {
  private Long productNumber;
  private Double pricePerUnit;
  private Integer amount;
  private String productName;

  public static ProductResponse from(Product product) {
    return new ProductResponse(product.getProductNumber(), product.getPricePerUnit(),
        product.getAmount(), product.getProductName());
  }

  public static ProductResponse fromNoSQL(ProductNoSQL productNoSQL) {
    return new ProductResponse(productNoSQL.getProductNumber(), productNoSQL.getPricePerUnit(),
        productNoSQL.getAmount(), productNoSQL.getProductName());
  }

  public ProductResponse(Long productNumber, Double pricePerUnit, Integer amount,
      String productName) {
    this.productNumber = productNumber;
    this.pricePerUnit = pricePerUnit;
    this.amount = amount;
    this.productName = productName;
  }
}
