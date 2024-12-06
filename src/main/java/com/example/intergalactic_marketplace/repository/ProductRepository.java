package com.example.intergalactic_marketplace.repository;

import com.example.intergalactic_marketplace.repository.entity.ProductEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, UUID> {
  @Query("select p from ProductEntity p where p.name=:name and p.category.name=:categoryName")
  Optional<ProductEntity> findByNameAndCategoryName(String name, String categoryName);
}
