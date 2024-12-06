package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.domain.Customer;
import com.example.intergalactic_marketplace.featureToggle.FeatureToggles;
import com.example.intergalactic_marketplace.featureToggle.annotation.FeatureToggle;
import com.example.intergalactic_marketplace.repository.CustomerRepository;
import com.example.intergalactic_marketplace.service.CosmoCatService;
import com.example.intergalactic_marketplace.service.exception.CustomerNotFoundException;
import com.example.intergalactic_marketplace.service.mapper.CustomerMapper;
import jakarta.persistence.PersistenceException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class CosmoCatServiceImpl implements CosmoCatService {

  CustomerRepository repository;
  CustomerMapper mapper;

  @Override
  @FeatureToggle(FeatureToggles.COSMO_CATS)
  @Transactional(readOnly = true)
  public List<String> getCosmoCats() {
    try {
      Customer customer1 =
          mapper.fromCustomerEntity(
              repository
                  .findById(1L)
                  .orElseThrow(
                      () -> {
                        throw new CustomerNotFoundException(1L);
                      }));
      return List.of(customer1.getName());
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
  }
}
