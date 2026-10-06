package com.example.demo.dto;

import java.time.LocalDateTime;

public class GuideSummaryDTO {

    private Long id;
    private String title;
    private String slug;
    private String category;
    private String summary;
    private String bannerImageUrl;
    private LocalDateTime updatedAt;
    private Long viewsCount;

    public GuideSummaryDTO(Long id, String title, String slug, String category,
                           String summary, String bannerImageUrl,
                           LocalDateTime updatedAt, Long viewsCount) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.category = category;
        this.summary = summary;
        this.bannerImageUrl = bannerImageUrl;
        this.updatedAt = updatedAt;
        this.viewsCount = viewsCount;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getCategory() { return category; }
    public String getSummary() { return summary; }
    public String getBannerImageUrl() { return bannerImageUrl; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getViewsCount() { return viewsCount; }
}