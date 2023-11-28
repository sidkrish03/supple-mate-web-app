package service;

import com.supplemateservice.data.DayLogDao;
import com.supplemateservice.model.Customers;
import com.supplemateservice.model.DayLog;
import com.supplemateservice.model.SupplementEntry;
import com.supplemateservice.model.SupplementType;
import com.supplemateservice.service.AddService;
import com.supplemateservice.service.DeleteService;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DeleteServiceTest {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    DeleteService deleteService;

    @Autowired
    AddService addService;

    @Autowired
    LookupService lookupService;

    @Autowired
    DayLogDao logDao;

    public DeleteServiceTest() {
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
     * Test of deleteSupplementEntry method, of class DeleteService.
     */
    @Test
    public void testDeleteSupplementEntry() {
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
        supplementType1.setSupplementName("Vitamin");
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
        supplementEntry.setSupplementDosageValue(1000); // Measured in mg
        supplementEntry.setEntryTime(Time.valueOf("00:00:00"));
        supplementEntry = addService.addSupplementEntry(supplementEntry);
        assertNotNull(supplementEntry);

        deleteService.deleteSupplementEntry(supplementEntry.getSupplementEntryId());
        supplementEntry = lookupService.getSupplementEntryById(supplementEntry.getSupplementEntryId());
        assertNull(supplementEntry);
    }

    /**
     * Test of deleteDayLog method, of class DeleteService.
     */
    @Test
    public void testDeleteDayLog() {
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
        log = addService.addDayLog(log);
        assertNotNull(log);

        deleteService.deleteDayLog(log.getDayLogId());
        log = logDao.getDayLogById(log.getDayLogId());
        assertNull(log);
    }

    /**
     * Test of deleteSupplementType method, of class DeleteService.
     */
    @Test
    public void testDeleteMetricType() {
        Customers customer = new Customers();
        customer.setUsername("testname");
        customer.setPassword("testpassword");
        customer.setFirstName("testFirstname");
        customer.setLastName("testLastname");
        customer.setEmail(("testemail@email.com"));
        customer.setCreationTime(LocalDateTime.now());
        customer.setTimeZone("EST");
        customer = addService.createNewAccount(customer); // has ID and Role now

        SupplementType supplementType = new SupplementType();
        supplementType.setSupplementName("subjectiveTestMetric");
        supplementType.setScale(10);
        supplementType.setUser(customer);
        supplementType = addService.addSupplementType(supplementType);
        assertNotNull(supplementType);

        deleteService.deleteSupplementType(supplementType.getSupplementTypeId());
        supplementType = lookupService.getSupplementTypeById(supplementType.getSupplementTypeId());
        assertNull(supplementType);
    }

}
