package com.example.demo.service;

import com.example.demo.model.Guide;
import java.util.List;
import java.util.Optional;

public interface GuideService {
    List<Guide> getGuidesByCategory(String category);
    List<Guide> getGuidesByCategoryAndSort(String category, String sortBy);
    Optional<Guide> getGuideBySlug(String slug);
    List<Guide> searchGuides(String query);
    Guide saveGuide(Guide guide); // util pentru adăugare / administrare
}
