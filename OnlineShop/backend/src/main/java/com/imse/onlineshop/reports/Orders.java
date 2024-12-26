package com.imse.onlineshop.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

  private Integer ranking;
  private Integer totalOrdered;
  private String productName;
  private String producerName;
  private Integer year;
}
