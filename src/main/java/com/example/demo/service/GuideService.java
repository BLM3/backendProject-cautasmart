package com.example.demo.service;

import com.example.demo.dto.GuideSummaryDTO;
import com.example.demo.model.Guide;
import java.util.List;
import java.util.Optional;

public interface GuideService {
    List<GuideSummaryDTO> getGuidesByCategory(String category);
    List<GuideSummaryDTO> getGuidesByCategoryAndSort(String category, String sortBy);
    Optional<Guide> getGuideBySlug(String slug); // Rămâne cu obiectul complet (pentru pagina dedicată ghidului)
    List<GuideSummaryDTO> searchGuides(String query);
    Guide saveGuide(Guide guide);
}
