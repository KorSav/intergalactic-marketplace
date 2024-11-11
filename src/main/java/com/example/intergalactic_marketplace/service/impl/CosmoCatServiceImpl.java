package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.featureToggle.FeatureToggles;
import com.example.intergalactic_marketplace.featureToggle.annotation.FeatureToggle;
import com.example.intergalactic_marketplace.service.CosmoCatService;
import com.example.intergalactic_marketplace.service.CustomerService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CosmoCatServiceImpl implements CosmoCatService {

  private CustomerService customerService;

  CosmoCatServiceImpl(CustomerService customerService) {
    this.customerService = customerService;
  }

  @Override
  @FeatureToggle(FeatureToggles.COSMO_CATS)
  public List<String> getCosmoCats() {
    return List.of(
        customerService.getCustomerById(1L).getName(),
        customerService.getCustomerById(2L).getName());
  }
}
