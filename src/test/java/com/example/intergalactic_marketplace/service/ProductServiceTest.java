package com.example.intergalactic_marketplace.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.example.intergalactic_marketplace.AbstractIt;
import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.repository.CategoryRepository;
import com.example.intergalactic_marketplace.repository.CustomerRepository;
import com.example.intergalactic_marketplace.repository.ProductRepository;
import com.example.intergalactic_marketplace.repository.entity.CategoryEntity;
import com.example.intergalactic_marketplace.repository.entity.CustomerEntity;
import com.example.intergalactic_marketplace.repository.entity.ProductEntity;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToUpdateProductException;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductsNotFoundException;
import jakarta.persistence.PersistenceException;
import java.util.*;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

@DisplayName("Product Service Tests with Testcontainers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceTest extends AbstractIt {

  @Autowired private ProductService productService;
  @SpyBean @Autowired private ProductRepository productRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private CategoryRepository categoryRepository;

  private static UUID newProductId;
  private static String newProductName = "test product";
  private static String updatedProductName = "updated test product";
  private static Long newProductOwnerId;
  private static Long otherProductOwnerId;

  @AfterEach
  void cleanUp() {
    productRepository.deleteAll();
    customerRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @BeforeEach
  void setUp() {
    reset(productRepository);
    CustomerEntity customer1 =
        customerRepository.save(
            CustomerEntity.builder().name("Owner 1").address("addr1").email("email1").build());
    CustomerEntity customer2 =
        customerRepository.save(
            CustomerEntity.builder().name("Owner 2").address("addr2").email("email2").build());

    CategoryEntity category =
        categoryRepository.save(CategoryEntity.builder().name("Test Category").build());

    ProductEntity product1 =
        ProductEntity.builder()
            .id(UUID.randomUUID())
            .name("Product 1")
            .description("description")
            .price(12)
            .owner(customer1)
            .category(category)
            .build();
    ProductEntity product2 =
        ProductEntity.builder()
            .id(UUID.randomUUID())
            .name("Product 2")
            .description("description")
            .price(3)
            .owner(customer2)
            .category(category)
            .build();

    productRepository.save(product1);
    productRepository.save(product2);

    newProductOwnerId = customer1.getId();
    otherProductOwnerId = customer2.getId();
  }

  @Test
  @Order(1)
  void shouldReturnAllProducts() {
    List<Product> products = productService.getAllProducts();
    assertNotNull(products);
    assertEquals(2, products.size());
  }

  @Test
  @Order(2)
  void shouldReturnProductByID() {
    ProductEntity productEntity = productRepository.findAll().iterator().next();
    Product product = productService.getProductById(productEntity.getId());
    assertNotNull(product);
    assertEquals(productEntity.getName(), product.getName());
  }

  @Test
  @Order(3)
  void shouldCreateProduct() {
    Product newProduct =
        Product.builder()
            .name(newProductName)
            .owner(Customer.builder().id(newProductOwnerId).build())
            .build();

    newProductId = productService.createProduct(newProduct, newProductOwnerId);
    Product createdProduct = productService.getProductById(newProductId);

    assertNotNull(createdProduct);
    assertEquals(newProductName, createdProduct.getName());
    assertThrows(
        ProductAlreadyExistsException.class,
        () -> productService.createProduct(newProduct, newProductOwnerId));
  }

  @Test
  @Order(4)
  void shouldUpdateProduct() {
    ProductEntity existingProduct = productRepository.findAll().iterator().next();
    Product updatedProduct =
        Product.builder()
            .id(existingProduct.getId())
            .name(updatedProductName)
            .owner(Customer.builder().id(newProductOwnerId).build())
            .build();

    productService.updateProduct(updatedProduct, newProductOwnerId);
    Product result = productService.getProductById(existingProduct.getId());

    assertNotNull(result);
    assertEquals(updatedProductName, result.getName());
    assertThrows(
        CustomerHasNoRulesToUpdateProductException.class,
        () -> productService.updateProduct(updatedProduct, newProductOwnerId + 1));
  }

  @Test
  @Order(4)
  void throwsExceptionWhenUpdatingProductWithExistingName() {
    ProductEntity existingProduct = productRepository.findAll().iterator().next();
    Product productWithExistingName =
        Product.builder()
            .id(newProductId)
            .name(existingProduct.getName())
            .owner(Customer.builder().id(newProductOwnerId).build())
            .build();
    assertThrows(
        ProductAlreadyExistsException.class,
        () -> productService.updateProduct(productWithExistingName, newProductOwnerId));
  }

  @Test
  @Order(5)
  void shouldDeleteProduct() {
    ProductEntity existingProduct = productRepository.findAll().iterator().next();

    productService.deleteProductById(existingProduct.getId(), newProductOwnerId);
    assertThrows(
        ProductNotFoundException.class,
        () -> productService.getProductById(existingProduct.getId()));
  }

  @Test
  @Order(6)
  void shouldThrowProductNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThrows(ProductNotFoundException.class, () -> productService.getProductById(randomId));
  }

  @Test
  @Order(7)
  void shouldThrowProductsNotFound() {
    productRepository.deleteAll();
    assertThrows(ProductsNotFoundException.class, () -> productService.getAllProducts());
  }

  @Test
  @Order(8)
  void shouldThrowPersistenceException() {
    UUID randomId = UUID.randomUUID();
    when(productRepository.findById(randomId)).thenThrow(JDBCConnectionException.class);
    assertThrows(PersistenceException.class, () -> productService.getProductById(randomId));
  }
}
