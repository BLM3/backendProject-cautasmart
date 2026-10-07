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
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;

    private static final Map<String, List<String>> CATEGORY_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("tech", List.of("Televizoare OLED & LED", "Telefoane Apple", "Tablete & E-readers", "Audio & Căști Bluetooth"));
        CATEGORY_MAP.put("ingrijirepersonala", List.of("Plăci de păr & Perii de îndreptat", "Uscătoare de păr", "Aparate de tuns", "Ondulatoare" ));
        CATEGORY_MAP.put("home", List.of( "Aspiratoare Verticale", "Purificatoare", "Espressoare", "Electrocasnice Mari"));
        CATEGORY_MAP.put("gaming", List.of( "Monitoare Gaming", "Laptopuri & PC-uri", "Periferice & Scaune Gaming", "Console"));
        CATEGORY_MAP.put("fitness", List.of("Ceasuri Smart & Brățări"));
        CATEGORY_MAP.put("supravegheresecuritate", List.of("Camere Supraveghere"));
        /*CATEGORY_MAP.put("uneltebricolaj", List.of("Scule Cu Acumulator", "Generatoare & Energie Solară", "Echipamente Atelier"));*/
        CATEGORY_MAP.put("bebelusicopii", List.of("Scaune Auto"));/*"Aparate & Hrănire Bebeluşi", "Jucării & Educație", "Monitorizare & Îngrijire"));*/
        CATEGORY_MAP.put("petshopanimale", List.of("Accesorii & Îngrijire Animale", "Aspiratoare & Păr Animale", "Ansamblu de joacă & Hrană", "Toalete Inteligente Pisici"));
    }

    public GuideServiceImpl(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    @Override
    public List<GuideSummaryDTO> getGuidesByCategory(String category) {
        return getGuidesByCategoryAndSort(category, "recent");
    }

    @Cacheable(value = "guides_category", key = "(#category != null ? #category : 'all') + '_' + (#sortBy != null ? #sortBy : 'recent')")
    @Override
    public List<GuideSummaryDTO> getGuidesByCategoryAndSort(String category, String sortBy) {

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

        boolean isAllCategories = (decodedCategory == null || decodedCategory.isEmpty() || decodedCategory.equalsIgnoreCase("All"));
        String cleanSort = (sortBy == null) ? "recent" : sortBy.trim().toLowerCase();

        if (isAllCategories) {
            return getSortedAllGuides(cleanSort);
        }

        String key = decodedCategory.toLowerCase()
                .replace(" ", "")
                .replace("-", "")
                .replace("+", "")
                .replace("&", "")
                .replace("ș", "s").replace("ş", "s")
                .replace("ț", "t").replace("ţ", "t")
                .replace("ă", "a").replace("â", "a");

        if (CATEGORY_MAP.containsKey(key)) {
            List<String> subcategories = CATEGORY_MAP.get(key);
            return guideRepository.findDTOByCategoryInIgnoreCase(subcategories);
        }

        switch (cleanSort) {
            case "views":
            case "views_desc":
                return guideRepository.findDTOByCategoryIgnoreCaseOrderByViewsCountDesc(decodedCategory);

            case "price_asc":
                return guideRepository.findDTOByCategoryOrderByMinPriceAsc(decodedCategory);

            case "price_desc":
                return guideRepository.findDTOByCategoryOrderByMaxPriceDesc(decodedCategory);

            case "recent":
            default:
                return guideRepository.findDTOByCategoryIgnoreCaseOrderByUpdatedAtDesc(decodedCategory);
        }
    }

    private List<GuideSummaryDTO> getSortedAllGuides(String cleanSort) {
        switch (cleanSort) {
            case "views":
            case "views_desc":
                return guideRepository.findAllDTOByOrderByViewsCountDesc();
            case "price_asc":
                return guideRepository.findAllDTOOrderByMinPriceAsc();
            case "price_desc":
                return guideRepository.findAllDTOOrderByMaxPriceDesc();
            case "recent":
            default:
                return guideRepository.findAllDTOByOrderByUpdatedAtDesc();
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