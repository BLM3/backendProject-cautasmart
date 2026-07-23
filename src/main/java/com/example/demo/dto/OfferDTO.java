package com.example.demo.dto;

import java.util.List;

public record OfferDTO(
        Long dbId,
        Integer profitshareId,
        String advName,
        String name,
        String description,
        double price,
        double oldPrice,
        Double discount,
        String currency,
        String category,
        String brand,
        boolean inStock,
        double rating,
        String imageUrl,
        String affiliateLink,
        String link,
        List<String> images
) {}
