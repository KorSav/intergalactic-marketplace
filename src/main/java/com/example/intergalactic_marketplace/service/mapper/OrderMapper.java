package com.example.intergalactic_marketplace.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.intergalactic_marketplace.domain.Order;
import com.example.intergalactic_marketplace.dto.OrderDto;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface OrderMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "products", source = "products")
    OrderDto toOrderDto(Order order);
}