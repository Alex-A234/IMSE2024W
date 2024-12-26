package com.imse.onlineshop.nosql.entities;

import java.util.Set;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document("producers")
public class ProducerNoSQL extends UserNoSQL {
  @Indexed(unique = true)
  private String producerName;
  private String headquarters;
  private Set<ProductNoSQL> products;
  private Set<String> producers;

  public ProducerNoSQL(String ssn, String name, String surname, String address, byte[] password,
      String email, String producerName, String headquarters, Set<ProductNoSQL> products,
      Set<String> producers) {
    super(ssn, name, surname, address, password, email);
    this.producerName = producerName;
    this.headquarters = headquarters;
    this.products = products;
    this.producers = producers;
  }
}
