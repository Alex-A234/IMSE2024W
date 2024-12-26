package com.imse.onlineshop.controller.sql.order;

import java.util.Set;
import com.imse.onlineshop.vo.ReturnProducts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlaceOrderRequest {

  private String shippingType;
  private String creditCardNumber;
  private String creditCardName;
  private String expirationDate;
  private Double productSum;
  private Set<ReturnProducts> products;
}
