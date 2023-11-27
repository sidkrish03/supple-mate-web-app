package com.supplemateservice.data;

import com.supplemateservice.model.Customers;
import com.supplemateservice.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CustomerDaoImpl implements CustomerDao {
    @Autowired
    private JdbcTemplate jdbc;

    public static final class UserAccountMapper implements RowMapper<Customers> {

        @Override
        public Customers mapRow(ResultSet rs, int i) throws SQLException {
            Customers customer = new Customers();
            customer.setCustomerId(rs.getInt("useraccountid"));
            customer.setUsername(rs.getString("username"));
            customer.setPassword(rs.getString("userpassword"));
            customer.setFirstName(rs.getString("firstname"));
            customer.setLastName(rs.getString("lastname"));
            customer.setEmail(rs.getString("email"));
            customer.setCreationTime(Timestamp.valueOf(rs.getString("creationtimestamp")).toLocalDateTime());
            customer.setTimeZone(rs.getString("timezone"));
            return customer;
        }
    }

    @Override
    @Transactional
    public Customers addCustomerAccount(Customers customer) {
        final String INSERT_USER = "INSERT INTO UserAccount"
                + "(username, userpassword, firstname, lastname, email, creationtimestamp, timezone)"
                + " VALUES(?,?,?,?,?,?,?)";
        LocalDateTime creationTime = LocalDateTime.now().withNano(0);
        Timestamp creationTimestamp = Timestamp.valueOf(creationTime);
        jdbc.update(INSERT_USER, customer.getUsername(), customer.getPassword(), customer.getFirstName(),
                customer.getLastName(), customer.getEmail(), creationTimestamp, customer.getTimeZone());
        // set user's creation time and ID
        int customerId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        customer.setCustomerId(customerId);
        creationTimestamp = (jdbc.queryForObject("SELECT creationtimestamp FROM UserAccount WHERE useraccountid = ?", Timestamp.class, customerId));
        customer.setCreationTime(creationTimestamp.toLocalDateTime());


        // add ROLE_USER for user (hard-coded for a user to be added as a Customer and not an ADMIN)
        final String INSERT_USER_ROLE = "INSERT INTO User_Role(useraccountid, roleid) VALUES(?,?)";
        jdbc.update(INSERT_USER_ROLE, customer.getCustomerId(), 2);
        Role userRole = new Role();
        userRole.setRoleId(2);
        userRole.setRoleName("ROLE_USER");
        Set<Role> roleList = Collections.singleton(userRole);
        customer.setRoles(roleList);

        return customer;
    }

    @Override
    public Customers getCustomerAccountById(int customerId) {
        try{
            final String SELECT_USER_BY_ID = "SELECT * FROM UserAccount WHERE useraccountid = ?";
            Customers customer = jdbc.queryForObject(SELECT_USER_BY_ID, new UserAccountMapper(), customerId);
            customer.setRoles(getRolesForUser(customer.getCustomerId()));
            return customer;
        }
        catch (DataAccessException e){
            return null;
        }
    }

    @Override
    public List<Customers> getAllCustomerAccounts() {
        final String SELECT_ALL_USER_ACCOUNTS = "SELECT * FROM UserAccount";
        return jdbc.query(SELECT_ALL_USER_ACCOUNTS, new UserAccountMapper());
    }

    @Override
    public Customers editCustomerAccount(Customers updatedCustomer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteCustomerAccount(int customerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Customers getCustomerByUsername(String username) {
        try{
            final String SELECT_USER_BY_USERNAME = "SELECT * FROM UserAccount WHERE username = ?";
            Customers customer = jdbc.queryForObject(SELECT_USER_BY_USERNAME, new UserAccountMapper(), username);
            customer.setRoles(getRolesForUser(customer.getCustomerId()));
            return customer;
        }
        catch(DataAccessException e){
            return null;
        }
    }

    private Set<Role> getRolesForUser(int userId) throws DataAccessException {
        final String SELECT_ROLES_FOR_USER = "SELECT * FROM User_Role "
                + "JOIN role ON User_Role.roleid = role.roleid "
                + "WHERE User_Role.useraccountid = ?";
        Set<Role> roles = new HashSet(jdbc.query(SELECT_ROLES_FOR_USER, new RoleDaoImpl.RoleMapper(), userId));
        return roles;
    }
}
