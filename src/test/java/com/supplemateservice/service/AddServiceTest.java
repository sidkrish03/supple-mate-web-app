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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AddServiceTest {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    LookupService lookupService;

    @Autowired
    AddService addService;

    public AddServiceTest() {
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

    /**
     * Test of createNewAccount method, of class AddService.
     */
    @Test
    public void testCreateNewAccount() {
        Customers customer = new Customers();
        customer.setUsername("testname");
        customer.setPassword("testpassword");
        customer.setFirstName("testFirstname");
        customer.setLastName("testLastname");
        customer.setEmail(("testemail@email.com"));
        customer.setCreationTime(LocalDateTime.now());
        customer.setTimeZone("EST");

        Customers customerFromDB = addService.createNewAccount(customer);
        customer.setCustomerId(1);
        assertEquals(customer, customerFromDB);
    }

    /**
     * Test of addSupplementTypes method, of class AddService.
     */
    @Test
    public void testAddSupplementTypes() {
        Customers customer = new Customers();
        customer.setUsername("testname");
        customer.setPassword("testpassword");
        customer.setFirstName("testFirstname");
        customer.setLastName("testLastname");
        customer.setEmail(("testemail@email.com"));
        customer.setCreationTime(LocalDateTime.now());
        customer.setTimeZone("EST");
        customer = addService.createNewAccount(customer); // has ID and Role now

        SupplementType supplementType1 = new SupplementType();
        supplementType1.setSupplementName("Iron");
        supplementType1.setScale(10);
        supplementType1.setUser(customer);
        SupplementType supplementType1FromDB = addService.addSupplementType(supplementType1);
        assertEquals(1, supplementType1FromDB.getSupplementTypeId());
        supplementType1FromDB.setSupplementTypeId(1);
        assertEquals(supplementType1, supplementType1FromDB);

        SupplementType supplementType2 = new SupplementType();
        supplementType2.setSupplementName("Vitamin");
        supplementType2.setUnit("g");
        supplementType2.setUser(customer);
        SupplementType supplementType2FromDB = addService.addSupplementType(supplementType2);
        assertEquals(2, supplementType2FromDB.getSupplementTypeId());
        supplementType2.setSupplementTypeId(2);
        assertEquals(supplementType2, supplementType2FromDB);

        assertEquals(2, lookupService.getSupplementTypesForCustomer(customer.getCustomerId()).size());
    }

    /**
     * Test of addDayLog method, of class AddService.
     */
    @Test
    public void testAddDayLog() {
        Customers customer = new Customers();
        customer.setUsername("testname");
        customer.setPassword("testpassword");
        customer.setFirstName("testFirstname");
        customer.setLastName("testLastname");
        customer.setEmail(("testemail@email.com"));
        customer.setCreationTime(LocalDateTime.now());
        customer.setTimeZone("EST");
        customer = addService.createNewAccount(customer); // has ID and Role now

        DayLog log = new DayLog();
        log.setCustomer(customer);
        log.setNotes("test notes");
        log.setLogDate(LocalDate.now());
        DayLog logFromDB = addService.addDayLog(log);
        assertEquals(1, logFromDB.getDayLogId());
        log.setDayLogId(1);
        assertEquals(log, logFromDB);
    }

    /**
     * Test of addSupplementEntry method, of class AddService.
     */
    @Test
    public void testAddSupplementEntry() {
        Customers customer = new Customers();
        customer.setUsername("testname");
        customer.setPassword("testpassword");
        customer.setFirstName("testFirstname");
        customer.setLastName("testLastname");
        customer.setEmail(("testemail@email.com"));
        customer.setCreationTime(LocalDateTime.now());
        customer.setTimeZone("EST");
        customer = addService.createNewAccount(customer); // has ID and Role now

        SupplementType supplementType1 = new SupplementType();
        supplementType1.setSupplementName("Zinc");
        supplementType1.setScale(10);
        supplementType1.setUser(customer);
        supplementType1 = addService.addSupplementType(supplementType1);

        DayLog log = new DayLog();
        log.setCustomer(customer);
        log.setNotes("test notes");
        log.setLogDate(LocalDate.now());
        log = addService.addDayLog(log);

        SupplementEntry supplementEntry = new SupplementEntry();
        supplementEntry.setDayLog(log);
        supplementEntry.setSupplementType(supplementType1);
        supplementEntry.setSupplementDosageValue(500); //Measured in mg
        supplementEntry.setEntryTime(Time.valueOf("00:00:00"));
        SupplementEntry entryFromDB = addService.addSupplementEntry(supplementEntry);
        assertEquals(1, entryFromDB.getSupplementEntryId());
        supplementEntry.setSupplementEntryId(1);
        assertEquals(supplementEntry, entryFromDB);

    }

    /**
     * Test of fillDayLogGaps method, of class AddService.
     */
    @Test
    public void testFillDayLogGaps() {
        Customers customer = new Customers();
        customer.setUsername("testname");
        customer.setPassword("testpassword");
        customer.setFirstName("testFirstname");
        customer.setLastName("testLastname");
        customer.setEmail(("testemail@email.com"));
        customer.setCreationTime(LocalDateTime.now());
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
        log1.setLogDate(LocalDate.of(2020, Month.JANUARY, 4));
        log1 = addService.addDayLog(log1);

        // verify only these dayLogs exist so far
        assertEquals(2, lookupService.getDatesForCustomer(customer.getCustomerId()).size());
        addService.fillDayLogGaps(customer.getCustomerId());
        assertEquals(4, lookupService.getDatesForCustomer(customer.getCustomerId()).size());
    }


}
