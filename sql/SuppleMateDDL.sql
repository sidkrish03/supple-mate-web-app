DROP DATABASE IF EXISTS wellbeingtrackerpersonal;

CREATE DATABASE wellbeingtrackerpersonal;
USE wellbeingtrackerpersonal;

CREATE TABLE Customer(
                            CustomerId INT PRIMARY KEY AUTO_INCREMENT,
                            UserName VARCHAR(15) UNIQUE NOT NULL,
                            UserPassword VARCHAR(200) NOT NULL,
                            FirstName VARCHAR(30) NOT NULL,
                            LastName VARCHAR(30) NOT NULL,
                            Email VARCHAR(254) NOT NULL,
                            CreationTimestamp DATETIME NOT NULL,
                            TimeZone VARCHAR(40) NOT NULL
);

CREATE TABLE `role`(
                       RoleId INT PRIMARY KEY AUTO_INCREMENT,
                       RoleName VARCHAR(30) NOT NULL
);

INSERT INTO `role`(RoleId, RoleName) VALUES
                                         (1, "ROLE_ADMIN"),
                                         (2, "ROLE_USER");

CREATE TABLE User_Role(
                          CustomerId INT NOT NULL,
                          RoleId INT NOT NULL,
                          PRIMARY KEY(CustomerId, RoleId),
                          FOREIGN KEY (CustomerId) REFERENCES Customer(CustomerId),
                          FOREIGN KEY (RoleId) REFERENCES `role`(RoleId)
);

CREATE TABLE DayLog(
                       DayLogId INT PRIMARY KEY AUTO_INCREMENT,
                       CustomerId INT NOT NULL,
                       LogDate DATE NOT NULL,
                       Notes VARCHAR(1200) NULL,
                       CONSTRAINT fk_Customer_DayLog FOREIGN KEY (CustomerId)
                           REFERENCES Customer(CustomerId)
    -- STRECH: Composite key of DayLogId and CustomerId (should begin incrementing at 1 for a new user)
);

CREATE TABLE SupplementType(
                           SupplementTypeId INT PRIMARY KEY AUTO_INCREMENT,
                           CustomerId INT NOT NULL,
                           SupplementName VARCHAR(50) NOT NULL,
                           Scale INT NULL,
                           Unit VARCHAR(50) NULL,
                           CONSTRAINT fk_Customer_SupplementType FOREIGN KEY (CustomerId)
                               REFERENCES Customer(CustomerId)
);

CREATE TABLE SupplementEntry(
                            SupplementEntryId INT PRIMARY KEY AUTO_INCREMENT,
                            DayLogId INT NOT NULL,
                            SupplementTypeId INT NOT NULL,
                            SupplementValue FLOAT NOT NULL,
                            EntryTime TIME NOT NULL,
                            CONSTRAINT fk_DayLog_SSupplementEntry FOREIGN KEY (DayLogId)
                                REFERENCES DayLog(DayLogId),
                            CONSTRAINT fk_SupplementType_SupplementEntry FOREIGN KEY (SupplementTypeId)
                                REFERENCES SupplementType(SupplementTypeId)
);