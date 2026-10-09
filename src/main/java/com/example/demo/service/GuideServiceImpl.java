package com.example.demo.service;

import com.example.demo.dto.GuideSummaryDTO;
import com.example.demo.model.Guide;
import com.example.demo.repository.GuideRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;

    private static final Map<String, List<String>> CATEGORY_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("tech", List.of("Televizoare OLED & LED", "Telefoane Apple", "Tablete & E-readers", "Audio & Căști Bluetooth"));
        CATEGORY_MAP.put("ingrijirepersonala", List.of("Plăci de păr & Perii de îndreptat", "Uscătoare de păr", "Aparate de tuns", "Ondulatoare"));
        CATEGORY_MAP.put("home", List.of("Aspiratoare Verticale", "Purificatoare", "Espressoare", "Electrocasnice Mari"));
        CATEGORY_MAP.put("gaming", List.of("Monitoare Gaming", "Laptopuri & PC-uri", "Periferice & Scaune Gaming", "Console"));
        CATEGORY_MAP.put("fitness", List.of("Ceasuri Smart & Brățări"));
        CATEGORY_MAP.put("supravegheresecuritate", List.of("Camere Supraveghere"));
        CATEGORY_MAP.put("bebelusicopii", List.of("Scaune Auto"));
        CATEGORY_MAP.put("petshopanimale", List.of("Accesorii & Îngrijire Animale", "Aspiratoare & Păr Animale", "Ansamblu de joacă & Hrană", "Toalete Inteligente Pisici"));
    }

    public GuideServiceImpl(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    @Override
    public List<GuideSummaryDTO> getGuidesByCategory(String category) {
        return getGuidesByCategoryAndSort(category, "recent");
    }

    @Override
    @Cacheable(value = "guides_category", key = "(#category != null ? #category : 'all') + '_' + (#sortBy != null ? #sortBy : 'recent')")
    public List<GuideSummaryDTO> getGuidesByCategoryAndSort(String category, String sortBy) {

        String decodedCategory = sanitizeCategory(category);
        boolean isAllCategories = (decodedCategory == null || decodedCategory.isEmpty() || decodedCategory.equalsIgnoreCase("All"));
        String cleanSort = (sortBy == null) ? "recent" : sortBy.trim().toLowerCase();

        if (isAllCategories) {
            return getSortedAllGuides(cleanSort);
        }

        // Normalizare robustă pentru cheia din map (ex: "Îngrijire Personală" -> "ingrijirepersonala")
        String key = normalizeCategoryKey(decodedCategory);

        if (CATEGORY_MAP.containsKey(key)) {
            List<String> subcategories = CATEGORY_MAP.get(key).stream()
                    .map(String::toLowerCase)
                    .toList();
            return getSortedGuidesForCategories(subcategories, cleanSort);
        }

        return getSortedGuidesForSingleCategory(decodedCategory, cleanSort);
    }

    private String normalizeCategoryKey(String input) {
        if (input == null) return "";

        // Convertim la litere mici
        String lower = input.toLowerCase();

        // Eliminăm diacriticele românești prin descompunere Unicode (NFD)
        String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Curățăm caracterele speciale, spațiile și cratimele
        return normalized
                .replace(" ", "")
                .replace("-", "")
                .replace("+", "")
                .replace("&", "");
    }

    private List<GuideSummaryDTO> getSortedGuidesForCategories(List<String> categories, String cleanSort) {
        return switch (cleanSort) {
            case "views", "views_desc" -> guideRepository.findDTOByCategoryInOrderByViewsCountDesc(categories);
            case "price_asc" -> guideRepository.findDTOByCategoryInOrderByMinPriceAsc(categories);
            case "price_desc" -> guideRepository.findDTOByCategoryInOrderByMaxPriceDesc(categories);
            default -> guideRepository.findDTOByCategoryInOrderByUpdatedAtDesc(categories);
        };
    }

    private List<GuideSummaryDTO> getSortedGuidesForSingleCategory(String category, String cleanSort) {
        return switch (cleanSort) {
            case "views", "views_desc" -> guideRepository.findDTOByCategoryIgnoreCaseOrderByViewsCountDesc(category);
            case "price_asc" -> guideRepository.findDTOByCategoryOrderByMinPriceAsc(category);
            case "price_desc" -> guideRepository.findDTOByCategoryOrderByMaxPriceDesc(category);
            default -> guideRepository.findDTOByCategoryIgnoreCaseOrderByUpdatedAtDesc(category);
        };
    }

    private List<GuideSummaryDTO> getSortedAllGuides(String cleanSort) {
        return switch (cleanSort) {
            case "views", "views_desc" -> guideRepository.findAllDTOByOrderByViewsCountDesc();
            case "price_asc" -> guideRepository.findAllDTOOrderByMinPriceAsc();
            case "price_desc" -> guideRepository.findAllDTOOrderByMaxPriceDesc();
            default -> guideRepository.findAllDTOByOrderByUpdatedAtDesc();
        };
    }

    private String sanitizeCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return null;
        }
        try {
            String formatted = category.replace("+", " ");
            String decoded = URLDecoder.decode(formatted, StandardCharsets.UTF_8.name()).trim();
            return decoded.startsWith("-") ? decoded.substring(1).trim() : decoded;
        } catch (Exception e) {
            return category.replace("+", " ").trim();
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
        return guideRepository.findBySlug(cleanSlug);
    }

    @Override
    public List<GuideSummaryDTO> searchGuides(String query) {
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }
        return guideRepository.searchDTOGuides(query.trim());
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