package com.example.intergalactic_marketplace.dto.Customer;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CustomerEntry {
  Long id;
  String name;
  String address;
  String email;
}
