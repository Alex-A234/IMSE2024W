package com.imse.onlineshop.sql.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.imse.onlineshop.sql.entities.Producer;
import com.imse.onlineshop.sql.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findAllByProducer(Producer producer);

  Optional<Product> findById(Long id);
}
