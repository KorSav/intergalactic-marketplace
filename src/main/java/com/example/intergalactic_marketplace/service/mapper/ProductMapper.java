package com.example.intergalactic_marketplace.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

import com.example.intergalactic_marketplace.domain.Product;
import com.example.intergalactic_marketplace.dto.ProductDto;
import com.example.intergalactic_marketplace.dto.ProductEntry;
import com.example.intergalactic_marketplace.dto.ProductListDto;

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
    Product toProduct(ProductDto productDto);
}
