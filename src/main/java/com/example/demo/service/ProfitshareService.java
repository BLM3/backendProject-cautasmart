package com.example.demo.service;

import com.example.demo.dto.OfferDTO;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
@Service

public class ProfitshareService {

    private List<OfferDTO> offers=new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;
//    @Value("${profitshare.data.path}")
    @Value("${profitshare.data.path}")
    private String dataPath;

    @PostConstruct
    public void init() {
        try (InputStream inputStream = new ClassPathResource(dataPath).getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            OfferDTO[] offersArray = mapper.readValue(inputStream, OfferDTO[].class);
            this.offers = new ArrayList<>(Arrays.asList(offersArray));
            System.out.println("Succes: S-au încărcat " + this.offers.size() + " produse din JSON-ul intern.");

            // Verificăm dacă baza de date este goală
            if (productRepository.count() == 0) {
                System.out.println("Baza de date Neon este goală. Se inițiază salvarea produselor...");

                List<Product> productsToSave = new ArrayList<>();

                // Parcurgem matricea de DTO-uri pas cu pas
                for (OfferDTO dto : offersArray) {
                    Product p = new Product(
                            dto.id(),
                            dto.name(),
                            dto.description(),
                            dto.price(),
                            dto.oldPrice(),
                            dto.currency(),
                            dto.category(),
                            dto.inStock(),
                            dto.rating(),
                            dto.imageUrl(),
                            dto.affiliateLink()
                    );
                    productsToSave.add(p);
                }

                // Salvăm totul dintr-o singură mișcare în cloud
                productRepository.saveAll(productsToSave);
                System.out.println("🚀 Succes! Toate cele " + productsToSave.size() + " produse au fost salvate în Neon!");
            } else {
                System.out.println("Baza de date Neon conține deja produse. Skip seeder.");
            }

        } catch (IOException e) {
            System.err.println("Eroare la procesarea fișierului JSON sau salvarea în DB: " + e.getMessage());
            this.offers = new ArrayList<>();
        }
    }

    public List<OfferDTO> getOffers(String keyword, String category, String sortBy, int page,int size) {
        List<OfferDTO> filtered=offers;

        // Filtrare după keyword (verificăm atât numele cât și descrierea pentru o căutare mai bună)
        if (keyword != null && !keyword.isBlank()) {
            // 1. Curățăm keyword-ul introdus de utilizator
            String lowerKeyword = removeDiacritics(keyword.toLowerCase());

            filtered = offers.stream()
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
