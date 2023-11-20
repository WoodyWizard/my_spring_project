package com.woody.gdatabase.controller;

import com.woody.gdatabase.service.GDatabaseService;
import com.woody.mydata.Order;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor

public class GDatabaseController {

    private GDatabaseService gDatabaseService;

    @PostMapping("/save")
    public ResponseEntity<Order> saveOrder(@RequestBody Order order) {
        try {
            return ResponseEntity.ok(gDatabaseService.saveOrder(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.findOrderById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
