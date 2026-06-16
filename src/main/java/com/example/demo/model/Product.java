package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId; // ID-ul intern al bazei de date (auto-increment)

    private int profitshareId; // ID-ul original din JSON/Profitshare (cel din record)

    @Column(length = 500) // Îi dăm o lungime mai mare numelui în caz că e lung
    private String name;

    @Column(columnDefinition = "TEXT") // Folosim TEXT pentru descrieri mari, să nu dea eroare de spațiu
    private String description;

    private double price;
    private double oldPrice;
    private String currency;
    private String category;
    private boolean inStock;
    private double rating;

    @Column(length = 1000) // URL-urile de imagini tind să fie lungi
    private String imageUrl;

    @Column(length = 1000) // Link-urile de afiliere pot fi foarte lungi
    private String affiliateLink;

    // Constructorul gol cerut obligatoriu de Hibernate
    public Product() {}

    // Constructor de ajutor pentru a converti ușor din DTO în Entitate
    public Product(int profitshareId, String name, String description, double price, double oldPrice,
                   String currency, String category, boolean inStock, double rating, String imageUrl, String affiliateLink) {
        this.profitshareId = profitshareId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.oldPrice = oldPrice;
        this.currency = currency;
        this.category = category;
        this.inStock = inStock;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.affiliateLink = affiliateLink;
    }

    // Getters și Setters (Sunt necesari pentru ca Spring Data JPA să citească datele)
    public Long getDbId() { return dbId; }
    public void setDbId(Long dbId) { this.dbId = dbId; }

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
}