package com.supplemateservice.data;

import com.supplemateservice.model.SupplementEntry;

import java.util.List;

public interface SupplementEntryDao {
    public SupplementEntry addSupplementEntry(SupplementEntry supplementEntry);

    public SupplementEntry getSupplementEntryById(int supplementEntryId);

    public List<SupplementEntry> getAllSupplementEntriesSorted();

    public SupplementEntry editSupplementEntry(SupplementEntry updatedSupplementEntry);

    public void deleteSupplementEntry(int supplementEntryId);
}
