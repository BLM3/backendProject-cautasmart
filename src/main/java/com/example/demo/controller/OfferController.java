package com.example.demo.controller;
import com.example.demo.dto.OfferDTO;
import java.io.IOException;
import com.example.demo.service.ProfitshareService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = "*")
public class OfferController {

    private final ProfitshareService profitshareService;

    public OfferController(ProfitshareService profitshareService) {
        this.profitshareService = profitshareService;
    }

    @GetMapping
    public List<OfferDTO> getAllOffers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) throws IOException {
        return profitshareService.getOffers(keyword, category, sortBy, page, size);
    }

    @GetMapping("/autocomplete")
    public List<OfferDTO> getAutocompleteSuggestions(@RequestParam String query) {
        if (query == null || query.isBlank() || query.length() < 2) {
            return Collections.emptyList();
        }
        // Preluăm doar primele 5 rezultate potrivite direct din service
        return profitshareService.getOffers(query, null, null, 0, 5);
    }

    /**
     * Endpoint pentru importul feed-ului JSON local direct în Neon DB.
     * Apel: POST http://localhost:8080/api/offers/import
     */
    @PostMapping("/import")
    public ResponseEntity<String> importFeed() {
        int totalSalvate = profitshareService.importFeedFromJsonFile("feed.json");

        if (totalSalvate > 0) {
            return ResponseEntity.ok("✅ Succes! S-au importat și salvat " + totalSalvate + " produse în baza Neon.");
        } else {
            return ResponseEntity.badRequest().body("❌ Nu s-au putut importa produsele. Verifică dacă fișierul 'feed.json' există în folderul src/main/resources/.");
        }
    }
}
