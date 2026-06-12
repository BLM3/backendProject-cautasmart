package com.example.demo.controller;
import com.example.demo.dto.OfferDTO;
import java.io.IOException;
import com.example.demo.service.ProfitshareService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = "*") //http://localhost:3000 Permite apeluri de la frontend-ul React (portul 3000)

public class OfferController {
    private final ProfitshareService profitshareService;
    public OfferController(ProfitshareService profitshareService) {
        this.profitshareService = profitshareService;
    }
    @GetMapping
    public List<OfferDTO> getAllOffers(@RequestParam(required = false) String keyword,@RequestParam(required = false) String category,
                                       @RequestParam(required = false) String sortBy,
                                       @RequestParam(defaultValue="0") int page,
                                       @RequestParam(defaultValue="12") int size
    )  throws IOException {
        return profitshareService.getOffers(keyword, category, sortBy, page, size);
    }
    @GetMapping("/autocomplete")
    public List<OfferDTO> getAutocompleteSuggestions(@RequestParam String query) {
        if (query == null || query.isBlank() || query.length() < 2) {
            return java.util.Collections.emptyList();
        }

        // Preluăm doar primele 5 rezultate potrivite direct din service
        return profitshareService.getOffers(query, null, null, 0, 5);
    }
//    @GetMapping("/products")
//    public String getProducts() throws IOException {
//        return Files.readString(Path.of("./products.json"));
//    }
}
