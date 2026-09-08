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

    // 1. Păstrăm EntityGraph doar la încărcarea UNUI SINGUR GHID (după slug)
    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g WHERE g.slug = :slug")
    Optional<Guide> findBySlug(@Param("slug") String slug);

    // --- FILTRARE DUPĂ CATEGORIE / SUBCATEGORIE ---
    // Notă: Eliminăm înlocuirile masive din SQL. Normalizarea se face simplificat sau în DB.
    @Query("SELECT g FROM Guide g WHERE LOWER(g.category) LIKE LOWER(CONCAT('%', :category, '%')) ORDER BY g.updatedAt DESC")
    List<Guide> findByCategoryIgnoreCaseOrderByUpdatedAtDesc(@Param("category") String category);

    @Query("SELECT g FROM Guide g ORDER BY g.updatedAt DESC")
    List<Guide> findAllByOrderByUpdatedAtDesc();

    @Query("SELECT g FROM Guide g WHERE g.category IN :categories ORDER BY g.updatedAt DESC")
    List<Guide> findByCategoryInIgnoreCase(@Param("categories") List<String> categories);

    // --- SORTARE DUPĂ VIZUALIZĂRI ---
    @Query("SELECT g FROM Guide g ORDER BY COALESCE(g.viewsCount, 0) DESC, g.updatedAt DESC")
    List<Guide> findAllByOrderByViewsCountDesc();

    @Query("SELECT g FROM Guide g WHERE LOWER(g.category) LIKE LOWER(CONCAT('%', :category, '%')) ORDER BY COALESCE(g.viewsCount, 0) DESC, g.updatedAt DESC")
    List<Guide> findByCategoryIgnoreCaseOrderByViewsCountDesc(@Param("category") String category);

    // --- SORTARE DUPĂ PREȚ ---
    @Query("SELECT g FROM Guide g LEFT JOIN g.items i GROUP BY g.id ORDER BY COALESCE(MIN(i.estimatedPrice), 0) ASC")
    List<Guide> findAllOrderByMinPriceAsc();

    @Query("SELECT g FROM Guide g LEFT JOIN g.items i GROUP BY g.id ORDER BY COALESCE(MAX(i.estimatedPrice), 0) DESC")
    List<Guide> findAllOrderByMaxPriceDesc();

    // --- CĂUTARE GLOBALĂ ---
    @Query("SELECT g FROM Guide g WHERE " +
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