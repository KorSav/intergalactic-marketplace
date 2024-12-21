package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.repository.CustomerRepository;
import com.example.intergalactic_marketplace.repository.entity.CustomerEntity;
import com.example.intergalactic_marketplace.service.CustomerService;
import com.example.intergalactic_marketplace.service.exception.CustomerNotFoundException;
import com.example.intergalactic_marketplace.service.mapper.CustomerMapper;
import jakarta.persistence.PersistenceException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  final CustomerRepository repository;
  final CustomerMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public Customer getCustomerById(Long id) {
    Optional<CustomerEntity> customerEntityOptional = null;
    try {
      customerEntityOptional = repository.findById(id);
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
    if (customerEntityOptional.isEmpty()) {
      throw new CustomerNotFoundException(id);
    }
    return mapper.fromCustomerEntity(customerEntityOptional.get());
  }

  @Override
  public Customer createCustomer(Customer customer) {
    try {
      return mapper.fromCustomerEntity(repository.save(mapper.toCustomerEntity(customer)));
    } catch (Exception ex) {
      throw new PersistenceException(ex);
    }
  }

  @Override
  public void deleteCustomerById(Long id) {
    try {
      repository.deleteById(id);
    } catch (Exception ex) {
      throw new PersistenceException(ex);
    }
  }

  @Override
  public void updateCustomer(Customer customer) {
    try {
      repository.save(mapper.toCustomerEntity(customer));
    } catch (Exception ex) {
      throw new PersistenceException(ex);
    }
  }
}
