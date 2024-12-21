package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.domain.Product;
import java.util.List;
import java.util.UUID;

public interface ProductService {
  UUID createProduct(Product product, Long requesterId);

  List<Product> getAllProducts();

  Product getProductById(UUID id);

  void updateProduct(Product product, Long requesterId);

  void deleteProductById(UUID productId, Long requesterId);
}
