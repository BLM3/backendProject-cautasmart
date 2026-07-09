package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Filtrare pe categorie (doar cele active/în stoc)
    List<Product> findByCategoryIgnoreCaseAndInStockTrue(String category);

    @Modifying
    @Query("UPDATE Product p SET p.inStock = false WHERE p.inStock = false")
    int deactivateOutOfStockProducts();
}
