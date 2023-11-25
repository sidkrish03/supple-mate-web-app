package com.supplemateservice.service;

import com.supplemateservice.data.CustomerDao;
import com.supplemateservice.exceptions.*;
import com.supplemateservice.model.Customers;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ValidateServiceImpl implements ValidateService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    LookupService lookupService;

    public void validateNewAccountSettings(Set<String> violations, Customers customer, String passwordConfirmationEntry){
        // check for first name
        if (customer.getFirstName().trim().isEmpty()){
            violations.add("Please enter your first name.");
        }
        validateUsername(violations, customer.getUsername());
        validatePassword(violations, customer.getPassword(), passwordConfirmationEntry);
        validateEmail(violations, customer.getPassword());
    }

    public void validateUsername(Set<String> violations, String username){
        // check for existence
        if (username.trim().isEmpty()){
            violations.add("Please enter your first name.");
        }
        // check for a length over 15
        if (username.length() > 15){
            violations.add("Username cannot be over 15 characters.");
        }
        validateUsernameDoesNotExist(violations, username);
    }

    private void validateUsernameDoesNotExist(Set<String> violations, String username){
        Set<String> usernames = customerDao.getAllCustomerAccounts()
                .stream().map(Customers::getUsername).collect(Collectors.toSet());
        if (usernames.contains(username)){
            violations.add("Username already exists.");
        }
    }

    public void validatePassword(Set<String> violations, String password, String passwordConfirmationEntry){
    /* ^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,50}$ matches strings with at least 8 characters,
    no more than 50 characters, and at least one lowercase letter, uppercase letter, and
    number each */

        // check if user entered password
        if(password.trim().isEmpty()){
            violations.add("Please enter a password.");
        }

        // check that passwordConfirmation matches password
        if(!password.equals(passwordConfirmationEntry)){
            violations.add("Passwords do not match.");
        }

        // TODO: split this up so regex only checks for uppercase/lowercase/number inclusion, not casing
        if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,50}$", password)){
            violations.add("Password must be between 8-50 characters, as well as "
                    + "contain at least 1 uppercase letter, 1 lowercase letter, and 1 number.");
        }
    }

    public void validateEmail(Set<String> violations, String email){
        // TODO: fix email regex. Works in tester, doesn't work here. Probably character escape issue.

//        if (!Pattern.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", email)){
//            throw new InvalidEmailException("Email address is invalid.");
//        }
        // check if user entered email
        if (email.trim().isEmpty()){
            violations.add("Please enter an email address.");
        }

        validateEmailDoesNotExist(violations, email);
    }

    private void validateEmailDoesNotExist(Set<String> violations, String email){
        Set<String> emails = customerDao.getAllCustomerAccounts()
                .stream().map(Customers::getEmail).collect(Collectors.toSet());
        if (emails.contains(email)){
            violations.add("Email is already registered.");
        }
    }

    // compiler implicitly creates an array in the parameter list when this is called
    public void validateSupplementTypes(int customerId, SupplementType... types) throws InvalidSupplementTypeException{
        for (SupplementType type : types){
            if (type.getSupplementName() == null){
                throw new InvalidSupplementTypeException("Supplement type's name cannot be empty.");
            }
            if (type.getCustomer() == null){
                throw new InvalidSupplementTypeException("Supplement type must be associated with a user.");
            }

            if ((type.getScale() == 0) && (type.getUnit() == null)){
                throw new InvalidSupplementTypeException("A supplement type's scale and unit cannot both be empty.");
            }
            List<SupplementType> supplementTypesForCustomer = lookupService.getSupplementTypesForCustomer(customerId);
            for (SupplementType existingType : supplementTypesForCustomer){
                if (existingType.getSupplementName().equalsIgnoreCase(type.getSupplementName())){
                    throw new InvalidSupplementTypeException("Supplement type already exists for user.");
                }
            }
        }
    }

    public void validateSupplementEntry(SupplementEntry entry) throws InvalidEntryException, InvalidSupplementTypeException {
        float value = entry.getSupplementDosageValue();
        SupplementType type = entry.getSupplementType();
        int scale = type.getScale();
        // check if entry has supplementType
        if (type == null){
            throw new InvalidEntryException("Entry has no associated type.");
        }
        // check that type exists for the user
        if (type.getCustomer() == null){
            throw new InvalidSupplementTypeException("Type of entry is not associated with user.");
        }
        // check that entry has a supplementValue
        if (value < 0){
            throw new InvalidEntryException("Entry must contain a positive value.");
        }
        // if entry has subjective type, check that its value is between 1 and the scale
        if (checkIfSubjective(entry)){
            if (!(value >= 1) || !(value <= scale)){
                throw new InvalidEntryException("Entry value must be between 1 and " + scale);
            }
        }
    }

    private boolean checkIfSubjective(SupplementEntry entry){
        // ints can't be null, default to 0 if not explicitly initialized
        if (entry.getSupplementType().getScale() == 0){
            return false;
        }
        return true;
    }

    // VALIDATIONS THAT THROW EXCEPTIONS
    public void validateNewAccountSettings(Customers customer) throws InvalidUsernameException, InvalidPasswordException, InvalidEmailException{
        validateUsername(customer.getUsername());
        validatePassword(customer.getPassword());
        validateEmail(customer.getPassword());
    }

    public void validateUsername(String username) throws InvalidUsernameException{
        // check for a length over 15
        if (username.length() > 15){
            throw new InvalidUsernameException("Username cannot be over 15 characters.");
        }
        validateUsernameDoesNotExist(username);
    }

    private void validateUsernameDoesNotExist(String username) throws InvalidUsernameException{
        Set<String> usernames = customerDao.getAllCustomerAccounts()
                .stream().map(Customers::getUsername).collect(Collectors.toSet());
        if (usernames.contains(username)){
            throw new InvalidUsernameException("Username already exists.");
        }
    }

    public void validatePassword(String password) throws InvalidPasswordException {
    /* ^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,50}$ matches strings with at least 8 characters,
    no more than 50 characters, and at least one lowercase letter, uppercase letter, and
    number each */

        // TODO: split this up so regex only checks for uppercase/lowercase/number inclusion, not casing
        if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,50}$", password)){
            throw new InvalidPasswordException("Password must be between 8-50 characters, as well as "
                    + "contain at least 1 uppercase letter, 1 lowercase letter, and 1 number.");
        }
    }

    public void validateEmail(String email) throws InvalidEmailException {
        // TODO: fix email regex. Works in tester, doesn't work here. Probably character escape issue.

//        if (!Pattern.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", email)){
//            throw new InvalidEmailException("Email address is invalid.");
//        }
        validateEmailDoesNotExist(email);
    }

    private void validateEmailDoesNotExist(String email) throws InvalidEmailException{
        Set<String> emails = customerDao.getAllCustomerAccounts()
                .stream().map(Customers::getEmail).collect(Collectors.toSet());
        if (emails.contains(email)){
            throw new InvalidEmailException("Email is already registered.");
        }
    }
}

