package com.imse.onlineshop.sql.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.imse.onlineshop.sql.entities.Customer;
import com.imse.onlineshop.sql.entities.Producer;
import com.imse.onlineshop.sql.entities.Product;
import com.imse.onlineshop.sql.repositories.ProductRepository;
import com.imse.onlineshop.sql.repositories.UserRepository;
import lombok.NonNull;

@Service
public class ProductService {
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  public ProductService(ProductRepository productRepository, UserRepository userRepository) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
  }

  public List<Product> findAllByProducer(@NonNull String producer) {
    var producerOpt = userRepository.findById(producer);
    if (producerOpt.isEmpty() || producerOpt.get() instanceof Customer) {
      return List.of();
    }

    return productRepository.findAllByProducer((Producer) producerOpt.get());
  }

  public Product findById(@NonNull Long id) {
    var productOpt = productRepository.findById(id);
    return productOpt.get();
  }

}
