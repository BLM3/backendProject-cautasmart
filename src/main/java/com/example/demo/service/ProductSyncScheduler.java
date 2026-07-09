package com.example.demo.service;

import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductSyncScheduler {

    @Autowired
    private ProductRepository productRepository;

    // Rulează la fiecare 6 ore
    @Scheduled(fixedRate = 21600000)
    @Transactional
    public void cleanupExpiredOrOutOfStockProducts() {
        System.out.println("Cron Job: Verificare produse fără stoc...");

        int updatedCount = productRepository.deactivateOutOfStockProducts();

        System.out.println("Cron Job Finalizat: " + updatedCount + " produse au fost dezactivate.");
    }
}