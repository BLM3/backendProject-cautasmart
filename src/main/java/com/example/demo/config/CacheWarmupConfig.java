package com.example.demo.config;

import com.example.demo.service.GuideService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CacheWarmupConfig {

    private final GuideService guideService;

    public CacheWarmupConfig(GuideService guideService) {
        this.guideService = guideService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmupCache() {
        try {
            // Pre-încărcăm categoriile principale imediat ce aplicația pornește
            guideService.getGuidesByCategoryAndSort("All", "recent");
            guideService.getGuidesByCategoryAndSort("sanatatefarma", "recent");
            guideService.getGuidesByCategoryAndSort("carti", "recent");
        } catch (Exception ignored) {
        }
    }
}