package com.imse.onlineshop.nosql.entities;

import java.util.Date;
import java.util.Set;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import com.imse.onlineshop.nosql.entities.reports.OrderProductNoSQL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("orders")
public class OrderNoSQL {
  @MongoId
  private Long orderId;
  private Double purchaseAmount;
  private Date date;
  private Long deliveryCompanyId;
  private Long shippingTypeId;
  private Long paymentId;
  private String customerSSN;
  private Set<OrderProductNoSQL> products;
}
