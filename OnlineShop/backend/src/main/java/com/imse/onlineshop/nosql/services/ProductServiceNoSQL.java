package com.imse.onlineshop.nosql.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.imse.onlineshop.nosql.entities.CustomerNoSQL;
import com.imse.onlineshop.nosql.entities.ProducerNoSQL;
import com.imse.onlineshop.nosql.entities.ProductNoSQL;
import com.imse.onlineshop.nosql.repositories.UserRepositoryNoSQL;
import lombok.NonNull;

@Service
public class ProductServiceNoSQL {
  private final UserRepositoryNoSQL userRepositoryNoSQL;

  public ProductServiceNoSQL(UserRepositoryNoSQL userRepositoryNoSQL) {
    this.userRepositoryNoSQL = userRepositoryNoSQL;
  }

  public List<ProductNoSQL> findAllByProducer(@NonNull String producer) {
    var producerOpt = userRepositoryNoSQL.findById(producer);
    if (producerOpt.isEmpty() || producerOpt.get() instanceof CustomerNoSQL) {
      return List.of();
    }

    return new ArrayList<>(((ProducerNoSQL) producerOpt.get()).getProducts());
  }

  public ProductNoSQL findById(@NonNull Long id) {
    var productOpt = userRepositoryNoSQL.findByProductId(id);
    var product = ((ProducerNoSQL) productOpt.get()).getProducts().stream().findFirst();
    return product.get();
  }
}
