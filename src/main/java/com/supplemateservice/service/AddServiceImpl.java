package com.supplemateservice.service;

import com.supplemateservice.data.CustomerDao;
import com.supplemateservice.data.DayLogDao;
import com.supplemateservice.data.SupplementEntryDao;
import com.supplemateservice.data.SupplementTypeDao;
import com.supplemateservice.model.Customers;
import com.supplemateservice.model.DayLog;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class AddServiceImpl implements AddService {
    @Autowired
    CustomerDao customerDao;

    @Autowired
    SupplementTypeDao supplementTypeDao;

    @Autowired
    DayLogDao logDao;

    @Autowired
    SupplementEntryDao supplementEntryDao;

    @Autowired
    LookupService lookupService;

    @Autowired
    private PasswordEncoder encoder;

    public Customers createNewAccount(Customers customer){
        return customerDao.addCustomerAccount(customer);
    }

    @Override
    public List<SupplementType> addSupplementTypes(SupplementType... types) {
        return null;
    }

    public List<SupplementType> addSupplementType(SupplementType... types){
        // list that will hold SupplementTypes after having IDs assigned
        List<SupplementType> populatedTypeList = new ArrayList();
        for (SupplementType type : types){
            SupplementType populatedType = supplementTypeDao.addSupplementType(type);
            populatedTypeList.add(populatedType);
        }
        return populatedTypeList;
    }

    public SupplementType addSupplementType(SupplementType type){
        return supplementTypeDao.addSupplementType(type);
    }

    public DayLog addDayLog(DayLog log){
        return logDao.addDayLog(log);
    }

    public SupplementEntry addSupplementEntry(SupplementEntry supplementEntry){
        return supplementEntryDao.addSupplementEntry(supplementEntry);
    }

    public void fillDayLogGaps(int customerId) {
        List<LocalDate> dates = lookupService.getDatesForCustomer(customerId);
        int i;

        for (i = 0; i < dates.size()-1; i++){
            LocalDate firstDate = dates.get(i);
            LocalDate secondDate = dates.get(i + 1);
            // if the number of days between the first date and the second date is greater than 0:
            long missingDays = DAYS.between(firstDate, secondDate) - 1;
            if (missingDays > 0){
                DayLog fillerLog = new DayLog();
                fillerLog.setCustomer(customerDao.getCustomerAccountById(customerId));
                for (long j = 1; j <= missingDays; j++){ // plusDays() takes a long, didn't want to upcast the counter each loop
                    // get the date that's missing
                    LocalDate fillerDate = firstDate.plusDays(j);
                    fillerLog.setLogDate(fillerDate);
                    addDayLog(fillerLog);
                }
            }
        }
    }

    @Override
    public Customers populateNewUserFromForm(HttpServletRequest request) {
        Customers customer = new Customers();
        customer.setUsername(request.getParameter("username"));
        customer.setPassword(request.getParameter("password"));
        customer.setFirstName(request.getParameter("firstName"));
        customer.setLastName(request.getParameter("lastName"));
        customer.setEmail(request.getParameter("email"));
        customer.setTimeZone(request.getParameter("timeZone"));
        return customer;
    }
}
