package com.keremcengiz0.inventoryservice.service;

import com.keremcengiz0.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode) {
        boolean isPresent = this.inventoryRepository.findBySkuCode(skuCode ).isPresent();
        return isPresent;
    }
}
