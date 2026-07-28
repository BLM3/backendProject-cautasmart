package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet; // <-- SCHIMBAT
import java.util.Set;           // <-- SCHIMBAT

@Entity
@Table(name = "guides")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(unique = true)
    private String slug;

    private String category;
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String buyingAdvice;

    private String bannerImageUrl;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rank ASC")
    @JsonManagedReference
    private Set<GuideItem> items = new LinkedHashSet<>(); // <-- SCHIMBAT DIN List ÎN Set

    // --- CONSTRUCTORS ---
    public Guide() {}

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getBuyingAdvice() { return buyingAdvice; }
    public void setBuyingAdvice(String buyingAdvice) { this.buyingAdvice = buyingAdvice; }

    public String getBannerImageUrl() { return bannerImageUrl; }
    public void setBannerImageUrl(String bannerImageUrl) { this.bannerImageUrl = bannerImageUrl; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // SCHIMBAT GETTER & SETTER
    public Set<GuideItem> getItems() { return items; }
    public void setItems(Set<GuideItem> items) { this.items = items; }

    private Long viewsCount = 0L; // Câmp nou pentru vizualizări

    public Long getViewsCount() { return viewsCount; }
    public void setViewsCount(Long viewsCount) { this.viewsCount = viewsCount; }
}