package com.imse.onlineshop.sql.repositories;

import com.imse.onlineshop.sql.entities.DeliveryCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryCompanyRepository extends JpaRepository<DeliveryCompany, Long> {
}
