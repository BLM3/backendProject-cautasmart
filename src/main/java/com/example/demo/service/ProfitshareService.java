package com.example.demo.service;

import com.example.demo.dto.OfferDTO;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
@Service
public class ProfitshareService {

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Importă și mapează feed-ul JSON salvat în src/main/resources/feed.json
     */
    @Transactional
    public int importFeedFromJsonFile(String filename) {
        int count = 0;
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            InputStream inputStream = resource.getInputStream();

            JsonNode rootNode = objectMapper.readTree(inputStream);
            List<Product> productsToSave = new ArrayList<>();

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    Product product = mapJsonNodeToProduct(node);
                    productsToSave.add(product);
                }
            } else if (rootNode.isObject()) {
                productsToSave.add(mapJsonNodeToProduct(rootNode));
            }

            // Curățare bază de date și re-salvare
            productRepository.deleteAllInBatch();
            List<Product> saved = productRepository.saveAll(productsToSave);
            count = saved.size();

            System.out.println("✅ Feed-ul a fost importat cu succes! Produse adăugate: " + count);

        } catch (Exception e) {
            System.err.println("❌ Eroare la citirea/importul feed-ului JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Mapează structura JSON direct pe Entitatea `Product`
     */
    private Product mapJsonNodeToProduct(JsonNode node) {
        Product p = new Product();

        p.setAdvName(getTextValue(node, "adv_name"));
        p.setCategory(getTextValue(node, "category"));
        p.setBrand(getTextValue(node, "manufacturer"));

        // Limitează numele la 500 caractere conform DB constraint
        String name = getTextValue(node, "product_name");
        p.setName(name.length() > 500 ? name.substring(0, 497) + "..." : name);

        p.setDescription(getTextValue(node, "product_desc"));

        // Calculare/Parsare Preț & Reducere
        double priceVat = node.has("price_vat") && !node.get("price_vat").isNull()
                ? node.get("price_vat").asDouble(0.0) : 0.0;

        String discountedStr = getTextValue(node, "price_discounted");
        double priceDiscounted = 0.0;

        if (!discountedStr.isBlank()) {
            try {
                priceDiscounted = Double.parseDouble(discountedStr);
            } catch (NumberFormatException ignored) {}
        }

        if (priceDiscounted > 0 && priceDiscounted < priceVat) {
            p.setPrice(priceDiscounted);
            p.setOldPrice(priceVat);
            double discPercent = ((priceVat - priceDiscounted) / priceVat) * 100.0;
            p.setDiscount(Math.round(discPercent * 100.0) / 100.0);
        } else {
            p.setPrice(priceVat);
            p.setOldPrice(priceVat);
            p.setDiscount(0.0);
        }

        p.setCurrency(getTextValue(node, "currency").isEmpty() ? "lei" : getTextValue(node, "currency"));

        // Stoc: String 'in_stock' -> Boolean
        String avail = getTextValue(node, "availability");
        p.setInStock("in_stock".equalsIgnoreCase(avail));

        // Rating Implicat (pentru NOT NULL constraint)
        p.setRating(0.0);

        // Imagini & Link-uri
        String pic = getTextValue(node, "product_pic");
        p.setImageUrl(pic);
        if (!pic.isBlank()) {
            p.setImages(List.of(pic));
        }

        String rawAffLink = getTextValue(node, "product_aff_link");
        p.setAffiliateLink(processAffiliateLink(rawAffLink));

        p.setLink(getTextValue(node, "link"));

        return p;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText().trim();
        }
        return "";
    }

    public String processAffiliateLink(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) return "";
        if (rawUrl.startsWith("//")) {
            return "https:" + rawUrl;
        }
        return rawUrl.trim();
    }

    public List<OfferDTO> getOffers(String keyword, String category, String sortBy, int page, int size) {
        List<Product> allProducts = productRepository.findAll();

        List<OfferDTO> dbOffers = allProducts.stream()
                .map(p -> new OfferDTO(
                        p.getDbId(),
                        p.getProfitshareId(),
                        p.getAdvName(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getOldPrice(),
                        p.getDiscount(),
                        p.getCurrency(),
                        p.getCategory(),
                        p.getBrand(),
                        p.isInStock(),
                        p.getRating(),
                        p.getImageUrl(),
                        p.getAffiliateLink(),
                        p.getLink(),
                        p.getImages()
                ))
                .toList();

        List<OfferDTO> filtered = dbOffers.stream()
                .filter(OfferDTO::inStock)
                .toList();

        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = removeDiacritics(keyword.toLowerCase());
            filtered = filtered.stream()
                    .filter(o -> {
                        String cleanName = o.name() != null ? removeDiacritics(o.name().toLowerCase()) : "";
                        String cleanDescription = o.description() != null ? removeDiacritics(o.description().toLowerCase()) : "";
                        return cleanName.contains(lowerKeyword) || cleanDescription.contains(lowerKeyword);
                    })
                    .toList();
        }

        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("all")) {
            filtered = filtered.stream()
                    .filter(o -> o.category() != null && o.category().equalsIgnoreCase(category))
                    .toList();
        }

        if (sortBy != null && !sortBy.isBlank()) {
            List<OfferDTO> mutableList = new ArrayList<>(filtered);
            switch (sortBy) {
                case "price_asc" -> mutableList.sort(Comparator.comparingDouble(OfferDTO::price));
                case "price_desc" -> mutableList.sort(Comparator.comparingDouble(OfferDTO::price).reversed());
                case "rating_desc" -> mutableList.sort(Comparator.comparingDouble(OfferDTO::rating).reversed());
                case "discount_desc" -> mutableList.sort(Comparator.comparing(OfferDTO::discount, Comparator.nullsLast(Comparator.reverseOrder())));
            }
            filtered = mutableList;
        }

        int start = page * size;
        if (start >= filtered.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + size, filtered.size());

        return filtered.subList(start, end);
    }

    private String removeDiacritics(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("")
                .replace("ș", "s").replace("ț", "t")
                .replace("Ș", "s").replace("Ț", "t");
    }
}
