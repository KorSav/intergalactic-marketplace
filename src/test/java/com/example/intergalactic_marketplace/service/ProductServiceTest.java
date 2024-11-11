package com.example.intergalactic_marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToDeleteProductException;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToUpdateProductException;
import com.example.intergalactic_marketplace.service.exception.CustomerNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductsNotFoundException;
import com.example.intergalactic_marketplace.service.impl.CustomerServiceImpl;
import com.example.intergalactic_marketplace.service.impl.ProductServiceImpl;

@SpringBootTest(classes = { ProductServiceImpl.class, CustomerServiceImpl.class })
@DisplayName("Product Service Tests")
@TestMethodOrder(OrderAnnotation.class)
public class ProductServiceTest {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    private static List<Product> products;
    private static UUID newProductId;
    private static Integer expectedProductsSize = 3;
    private static String newProductName = "test product";
    private static String updatedProductName = "updated test product";
    private static Long newProductOwnerId = 1L;
    private static Long otherProductOwnerId = 2L;

    static Stream<Product> provideProducts() {
        return products.stream();
    }

    @Test
    @Order(1)
    void shouldReturnAllProducts() {
        products = productService.getAllProducts();
        assertNotNull(products);
        assertEquals(expectedProductsSize, products.size());
    }

    @ParameterizedTest
    @MethodSource("provideProducts")
    @Order(2)
    void shouldReturnProductByID(Product expectedProduct) {
        Product actualProduct = productService.getProductById(expectedProduct.getId());
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    @Order(3)
    void shouldCreateProduct() {
        Product expectedProduct = Product.builder()
                .id(UUID.randomUUID())
                .name(newProductName)
                .owner(customerService.getCustomerById(newProductOwnerId))
                .build();

        assertThrows(CustomerNotFoundException.class,
                () -> productService.createProduct(expectedProduct, -1L));

        newProductId = productService.createProduct(expectedProduct, newProductOwnerId);
        Product actualProduct = productService.getProductById(newProductId);

        assertNotNull(actualProduct);
        assertEquals(expectedProduct.getName(), actualProduct.getName());
        assertEquals(expectedProduct.getOwner(), actualProduct.getOwner());
        assertThrows(ProductAlreadyExistsException.class,
                () -> productService.createProduct(actualProduct, newProductOwnerId));
    }

    @Test
    @Order(4)
    void shouldUpdateProduct() {
        Product expectedProduct = Product.builder()
                .id(newProductId)
                .name(updatedProductName)
                .owner(customerService.getCustomerById(newProductOwnerId))
                .build();

        productService.updateProduct(expectedProduct, newProductOwnerId);
        Product actualProduct = productService.getProductById(newProductId);

        assertNotNull(actualProduct);
        assertEquals(expectedProduct.getName(), actualProduct.getName());
        assertEquals(expectedProduct.getOwner(), actualProduct.getOwner());
        assertThrows(CustomerHasNoRulesToUpdateProductException.class,
                () -> productService.updateProduct(actualProduct, otherProductOwnerId));
    }

    @Test
    @Order(4)
    void throwsExceptionWhenUpdatingProductWithExistingName() {
        Product productWithExistingName = Product.builder()
                .id(newProductId)
                .name(products.get(0).getName())
                .owner(customerService.getCustomerById(newProductOwnerId))
                .build();
        assertThrows(ProductAlreadyExistsException.class,
                () -> productService.updateProduct(productWithExistingName, newProductOwnerId));
    }

    @Test
    @Order(5)
    void shouldDeleteProduct() {
        assertThrows(CustomerHasNoRulesToDeleteProductException.class,
                () -> productService.deleteProductById(newProductId, otherProductOwnerId));

        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProductById(UUID.randomUUID(), otherProductOwnerId));

        productService.deleteProductById(newProductId, newProductOwnerId);
        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductById(newProductId));
    }

    @Test
    @Order(6)
    void shouldThrowProductsNotFound() {
        List<Product> productsCopy = new ArrayList<>(products);
        for (Product p : productsCopy) {
            productService.deleteProductById(p.getId(), p.getOwner().getId());
        }
        assertThrows(ProductsNotFoundException.class,
                () -> productService.getAllProducts());
    }
}
