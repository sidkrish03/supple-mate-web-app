package com.supplemateservice.service;

import com.supplemateservice.model.DayLog;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;

public interface UpdateService {
    public void populateSupplementTypesWithCustomer(int customer, SupplementType... types);
    public SupplementEntry updateSupplementEntry(SupplementEntry supplementEntry);
    public DayLog updateDayLog(DayLog log);
}
