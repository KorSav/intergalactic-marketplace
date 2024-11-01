package com.example.intergalactic_marketplace.config;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.intergalactic_marketplace.service.mapper.CategoryMapper;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;

@TestConfiguration
public class MappersTestConfiguration {

    @Bean
    public ProductMapper paymentMapper() {
        return Mappers.getMapper(ProductMapper.class);
    }

    @Bean
    public CategoryMapper categoryMapper() {
        return Mappers.getMapper(CategoryMapper.class);
    }
}

