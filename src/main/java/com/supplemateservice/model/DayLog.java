package com.supplemateservice.model;

import com.sun.xml.bind.v2.TODO;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
public class DayLog {
    private int dayLogId;
    private Customers customer;
    private LocalDate logDate;
    private String notes;

    public void setDayLogId(int dayLogId) {
        this.dayLogId = dayLogId;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.dayLogId;
        hash = 97 * hash + Objects.hashCode(this.customer);
        hash = 97 * hash + Objects.hashCode(this.logDate);
        hash = 97 * hash + Objects.hashCode(this.notes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DayLog other = (DayLog) obj;
        if (this.dayLogId != other.dayLogId) {
            return false;
        }
        if (!Objects.equals(this.notes, other.notes)) {
            return false;
        }
        if (!Objects.equals(this.customer, other.customer)) {
            return false;
        }
        if (!Objects.equals(this.logDate, other.logDate)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DayLog{" + "dayLogId=" + dayLogId + ", customer=" + customer + ", logDate=" + logDate + ", notes=" + notes + '}';
    }

    //TODO - Check why compareTo Method is throwing an error and if it's needed
//    @Override
//    public int compareTo(DayLog otherDayLog) {
//        return (this.getLogDate().compareTo(otherDayLog.getLogDate()));
//    }
}
