package com.imse.onlineshop.nosql.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportProductsNoSQL {
  private String productName;
  private Integer totalOrdered;
  private String producerName;
}
