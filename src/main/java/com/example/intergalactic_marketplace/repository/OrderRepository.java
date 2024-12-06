package com.example.intergalactic_marketplace.repository;

import com.example.intergalactic_marketplace.repository.entity.OrderEntity;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<OrderEntity, UUID> {}
