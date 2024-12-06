package com.example.intergalactic_marketplace.repository;

import com.example.intergalactic_marketplace.repository.entity.CustomerEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends ListCrudRepository<CustomerEntity, Long> {}
