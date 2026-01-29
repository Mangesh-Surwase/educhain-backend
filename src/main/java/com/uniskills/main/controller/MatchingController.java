package com.uniskills.main.controller;


import com.uniskills.main.dto.MatchResponse;
import com.uniskills.main.service.MatchingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")

public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/{userId}")
    public List<MatchResponse> getMatches(@PathVariable Long userId) {
        return matchingService.findMatchesForUser(userId);
    }
}

