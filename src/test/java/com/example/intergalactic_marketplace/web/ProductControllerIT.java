package com.example.intergalactic_marketplace.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.intergalactic_marketplace.config.MappersTestConfiguration;
import com.example.intergalactic_marketplace.domain.Category;
import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.dto.Product.ProductDto;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToDeleteProductException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductsNotFoundException;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@Import({MappersTestConfiguration.class, ExceptionTranslator.class})
@DisplayName("Product Controller IT")
public class ProductControllerIT {
  @MockBean private ProductService productService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ProductMapper productMapper;

  private static Category category = Category.builder().id(1).name("Space Food").build();
  private static Customer customer =
      Customer.builder()
          .id(1L)
          .name("John Doe")
          .address("123 Space St")
          .email("john.doe@example.com")
          .build();
  private static Product product =
      Product.builder()
          .id(UUID.randomUUID())
          .name("Galaxy Fish Treats")
          .description("Delicious fish snacks for your interstellar feline friend.")
          .price(15)
          .category(category)
          .owner(customer)
          .build();

  private static Stream<Arguments> provideWrongNames() {
    return Stream.of(
        Arguments.arguments("fish", "shorter than 5"),
        Arguments.arguments("fish".repeat(30), "longer than 30"));
  }

  private static Stream<Arguments> provideWrongDescriptions() {
    return Stream.of(
        Arguments.arguments("fish", "shorter than 5"),
        Arguments.arguments("fish".repeat(200), "longer than 500"));
  }

  private static Stream<Arguments> provideWrongPrices() {
    return Stream.of(
        Arguments.arguments(0, "less than 1"), Arguments.arguments(100_001, "exceed 100 000"));
  }

  @ParameterizedTest
  @MethodSource("provideWrongNames")
  void shouldThrowInvalidNameException(String productName, String partOfErrorMsg) throws Exception {
    Product localProduct = product.toBuilder().name(productName).build();
    testInvalidProductField(localProduct, "name", partOfErrorMsg);
  }

  @ParameterizedTest
  @MethodSource("provideWrongDescriptions")
  void shouldThrowInvalidDescriptionException(String productDescription, String partOfErrorMsg)
      throws Exception {
    Product localProduct = product.toBuilder().description(productDescription).build();
    testInvalidProductField(localProduct, "description", partOfErrorMsg);
  }

  @ParameterizedTest
  @MethodSource("provideWrongPrices")
  void shouldThrowInvalidPriceException(int productPrice, String partOfErrorMsg) throws Exception {
    Product localProduct = product.toBuilder().price(productPrice).build();
    testInvalidProductField(localProduct, "price", partOfErrorMsg);
  }

  @Test
  void shouldCreateValidProduct() throws Exception {
    when(productService.createProduct(product, customer.getId())).thenReturn(product.getId());
    ProductDto productDto = productMapper.toProductDto(product);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productDto))
                .header("customerId", customer.getId().toString()))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(content().string(""));
  }

  @Test
  void shouldReturnProduct() throws Exception {
    when(productService.getProductById(product.getId())).thenReturn(product);
    mockMvc
        .perform(get("/v1/products/{productId}", product.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(product.getName()))
        .andExpect(jsonPath("$.description").value(product.getDescription()))
        .andExpect(jsonPath("$.price").value(product.getPrice()))
        .andExpect(jsonPath("$.category.name").value(product.getCategory().getName()));
  }

  @Test
  void shouldThrowProductNotFoun() throws Exception {
    when(productService.getProductById(product.getId()))
        .thenThrow(new ProductNotFoundException(product.getId()));
    String path = "/v1/products/" + product.getId().toString();
    mockMvc
        .perform(get(path))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.path").value(path))
        .andExpect(jsonPath("$.error").value("product-not-found"))
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void shouldThrowProductsNotFound() throws Exception {
    when(productService.getAllProducts()).thenThrow(new ProductsNotFoundException());
    String path = "/v1/products";
    mockMvc
        .perform(get(path))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.path").value(path))
        .andExpect(jsonPath("$.error").value("products-not-found"))
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void shouldThrowCustomerHasNoRules() throws Exception {
    doThrow(new CustomerHasNoRulesToDeleteProductException(customer.getId(), product.getId()))
        .when(productService)
        .deleteProductById(product.getId(), customer.getId());
    String path = "/v1/products/" + product.getId().toString();
    mockMvc
        .perform(delete(path).header("customerId", customer.getId()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.path").value(path))
        .andExpect(jsonPath("$.error").value("customer-has-no-rules"))
        .andExpect(jsonPath("$.message").exists());
  }

  private void testInvalidProductField(
      Product product, String invalidFieldName, String partOfErrorMsg) throws Exception {
    ProductDto productDto = productMapper.toProductDto(product);
    when(productService.createProduct(product, customer.getId())).thenReturn(product.getId());
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productDto))
                .header("customerId", customer.getId().toString()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.path").value("/v1/products"))
        .andExpect(jsonPath("$.error").value("validation-error"))
        .andExpect(jsonPath("$.message").doesNotExist())
        .andExpect(jsonPath("$.invalidParams").exists())
        .andExpect(jsonPath("$.invalidParams[0].fieldName").value(invalidFieldName))
        .andExpect(jsonPath("$.invalidParams[0].reason").value(containsString(partOfErrorMsg)));
  }
}
