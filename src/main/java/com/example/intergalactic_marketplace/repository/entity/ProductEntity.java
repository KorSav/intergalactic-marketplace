package com.example.intergalactic_marketplace.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "product")
public class ProductEntity {
  @Id UUID id;

  String name;
  String description;
  Integer price;

  @ManyToMany(mappedBy = "products")
  List<OrderEntity> orders;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  CustomerEntity owner;

  @ManyToOne
  @JoinColumn(name = "category_id")
  CategoryEntity category;
}
