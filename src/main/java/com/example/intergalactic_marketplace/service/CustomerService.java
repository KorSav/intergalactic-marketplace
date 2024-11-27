package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.domain.Customer;

public interface CustomerService {
  Customer getCustomerById(Long id);

  Customer createCustomer(Customer customer);

  void deleteCustomerById(Long id);

  void updateCustomer(Customer customer);
}
