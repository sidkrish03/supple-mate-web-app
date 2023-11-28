package com.supplemateservice.model;

import lombok.Getter;

public class UpdatedEntryInfo {

    @Getter
    private int supplementEntryId;
    @Getter
    private float supplementDosageValue;
    private boolean valueIsEmpty;

    public boolean isValueEmpty() {
        return valueIsEmpty;
    }


    @Override
    public String toString() {
        return "UpdatedEntryInfo{" + "supplementEntryId=" + supplementEntryId + ", supplementDosageValue=" + supplementDosageValue + '}';
    }
}
