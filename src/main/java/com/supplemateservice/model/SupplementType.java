package com.supplemateservice.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class SupplementType {
    private int supplementTypeId;
    private Customers customer;
    private String supplementName;
    private int scale;
    private String unit;

    public void setSupplementTypeId(int supplementTypeId) {
        this.supplementTypeId = supplementTypeId;
    }

    public void setUser(Customers customer) {
        this.customer = customer;
    }

    public void setSupplementName(String supplementName) {
        this.supplementName = supplementName;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.supplementTypeId;
        hash = 79 * hash + Objects.hashCode(this.customer);
        hash = 79 * hash + Objects.hashCode(this.supplementName);
        hash = 79 * hash + this.scale;
        hash = 79 * hash + Objects.hashCode(this.unit);
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
        final SupplementType other = (SupplementType) obj;
        if (this.supplementTypeId != other.supplementTypeId) {
            return false;
        }
        if (this.scale != other.scale) {
            return false;
        }
        if (!Objects.equals(this.supplementName, other.supplementName)) {
            return false;
        }
        if (!Objects.equals(this.unit, other.unit)) {
            return false;
        }
        if (!Objects.equals(this.customer, other.customer)) {
            return false;
        }
        return true;
    }
}
