package com.example.demo.controller;

import com.example.demo.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite apeluri din frontend-ul tău de pe Vercel
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/contact")
    public ResponseEntity<?> sendContactEmail(@RequestBody ContactRequest request) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("contact@cautasmart.ro");
            mailMessage.setTo("contact@cautasmart.ro");
            mailMessage.setSubject("[Cautasmart] Mesaj nou de la " + request.getName());
            mailMessage.setReplyTo(request.getEmail());
            mailMessage.setText(
                    "Nume: " + request.getName() + "\n" +
                            "Email: " + request.getEmail() + "\n\n" +
                            "Mesaj:\n" + request.getMessage()
            );

            mailSender.send(mailMessage);

            return ResponseEntity.ok(Map.of("success", true, "message", "Email trimis cu succes!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Eroare la trimiterea email-ului"));
        }
    }
}