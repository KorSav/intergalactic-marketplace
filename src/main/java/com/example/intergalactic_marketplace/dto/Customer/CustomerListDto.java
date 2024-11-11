package com.example.intergalactic_marketplace.dto.Customer;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CustomerListDto {
  List<CustomerEntry> customers;
}
