package com.example.intergalactic_marketplace.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.intergalactic_marketplace.repository.entity.ProductEntity;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, UUID>{}
