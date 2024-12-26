package com.imse.onlineshop.sql.repositories;

import com.imse.onlineshop.sql.entities.ReturnOrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnOrderProductsRepository extends JpaRepository<ReturnOrderProducts, Long> {
    @Query(nativeQuery = true, value = "SELECT rop.*\n" +
            "FROM return_orders ro\n" +
            "         INNER JOIN return_order_products rop\n" +
            "                    ON ro.customer_ssn = rop.customer_ssn\n" +
            "                        AND ro.return_order_id = rop.return_order_id\n" +
            "where ro.order_id = :order")
    List<ReturnOrderProducts> findAllByOrderId(@Param("order") Long order);
}
