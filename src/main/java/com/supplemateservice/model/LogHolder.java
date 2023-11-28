package com.supplemateservice.model;

public class LogHolder {
    private String date;
    private UpdatedEntryInfo[] updatedEntries;
    private NewEntryInfo[] newEntries;
    private String notes;

    public String getDate() {
        return date;
    }

    public UpdatedEntryInfo[] getUpdatedEntries() {
        return updatedEntries;
    }

    public NewEntryInfo[] getNewEntries() {
        return newEntries;
    }

    public String getNotes() {
        return notes;
    }
}
