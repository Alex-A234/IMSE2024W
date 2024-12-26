package com.imse.onlineshop.sql.repositories;

import com.imse.onlineshop.sql.entities.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, String> {
}
