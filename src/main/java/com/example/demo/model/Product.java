package com.example.demo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_id")
    private Long dbId;

    @Column(name = "profitshare_id")
    private Integer profitshareId;

    @Column(name = "adv_name")
    private String advName;

    @Column(length = 500)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(name = "old_price", nullable = false)
    private double oldPrice;

    private Double discount;

    private String currency;
    private String category;
    private String brand;
    private String code;
    private String ean;

    @Column(name = "in_stock", nullable = false)
    private boolean inStock;

    @Column(nullable = false)
    private double rating = 0.0;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "affiliate_link", length = 1000)
    private String affiliateLink;

    @Column(length = 1000)
    private String link;

    @Column(name = "images", columnDefinition = "text[]")
    private List<String> images = new ArrayList<>();

    public Product() {}

    // Getters & Setters
    public Long getDbId() { return dbId; }
    public void setDbId(Long dbId) { this.dbId = dbId; }

    public Integer getProfitshareId() { return profitshareId; }
    public void setProfitshareId(Integer profitshareId) { this.profitshareId = profitshareId; }

    public String getAdvName() { return advName; }
    public void setAdvName(String advName) { this.advName = advName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOldPrice() { return oldPrice; }
    public void setOldPrice(double oldPrice) { this.oldPrice = oldPrice; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getEan() { return ean; }
    public void setEan(String ean) { this.ean = ean; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAffiliateLink() { return affiliateLink; }
    public void setAffiliateLink(String affiliateLink) { this.affiliateLink = affiliateLink; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}