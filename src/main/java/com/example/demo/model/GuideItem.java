package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "guide_items")
public class GuideItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rank;
    private String badgeText;
    private String productName;
    private String brand;
    private Double estimatedPrice;
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String reviewSummary;

    // SCHIMBATED: List -> Set
    @ElementCollection
    @CollectionTable(name = "guide_item_pros", joinColumns = @JoinColumn(name = "guide_item_id"))
    @Column(name = "pros")
    private Set<String> pros = new HashSet<>();

    // SCHIMBATED: List -> Set
    @ElementCollection
    @CollectionTable(name = "guide_item_cons", joinColumns = @JoinColumn(name = "guide_item_id"))
    @Column(name = "cons")
    private Set<String> cons = new HashSet<>();

    private String affiliateUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    @JsonBackReference
    private Guide guide;

    // --- CONSTRUCTORS ---
    public GuideItem() {}

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public String getBadgeText() { return badgeText; }
    public void setBadgeText(String badgeText) { this.badgeText = badgeText; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Double getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(Double estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getReviewSummary() { return reviewSummary; }
    public void setReviewSummary(String reviewSummary) { this.reviewSummary = reviewSummary; }

    public Set<String> getPros() { return pros; }
    public void setPros(Set<String> pros) { this.pros = pros; }

    public Set<String> getCons() { return cons; }
    public void setCons(Set<String> cons) { this.cons = cons; }

    public String getAffiliateUrl() { return affiliateUrl; }
    public void setAffiliateUrl(String affiliateUrl) { this.affiliateUrl = affiliateUrl; }

    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }
}