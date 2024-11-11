package com.example.intergalactic_marketplace.featureToggle.exception;

public class FeatureNotAvailableException extends RuntimeException {
  public FeatureNotAvailableException(String featureToggleName) {
    super(String.format("Feature toggle %s is not enabled", featureToggleName));
  }
}
