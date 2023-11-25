package com.supplemateservice.data;

import com.supplemateservice.model.Customers;
import com.supplemateservice.model.SupplementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SupplementTypeDaoImpl implements SupplementTypeDao{
    @Autowired
    private JdbcTemplate jdbc;

    // must be static for use in MetricTypeMapper
    @Autowired
    CustomerDao customerDao;


    public final class SupplementTypeMapper implements RowMapper<SupplementType> {

        @Override
        public SupplementType mapRow(ResultSet rs, int i) throws SQLException {
            SupplementType supplementType = new SupplementType();
            supplementType.setSupplementTypeId(rs.getInt("metrictypeid"));
            supplementType.setUser(customerDao.getCustomerAccountById(rs.getInt("useraccountid")));
            supplementType.setSupplementName(rs.getString("metricname"));
            supplementType.setScale(rs.getInt("scale"));
            supplementType.setUnit(rs.getString("unit"));
            return supplementType;
        }

    }

    @Override
    public SupplementType addSupplementType(SupplementType supplementType) {
        final String INSERT_METRIC_TYPE = "INSERT INTO MetricType(useraccountid, metricname, scale, unit)"
                + " VALUES(?,?,?,?)";
        jdbc.update(INSERT_METRIC_TYPE, supplementType.getCustomer().getCustomerId(), supplementType.getSupplementName(),
                supplementType.getScale(), supplementType.getUnit());

        // set MetricType's ID
        supplementType.setSupplementTypeId(jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));
        return supplementType;
    }

    @Override
    public SupplementType getSupplementTypeById(int supplementTypeId) {
        final String SELECT_METRICTYPE_BY_ID = "SELECT * FROM MetricType WHERE metrictypeid = ?";
        SupplementType type = new SupplementType();
        try{
            type = jdbc.queryForObject(SELECT_METRICTYPE_BY_ID, new SupplementTypeMapper(), supplementTypeId);
        }
        catch(EmptyResultDataAccessException e){
            type = null;
        }
        return type;
    }

    @Override
    public List<SupplementType> getAllSupplementTypes() {
        final String SELECT_ALL_METRIC_TYPES = "SELECT * FROM MetricType";
        return jdbc.query(SELECT_ALL_METRIC_TYPES, new SupplementTypeMapper());
    }

    @Override
    public SupplementType editSupplementType(SupplementType updatedSupplementType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteSupplementType(int typeId) {
        final String DELETE_TYPE = "DELETE FROM MetricType WHERE metrictypeid = ?";
        jdbc.update(DELETE_TYPE, typeId);
    }
}
