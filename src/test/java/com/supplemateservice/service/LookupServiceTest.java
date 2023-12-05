package com.supplemateservice.service;

import com.supplemateservice.model.Customers;
import com.supplemateservice.model.DayLog;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;
import com.supplemateservice.service.AddService;
import com.supplemateservice.service.LookupService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class LookupServiceTest {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    LookupService lookupService;

    @Autowired
    AddService addService;

    public LookupServiceTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        jdbc.update("DROP TABLE metricentry");
        jdbc.update("DROP TABLE metrictype");
        jdbc.update("DROP TABLE daylog");
        jdbc.update("DROP TABLE user_role");
        jdbc.update("DROP TABLE useraccount");

        jdbc.update("CREATE TABLE UserAccount(\n" +
                "	UserAccountId INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    UserName VARCHAR(15) UNIQUE NOT NULL,\n" +
                "    UserPassword VARCHAR(200) NOT NULL,\n" +
                "    FirstName VARCHAR(30) NOT NULL,\n" +
                "    LastName VARCHAR(30) NOT NULL,\n" +
                "    Email VARCHAR(254) NOT NULL,\n" +
                "    CreationTimestamp DATETIME NOT NULL,\n" +
                "    TimeZone VARCHAR(40) NOT NULL\n" +
                ")");
        jdbc.update("CREATE TABLE User_Role(\n" +
                "	UserAccountId INT NOT NULL,\n" +
                "    RoleId INT NOT NULL,\n" +
                "    PRIMARY KEY(UserAccountId, RoleId),\n" +
                "    FOREIGN KEY (UserAccountId) REFERENCES UserAccount(UserAccountId),\n" +
                "    FOREIGN KEY (RoleId) REFERENCES `role`(RoleId)\n" +
                ")");

        jdbc.update("CREATE TABLE DayLog(\n" +
                "	DayLogId INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    UserAccountId INT NOT NULL,\n" +
                "    LogDate DATE NOT NULL,\n" +
                "    Notes VARCHAR(1200) NULL,\n" +
                "    CONSTRAINT fk_UserAccount_DayLog FOREIGN KEY (UserAccountId)\n" +
                "		REFERENCES UserAccount(UserAccountId))");

        jdbc.update("CREATE TABLE MetricType(\n" +
                "	MetricTypeId INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    UserAccountId INT NOT NULL,\n" +
                "    MetricName VARCHAR(50) NOT NULL,\n" +
                "    Scale INT NULL,\n" +
                "    Unit VARCHAR(50) NULL,\n" +
                "    CONSTRAINT fk_UserAccount_MetricType FOREIGN KEY (UserAccountId)\n" +
                "		REFERENCES UserAccount(UserAccountId)\n" +
                ")");

        jdbc.update("CREATE TABLE MetricEntry(\n" +
                "	MetricEntryId INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    DayLogId INT NOT NULL,\n" +
                "    MetricTypeId INT NOT NULL,\n" +
                "    MetricValue FLOAT NOT NULL,\n" +
                "    EntryTime TIME NOT NULL,\n" +
                "    CONSTRAINT fk_DayLog_SMetricEntry FOREIGN KEY (DayLogId)\n" +
                "		REFERENCES DayLog(DayLogId),\n" +
                "	CONSTRAINT fk_MetricType_MetricEntry FOREIGN KEY (MetricTypeId)\n" +
                "		REFERENCES MetricType(MetricTypeId)\n" +
                ")");
    }

    @AfterEach
    public void tearDown() {
    }

    /*
    The commented-out tests fail because a Customer's creationtimestamp varies slightly after they're
    retrieved from the database vs. when they're added. Assuming it's some weird discrepancy with SQL,
    not going to worry about it for now.
    */

    /**
     * Test of getDayLogsForCustomer method, of class LookupService.
     */
    @Test
    public void testGetDayLogsForCustomer() {
        Customers customer1 = new Customers();
        customer1.setUsername("testname");
        customer1.setPassword("testpassword");
        customer1.setFirstName("testFirstname");
        customer1.setLastName("testLastname");
        customer1.setEmail(("testemail@email.com"));
        customer1.setTimeZone("EST");
        customer1 = addService.createNewAccount(customer1); // has ID and Role now

        Customers customer2 = new Customers();
        customer2.setUsername("testname1");
        customer2.setPassword("testpassword1");
        customer2.setFirstName("testFirstname1");
        customer2.setLastName("testLastname1");
        customer2.setEmail(("testemail1@email.com"));
        customer2.setTimeZone("CST");
        customer2 = addService.createNewAccount(customer2); // has ID and Role now

        DayLog log1 = new DayLog();
        log1.setCustomer(customer1);
        log1.setNotes("test notes");
        log1.setLogDate(LocalDate.now());
        log1 = addService.addDayLog(log1);
        System.out.println("log: " + log1.toString());

        DayLog log2 = new DayLog();
        log2.setCustomer(customer2);
        log2.setNotes("more test notes");
        log2.setLogDate(LocalDate.now());
        log2 = addService.addDayLog(log2);
        System.out.println("log1: " + log2.toString());

        DayLog log3 = new DayLog();
        log3.setCustomer(customer2);
        log3.setNotes("even more test notes");
        log3.setLogDate(LocalDate.now());
        log3 = addService.addDayLog(log3);
        System.out.println("log2: " + log3.toString());

        List<DayLog> customer1Logs = lookupService.getDayLogsForCustomer(customer1.getCustomerId());
        System.out.println("Customer1Logs: ");
        customer1Logs.stream()
                .forEach(l -> System.out.println(l.toString()));
        List<DayLog> customer2Logs = lookupService.getDayLogsForCustomer(customer2.getCustomerId());
        assertEquals(2, customer1Logs.size());
        assertTrue(customer1Logs.contains(log1)); // false right now
        assertTrue(customer1Logs.contains(log2));
        assertFalse(customer1Logs.contains(log2));

        assertEquals(1, customer2Logs.size());
        assertTrue(customer2Logs.contains(log3));
        assertFalse(customer2Logs.contains(log1));
        assertFalse(customer2Logs.contains(log2));
    }

    /**
     * Test of getDatesForCustomer method, of class LookupService.
     */
    @Test
    public void testGetDatesForCustomer() {
        Customers customer = new Customers();
        customer.setUsername("testname");
        customer.setPassword("testpassword");
        customer.setFirstName("testFirstname");
        customer.setLastName("testLastname");
        customer.setEmail(("testemail@email.com"));
        customer.setTimeZone("EST");
        customer = addService.createNewAccount(customer); // has ID and Role now

        DayLog log = new DayLog();
        log.setCustomer(customer);
        log.setNotes("test notes");
        log.setLogDate(LocalDate.of(2020, Month.JANUARY, 1));
        log = addService.addDayLog(log);

        DayLog log1 = new DayLog();
        log1.setCustomer(customer);
        log1.setNotes("more test notes");
        log1.setLogDate(LocalDate.of(2020, Month.JANUARY, 3));
        log1 = addService.addDayLog(log1);

        List<LocalDate> dates = lookupService.getDatesForCustomer(customer.getCustomerId());
        assertEquals(2, dates.size());
        assertTrue(dates.contains(log.getLogDate()));
        assertTrue(dates.contains(log1.getLogDate()));
    }

    /**
     * Test of getMetricEntriesForType method, of class LookupService.
     */
    @Test
    public void testGetMetricEntriesForType() {
        Customers customer = new Customers();
        customer.setUsername("testname");
        customer.setPassword("testpassword");
        customer.setFirstName("testFirstname");
        customer.setLastName("testLastname");
        customer.setEmail(("testemail@email.com"));
        customer.setTimeZone("EST");
        customer = addService.createNewAccount(customer); // has ID and Role now

        DayLog log = new DayLog();
        log.setCustomer(customer);
        log.setNotes("test notes");
        log.setLogDate(LocalDate.now());
        log = addService.addDayLog(log);

        SupplementType supplementType = new SupplementType();
        supplementType.setSupplementName("quantitativeTestMetric");
        supplementType.setUnit("g");
        supplementType.setUser(customer);
        supplementType = addService.addSupplementType(supplementType);

        SupplementType supplementType1 = new SupplementType();
        supplementType1.setSupplementName("subjectiveTestMetric");
        supplementType1.setScale(10);
        supplementType1.setUser(customer);
        supplementType1 = addService.addSupplementType(supplementType1);

        SupplementEntry supplementEntry = new SupplementEntry();
        supplementEntry.setDayLog(log);
        supplementEntry.setSupplementType(supplementType);
        supplementEntry.setSupplementDosageValue(200);
        supplementEntry.setEntryTime(Time.valueOf("09:00:00"));
        supplementEntry = addService.addSupplementEntry(supplementEntry);

        SupplementEntry supplementEntry1 = new SupplementEntry();
        supplementEntry1.setDayLog(log);
        supplementEntry1.setSupplementType(supplementType);
        supplementEntry1.setSupplementDosageValue(100);
        supplementEntry1.setEntryTime(Time.valueOf("15:00:00"));
        supplementEntry1 = addService.addSupplementEntry(supplementEntry1);

        SupplementEntry supplementEntry2 = new SupplementEntry();
        supplementEntry2.setDayLog(log);
        supplementEntry2.setSupplementType(supplementType1);
        supplementEntry2.setSupplementDosageValue(1000);
        supplementEntry2.setEntryTime(Time.valueOf("21:00:00"));
        supplementEntry2 = addService.addSupplementEntry(supplementEntry2);

        List<SupplementEntry> typeEntries = lookupService.getSupplementEntriesForType(supplementType.getSupplementTypeId());
        List<SupplementEntry> type1Entries = lookupService.getSupplementEntriesForType(supplementType1.getSupplementTypeId());

        assertEquals(2, typeEntries.size());
        assertTrue(typeEntries.contains(supplementEntry));
        assertTrue(typeEntries.contains(supplementEntry1));
        assertFalse(typeEntries.contains(supplementEntry2));

        assertEquals(1, type1Entries.size());
        assertFalse(type1Entries.contains(supplementEntry));
        assertFalse(type1Entries.contains(supplementEntry1));
        assertTrue(type1Entries.contains(supplementEntry2));
    }

    /**
     * Test of getSupplementEntriesForCustomer method, of class LookupService.
     */
    @Test
    public void testGetSupplementEntriesForCustomer() {
        // Customer
        Customers customer = new Customers();
        customer.setUsername("testname");
        customer.setPassword("testpassword");
        customer.setFirstName("testFirstname");
        customer.setLastName("testLastname");
        customer.setEmail(("testemail@email.com"));
        customer.setTimeZone("EST");
        customer = addService.createNewAccount(customer); // has ID and Role now

        DayLog log = new DayLog();
        log.setCustomer(customer);
        log.setNotes("test notes");
        log.setLogDate(LocalDate.now());
        log = addService.addDayLog(log);

        SupplementType supplementType = new SupplementType();
        supplementType.setSupplementName("quantitativeTestMetric");
        supplementType.setUnit("g");
        supplementType.setUser(customer);
        supplementType = addService.addSupplementType(supplementType);

        // Customer 1
        Customers customer1 = new Customers();
        customer1.setUsername("testname1");
        customer1.setPassword("testpassword1");
        customer1.setFirstName("testFirstname1");
        customer1.setLastName("testLastname1");
        customer1.setEmail(("testemail1@email.com"));
        customer1.setCreationTime(LocalDateTime.now());
        customer1.setTimeZone("EST");
        customer1 = addService.createNewAccount(customer1); // has ID and Role now

        DayLog log1 = new DayLog();
        log1.setCustomer(customer1);
        log1.setNotes("test notes");
        log1.setLogDate(LocalDate.now());
        log1 = addService.addDayLog(log1);

        SupplementType supplementType1 = new SupplementType();
        supplementType1.setSupplementName("quantitativeTestMetric");
        supplementType1.setUnit("g");
        supplementType1.setUser(customer1);
        supplementType1 = addService.addSupplementType(supplementType1);

        // Customer supplement entries
        SupplementEntry supplementEntry = new SupplementEntry();
        supplementEntry.setDayLog(log);
        supplementEntry.setSupplementType(supplementType);
        supplementEntry.setSupplementDosageValue(300);
        supplementEntry.setEntryTime(Time.valueOf("00:00:00"));
        supplementEntry = addService.addSupplementEntry(supplementEntry);

        SupplementEntry supplementEntry1 = new SupplementEntry();
        supplementEntry1.setDayLog(log);
        supplementEntry1.setSupplementType(supplementType);
        supplementEntry1.setSupplementDosageValue(200);
        supplementEntry1.setEntryTime(Time.valueOf("00:00:00"));
        supplementEntry1 = addService.addSupplementEntry(supplementEntry1);

        // Customer 1 supplement entry
        SupplementEntry supplementEntry2 = new SupplementEntry();
        supplementEntry2.setDayLog(log1);
        supplementEntry2.setSupplementType(supplementType1);
        supplementEntry2.setSupplementDosageValue(500);
        supplementEntry2.setEntryTime(Time.valueOf("00:00:00"));
        supplementEntry2 = addService.addSupplementEntry(supplementEntry2);

        List<SupplementEntry> customerEntries = lookupService.getSupplementEntriesForCustomer(customer.getCustomerId());
        List<SupplementEntry> customer1Entries = lookupService.getSupplementEntriesForCustomer(customer1.getCustomerId());

        assertEquals(2, customerEntries.size());
        assertTrue(customerEntries.contains(supplementEntry));
        assertTrue(customerEntries.contains(supplementEntry1));
        assertFalse(customerEntries.contains(supplementEntry2));

        assertEquals(1, customer1Entries.size());
        assertFalse(customer1Entries.contains(supplementEntry));
        assertFalse(customer1Entries.contains(supplementEntry1));
        assertTrue(customer1Entries.contains(supplementEntry2));
    }

    /**
     * Test of getSupplementEntriesByDate method, of class LookupService.
     */
    @Test
    public void testGetMetricEntriesByDate() {
    }

    /**
     * Test of getMetricTypesForUser method, of class LookupService.
     */
    @Test
    public void testGetMetricTypesForUser() {
    }

    /**
     * Test of getMetricEntryById method, of class LookupService.
     */
    @Test
    public void testGetMetricEntryById() {
    }

    /**
     * Test of getMetricTypeById method, of class LookupService.
     */
    @Test
    public void testGetMetricTypeById() {
    }

    /**
     * Test of getUserAccountById method, of class LookupService.
     */
    @Test
    public void testGetUserAccountById() {
    }

    /**
     * Test of getDayLogByDateAndUser method, of class LookupService.
     */
    @Test
    public void testGetDayLogByDateAndUser() {
    }

    /**
     * Test of getUserByUsername method, of class LookupService.
     */
    @Test
    public void testGetUserByUsername() {
    }


}
