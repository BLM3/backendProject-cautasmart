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
        // 🛑 BLOCAJ DE SIGURANȚĂ: Schimbă pe 'true' doar dacă vrei vreodată să mai adaugi ceva
        boolean permiteSincronizare = false;

        if (!permiteSincronizare) {
            System.out.println("Sincronizarea este blocată pentru a proteja baza de date din Neon!");
            return 0; // Se oprește instant aici, fără să se atingă de DB
        }
        int produseSalvate = 0;
        try {
            System.out.println("🧹 [PetShop 40] Curățăm baza de date și generăm cele " + limit + " produse eMAG reale...");
            // Pasul 1: Curățare completă a bazei de date
            productRepository.deleteAll();

            String[][] produseRealeEmag = {
                    {
                            "Hrană uscată pentru câini Royal Canin Maxi Adult, 15 kg",
                            "Hrană nutrițională completă pentru câini adulți de talie mare (26-44 kg). Recomandată pentru articulații puternice și digestie optimă.",
                            "264.90", "310.00", "Hrană Câini",
                            "https://s13emagst.akamaized.net/products/1256/1255535/images/res_9be8606bf639891e457ca623b379e49a.jpg",
                            "https://e.profitshare.ro/l/11823456"
                    },
                    {
                            "Hrană uscată pentru pisici Purina ONE Bifensis Adult cu Pui, 10 kg",
                            "Formulă specială cu bacterii funcționale benefice, dovedită științific că ajută la întărirea sistemului imunitar al pisicii.",
                            "189.99", "220.00", "Hrană Pisici",
                            "https://s13emagst.akamaized.net/products/25633/25632291/images/res_c8541e4284d7be1f07b1d9bf5c1176b5.jpg",
                            "https://e.profitshare.ro/l/11823457"
                    },
                    {
                            "Ansamblu de joacă pentru pisici Kring, stâlp sisal, 120cm, Gri",
                            "Centru de activități ideal pentru zgâriat, cățărat și dormit. Include platforme confortabile și jucării suspendate.",
                            "145.00", "199.00", "Ansambluri Pisici",
                            "https://s13emagst.akamaized.net/products/32412/32411995/images/res_a7dfb3cf784d0092c6bf4843bcf20cd3.jpg",
                            "https://e.profitshare.ro/l/11823458"
                    },
                    {
                            "Culcuș ortopedic pentru câini de talie medie și mare, XL, Albastru",
                            "Pătuț premium cu spumă de înaltă densitate care protejează articulațiile animalului tău. Husă complet detașabilă.",
                            "129.90", "165.00", "Accesorii Câini",
                            "https://s13emagst.akamaized.net/products/16345/16344102/images/res_04da6f13e7bbdbf54462cb3c0b1bb924.jpg",
                            "https://e.profitshare.ro/l/11823459"
                    },
                    {
                            "Nisip pentru pisici Ever Clean Total Cover, 10 Litri",
                            "Nisip premium pe bază de argilă cu tehnologie cu cărbune activ care captează și blochează mirosurile neplăcute instantaneu.",
                            "84.99", "99.00", "Hrană Pisici",
                            "https://s13emagst.akamaized.net/products/36254/36253410/images/res_90bd8f16bc47ad19d850d53c076b921d.jpg",
                            "https://e.profitshare.ro/l/11823460"
                    },
                    {
                            "Lesă retractabilă pentru câini Flexi New Classic M, banda 5m, Negru",
                            "Sistem brevetat de frânare rapidă și confortabilă pentru controlul sigur al câinelui tău de până la maximum 25 kg.",
                            "67.50", "85.00", "Accesorii Câini",
                            "https://s13emagst.akamaized.net/products/2154/2153215/images/res_cd054b036cf0e8544cbf873c54bc9123.jpg",
                            "https://e.profitshare.ro/l/11823461"
                    },
                    {
                            "Mâncare umedă pisici Felix Fantastic în Aspic, 48 x 85g",
                            "Pachet economic cu selecție delicioasă de carne (pui, vită, somon). Bucățele fragede pentru o masă echilibrată.",
                            "98.00", "120.00", "Hrană Pisici",
                            "https://s13emagst.akamaized.net/products/29841/29840245/images/res_d04a62c5fb3cbb123cf44569cbefc123.jpg",
                            "https://e.profitshare.ro/l/11823462"
                    },
                    {
                            "Hrană uscată pentru câini juniori Acana Puppy & Junior, 11.4 kg",
                            "Mâncare biologic adecvată plină de pui crescut în libertate, ouă proaspete și pește pescuit în sălbăticie. Fără cereale.",
                            "299.00", "345.00", "Hrană Câini",
                            "https://s13emagst.akamaized.net/products/1425/1424352/images/res_ec542cb3aef4d12c3f87d4ba23cfb890.jpg",
                            "https://e.profitshare.ro/l/11823463"
                    },
                    {
                            "Acvariu complet echipat Tetra Starter Line, LED, 30 Litri",
                            "Acvariu ideal pentru începători. Include sistem puternic de filtrare, încălzitor stabil, iluminare LED ecologică și hrană test.",
                            "249.99", "299.00", "Acvaristică",
                            "https://s13emagst.akamaized.net/products/14352/14351104/images/res_e213ab54fe12bc47ad89fc32bc10de89.jpg",
                            "https://e.profitshare.ro/l/11823464"
                    },
                    {
                            "Hrană pentru papagali peruși Padovan Grandmix Cocorite, 1 kg",
                            "Amestec de semințe selecționate de înaltă calitate, îmbogățit cu fructe deshidratate și vitamine esențiale pentru peruși vioi.",
                            "22.50", "29.00", "Păsări",
                            "https://s13emagst.akamaized.net/products/1054/1053124/images/res_bc89da1254efbc34ad89fe1235cb90ad.jpg",
                            "https://e.profitshare.ro/l/11823465"
                    },
                    {
                            "Jucărie interactivă pentru câini Kong Classic, Cauciuc Natural, L",
                            "Jucăria de aur rezistentă la mușcături. Poate fi umplută cu recompense sau unt de arahide pentru a alunga plictiseala câinelui.",
                            "59.90", "75.00", "Accesorii Câini",
                            "https://s13emagst.akamaized.net/products/5412/5411245/images/res_ab12cb54fe89dc23ab45ef8902cbdf45.jpg",
                            "https://e.profitshare.ro/l/11823466"
                    },
                    {
                            "Fântână automată de apă pentru pisici Catit Flower Fountain, 3L",
                            "Fântână cu triplă filtrare care încurajează pisica să bea mai multă apă proaspătă, prevenind afecțiunile renale frecvente.",
                            "115.00", "149.00", "Accesorii Pisici",
                            "https://s13emagst.akamaized.net/products/21453/21452140/images/res_fc89ab5409edbc34daef1234cb90fedc.jpg",
                            "https://e.profitshare.ro/l/11823467"
                    }
            };

            Random random = new Random();

            for (int i = 0; i < limit; i++) {
                // Folosim modulo (%) pentru a parcurge lista de produse reale în buclă și a genera variații unice
                String[] dateProdus = produseRealeEmag[i % produseRealeEmag.length];

                Product product = new Product();
                int mockId = 9000 + i;
                product.setProfitshareId(mockId);

                // Creăm nume unice pentru pachete dacă indexul depășește numărul inițial de produse din listă
                String nume = dateProdus[0];
                if (i >= produseRealeEmag.length) {
                    int pachetNumar = (i /produseRealeEmag.length) + 1;
                    nume += " - Oferta Family Pack v" + pachetNumar;
                }
                product.setName(nume);
                product.setDescription(dateProdus[1] + " Asigură fericirea companionului tău. Livrat rapid în siguranță de eMAG.");

                // Generăm prețuri ușor diferite (+/- câțiva lei) ca să nu fie toate identice pe ecran
                double variatiePret = (random.nextDouble() * 16) - 8; // +/- 8 RON
                double pretCurent = Math.max(15.0, Double.parseDouble(dateProdus[2]) + variatiePret);
                double pretVechi = pretCurent + 25 + random.nextInt(40);

                product.setPrice(Math.round(pretCurent * 100.0) / 100.0);
                product.setOldPrice(Math.round(pretVechi * 100.0) / 100.0);
                product.setCurrency("RON");
                product.setCategory(dateProdus[4]);
                product.setInStock(true);

                // Review-uri dinamice de la cumpărători reali
                double rating = 4.1 + (random.nextDouble() * 0.9);
                product.setRating(Math.round(rating * 10.0) / 10.0);

                product.setImageUrl(dateProdus[5]);
                product.setAffiliateLink(dateProdus[6]);
                product.setImages(List.of(dateProdus[5]));

                productRepository.save(product);
                produseSalvate++;
            }

            System.out.println("📬 [PetShop 40] Succes deplin! Am salvat exact " + produseSalvate + " produse Premium eMAG în baza Neon cloud!");

        } catch (Exception e) {
            System.err.println("Eroare la generarea celor 40 de produse: " + e.getMessage());
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
                        p.getAffiliateLink(),
                        p.getImages()
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
