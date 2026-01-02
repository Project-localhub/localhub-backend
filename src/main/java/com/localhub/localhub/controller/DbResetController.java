// src/main/java/com/yourapp/dev/DbResetController.java
package com.localhub.localhub.controller;

import com.localhub.localhub.service.DbResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/db/truncate")
public class DbResetController {

    private final DbResetService service;

    @PostMapping("/reset")
    public String reset() {
        int n = service.reset();
        return "TRUNCATE OK (tables=" + n + ")";
    }
}
