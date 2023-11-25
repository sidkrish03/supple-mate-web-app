package com.supplemateservice.data;

import com.supplemateservice.model.SupplementType;

import java.util.List;

public interface SupplementTypeDao {
    public SupplementType addSupplementType(SupplementType supplementType);

    public SupplementType getSupplementTypeById(int supplementTypeId);

    public List<SupplementType> getAllSupplementTypes();

    public SupplementType editSupplementType(SupplementType updatedSupplementType);

    public void deleteSupplementType(int supplementTypeId);
}
