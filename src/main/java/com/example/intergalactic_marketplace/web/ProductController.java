package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.dto.Product.ProductDto;
import com.example.intergalactic_marketplace.dto.Product.ProductListDto;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("v1/products")
public class ProductController {
  private final ProductService productService;
  private final ProductMapper productMapper;

  ProductController(ProductService productService, ProductMapper productMapper) {
    this.productService = productService;
    this.productMapper = productMapper;
  }

  @GetMapping
  public ResponseEntity<ProductListDto> getAllProducts() {
    return ResponseEntity.ok(productMapper.toProductListDto(productService.getAllProducts()));
  }

  @GetMapping("/{productId}")
  public ResponseEntity<ProductDto> getProductById(@PathVariable UUID productId) {
    return ResponseEntity.ok(productMapper.toProductDto(productService.getProductById(productId)));
  }

  @PostMapping
  public ResponseEntity<Void> createProduct(
      @RequestBody @Valid ProductDto productDto, @RequestHeader Long customerId) {
    UUID productId = productService.createProduct(productMapper.toProduct(productDto), customerId);
    URI location = URI.create(String.format("v1/products/%s", productId));
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(location);
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @PutMapping("/{productId}")
  public ResponseEntity<Void> updateProduct(
      @PathVariable UUID productId,
      @RequestBody ProductDto productDto,
      @RequestHeader Long customerId) {
    Product product = productMapper.toProduct(productDto);
    product.toBuilder().id(productId).build();
    productService.updateProduct(product, customerId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> deleteProduct(
      @PathVariable UUID productId, @RequestHeader Long customerId) {
    productService.deleteProductById(productId, customerId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
