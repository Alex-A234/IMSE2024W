package com.imse.onlineshop.nosql.repositories;

import com.imse.onlineshop.nosql.entities.DeliveryCompanyNoSQL;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryCompanyRepositoryNoSQL extends MongoRepository<DeliveryCompanyNoSQL, Long> {
}
