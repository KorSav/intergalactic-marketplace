package com.example.intergalactic_marketplace.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.intergalactic_marketplace.AbstractIt;
import com.example.intergalactic_marketplace.domain.Category;
import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.dto.Product.ProductDto;
import com.example.intergalactic_marketplace.repository.CategoryRepository;
import com.example.intergalactic_marketplace.repository.CustomerRepository;
import com.example.intergalactic_marketplace.repository.ProductRepository;
import com.example.intergalactic_marketplace.repository.entity.CategoryEntity;
import com.example.intergalactic_marketplace.repository.entity.CustomerEntity;
import com.example.intergalactic_marketplace.repository.entity.ProductEntity;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.mapper.CategoryMapper;
import com.example.intergalactic_marketplace.service.mapper.CustomerMapper;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@DisplayName("Product Controller IT")
public class ProductControllerIT extends AbstractIt {
  @SpyBean private ProductService productService;

  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private CustomerRepository customerRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ProductMapper productMapper;
  @Autowired private CategoryMapper categoryMapper;
  @Autowired private CustomerMapper customerMapper;

  @BeforeEach
  void setUp() {
    reset(productService);
    productRepository.deleteAll();
  }

  private CategoryEntity categoryEntity;
  private CustomerEntity customerEntity;
  private ProductEntity productEntity;
  private Category category;
  private Customer customer;
  private Product product;

  @BeforeAll
  void init() {
    categoryEntity = CategoryEntity.builder().name("Space Food").build();
    customerEntity =
        CustomerEntity.builder()
            .name("John Doe")
            .address("123 Space St")
            .email("john.doe@example.com")
            .build();
    productEntity =
        ProductEntity.builder()
            .id(UUID.randomUUID())
            .name("Galaxy Fish Treats")
            .description("Delicious fish snacks for your interstellar feline friend.")
            .price(15)
            .category(categoryEntity)
            .owner(customerEntity)
            .build();
  }

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
  @WithMockUser(roles = "COSMO_CAT")
  void shouldThrowInvalidNameException(String productName, String partOfErrorMsg) throws Exception {
    seedDb();
    mapDomainFromEntity();
    Product localProduct = product.toBuilder().name(productName).build();
    testInvalidProductField(localProduct, "name", partOfErrorMsg);
  }

  @ParameterizedTest
  @MethodSource("provideWrongDescriptions")
  @WithMockUser(roles = "COSMO_CAT")
  void shouldThrowInvalidDescriptionException(String productDescription, String partOfErrorMsg)
      throws Exception {
    seedDb();
    mapDomainFromEntity();
    Product localProduct = product.toBuilder().description(productDescription).build();
    testInvalidProductField(localProduct, "description", partOfErrorMsg);
  }

  @ParameterizedTest
  @MethodSource("provideWrongPrices")
  @WithMockUser(roles = "COSMO_CAT")
  void shouldThrowInvalidPriceException(int productPrice, String partOfErrorMsg) throws Exception {
    seedDb();
    mapDomainFromEntity();
    Product localProduct = product.toBuilder().price(productPrice).build();
    testInvalidProductField(localProduct, "price", partOfErrorMsg);
  }

  @Test
  void shouldCreateValidProduct() throws Exception {
    seedDbWithoutProduct();
    mapDomainFromEntity();
    mockCustomerInJwt(customer, List.of("COSMO_CAT"));
    ProductDto productDto = productMapper.toProductDto(product);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productDto)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(content().string(""));
  }

  @Test
  void shouldReturnProduct() throws Exception {
    seedDb();
    mapDomainFromEntity();
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
  void shouldThrowProductNotFound() throws Exception {
    seedDbWithoutProduct();
    mapDomainFromEntity();
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
    seedDbWithoutProduct();
    mapDomainFromEntity();
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
    seedDb();
    mapDomainFromEntity();
    mockCustomerInJwt(customer.toBuilder().id(-1L).build(), List.of("COSMO_CAT"));
    String path = "/v1/products/" + product.getId().toString();
    mockMvc
        .perform(delete(path))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.path").value(path))
        .andExpect(jsonPath("$.error").value("customer-has-no-rules"))
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void shouldThrowUnauthorized() throws Exception {
    seedDb();
    mapDomainFromEntity();
    String path = "/v1/products";
    mockMvc.perform(post(path)).andDo(print()).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldThrowForbidden() throws Exception {
    seedDb();
    mapDomainFromEntity();
    mockCustomerInJwt(customer, List.of("CAT"));
    String path = "/v1/products";
    mockMvc.perform(post(path)).andDo(print()).andExpect(status().isForbidden());
  }

  private void testInvalidProductField(
      Product product, String invalidFieldName, String partOfErrorMsg) throws Exception {
    ProductDto productDto = productMapper.toProductDto(product);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto))
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

  private void mockCustomerInJwt(Customer customer, List<String> roles) {
    Jwt jwt =
        Jwt.withTokenValue("mock-token")
            .header("alg", "RS256")
            .claim("customerId", customer.getId())
            .build();

    List<String> authorityList = roles.stream().map(r -> "ROLE_" + r).toList();
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(
        new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList(authorityList)));
    SecurityContextHolder.setContext(securityContext);
  }

  private void seedDb() {
    categoryEntity = categoryRepository.save(categoryEntity);
    customerEntity = customerRepository.save(customerEntity);

    productEntity.setOwner(customerEntity);
    productEntity.setCategory(categoryEntity);
    productRepository.save(productEntity);
  }

  private void seedDbWithoutProduct() {
    categoryEntity = categoryRepository.save(categoryEntity);
    customerEntity = customerRepository.save(customerEntity);
  }

  private void mapDomainFromEntity() {
    category = categoryMapper.fromCategoryEntity(categoryEntity);
    customer = customerMapper.fromCustomerEntity(customerEntity);
    product = productMapper.fromProductEntity(productEntity);
  }
}
