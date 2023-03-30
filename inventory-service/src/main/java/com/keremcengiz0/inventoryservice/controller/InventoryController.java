package com.keremcengiz0.inventoryservice.controller;


import com.keremcengiz0.inventoryservice.dto.InventoryResponse;
import com.keremcengiz0.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return this.inventoryService.isInStock(skuCode);
    }
}
