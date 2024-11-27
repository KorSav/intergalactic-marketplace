package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.dto.Product.ProductDto;
import com.example.intergalactic_marketplace.dto.Product.ProductEntry;
import com.example.intergalactic_marketplace.dto.Product.ProductListDto;
import com.example.intergalactic_marketplace.repository.entity.ProductEntity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "category", source = "category")
  ProductDto toProductDto(Product product);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "category", source = "category.name")
  ProductEntry toProductEntry(Product product);

  default ProductListDto toProductListDto(List<Product> products) {
    return ProductListDto.builder().products(toProductEntries(products)).build();
  }

  List<ProductEntry> toProductEntries(List<Product> product);

  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "category", source = "category")
  Product fromProductDto(ProductDto productDto);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "category", source = "category")
  @Mapping(target = "owner", source = "owner")
  ProductEntity toProductEntity(Product product);

  default List<Product> fromProductEntities(Iterator<ProductEntity> productEntityIterator) {
    List<Product> result = new ArrayList<>();
    productEntityIterator.forEachRemaining(
        (productEntity) -> {
          result.add(fromProductEntity(productEntity));
        });
    return result;
  }

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "category", source = "category")
  @Mapping(target = "owner", source = "owner")
  Product fromProductEntity(ProductEntity productEntity);
}
