package com.example.demo.dto;

import java.util.List;

public record OfferDTO(
     int id,
     String name,
     String description,
     double price,
     double oldPrice,
     String currency,
     String category,
     boolean inStock,
     double rating,
     String imageUrl,
     String affiliateLink,
     List<String> images
) {}
