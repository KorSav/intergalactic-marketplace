package com.example.intergalactic_marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.intergalactic_marketplace.config.FeatureToggleProperties;
import com.example.intergalactic_marketplace.featureToggle.FeatureToggleExtension;
import com.example.intergalactic_marketplace.featureToggle.FeatureToggleService;
import com.example.intergalactic_marketplace.featureToggle.FeatureToggles;
import com.example.intergalactic_marketplace.featureToggle.annotation.DisabledFeatureToggle;
import com.example.intergalactic_marketplace.featureToggle.annotation.EnabledFeatureToggle;
import com.example.intergalactic_marketplace.featureToggle.aspect.FeatureToggleAspect;
import com.example.intergalactic_marketplace.featureToggle.exception.FeatureNotAvailableException;
import com.example.intergalactic_marketplace.repository.CustomerRepository;
import com.example.intergalactic_marketplace.repository.entity.CustomerEntity;
import com.example.intergalactic_marketplace.service.impl.CosmoCatServiceImpl;
import com.example.intergalactic_marketplace.service.impl.CustomerServiceImpl;
import com.example.intergalactic_marketplace.service.mapper.CustomerMapperImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootTest(
    classes = {
      CosmoCatServiceImpl.class,
      CustomerServiceImpl.class,
      FeatureToggleService.class,
      FeatureToggleAspect.class,
      CustomerMapperImpl.class
    })
@DisplayName("Cosmo Cat Service Tests")
@ExtendWith(FeatureToggleExtension.class)
@EnableAspectJAutoProxy
public class CosmoCatServiceTest {
  @MockBean private CustomerRepository customerRepository;
  @Autowired private CosmoCatService cosmoCatService;

  @MockBean private FeatureToggleProperties featureToggleProperties;

  private static List<String> catsNames = List.of("John Doe");

  @BeforeEach
  private void setUp() {
    Map<String, Boolean> map = new HashMap<>();
    for (FeatureToggles toggle : FeatureToggles.values()) {
      map.put(toggle.getFeatureName(), false);
    }
    featureToggleProperties.setToggles(map);
  }

  @Test
  @DisabledFeatureToggle(FeatureToggles.COSMO_CATS)
  void shouldThrowFeatureNotAvailableException() {
    assertThrows(FeatureNotAvailableException.class, cosmoCatService::getCosmoCats);
  }

  @Test
  @EnabledFeatureToggle(FeatureToggles.COSMO_CATS)
  void shouldReturnNames() {
    CustomerEntity testEntity = CustomerEntity.builder().id(1L).name(catsNames.get(0)).build();
    when(customerRepository.findAll()).thenReturn(List.of(testEntity));
    assertEquals(catsNames, cosmoCatService.getCosmoCats());
  }
}
