package com.imse.onlineshop.sql.repositories;

import com.imse.onlineshop.sql.entities.ShippingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingTypeRepository extends JpaRepository<ShippingType, Long> {
}
