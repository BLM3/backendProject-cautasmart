package com.example.demo.controller;

import com.example.demo.dto.GuideSummaryDTO;
import com.example.demo.model.Guide;
import com.example.demo.service.GuideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/guides")
@CrossOrigin(origins = "*") // Permite apelul din Vercel/Frontend
public class GuideController {

    private final GuideService guideService;

    public GuideController(GuideService guideService) {
        this.guideService = guideService;
    }

    // 1. Lista de ghiduri ușoară (DTO) pentru Homepage sau Pagina de Categorie
//    @GetMapping
//    public ResponseEntity<List<GuideSummaryDTO>> getGuides(
//            @RequestParam(required = false) String category,
//            @RequestParam(required = false, defaultValue = "recent") String sortBy) {
//
//        return ResponseEntity.ok(guideService.getGuidesByCategoryAndSort(category, sortBy));
//    }
    @GetMapping
    public ResponseEntity<List<GuideSummaryDTO>> getGuides(
            @RequestParam(required = false) String category,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sort", required = false) String sortParam) {

        // Dacă frontend-ul a trimis 'sort' în loc de 'sortBy', îl folosim pe acela
        String effectiveSort = (sortBy != null && !sortBy.isBlank()) ? sortBy : sortParam;

        return ResponseEntity.ok(guideService.getGuidesByCategoryAndSort(category, effectiveSort));
    }

    // 2. Un singur ghid complet cu toate produsele/pros/cons (după Slug)
    @GetMapping("/{slug}")
    public ResponseEntity<Guide> getGuideBySlug(@PathVariable String slug) {
        return guideService.getGuideBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Căutare rapidă (DTO) folosită de bara de căutare
    @GetMapping("/search")
    public ResponseEntity<List<GuideSummaryDTO>> searchGuides(@RequestParam String q) {
        return ResponseEntity.ok(guideService.searchGuides(q));
    }
}