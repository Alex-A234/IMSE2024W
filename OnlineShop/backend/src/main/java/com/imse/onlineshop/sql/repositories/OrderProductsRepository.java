package com.imse.onlineshop.sql.repositories;

import com.imse.onlineshop.sql.entities.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductsRepository extends JpaRepository<OrderProducts, Long> {
}
