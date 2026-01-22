package com.localhub.localhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/forTest")
    public ResponseEntity<String> test() {

        return ResponseEntity.ok("테스트용12");
    }


}