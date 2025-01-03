package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.repository.ProductRepository;
import com.example.intergalactic_marketplace.repository.entity.ProductEntity;
import com.example.intergalactic_marketplace.service.CustomerService;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToDeleteProductException;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToUpdateProductException;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductsNotFoundException;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  final ProductRepository productRepository;
  final CustomerService customerService;
  final ProductMapper productMapper;

  @Override
  @Transactional
  public UUID createProduct(Product product, Long requesterId) {
    Customer customer = customerService.getCustomerById(requesterId);
    Optional<ProductEntity> sameProductEntity = null;
    try {
      sameProductEntity =
          productRepository.findByNameAndCategoryName(
              product.getName(), product.getCategory().getName());
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
    if (sameProductEntity.isPresent()) {
      throw new ProductAlreadyExistsException(product.getName());
    }
    try {
      product = product.toBuilder().owner(customer).build();
      Product savedProduct =
          productMapper.fromProductEntity(
              productRepository.save(productMapper.toProductEntity(product)));
      return savedProduct.getId();
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
  }

  @Override
  @Transactional
  public List<Product> getAllProducts() {
    List<ProductEntity> productEntities = null;
    try {
      productEntities = productRepository.findAll();
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
    if (productEntities.isEmpty()) {
      throw new ProductsNotFoundException();
    }
    return productMapper.fromProductEntities(productEntities);
  }

  @Override
  @Transactional
  public Product getProductById(UUID id) {
    Optional<ProductEntity> productEntityOptional = null;
    try {
      productEntityOptional = productRepository.findById(id);
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
    if (productEntityOptional.isEmpty()) {
      throw new ProductNotFoundException(id);
    }
    return productMapper.fromProductEntity(productEntityOptional.get());
  }

  @Override
  @Transactional
  public void updateProduct(Product product, Long requesterId) {
    Optional<ProductEntity> existingProductSameName = Optional.empty();
    try {
      existingProductSameName =
          productRepository.findByNameAndCategoryName(
              product.getName(), product.getCategory().getName());
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
    boolean isProductNameUnique =
        existingProductSameName.isEmpty()
            || existingProductSameName.get().getId() == product.getId();
    if (!isProductNameUnique) {
      throw new ProductAlreadyExistsException(product.getName());
    }
    if (product.getOwner().getId() != requesterId) {
      throw new CustomerHasNoRulesToUpdateProductException(requesterId, product.getId());
    }
    try {
      productRepository.save(productMapper.toProductEntity(product));
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
  }

  @Override
  @Transactional
  public void deleteProductById(UUID productId, Long requesterId) {
    Optional<ProductEntity> existingProductEntityOptional = null;
    try {
      existingProductEntityOptional = productRepository.findById(productId);
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
    if (existingProductEntityOptional.isEmpty()) {
      return;
    }
    if (existingProductEntityOptional.get().getOwner().getId() != requesterId) {
      throw new CustomerHasNoRulesToDeleteProductException(requesterId, productId);
    }
    try {
      productRepository.deleteById(productId);
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
  }
}
