package com.example.intergalactic_marketplace.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Category {
    int id;
    String name;
}
