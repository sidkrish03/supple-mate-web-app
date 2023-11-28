package com.supplemateservice.controller;

import com.supplemateservice.exceptions.InvalidEntryException;
import com.supplemateservice.exceptions.InvalidSupplementTypeException;
import com.supplemateservice.model.*;
import com.supplemateservice.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SuppleMateController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AddService addService;

    @Autowired
    LookupService lookupService;

    @Autowired
    UpdateService updateService;

    @Autowired
    DeleteService deleteService;

    @Autowired
    ValidateService validateService;

    @RequestMapping
    public String SuppleMateIntro() {
        return "Welcome to SuppleMate! Your friendly supplement tracker";
    }

    @RequestMapping("/pre-registration")
    public String SuppleMatePreRegistration() {
        return "Please enter the details below to create an account and register with us.";
    }

    @RequestMapping("/post-registration")
    public String SuppleMatePostRegistration() {
        return "You have successfully registered with us!";
    }

    @RequestMapping("/login")
    public String SuppleMateLogin() {
        return "Please enter your user name and password to login.";
    }

    @RequestMapping("/home")
    public String SuppleMateHome() {
        return "Welcome to SuppleMate Home Page.";
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("this is a test", HttpStatus.OK);
    }

    @PostMapping("/addSupplementTypes/{customerId}")
    public ResponseEntity<List<SupplementType>> createMetricSettings(@PathVariable int customerId, @RequestBody SupplementType[] supplementTypes) throws InvalidSupplementTypeException{
        // lookup the user using the ID from the PathVariable and assign to each MetricType
        updateService.populateSupplementTypesWithCustomer(customerId, supplementTypes);
        // validate user's initial MetricType settings
        validateService.validateSupplementTypes(customerId, supplementTypes);
        // add MetricTypes to DB, get them back with IDs
        List<SupplementType> populatedTypeList = addService.addSupplementTypes(supplementTypes);
        return new ResponseEntity(populatedTypeList, HttpStatus.CREATED);
    }

    // get all DayLogs for a user
    @GetMapping("/dayLogs/{customerId}")
    public ResponseEntity<List<DayLog>> getAllDayLogsForUser(@PathVariable int customerId){
        return new ResponseEntity(lookupService.getDayLogsForCustomer(customerId), HttpStatus.OK);
    }

    // get notes for a specific date/dayLog
    @GetMapping("/notes/{customerId}/{date}")
    public ResponseEntity<List<DayLog>> getNotesForUserAndDate(@PathVariable int customerId, @PathVariable String date){
        LocalDate convertedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        String notes = "";
        try{
            notes = lookupService.getNotesForCustomerAndDate(customerId, convertedDate);
        }
        catch(NullPointerException e){
            // do nothing
        }
        return new ResponseEntity(notes, HttpStatus.OK);
    }

    // get all log dates for a user (FOR GRAPH VIEW)
    @GetMapping("/dates/{customerId}")
    public ResponseEntity<List<LocalDate>> getAllLogDatesForUser(@PathVariable int customerId){
        return new ResponseEntity(lookupService.getDatesForCustomer(customerId), HttpStatus.OK);
    }

    // get all metric types for a user (FOR GRAPH VIEW)
    @GetMapping("/supplementTypes/{customerId}")
    public ResponseEntity<List<SupplementType>> getAllSupplementTypesForCustomer(@PathVariable int customerId){
        List<SupplementType> types = lookupService.getSupplementTypesForCustomer(customerId);
        logger.info("Types: {}", types.toString());
        return new ResponseEntity(lookupService.getSupplementTypesForCustomer(customerId), HttpStatus.OK);
    }

    // get all metric entries for a user (FOR GRAPH VIEW)
    @GetMapping("/supplementEntries/{customerId}")
    public ResponseEntity<List<List<SupplementEntry>>> getAllMetricEntriesForCustomer(@PathVariable int customerId){
        // Get all types for a user
        List<SupplementType> userTypes = lookupService.getSupplementTypesForCustomer(customerId);
        // List of MetricEntry-containing Lists
        List<List<SupplementEntry>> entryLists = new ArrayList();
        // for each type the user has, get all the entries for that type in a list
        for (SupplementType type : userTypes){
            List<SupplementEntry> entryList = lookupService.getSupplementEntriesForType(type.getSupplementTypeId());
            // add all entries for that type to the list of MetricEntry-containing lists
            entryLists.add(entryList);
        }
        return new ResponseEntity(entryLists, HttpStatus.OK);
    }

    // get all entries for a user by date
    @GetMapping("/supplementEntries/{customerId}/{date}")
    public ResponseEntity<List<SupplementEntry>> getSupplementEntriesByDate(@PathVariable int customerId, @PathVariable String date){
        LocalDate convertedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        return new ResponseEntity(lookupService.getSupplementEntriesByDate(customerId, convertedDate), HttpStatus.OK);
    }

    // maybe could improve conditionals here, but it keeps breaking when I try to improve it so it's staying like this for now
    @PostMapping("/updateLog/{customerId}")
    public ResponseEntity updateLogEntries(@PathVariable int customerId, @RequestBody LogHolder holder) throws InvalidEntryException, InvalidSupplementTypeException {
        long startTime = System.currentTimeMillis(); // performance testing
        LocalDate convertedDate = LocalDate.parse(holder.getDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        String notes = holder.getNotes();
        UpdatedEntryInfo[] updatedEntries = holder.getUpdatedEntries();
        NewEntryInfo[] newEntries = holder.getNewEntries();

        boolean onlyNewEntries = false;

        // if there are no updatedEntries, then there are only new entries and notes
        if (updatedEntries.length == 0){
            onlyNewEntries = true;
        }
        if (updatedEntries.length == 0 && newEntries.length == 0 && notes.trim().isEmpty()){
            onlyNewEntries = false;
        }

        // DayLog reference- will either point to an existing DayLog or become a new one
        DayLog log = null;

        // check if DayLog has already been created
        log = lookupService.getDayLogByDateAndCustomer(customerId, convertedDate);
        if (log != null){
            log.setNotes(notes);
            updateService.updateDayLog(log);
        }

        // if there are existing entries:
        if (!onlyNewEntries){
            // EDITING ENTRIES
            for (int i = 0; i < updatedEntries.length; i++){
                // get the original entry from DB
                SupplementEntry originalEntry = lookupService.getSupplementEntryById(updatedEntries[i].getSupplementEntryId());
                // DELETION
                if (updatedEntries[i].isValueEmpty()){
                    deleteService.deleteSupplementEntry(updatedEntries[i].getSupplementEntryId());
                    continue;
                }
                originalEntry.setSupplementDosageValue(updatedEntries[i].getSupplementDosageValue());
                validateService.validateSupplementEntry(originalEntry);
                SupplementEntry updatedEntry = updateService.updateSupplementEntry(originalEntry);
            }
        }

        // if log is still null (meaning there are only new entries), create a new one
        if (log == null){
            log = new DayLog();
            log.setLogDate(convertedDate);
            log.setCustomer(lookupService.getCustomerAccountById(customerId));
            log.setNotes(notes);
            log = addService.addDayLog(log);
        }

        // ADDING NEW ENTRIES
        for (int j = 0; j < newEntries.length; j++){
            SupplementEntry newSupplementEntry = new SupplementEntry();
            newSupplementEntry.setDayLog(log);
            newSupplementEntry.setSupplementType(lookupService.getSupplementTypeById(newEntries[j].getSupplementTypeId()));
            newSupplementEntry.setSupplementDosageValue(newEntries[j].getSupplementDosageValue());
            newSupplementEntry.setEntryTime(Time.valueOf(LocalTime.now())); // TODO: change to user's time zone (shouldn't be that hard)
            validateService.validateSupplementEntry(newSupplementEntry);
            // newEntry is added to DB
            addService.addSupplementEntry(newSupplementEntry);
        }

        // If a DayLog has no entries, delete it
        List<DayLog> userLogs = lookupService.getDayLogsForCustomer(customerId);


        // a log won't be deleted if it has notes but no entries
        for (DayLog dayLog : userLogs){
            if (lookupService.getSupplementEntriesByDate(customerId, dayLog.getLogDate()).isEmpty() && (dayLog.getNotes() == null || dayLog.getNotes().trim().isEmpty())){
                deleteService.deleteDayLog(dayLog.getDayLogId());
            }
        }

        /*
        Add any missing DayLogs (dates with no entries) for user to fill in potential gaps.
        Does not contradict previous code block.
        */
        addService.fillDayLogGaps(customerId);

        logger.info("Time to complete update: " + (System.currentTimeMillis() - startTime) + "ms");
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping("/thank-you")
    public String SuppleMatePostLogout() {
        return "Thank you for using SuppleMate! Hope to see you soon";
    }
}
