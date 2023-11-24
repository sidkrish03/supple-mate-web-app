package com.supplemateservice.data;

import com.supplemateservice.model.Customers;

import java.util.List;

public interface CustomerDao{
    public Customers addCustomerAccount(Customers customer);

    public Customers getCustomerAccountById(int customerId);

    public List<Customers> getAllCustomerAccounts();

    public Customers editCustomerAccount(Customers updatedUser);

    public void deleteCustomerAccount(int customerId);

    public Customers getCustomerByUsername(String username);
}
