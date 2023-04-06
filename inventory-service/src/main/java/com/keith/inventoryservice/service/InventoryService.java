package com.keith.inventoryservice.service;

import com.keith.inventoryservice.dto.InventoryResponse;
import com.keith.inventoryservice.model.Inventory;
import com.keith.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @SneakyThrows
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode){
//        The below was to test the circuit breakers
//        log.info("Execution Started");
//        Thread.sleep(10000);
//        log.info("Execution finished");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                    InventoryResponse
                            .builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build()
                ).toList();
    }
}
