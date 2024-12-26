package com.imse.onlineshop.nosql.entities;

import java.util.List;
import java.util.Set;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document("customers")
public class CustomerNoSQL extends UserNoSQL {
  private Integer age;
  private String phoneNumber;
  private Set<PaymentInformationNoSQL> paymentOptions;
  private Set<ReturnOrderNoSQL> returnedOrders;
  private List<ShoppingCartProductNoSQL> shoppingCartProducts;

  public CustomerNoSQL(String ssn, String name, String surname, String address, byte[] password,
      String email, Integer age, String phoneNumber, Set<PaymentInformationNoSQL> paymentOptions,
      Set<ReturnOrderNoSQL> returnedOrders, List<ShoppingCartProductNoSQL> shoppingCartProducts) {
    super(ssn, name, surname, address, password, email);
    this.age = age;
    this.phoneNumber = phoneNumber;
    this.paymentOptions = paymentOptions;
    this.returnedOrders = returnedOrders;
    this.shoppingCartProducts = shoppingCartProducts;
  }
}
