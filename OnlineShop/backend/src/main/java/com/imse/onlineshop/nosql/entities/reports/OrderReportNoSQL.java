package com.imse.onlineshop.nosql.entities.reports;

import java.util.List;
import com.imse.onlineshop.nosql.entities.OrderReportProductsNoSQL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportNoSQL {
  private Integer year;
  private List<OrderReportProductsNoSQL> products;
}
