package com.gooroomees.neulbomgil_backend.domain.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MapViewController {

    @Value("${kakao.map.api-key}")
    private String kakaoMapApiKey;

    @GetMapping("/map")
    public String mapPage(Model model) {
        model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
        return "map/main";
    }
}