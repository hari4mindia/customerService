package com.example.customerservice.controller;

import com.example.customerservice.model.Customer;
import com.example.customerservice.model.Payment;
import com.example.customerservice.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return updatedCustomer != null ? ResponseEntity.ok(updatedCustomer) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/{customerId}/payments")
    public ResponseEntity<Payment> getPaymentsForCustomer(@PathVariable String customerId) {
        log.info("Fetching payments for customer ID: {}", customerId);
        Customer customer = customerService.getCustomerByCustomerId(customerId);
        log.info("Checking customer available for this customer ID: {}", customerId);
        if (customer != null) {
            log.info("Customer found for customer ID: {}", customerId);
        } else {
            log.info("No Customer found for customer ID: {}", customerId);
        }
        String paymentServiceUrl = "http://localhost:8081/api/payments/customer/" + customerId;
        log.info("Payment service URL: {}", paymentServiceUrl);

        Payment payment = restTemplate.getForObject(paymentServiceUrl, Payment.class);

        if (payment != null) {
            log.info("Payment found for customer ID: {}", customerId);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } else {
            log.info("No payment found for customer ID: {}", customerId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
