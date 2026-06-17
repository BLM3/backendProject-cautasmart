package com.example.demo.service;

import com.example.demo.dto.OfferDTO;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Instant;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
@Service

public class ProfitshareService {

    @Autowired
    private ProductRepository productRepository;
//    @Value("${profitshare.data.path}")
    @Value("${profitshare.api.user}")
    private String apiUser;

    @Value("${profitshare.api.key}")
    private String apiKey;
    //@Value("${profitshare.data.path}")

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        System.out.println("Profitshare Service inițializat cu API User: " + apiUser);
//
    }
    /**
     * Generează header-ul de autentificare securizat X-Profitshare-Auth cerut de rețea.
     */
    private String genereazaHeaderAutentificare(String metodaHttp, String urlPath, String timestamp) {
        try {
            String textDeSemnat = metodaHttp.toUpperCase() + urlPath + "/" + timestamp;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] hashBytes = sha256Hmac.doFinal(textDeSemnat.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            System.err.println("Eroare la generarea semnăturii API: " + e.getMessage());
            return "";
        }
    }
    /**
     * Metodă model pentru a trage produsele live prin API-ul lor în viitor
     */
    public int sincronizeazaProduseDinProfitshare(int limit) {
        int produseSalvate = 0;
        try {
            System.out.println("🚀 [Simulare API] Se generează " + limit + " produse de la eMAG din categoria: telefoane");

            // Liste de date reale pentru a simula un feed eMAG autentic
            String[] branduri = {"Apple iPhone 15", "Samsung Galaxy S24", "Apple iPhone 14", "Samsung Galaxy A55", "Google Pixel 8"};
            String[] stocari = {"128GB", "256GB", "512GB"};
            String[] culori = {"Black", "White", "Blue", "Titanium Gray"};

            String[] imaginiEmag = {
                    "https://s13emagst.akamaized.net/products/60907/60906353/images/res_5bd11df779ff14bd100c5bc20c5b736b.jpg", // iPhone 15
                    "https://s13emagst.akamaized.net/products/64511/64510452/images/res_1f114c000ef854ca63ba65da98263da3.jpg", // S24
                    "https://s13emagst.akamaized.net/products/48737/48736342/images/res_6cd75f63d0859a850787e9f3b5bc20c5.jpg"  // iPhone 14
            };

            Random random = new Random();

            for (int i = 1; i <= limit; i++) {
                Product product = new Product();

                // Generăm un ID unic Profitshare (ex: 5001, 5002...)
                int mockProfitshareId = 5000 + i;
                product.setProfitshareId(mockProfitshareId);

                // Construim un nume de produs realist
                String numeProdus = branduri[random.nextInt(branduri.length)] + ", " +
                        stocari[random.nextInt(stocari.length)] + ", " +
                        culori[random.nextInt(culori.length)];
                product.setName(numeProdus);

                product.setDescription("Ecran de ultimă generație, autonomie sporită a bateriei și sistem de camere foto profesionale. Produs vândut și livrat de eMAG.");

                // Generăm prețuri realiste
                double pretCurent = 2000 + (random.nextDouble() * 4000); // Între 2000 și 6000 RON
                double pretVechi = pretCurent + 300 + random.nextInt(500);

                // Rotunjim la 2 zecimale
                product.setPrice(Math.round(pretCurent * 100.0) / 100.0);
                product.setOldPrice(Math.round(pretVechi * 100.0) / 100.0);

                product.setCurrency("RON");
                product.setCategory("Telefoane");
                product.setInStock(true);

                // Rating aleatoriu între 4.0 și 5.0
                double rating = 4.0 + (random.nextDouble() * 1.0);
                product.setRating(Math.round(rating * 10.0) / 10.0);

                // Alocăm o imagine eMAG din listă
                product.setImageUrl(imaginiEmag[random.nextInt(imaginiEmag.length)]);

                // Un link de afiliere standard fictiv bazat pe ID
                product.setAffiliateLink("https://pft.ro/c/mock-affiliate-" + mockProfitshareId);

                // Salvăm direct în baza ta de date din Neon cloud!
                productRepository.save(product);
                produseSalvate++;
            }

            System.out.println("📬 [Simulare] S-au salvat cu succes " + produseSalvate + " telefoane eMAG în Neon!");

        } catch (Exception e) {
            System.err.println("Eroare la generarea simulării: " + e.getMessage());
        }
        return produseSalvate;
    }

    public List<OfferDTO> getOffers(String keyword, String category, String sortBy, int page,int size) {
        List<Product> allProducts = productRepository.findAll();

        // Convertim obiectele Product din baza de date în OfferDTO-uri pentru a păstra logica ta intactă
        List<OfferDTO> dbOffers = allProducts.stream()
                .map(p -> new OfferDTO(
                        p.getProfitshareId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getOldPrice(),
                        p.getCurrency(),
                        p.getCategory(),
                        p.isInStock(),
                        p.getRating(),
                        p.getImageUrl(),
                        p.getAffiliateLink()
                ))
                .toList();

        List<OfferDTO> filtered = dbOffers;
        // Filtrare după keyword (verificăm atât numele cât și descrierea pentru o căutare mai bună)
        if (keyword != null && !keyword.isBlank()) {
            // 1. Curățăm keyword-ul introdus de utilizator
            String lowerKeyword = removeDiacritics(keyword.toLowerCase());

            filtered = dbOffers.stream()
                    .filter(o -> {
                        // 2. Curățăm numele produsului curent (dacă nu e null)
                        String cleanName = o.name() != null ? removeDiacritics(o.name().toLowerCase()) : "";

                        // 3. Curățăm descrierea produsului curent (dacă nu e null)
                        String cleanDescription = o.description() != null ? removeDiacritics(o.description().toLowerCase()) : "";

                        // 4. Facem comparația pe textele „curățate” de diacritice
                        return cleanName.contains(lowerKeyword) || cleanDescription.contains(lowerKeyword);
                    })
                    .toList();
        }
        //filtrare dupa categorie
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("all")) {
            filtered = filtered.stream()
                    .filter(o -> o.category() != null && o.category().equalsIgnoreCase(category))
                    .toList();
        }

        // sortare dinamica
        if (sortBy != null && !sortBy.isBlank()) {
            // Creăm o listă mutabilă din stream pentru a o putea sorta
            List<OfferDTO> mutableList = new ArrayList<>(filtered);

            switch (sortBy) {
                case "price_asc":
                    mutableList.sort(Comparator.comparingDouble(OfferDTO::price));
                    break;
                case "price_desc":
                    mutableList.sort(Comparator.comparingDouble(OfferDTO::price).reversed());
                    break;
                case "rating_desc":
                    mutableList.sort(Comparator.comparingDouble(OfferDTO::rating).reversed());
                    break;
                default:
                    // Rămâne sortarea implicită din fișier
                    break;
            }
            filtered = mutableList;
        }

        // calcul paginare
        int start = page * size;

        if (start >= filtered.size()) {
            return Collections.emptyList();
        }

        int end = Math.min(start + size, filtered.size());

        return filtered.subList(start, end);
    }
    /**
     * Metodă Helper care elimină diacriticele dintr-un text.
     * Transformă "cămașă" în "camasa", "încălțăminte" în "incaltaminte", etc.
     */
    private String removeDiacritics(String text) {
        if (text == null) {
            return "";
        }
        // Pasul 1: Normalizăm textul în forma descompusă (NFD). De exemplu, 'ă' devine 'a' + un accent separat.
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);

        // Pasul 2: Folosim un Regex pentru a șterge toate semnele diacritice (caracterele non-spacing mark)
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(normalized).replaceAll("");

        // Pasul 3: Corecție specială pentru ș-ul și ț-ul românesc vechi/nou (care uneori nu se curăță prin NFD standard)
        return result.replace("ș", "s")
                .replace("ț", "t")
                .replace("Ș", "s")
                .replace("Ț", "t");
    }
}
