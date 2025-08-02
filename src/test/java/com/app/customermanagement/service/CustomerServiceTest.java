package com.app.customermanagement.service;

import com.app.customermanagement.model.Customer;
import com.app.customermanagement.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceTest {
    

    @InjectMocks
    private CustomerService service;

    @Mock
    private CustomerRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateTierSilver() {
        Customer c = new Customer(null, "Test", "t@test.com", 500.0, LocalDateTime.now());
        assertEquals("Silver", service.calculateTier(c));
    }

    @Test
    void testCalculateTierGold() {
        Customer c = new Customer(null, "Test", "t@test.com", 5000.0, LocalDateTime.now().minusMonths(6));
        assertEquals("Gold", service.calculateTier(c));
    }

    @Test
    void testCalculateTierPlatinum() {
        Customer c = new Customer(null, "Test", "t@test.com", 15000.0, LocalDateTime.now().minusMonths(3));
        assertEquals("Platinum", service.calculateTier(c));
    }

    @Test
    void testGoldExpiredBecomesSilver() {
        Customer c = new Customer(null, "Test", "t@test.com", 5000.0, LocalDateTime.now().minusMonths(13));
        assertEquals("Silver", service.calculateTier(c));
    }

    @Test
    void testPlatinumExpiredBecomesSilver() {
        Customer c = new Customer(null, "Test", "t@test.com", 12000.0, LocalDateTime.now().minusMonths(7));
        assertEquals("Silver", service.calculateTier(c));
    }
       // CRUD operation tests

    @Test
    void testCreateCustomer() {
        Customer c = new Customer(null, "John Doe", "john@example.com", 1200.0, LocalDateTime.now());
        when(repository.save(any(Customer.class))).thenReturn(c);

        Customer created = service.create(c);
        assertEquals("John Doe", created.getName());
        verify(repository).save(c);
    }

    @Test
    void testGetByIdFound() {
        UUID id = UUID.randomUUID();
        Customer c = new Customer(id, "John Doe", "john@example.com", 1200.0, LocalDateTime.now());
        when(repository.findById(id)).thenReturn(Optional.of(c));

        Optional<Customer> result = service.getById(id);
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    void testGetByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Customer> result = service.getById(id);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName() {
        List<Customer> customers = Collections.singletonList(
            new Customer(null, "Alice", "alice@example.com", 2000.0, LocalDateTime.now())
        );
        when(repository.findByName("Alice")).thenReturn(customers);

        List<Customer> result = service.getByName("Alice");
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
    }

    @Test
    void testGetByNameAndEmail() {
        List<Customer> customers = Collections.singletonList(
            new Customer(null, "Bob", "bob@example.com", 3000.0, LocalDateTime.now())
        );
        when(repository.findByNameAndEmail("Bob", "bob@example.com")).thenReturn(customers);

        List<Customer> result = service.getByNameAndEmail("Bob", "bob@example.com");
        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).getName());
        assertEquals("bob@example.com", result.get(0).getEmail());
    }

    @Test
    void testGetByEmail() {
        Customer c = new Customer(null, "Carol", "carol@example.com", 2500.0, LocalDateTime.now());
        when(repository.findByEmail("carol@example.com")).thenReturn(Optional.of(c));

        Optional<Customer> result = service.getByEmail("carol@example.com");
        assertTrue(result.isPresent());
        assertEquals("Carol", result.get().getName());
    }

    @Test
    void testUpdateCustomer() {
        UUID id = UUID.randomUUID();
        Customer existing = new Customer(id, "Old Name", "old@example.com", 1000.0, LocalDateTime.now());
        Customer updated = new Customer(null, "New Name", "new@example.com", 2000.0, LocalDateTime.now());

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenReturn(updated);

        Optional<Customer> result = service.update(id, updated);
        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getName());
        verify(repository).save(existing);
    }

    @Test
    void testDeleteCustomer() {
        UUID id = UUID.randomUUID();
        doNothing().when(repository).deleteById(id);

        service.delete(id);
        verify(repository).deleteById(id);
    }

    @Test
    void testInvalidEmailFormat() {
        Customer c = new Customer(null, "Invalid", "invalid-email", 1000.0, LocalDateTime.now());
    
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        var violations = validator.validate(c);
    
        assertFalse(violations.isEmpty(), "Expected validation errors due to invalid email format");
    }

}
