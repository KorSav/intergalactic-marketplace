package com.example.intergalactic_marketplace.featureToggle;

import com.example.intergalactic_marketplace.featureToggle.annotation.DisabledFeatureToggle;
import com.example.intergalactic_marketplace.featureToggle.annotation.EnabledFeatureToggle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
public class FeatureToggleExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    log.error("ISIDE AFTER EACH METHOD: {}", 1234);
    context
        .getTestMethod()
        .ifPresent(
            method -> {
              String featureName = null;
              if (method.isAnnotationPresent(EnabledFeatureToggle.class)) {
                EnabledFeatureToggle enabledFeatureToggle =
                    method.getAnnotation(EnabledFeatureToggle.class);
                featureName = enabledFeatureToggle.value().getFeatureName();
              } else if (method.isAnnotationPresent(DisabledFeatureToggle.class)) {
                DisabledFeatureToggle disabledFeatureToggle =
                    method.getAnnotation(DisabledFeatureToggle.class);
                featureName = disabledFeatureToggle.value().getFeatureName();
              }
              if (featureName != null) {
                FeatureToggleService featureToggleService = getFeatureToggleService(context);
                if (getApplicationPropertyAsBoolean(context, featureName)) {
                  featureToggleService.enable(featureName);
                } else {
                  featureToggleService.disable(featureName);
                }
              }
            });
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    log.error("ISIDE BEFORE EACH METHOD");
    context
        .getTestMethod()
        .ifPresent(
            method -> {
              FeatureToggleService featureToggleService = getFeatureToggleService(context);
              if (method.isAnnotationPresent(EnabledFeatureToggle.class)) {
                EnabledFeatureToggle enabledFeatureToggle =
                    method.getAnnotation(EnabledFeatureToggle.class);
                featureToggleService.enable(enabledFeatureToggle.value().getFeatureName());
              } else if (method.isAnnotationPresent(DisabledFeatureToggle.class)) {
                DisabledFeatureToggle disabledFeatureToggle =
                    method.getAnnotation(DisabledFeatureToggle.class);
                featureToggleService.disable(disabledFeatureToggle.value().getFeatureName());
              }
              log.error(
                  "FEATURE TOGGLE COSMO_CATS: {}",
                  featureToggleService.check(FeatureToggles.COSMO_CATS.getFeatureName()));
              log.error(
                  "FEATURE TOGGLE KITTY_PRODUCTS: {}",
                  featureToggleService.check(FeatureToggles.KITTY_PRODUCTS.getFeatureName()));
            });
  }

  private FeatureToggleService getFeatureToggleService(ExtensionContext context) {
    return SpringExtension.getApplicationContext(context).getBean(FeatureToggleService.class);
  }

  private boolean getApplicationPropertyAsBoolean(ExtensionContext context, String featureName) {
    Environment environment = SpringExtension.getApplicationContext(context).getEnvironment();
    return environment.getProperty(
        "application.feature.toggles." + featureName, Boolean.class, Boolean.FALSE);
  }
}
