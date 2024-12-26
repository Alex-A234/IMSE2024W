package com.imse.onlineshop.sql.repositories;

import com.imse.onlineshop.sql.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query(nativeQuery = true, value = "SELECT ranking, name, surname, product_name, year, total_returned\n" +
            "FROM (\n" +
            "         SELECT ROW_NUMBER() over (PARTITION BY name, surname ORDER BY sum(rop.amount) DESC) as ranking,\n" +
            "                u.name,\n" +
            "                u.surname,\n" +
            "                p.product_name,\n" +
            "                year(ro.date) as year,\n" +
            "                sum(rop.amount) as total_returned\n" +
            "         FROM users as u\n" +
            "                  INNER JOIN customers as c ON c.ssn = u.ssn\n" +
            "                  INNER JOIN return_orders as ro ON c.ssn = ro.customer_ssn\n" +
            "                  INNER JOIN return_order_products as rop ON rop.return_order_id = ro.return_order_id\n" +
            "                  INNER JOIN products as p ON p.product_number = rop.product_number\n" +
            "         WHERE ro.date\n" +
            "                   BETWEEN (MAKEDATE(year(current_date) - 1, 1)) AND (MAKEDATE(year(current_date) - 1, 365))\n" +
            "         GROUP BY u.name,\n" +
            "                  u.surname,\n" +
            "                  p.product_name,\n" +
            "                  year(ro.date)\n" +
            "     ) as result\n" +
            "where result.ranking <= 5;")
    List<Object[]> top5MostReturnedProducts();
}
