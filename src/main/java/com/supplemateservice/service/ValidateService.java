package com.supplemateservice.service;

import com.supplemateservice.exceptions.*;
import com.supplemateservice.model.Customers;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;

import java.util.Set;

public interface ValidateService {

    public void validateNewAccountSettings(Set<String> violations, Customers customer, String passwordConfirmationEntry);
    public void validateNewAccountSettings(Customers customer) throws InvalidUsernameException, InvalidPasswordException, InvalidEmailException;
    public void validateUsername(Set<String> violations, String username);
    public void validateUsername(String username) throws InvalidUsernameException;
    public void validatePassword(Set<String> violations, String password, String passwordConfirmationEntry);
    public void validatePassword(String password) throws InvalidPasswordException;
    public void validateEmail(Set<String> violations, String email);
    public void validateEmail(String email) throws InvalidEmailException;
    public void validateSupplementTypes(int Id, SupplementType... types) throws InvalidSupplementTypeException;
    public void validateSupplementEntry(SupplementEntry entry) throws InvalidEntryException, InvalidSupplementTypeException;
}
