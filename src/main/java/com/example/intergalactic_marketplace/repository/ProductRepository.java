package com.example.intergalactic_marketplace.repository;

import com.example.intergalactic_marketplace.repository.entity.ProductEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, UUID> {
  List<ProductEntity> findByName(String name);
}
