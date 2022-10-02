package org.example.scraper.controller;

import lombok.RequiredArgsConstructor;
import org.example.scraper.model.ScrapingResults;
import org.example.scraper.service.ScrapingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {

    private final ScrapingService scrapingService;
    private static final String EXAMPLE_URL = "https://www.idealista.com/ca/alquiler-viviendas/barcelona/eixample/el-fort-pienc/";

    @GetMapping("/scrape")
    public ScrapingResults scrape() {
        return scrapingService.scrape(EXAMPLE_URL);
    }
}
