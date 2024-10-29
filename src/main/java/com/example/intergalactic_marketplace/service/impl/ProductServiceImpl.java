package com.example.intergalactic_marketplace.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.domain.Category;
import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.service.CustomerService;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToDeleteProductException;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductsNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final List<Product> products = buildAllProductsMock();
    private final CustomerService customerService;

    public ProductServiceImpl(CustomerService customerService){
        this.customerService = customerService;
    }

    @Override
    public UUID createProduct(Product product) {
        if (products.stream().anyMatch(
            p -> p.getName().equals(product.getName()))){
            throw new ProductAlreadyExistsException(product.getName());
        }
        Product newProduct = product.toBuilder()
                .id(UUID.randomUUID())
                .build();
        products.add(newProduct);
        return newProduct.getId();
    }

    @Override
    public List<Product> getAllProducts() {
        if (products.isEmpty()){
            throw new ProductsNotFoundException();
        }
        return products;
    }

    @Override
    public Product getProductById(UUID id) {
        return products.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> {
                log.info("Product with {} id not found in mock", id);
                return new ProductNotFoundException(id);
            });
    }

    @Override
    public void updateProduct(Product product) {
        Product existingProduct = getProductById(product.getId());
        Product updatedProduct = existingProduct.toBuilder()
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .category(product.getCategory())
            .build();
        products.set(products.indexOf(existingProduct), updatedProduct);
    }

    @Override
    public void deleteProductById(UUID productId, Long requesterId) {
        Product existingProduct = getProductById(productId);
        Customer owner = existingProduct.getOwner();
        if (!owner.getId().equals(requesterId)){
            throw new CustomerHasNoRulesToDeleteProductException(requesterId, productId);
        }
        products.remove(existingProduct);
    }

    private List<Product> buildAllProductsMock() {
        List<Product> products = new ArrayList<>();

        Category spaceFood = Category.builder()
            .id(1)
            .name("Space Food")
            .build();
        Category toys = Category.builder()
            .id(2)
            .name("Cosmic Toys")
            .build();

        Customer customer1 = customerService.getCustomerById(1L);
        Customer customer2 = customerService.getCustomerById(2L);

        products.add(Product.builder()
                .id(UUID.randomUUID())
                .name("Galactic Fish Treats")
                .description("Delicious fish snacks for your interstellar feline friend.")
                .price(15)
                .category(spaceFood)
                .owner(customer1)
                .build());

        products.add(Product.builder()
                .id(UUID.randomUUID())
                .name("Laser Pointer")
                .description("High-powered laser pointer to keep your cosmic cat entertained for hours.")
                .price(30)
                .category(toys)
                .owner(customer2)
                .build());

        products.add(Product.builder()
                .id(UUID.randomUUID())
                .name("Anti-Gravity Harness")
                .description("State-of-the-art harness to keep your cat safe during space walks.")
                .price(120)
                .category(toys)
                .owner(customer1)
                .build());

        return products;
    }

}
