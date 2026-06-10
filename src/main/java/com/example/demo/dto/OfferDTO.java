package com.example.demo.dto;
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
     String affiliateLink
) {}
