package com.gooroomees.neulbomgil_backend.domain.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CareGradeViewController {

    @GetMapping("/caregrade")
    public String careGradePage() {
        return "grade/caregrade";
    }

    @GetMapping("/caregrade/test")
    public String careGradeTestPage() {
        return "grade/caregrade-test";
    }
}