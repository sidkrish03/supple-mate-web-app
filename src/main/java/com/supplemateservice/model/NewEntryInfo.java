package com.supplemateservice.model;

import lombok.Getter;

public class NewEntryInfo {

    @Getter
    private int supplementTypeId;
    private float supplementDosage;

    public float getSupplementDosageValue() {
        return supplementDosage;
    }

}
