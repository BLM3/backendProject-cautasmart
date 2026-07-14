package com.example.demo.controller;

import com.example.demo.dto.ContactRequest;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite apeluri din frontend-ul tău de pe Vercel
public class ContactController {

    @PostMapping("/contact")
    public ResponseEntity<?> sendContactEmail(@RequestBody ContactRequest request) {
        try {
            // Preia cheia API direct din variabila de mediu setata pe Render
            String apiKey = System.getenv("RESEND_API_KEY");
            Resend resend = new Resend(apiKey);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("contact@cautasmart.ro")
                    .to("contact@cautasmart.ro")
                    .replyTo(request.getEmail())
                    .subject("[Cautasmart] Mesaj nou de la " + request.getName())
                    .text("Nume: " + request.getName() + "\n" +
                            "Email: " + request.getEmail() + "\n\n" +
                            "Mesaj:\n" + request.getMessage())
                    .build();

            CreateEmailResponse data = resend.emails().send(params);

            return ResponseEntity.ok(Map.of("success", true, "id", data.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}