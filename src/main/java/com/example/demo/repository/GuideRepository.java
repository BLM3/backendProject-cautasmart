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

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g WHERE LOWER(g.category) = LOWER(:category) ORDER BY g.updatedAt DESC")
    List<Guide> findByCategoryIgnoreCaseOrderByUpdatedAtDesc(@Param("category") String category);

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g ORDER BY g.updatedAt DESC")
    List<Guide> findAllByOrderByUpdatedAtDesc();

    // --- SORTARE DUPĂ CELE MAI VIZUALIZATE (viewsCount DESC) ---

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g ORDER BY COALESCE(g.viewsCount, 0) DESC, g.updatedAt DESC")
    List<Guide> findAllByOrderByViewsCountDesc();

    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g WHERE LOWER(g.category) = LOWER(:category) ORDER BY COALESCE(g.viewsCount, 0) DESC, g.updatedAt DESC")
    List<Guide> findByCategoryIgnoreCaseOrderByViewsCountDesc(@Param("category") String category);


    // --- SORTARE DUPĂ PREȚ (Dacă le sortezi din Backend) ---

    // Preț: Mic -> Mare (bazat pe cel mai ieftin produs din ghid)
    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g LEFT JOIN g.items i GROUP BY g ORDER BY MIN(i.estimatedPrice) ASC")
    List<Guide> findAllOrderByMinPriceAsc();

    // Preț: Mare -> Mic (bazat pe cel mai scump produs din ghid)
    @EntityGraph(attributePaths = {"items", "items.pros", "items.cons"})
    @Query("SELECT DISTINCT g FROM Guide g LEFT JOIN g.items i GROUP BY g ORDER BY MAX(i.estimatedPrice) DESC")
    List<Guide> findAllOrderByMaxPriceDesc();

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