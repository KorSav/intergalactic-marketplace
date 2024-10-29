package com.example.intergalactic_marketplace.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.dto.CustomerDto;
import com.example.intergalactic_marketplace.dto.CustomerEntry;
import com.example.intergalactic_marketplace.dto.CustomerListDto;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface CustomerMapper {
    
    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "email", source = "email")
    CustomerDto toCustomerDto(Customer customer);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "email", source = "email")
    CustomerEntry toCustomerEntry(Customer customer);

    default CustomerListDto toCustomerListDto(List<Customer> customers) {
        return CustomerListDto.builder().customers(toCustomerEntries(customers)).build();
    }
    List<CustomerEntry> toCustomerEntries(List<Customer> customers);
}
