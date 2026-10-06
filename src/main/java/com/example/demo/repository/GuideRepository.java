package com.example.demo.repository;

import com.example.demo.dto.GuideSummaryDTO;
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

    // 1. Încărcare detaliată a UNUI SINGUR GHID (după slug) – Păstrat complet cu items/pros/cons
    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g WHERE g.slug = :slug")
    Optional<Guide> findBySlug(@Param("slug") String slug);

    // --- QUERY-URI OPTIMIZATE PENTRU LISTE (DTO SUMMARY) ---

    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g WHERE LOWER(g.category) LIKE LOWER(CONCAT('%', :category, '%')) ORDER BY g.updatedAt DESC")
    List<GuideSummaryDTO> findDTOByCategoryIgnoreCaseOrderByUpdatedAtDesc(@Param("category") String category);

    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g ORDER BY g.updatedAt DESC")
    List<GuideSummaryDTO> findAllDTOByOrderByUpdatedAtDesc();

    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g WHERE g.category IN :categories ORDER BY g.updatedAt DESC")
    List<GuideSummaryDTO> findDTOByCategoryInIgnoreCase(@Param("categories") List<String> categories);

    // --- SORTARE DUPĂ VIZUALIZĂRI ---
    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g ORDER BY COALESCE(g.viewsCount, 0) DESC, g.updatedAt DESC")
    List<GuideSummaryDTO> findAllDTOByOrderByViewsCountDesc();

    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g WHERE LOWER(g.category) LIKE LOWER(CONCAT('%', :category, '%')) ORDER BY COALESCE(g.viewsCount, 0) DESC, g.updatedAt DESC")
    List<GuideSummaryDTO> findDTOByCategoryIgnoreCaseOrderByViewsCountDesc(@Param("category") String category);

    // --- SORTARE DUPĂ PREȚ ---
    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g LEFT JOIN g.items i WHERE LOWER(g.category) LIKE LOWER(CONCAT('%', :category, '%')) GROUP BY g.id ORDER BY COALESCE(MIN(i.estimatedPrice), 0) ASC")
    List<GuideSummaryDTO> findDTOByCategoryOrderByMinPriceAsc(@Param("category") String category);

    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g LEFT JOIN g.items i WHERE LOWER(g.category) LIKE LOWER(CONCAT('%', :category, '%')) GROUP BY g.id ORDER BY COALESCE(MAX(i.estimatedPrice), 0) DESC")
    List<GuideSummaryDTO> findDTOByCategoryOrderByMaxPriceDesc(@Param("category") String category);

    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g LEFT JOIN g.items i GROUP BY g.id ORDER BY COALESCE(MIN(i.estimatedPrice), 0) ASC")
    List<GuideSummaryDTO> findAllDTOOrderByMinPriceAsc();

    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g LEFT JOIN g.items i GROUP BY g.id ORDER BY COALESCE(MAX(i.estimatedPrice), 0) DESC")
    List<GuideSummaryDTO> findAllDTOOrderByMaxPriceDesc();

    // --- CĂUTARE GLOBALĂ ---
    @Query("SELECT new com.example.demo.dto.GuideSummaryDTO(g.id, g.title, g.slug, g.category, g.summary, g.bannerImageUrl, g.updatedAt, COALESCE(g.viewsCount, 0)) " +
            "FROM Guide g WHERE " +
            "LOWER(g.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.summary) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY g.updatedAt DESC")
    List<GuideSummaryDTO> searchDTOGuides(@Param("query") String query);

    @Modifying
    @Transactional
    @Query("UPDATE Guide g SET g.viewsCount = COALESCE(g.viewsCount, 0) + 1 WHERE g.slug = :slug")
    void incrementViewsCount(@Param("slug") String slug);
}