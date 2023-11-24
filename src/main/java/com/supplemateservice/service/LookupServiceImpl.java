package com.supplemateservice.service;

import com.supplemateservice.data.CustomerDao;
import com.supplemateservice.model.Customers;
import com.supplemateservice.model.DayLog;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LookupServiceImpl implements LookupService{

        @Autowired
        DayLogDao logDao;

        @Autowired
        SupplementEntryDao supplementEntryDao;

        @Autowired
        SupplementTypeDao supplementTypeDao;

        @Autowired
        CustomerDao customerDao;


        // DayLog implements Comparator, should be sorted in ascending order by logDate
        public List<DayLog> getDayLogsForCustomer(int customerId){
            List<DayLog> dayLogs = logDao.getAllDayLogs().stream()
                    .filter(log -> log.getCustomer().getUserAccountId() == customerId)
                    .sorted()
                    .collect(Collectors.toList());
            return dayLogs;
        }

        // sorted (natural order)
        public List<LocalDate> getDatesForCustomer(int customerId){
            return getDayLogsForUser(customerId).stream()
                    .map(DayLog::getLogDate)
                    .sorted()
                    .collect(Collectors.toList());
        }

        public List<SupplementEntry> getSupplementEntriesForType(int supplementTypeId){
            return supplementEntryDao.getAllSupplementEntriesSorted().stream()
                    .filter(entry -> entry.getSupplementType().getSupplementTypeId() == supplementTypeId)
                    .collect(Collectors.toList());
        }


        public List<SupplementEntry> getSupplementEntriesForCustomer(int customerId){
            return supplementEntryDao.getAllSupplementEntriesSorted().stream()
                    .filter(entry -> entry.getDayLog().getCustomer().getCustomerId() == customerId)
                    .collect(Collectors.toList());
        }

        public List<SupplementEntry> getSupplementEntriesByDate(int customerId, LocalDate date){

            List<SupplementEntry> supplementEntryList = getSupplementEntriesForCustomer(customerId).stream()
                    .filter(entry -> entry.getDayLog().getLogDate().isEqual(date))
                    .collect(Collectors.toList());
            return supplementEntryList;
        }

        public List<SupplementType> getSupplementTypesForCustomer(int customerId){
            return supplementTypeDao.getAllSupplementTypes().stream()
                    .filter(type -> type.getCustomer().getCustomerAccountId() == customerId)
                    .collect(Collectors.toList());
        }

        public SupplementEntry getSupplementEntryById(int supplementEntryId) {
            return supplementEntryDao.getSupplementEntryById(supplementEntryId);
        }

        public SupplementType getSupplementTypeById(int supplementTypeId){
            return supplementTypeDao.getSupplementTypeById(supplementTypeId);
        }

        public Customers getCustomerAccountById(int customerId){
            return customerDao.getCustomerAccountById(customerId);
        }

        public DayLog getDayLogByDateAndCustomer(int customerId, LocalDate convertedDate) {
            return getDayLogsForCustomer(customerId).stream()
                    .filter(log -> log.getLogDate().isEqual(convertedDate))
                    .findFirst().orElse(null);
        }

        public Customers getCustomerByUsername(String username){
            return customerDao.getCustomerByUsername(username);
        }

        @Override
        public String getNotesForCustomerAndDate(int customerId, LocalDate convertedDate) {
            return getDayLogByDateAndCustomer(customerId, convertedDate).getNotes();
        }
}
