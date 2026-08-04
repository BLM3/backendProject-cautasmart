package com.example.demo.repository;

import com.example.demo.model.Guide;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g WHERE g.slug = :slug")
    Optional<Guide> findBySlug(@Param("slug") String slug);

    // --- FILTRARE DUPĂ CATEGORIE / SUBCATEGORIE ---
    // Înlocuim diacriticele și caracterele speciale cu un string curat
    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g WHERE " +
            "LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(g.category, ' ', ''), '-', ''), '+', ''), '&', ''), 'ș', 's'), 'ş', 's'), 'ț', 't'), 'ţ', 't'), 'ă', 'a'), 'â', 'a')) LIKE " +
            "LOWER(CONCAT('%', REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(:category, ' ', ''), '-', ''), '+', ''), '&', ''), 'ș', 's'), 'ş', 's'), 'ț', 't'), 'ţ', 't'), 'ă', 'a'), 'â', 'a'), '%')) " +
            "OR " +
            "LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(:category, ' ', ''), '-', ''), '+', ''), '&', ''), 'ș', 's'), 'ş', 's'), 'ț', 't'), 'ţ', 't'), 'ă', 'a'), 'â', 'a')) LIKE " +
            "LOWER(CONCAT('%', REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(g.category, ' ', ''), '-', ''), '+', ''), '&', ''), 'ș', 's'), 'ş', 's'), 'ț', 't'), 'ţ', 't'), 'ă', 'a'), 'â', 'a'), '%')) " +
            "ORDER BY g.updatedAt DESC")
    List<Guide> findByCategoryIgnoreCaseOrderByUpdatedAtDesc(@Param("category") String category);

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g ORDER BY g.updatedAt DESC")
    List<Guide> findAllByOrderByUpdatedAtDesc();


    // Căutare pentru lista de subcategorii (folosită la selectarea unei categorii mame)
    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g WHERE g.category IN :categories ORDER BY g.updatedAt DESC")
    List<Guide> findByCategoryInIgnoreCase(@Param("categories") List<String> categories);
    // --- SORTARE DUPĂ VIZUALIZĂRI (viewsCount DESC) ---

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g ORDER BY COALESCE(g.viewsCount, 0) DESC, g.updatedAt DESC")
    List<Guide> findAllByOrderByViewsCountDesc();

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g WHERE " +
            "LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(g.category, ' ', ''), '-', ''), '+', ''), '&', ''), 'ș', 's'), 'ş', 's'), 'ț', 't'), 'ţ', 't'), 'ă', 'a'), 'â', 'a')) LIKE " +
            "LOWER(CONCAT('%', REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(:category, ' ', ''), '-', ''), '+', ''), '&', ''), 'ș', 's'), 'ş', 's'), 'ț', 't'), 'ţ', 't'), 'ă', 'a'), 'â', 'a'), '%')) " +
            "ORDER BY COALESCE(g.viewsCount, 0) DESC, g.updatedAt DESC")
    List<Guide> findByCategoryIgnoreCaseOrderByViewsCountDesc(@Param("category") String category);

    // --- SORTARE DUPĂ PREȚ ---

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT g FROM Guide g LEFT JOIN g.items i GROUP BY g.id ORDER BY COALESCE(MIN(i.estimatedPrice), 0) ASC")
    List<Guide> findAllOrderByMinPriceAsc();

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT g FROM Guide g LEFT JOIN g.items i GROUP BY g.id ORDER BY COALESCE(MAX(i.estimatedPrice), 0) DESC")
    List<Guide> findAllOrderByMaxPriceDesc();

    // --- CĂUTARE GLOBALĂ ---

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g WHERE " +
            "LOWER(g.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.summary) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY g.updatedAt DESC")
    List<Guide> searchGuides(@Param("query") String query);

    @Modifying
    @Transactional
    @Query("UPDATE Guide g SET g.viewsCount = COALESCE(g.viewsCount, 0) + 1 WHERE g.slug = :slug")
    void incrementViewsCount(@Param("slug") String slug);
}