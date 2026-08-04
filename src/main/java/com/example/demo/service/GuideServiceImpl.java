package com.example.demo.service;

import com.example.demo.model.Guide;
import com.example.demo.repository.GuideRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;

    // Mapare între Categoria Mamă (din Frontend) și Subcategoriile reale din Baza de Date
    private static final Map<String, List<String>> CATEGORY_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("tech", List.of("Televizoare OLED & LED", "Laptopuri & PC-uri", "Telefoane Smart", "Tablete & E-readers", "Audio & Căști Bluetooth"));
        CATEGORY_MAP.put("home", List.of("Home", "Aspiratoare Robot", "Climatizare & Purificatoare", "Espressoare & Cafetiere", "Electrocasnice Mari"));
        CATEGORY_MAP.put("gaming", List.of("Gaming", "Monitoare Gaming", "Periferice & Scaune Gaming", "Console & Accesorii"));
        CATEGORY_MAP.put("fitness", List.of("Fitness", "Ceasuri Smart & Brățări", "Benzi de Alergat & Biciclete", "Accesorii Recuperare"));
        CATEGORY_MAP.put("supravegheresecuritate", List.of("Camere Supraveghere", "Sisteme de Alarmă", "Smart Lock & Interfoane"));
        CATEGORY_MAP.put("uneltebricolaj", List.of("Scule Cu Acumulator", "Generatoare & Energie Solară", "Echipamente Atelier"));
        CATEGORY_MAP.put("bebelusicopii", List.of("Aparate & Hrănire Bebeluşi", "Cărucioare & Scaune Auto", "Jucării & Educație", "Monitorizare & Îngrijire"));
        CATEGORY_MAP.put("petshopanimale", List.of("Accesorii & Îngrijire Animale", "Aspiratoare & Păr Animale", "Hrănitoare Inteligente & Dozatoare", "Toalete Inteligente Pisici"));
    }

    public GuideServiceImpl(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    @Override
    public List<Guide> getGuidesByCategory(String category) {
        return getGuidesByCategoryAndSort(category, "recent");
    }

    @CacheEvict(value = "guides_category", allEntries = true)
    @Override
    public List<Guide> getGuidesByCategoryAndSort(String category, String sortBy) {

        String decodedCategory = category;
        if (category != null && !category.trim().isEmpty()) {
            try {
                String formatted = category.replace("+", " ");
                decodedCategory = URLDecoder.decode(formatted, StandardCharsets.UTF_8.name()).trim();

                if (decodedCategory.startsWith("-")) {
                    decodedCategory = decodedCategory.substring(1).trim();
                }
            } catch (Exception e) {
                decodedCategory = category.replace("+", " ").trim();
            }
        }

        System.out.println("💾 [SQL Executat] Categorie Curățată finală: [" + decodedCategory + "]");

        boolean isAllCategories = (decodedCategory == null || decodedCategory.isEmpty() || decodedCategory.equalsIgnoreCase("All"));
        String cleanSort = (sortBy == null) ? "recent" : sortBy.trim().toLowerCase();

        if (isAllCategories) {
            return getSortedAllGuides(cleanSort);
        }

        // Curățăm denumirea pentru a verifica în Map dacă este Categorie Mamă
        String key = decodedCategory.toLowerCase()
                .replace(" ", "")
                .replace("-", "")
                .replace("+", "")
                .replace("&", "")
                .replace("ș", "s").replace("ş", "s")
                .replace("ț", "t").replace("ţ", "t")
                .replace("ă", "a").replace("â", "a");

        // DACA S-A SELECTAT O CATEGORIE MAMĂ:
        if (CATEGORY_MAP.containsKey(key)) {
            List<String> subcategories = CATEGORY_MAP.get(key);
            System.out.println("🔍 S-a detectat Categoria Mamă [" + key + "]. Căutăm subcategoriile: " + subcategories);
            return guideRepository.findByCategoryInIgnoreCase(subcategories);
        }

        // DACA S-A SELECTAT O SUBCATEGORIE INDIVIDUALĂ:
        switch (cleanSort) {
            case "views":
            case "views_desc":
                return guideRepository.findByCategoryIgnoreCaseOrderByViewsCountDesc(decodedCategory);

            case "price_asc":
                return guideRepository.findAllOrderByMinPriceAsc();

            case "price_desc":
                return guideRepository.findAllOrderByMaxPriceDesc();

            case "recent":
            default:
                return guideRepository.findByCategoryIgnoreCaseOrderByUpdatedAtDesc(decodedCategory);
        }
    }

    private List<Guide> getSortedAllGuides(String cleanSort) {
        switch (cleanSort) {
            case "views":
            case "views_desc":
                return guideRepository.findAllByOrderByViewsCountDesc();
            case "price_asc":
                return guideRepository.findAllOrderByMinPriceAsc();
            case "price_desc":
                return guideRepository.findAllOrderByMaxPriceDesc();
            case "recent":
            default:
                return guideRepository.findAllByOrderByUpdatedAtDesc();
        }
    }

    @Override
    @Transactional
    public Optional<Guide> getGuideBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return Optional.empty();
        }

        String cleanSlug = slug.trim().toLowerCase();
        guideRepository.incrementViewsCount(cleanSlug);
        System.out.println("👁️ [Views] S-a incrementat contorul pentru slug: " + cleanSlug);

        return guideRepository.findBySlug(cleanSlug);
    }

    @Override
    public List<Guide> searchGuides(String query) {
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }
        return guideRepository.searchGuides(query.trim());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"guides_category", "guide_slug"}, allEntries = true)
    public Guide saveGuide(Guide guide) {
        if (guide.getUpdatedAt() == null) {
            guide.setUpdatedAt(LocalDateTime.now());
        }
        return guideRepository.save(guide);
    }
}