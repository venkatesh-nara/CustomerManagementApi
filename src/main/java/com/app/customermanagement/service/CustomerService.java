package com.app.customermanagement.service;

import com.app.customermanagement.model.Customer;
import com.app.customermanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;

    public Customer create(Customer customer) {
        return repository.save(customer);
    }
    public List<Customer> getByNameAndEmail(String name, String email) {
        return repository.findByNameAndEmail(name, email);
    }
    public List<Customer> getAll() {
        return repository.findAll();
    }
    public Optional<Customer> getById(UUID id) {
        return repository.findById(id);
    }

    public List<Customer> getByName(String name) {
        return repository.findByName(name);
    }

    public Optional<Customer> getByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<Customer> update(UUID id, Customer updated) {
        return repository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setEmail(updated.getEmail());
            existing.setAnnualSpend(updated.getAnnualSpend());
            existing.setLastPurchaseDate(updated.getLastPurchaseDate());
            return repository.save(existing);
        });
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public String calculateTier(Customer customer) {
        if (customer.getAnnualSpend() == null || customer.getAnnualSpend() < 1000) {
            return "Silver";
        } else if (customer.getAnnualSpend() < 10000) {
            if (customer.getLastPurchaseDate() != null &&
                customer.getLastPurchaseDate().isAfter(LocalDateTime.now().minusMonths(12))) {
                return "Gold";
            }
        } else {
            if (customer.getLastPurchaseDate() != null &&
                customer.getLastPurchaseDate().isAfter(LocalDateTime.now().minusMonths(6))) {
                return "Platinum";
            }
        }
        return "Silver";
    }
}
