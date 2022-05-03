package com.bernic.msscbeerorderservice.repositories;

import com.bernic.msscbeerorderservice.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findAllByCustomerName(String customerName); //to be replaced by findAllByCustomerName not working because of https://github.com/spring-projects/spring-data-jpa/issues/2472
}
