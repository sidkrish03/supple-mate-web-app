package com.supplemateservice.service;

import com.supplemateservice.model.Customers;
import com.supplemateservice.model.DayLog;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AddService {
    public Customers createNewAccount(Customers customer);
    public List<SupplementType> addSupplementTypes(SupplementType... types);
    public SupplementType addSupplementType(SupplementType supplementType);
    public DayLog addDayLog(DayLog log);
    public SupplementEntry addSupplementEntry(SupplementEntry supplementEntry);
    public void fillDayLogGaps(int customerId);
    public Customers populateNewUserFromForm(HttpServletRequest request);
}
