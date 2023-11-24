package com.supplemateservice.service;

import com.supplemateservice.model.Customers;
import com.supplemateservice.model.DayLog;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;

import java.time.LocalDate;
import java.util.List;

public interface LookupService {
    public List<DayLog> getDayLogsForCustomer(int customerId);
    public List<LocalDate> getDatesForCustomer(int customerId);
    public List<SupplementEntry> getSupplementEntriesForType(int supplementTypeId);
    public List<SupplementEntry> getSupplementEntriesForCustomer(int customerId);
    public List<SupplementEntry> getSupplementEntriesByDate(int customerId, LocalDate date);
    public List<SupplementType> getSupplementTypesForCustomer(int customerId);
    public SupplementEntry getSupplementEntryById(int supplementEntryId);
    public SupplementType getSupplementTypeById(int supplementTypeId);
    public Customers getCustomerAccountById(int customerId);
    public DayLog getDayLogByDateAndCustomer(int customerId, LocalDate convertedDate);
    public Customers getCustomerByUsername(String username);
    public String getNotesForCustomerAndDate(int customerId, LocalDate convertedDate);

}
