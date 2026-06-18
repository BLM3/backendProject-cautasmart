package com.example.demo.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profitshare_id")
    private int profitshareId;

    @Column(length = 500) // Îi dăm o lungime mai mare numelui în caz că e lung
    private String name;

    @Column(columnDefinition = "TEXT") // Folosim TEXT pentru descrieri mari, să nu dea eroare de spațiu
    private String description;

    private double price;
    @Column(name = "old_price")
    private double oldPrice;

    private String currency;
    private String category;

    @Column(name = "in_stock")
    private boolean inStock;

    private double rating;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "affiliate_link", length = 1000)
    private String affiliateLink;

    @Column(name = "images", columnDefinition = "text[]")
    private List<String> images = new ArrayList<>();

    // Constructorul gol cerut obligatoriu de Hibernate
    public int getProfitshareId() { return profitshareId; }
    public void setProfitshareId(int profitshareId) { this.profitshareId = profitshareId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOldPrice() { return oldPrice; }
    public void setOldPrice(double oldPrice) { this.oldPrice = oldPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAffiliateLink() { return affiliateLink; }
    public void setAffiliateLink(String affiliateLink) { this.affiliateLink = affiliateLink; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}