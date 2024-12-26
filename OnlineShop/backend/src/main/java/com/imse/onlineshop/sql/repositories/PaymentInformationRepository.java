package com.imse.onlineshop.sql.repositories;

import com.imse.onlineshop.sql.entities.PaymentInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentInformationRepository extends JpaRepository<PaymentInformation, Long> {
}
