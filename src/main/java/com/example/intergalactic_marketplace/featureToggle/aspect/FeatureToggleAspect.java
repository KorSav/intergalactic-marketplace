package com.example.intergalactic_marketplace.featureToggle.aspect;

import com.example.intergalactic_marketplace.featureToggle.FeatureToggleService;
import com.example.intergalactic_marketplace.featureToggle.FeatureToggles;
import com.example.intergalactic_marketplace.featureToggle.annotation.FeatureToggle;
import com.example.intergalactic_marketplace.featureToggle.exception.FeatureNotAvailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FeatureToggleAspect {
  private final FeatureToggleService featureToggleService;

  @Before(value = "@annotation(featureToggle)")
  public Object checkFeatureToggleAnnotation(
      ProceedingJoinPoint joinPoint, FeatureToggle featureToggle) throws Throwable {
    FeatureToggles toggle = featureToggle.value();
    if (featureToggleService.check(toggle.getFeatureName())) {
      return joinPoint.proceed();
    }
    log.warn("Feature toggle {} is not enabled!", toggle.getFeatureName());
    throw new FeatureNotAvailableException(toggle.getFeatureName());
  }
}
