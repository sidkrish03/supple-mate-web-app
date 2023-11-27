package com.supplemateservice.service;

import com.supplemateservice.data.DayLogDao;
import com.supplemateservice.data.SupplementEntryDao;
import com.supplemateservice.data.SupplementTypeDao;
import com.supplemateservice.model.SupplementEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteServiceImpl implements DeleteService {

    @Autowired
    SupplementEntryDao supplementEntryDao;

    @Autowired
    DayLogDao logDao;

    @Autowired
    SupplementTypeDao supplementTypeDao;

    @Autowired
    LookupService lookupService;

    public void deleteSupplementEntry(int supplementEntryId) {
        supplementEntryDao.deleteSupplementEntry(supplementEntryId);
    }

    public void deleteDayLog(int dayLogId){
        logDao.deleteDayLog(dayLogId);
    }

    public void deleteSupplementType(int supplementTypeId){
        for (SupplementEntry supplementEntry : lookupService.getSupplementEntriesForType(supplementTypeId)){
            supplementEntryDao.deleteSupplementEntry(supplementEntry.getSupplementEntryId());
        }
        supplementTypeDao.deleteSupplementType(supplementTypeId);
    }
}
