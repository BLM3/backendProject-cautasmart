package com.example.demo.service;

import com.example.demo.model.Guide;
import com.example.demo.repository.GuideRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;

    public GuideServiceImpl(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    @Override
    public List<Guide> getGuidesByCategory(String category) {
        return getGuidesByCategoryAndSort(category, "recent");
    }

    // Noua metodă care suportă atât categoria cât și opțiunea de sortare (recent, views, price_asc, price_desc)
    @Cacheable(value = "guides_category", key = "(#category != null ? #category.toLowerCase().trim() : 'all') + '_' + (#sortBy != null ? #sortBy : 'recent')")
    public List<Guide> getGuidesByCategoryAndSort(String category, String sortBy) {
        System.out.println("💾 [SQL Executat] Se încarcă din DB - Categorie: " + category + " | Sortare: " + sortBy);

        boolean isAllCategories = (category == null || category.trim().isEmpty() || category.trim().equalsIgnoreCase("All"));
        String cleanCategory = isAllCategories ? null : category.trim();
        String cleanSort = (sortBy == null) ? "recent" : sortBy.trim().toLowerCase();

        switch (cleanSort) {
            case "views":
            case "views_desc":
                return isAllCategories
                        ? guideRepository.findAllByOrderByViewsCountDesc()
                        : guideRepository.findByCategoryIgnoreCaseOrderByViewsCountDesc(cleanCategory);

            case "price_asc":
                return guideRepository.findAllOrderByMinPriceAsc();

            case "price_desc":
                return guideRepository.findAllOrderByMaxPriceDesc();

            case "recent":
            default:
                return isAllCategories
                        ? guideRepository.findAllByOrderByUpdatedAtDesc()
                        : guideRepository.findByCategoryIgnoreCaseOrderByUpdatedAtDesc(cleanCategory);
        }
    }

    @Override
    @Transactional // 1. Trecem la tranzacție de scriere pentru a permite UPDATE-ul
    // 2. Scoatem sau comentăm @Cacheable aici pentru a nu bloca incrementarea contorului la fiecare accesare:
    // @Cacheable(value = "guide_slug", key = "#slug != null ? #slug.toLowerCase().trim() : ''")
    public Optional<Guide> getGuideBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return Optional.empty();
        }

        String cleanSlug = slug.trim().toLowerCase();

        // 3. Incrementăm contorul în baza de date
        guideRepository.incrementViewsCount(cleanSlug);
        System.out.println("👁️ [Views] S-a incrementat contorul pentru slug: " + cleanSlug);

        // 4. Preluăm datele actualizate ale ghidului
        return guideRepository.findBySlug(cleanSlug);
    }

    @Override
    public List<Guide> searchGuides(String query) {
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }
        // Căutările le lăsăm fără Cache pentru a returna mereu rezultate dinamice în timp real
        return guideRepository.searchGuides(query.trim());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"guides_category", "guide_slug"}, allEntries = true)
    public Guide saveGuide(Guide guide) {
        if (guide.getUpdatedAt() == null) {
            guide.setUpdatedAt(LocalDateTime.now());
        }
        // Când se salvează/modifică un ghid, curățăm memoria cache automat
        return guideRepository.save(guide);
    }
}