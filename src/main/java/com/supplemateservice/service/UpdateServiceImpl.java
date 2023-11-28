package com.supplemateservice.service;

import com.supplemateservice.data.CustomerDao;
import com.supplemateservice.data.DayLogDao;
import com.supplemateservice.data.SupplementEntryDao;
import com.supplemateservice.model.DayLog;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    SupplementEntryDao supplementEntryDao;

    @Autowired
    DayLogDao logDao;

    public void populateSupplementTypesWithCustomer(int customerId, SupplementType... types){
        for (SupplementType supplementType: types){
            supplementType.setUser(customerDao.getCustomerAccountById(customerId));
        }
    }

    public SupplementEntry updateSupplementEntry(SupplementEntry supplementEntry){
        return supplementEntryDao.editSupplementEntry(supplementEntry);
    }

    public DayLog updateDayLog(DayLog log){
        return logDao.updateDayLog(log);
    }

}
