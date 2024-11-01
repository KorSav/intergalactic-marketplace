package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.service.CustomerService;
import com.example.intergalactic_marketplace.service.exception.CustomerNotFoundException;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final List<Customer> customers = buildAllCustomersMock();

    @Override
    public Customer getCustomerById(Long id) {
        return customers.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> {
                log.info("Customer with id %d not found in mock", id);
                throw new CustomerNotFoundException(id);
            });
    }

    private List<Customer> buildAllCustomersMock() {
        return List.of(
            Customer.builder()
                .id(1L)
                .name("John Doe")
                .address("123 Space St")
                .email("john.doe@example.com")
                .build(),
            Customer.builder()
                .id(2L)
                .name("Jane Smith")
                .address("456 Galactic Ave")
                .email("jane.smith@example.com")
                .build()
        );
    }
}
