package com.supplemateservice.data;

import com.supplemateservice.model.SupplementEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

@Repository
public class SupplementEntryDaoImpl implements SupplementEntryDao{
    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    DayLogDao logDao;

    @Autowired
    SupplementTypeDao supplementTypeDao;

    @Override
    public SupplementEntry addSupplementEntry(SupplementEntry supplementEntry) {
        final String INSERT_METRIC_ENTRY = "INSERT INTO MetricEntry"
                + "(daylogid, metrictypeid, metricvalue, entrytime) VALUES"
                + "(?, ?, ?, ?)";
        jdbc.update(INSERT_METRIC_ENTRY, supplementEntry.getDayLog().getDayLogId(),
                supplementEntry.getSupplementType().getSupplementTypeId(), supplementEntry.getSupplementDosageValue(),
                supplementEntry.getEntryTime());
        // set ID
        supplementEntry.setSupplementEntryId(jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));
        return supplementEntry;
    }

    @Override
    public SupplementEntry getSupplementEntryById(int supplementEntryId) {
        final String SELECT_ENTRY_BY_ID = "SELECT * FROM MetricEntry WHERE metricentryid = ?";
        SupplementEntry entry = new SupplementEntry();
        try{
            entry = jdbc.queryForObject(SELECT_ENTRY_BY_ID, new SupplementEntryMapper(), supplementEntryId);
        }
        catch(EmptyResultDataAccessException e){
            entry = null;
        }
        return entry;
    }

    @Override
    public List<SupplementEntry> getAllSupplementEntriesSorted(){
        final String SELECT_ALL_METRIC_ENTRIES = "SELECT * FROM MetricEntry INNER JOIN DayLog ON MetricEntry.daylogid = DayLog.DayLogId ORDER BY logdate, metricentryid";
        return jdbc.query(SELECT_ALL_METRIC_ENTRIES, new SupplementEntryMapper());
    }

    @Override
    public SupplementEntry editSupplementEntry(SupplementEntry updatedSupplementEntry) {
        final String UPDATE_ENTRY = "UPDATE MetricEntry SET daylogid = ?, metrictypeid = ?, metricvalue = ?, entrytime = ? WHERE metricentryid = ?";
        jdbc.update(UPDATE_ENTRY, updatedSupplementEntry.getDayLog().getDayLogId(), updatedSupplementEntry.getSupplementType().getSupplementTypeId(),
                updatedSupplementEntry.getSupplementDosageValue(), updatedSupplementEntry.getEntryTime(), updatedSupplementEntry.getSupplementEntryId());
        return getSupplementEntryById(updatedSupplementEntry.getSupplementEntryId());
    }

    @Override
    public void deleteSupplementEntry(int supplementEntryId) {
        final String DELETE_ENTRY = "DELETE FROM MetricEntry WHERE metricentryid = ?";
        jdbc.update(DELETE_ENTRY, supplementEntryId);
    }

    public final class SupplementEntryMapper implements RowMapper<SupplementEntry> {

        @Override
        public SupplementEntry mapRow(ResultSet rs, int i) throws SQLException {
            SupplementEntry supplementEntry = new SupplementEntry();
            supplementEntry.setSupplementEntryId(rs.getInt("metricentryid"));
            supplementEntry.setDayLog(logDao.getDayLogById(rs.getInt("daylogid")));
            supplementEntry.setSupplementType(supplementTypeDao.getSupplementTypeById(rs.getInt("metrictypeid")));
            supplementEntry.setSupplementDosageValue(rs.getFloat("metricvalue"));
            supplementEntry.setEntryTime(Time.valueOf(rs.getString("entrytime")));
            return supplementEntry;
        }
    }

}
