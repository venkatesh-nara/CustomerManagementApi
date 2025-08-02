package com.app.customermanagement.controller;

import com.app.customermanagement.model.Customer;
import com.app.customermanagement.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerService service;

    // POST /customers
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Customer customer) {
        if (customer.getId() != null) {
            return ResponseEntity.badRequest().body("ID should not be provided.");
        }
        Customer saved = service.create(customer);
        return ResponseEntity.status(201).body(addTier(saved));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCustomers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email) {
    
        List<Customer> customers;
    
        if (name != null && email != null) {
            // Optionally support both filters if you want (or ignore this case)
            customers = service.getByNameAndEmail(name, email);
        } else if (name != null) {
            customers = service.getByName(name);
        } else if (email != null) {
            Optional<Customer> customerOpt = service.getByEmail(email);
            if (customerOpt.isPresent()) {
                return ResponseEntity.ok(Collections.singletonList(addTier(customerOpt.get())));
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            customers = service.getAll();
        }
    
        List<Map<String, Object>> result = customers.stream()
            .map(this::addTier)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    

    // GET /customers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return service.getById(id)
                .map(customer -> ResponseEntity.ok(addTier(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

 // PUT /customers
 @PutMapping("/{id}")
public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody Customer customer) {
    // Force the customer ID to the path variable ID to avoid mismatch
    customer.setId(id);

    return service.update(id, customer)
            .map(updated -> ResponseEntity.ok(addTier(updated)))
            .orElse(ResponseEntity.notFound().build());
}

    // DELETE /customers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> addTier(Customer customer) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", customer.getId());
        map.put("name", customer.getName());
        map.put("email", customer.getEmail());
        map.put("annualSpend", customer.getAnnualSpend());
        map.put("lastPurchaseDate", customer.getLastPurchaseDate());
        map.put("tier", service.calculateTier(customer));
        return map;
    }
}
