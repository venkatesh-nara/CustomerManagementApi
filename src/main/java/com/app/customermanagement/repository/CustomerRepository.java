package com.app.customermanagement.repository;

import com.app.customermanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByName(String name);
    Optional<Customer> findByEmail(String email);
    List<Customer> findByNameAndEmail(String name, String email);
}
