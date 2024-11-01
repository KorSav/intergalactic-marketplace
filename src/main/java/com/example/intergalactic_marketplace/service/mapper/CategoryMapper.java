package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.domain.Category;
import com.example.intergalactic_marketplace.dto.Category.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  CategoryDto toCategoryDto(Category category);
}
