package com.supplemateservice.model;

import lombok.Getter;

import java.sql.Time;
import java.util.Objects;

public class SupplementEntry {
    private int supplementEntryId;
    @Getter
    private DayLog dayLog;
    @Getter
    private SupplementType supplementType;
    @Getter
    private float supplementDosageValue;
    @Getter
    private Time entryTime;

    public int setSupplementEntryId() {
        return supplementEntryId;
    }

    public void setSupplementEntryId(int supplementEntryId) {
        this.supplementEntryId = supplementEntryId;
    }

    public void setDayLog(DayLog dayLog) {
        this.dayLog = dayLog;
    }

    public void setSupplementType(SupplementType supplementType) {
        this.supplementType = supplementType;
    }

    public void setSupplementDosageValue(float supplementDosageValue) {
        this.supplementDosageValue = supplementDosageValue;
    }

    public void setEntryTime(Time entryTime) {
        this.entryTime = entryTime;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.supplementEntryId;
        hash = 19 * hash + Objects.hashCode(this.dayLog);
        hash = 19 * hash + Objects.hashCode(this.supplementType);
        hash = 19 * hash + Float.floatToIntBits(this.supplementDosageValue);
        hash = 19 * hash + Objects.hashCode(this.entryTime);
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
        final SupplementEntry other = (SupplementEntry) obj;
        if (this.supplementEntryId != other.supplementEntryId) {
            return false;
        }
        if (Float.floatToIntBits(this.supplementDosageValue) != Float.floatToIntBits(other.supplementDosageValue)) {
            return false;
        }
        if (!Objects.equals(this.dayLog, other.dayLog)) {
            return false;
        }
        if (!Objects.equals(this.supplementType, other.supplementType)) {
            return false;
        }
        if (!Objects.equals(this.entryTime, other.entryTime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MetricEntry{" + "metricEntryId=" + supplementEntryId + ", dayLog=" + dayLog + ", metricType=" + supplementType + ", metricValue=" + supplementDosageValue + ", entryTime=" + entryTime + '}';
    }

}
