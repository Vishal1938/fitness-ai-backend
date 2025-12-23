package com.spring.ollama.controller;

import com.spring.ollama.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/ai")
@CrossOrigin(origins = "http://localhost:3000")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String q ) {
        return aiService.ask(q);
    }
}
