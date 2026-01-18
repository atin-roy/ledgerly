package dev.atinroy.financebackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/bla")
    private String bla() {
        return "bla";
    }
}
