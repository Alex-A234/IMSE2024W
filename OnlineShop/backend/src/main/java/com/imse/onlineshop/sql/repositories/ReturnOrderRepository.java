package com.imse.onlineshop.sql.repositories;

import com.imse.onlineshop.sql.entities.Customer;
import com.imse.onlineshop.sql.entities.Order;
import com.imse.onlineshop.sql.entities.ReturnOrder;
import com.imse.onlineshop.sql.entities.ReturnOrderKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, ReturnOrderKey> {
    List<ReturnOrder> findAllByCustomer(Customer customer);

    List<ReturnOrder> findAllByOrder(Order order);

    @Query(nativeQuery = true, value = "SELECT count(distinct return_order_id) + 1 from return_orders")
    Long nextId();
}
