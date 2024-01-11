package com.zerobase.commerce.api.purchase.controller;

import com.zerobase.commerce.api.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping
    ResponseEntity<?> purchase(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(purchaseService.purchase(headers));
    }
}
