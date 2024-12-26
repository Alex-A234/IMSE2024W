package com.imse.onlineshop.controller.sql.order.response;

import java.util.List;
import com.imse.onlineshop.reports.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReportResponse {
  List<Orders> rows;
}
