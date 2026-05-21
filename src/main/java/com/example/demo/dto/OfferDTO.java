package com.example.demo.dto;
import lombok.Data;//necesita Lombok
@Data // Generează getters, setters, toString, etc.
public class OfferDTO {
//    private String name;
//    private String description;
//    private String landingPage;
//    private String image;// URL către imaginea ofertei
//    private String programName;// Numele programului de afiliere
    // Poți adăuga mai multe câmpuri conform API-ului Profitshare
    private int id;
    private String name;
    private String description;
    private double price;
    private String currency;
    private String category;
    private boolean inStock;
    private double rating;
    private String imageUrl;
    private String affiliateLink;

    public OfferDTO() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAffiliateLink() {
        return affiliateLink;
    }

    public void setAffiliateLink(String affiliateLink) {
        this.affiliateLink = affiliateLink;
    }
}
