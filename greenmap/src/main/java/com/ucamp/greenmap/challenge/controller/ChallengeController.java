package com.ucamp.greenmap.challenge.controller;

import com.ucamp.greenmap.challenge.dto.ChalDto;
import com.ucamp.greenmap.challenge.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chal")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/available")
    public List<ChalDto> AvailableChallengesForMember (@RequestParam String token){


        return null;
    }




}
