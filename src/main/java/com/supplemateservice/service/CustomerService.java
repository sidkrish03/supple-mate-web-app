package com.supplemateservice.service;

import com.supplemateservice.repository.CustomerRepository;
import com.supplemateservice.model.Customers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

//    public Customers addCustomer(Customers customer, List<Customers> customersList) {
//        return customerRepository.add(customer);
//    }

    public Customers saveCustomer(Customers customer) {
        return customerRepository.save(customer);
    }

    public List<Customers> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customers getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }

}
