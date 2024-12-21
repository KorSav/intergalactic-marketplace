package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.dto.Customer.CustomerDto;
import com.example.intergalactic_marketplace.dto.Customer.CustomerEntry;
import com.example.intergalactic_marketplace.dto.Customer.CustomerListDto;
import com.example.intergalactic_marketplace.repository.entity.CustomerEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "address", source = "address")
  @Mapping(target = "email", source = "email")
  Customer fromCustomerEntry(CustomerEntry customerEntry);

  default CustomerListDto toCustomerListDto(List<Customer> customers) {
    return CustomerListDto.builder().customers(toCustomerEntries(customers)).build();
  }

  List<CustomerEntry> toCustomerEntries(List<Customer> customers);

  List<Customer> fromCustomerEntities(List<CustomerEntity> customers);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "customer.name")
  @Mapping(target = "address", source = "customer.address")
  @Mapping(target = "email", source = "customer.email")
  CustomerEntity toCustomerEntity(Customer customer);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "address", source = "address")
  @Mapping(target = "email", source = "email")
  Customer fromCustomerEntity(CustomerEntity customerEntity);
}
