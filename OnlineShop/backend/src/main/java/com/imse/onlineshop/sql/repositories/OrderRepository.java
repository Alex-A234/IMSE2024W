package com.imse.onlineshop.sql.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.imse.onlineshop.sql.entities.Customer;
import com.imse.onlineshop.sql.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findAllByCustomer(Customer customer);

  @Query(nativeQuery = true,
      value = "SELECT ranking, total_amount, product_name, producer_name, year\n" + "    FROM (\n"
          + "        SELECT ROW_NUMBER() over (ORDER BY sum(op.amount) DESC) as ranking, SUM(op.amount) as total_amount, p.product_name, pr.producer_name, year(o.date) as year\n"
          + "            from orders as o\n"
          + "                inner join order_products as op on o.order_id = op.order_id\n"
          + "                inner join products as p on op.product_number = p.product_number\n"
          + "                inner join producers as pr on p.producer_ssn = pr.ssn\n"
          + "                    WHERE o.date BETWEEN (MAKEDATE(year(current_date) - 1, 1)) AND (MAKEDATE(year(current_date) - 1, 365))\n"
          + "                    GROUP BY p.product_number, p.product_name, pr.producer_name, year(o.date)    ) as result\n"
          + "        WHERE result.ranking <= 10;")
  List<Object[]> findTop10OrderedProducts();



}
