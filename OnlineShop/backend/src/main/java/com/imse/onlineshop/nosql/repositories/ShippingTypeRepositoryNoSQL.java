package com.imse.onlineshop.nosql.repositories;

import com.imse.onlineshop.nosql.entities.ShippingTypeNoSQL;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingTypeRepositoryNoSQL extends MongoRepository<ShippingTypeNoSQL, Long> {
}
