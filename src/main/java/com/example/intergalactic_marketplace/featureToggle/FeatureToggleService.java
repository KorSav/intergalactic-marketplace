package com.example.intergalactic_marketplace.featureToggle;

import com.example.intergalactic_marketplace.config.FeatureToggleProperties;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

  private ConcurrentHashMap<String, Boolean> featureToggles;

  public FeatureToggleService(FeatureToggleProperties featureToggleProperties) {
    featureToggles = new ConcurrentHashMap<>(featureToggleProperties.getToggles());
  }

  public boolean check(String featureName) {
    return featureToggles.getOrDefault(featureName, false);
  }

  public void enable(String featureName) {
    featureToggles.put(featureName, true);
  }

  public void disable(String featureName) {
    featureToggles.put(featureName, false);
  }
}
