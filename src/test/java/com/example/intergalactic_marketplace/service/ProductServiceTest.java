package com.example.intergalactic_marketplace.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.repository.ProductRepository;
import com.example.intergalactic_marketplace.repository.entity.ProductEntity;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToDeleteProductException;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToUpdateProductException;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductsNotFoundException;
import com.example.intergalactic_marketplace.service.impl.ProductServiceImpl;
import com.example.intergalactic_marketplace.service.mapper.CategoryMapperImpl;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import com.example.intergalactic_marketplace.service.mapper.ProductMapperImpl;
import jakarta.persistence.PersistenceException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(
    classes = {ProductServiceImpl.class, ProductMapperImpl.class, CategoryMapperImpl.class})
@DisplayName("Product Service Tests")
@TestMethodOrder(OrderAnnotation.class)
public class ProductServiceTest {
  @MockBean private ProductRepository productRepository;
  @MockBean private CustomerService customerService;
  @Autowired private ProductMapper productMapper;

  @Autowired private ProductService productService;

  private static UUID newProductId;
  private static String newProductName = "test product";
  private static String updatedProductName = "updated test product";
  private static Long newProductOwnerId = 1L;
  private static Long otherProductOwnerId = 2L;
  private static List<Product> products =
      List.of(
          Product.builder()
              .id(UUID.randomUUID())
              .owner(Customer.builder().id(newProductOwnerId).build())
              .build(),
          Product.builder()
              .id(UUID.randomUUID())
              .owner(Customer.builder().id(otherProductOwnerId).build())
              .build());

  static Stream<Product> provideProducts() {
    return products.stream();
  }

  @Test
  @Order(1)
  void shouldReturnAllProducts() {
    when(productRepository.findAll())
        .thenReturn(
            products.stream().map((product) -> productMapper.toProductEntity(product)).toList())
        .thenReturn(Collections.emptyList());
    when(customerService.getCustomerById(newProductOwnerId)).thenReturn(products.get(0).getOwner());
    when(customerService.getCustomerById(newProductOwnerId)).thenReturn(products.get(1).getOwner());
    assertNotNull(products);
    assertEquals(products.size(), productService.getAllProducts().size());
    assertThrows(ProductsNotFoundException.class, () -> productService.getAllProducts());
  }

  @ParameterizedTest
  @MethodSource("provideProducts")
  @Order(2)
  void shouldReturnProductByID(Product expectedProduct) {
    ProductEntity expectedProductEntity = productMapper.toProductEntity(expectedProduct);
    expectedProductEntity = expectedProductEntity.toBuilder().id(expectedProduct.getId()).build();
    when(productRepository.findById(expectedProduct.getId()))
        .thenReturn(Optional.of(expectedProductEntity))
        .thenReturn(Optional.empty());
    Product actualProduct = productService.getProductById(expectedProduct.getId());
    assertEquals(expectedProduct, actualProduct);
    assertThrows(
        ProductNotFoundException.class,
        () -> productService.getProductById(expectedProduct.getId()));
  }

  @Test
  @Order(3)
  void shouldCreateProduct() {
    Product expectedProduct =
        Product.builder()
            .id(UUID.randomUUID())
            .name(newProductName)
            .owner(Customer.builder().id(newProductOwnerId).build())
            .build();
    ProductEntity expectedProductEntity = productMapper.toProductEntity(expectedProduct);

    when(productRepository.save(any(ProductEntity.class))).thenReturn(expectedProductEntity);
    when(productRepository.findById(expectedProduct.getId())).thenReturn(Optional.empty());
    when(customerService.getCustomerById(newProductOwnerId)).thenReturn(products.get(0).getOwner());
    newProductId = productService.createProduct(expectedProduct, newProductOwnerId);

    when(productRepository.findById(newProductId)).thenReturn(Optional.of(expectedProductEntity));
    Product actualProduct = productService.getProductById(newProductId);

    assertNotNull(actualProduct);
    assertEquals(expectedProduct.getName(), actualProduct.getName());
    assertEquals(expectedProduct.getOwner(), actualProduct.getOwner());
    assertThrows(
        ProductAlreadyExistsException.class,
        () -> productService.createProduct(actualProduct, newProductOwnerId));
  }

  @Test
  @Order(4)
  void shouldUpdateProduct() {
    Product expectedProduct =
        Product.builder()
            .id(newProductId)
            .name(updatedProductName)
            .owner(Customer.builder().id(newProductOwnerId).build())
            .build();

    when(productRepository.findByName(updatedProductName)).thenReturn(Collections.emptyList());
    when(productRepository.findById(newProductId))
        .thenReturn(Optional.of(productMapper.toProductEntity(expectedProduct)));
    productService.updateProduct(expectedProduct, newProductOwnerId);
    Product actualProduct = productService.getProductById(newProductId);

    assertNotNull(actualProduct);
    assertEquals(expectedProduct.getName(), actualProduct.getName());
    assertEquals(expectedProduct.getOwner(), actualProduct.getOwner());
    assertThrows(
        CustomerHasNoRulesToUpdateProductException.class,
        () -> productService.updateProduct(actualProduct, otherProductOwnerId));
  }

  @Test
  @Order(4)
  void throwsExceptionWhenUpdatingProductWithExistingName() {
    Product productWithExistingName =
        Product.builder()
            .id(newProductId)
            .name(products.get(0).getName())
            .owner(customerService.getCustomerById(newProductOwnerId))
            .build();
    when(productRepository.findByName(productWithExistingName.getName()))
        .thenReturn(List.of(productMapper.toProductEntity(productWithExistingName)));
    assertThrows(
        ProductAlreadyExistsException.class,
        () -> productService.updateProduct(productWithExistingName, newProductOwnerId));
  }

  @Test
  @Order(5)
  void shouldDeleteProduct() {
    when(productRepository.findById(newProductId))
        .thenReturn(Optional.of(productMapper.toProductEntity(products.get(0))))
        .thenReturn(Optional.empty());
    assertThrows(
        CustomerHasNoRulesToDeleteProductException.class,
        () -> productService.deleteProductById(newProductId, otherProductOwnerId));

    when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    assertDoesNotThrow(
        () -> productService.deleteProductById(UUID.randomUUID(), otherProductOwnerId));

    productService.deleteProductById(newProductId, newProductOwnerId);
    assertThrows(ProductNotFoundException.class, () -> productService.getProductById(newProductId));
  }

  @Test
  @Order(6)
  void shouldThrowProductsNotFound() {
    when(productRepository.findAll()).thenReturn(Collections.emptyList());
    assertThrows(ProductsNotFoundException.class, () -> productService.getAllProducts());
  }

  @Test
  @Order(7)
  void shouldThrowPersistenceException() {
    when(productRepository.findById(any(UUID.class))).thenThrow(JDBCConnectionException.class);
    assertThrows(PersistenceException.class, () -> productService.getProductById(newProductId));
    assertThrows(
        PersistenceException.class,
        () -> productService.deleteProductById(newProductId, newProductOwnerId));
  }
}
