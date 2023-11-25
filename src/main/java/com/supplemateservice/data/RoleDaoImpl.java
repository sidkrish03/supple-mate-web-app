package com.supplemateservice.data;

import com.supplemateservice.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RoleDaoImpl implements RoleDao {
    @Autowired
    JdbcTemplate jdbc;

    @Override
    public Role getRoleById(int roleId) {
        try {
            final String SELECT_ROLE_BY_ID = "SELECT * FROM role WHERE roleid = ?";
            return jdbc.queryForObject(SELECT_ROLE_BY_ID, new RoleMapper(), roleId);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public Role getRoleByRole(String role) {
        try {
            final String SELECT_ROLE_BY_ROLE = "SELECT * FROM role WHERE rolename = ?";
            return jdbc.queryForObject(SELECT_ROLE_BY_ROLE, new RoleMapper(), role);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Role> getAllRoles() {
        final String SELECT_ALL_ROLES = "SELECT * FROM role";
        return jdbc.query(SELECT_ALL_ROLES, new RoleMapper());
    }

    @Override
    public void deleteRole(int id) {
        final String DELETE_USER_ROLE = "DELETE FROM User_Role WHERE roleid = ?";
        final String DELETE_ROLE = "DELETE FROM role WHERE roleid = ?";
        jdbc.update(DELETE_USER_ROLE, id);
        jdbc.update(DELETE_ROLE, id);
    }

    @Override
    public void updateRole(Role role) {
        final String UPDATE_ROLE = "UPDATE role SET rolename = ? WHERE roleid = ?";
        jdbc.update(UPDATE_ROLE, role.getRoleName(), role.getRoleId());
    }

    @Override
    @Transactional
    public Role createRole(Role role) {
        final String INSERT_ROLE = "INSERT INTO role(rolename) VALUES(?)";
        jdbc.update(INSERT_ROLE, role.getRoleName());
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        role.setRoleId(newId);
        return role;
    }

    public static final class RoleMapper implements RowMapper<Role> {

        @Override
        public Role mapRow(ResultSet rs, int i) throws SQLException {
            Role role = new Role();
            role.setRoleId(rs.getInt("roleid"));
            role.setRoleName(rs.getString("rolename"));
            return role;
        }
    }
}
